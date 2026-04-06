package com.editorservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MoveFileRequest {

    @NotBlank
    private String newPath;
}