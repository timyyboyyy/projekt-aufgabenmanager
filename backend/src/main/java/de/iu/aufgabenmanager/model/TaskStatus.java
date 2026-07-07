package de.iu.aufgabenmanager.model;

/**
 * Status einer Aufgabe. Vorgesehener Ablauf: OFFEN -> IN_BEARBEITUNG -> ERLEDIGT.
 */
public enum TaskStatus {
    OFFEN,
    IN_BEARBEITUNG,
    ERLEDIGT;

    /**
     * Erlaubte Statuswechsel (US5). Linearer Ablauf mit der Moeglichkeit, jeweils einen
     * Schritt zurueckzugehen; das Ueberspringen von IN_BEARBEITUNG ist nicht erlaubt.
     * Ein Wechsel auf den gleichen Status wird hier nicht als Uebergang gewertet.
     */
    public boolean canTransitionTo(TaskStatus target) {
        return switch (this) {
            case OFFEN -> target == IN_BEARBEITUNG;
            case IN_BEARBEITUNG -> target == ERLEDIGT || target == OFFEN;
            case ERLEDIGT -> target == IN_BEARBEITUNG;
        };
    }
}
