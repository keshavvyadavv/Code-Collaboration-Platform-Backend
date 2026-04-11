package com.editorservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveFileRequest {

    @NotBlank
    private String newPath;
}