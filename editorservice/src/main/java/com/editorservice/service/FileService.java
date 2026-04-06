package com.editorservice.service;

import com.editorservice.entity.CodeFile;

import java.util.List;

public interface FileService {

    CodeFile createFile(CodeFile codeFile);

    CodeFile getFileById(Integer fileId);

    List<CodeFile> getFilesByProject(Integer projectId);

    String getFileContent(Integer fileId);

    CodeFile updateFileContent(Integer fileId, String content, Integer userId);

    CodeFile renameFile(Integer fileId, String newName);

    void deleteFile(Integer fileId);

    CodeFile restoreFile(Integer fileId);

    CodeFile moveFile(Integer fileId, String newPath);

    CodeFile createFolder(Integer projectId, String folderName, String path, Integer createdById);

    List<CodeFile> getFileTree(Integer projectId);

    List<CodeFile> searchInProject(Integer projectId, String keyword);
}