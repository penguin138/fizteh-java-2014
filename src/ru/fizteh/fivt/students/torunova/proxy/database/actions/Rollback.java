package ru.fizteh.fivt.students.torunova.proxy.database.actions;

import ru.fizteh.fivt.students.torunova.proxy.database.TableHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 01.11.14.
 */
public class Rollback extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public Rollback(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args)
            throws IOException {
        String[] parameters = parseArguments(args);
        if (checkNumberOfArguments(0, parameters.length, writer)) {
            if (currentTable.get() == null) {
                writer.println("no table");
                return false;
            }
            writer.println(currentTable.get().rollback());
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "rollback";
    }
}
