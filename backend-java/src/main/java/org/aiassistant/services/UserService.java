package org.aiassistant.services;

import org.aiassistant.dtos.UserRegisterDTO;
import org.aiassistant.entities.User;

public interface UserService {
    public UserRegisterDTO addUser(UserRegisterDTO registerDTO);
    public User findUserByUsername(String username);
    public User findById(String id);
}
