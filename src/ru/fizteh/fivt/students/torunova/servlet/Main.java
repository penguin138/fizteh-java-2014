package ru.fizteh.fivt.students.torunova.servlet;

import ru.fizteh.fivt.students.torunova.servlet.database.actions.Action;
import ru.fizteh.fivt.students.torunova.servlet.database.actions.Start;
import ru.fizteh.fivt.students.torunova.servlet.database.actions.Stop;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.TableNotCreatedException;
import ru.fizteh.fivt.students.torunova.servlet.interpreter.Shell;
import ru.fizteh.fivt.students.torunova.servlet.server.HttpServer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nastya on 22.10.14.
 */

public class Main {
    private static final String DATABASE_DIRECTORY = "fizteh.db.dir";

    public static void main(String[] args) {
        if (System.getProperty(DATABASE_DIRECTORY) == null) {
            System.err.println("Name of database not specified. Please, specify it via -D" + DATABASE_DIRECTORY);
            System.exit(1);
        }
        HttpServer server = null;
        try {
            server = new HttpServer(System.getProperty(DATABASE_DIRECTORY));
        } catch (IncorrectDbException | IncorrectDbNameException
                | IncorrectFileException | TableNotCreatedException | IOException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
        Set<Action> actions = new HashSet<>();
        actions.add(new Start(server, System.out));
        actions.add(new Stop(server, System.out));
        Shell shell = new Shell(actions, System.in, System.out, "stophttp", true);

        if (!shell.run()) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}


