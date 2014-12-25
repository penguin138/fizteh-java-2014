package ru.fizteh.fivt.students.torunova.servlet.server.servlets;

import ru.fizteh.fivt.students.torunova.servlet.database.TransactionDatabase;

import javax.servlet.http.HttpServlet;

/**
 * Created by nastya on 25.12.14.
 */
public class MainServlet extends HttpServlet {
    protected TransactionDatabase db;
    protected static final int OK = 200;
    protected static final int BAD_REQUEST = 400;
    protected static final int SERVER_ERROR = 500;
    protected static final int TID_NUMBER_DIGITS = 5;
    MainServlet(TransactionDatabase db) {
        this.db = db;
    }
    void checkTransactionId(int id) {
        if (String.valueOf(id).length() != TID_NUMBER_DIGITS) {
            throw new IllegalArgumentException();
        }
    }
}
