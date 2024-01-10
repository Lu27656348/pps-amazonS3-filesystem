package com.luiscarlossomoza.services;

import com.luiscarlossomoza.interfaces.FileNameProjection;
import com.luiscarlossomoza.interfaces.RequestResponse;
import com.luiscarlossomoza.interfaces.ValidateFileNameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IS3Service {
    ResponseEntity<RequestResponse> uploadFile(MultipartFile file) throws IOException;
    ResponseEntity<RequestResponse> uploadGraduateWork(MultipartFile file) throws IOException;

    ResponseEntity<RequestResponse> uploadRevision(MultipartFile file) throws IOException;

    ResponseEntity<RequestResponse> uploadFinalSubmittion(MultipartFile file) throws IOException;
    void downloadFile(String fileName);
    List<String> listFiles() throws IOException;
    String deleteFile(String fileName);

    List<String> getGraduateWorkReviewsFiles() throws IOException;

    List<String> getGraduateWorkProposalsFiles() throws IOException;

    List<String> getGraduateWorkFinalFiles() throws IOException;

    Boolean validateFileName(ValidateFileNameRequest fileName);
    List<String> getGraduateWorkFiles() throws IOException;

}
