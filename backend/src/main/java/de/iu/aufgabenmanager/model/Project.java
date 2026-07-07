package de.iu.aufgabenmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Projekt mit Leiter (verantwortlicher Projektleiter) und zugeordneten Mitgliedern.
 * Die n:m-Zuordnung Mitglied entspricht der Join-Tabelle {@code project_member}.
 */
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String beschreibung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.AKTIV;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "leiter_id", nullable = false)
    private User leiter;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_member",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    /** Mandanten-Vorbereitung: ein aktiver Mandant, Default 1. */
    @Column(name = "organisation_id", nullable = false)
    private Long organisationId = 1L;

    protected Project() {
        // fuer JPA
    }

    public Project(String name, String beschreibung, User leiter) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.leiter = leiter;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public User getLeiter() {
        return leiter;
    }

    public void setLeiter(User leiter) {
        this.leiter = leiter;
    }

    public Set<User> getMembers() {
        return members;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }
}
