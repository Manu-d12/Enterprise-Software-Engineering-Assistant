package org.aiassistant.services.impl;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.UserRegisterDTO;
import org.aiassistant.entities.User;
import org.aiassistant.repositories.UserRepo;
import org.aiassistant.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserRegisterDTO addUser(UserRegisterDTO registerDTO) {
        User user = modelMapper.map(registerDTO, User.class);
        User savedUser = this.userRepo.save(user);
        return this.modelMapper.map(savedUser, UserRegisterDTO.class);
    }
}
