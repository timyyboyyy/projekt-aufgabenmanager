package de.iu.aufgabenmanager.repository;

import de.iu.aufgabenmanager.model.Role;
import de.iu.aufgabenmanager.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
