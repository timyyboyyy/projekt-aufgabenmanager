package de.iu.aufgabenmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.iu.aufgabenmanager.model.Project;
import de.iu.aufgabenmanager.model.ProjectStatus;
import de.iu.aufgabenmanager.model.Role;
import de.iu.aufgabenmanager.model.RoleName;
import de.iu.aufgabenmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * US8: die Sicht-Queries, mit denen Projekte je Rolle gefiltert werden, gegen H2.
 */
@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TestEntityManager em;

    private User leiter;
    private User member;
    private User outsider;
    private Project aktivesProjekt;
    private Project archiviertesProjekt;

    @BeforeEach
    void setUp() {
        Role leiterRole = em.persist(new Role(RoleName.PROJEKTLEITER));
        Role mitarbeiterRole = em.persist(new Role(RoleName.MITARBEITER));

        leiter = em.persist(new User("pl", "hash", leiterRole));
        member = em.persist(new User("m1", "hash", mitarbeiterRole));
        outsider = em.persist(new User("m2", "hash", mitarbeiterRole));

        aktivesProjekt = new Project("Aktiv", "", leiter);
        aktivesProjekt.getMembers().add(member);
        em.persist(aktivesProjekt);

        archiviertesProjekt = new Project("Archiv", "", leiter);
        archiviertesProjekt.setStatus(ProjectStatus.ARCHIVIERT);
        em.persist(archiviertesProjekt);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findByLeiterId liefert alle Projekte des Leiters (inkl. archivierter)")
    void findByLeiterId() {
        assertThat(projectRepository.findByLeiterId(leiter.getId()))
                .extracting(Project::getName)
                .containsExactlyInAnyOrder("Aktiv", "Archiv");
    }

    @Test
    @DisplayName("findByStatus filtert auf aktive Projekte")
    void findByStatus() {
        assertThat(projectRepository.findByStatus(ProjectStatus.AKTIV))
                .extracting(Project::getName)
                .containsExactly("Aktiv");
    }

    @Test
    @DisplayName("findByMembersId liefert nur Projekte, in denen der Benutzer Mitglied ist")
    void findByMembersId() {
        assertThat(projectRepository.findByMembersId(member.getId()))
                .extracting(Project::getName)
                .containsExactly("Aktiv");
        assertThat(projectRepository.findByMembersId(outsider.getId())).isEmpty();
    }
}
