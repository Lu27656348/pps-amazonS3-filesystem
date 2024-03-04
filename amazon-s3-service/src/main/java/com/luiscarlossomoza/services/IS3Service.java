package com.luiscarlossomoza.services;

import com.luiscarlossomoza.interfaces.FileNameProjection;
import com.luiscarlossomoza.interfaces.RequestResponse;
import com.luiscarlossomoza.interfaces.UserDataRequest;
import com.luiscarlossomoza.interfaces.ValidateFileNameRequest;
import com.luiscarlossomoza.interfaces.requests.CreateUserFolder;
import com.luiscarlossomoza.interfaces.responses.VerifyFolderExistenseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IS3Service {
    ResponseEntity<RequestResponse> uploadFile(MultipartFile file,CreateUserFolder[] userData, String escuela) throws IOException;
    ResponseEntity<RequestResponse> uploadFileDouble(MultipartFile file, CreateUserFolder[] userData, String escuela) throws IOException;
    ResponseEntity<RequestResponse> uploadGraduateWork(MultipartFile file, List<UserDataRequest> studentDataList) throws IOException;

    ResponseEntity<RequestResponse> uploadCoordinatorEvaluation(MultipartFile file, List<UserDataRequest> studentData, UserDataRequest coordinatorData) throws IOException;

    ResponseEntity<RequestResponse> uploadRevision(MultipartFile file, List<UserDataRequest> studentDataList) throws IOException;

    ResponseEntity<RequestResponse> uploadFinalSubmittion(MultipartFile file,List<UserDataRequest> studentDataList,String escuela) throws IOException;
    List<String> listFiles(String schoolname) throws IOException;
    String deleteFile(String fileName);

    List<String> getGraduateWorkReviewsFiles() throws IOException;

    List<String> getGraduateWorkProposalsFiles() throws IOException;

    List<String> getGraduateWorkFinalFiles() throws IOException;

    Boolean validateFileName(ValidateFileNameRequest fileName);
    List<String> getGraduateWorkFiles() throws IOException;

    ResponseEntity<RequestResponse> createUserFolder (String studentDNI, String userFirstName, String userLastName) throws IOException;
    ResponseEntity<VerifyFolderExistenseResponse> verifyFolderExistence(String studentDNI, String userFirstName, String userLastName) throws IOException;

    ResponseEntity<VerifyFolderExistenseResponse> verifyFolderPath(String folderPath) throws IOException;

    ResponseEntity<RequestResponse> uploadIntershipProposal(MultipartFile file, CreateUserFolder userData, String escuela) throws IOException;

    ResponseEntity<RequestResponse> cargarConsejoDeEscuela(MultipartFile file,String consejoDeEscuela,String schoolName) throws IOException;

    ResponseEntity<RequestResponse> cargarComiteTG(MultipartFile file,String comiteTG, String schoolName) throws IOException;
    public ResponseEntity<RequestResponse> cargarCartasJurado (MultipartFile[] file,List<UserDataRequest> studentDataList) throws IOException;

}