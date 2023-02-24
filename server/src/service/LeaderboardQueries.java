package service;

import shared.transferable.Chore;
import shared.transferable.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LeaderboardQueries {

    private QueryExecutor queryExecutor;

    public LeaderboardQueries(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * gets the scores for a given group
     * @param groupID group to make a leaderboard from
     * @return hashmap with users and their scores
     */
    public HashMap<User, Integer> getLeaderboard (int groupID) {
        HashMap<User, Integer> leaderboard = new HashMap<>();
        String query = "SELECT * FROM [Member] WHERE group_id = " + groupID + ";";
        System.out.println(query);
        try {
            ResultSet resultSet = queryExecutor.executeReadQuery(query);
            while (resultSet.next()) {
                User user = new User(resultSet.getString("user_name"));
                int score = resultSet.getInt("user_score");
                leaderboard.put(user, score);
                }
            } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }

    /**
     * resets all the scores on a group's leaderboard to zero
     * @param groupID group to be reset
     * @return true if success, false otherwise
     */
    public boolean resetLeaderboard(int groupID) {
        boolean boardReset = false;
        String query = "UPDATE [Member] SET user_score = 0 WHERE group_id = " + groupID;
        try {
            queryExecutor.executeUpdateQuery(query);
            boardReset = true;
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return boardReset;
    }
}
