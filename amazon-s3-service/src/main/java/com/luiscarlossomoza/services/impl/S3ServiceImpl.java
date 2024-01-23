package com.luiscarlossomoza.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.luiscarlossomoza.interfaces.RequestResponse;
import com.luiscarlossomoza.interfaces.UserDataRequest;
import com.luiscarlossomoza.interfaces.ValidateFileNameRequest;
import com.luiscarlossomoza.interfaces.requests.CreateUserFolder;
import com.luiscarlossomoza.interfaces.responses.VerifyFolderExistenseResponse;
import com.luiscarlossomoza.services.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class S3ServiceImpl implements IS3Service {
    private final AmazonS3 s3client;
    String GRADUATE_WORK_FOLDER = "trabajos_de_grado/";
    private final String BUCKET_NAME = "bucket-gw-storage";
    private final String FOLDER_NAME = "propuestas/";

    private final String FOLDER_R_NAME = "propuestas/revisiones/";
    private final String FOLDER_GW_NAME = "graduatework/";

    private final String FOLDER_GWF_NAME = "graduatework/final/";
    private final String FOLDER_GWFR_NAME = "graduatework/final/resume/";

    private final String FOLDER_GWR_NAME = "graduatework/reviews/";
    private final String PDF_EXTENSION = ".pdf";



    @Autowired
    public S3ServiceImpl(AmazonS3 s3client){
        this.s3client = s3client;
    }

    public ResponseEntity<RequestResponse> createUserFolder (String studentDNI, String userFirstName, String userLastName) throws IOException {
        VerifyFolderExistenseResponse folderExists = verifyFolderExistence(studentDNI,userFirstName,userLastName).getBody();
        if(folderExists.getExists()){
            return ResponseEntity.ok(new RequestResponse("La carpeta ya existe"));
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

        String userFolderFormat = studentDNI + "@" + userLastName + userFirstName + "/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userFolderFormat,emptyContent,metadata));

        String userProposalFolder = userFolderFormat + "propuestas/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userProposalFolder,emptyContent,metadata));
        String userProposalRevisionFolder = userFolderFormat + "propuestas/revisiones/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userProposalRevisionFolder,emptyContent,metadata));

        String userGraduateWorkFolder = userFolderFormat + "desarrollo/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userGraduateWorkFolder,emptyContent,metadata));
        String userGraduateWorkRevisionFolder = userFolderFormat + "desarrollo/revisiones/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userGraduateWorkRevisionFolder,emptyContent,metadata));

        String userDefenseFolder = userFolderFormat + "defensa/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userDefenseFolder,emptyContent,metadata));
        String userDefenseRevisionFolder = userFolderFormat + "defensa/planillas/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userDefenseRevisionFolder,emptyContent,metadata));

        String userFinalFolder = userFolderFormat + "archivos_finales/";
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, GRADUATE_WORK_FOLDER + userFolderFormat,emptyContent,metadata));

        return ResponseEntity.ok(new RequestResponse("La carpeta fue creada exitosamente"));

    }

    public ResponseEntity<VerifyFolderExistenseResponse> verifyFolderExistence(String studentDNI, String userFirstName, String userLastName) throws IOException {
        try {
            String folderNameFormat = studentDNI+"@"+userLastName+userFirstName;
            S3Object folderSearch = s3client.getObject(BUCKET_NAME,GRADUATE_WORK_FOLDER+folderNameFormat+"/");
            System.out.println(folderSearch);
            return ResponseEntity.ok(new VerifyFolderExistenseResponse("La carpeta ya existe",true));
        } catch (AmazonS3Exception amazonS3Exception){
            System.out.println(amazonS3Exception.getMessage());
            return ResponseEntity.badRequest().body(new VerifyFolderExistenseResponse("La carpeta solitada no existe",false));
        }

    }

    public ResponseEntity<VerifyFolderExistenseResponse> verifyFolderPath(String folderPath) throws IOException {
        try {
            S3Object folderSearch = s3client.getObject(BUCKET_NAME,folderPath);
            System.out.println(folderSearch);
            return ResponseEntity.ok(new VerifyFolderExistenseResponse("La carpeta ya existe",true));
        } catch (AmazonS3Exception amazonS3Exception){
            System.out.println(amazonS3Exception.getMessage());
            return ResponseEntity.badRequest().body(new VerifyFolderExistenseResponse("La carpeta solitada no existe",false));
        }

    }
    public Boolean validateFileName( ValidateFileNameRequest fileNameRequest ){
        System.out.println(fileNameRequest.getFileName());
        String regexValidator = "^[A-Z]{1}[a-z]+[A-Z]{1}[a-z]+(\s?[A-Z]{1}[a-z]+[A-Z]{1}[a-z]+)?\s(PTG|TG|Pasantía|SC|Propuesta\sPasantía|Propuesta\sSC)$";
        Pattern pattern = Pattern.compile(regexValidator);
        Matcher matcher = pattern.matcher(fileNameRequest.getFileName());

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    public Boolean validateRevisionFileName( ValidateFileNameRequest fileNameRequest ){
        System.out.println(fileNameRequest.getFileName());
        String regexValidator = "^[A-Z]{1}[a-z]+[A-Z]{1}[a-z]+(\\s?[A-Z]{1}[a-z]+[A-Z]{1}[a-z]+)?\\s(PTG|TG|Pasantía|SC|Propuesta\\sPasantía|Propuesta\\sSC)\\s(Rev|Rev[2-9]{1}[A-Z]{2})$";
        Pattern pattern = Pattern.compile(regexValidator);
        Matcher matcher = pattern.matcher(fileNameRequest.getFileName());

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    public ResponseEntity<RequestResponse> uploadFile(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            if(validateFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",FOLDER_NAME + file.getOriginalFilename(),fileTemp));
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                System.out.println("Nombre invalido");
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public ResponseEntity<RequestResponse> uploadFileDouble(MultipartFile file, CreateUserFolder[] userData) throws IOException {
        try {
            VerifyFolderExistenseResponse hasFirstStudentFolder = verifyFolderExistence(userData[0].getStudentDNI(),userData[0].getUserFirstName(),userData[0].getUserLastName()).getBody();
            VerifyFolderExistenseResponse hasSecondtStudentFolder = verifyFolderExistence(userData[1].getStudentDNI(),userData[1].getUserFirstName(),userData[1].getUserLastName()).getBody();
            System.out.println(hasFirstStudentFolder.getExists());
            System.out.println(hasSecondtStudentFolder.getExists());
            if(!hasFirstStudentFolder.getExists()){
                RequestResponse result = createUserFolder(userData[0].getStudentDNI(),userData[0].getUserFirstName(),userData[0].getUserLastName()).getBody();
                System.out.println(result);
            }
            if(!hasSecondtStudentFolder.getExists()){
                RequestResponse result = createUserFolder(userData[1].getStudentDNI(),userData[1].getUserFirstName(),userData[1].getUserLastName()).getBody();
                System.out.println(result);
            }
            InputStream is = file.getInputStream();
            if(validateFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                String firstStudentFolderPath = GRADUATE_WORK_FOLDER + userData[0].getStudentDNI() + "@" + userData[0].getUserLastName() + userData[0].getUserFirstName();
                String secondtStudentFolderPath = GRADUATE_WORK_FOLDER + userData[1].getStudentDNI() + "@" + userData[1].getUserLastName() + userData[1].getUserFirstName();
                System.out.println(firstStudentFolderPath);
                System.out.println(secondtStudentFolderPath);
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",firstStudentFolderPath + "/propuestas/" + file.getOriginalFilename(),fileTemp));
                s3client.copyObject(new CopyObjectRequest("bucket-gw-storage",firstStudentFolderPath + "/propuestas/" + file.getOriginalFilename(),"bucket-gw-storage",secondtStudentFolderPath+"/propuestas/"+file.getOriginalFilename()));
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                System.out.println("Nombre invalido");
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }

        }catch (IOException e){
            throw new IOException(e.getMessage());
        }

    }


    public ResponseEntity<RequestResponse> uploadCoordinatorEvaluation(MultipartFile file, List<UserDataRequest> studentData, UserDataRequest coordinatorData) throws IOException {
        try (InputStream is = file.getInputStream()) {
            if(validateRevisionFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                String studentFolderPath = GRADUATE_WORK_FOLDER + studentData.getFirst().getUserDNI()+"@"+studentData.getFirst().getUserLastName().split(" ")[0]+studentData.getFirst().getUserFirstName().split(" ")[0] + "/propuestas/revisiones/";
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",studentFolderPath + file.getOriginalFilename(),fileTemp));
                if(studentData.size() > 1){
                    String partnerFolderPath = GRADUATE_WORK_FOLDER + studentData.getLast().getUserDNI()+"@"+studentData.getLast().getUserLastName().split(" ")[0]+studentData.getLast().getUserFirstName().split(" ")[0] + "/propuestas/revisiones/";
                    s3client.copyObject(new CopyObjectRequest("bucket-gw-storage",studentFolderPath + file.getOriginalFilename(),"bucket-gw-storage",partnerFolderPath+file.getOriginalFilename()));
                }
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public ResponseEntity<RequestResponse> uploadGraduateWork(MultipartFile file, List<UserDataRequest> studentDataList) throws IOException {
        try (InputStream is = file.getInputStream()) {
            //Validamos el nombre del archivo
            System.out.println("Nombre del archivo = " + file.getOriginalFilename().replace(PDF_EXTENSION,""));
            if(validateFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                String studentFolderPath = GRADUATE_WORK_FOLDER + studentDataList.getFirst().getUserDNI() + "@" + studentDataList.getFirst().getUserLastName().split(" ")[0] + studentDataList.getFirst().getUserFirstName().split(" ")[0] + "/";

                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",studentFolderPath + "desarrollo/"+ file.getOriginalFilename(),fileTemp));
                if(studentDataList.size() > 1){
                    String partnerFolderPath = GRADUATE_WORK_FOLDER + studentDataList.getLast().getUserDNI() + "@" + studentDataList.getLast().getUserLastName().split(" ")[0] + studentDataList.getLast().getUserFirstName().split(" ")[0] + "/";
                    s3client.copyObject(new CopyObjectRequest("bucket-gw-storage",studentFolderPath + "desarrollo/"+ file.getOriginalFilename(),"bucket-gw-storage",partnerFolderPath + "desarrollo/"+file.getOriginalFilename()));
                }
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public ResponseEntity<RequestResponse> uploadRevision(MultipartFile file, List<UserDataRequest> studentDataList) throws IOException {
        try (InputStream is = file.getInputStream()) {
            //Validamos el nombre del archivo
            System.out.println("Nombre del archivo = " + file.getOriginalFilename().replace(PDF_EXTENSION,""));
            if(validateRevisionFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                String studentFolderPath = GRADUATE_WORK_FOLDER + studentDataList.getFirst().getUserDNI() + "@" + studentDataList.getFirst().getUserLastName().split(" ")[0] + studentDataList.getFirst().getUserFirstName().split(" ")[0] + "/";
                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",studentFolderPath + "desarrollo/revisiones/"+ file.getOriginalFilename(),fileTemp));
                if(studentDataList.size() > 1){
                    String partnerFolderPath = GRADUATE_WORK_FOLDER + studentDataList.getLast().getUserDNI() + "@" + studentDataList.getLast().getUserLastName().split(" ")[0] + studentDataList.getLast().getUserFirstName().split(" ")[0] + "/";
                    s3client.copyObject(new CopyObjectRequest("bucket-gw-storage",studentFolderPath + "desarrollo/revisiones/"+ file.getOriginalFilename(),"bucket-gw-storage",partnerFolderPath + "desarrollo/revisiones/"+file.getOriginalFilename()));
                }
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public ResponseEntity<RequestResponse> uploadFinalSubmittion(MultipartFile file, List<UserDataRequest> studentDataList) throws IOException {
        try (InputStream is = file.getInputStream()) {
            //Validamos el nombre del archivo
            System.out.println("Nombre del archivo = " + file.getOriginalFilename().replace(PDF_EXTENSION,""));
            if(validateFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                String studentFolderPath = GRADUATE_WORK_FOLDER + studentDataList.getFirst().getUserDNI() + "@" + studentDataList.getFirst().getUserLastName().split(" ")[0] + studentDataList.getFirst().getUserFirstName().split(" ")[0] + "/";
                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",studentFolderPath + "defensa/"+ file.getOriginalFilename(),fileTemp));
                if(studentDataList.size() > 1){
                    String partnerFolderPath = GRADUATE_WORK_FOLDER + studentDataList.getLast().getUserDNI() + "@" + studentDataList.getLast().getUserLastName().split(" ")[0] + studentDataList.getLast().getUserFirstName().split(" ")[0] + "/";
                    s3client.copyObject(new CopyObjectRequest("bucket-gw-storage",studentFolderPath + "defensa/"+ file.getOriginalFilename(),"bucket-gw-storage",partnerFolderPath + "defensa/"+ file.getOriginalFilename()));
                }
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public ResponseEntity<RequestResponse> uploadCulmination(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            //Validamos el nombre del archivo
            System.out.println("Nombre del archivo = " + file.getOriginalFilename().replace(PDF_EXTENSION,""));
            if(validateFileName(new ValidateFileNameRequest(file.getOriginalFilename().replace(PDF_EXTENSION,"")))){
                File fileTemp = File.createTempFile("upload", ".tmp");
                file.transferTo(fileTemp);
                s3client.putObject(new PutObjectRequest("bucket-gw-storage",FOLDER_GWFR_NAME + file.getOriginalFilename(),fileTemp));
                return ResponseEntity.ok(new RequestResponse("Archivo Subido Correctamente"));
            }else{
                return ResponseEntity.badRequest().body(new RequestResponse("Error en nombre de archivo"));
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public List<String> listFiles() throws IOException {
        try {
            ListObjectsV2Result result = s3client.listObjectsV2("bucket-gw-storage");
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            List<String> fileNames = new ArrayList<>();
            for (S3ObjectSummary os : objects) {
                System.out.println("* " + os.getKey());
                fileNames.add(os.getKey());
            }
            return fileNames;
        }catch (AmazonServiceException e){
            throw new IOException(e.getMessage());
        }
    }

    public String deleteFile(String fileName){
        try{
            s3client.deleteObject("bucket-gw-storage",fileName);
            return "Archivo eliminado exitosamente";
        }catch (AmazonServiceException e){
            throw new AmazonServiceException(e.getMessage());
        }
    }

    public  List<String> getGraduateWorkFiles() throws IOException{
        try {
            ListObjectsV2Result result = s3client.listObjectsV2("bucket-gw-storage","graduatework/");
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            List<String> fileNames = new ArrayList<>();
            for (S3ObjectSummary os : objects) {
                fileNames.add(os.getKey());
            }
            return fileNames;
        }catch (AmazonServiceException e){
            throw new IOException(e.getMessage());
        }
    }

    public  List<String> getGraduateWorkReviewsFiles() throws IOException{
        try {
            ListObjectsV2Result result = s3client.listObjectsV2("bucket-gw-storage","graduatework/reviews/");
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            List<String> fileNames = new ArrayList<>();
            for (S3ObjectSummary os : objects) {
                fileNames.add(os.getKey());
            }
            return fileNames;
        }catch (AmazonServiceException e){
            throw new IOException(e.getMessage());
        }
    }

    public  List<String> getGraduateWorkFinalFiles() throws IOException{
        try {
            ListObjectsV2Result result = s3client.listObjectsV2("bucket-gw-storage","graduatework/final/");
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            List<String> fileNames = new ArrayList<>();
            for (S3ObjectSummary os : objects) {
                fileNames.add(os.getKey());
            }
            return fileNames;
        }catch (AmazonServiceException e){
            throw new IOException(e.getMessage());
        }
    }

    public  List<String> getGraduateWorkProposalsFiles() throws IOException{
        try {
            ListObjectsV2Result result = s3client.listObjectsV2("bucket-gw-storage","proposal/");
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            List<String> fileNames = new ArrayList<>();
            for (S3ObjectSummary os : objects) {
                fileNames.add(os.getKey());
            }
            return fileNames;
        }catch (AmazonServiceException e){
            throw new IOException(e.getMessage());
        }
    }

}


