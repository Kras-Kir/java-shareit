package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUser(Long userId);
}