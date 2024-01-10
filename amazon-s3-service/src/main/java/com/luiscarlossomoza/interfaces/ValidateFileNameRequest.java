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
}
