package ru.fizteh.fivt.students.torunova.servlet.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.torunova.servlet.database.Transaction;
import ru.fizteh.fivt.students.torunova.servlet.database.TransactionDatabase;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.TableNotCreatedException;
import ru.fizteh.fivt.students.torunova.servlet.server.servlets.*;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by nastya on 25.12.14.
 */
public class HttpServer {
    private TransactionDatabase db;
    private Server server;
    private boolean running;
    private static final String LOCAL_HOST = "0.0.0.0";
    public HttpServer(String dbName) throws IncorrectDbException,
            IncorrectDbNameException, IncorrectFileException,
            TableNotCreatedException, IOException {
        db = new TransactionDatabase(dbName);
    }
    public void start(int bindPort) throws Exception {
        if (running) {
            throw new IllegalStateException("Server is already running");
        }
        server = new Server(new InetSocketAddress(LOCAL_HOST, bindPort));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.addServlet(new ServletHolder(new GetServlet(db)), "/get");
        context.addServlet(new ServletHolder(new PutServlet(db)), "/put");
        context.addServlet(new ServletHolder(new SizeServlet(db)), "/size");
        context.addServlet(new ServletHolder(new CommitServlet(db)), "/commit");
        context.addServlet(new ServletHolder(new RollbackServlet(db)), "/rollback");
        context.addServlet(new ServletHolder(new BeginServlet(db)), "/begin");

        context.setContextPath("/");
        server.setHandler(context);
        server.start();
    }
    public void stop() throws Exception {
        Transaction[] transactions = db.getTransactions();
        for (Transaction transaction : transactions) {
            transaction.rollback();
        }
        server.stop();
        running = false;
    }
}
