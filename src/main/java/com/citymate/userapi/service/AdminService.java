package com.citymate.userapi.service;

import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.Role;
import com.citymate.userapi.entity.User;
import com.citymate.userapi.exception.BadRequestException;
import com.citymate.userapi.exception.ResourceNotFoundException;
import com.citymate.userapi.mapper.UserMapper;
import com.citymate.userapi.repository.RoleRepository;
import com.citymate.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO setUserActive(String username, boolean active) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        user.setIsActive(active);
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    @Transactional
    public UserDTO changeUserRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rôle inconnu : " + roleName));

        user.getRoles().clear();
        user.getRoles().add(role);

        try {
            user.setProfileType(User.ProfileType.valueOf(roleName));
        } catch (IllegalArgumentException ignored) {}

        userRepository.save(user);
        return UserMapper.toDTO(user);
    }
}
