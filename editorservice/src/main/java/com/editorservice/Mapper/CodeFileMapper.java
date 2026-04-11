package com.editorservice.Mapper;

import com.editorservice.dto.CodeFileRequest;
import com.editorservice.dto.CodeFileResponse;
import com.editorservice.dto.CreateFolderRequest;
import com.editorservice.dto.CreateFolderResponse;
import com.editorservice.entity.CodeFile;

public class CodeFileMapper {
    public static CodeFileResponse toResponse(CodeFile file){
        return CodeFileResponse.builder()
                .fileId(file.getFileId())
                .projectId(file.getProjectId())
                .name(file.getName())
                .path(file.getPath())
                .language(file.getLanguage())
                .content(file.getContent())
                .size(file.getSize())
                .createdById(file.getCreatedById())
                .lastEditedBy(file.getLastEditedBy())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .isDeleted(file.getIsDeleted())
                .build();
    }

    public static CreateFolderResponse toFolderResponse(CodeFile codeFile){
        return CreateFolderResponse
                .builder()
                .fileId(codeFile.getFileId())
                .projectId(codeFile.getProjectId())
                .folderName(codeFile.getName())
                .path(codeFile.getPath())
                .language(codeFile.getLanguage())
                .createdById(codeFile.getCreatedById())
                .build();
    }

}
