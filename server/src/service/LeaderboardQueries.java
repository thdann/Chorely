package service;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Reward;
import shared.transferable.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public boolean updateLeaderboard(Group updatedGroup) {
        boolean success = false;
        try {
            HashMap<User, Integer> oldLeaderboard = getLeaderboard(updatedGroup.getGroupID());
            for (Map.Entry entry : updatedGroup.getLeaderBoard().entrySet()) {
                User user = (User) entry.getKey();
                int score = (Integer) entry.getValue();
                if (score != oldLeaderboard.get(user)) {
                    updateScore(updatedGroup.getGroupID(), user, score);
                }
            }
            success = true;
        }catch (Exception e) {
            success = false;
        }
        return success;
    }

    private boolean updateScore(int groupID, User user, int score) {
        boolean scoreUpdated = false;
        String query = "UPDATE [Member] SET user_score = " + score + " WHERE user_name = '" + user.getUsername() + "' AND group_id = " + groupID;
        try {
            queryExecutor.executeUpdateQuery(query);
            scoreUpdated = true;
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return scoreUpdated;
    }
}
