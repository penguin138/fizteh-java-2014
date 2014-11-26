package ru.fizteh.fivt.students.torunova.storeable;


import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by nastya on 19.10.14.
 */
public class  Database implements TableProvider {
    public String dbName;
    private  Map<String, Table> tables = new HashMap<>();
    public Table currentTable;

    @Override
    public int hashCode() {
        return dbName.hashCode();
    }

    @Override
    public boolean equals(Object db1) {
        if (!(db1 instanceof Database)) {
            return false;
        }
        Database db = (Database) db1;
        return dbName.equals(db.dbName);
    }

    public Database(String name) throws IncorrectDbNameException,
                                        IOException,
                                        TableNotCreatedException,
                                        IncorrectFileException,
                                        IncorrectDbException {
        if (name == null) {
            throw new IncorrectDbNameException("Name of database not specified."
                    + "Please,specify it via -Dfizteh.db.dir");
        }
        File db = new File(name).getAbsoluteFile();
        if (!db.exists()) {
            db.mkdirs();
        } else if (!db.isDirectory()) {
            throw new IncorrectDbNameException("File with this name already exists.");
        }
        dbName = db.getAbsolutePath();
        File[]dbTables = db.listFiles();
        if (dbTables != null) {
            for (File table : dbTables) {
                if (table.getAbsoluteFile().isDirectory()) {
                    tables.put(table.getName(), new Table(table.getAbsolutePath()));
                } else {
                    throw new IncorrectDbException("Database contains illegal files.");
                }
            }
        }
    }

    @Override
    public Table getTable(String name) {
        checkTableName(name);
        return tables.get(name);
    }

    @Override
    public Table createTable(String tableName) {
        checkTableName(tableName);
        File table = new File(dbName, tableName);
        String newTableName = table.getAbsolutePath();
        if (!tables.containsKey(tableName)) {
            Table newTable;
            try {
                newTable = new Table(newTableName);
            } catch (TableNotCreatedException | IncorrectFileException | IOException e) {
                throw new RuntimeException(e);
            }
            tables.put(tableName, newTable);
            return newTable;
        }
        return null;
    }

    @Override
    public void removeTable(String name) {
        checkTableName(name);
        File f = new File(dbName, name);
        if (tables.containsKey(name)) {
            removeRecursive(f.getAbsolutePath());
            tables.remove(name);
            if (currentTable != null) {
                if (currentTable.tableName.equals(f.getAbsolutePath())) {
                    currentTable = null;
                }
            }
        } else {
            throw new IllegalStateException("does not exist");
        }
    }

    public boolean useTable(String name) {
        if (tables.containsKey(name)) {
            currentTable = tables.get(name);
            return true;
        }
        return false;
    }

    public Map<String, Integer> showTables() {
        Map<String, Integer> tablesWithSize = new HashMap<>();
        tables.forEach((name, table)->tablesWithSize.put(name, table.numberOfEntries));
        return tablesWithSize;
    }

    /**
     * removes file
     * @param file - filename.
     * @return true if file is regular and deleted,false otherwise.
     */
    private  boolean remove(final String file) {
        File file1 = new File(file).getAbsoluteFile();
        if (file1.isFile()) {
            if (!file1.delete()) {
                return false;
            }
        } else if (file1.isDirectory()) {
            return false;
        } else if (!file1.exists()) {
            return false;
        }
        return true;
    }
    /**
     * removes directory.
     *
     * @param dir - directory name.
     */
    private  boolean removeRecursive(final String dir) {
        File directory = new File(dir).getAbsoluteFile();
        if (directory.isDirectory()) {
            if (dir.equals(System.getProperty("user.dir"))) {
                System.setProperty("user.dir", directory.getParent());
            }
            File[] content = directory.listFiles();
            if (content != null) {
                for (File item : content) {
                    if (item.isDirectory()) {
                        if (!removeRecursive(item.getAbsolutePath())) {
                            return false;
                        }
                    } else {
                        if (!remove(item.getAbsolutePath())) {
                            return false;
                        }
                    }
                }
            }
            if (!directory.delete()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    private void checkTableName(String name) {
        if (name == null || Pattern.matches(".*" + Pattern.quote(File.separator) + ".*", name)
                || name.equals("..") || name.equals(".")) {
            throw new IllegalArgumentException("illegal table name");
        }
    }


}