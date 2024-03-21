package com.luiscarlossomoza.interfaces;

import lombok.Data;

@Data
public class UserDataRequest {
    private String userDNI;
    private String userPassword;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userEmailAlt;
    private String userPhone;
    private String schoolName;
    private String graduateWorkId;

}
