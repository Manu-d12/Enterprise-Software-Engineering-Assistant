package org.aiassistant.services;

import org.aiassistant.dtos.DocumentDTO;
import org.aiassistant.entities.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    List<DocumentDTO> uploadDocument(String projectId, MultipartFile[] files, String userId);
    List<DocumentDTO> getDocsByProject(String projectId);
}
