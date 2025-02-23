package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.constants.AppConstants;
import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.dtos.UserDto;
import com.soubhagya.electronic.store.entities.Role;
import com.soubhagya.electronic.store.entities.User;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.helper.Helper;
import com.soubhagya.electronic.store.repositories.RoleRepository;
import com.soubhagya.electronic.store.repositories.UserRepository;
import com.soubhagya.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface, providing methods for user management operations.
 * This service handles the creation, updating, deletion, and retrieval of user accounts.
 */
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ModelMapper mapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${user.profile.image.path}")
    private String imagePath;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    /**
     * Creates a new user with a unique user ID, encodes the password,
     * assigns the normal user role, saves the user entity, and returns
     * the created user as a Data Transfer Object (DTO).
     *
     * @param userDto the user data transfer object containing user details
     * @return the Data Transfer Object representation of the saved user including its unique ID and roles
     */
    @Override
    public UserDto createUser(UserDto userDto) {

        //generate unique id in string format
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);

        //dto->entity
        User user = dtoToEntity(userDto);

        //encoding the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //get the normal role since it is a user
        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setName("ROLE_" + AppConstants.ROLE_NORMAL);
        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(role);
        user.setRoles(Set.of(roleNormal));

        //saving the user
        User savedUser = userRepository.save(user);
        //entity->dto
        return entityToDto(savedUser);
    }

    /**
     * Updates an existing user in the repository with the provided user details.
     *
     * @param userDto the data transfer object containing updated user information
     * @param userId the unique identifier of the user to be updated
     * @return the updated user information as a data transfer object
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     */
    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(ResourceNotFoundException::new);

        user.setName(userDto.getName());
        //email update
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setImageName(userDto.getImageName());

        //save data
        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    /**
     * Deletes a user from the system based on the provided user ID.
     * This method removes the user's profile image from the file system
     * before deleting the user record from the repository.
     *
     * @param userId the unique identifier of the user to be deleted
     * @throws ResourceNotFoundException if the user is not found with the given ID
     * @throws RuntimeException if an error occurs while deleting the user's profile image
     */
    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(ResourceNotFoundException::new);

        //delete user profile image
        //images/user/abc.png
        String fullPath = imagePath + user.getImageName();

        Path path = Paths.get(fullPath);

//        try {
//            Files.delete(path);
//            logger.info("User image deleted: {}", fullPath);
//        } catch (NoSuchFileException ex) {
//            logger.warn("User image not found: {}", fullPath);
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        try {
            // Delete the user's profile image
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("User image deleted: {}", fullPath);
            } else {
                logger.warn("User image not found: {}", fullPath);
            }
        } catch (IOException ex) {
            logger.error("Error occurred while deleting user image: {}", fullPath, ex);
            throw new RuntimeException("Failed to delete user image", ex);
        }

        userRepository.delete(user);
    }

    /**
     * Retrieves a paginated list of user data transfer objects (DTOs) with sorting options.
     *
     * @param pageNumber the page number to retrieve, starting from 0
     * @param pageSize the number of users per page
     * @param sortBy the attribute by which the list should be sorted
     * @param sortDir the direction for sorting; can be either "asc" or "desc"
     * @return a pageable response containing user DTOs
     */
    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> page = userRepository.findAll(pageable);

        return Helper.getPageableResponse(page, UserDto.class);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return a UserDto object containing the user's details
     * @throws ResourceNotFoundException if no user is found with the given id
     */
    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found with the given id"));
        return entityToDto(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return a UserDto object containing the user's information
     * @throws ResourceNotFoundException if no user is found with the given email
     */
    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email id"));
        return entityToDto(user);
    }

    /**
     * Searches and retrieves a list of users whose names contain the specified keyword.
     *
     * @param keyword the keyword to search for in user names
     * @return a list of UserDto objects that match the search criteria
     */
    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        return users.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a User entity to a UserDto.
     *
     * @param savedUser the User entity to be converted
     * @return the corresponding UserDto representation of the User entity
     */
    private UserDto entityToDto(User savedUser) {

//        return UserDto.builder()
//                .userId(savedUser.getUserId())
//                .name(savedUser.getName())
//                .password(savedUser.getPassword())
//                .email(savedUser.getEmail())
//                .about(savedUser.getAbout())
//                .gender(savedUser.getGender())
//                .imageName(savedUser.getImageName())
//                .build();
        return mapper.map(savedUser, UserDto.class);
    }

    /**
     * Converts a UserDto object to a User entity.
     *
     * @param userDto the UserDto object that contains user data to be converted
     * @return a User entity populated with data from the given UserDto
     */
    private User dtoToEntity(UserDto userDto) {

//        return User.builder()
//                .userId(userDto.getUserId())
//                .name(userDto.getName())
//                .email(userDto.getEmail())
//                .password(userDto.getPassword())
//                .about(userDto.getAbout())
//                .gender(userDto.getGender())
//                .imageName(userDto.getImageName()).build();
        return mapper.map(userDto, User.class);
    }

}
