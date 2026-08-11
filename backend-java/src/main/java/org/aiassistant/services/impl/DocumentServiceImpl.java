package org.aiassistant.services.impl;

import org.aiassistant.ai.services.DocumentIngestionReaderService;
import org.aiassistant.ai.services.S3Service;
import org.aiassistant.dtos.DocumentDTO;
import org.aiassistant.entities.Document;
import org.aiassistant.entities.Project;
import org.aiassistant.repositories.DocumentRepo;
import org.aiassistant.services.DocumentService;
import org.aiassistant.services.ProjectService;
import org.aiassistant.utils.FileUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final String docsPath;
    private final DocumentRepo documentRepo;
    private final DocumentIngestionReaderService documentIngestionReaderService;
    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;

    public DocumentServiceImpl(
            @Value("${project.document.upload-path}") String docsPath,
            DocumentRepo documentRepo,
            DocumentIngestionReaderService documentIngestionReaderService,
            ProjectService projectService,
            S3Service s3Service,
            ModelMapper modelMapper) {
        this.docsPath = docsPath;
        this.documentRepo = documentRepo;
        this.documentIngestionReaderService = documentIngestionReaderService;
        this.projectService = projectService;
        this.modelMapper = modelMapper;
        this.s3Service = s3Service;
    }

    @Transactional
    @Override
    public List<DocumentDTO> uploadDocument(String projectId, MultipartFile[] files, String userId) {
        Project currentProject = projectService.findById(projectId);
        documentIngestionReaderService.ingestDocs(projectId, files, userId);
        List<Document> toSave = new ArrayList<>();

        for(MultipartFile file : files) {
            String uploadedPath = s3Service.saveProjectDocs(file, userId, projectId);
            toSave.add(Document.builder().url(uploadedPath).project(currentProject).build());
        }
        return documentRepo.saveAll(toSave).stream().map(
                doc -> DocumentDTO.builder().id(doc.getId()).url(doc.getUrl()).build()
        ).toList();
    }

    @Override
    public List<DocumentDTO> getDocsByProject(String projectId) {
        Project project = projectService.findById(projectId);
        List<Document> byProject = this.documentRepo.findByProject(project);

        return byProject.stream().map(doc -> modelMapper.map(doc, DocumentDTO.class)).toList();
    }

    private String getLocalFilePath(String projectId) {
        return System.getProperty("user.dir") + File.separator + docsPath + File.separator + ("project-" + projectId);
    }
}
