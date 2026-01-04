package io.neskdev.api.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class AsyncUtils {

    // Utilise ForkJoinPool.commonPool() ou un ThreadPoolExecutor pour Java 17
    private static final ExecutorService DEFAULT_EXECUTOR = createDefaultExecutor();
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors() / 4)
    );
    private static final ConcurrentMap<Long, ScheduledFuture<?>> TASKS = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private static ExecutorService createDefaultExecutor() {
        return ForkJoinPool.commonPool();
    }

    /**
     * Exécute une tâche de manière asynchrone et retourne un CompletableFuture
     * @param supplier La fonction à exécuter
     * @return CompletableFuture contenant le résultat
     */
    public static <T> CompletableFuture<T> run(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, DEFAULT_EXECUTOR);
    }

    /**
     * Exécute une tâche de manière asynchrone sans valeur de retour
     * @param runnable La tâche à exécuter
     * @return CompletableFuture<Void>
     */
    public static CompletableFuture<Void> run(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, DEFAULT_EXECUTOR);
    }

    /**
     * Programme l'exécution d'une tâche après un délai
     * @param task La tâche à exécuter
     * @param delay Le délai avant exécution
     * @param unit L'unité de temps du délai
     * @return L'ID de la tâche programmée
     */
    public static long runLater(Runnable task, long delay, TimeUnit unit) {
        long id = ID_GENERATOR.incrementAndGet();
        ScheduledFuture<?> future = SCHEDULER.schedule(() -> {
            try {
                DEFAULT_EXECUTOR.execute(task);
            } finally {
                TASKS.remove(id);
            }
        }, delay, unit);
        TASKS.put(id, future);
        return id;
    }

    /**
     * Programme l'exécution répétée d'une tâche
     * @param task La tâche à exécuter
     * @param initialDelay Le délai initial avant la première exécution
     * @param period La période entre les exécutions
     * @param unit L'unité de temps
     * @return L'ID de la tâche programmée
     */
    public static long runRepeating(Runnable task, long initialDelay, long period, TimeUnit unit) {
        long id = ID_GENERATOR.incrementAndGet();
        ScheduledFuture<?> future = SCHEDULER.scheduleAtFixedRate(() -> {
            DEFAULT_EXECUTOR.execute(task);
        }, initialDelay, period, unit);
        TASKS.put(id, future);
        return id;
    }

    /**
     * Arrête une tâche programmée
     * @param id L'ID de la tâche à arrêter
     * @return true si la tâche a été trouvée et annulée, false sinon
     */
    public static boolean stopTask(long id) {
        ScheduledFuture<?> future = TASKS.remove(id);
        if (future != null) {
            return future.cancel(false);
        }
        return false;
    }

    /**
     * Vérifie si une tâche est encore active
     * @param id L'ID de la tâche
     * @return true si la tâche existe et n'est pas terminée
     */
    public static boolean isTaskActive(long id) {
        ScheduledFuture<?> future = TASKS.get(id);
        return future != null && !future.isDone();
    }

    /**
     * Retourne le nombre de tâches programmées actives
     * @return Le nombre de tâches actives
     */
    public static int getActiveTaskCount() {
        return TASKS.size();
    }

    /**
     * Arrête tous les executors et nettoie les ressources
     */
    public static void shutdown() {
        TASKS.values().forEach(future -> future.cancel(false));
        TASKS.clear();

        SCHEDULER.shutdown();
        try {
            if (!SCHEDULER.awaitTermination(5, TimeUnit.SECONDS)) {
                SCHEDULER.shutdownNow();
            }
        } catch (InterruptedException e) {
            SCHEDULER.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Arrêt gracieux avec timeout personnalisé
     * @param timeout Le timeout pour l'arrêt
     * @param unit L'unité de temps du timeout
     * @return true si l'arrêt s'est fait dans les temps, false sinon
     */
    public static boolean shutdown(long timeout, TimeUnit unit) {
        TASKS.values().forEach(future -> future.cancel(false));
        TASKS.clear();

        SCHEDULER.shutdown();
        try {
            return SCHEDULER.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            SCHEDULER.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
    }
}