package com.editorservice.service.impl;

import com.editorservice.entity.CodeFile;
import com.editorservice.exception.ResourceNotFoundException;
import com.editorservice.repository.FileRepository;
import com.editorservice.service.FileService;
import com.editorservice.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public CodeFile createFile(CodeFile codeFile) {
        String normalizedPath = PathUtil.normalizePath(codeFile.getPath());

        if (fileRepository.findByProjectIdAndPathAndIsDeletedFalse(codeFile.getProjectId(), normalizedPath).isPresent()) {
            throw new IllegalArgumentException("File already exists at path: " + normalizedPath);
        }

        codeFile.setPath(normalizedPath);
        if (codeFile.getContent() == null) {
            codeFile.setContent("");
        }
        codeFile.setSize((long) codeFile.getContent().getBytes().length);

        return fileRepository.save(codeFile);
    }

    @Override
    public CodeFile getFileById(Integer fileId) {
        return fileRepository.findByFileIdAndIsDeletedFalse(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    }

    @Override
    public List<CodeFile> getFilesByProject(Integer projectId) {
        return fileRepository.findByProjectIdAndIsDeletedFalse(projectId);
    }

    @Override
    public String getFileContent(Integer fileId) {
        CodeFile file = getFileById(fileId);
        return file.getContent();
    }

    @Override
    public CodeFile updateFileContent(Integer fileId, String content, Integer userId) {
        CodeFile file = getFileById(fileId);

        file.setContent(content);
        file.setLastEditedBy(userId);
        file.setSize((long) content.getBytes().length);

        return fileRepository.save(file);
    }

    @Override
    public CodeFile renameFile(Integer fileId, String newName) {
        CodeFile file = getFileById(fileId);

        String oldPath = file.getPath();
        String parentPath = PathUtil.getParentPath(oldPath);

        String updatedPath;
        if ("folder".equalsIgnoreCase(file.getLanguage())) {
            updatedPath = PathUtil.buildFolderPath(parentPath, newName);
        } else {
            updatedPath = PathUtil.buildFilePath(parentPath, newName);
        }

        file.setName(newName);
        file.setPath(updatedPath);

        return fileRepository.save(file);
    }

    @Override
    public void deleteFile(Integer fileId) {
        CodeFile file = getFileById(fileId);
        file.setIsDeleted(true);
        fileRepository.save(file);
    }

    @Override
    public CodeFile restoreFile(Integer fileId) {
        CodeFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));

        file.setIsDeleted(false);
        return fileRepository.save(file);
    }

    @Override
    public CodeFile moveFile(Integer fileId, String newPath) {
        CodeFile file = getFileById(fileId);

        String normalizedPath = PathUtil.normalizePath(newPath);
        file.setPath(normalizedPath);

        return fileRepository.save(file);
    }

    @Override
    public CodeFile createFolder(Integer projectId, String folderName, String path, Integer createdById) {
        String folderPath = PathUtil.buildFolderPath(path, folderName);

        if (fileRepository.findByProjectIdAndPathAndIsDeletedFalse(projectId, folderPath).isPresent()) {
            throw new IllegalArgumentException("Folder already exists at path: " + folderPath);
        }

        CodeFile folder = CodeFile.builder()
                .projectId(projectId)
                .name(folderName)
                .path(folderPath)
                .language("folder")
                .content("")
                .size(0L)
                .createdById(createdById)
                .lastEditedBy(createdById)
                .isDeleted(false)
                .build();

        return fileRepository.save(folder);
    }

    @Override
    public List<CodeFile> getFileTree(Integer projectId) {
        return fileRepository.findByProjectIdAndIsDeletedFalse(projectId)
                .stream()
                .sorted((a, b) -> a.getPath().compareToIgnoreCase(b.getPath()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CodeFile> searchInProject(Integer projectId, String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        return fileRepository.findByProjectIdAndIsDeletedFalse(projectId)
                .stream()
                .filter(file ->
                        (file.getName() != null && file.getName().toLowerCase().contains(lowerKeyword)) ||
                                (file.getPath() != null && file.getPath().toLowerCase().contains(lowerKeyword)) ||
                                (file.getContent() != null && file.getContent().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
}