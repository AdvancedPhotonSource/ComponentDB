/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.test.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * Running this file will generate an XML file from dev-db('mysql') that could
 * be used for dbunit test prep.
 *
 * The resulting file will be placed in the root directory of the project.
 * Please note, the order of the backup must be adjusted to address order of
 * table dependencies. (Tables with no dependencies go first)
 */
public class SaveMySQLForDBUnit {

    static String MYSQL_CONN_STRING = "jdbc:mysql://localhost:3306/cdb";
    static String MYSQL_USERNAME = "cdb";
    static String MYSQL_PASSWORD = "cdb";

    // The file will be placed in root directory of the CdbWebPortalTest project.
    static String RESULT_FILE_NAME = "dbUnitBackup.xml";

    public static void main(String[] args) throws SQLException, IOException, DataSetException, DatabaseUnitException {

        Connection mysqlConnection = DriverManager.getConnection(MYSQL_CONN_STRING, MYSQL_USERNAME, MYSQL_PASSWORD);
        IDatabaseConnection dbunitConnection = new DatabaseConnection(mysqlConnection);

        IDataSet fullDataSet = dbunitConnection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream(RESULT_FILE_NAME));

    }

}
