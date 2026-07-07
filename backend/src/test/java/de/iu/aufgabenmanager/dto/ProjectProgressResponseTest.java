package de.iu.aufgabenmanager.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * US6: Fortschrittsberechnung (% erledigter Aufgaben) inkl. kaufmaennischer Rundung
 * und Sonderfall "keine Aufgaben".
 */
class ProjectProgressResponseTest {

    @DisplayName("percentDone wird korrekt berechnet und gerundet")
    @ParameterizedTest(name = "{1} von {0} -> {2}%")
    @CsvSource({
            "0, 0, 0",
            "4, 2, 50",
            "3, 1, 33",
            "3, 2, 67",
            "5, 5, 100"
    })
    void percentDone(long total, long done, int expectedPercent) {
        ProjectProgressResponse progress = ProjectProgressResponse.of(1L, total, done);

        assertThat(progress.totalTasks()).isEqualTo(total);
        assertThat(progress.doneTasks()).isEqualTo(done);
        assertThat(progress.percentDone()).isEqualTo(expectedPercent);
    }

    @Test
    @DisplayName("ohne Aufgaben ist der Fortschritt 0% (keine Division durch null)")
    void noTasksIsZero() {
        assertThat(ProjectProgressResponse.of(7L, 0, 0).percentDone()).isZero();
    }
}
