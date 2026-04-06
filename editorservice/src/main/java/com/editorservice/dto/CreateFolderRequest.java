package com.editorservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFolderRequest {

    @NotNull
    private Integer projectId;

    @NotBlank
    private String folderName;

    @NotBlank
    private String path;

    @NotNull
    private Integer createdById;
}