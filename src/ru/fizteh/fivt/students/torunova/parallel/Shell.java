package ru.fizteh.fivt.students.torunova.parallel;

/**
 * Created by nastya on 21.10.14.
 */

import ru.fizteh.fivt.students.torunova.parallel.actions.Action;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
public class Shell {
    private Map<String, Action> commands;
    private Scanner scanner;
    private Database db;
    private CurrentTable currentTable;
    private boolean interactive;

    public Shell(Set<Action> cmds, InputStream is, String dbfile, boolean isInteractive) {
        commands = new HashMap<>();
        for (Action action : cmds) {
            commands.put(action.getName(), action);
        }
        scanner = new Scanner(is);
        try {
            db = new Database(dbfile);
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
            abort();
        }
        interactive = isInteractive;
        currentTable = new CurrentTable(new DatabaseWrapper(db));
    }

    public void run() {
        String nextCommand;
        String[] functions;
        while (true) {
            if (interactive) {
                System.out.print("$ ");
            }
            try {
                nextCommand = scanner.nextLine();
            } catch (NoSuchElementException e) {
                if (currentTable.get() != null) {
                    currentTable.get().commit();
                }
                return;
            }
            functions = nextCommand.split(";");
            for (String function : functions) {
                function = function.trim();
                String[] args = parseArguments(function);
                String name = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                if (commands.containsKey(name)) {
                    boolean res = false;
                    try {
                        if (name.equals("create")) {
                            args = argsForCreate(function);
                        }
                         res = commands.get(name).run(args, currentTable);
                    } catch (Exception e) {
                        System.err.println("Caught " + e.getClass().getSimpleName() +  ": " + e.getMessage());
                        if (!interactive || name.equals("exit")) {
                            abort();
                        }
                    }
                    if (!interactive && !res) {
                        if (currentTable.get() != null) {
                            currentTable.get().commit();
                        }
                        System.exit(1);
                    } else if (!res) {
                        break;
                    }
                } else if (!Pattern.matches("\\s*", name)) {
                    System.err.println("Command not found.");
                    if (!interactive) {
                        if (currentTable.get() != null) {
                            currentTable.get().commit();
                        }
                            System.exit(1);

                    } else {
                        break;
                    }
                }
            }
        }
    }

    private String[] argsForCreate(String args) {
        args = args.replaceAll("\\s+", " ");
        String typesWithoutBrakets;
        String tableName;
        try {
            String newArgs = args.substring(args.indexOf(' ') + 1);
            tableName = newArgs.substring(0, newArgs.indexOf(' '));
            String argsWithoutTableName = newArgs.substring(newArgs.indexOf(' '));
            typesWithoutBrakets = argsWithoutTableName.substring(argsWithoutTableName.indexOf('(') + 1,
                                                                 argsWithoutTableName.lastIndexOf(')'));
        } catch (StringIndexOutOfBoundsException e) {
            throw new RuntimeException("create: wrong command format");
        }
        StringBuilder builder = new StringBuilder();
        builder = builder.append(tableName).append(' ').append(typesWithoutBrakets);
        return builder.toString().trim().split("\\s+");
    }

    private String[] parseArguments(String arg) {
        return arg.split("\\s+");
    }
    private void abort() {
        System.err.println("Aborting...");
        System.exit(1);
    }
}

