package org.aiassistant.ai.repositories;

import org.aiassistant.ai.entities.Token;
import org.aiassistant.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository <Token, Long> {
    Optional<Token> findByUser(User user);
}
