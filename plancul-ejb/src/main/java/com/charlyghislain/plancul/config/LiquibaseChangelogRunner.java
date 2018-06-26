package com.charlyghislain.plancul.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LiquibaseChangelogRunner {

    private static final String MAIN_CHANGELOG_FILE = "com/charlyghislain/plancul/liquibase/main.xml";

    @Resource(lookup = "jdbc/plancul-db")
    private DataSource dataSource;

    public void runChangeLogs() {
        try (Connection connection = dataSource.getConnection()) {
            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);

            ResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            ResourceAccessor fileSystemResourceAccessor = new FileSystemResourceAccessor();
            CompositeResourceAccessor resourceAccessor = new CompositeResourceAccessor(classLoaderResourceAccessor, fileSystemResourceAccessor);

            Liquibase liquibase = new Liquibase(MAIN_CHANGELOG_FILE, resourceAccessor, database);
            liquibase.update((String) null);
        } catch (SQLException e) {
            throw new RuntimeException("SQL error", e);
        } catch (DatabaseException e) {
            throw new RuntimeException("Database error", e);
        } catch (LiquibaseException e) {
            throw new RuntimeException("Liquibase error", e);
        }
    }
}
