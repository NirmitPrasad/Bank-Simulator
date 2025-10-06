package com.bfe.route.enums.exceptions;

/**
 * Exception thrown when trying to create or update an account
 * with an account number that already exists.
 */
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
