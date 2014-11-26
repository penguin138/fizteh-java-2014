package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class UseTable extends Action{
    @Override
    public boolean run(String[] args, DatabaseWrapper db) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.getCurrentTable() != null) {
            int numberOfUnsavedChanges = db.getCurrentTable().getNumberOfUncommittedChanges();
            if (numberOfUnsavedChanges != 0) {
                System.err.println(numberOfUnsavedChanges + " unsaved changes");
                return false;
            }
        }
        if (db.useTable(args[0])) {
            System.out.println("using " + args[0]);
            return true;
        } else {
            System.out.println(args[0] + " does not exist");
            return false;
        }
    }

    @Override
    public String getName() {
        return "use";
    }
}