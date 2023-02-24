package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for handling queries to database
 */
public class QueryExecutor {

    private DatabaseConnection connection;
    UserQueries userQueries;
    GroupQueries groupQueries;
    ChoreRewardQueries choreRewardQueries;
    LeaderboardQueries leaderboardQueries;

    public QueryExecutor(DatabaseConnection connection) {
        this.connection = connection;
        choreRewardQueries = new ChoreRewardQueries(this);
        leaderboardQueries = new LeaderboardQueries(this);

        userQueries = new UserQueries(this);
        groupQueries = new GroupQueries(this);
        userQueries.setGroupQueries(groupQueries);
    }

    public UserQueries getUserQueries() {
        return userQueries;
    }

    public GroupQueries getGroupQueries() {
        return groupQueries;
    }

    public ChoreRewardQueries getChoreRewardQueries() {
        return choreRewardQueries;
    }

    public LeaderboardQueries getLeaderboardQueries() {
        return leaderboardQueries;
    }

    //execute queries that modify the database
    public void executeUpdateQuery(String query) throws SQLException {
        SQLException exception;
        boolean isSuccess = false;
        int retries = 0;
        do {
            try {
                this.connection.getConnection().createStatement().executeUpdate(query);
                isSuccess = true;
                return;
            }
            catch (SQLException sqlException) {
                exception = sqlException;
                connection.closeConnection();
                retries++;
            }
        } while (!isSuccess && retries < 3);
        //retry connection 3 times, if no success, throw the last exception
        throw exception;
    }
    //execute queries that read from the database
    public ResultSet executeReadQuery(String query) throws SQLException {
        SQLException exception;
        int retries = 0;
        do {
            try {
                ResultSet resultSet = this.connection.getConnection().createStatement().executeQuery(query);
                return resultSet;
            }
            catch (SQLException sqlException) {
                exception = sqlException;
                connection.closeConnection();
                retries++;
            }
        } while (retries < 3);
        //retry connection 3 times, if no success, throw the last exception
        throw exception;
    }
    //can be used when issuing updates that must succeed/fail
    public Statement beginTransaction() throws SQLException {
        SQLException exception;
        int retries = 0;
        do {
            try {
                connection.getConnection().setAutoCommit(false);
                return connection.getConnection().createStatement();
            }
            catch (SQLException sqlException) {
                exception = sqlException;
                System.out.println(sqlException);
                connection.closeConnection();
                retries++;
            }
        }
        while (retries < 3);
        //retry connection 3 times, if no success, throw the last exception
        throw exception;
    }

    //commit tx once connection established
    public void endTransaction() throws SQLException {
        connection.getConnection().commit();
        connection.getConnection().setAutoCommit(true);
    }
    //any problems and the tx gets rolled back
    public void rollbackTransaction() throws SQLException {
        connection.getConnection().rollback();
    }
}
