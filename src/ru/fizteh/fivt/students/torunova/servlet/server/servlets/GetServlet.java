package ru.fizteh.fivt.students.torunova.servlet.server.servlets;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.torunova.servlet.database.Transaction;
import ru.fizteh.fivt.students.torunova.servlet.database.TransactionDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nastya on 25.12.14.
 */
public class GetServlet extends MainServlet {
    public GetServlet(TransactionDatabase db) {
        super(db);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int transactionId = 0;
        try {
            transactionId = Integer.parseInt(request.getParameter("tid"));
            //checkTransactionId(transactionId);
        } catch (IllegalArgumentException e) {
            response.sendError(BAD_REQUEST, "Transaction id shoud be a number with 5 digits");
        }
        String key = request.getParameter("key");
        Transaction transaction = db.getTransactionById(transactionId);
        if (transaction == null) {
            response.sendError(BAD_REQUEST, "Transaction not found");
        }
        Storeable value = null;
        try {
            value = transaction.get(key);
        } catch (RuntimeException e) {
            response.sendError(SERVER_ERROR, e.getMessage());
        }
        if (value == null) {
            response.sendError(BAD_REQUEST, "Key not found");
        }
        response.setStatus(OK);
        response.getWriter().write(transaction.serialize(value));
    }
}
