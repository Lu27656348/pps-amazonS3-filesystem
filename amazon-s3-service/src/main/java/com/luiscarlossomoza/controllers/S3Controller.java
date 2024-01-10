package com.luiscarlossomoza.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.luiscarlossomoza.interfaces.FileNameProjection;
import com.luiscarlossomoza.interfaces.RequestResponse;
import com.luiscarlossomoza.interfaces.ValidateFileNameRequest;
import com.luiscarlossomoza.services.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController

public class S3Controller {
    private final String FOLDER_NAME = "proposal/";
    private final String FOLDER_GW_NAME = "graduatework/";
    @Autowired
    private IS3Service s3Service;

    @Autowired
    private AmazonS3 s3Client;

    @PostMapping("/upload")
    public ResponseEntity<RequestResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @PostMapping("/upload/graduatework")
    public ResponseEntity<RequestResponse> uploadGraduateWork(@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadGraduateWork(file);
    }

    @GetMapping("/graduatework/files")
    public List<String> getGraduateWorkFiles() throws IOException {
        return s3Service.getGraduateWorkFiles();
    }

    @PostMapping("/upload/graduatework/revision")
    public ResponseEntity<RequestResponse> uploadRevision (@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadRevision(file);
    }

    @PostMapping("/upload/graduatework/final")
    public ResponseEntity<RequestResponse> uploadFinalSubmittion (@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadFinalSubmittion(file);
    }

    @GetMapping("/graduatework/reviews/files")
    public List<String> getGraduateWorkReviewsFiles() throws IOException {
        return s3Service.getGraduateWorkReviewsFiles();
    }


    @GetMapping("/graduatework/proposals/files")
    public List<String> getGraduateWorkProposalsFiles() throws IOException {
        return s3Service.getGraduateWorkProposalsFiles();
    }

    @GetMapping("/graduatework/final/files")
    public List<String> getGraduateWorkFinalFiles() throws IOException {
        return s3Service.getGraduateWorkFinalFiles();
    }

    @PostMapping("/download")

    public ResponseEntity<InputStreamResource> download(@RequestBody ValidateFileNameRequest fileName) throws IOException {
        try {
            // Obtener el objeto del archivo de S3
            S3Object object = s3Client.getObject("bucket-gw-storage", FOLDER_NAME+ fileName.getFileName());
            S3ObjectInputStream s3is = object.getObjectContent();
            // Configurar las cabeceras de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName.getFileName())
                    .build();
            headers.setContentDisposition(contentDisposition);
            headers.setContentLength(object.getObjectMetadata().getContentLength());


            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(s3is));

        } catch (Exception e) {
            // Manejar la excepción
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/download/graduatework")
    public ResponseEntity<InputStreamResource> downloadGraduateWork(@RequestBody ValidateFileNameRequest fileName) throws IOException {
        try {
            // Obtener el objeto del archivo de S3
            S3Object object = s3Client.getObject("bucket-gw-storage", FOLDER_GW_NAME + fileName.getFileName());
            S3ObjectInputStream s3is = object.getObjectContent();
            // Configurar las cabeceras de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName.getFileName())
                    .build();
            headers.setContentDisposition(contentDisposition);
            headers.setContentLength(object.getObjectMetadata().getContentLength());


            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(s3is));

        } catch (Exception e) {
            // Manejar la excepción
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    public List<String > getAllObjects() throws IOException {
        return s3Service.listFiles();
    }

    @GetMapping("/validate")
    public Boolean validateFileName(@RequestBody ValidateFileNameRequest fileName) {
        return s3Service.validateFileName(fileName);
    }

    @DeleteMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable("fileName") String name){
        return s3Service.deleteFile(name);
    }
}
