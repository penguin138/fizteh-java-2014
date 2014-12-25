package ru.fizteh.fivt.students.torunova.servlet.server.servlets;

import ru.fizteh.fivt.students.torunova.servlet.database.Transaction;
import ru.fizteh.fivt.students.torunova.servlet.database.TransactionDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nastya on 25.12.14.
 */
public class BeginServlet extends MainServlet {
    public BeginServlet(TransactionDatabase db) {
        super(db);
    }
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String tableName = request.getParameter("table");
        Transaction transaction = null;
        try {
            transaction = db.beginTransaction(tableName);
        } catch (IllegalArgumentException e) {
            response.sendError(BAD_REQUEST, e.getMessage());
        }
        response.setStatus(OK);
        response.getWriter().write("tid = " + transaction.getId());
    }
}
