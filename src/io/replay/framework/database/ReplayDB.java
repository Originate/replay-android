package io.replay.framework.database;

/**
 * Created by parthpadgaonkar on 8/13/14.
 */
public class ReplayDB {
    private static ReplayDB ourInstance = new ReplayDB();

    public static ReplayDB getInstance() {
        return ourInstance;
    }

    private ReplayDB() {
    }
}
