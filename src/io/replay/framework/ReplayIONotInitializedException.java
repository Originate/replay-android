package io.replay.framework;

/**
 * Exception that throws when static methods are called before ReplayIO is initialized.
 */
public class ReplayIONotInitializedException extends Exception {
    public ReplayIONotInitializedException() {
        super("ReplayIO is not initialized! Please call init() method before calling this static method.");
    }
}
