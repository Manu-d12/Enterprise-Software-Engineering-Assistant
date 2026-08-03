package org.aiassistant.ai.entities;

import jakarta.persistence.*;
import lombok.*;
import org.aiassistant.entities.User;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "input_tokens")
    private Long inputTokens;

    @Column(name = "outout_tokens")
    private Long outputTokens;

    @Column(name = "total_tokens")
    private Long totalTokens;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
