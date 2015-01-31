package com.hypnoticocelot.jefuckery;

import com.sleepycat.je.*;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicationConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static final int MAX_VALUE = 50000000;

    public static void main(String[] args) throws Exception {
        final EnvironmentConfig environmentConfig = new EnvironmentConfig()
                .setTransactional(true)
                .setAllowCreate(true)
                .setConfigParam(EnvironmentConfig.CLEANER_EXPUNGE, "false");

        final ReplicationConfig replicationConfig = new ReplicationConfig()
                .setGroupName("FuckBdbAllTheWay")
                .setNodeName("MeYouAsshole")
                .setNodeHostPort("localhost:10101")
                .setHelperHosts("localhost:10101")
                .setConfigParam(ReplicationConfig.REP_STREAM_TIMEOUT, "1 min");

        final File dataDirectory = new File("./bdbdata");
        if (!dataDirectory.isDirectory() && !dataDirectory.mkdirs()) {
            throw new Exception("Can't create data directory");
        }

        try (ReplicatedEnvironment env = new ReplicatedEnvironment(
                dataDirectory,
                replicationConfig,
                environmentConfig)) {
            final DatabaseConfig databaseConfig = new DatabaseConfig()
                    .setAllowCreate(true)
                    .setTransactional(true);

            try (Database db = env.openDatabase(null, "test", databaseConfig)) {
                final AtomicBoolean running = new AtomicBoolean(true);

                final Thread updates = new Thread(() -> runUpdates(db, running));
//                updates.start();

                final Thread deletes = new Thread(() -> runDeletes(db, running));
//                deletes.start();

                final Thread clean = new Thread(env::cleanLog);
                clean.start();

                System.out.println("Press ENTER to stop: ");
                new BufferedReader(new InputStreamReader(System.in)).readLine();
                running.set(false);

                if (updates.isAlive()) {
                    updates.join();
                }
                if (deletes.isAlive()) {
                    deletes.join();
                }
                if (clean.isAlive()) {
                    clean.join();
                }
            }
        }
    }

    private static void runUpdates(Database db, AtomicBoolean running) {
        while (running.get()) {
            for (int counter = 0; running.get() && counter < MAX_VALUE; counter++) {
                try {
                    db.put(null, new DatabaseEntry(("key " + counter).getBytes(StandardCharsets.UTF_8)),
                            new DatabaseEntry("value".getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    System.err.println("Error putting key " + counter + ": " + e.getMessage());
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private static void runDeletes(Database db, AtomicBoolean running) {
        final Random random = new Random();

        while (running.get()) {
            final int keyNumber = random.nextInt(MAX_VALUE);
            try {
                db.delete(null, new DatabaseEntry(("key " + keyNumber).getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
                System.err.println("Error deleteing key " + keyNumber + ": " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }
}
