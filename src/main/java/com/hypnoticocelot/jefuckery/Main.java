package com.hypnoticocelot.jefuckery;

import com.sleepycat.je.*;
import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicationConfig;
import com.sleepycat.je.util.DbPrintLog;
import com.sleepycat.je.utilint.DbLsn;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    public static final int MAX_VALUE = 50000000;

    private EnvironmentConfig environmentConfig;
    private ReplicationConfig replicationConfig;
    private File dataDirectory;

    public Main() throws Exception {
        environmentConfig = new EnvironmentConfig()
                .setTransactional(true)
                .setAllowCreate(true)
                .setConfigParam(EnvironmentConfig.CLEANER_EXPUNGE, "true");
        replicationConfig = new ReplicationConfig()
                .setGroupName("FuckBdbAllTheWay")
                .setNodeName("MeYouAsshole")
                .setNodeHostPort("localhost:10101")
                .setHelperHosts("localhost:10101")
                .setConfigParam(ReplicationConfig.REP_STREAM_TIMEOUT, "1 min");
        dataDirectory = new File("./bdbdata");
        if (!dataDirectory.isDirectory() && !dataDirectory.mkdirs()) {
            throw new Exception("Can't create data directory");
        }
    }

    public static void main(String[] args) throws Exception {
        Main runner = new Main();

        runner.withDatabase((db, env) -> {
            Field envImpl = Environment.class.getDeclaredField("envImpl");
            envImpl.setAccessible(true);
            EnvironmentImpl envImplValue = (EnvironmentImpl) envImpl.get(env);
            Map<DatabaseId, String> dbNamesAndIds = envImplValue.getDbTree().getDbNamesAndIds();
            for (Map.Entry<DatabaseId, String> entry : dbNamesAndIds.entrySet()) {
                System.out.printf("%s => %s%n", entry.getValue(), entry.getKey());
            }
        });

//        runner.withDatabase((db, env) -> {
//            final AtomicBoolean running = new AtomicBoolean(true);
//
//            final Thread updates = new Thread(() -> runner.runUpdates(db, running, Long.MAX_VALUE));
//                updates.start();
//
//            final Thread deletes = new Thread(() -> runner.runDeletes(db, running, Long.MAX_VALUE));
//                deletes.start();
//
//            System.out.println("Press ENTER to stop POPULATING: ");
//            new BufferedReader(new InputStreamReader(System.in)).readLine();
//            running.set(false);
//
//            if (updates.isAlive()) {
//                updates.join();
//            }
//            if (deletes.isAlive()) {
//                deletes.join();
//            }
//        });

//        System.out.println("Populated..");
//
//        String filename = "dbprintlog2.xml";
//        runner.getBins(filename);
//
//        System.out.println("Done");

//        runner.withDatabase((db, env) -> {
//            final AtomicBoolean running = new AtomicBoolean(true);
//
//            final Thread clean = new Thread(env::cleanLog);
//            clean.start();
//
//            System.out.println("Press ENTER to stop CLEANING: ");
//            new BufferedReader(new InputStreamReader(System.in)).readLine();
//            running.set(false);
//
//            if (clean.isAlive()) {
//                clean.join();
//            }
//        });
    }

    private void getBins(String filename) throws IOException, JAXBException {
        PrintStream out = System.out;
        try (FileOutputStream out1 = new FileOutputStream(filename)) {
            System.setOut(new PrintStream(out1));
            DbPrintLog log = new DbPrintLog();
            log.dump(dataDirectory, null, null, DbLsn.NULL_LSN, DbLsn.NULL_LSN, true, false, false, false, true, false, null);
            System.out.flush();
        } finally {
            System.setOut(out);
        }

        System.out.println("Searching logs for BINs");
        DbPrintLogProcessor.processLog(filename);
    }

    private void withDatabase(DbOperation op) throws Exception {
        try (ReplicatedEnvironment env = new ReplicatedEnvironment(
                dataDirectory,
                replicationConfig,
                environmentConfig)) {
            final DatabaseConfig databaseConfig = new DatabaseConfig()
                    .setAllowCreate(true)
                    .setTransactional(true);

            try (Database db = env.openDatabase(null, "test", databaseConfig)) {
                op.withDb(db, env);
            }
        }
    }


    private void runUpdates(Database db, AtomicBoolean running, long total) {
        AtomicLong updates = new AtomicLong(total);
        while (running.get() && updates.decrementAndGet() > 0) {
            for (int counter = 0; running.get() && counter < MAX_VALUE && updates.decrementAndGet() > 0; counter++) {
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

    private void runDeletes(Database db, AtomicBoolean running, long total) {
        AtomicLong deletes = new AtomicLong(total);
        final Random random = new Random();

        while (running.get() && deletes.decrementAndGet() > 0) {
            final int keyNumber = random.nextInt(MAX_VALUE);
            try {
                db.delete(null, new DatabaseEntry(("key " + keyNumber).getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
                System.err.println("Error deleteing key " + keyNumber + ": " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    private static interface DbOperation {
        void withDb(Database db, ReplicatedEnvironment env) throws Exception;
    }
}
