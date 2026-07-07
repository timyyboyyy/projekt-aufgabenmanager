package de.iu.aufgabenmanager.exception;

/** Aktion ist dem angemeldeten Benutzer nicht erlaubt -> HTTP 403. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
