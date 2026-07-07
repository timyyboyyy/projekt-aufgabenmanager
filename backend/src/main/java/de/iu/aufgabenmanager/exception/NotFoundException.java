package de.iu.aufgabenmanager.exception;

/** Angefragte Ressource existiert nicht -> HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
