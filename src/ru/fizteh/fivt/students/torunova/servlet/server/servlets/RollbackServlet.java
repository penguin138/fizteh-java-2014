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
public class RollbackServlet extends MainServlet {
    public RollbackServlet(TransactionDatabase db) {
        super(db);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        int transactionId = 0;
        try {
            transactionId = Integer.parseInt(request.getParameter("tid"));
            //checkTransactionId(transactionId);
        } catch (IllegalArgumentException e) {
            response.sendError(BAD_REQUEST, "Transaction id shoud be a number with 5 digits");
        }
        Transaction transaction = db.getTransactionById(transactionId);
        if (transaction == null) {
            response.sendError(BAD_REQUEST, "Transaction not found");
        }
        int numberOfRevertedChanges = 0;

        numberOfRevertedChanges = transaction.rollback();

        response.setStatus(OK);
        response.getWriter().write("diff = " + numberOfRevertedChanges);
    }
}
