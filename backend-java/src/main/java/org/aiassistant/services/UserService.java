package org.aiassistant.services;

import org.aiassistant.dtos.UserRegisterDTO;
import org.aiassistant.entities.User;

public interface UserService {
    UserRegisterDTO addUser(UserRegisterDTO registerDTO);
    User findUserByUsername(String username);
    User findById(String id);
}
