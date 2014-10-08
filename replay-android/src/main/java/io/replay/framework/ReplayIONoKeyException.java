package io.replay.framework;

/**
 * Exception that throws when static methods are called before ReplayIO is initialized.
 * It is a subclass of RuntimeException, so that try...catch is not needed everywhere.
 */
class ReplayIONoKeyException extends IllegalArgumentException {
    ReplayIONoKeyException() {
        super("There is no api key defined in res/values/settings.xml");
    }
}
