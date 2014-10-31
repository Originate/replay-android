package io.replay.framework;

import android.util.Log;

/**
 * {@link android.util.Log} wrapper.
 *
 * Can generate a tag dynamically.
 *
 * <p>Created by parthpadgaonkar on 8/22/14.</p>
 */
final class ReplayLogger {

    private static volatile boolean logEnabled;

    static boolean isLogEnabled(){
        return logEnabled;
    }

    static void setLogging(boolean enable){
        logEnabled = enable;
    }

    static void e(String format, Object... args) { log(Log.ERROR,    format, args); }
    static void w(String format, Object... args) { log(Log.WARN,     format, args); }
    static void d(String format, Object... args) { log(Log.DEBUG,    format, args); }
    static void i(String format, Object... args) { log(Log.INFO,     format, args); }
    static void v(String format, Object... args) { log(Log.VERBOSE,  format, args); }

    static void e(Throwable throwable, String format, Object... args) { log(Log.ERROR,   throwable, format, args); }
    static void w(Throwable throwable, String format, Object... args) { log(Log.WARN,    throwable, format, args); }
    static void d(Throwable throwable, String format, Object... args) { log(Log.DEBUG,   throwable, format, args); }
    static void i(Throwable throwable, String format, Object... args) { log(Log.INFO,    throwable, format, args);}
    static void v(Throwable throwable, String format, Object... args) { log(Log.VERBOSE, throwable, format, args); }

    static void e(String tag, String format, Object... args) { log(Log.ERROR,    tag, format, args); }
    static void w(String tag, String format, Object... args) { log(Log.WARN,     tag, format, args); }
    static void d(String tag, String format, Object... args) { log(Log.DEBUG,    tag, format, args); }
    static void i(String tag, String format, Object... args) { log(Log.INFO,     tag, format, args); }
    static void v(String tag, String format, Object... args) { log(Log.VERBOSE,  tag, format, args); }


    private static void log(int level, String format, Object[] args){
        if(!logEnabled) return;
        log(level, processTag(), format, args);
    }

    private static void log(int level, String tag, String format, Object[] args){
        if(!logEnabled) return;
        Log.println(level, tag, String.format(format, args));
    }

    private static void log(int level, Throwable throwable, String format, Object[] args){
        if(!logEnabled) return;
        Log.println(level, processTag(), String.format(format, args) + "\n" + Log.getStackTraceString(throwable));
    }

    /**
     * Returns a string suitable for tagging log messages. It includes the current thread's name, and
     * where the code was called from.
     * (from com.segment.android.Logger)
     */
    private static String processTag() {
        final Thread thread = Thread.currentThread();
        final StackTraceElement trace = thread.getStackTrace()[6]; // skip 6 stackframes to find the location where this was called
        return "[" + thread.getName() + "] " + trace.getFileName() + ":" + trace.getLineNumber();
    }


    
    

}
