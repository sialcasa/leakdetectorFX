package de.buildpath.leakscanner;

import java.util.function.Supplier;

public class GCActivity {
    public static void startGCActivity(Supplier<Boolean> shouldRun, long garbageCollectionIntervalMillis) {
        Thread thread = new Thread(() -> {
            // While the View retains in memory, we force the GarbageCollection
            // to work
            while (shouldRun.get()) {
                @SuppressWarnings("unused")
                String[] generateOutOfMemoryStr = new String[999999];
                System.gc();
                try {
                    Thread.sleep(garbageCollectionIntervalMillis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}