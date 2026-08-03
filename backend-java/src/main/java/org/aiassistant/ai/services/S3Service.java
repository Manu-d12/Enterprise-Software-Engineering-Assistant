package org.aiassistant.ai.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    public String saveFile(File file, String userId, String projectId) {
        String key = buildS3Key(userId, projectId, file.getName());
        s3Client.putObject(new PutObjectRequest(bucketName, key, file));
        return key;
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
}
