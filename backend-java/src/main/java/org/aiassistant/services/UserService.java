package org.aiassistant.services;

import org.aiassistant.dtos.UserRegisterDTO;

public interface UserService {
    public UserRegisterDTO addUser(UserRegisterDTO registerDTO);
}
