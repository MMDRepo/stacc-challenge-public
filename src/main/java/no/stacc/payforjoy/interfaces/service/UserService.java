package no.stacc.payforjoy.interfaces.service;

import no.stacc.payforjoy.model.dto.UserDto;

public interface UserService {
    UserDto findById(Long id);
    UserDto findByEmail(String email);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
}