package com.gaurav.springbootfileuploaddownloadrestapis.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping
public class FileDownloadController {

    @Value("${fileDownloadPath}")
    private String fileDownloadPath;

    @Value("${filePath}")
    private String filePath;

    @GetMapping(value = "/download/{fileName}")
    public void downloadTheFile(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {

        Resource resource = null;
        Path pathToFile = null;

        Path pathToDownload = null ;

        try {
            pathToFile = Paths.get(filePath).toAbsolutePath().normalize();
            pathToFile = pathToFile.resolve(fileName).normalize();

            try {
                Files.createDirectory(pathToDownload);
            } catch (Exception e) {

            }

            resource = new UrlResource(pathToFile.toUri());
        } catch (Exception e) {
            throw new RuntimeException("Error in processing request " + e.getMessage());
        }
        if (!resource.exists()) {
            throw new FileSystemNotFoundException("This file does not exist ====> " + resource.getFilename());
        }

        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error in processing request " + e.getMessage());
        }


        if (contentType == null)
            contentType = "application/octet-stream";
//
        InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(pathToFile.toString())));
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline ; fileName:\"" +  resource.getFilename() + "\"");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline ; attachments:\"" +  resource.getFilename() + "\"");
        response.setContentLength((int) resource.getFile().length());
        response.setContentType(contentType);

        FileCopyUtils.copy(inputStream, response.getOutputStream());

    }

}
