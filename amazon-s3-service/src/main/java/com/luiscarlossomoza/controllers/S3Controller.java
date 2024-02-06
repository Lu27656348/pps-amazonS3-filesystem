package com.luiscarlossomoza.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luiscarlossomoza.interfaces.RequestResponse;
import com.luiscarlossomoza.interfaces.UserDataRequest;
import com.luiscarlossomoza.interfaces.ValidateFileNameRequest;
import com.luiscarlossomoza.interfaces.VerifyFolderPathRequest;
import com.luiscarlossomoza.interfaces.requests.CreateUserFolder;
import com.luiscarlossomoza.interfaces.requests.UploadFileRequest;
import com.luiscarlossomoza.interfaces.responses.VerifyFolderExistenseResponse;
import com.luiscarlossomoza.services.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController

public class S3Controller {
    @Autowired
    private IS3Service s3Service;

    @Autowired
    private AmazonS3 s3Client;
    String GRADUATE_WORK_FOLDER = "trabajos_de_grado/";


    @GetMapping({"/","/home"})
    public ResponseEntity<RequestResponse> welcomeMessage(){
        return ResponseEntity.ok(new RequestResponse("Welcome to Luis Somoza Amazon S3 Graduate Work API"));
    }

    @PostMapping("/upload")
    public ResponseEntity<RequestResponse> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("userData") String userData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CreateUserFolder studentData = mapper.readValue(userData, CreateUserFolder.class);
        System.out.println(studentData);
        return s3Service.uploadFile(file,studentData);
    }

    @PostMapping("upload/pasantia/propuesta")
    public ResponseEntity<RequestResponse> uploadIntershipProposal(@RequestParam("file") MultipartFile file,@RequestParam("studentData") String userData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CreateUserFolder studentData = mapper.readValue(userData, CreateUserFolder.class);
        System.out.println(studentData);
        return s3Service.uploadIntershipProposal(file,studentData);
    }

    @PostMapping("/upload/double")
    public ResponseEntity<RequestResponse> uploadFileDouble(@RequestParam("file") MultipartFile file, @RequestParam("userData") String uploadFileRequest) throws IOException {

      CreateUserFolder[] myObjectsArray = new CreateUserFolder[2];
      ObjectMapper mapper = new ObjectMapper();
      CreateUserFolder[] uploadFileRequestArray = mapper.readValue(uploadFileRequest,CreateUserFolder[].class);
      System.out.println(uploadFileRequestArray[0]);
      System.out.println(uploadFileRequestArray[1]);
      return s3Service.uploadFileDouble(file,uploadFileRequestArray);
    }

    @PostMapping("/upload/graduatework/coordinator/evaluation")
    public ResponseEntity<RequestResponse> uploadCoordinatorEvaluation(@RequestParam("file") MultipartFile file, @RequestParam("studentData") String studentData, @RequestParam("coordinatorData") String coordinatorData) throws IOException {
        System.out.println(studentData);
        System.out.println(coordinatorData);
        List<UserDataRequest> myObjectsList;
        ObjectMapper mapper = new ObjectMapper();
        myObjectsList = List.of(mapper.readValue(studentData, UserDataRequest[].class));
        for (UserDataRequest student : myObjectsList){
            System.out.println(student);
        }
        UserDataRequest coordinatorObject = mapper.readValue(coordinatorData, UserDataRequest.class);
        System.out.println(coordinatorObject);
        return s3Service.uploadCoordinatorEvaluation(file,myObjectsList,coordinatorObject);
    }

    @PostMapping("/upload/graduatework")
    public ResponseEntity<RequestResponse> uploadGraduateWork(@RequestParam("file") MultipartFile file,@RequestParam("studentData") String studentData) throws IOException {
        System.out.println(studentData);
        ObjectMapper mapper = new ObjectMapper();
        List<UserDataRequest> studentDataList = List.of(mapper.readValue(studentData, UserDataRequest[].class));
        for (UserDataRequest student : studentDataList){
            System.out.println(student);
        }
        return s3Service.uploadGraduateWork(file,studentDataList);
        //return ResponseEntity.ok(new RequestResponse("Hola"));
        //return s3Service.uploadGraduateWork(file,studentData);
    }

    @GetMapping("/graduatework/files")
    public List<String> getGraduateWorkFiles() throws IOException {
        return s3Service.getGraduateWorkFiles();
    }

    @PostMapping("/upload/graduatework/revision")
    public ResponseEntity<RequestResponse> uploadRevision (@RequestParam("file") MultipartFile file, @RequestParam("studentData") String studentData) throws IOException {
        System.out.println(studentData);
        ObjectMapper mapper = new ObjectMapper();
        List<UserDataRequest> studentDataList = List.of(mapper.readValue(studentData, UserDataRequest[].class));
        for (UserDataRequest student : studentDataList){
            System.out.println(student);
        }
        return s3Service.uploadRevision(file,studentDataList);
    }

    @PostMapping("/download/graduatework/revision")
    public ResponseEntity<InputStreamResource> downloadRevision (@RequestBody ValidateFileNameRequest validateFileNameRequest) throws IOException {

        try {
            // Obtener el objeto del archivo de S3
            String FOLDER_GW_NAME = "trabajos_de_grado/";
            String studentFolderPath = FOLDER_GW_NAME + validateFileNameRequest.getStudentDNI() + "@" + validateFileNameRequest.getUserLastName().split(" ")[0] + validateFileNameRequest.getUserFirstName().split(" ")[0] + "/";
            System.out.println(studentFolderPath);
            System.out.println(validateFileNameRequest.getFileName());
            S3Object object = s3Client.getObject("bucket-gw-storage", studentFolderPath + "desarrollo/revisiones/" + validateFileNameRequest.getFileName());
            S3ObjectInputStream s3is = object.getObjectContent();
            // Configurar las cabeceras de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(validateFileNameRequest.getFileName())
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

    @PostMapping("/upload/graduatework/final")
    public ResponseEntity<RequestResponse> uploadFinalSubmittion (@RequestParam("file") MultipartFile file, @RequestParam("studentData") String userDataRequest) throws IOException {
        System.out.println(userDataRequest);
        ObjectMapper mapper = new ObjectMapper();
        List<UserDataRequest> studentDataList = List.of(mapper.readValue(userDataRequest, UserDataRequest[].class));
        for (UserDataRequest student : studentDataList){
            System.out.println(student);
        }
        return s3Service.uploadFinalSubmittion(file,studentDataList);
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
            String FOLDER_NAME = "trabajos_de_grado/"+fileName.getStudentDNI()+"@"+fileName.getUserLastName()+fileName.getUserFirstName()+"/"+"propuestas/";
            System.out.println(FOLDER_NAME);
            S3Object object = s3Client.getObject("bucket-gw-storage", FOLDER_NAME + fileName.getFileName());
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

    @PostMapping("/download/pasantia/propuesta")
    public ResponseEntity<InputStreamResource> downloadPropuestaPasantia(@RequestBody ValidateFileNameRequest fileName) throws IOException {
        try {
            // Obtener el objeto del archivo de S3
            String FOLDER_NAME = "pasantias/"+fileName.getStudentDNI()+"@"+fileName.getUserLastName().split(" ")[0]+fileName.getUserFirstName().split(" ")[0]+"/";
            System.out.println(FOLDER_NAME);
            S3Object object = s3Client.getObject("bucket-gw-storage", FOLDER_NAME + fileName.getFileName());
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
            String FOLDER_GW_NAME = "trabajos_de_grado/";
            String studentFolderPath = FOLDER_GW_NAME + fileName.getStudentDNI() + "@" + fileName.getUserLastName().split(" ")[0] + fileName.getUserFirstName().split(" ")[0] + "/";
            System.out.println(studentFolderPath);
            S3Object object = s3Client.getObject("bucket-gw-storage", studentFolderPath + "desarrollo/" + fileName.getFileName());
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

    @PostMapping("/download/root")
    public ResponseEntity<InputStreamResource> downloadFromRoot(@RequestBody ValidateFileNameRequest fileName) throws IOException {
        try {
            // Obtener el objeto del archivo de S3
            S3Object object = s3Client.getObject("bucket-gw-storage", fileName.getFileName());
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

    @PostMapping("/download/proposal/evaluation")
    public ResponseEntity<InputStreamResource> downloadProposalEvaluation(@RequestBody ValidateFileNameRequest fileName) throws IOException {
        try {
            // Obtener el objeto del archivo de S3
            System.out.println(fileName.getRevisionNumber());
            String  studentFolderPath = fileName.getStudentDNI() + "@" +fileName.getUserLastName().split(" ")[0] + fileName.getUserFirstName().split(" ")[0] + "/";
            System.out.println(GRADUATE_WORK_FOLDER + studentFolderPath + "propuestas/revisiones/" + fileName.getFileName());
            S3Object object = s3Client.getObject("bucket-gw-storage", GRADUATE_WORK_FOLDER + studentFolderPath + "propuestas/revisiones/" + fileName.getFileName());
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

    @PostMapping("create/folder")
    ResponseEntity<RequestResponse> createUserFolder (@RequestBody CreateUserFolder createUserFolder) throws IOException {
        return s3Service.createUserFolder(createUserFolder.getStudentDNI(),createUserFolder.getUserFirstName(),createUserFolder.getUserLastName());
    }

    @GetMapping("validate/folder")
    ResponseEntity<VerifyFolderExistenseResponse> verifyFolderExistence (@RequestBody CreateUserFolder createUserFolder) throws IOException {
        return s3Service.verifyFolderExistence(createUserFolder.getStudentDNI(),createUserFolder.getUserFirstName(),createUserFolder.getUserLastName());
    }

    @GetMapping("search/folder")
    ResponseEntity<VerifyFolderExistenseResponse> verifyFolderPath(@RequestBody VerifyFolderPathRequest verifyFolderPathRequest) throws IOException {
        return s3Service.verifyFolderPath(verifyFolderPathRequest.getFolderPath());
    }
}
