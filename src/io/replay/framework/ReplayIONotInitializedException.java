package io.replay.framework;

/**
 * Exception that throws when static methods are called before ReplayIO is initialized.
 * It is a subclass of RuntimeException, so that try...catch is not needed everywhere.
 */
public class ReplayIONotInitializedException extends RuntimeException {
    public ReplayIONotInitializedException() {
        super("ReplayIO is not initialized! Please call init() method before calling this static method.");
    }
}
