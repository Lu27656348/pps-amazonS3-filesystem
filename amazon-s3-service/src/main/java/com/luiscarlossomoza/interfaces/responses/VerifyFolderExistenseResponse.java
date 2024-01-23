package com.luiscarlossomoza.interfaces.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyFolderExistenseResponse {
    private String message;
    private Boolean exists;
}
