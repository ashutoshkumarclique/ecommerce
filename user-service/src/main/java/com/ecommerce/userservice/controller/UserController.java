package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.UserDto;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            @RequestHeader("X-User-Id") String id,
            @RequestHeader("X-User-Role") String role) {

        log.debug("getCurrentUser called with userId: {}, role: {}", id, role);

        Long userId = Long.valueOf(id);

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            log.warn("User not found for id: {}", userId);
            return ResponseEntity.notFound().build();
        }

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateUser(
            @RequestHeader("X-User-Id") String id,
            @RequestHeader("X-User-Role") String role,
            @RequestBody UserDto userDto) {

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        Long userId = Long.valueOf(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getUserName() != null) {
            user.setUsername(userDto.getUserName());
        }

        userRepository.save(user);
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role) {

        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role) {

        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
