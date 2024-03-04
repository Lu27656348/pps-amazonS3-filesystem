package com.luiscarlossomoza.interfaces;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateFileNameRequest {
    private String fileName;
    private String studentDNI;
    private String userFirstName;
    private String userLastName;
    private Integer revisionNumber;
    private Boolean areTwo;
    private String escuela;

    public ValidateFileNameRequest(String fileName) {
        this.fileName = fileName;
    }
}
