package org.aiassistant.ai.tools.planning;

import org.aiassistant.utils.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class FileTools {

    private final String docsBasePath;
    private final String projectId;

    public FileTools(String docsBasePath, String projectId) {
        this.docsBasePath = docsBasePath;
        this.projectId = projectId;
    }

    @Tool(description = "Read the actual content of a file mentioned in the interface index")
    public String read_file(
            @ToolParam(description = "name (the 'key' in the interface index) of the file to read")
            String fileName) {
        String base = System.getProperty("user.dir") + File.separator
                + docsBasePath + File.separator
                + "project-" + projectId + File.separator + fileName;
        return FileUtil.readFileContent(base);
    }
}