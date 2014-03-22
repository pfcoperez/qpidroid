package com.orionsword.qpid;

import java.io.PrintStream;

/**
 *
 */
public class QPIDLogger {

    public void error(String msg) { logEntry(msg,LevelType.ERROR);};
    public void warn(String msg) { logEntry(msg,LevelType.WARN);};
    public void info(String msg) { logEntry(msg,LevelType.INFO);};

    private enum LevelType { ERROR, WARN, INFO};
    private void logEntry(String msg, LevelType level) {
        PrintStream ps;
        switch(level)
        {
            case ERROR:
                ps = System.err;
                break;
            case WARN:
                ps = System.out;
                break;
            default:
                ps = System.out;
                break;
        }
        ps.print(msg);
    }
}
