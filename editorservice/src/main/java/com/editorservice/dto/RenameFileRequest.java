package com.editorservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenameFileRequest {

    @NotBlank
    private String newName;
}