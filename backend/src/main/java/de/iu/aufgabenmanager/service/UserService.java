package de.iu.aufgabenmanager.service;

import de.iu.aufgabenmanager.dto.CreateUserRequest;
import de.iu.aufgabenmanager.dto.UpdateRoleRequest;
import de.iu.aufgabenmanager.dto.UpdateUserRequest;
import de.iu.aufgabenmanager.dto.UserResponse;
import de.iu.aufgabenmanager.exception.ConflictException;
import de.iu.aufgabenmanager.exception.NotFoundException;
import de.iu.aufgabenmanager.model.Role;
import de.iu.aufgabenmanager.model.RoleName;
import de.iu.aufgabenmanager.model.User;
import de.iu.aufgabenmanager.repository.RoleRepository;
import de.iu.aufgabenmanager.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Benutzer- und Rollenverwaltung (US1). Nur fuer den Admin (Rollen-Gate im Controller).
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Benutzername bereits vergeben: " + request.username());
        }
        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                requireRole(request.role()));
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = requireUser(id);

        if (request.username() != null && !request.username().isBlank()
                && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new ConflictException("Benutzername bereits vergeben: " + request.username());
            }
            user.setUsername(request.username());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        if (request.aktiv() != null) {
            user.setAktiv(request.aktiv());
        }
        return UserResponse.from(user);
    }

    public UserResponse updateRole(Long id, UpdateRoleRequest request) {
        User user = requireUser(id);
        user.setRole(requireRole(request.role()));
        return UserResponse.from(user);
    }

    private User requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Benutzer nicht gefunden: " + id));
    }

    private Role requireRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Rolle nicht gefunden: " + name));
    }
}
