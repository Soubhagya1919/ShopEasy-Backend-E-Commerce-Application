package com.soubhagya.electronic.store.services;

import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.dtos.UserDto;

import java.util.List;

/**
 * Interface for managing user-related operations within the system.
 * This service interface provides a set of methods for creating,
 * updating, deleting, retrieving, and searching users.
 */
public interface UserService {

    //create
    UserDto createUser(UserDto userDto);

    //update
    UserDto updateUser(UserDto userDto, String userId);

    //delete
    void deleteUser(String userId);

    //get all users
    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get single user by id
    UserDto getUserById(String userId);

    //get user by email
    UserDto getUserByEmail(String email);

    //search user
    List<UserDto> searchUser(String keyword);

    //other user specific features

}
