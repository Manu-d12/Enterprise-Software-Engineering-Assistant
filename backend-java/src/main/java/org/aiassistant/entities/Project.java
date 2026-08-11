package org.aiassistant.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Document> documents = new ArrayList<>();

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @Column(name = "S3_Path")
    private String s3Path;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> fileSummaries;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onModify() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
