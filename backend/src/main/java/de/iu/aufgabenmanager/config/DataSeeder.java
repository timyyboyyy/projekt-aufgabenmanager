package de.iu.aufgabenmanager.config;

import de.iu.aufgabenmanager.model.Project;
import de.iu.aufgabenmanager.model.ProjectStatus;
import de.iu.aufgabenmanager.model.Role;
import de.iu.aufgabenmanager.model.RoleName;
import de.iu.aufgabenmanager.model.Task;
import de.iu.aufgabenmanager.model.TaskStatus;
import de.iu.aufgabenmanager.model.User;
import de.iu.aufgabenmanager.repository.ProjectRepository;
import de.iu.aufgabenmanager.repository.RoleRepository;
import de.iu.aufgabenmanager.repository.TaskRepository;
import de.iu.aufgabenmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Legt beim Start Demo-Daten an (Rollen, Benutzer, Projekte, Aufgaben in allen drei Status),
 * damit Fortschrittsanzeige und Screenshots sofort Inhalt haben.
 * Idempotent: laeuft nur, solange noch keine Benutzer existieren (H2 im Datei-Modus bleibt bestehen).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      ProjectRepository projectRepository,
                      TaskRepository taskRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("DataSeeder: Daten bereits vorhanden, Seeding uebersprungen.");
            return;
        }

        Role adminRole = roleRepository.save(new Role(RoleName.ADMIN));
        Role leiterRole = roleRepository.save(new Role(RoleName.PROJEKTLEITER));
        Role mitarbeiterRole = roleRepository.save(new Role(RoleName.MITARBEITER));

        User admin = createUser("admin", "admin123", adminRole);
        User pleiter = createUser("pleiter", "pleiter123", leiterRole);
        User user1 = createUser("user1", "user123", mitarbeiterRole);
        User user2 = createUser("user2", "user123", mitarbeiterRole);

        Project website = new Project("Website-Relaunch",
                "Neugestaltung des oeffentlichen Webauftritts.", pleiter);
        website.getMembers().add(user1);
        website.getMembers().add(user2);

        Project migration = new Project("Interne Tool-Migration",
                "Abloesung des Altsystems durch die neue Plattform.", pleiter);
        migration.getMembers().add(user1);

        projectRepository.save(website);
        projectRepository.save(migration);

        // Aufgaben in allen drei Status (fuer Fortschrittsanzeige/Screenshots).
        taskRepository.save(new Task("Design-Konzept erstellen",
                "Wireframes und Farbschema abstimmen.", website, user1));
        Task inArbeit = new Task("Startseite umsetzen",
                "HTML/CSS der Landingpage.", website, user2);
        inArbeit.setStatus(TaskStatus.IN_BEARBEITUNG);
        taskRepository.save(inArbeit);
        Task erledigt = new Task("Anforderungen sammeln",
                "Stakeholder-Interviews auswerten.", website, user1);
        erledigt.setStatus(TaskStatus.ERLEDIGT);
        taskRepository.save(erledigt);

        taskRepository.save(new Task("Datenmodell abstimmen",
                "Zielschema mit dem Team klaeren.", migration, user1));
        Task migErledigt = new Task("Systemumfang festlegen",
                "Scope der Migration dokumentieren.", migration, user1);
        migErledigt.setStatus(TaskStatus.ERLEDIGT);
        taskRepository.save(migErledigt);

        log.info("DataSeeder: {} Rollen, {} Benutzer, {} Projekte, {} Aufgaben angelegt.",
                roleRepository.count(), userRepository.count(),
                projectRepository.count(), taskRepository.count());
    }

    private User createUser(String username, String rawPassword, Role role) {
        return userRepository.save(new User(username, passwordEncoder.encode(rawPassword), role));
    }
}
