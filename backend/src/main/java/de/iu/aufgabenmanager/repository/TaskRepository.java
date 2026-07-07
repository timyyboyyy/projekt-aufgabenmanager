package de.iu.aufgabenmanager.repository;

import de.iu.aufgabenmanager.model.Task;
import de.iu.aufgabenmanager.model.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    /** Gesamtzahl der Aufgaben eines Projekts (Nenner der Fortschrittsberechnung, US6). */
    long countByProjectId(Long projectId);

    /** Anzahl Aufgaben eines Projekts in einem bestimmten Status (Zaehler fuer Fortschritt, US6). */
    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
}
