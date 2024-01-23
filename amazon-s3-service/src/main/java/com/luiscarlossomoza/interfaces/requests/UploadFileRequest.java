package com.luiscarlossomoza.interfaces.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileRequest {
    private CreateUserFolder userData;
}
