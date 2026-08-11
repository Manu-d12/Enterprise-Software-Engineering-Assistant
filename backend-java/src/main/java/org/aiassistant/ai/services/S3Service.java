package org.aiassistant.ai.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.aiassistant.services.ProjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 s3Client;

    private final ProjectService projectService;

    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    @Value("${project.document.upload-path}")
    private String docsDir;

    public String saveFile(File file, String projectId, String userId) {
        String key = buildS3Key(userId, projectId, file.getName());
        s3Client.putObject(new PutObjectRequest(bucketName, key, file));
        projectService.setS3Path(projectId, key);
        return key;
    }

    public String saveProjectDocs(MultipartFile file, String userId, String projectId) {
        try {
            String key = buildS3KeyForDocs(userId, projectId, file.getOriginalFilename());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType()); // Optional but recommended

            try (InputStream is = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(bucketName, key, is, metadata));
            }

            return key;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return "FAILED_TO_UPLOAD_TO_S3";
    }

    public File downloadFile(String userId, String projectId, String fileName) throws IOException {
        String key = buildS3Key(userId, projectId, fileName);
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));

        File tempFile = File.createTempFile("s3-download-", "-" + fileName);
        try (InputStream inputStream = s3Object.getObjectContent();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    private String buildS3Key(String userId, String projectId, String fileName) {
        return String.format("%s/%s/%s", userId, projectId, fileName);
    }

    private String buildS3KeyForDocs(String userId, String projectId, String fileName) {
        return String.format("%s/%s/%s/%s", userId, projectId, docsDir, fileName);
    }
}
