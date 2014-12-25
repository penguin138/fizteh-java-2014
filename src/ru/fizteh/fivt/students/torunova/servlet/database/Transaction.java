package ru.fizteh.fivt.students.torunova.servlet.database;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;

/**
 * Created by nastya on 25.12.14.
 */
public interface Transaction {
    int getId();

    Storeable put(String key, Storeable value);

    Storeable get(String key);

    int size();

    int commit() throws IOException;

    int rollback();

    String serialize(Storeable value);

    Storeable deserialize(String value);
}
