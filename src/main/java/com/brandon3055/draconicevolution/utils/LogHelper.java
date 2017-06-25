package com.brandon3055.draconicevolution.utils;

import codechicken.lib.asm.ObfMapping;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by covers1624 on 3/21/2016.
 * Covers gave me permission to use this. In fact he FORCED me to use it!!!
 */
public class LogHelper {

    private static long startTime = 0;
    private static String timerName = "";
    private static boolean timerRunning = false;

    private static Logger logger = LogManager.getLogger(DraconicEvolution.MODID);

    /**
     * Log with a supplied level.
     */
    public static void log(Level logLevel, Object object) {
        logger.log(logLevel, String.valueOf(object));
    }

    public static void log(Level logLevel, Object object, Throwable throwable) {
        logger.log(logLevel, String.valueOf(object), throwable);
    }

    //Standard log entries.

    public static void dev(Object object) {
        if (!ObfMapping.obfuscated || DEConfig.devLog) {
            log(Level.INFO, "[DEV]: " + object);
        }
    }

    public static void all(Object object) {
        log(Level.ALL, object);
    }

    public static void debug(Object object) {
        log(Level.DEBUG, object);
    }

    public static void error(Object object) {
        log(Level.ERROR, object);
    }

    public static void fatal(Object object) {
        log(Level.FATAL, object);
    }

    public static void info(Object object) {
        log(Level.INFO, object);
    }

    public static void off(Object object) {
        log(Level.OFF, object);
    }

    public static void trace(Object object) {
        log(Level.TRACE, object);
    }

    public static void warn(Object object) {
        log(Level.WARN, object);
    }

    //log with format.

    public static void dev(String object, Object... format) {
        if (!ObfMapping.obfuscated || DEConfig.devLog) {
            log(Level.INFO, "[DEV]: " + String.format(object, format));
        }
    }

    public static void all(String object, Object... format) {
        log(Level.ALL, String.format(object, format));
    }

    public static void debug(String object, Object... format) {
        log(Level.DEBUG, String.format(object, format));
    }

    public static void error(String object, Object... format) {
        log(Level.ERROR, String.format(object, format));
    }

    public static void fatal(String object, Object... format) {
        log(Level.FATAL, String.format(object, format));
    }

    public static void info(String object, Object... format) {
        log(Level.INFO, String.format(object, format));
    }

    public static void off(String object, Object... format) {
        log(Level.OFF, String.format(object, format));
    }

    public static void trace(String object, Object... format) {
        log(Level.TRACE, String.format(object, format));
    }

    public static void warn(String object, Object... format) {
        log(Level.WARN, String.format(object, format));
    }

    //Log Throwable with format.

    public static void allError(String object, Throwable throwable, Object... format) {
        log(Level.ALL, String.format(object, format), throwable);
    }

    public static void debugError(String object, Throwable throwable, Object... format) {
        log(Level.DEBUG, String.format(object, format), throwable);
    }

    public static void errorError(String object, Throwable throwable, Object... format) {
        log(Level.ERROR, String.format(object, format), throwable);
    }

    public static void fatalError(String object, Throwable throwable, Object... format) {
        log(Level.FATAL, String.format(object, format), throwable);
    }

    public static void infoError(String object, Throwable throwable, Object... format) {
        log(Level.INFO, String.format(object, format), throwable);
    }

    public static void offError(String object, Throwable throwable, Object... format) {
        log(Level.OFF, String.format(object, format), throwable);
    }

    public static void traceError(String object, Throwable throwable, Object... format) {
        log(Level.TRACE, String.format(object, format), throwable);
    }

    public static void warnError(String object, Throwable throwable, Object... format) {
        log(Level.WARN, String.format(object, format), throwable);
    }

    //Log throwable.
    public static void allError(String object, Throwable throwable) {
        log(Level.ALL, object, throwable);
    }

    public static void debugError(String object, Throwable throwable) {
        log(Level.DEBUG, object, throwable);
    }

    public static void errorError(String object, Throwable throwable) {
        log(Level.ERROR, object, throwable);
    }

    public static void fatalError(String object, Throwable throwable) {
        log(Level.FATAL, object, throwable);
    }

    public static void infoError(String object, Throwable throwable) {
        log(Level.INFO, object, throwable);
    }

    public static void offError(String object, Throwable throwable) {
        log(Level.OFF, object, throwable);
    }

    public static void traceError(String object, Throwable throwable) {
        log(Level.TRACE, object, throwable);
    }

    public static void warnError(String object, Throwable throwable) {
        log(Level.WARN, object, throwable);
    }

    //Log with trace element.

    public static void bigDev(String format, Object... data) {
        if (!ObfMapping.obfuscated || DEConfig.devLog) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            dev("****************************************");
            dev("* " + format, data);
            for (int i = 2; i < trace.length; i++) {
                dev("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
            }
            dev("****************************************");
        }
    }

    public static void bigAll(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        all("****************************************");
        all("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            all("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        all("****************************************");
    }

    public static void bigDebug(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        debug("****************************************");
        debug("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            debug("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        debug("****************************************");
    }

    public static void bigError(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        error("****************************************");
        error("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            error("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        error("****************************************");
    }

    public static void bigFatal(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        fatal("****************************************");
        fatal("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            fatal("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        fatal("****************************************");
    }

    public static void bigInfo(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        info("****************************************");
        info("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            info("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        info("****************************************");
    }

    public static void bigOff(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        off("****************************************");
        off("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            off("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        off("****************************************");
    }

    public static void bigTrace(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        trace("****************************************");
        trace("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            trace("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        trace("****************************************");
    }

    public static void bigWarn(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        warn("****************************************");
        warn("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            warn("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        warn("****************************************");
    }

    public static void startTimer(String name) {
        if (timerRunning) {
            LogHelper.error("The timer is already running!");
            return;
        }

        timerName = name;
        timerRunning = true;
        startTime = System.nanoTime();
    }

    public static void stopTimer() {
        if (!timerRunning) {
            LogHelper.error("The timer was not running!!!");
            return;
        }

        long ns = System.nanoTime() - startTime;

        String value;
        long ms = 1000000;
        long s = ms * 1000;

        if (ns > s) {
            value = Utils.round(ns / (double) s, 1000) + "s";
        }
        else if (ns > 1000) {
            value = Utils.round(ns / (double) ms, 10000) + "ms";
        }
        else {
            value = ns + "ns";
        }

        dev("[Timer]: " + timerName + " Took " + value);
        timerRunning = false;
    }
}