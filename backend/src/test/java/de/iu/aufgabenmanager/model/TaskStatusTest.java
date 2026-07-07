package de.iu.aufgabenmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * US5: erlaubte Statusuebergaenge einer Aufgabe. Linearer Ablauf mit einem Schritt zurueck,
 * kein Ueberspringen, kein Uebergang auf den gleichen Status.
 */
class TaskStatusTest {

    @DisplayName("canTransitionTo bildet die erlaubten Uebergaenge ab")
    @ParameterizedTest(name = "{0} -> {1} = {2}")
    @CsvSource({
            "OFFEN,          IN_BEARBEITUNG, true",
            "OFFEN,          ERLEDIGT,       false",
            "OFFEN,          OFFEN,          false",
            "IN_BEARBEITUNG, ERLEDIGT,       true",
            "IN_BEARBEITUNG, OFFEN,          true",
            "IN_BEARBEITUNG, IN_BEARBEITUNG, false",
            "ERLEDIGT,       IN_BEARBEITUNG, true",
            "ERLEDIGT,       OFFEN,          false",
            "ERLEDIGT,       ERLEDIGT,       false"
    })
    void canTransitionTo(TaskStatus from, TaskStatus to, boolean allowed) {
        assertThat(from.canTransitionTo(to)).isEqualTo(allowed);
    }
}
