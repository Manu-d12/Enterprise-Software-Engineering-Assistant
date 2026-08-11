package org.aiassistant.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    /**
     * Writes the given text content to the specified file path, creating any
     * missing parent directories. Overwrites the file if it already exists.
     *
     * @param path    the destination file path (including file name)
     * @param content the text content to write
     * @throws IllegalArgumentException if the path is {@code null} or blank
     * @throws UncheckedIOException     if the content could not be written
     */
    public static void writeFileToPath(String path, String content) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("path must not be null or blank");
        }

        Path destination = Paths.get(path);
        try {
            Path parentDir = destination.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.writeString(destination, content == null ? "" : content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write file to path: " + path, e);
        }
    }


    /**
     * Reads the entire content of the file at the specified path as UTF-8 text.
     *
     * @param path the file path (including file name) to read
     * @return the file's content as a string
     * @throws IllegalArgumentException if the path is {@code null} or blank
     * @throws UncheckedIOException     if the file does not exist or could not be read
     */
    public static String readFileContent(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("path must not be null or blank");
        }
        Path source = Paths.get(path);
        try {
            return Files.readString(source, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file from path: " + path, e);
        }
    }

    public static void recursiveDelete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }
}
