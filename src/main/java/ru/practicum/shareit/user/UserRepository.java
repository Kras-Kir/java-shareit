package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Можно добавить кастомные методы:
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long excludedUserId);
}
