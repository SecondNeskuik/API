package io.neskdev.api.utils;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static Implementation implementation;

    public static void setImplementation(Implementation implementation) {
        Scheduler.implementation = implementation;
    }

    public static void runLater(Runnable task, long delay, TimeUnit unit) {
        if (implementation != null) {
            implementation.runLater(task, delay, unit);
        }
    }

    public static void runAsync(Runnable task) {
        if (implementation != null) {
            implementation.runAsync(task);
        }
    }

    public static Task runRepeating(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return implementation != null ? implementation.runRepeating(task, initialDelay, period, unit) : null;
    }

    public static Task runRepeatingAsync(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return implementation != null ? implementation.runRepeatingAsync(task, initialDelay, period, unit) : null;
    }

    public static void runNow(Runnable task) {
        if (implementation != null) {
            implementation.runNow(task);
        }
    }

    public static void runNowAsync(Runnable task) {
        if (implementation != null) {
            implementation.runNowAsync(task);
        }
    }

    public interface Task {
        void cancel();
    }

    public interface Implementation {
        void runLater(Runnable task, long delay, TimeUnit unit);
        void runAsync(Runnable task);
        Task runRepeating(Runnable task, long initialDelay, long period, TimeUnit unit);
        Task runRepeatingAsync(Runnable task, long initialDelay, long period, TimeUnit unit);
        void runNow(Runnable task);
        void runNowAsync(Runnable task);
    }
}
