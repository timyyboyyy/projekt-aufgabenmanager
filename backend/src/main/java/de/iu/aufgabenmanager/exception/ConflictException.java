package de.iu.aufgabenmanager.exception;

/** Konflikt mit dem aktuellen Zustand (z. B. Benutzername vergeben) -> HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
