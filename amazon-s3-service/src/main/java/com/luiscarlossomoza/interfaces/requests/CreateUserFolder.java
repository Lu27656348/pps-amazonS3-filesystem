package com.luiscarlossomoza.interfaces.requests;

import lombok.Data;

@Data
public class CreateUserFolder {
    private String studentDNI;
    private String userFirstName;
    private String userLastName;
}
