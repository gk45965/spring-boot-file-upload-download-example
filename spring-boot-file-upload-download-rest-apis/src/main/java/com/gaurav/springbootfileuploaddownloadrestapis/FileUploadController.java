package com.gaurav.springbootfileuploaddownloadrestapis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping
public class FileUploadController {

    @Value("${fileUploadPath}")
    private String fileUploadPath;

    @PostMapping(value = "/upload" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestParam MultipartFile fileName) throws IOException {

        Path uploadingPath = Paths.get(fileUploadPath).toAbsolutePath().normalize();

        try {
            Files.createDirectory(uploadingPath);
        } catch (Exception e) {

        }


        String fileOriginalName = StringUtils.cleanPath(fileName.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileOriginalName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

        }catch (Exception e)
        {

        }


        uploadingPath  = uploadingPath.resolve(fileOriginalName);

        Files.copy( fileName.getInputStream() , uploadingPath , StandardCopyOption.REPLACE_EXISTING);

    }


}
