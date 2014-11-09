package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Exit extends Action {


    @Override
    public boolean run(String[] args, Database db)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (db.currentTable != null) {
            db.currentTable.commit();
        }
        System.exit(0);
        return true;
    }
    @Override
    public String getName() {
        return "exit";
    }
}