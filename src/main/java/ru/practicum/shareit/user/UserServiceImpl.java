package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emailToUserMap = new HashMap<>(); // Исправлено: храним User вместо Long
    private Long idCounter = 1L;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> result = new ArrayList<>();
        for (User user : users.values()) {
            result.add(UserMapper.toUserDto(user));
        }
        return result;
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        checkEmailUniqueness(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        emailToUserMap.put(user.getEmail(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        User existingUser = users.get(userId);

        if (userDto.getEmail() != null) {
            String newEmail = userDto.getEmail();
            String currentEmail = existingUser.getEmail();

            validateUserEmail(newEmail);

            if (!newEmail.equals(currentEmail)) {
                checkEmailUniqueness(newEmail);

                emailToUserMap.remove(currentEmail);
                existingUser.setEmail(newEmail);
                emailToUserMap.put(newEmail, existingUser);
            }
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        User user = users.get(userId);
        emailToUserMap.remove(user.getEmail());
        users.remove(userId);
    }

    private void checkEmailUniqueness(String email) {
        if (emailToUserMap.containsKey(email)) {
            throw new ConflictException("Пользователь с email " + email + " уже существует");
        }
    }

    private void validateUserEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Некорректный формат email");
        }
    }
}