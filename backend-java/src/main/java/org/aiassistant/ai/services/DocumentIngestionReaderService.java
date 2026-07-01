package org.aiassistant.ai.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentIngestionReaderService {

    private final EmbeddingModel embeddingModel;
    private final ChatClient chatClient;
    private final ChromaApi chromaApi;
    private static final String SYSTEM_PROMPT = """
        You are a helpful assistant that answers questions based strictly on the
        provided context.

        Instructions:
        - Use ONLY the information in the context below to answer the question.
        - If the context does not contain enough information to answer, say:
          "I don't have enough information in the provided documents to answer that."
        - Do not make up facts or rely on outside knowledge.
        - Be concise and accurate. Quote or reference the context where relevant.
        - If the question is ambiguous, ask for clarification.

        Context:
        ---------------------
        {context}
        ---------------------

        Question:
        {userQuestion}

        Answer:
        """;

    public DocumentIngestionReaderService (
            EmbeddingModel embeddingModel,
            @Qualifier("OpenAIChatClient") ChatClient chatClient,
            ChromaApi chromaApi
    ) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.chromaApi = chromaApi;
    }


    private static final String PROJECT_COLLECTION_PREFIX = "project-";

    public void ingestDocs(String projectId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return;
        }

        VectorStore store = forProject(projectId);
        TokenTextSplitter splitter = new TokenTextSplitter();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            try {
                // Wrap MultipartFile as a Spring Resource (keeps original filename)
                Resource resource = new InputStreamResource(file.getInputStream()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                    @Override
                    public long contentLength() {
                        return file.getSize();
                    }
                };

                // Tika handles pdf, docx, txt, html, pptx, etc.
                DocumentReader reader = new TikaDocumentReader(resource);
                List<Document> documents = reader.get();

                // Tag metadata for traceability / filtering
                String fileId = UUID.randomUUID().toString();
                documents.forEach(d -> {
                    d.getMetadata().put("projectId", projectId);
                    d.getMetadata().put("fileName", file.getOriginalFilename());
                    d.getMetadata().put("fileId", fileId);
                });

                // Chunk before embedding
                List<Document> chunks = splitter.apply(documents);

                // Carry metadata onto chunks too

                chunks.forEach(c -> {
                    c.getMetadata().putIfAbsent("projectId", projectId);
                    c.getMetadata().putIfAbsent("fileName", file.getOriginalFilename());
                    c.getMetadata().putIfAbsent("fileId", fileId);
                });

                store.add(chunks);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to read file: " + file.getOriginalFilename(), e);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to ingest file: " + file.getOriginalFilename(), e);
            }
        }
    }


    public String query(String projectId, String userQuery) {
        VectorStore store = forProject(projectId);
        PromptTemplate template = new PromptTemplate(SYSTEM_PROMPT);
        Prompt prompt = template.create(Map.of(
                "context", retrieveContext(store, userQuery),
                "userQuestion", userQuery
        ));

        return chatClient.prompt(prompt).call().content();
    }

    private VectorStore forProject(String projectId) {
        String collection = PROJECT_COLLECTION_PREFIX + projectId;
        ChromaVectorStore store = ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName(collection)
                .initializeSchema(true)
                .build();
        try {
            store.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to init collection " + collection, e);
        }
        return store;
    }

    private String retrieveContext(VectorStore store, String question) {
        List<Document> docs = store.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .build());

        if (docs.isEmpty()) {
            return "";
        }

        return docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
    }

}
