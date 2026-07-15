package org.aiassistant.ai.tools;

import org.aiassistant.utils.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileTools {

    @Value("${codegen.workspace.dir}")
    private String codeGenWorkspace;

    @Tool(description = "Read the current contents of an already-generated file")
    public String read_file(
            @ToolParam(description = "Path (Mentioned in the Blueprint) of the file to read")
            String path) {
        System.out.println("read_file called");
        return FileUtil.readFileContent(System.getProperty("user.dir") + File.separator + codeGenWorkspace + File.separator + path);
    }
}