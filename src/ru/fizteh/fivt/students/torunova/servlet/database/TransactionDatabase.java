package ru.fizteh.fivt.students.torunova.servlet.database;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.servlet.database.exceptions.TableNotCreatedException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by nastya on 25.12.14.
 */
public class TransactionDatabase {
    private DatabaseWrapper innerDb;
    class TransactionImpl implements Transaction{
        //Set<String> deletedKeys= new HashSet<>();
        Map<String, String> changedKeys = new HashMap<>();
        int transactionId;
        TableWrapper table;
        TransactionImpl(String tableName, int transactionId) {
            table = (TableWrapper) innerDb.getTable(tableName);
            this.transactionId = transactionId;
        }
        @Override
        public int getId() {
            return transactionId;
        }
        @Override
        public Storeable put(String key, Storeable value) {
            synchronized (table) {
                apply();
                Storeable result = table.put(key, value);
                table.rollback();
                changedKeys.put(key, innerDb.serialize(table, value));
               // deletedKeys.remove(key);
                return result;
            }
        }
        @Override
        public Storeable get(String key) {
            synchronized (table) {
                apply();
                Storeable result = table.get(key);
                table.rollback();
                return result;
            }
        }
        @Override
        public int size() {
            synchronized (table) {
                apply();
                int size = table.size();
                table.rollback();
                return size;
            }
        }
        @Override
        public int commit() throws IOException {
            synchronized (table) {
                apply();
                //deletedKeys.clear();
                changedKeys.clear();
                return table.commit();
            }
        }
        @Override
        public int rollback() {
            synchronized (table) {
                apply();
                //deletedKeys.clear();
                changedKeys.clear();
                return table.rollback();
            }
        }
        @Override
        public String serialize(Storeable value) {
            return innerDb.serialize(table, value);
        }

        @Override
        public Storeable deserialize(String value) {
            Storeable storeable = null;
            try {
                storeable = innerDb.deserialize(table, value);
            } catch (ParseException e) {
                //ignored.
            }
            return storeable;
        }
        private void apply()  {
            for (Map.Entry<String, String> entry : changedKeys.entrySet()) {
                try {
                    table.put(entry.getKey(), innerDb.deserialize(table, entry.getValue()));
                } catch (ParseException e) {
                    //ignored, because it is never thrown.
                }
            }
          /*  for (String key : deletedKeys) {
                table.remove(key);
            }*/
        }
    }
    /*it should be thread local,but there are some problems with it,
    * so for now it is ordinary reference variable...it will be fixed soon.
    * */

    private Map<Integer, TransactionImpl> transactions;
    private int numberOfTransactions;
    public TransactionDatabase(String dbName) throws
            IncorrectDbException, IncorrectDbNameException,
            IncorrectFileException, TableNotCreatedException, IOException {
        transactions = new HashMap<>();
        innerDb = new DatabaseWrapper(dbName);
        numberOfTransactions = 0;
    }
    public Transaction getTransactionById(int id) {
        return transactions.get(id);
    }
    public Transaction[] getTransactions() {
        ArrayList<Transaction> transactionList = new ArrayList<>();
        transactions.forEach((key, value) -> transactionList.add(value));
        return transactionList.toArray(new Transaction[0]);
    }
    public TransactionImpl beginTransaction(String tablename) {
        if (tablename == null) {
            throw new IllegalArgumentException("Tablename shouldn't be null.");
        }
        TransactionImpl transaction = new TransactionImpl(tablename, ++numberOfTransactions);
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }
}
