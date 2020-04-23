package edu.wpi.N.database;

import java.sql.*;

public class setupDB {
  public static void main(String[] args) throws SQLException, ClassNotFoundException, DBException {
    String query;
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    String URL;
    URL = "jdbc:derby:MapDB;create=true";
    Connection con = DriverManager.getConnection(URL);
    Statement statement = con.createStatement();
    try {
      query =
          "CREATE TABLE nodes ("
              + "nodeID CHAR(10) NOT NULL PRIMARY KEY, "
              + "xcoord INT NOT NULL, "
              + "ycoord INT NOT NULL, "
              + "floor INT NOT NULL, "
              + "building VARCHAR(255) NOT NULL, "
              + "nodeType CHAR(4) NOT NULL CONSTRAINT TYPE_CK CHECK (nodeType IN ('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV')), "
              + "longName VARCHAR(255) NOT NULL, "
              + "shortName VARCHAR(255) NOT NULL, "
              + "teamAssigned CHAR(1) NOT NULL"
              + ")";
      statement.execute(query);
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) throw e;
    }

    try {
      query =
          "CREATE TABLE edges ("
              + "edgeID CHAR(21) NOT NULL PRIMARY KEY, "
              + "node1 CHAR(10) NOT NULL, "
              + "node2 CHAR(10) NOT NULL, "
              + "FOREIGN KEY (node1) REFERENCES nodes(nodeID) ON DELETE CASCADE,"
              + "FOREIGN KEY (node2) REFERENCES nodes(nodeID) ON DELETE CASCADE"
              + ")";
      statement.execute(query);
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) throw e;
    }

    try {
      query =
          "CREATE TABLE doctors ("
              + "doctorID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
              + "name VARCHAR(255) NOT NULL, "
              + "field VARCHAR(255) NOT NULL)";

      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE location ("
              + "doctor INT NOT NULL REFERENCES doctors(doctorID) ON DELETE CASCADE, "
              + "nodeID char(10) NOT NULL REFERENCES nodes(nodeID) ON DELETE CASCADE,"
              + "priority INT NOT NULL GENERATED ALWAYS AS IDENTITY,"
              + "PRIMARY KEY (doctor, nodeID))";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initDoctor", e);
      }
    }
    try {
      query =
          "CREATE TABLE service ("
              + "serviceType VARCHAR(255) NOT NULL PRIMARY KEY,"
              + "timeStart CHAR(5),"
              + "timeEnd CHAR(5),"
              + "description VARCHAR(255))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "INSERT INTO service VALUES ('Translator', '00:00', '00:00', 'Make a request for our translation services!')";
      state = con.prepareStatement(query);
      state.execute();

      query =
          "INSERT INTO service VALUES ('Laundry', '00:00', '00:00', 'Make a request for laundry services!')";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee", e);
      }
    }
    try {
      query =
          "CREATE TABLE employees ("
              + "employeeID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
              + "name VARCHAR(255) NOT NULL,"
              + "serviceType VARCHAR(255) NOT NULL,"
              + "FOREIGN KEY (serviceType) REFERENCES service (serviceType))";

      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee", e);
      }
    }
    try {
      query =
          "CREATE TABLE translator ("
              + "t_employeeID INT NOT NULL PRIMARY KEY,"
              + "FOREIGN KEY (t_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE)";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE language ("
              + "t_employeeID INT NOT NULL, "
              + "language VARCHAR(255) NOT NULL, "
              + "CONSTRAINT LANG_PK PRIMARY KEY (t_employeeID, language),"
              + "FOREIGN KEY (t_employeeID) REFERENCES translator (t_employeeID) ON DELETE CASCADE)";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee creating translator table", e);
      }
    }
    try {
      query =
          "CREATE TABLE laundry("
              + "l_employeeID INT NOT NULL References employees(employeeID) ON DELETE CASCADE,"
              + "PRIMARY KEY(l_employeeID))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: intiEmployee creating laundry table", e);
      }
    }
    try {
      query =
          "CREATE TABLE request("
              + "requestID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
              + "timeRequested TIMESTAMP NOT NULL,"
              + "timeCompleted TIMESTAMP,"
              + "notes VARCHAR(255),"
              + "assigned_eID INT REFERENCES employees(employeeID) ON DELETE SET NULL,"
              + "serviceType VARCHAR(255) NOT NULL REFERENCES service(serviceType),"
              + "nodeID CHAR(10) REFERENCES nodes(nodeID) ON DELETE SET NULL,"
              + "status CHAR(4) NOT NULL CONSTRAINT STAT_CK CHECK (status IN ('OPEN', 'DENY', 'DONE')))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE lrequest("
              + "requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE)";
      state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE trequest("
              + "requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,"
              + "language VARCHAR(255) NOT NULL)";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: intiEmployee creating Request table", e);
      }
    }
  }
}