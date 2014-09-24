package io.replay.framework.error;

/**
 * Exception that throws when static methods are called before ReplayIO is initialized.
 * It is a subclass of {@link IllegalStateException}, so that try...catch is not needed everywhere.
 */
public class ReplayIONotInitializedException extends IllegalStateException {
    public ReplayIONotInitializedException() {
        super("ReplayIO is not initialized! Please call init() method before calling this method.");
    }
}
