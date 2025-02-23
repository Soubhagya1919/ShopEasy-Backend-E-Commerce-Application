package com.soubhagya.electronic.store.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A service interface for handling file operations such as uploading and retrieving files.
 * Provides methods to upload a file to a specified path and to retrieve a file as a resource.
 */
public interface FileService {

    String uploadFile(MultipartFile file, String path) throws IOException;

    InputStream getResource(String path, String name) throws FileNotFoundException;
}
