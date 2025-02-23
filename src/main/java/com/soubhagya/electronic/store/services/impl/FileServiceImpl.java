package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.exceptions.BadApiRequestException;
import com.soubhagya.electronic.store.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementation of the FileService interface providing file operations such as uploading
 * and retrieving files. This service handles file uploads by saving them to a specified directory
 * on the filesystem and enforces file type restrictions, only allowing PNG, JPG, and JPEG files.
 * Additionally, it retrieves files from the filesystem as InputStream resources.
 */
@Service
public class FileServiceImpl implements FileService {
    private final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    /**
     * Uploads a file to a specified directory. The file is saved with a randomly generated name,
     * retaining its original extension. Only files with extensions .png, .jpg, and .jpeg are allowed.
     * Directories will be created if they do not exist.
     *
     * @param file the MultipartFile object containing the file to be uploaded
     * @param path the directory path where the file should be uploaded
     * @return the new filename with its extension after being successfully saved
     * @throws IOException if an I/O error occurs during file upload
     * @throws BadApiRequestException if the file extension is not allowed
     */
    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        String originalFilename = file.getOriginalFilename();
        logger.info("Filename : {}", originalFilename);
        String fileName = UUID.randomUUID().toString();
        //abc.png
        assert originalFilename != null;
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileNameWithExtension = fileName + extension;
        String fullPathWithFileName = path + fileNameWithExtension;

        logger.info("full image path: {} ", fullPathWithFileName);
        if(extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")) {
            //file save
            File folder = new File(path);
            if(!folder.exists()){
                folder.mkdirs();
            }
            //upload
            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));
            return fileNameWithExtension;
        } else {
            throw new BadApiRequestException("File with " + extension + " not allowed !!");
        }
    }

    /**
     * Retrieves a file from the file system as an InputStream based on the specified path and file name.
     *
     * @param path the directory path where the file is located
     * @param name the name of the file to be retrieved
     * @return an InputStream for the specified file
     * @throws FileNotFoundException if the file does not exist at the specified path and name
     */
    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullPath = path + File.separator + name;
        return new FileInputStream(fullPath);
    }
}
