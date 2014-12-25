package ru.fizteh.fivt.students.torunova.servlet.database.actions;

import ru.fizteh.fivt.students.torunova.servlet.server.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 25.12.14.
 */
public class Stop extends Action{
    private HttpServer server;
    private PrintWriter writer;
    public Stop(HttpServer server, OutputStream os) {
        this.server = server;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            writer.println(e.toString());
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "stophttp";
    }
}
