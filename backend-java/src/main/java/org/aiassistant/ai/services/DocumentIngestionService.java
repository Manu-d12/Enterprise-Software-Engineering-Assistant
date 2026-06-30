package org.aiassistant.ai.services;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    public void ingestSampleDocs() {
        List<Document> documents = List.of(
                new Document(
                        "Spring Security is used to secure REST APIs using filters, authentication providers, and security context.",
                        Map.of("source", "spring-security")
                ),
                new Document(
                        "ChromaDB stores vector embeddings and allows similarity search over documents.",
                        Map.of("source", "chromadb")
                ),
                new Document(
                        "Ollama can run local embedding models like nomic-embed-text.",
                        Map.of("source", "ollama")
                )
        );

        vectorStore.add(documents);
    }

    @PostConstruct
    public void addDocs() {
        System.out.println("Started  ");
        final List<Document> documents = this.vectorStore.similaritySearch("Ollama ");
        for (Document d : documents) {
            System.out.println(d.getText());
        }
    }
}