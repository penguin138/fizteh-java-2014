package ru.fizteh.fivt.students.torunova.servlet.database.actions;

import ru.fizteh.fivt.students.torunova.servlet.server.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 25.12.14.
 */
public class Start extends Action {
    private HttpServer server;
    private PrintWriter writer;
    public Start(HttpServer server, OutputStream os) {
        this.server = server;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length, writer)) {
            return false;
        }
        int port;
        try {
            port = Integer.parseInt(parameters[0]);
        } catch (NumberFormatException e) {
            writer.println("port should be a number");
            return false;
        }
        try {
            server.start(port);
        } catch (Exception e) {
            writer.println(e.toString());
        }
        return true;
    }

    @Override
    public String getName() {
        return "starthttp";
    }
}
