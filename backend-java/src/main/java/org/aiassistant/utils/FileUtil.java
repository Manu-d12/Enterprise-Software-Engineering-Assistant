package org.aiassistant.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    /**
     * Saves the given multipart file to the provided local path.
     *
     * @param toSave    the uploaded file to persist
     * @param localPath the destination file path (including file name) where the file should be stored
     * @return {@code true} if the file was saved successfully, {@code false} otherwise
     */
    public static boolean upload(MultipartFile toSave, String localPath) {
        if (toSave == null || toSave.isEmpty()) {
            return false;
        }
        if (localPath == null || localPath.isBlank()) {
            return false;
        }

        File destination = new File(localPath);
        File parentDir = destination.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            return false;
        }

        try {
            toSave.transferTo(destination);
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }
}
