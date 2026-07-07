package de.iu.aufgabenmanager.exception;

/** Fachlich ungueltige Anfrage (z. B. unzulaessiger Statuswechsel) -> HTTP 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
