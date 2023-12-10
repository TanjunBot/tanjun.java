package tanjun.api;

import tanjun.utilitys.DatabaseConnector;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Casino {
  /**
   * Changed the data of a Casino Player, so he has played a game. If the Casino Player does not exist in the Database,
   * the player gets created.
   * @param userid the ID of the user that played the game.
   * @param earnedMoney the Money the user earned. If the user lost money, this values has to be less than 0.
   * @return rather the Data was updated successfully or not.
   */
  public static boolean playGame(String userid, int earnedMoney) throws SQLException, IOException {
    if(!checkIfPlayerExists(userid)){
      addCasinoPlayer(userid);
    }
    Logger.addLog("Changing database Data because Player played in Casino", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "UPDATE casinoUser SET money = casinoUser.money + " + earnedMoney + " WHERE " +
            "id = " + userid;
    int result = statement.executeUpdate(query);
    boolean successfully = result == 0;
    if(successfully){
      Logger.addLog("Data changed Successfully.", "API");
    } else {
      Logger.addLog("Failed to change Data.", "API");
    }
    return successfully;
  }

  /**
   * Inserts a user in the Casino Player Table.
   * @param userid the ID of the User that should be inserted.
   * @return rather the User was successfully added or not.
   */
  public static Boolean addCasinoPlayer(String userid) throws IOException, SQLException {
    Logger.addLog("Adding User " + userid + " as a Casino Player.", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "INSERT INTO casinoUser (id) VALUES (" + userid + ")";
    int result = statement.executeUpdate(query);
    boolean successfully = result == 0;
    if(successfully){
      Logger.addLog("Successfully added CasinoPlayer.", "API");
    } else{
      Logger.addLog("CasinoPlayer was not added.", "API");
    }
    return  successfully;
  }

  /**
   * checks rather a User is registered in the Casino or not.
   * @param userid the ID of the User that you want to check.
   * @return rather the User is registered or not.
   */
  public static boolean checkIfPlayerExists(String userid) throws SQLException, IOException {
    Logger.addLog("Checking if User " + userid + " Exists as a Casino Player.", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT id FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    return result.next();
  }
}
