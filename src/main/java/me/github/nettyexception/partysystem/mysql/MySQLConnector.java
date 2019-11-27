package me.github.nettyexception.partysystem.mysql;

import lombok.Getter;
import me.github.nettyexception.partysystem.PartySystem;

import java.sql.*;

/**
 * PartySystem copyright (©) 11 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

public class MySQLConnector {

    /**
     * @PartySystem To connecting the PartyApplication to MySQL-Database
     */

    @Getter
    private final String hostname;

    @Getter
    private final String username;

    @Getter
    private final String database;

    @Getter
    private final String password;

    @Getter
    private final int port;

    @Getter
    private Connection connection;

    public MySQLConnector(String hostname, String username, String database, String password, int port) {
        this.hostname = hostname; this.username = username; this.database = database; this.password = password; this.port = port;
    }

    public synchronized void connect() {
        if (!this.isConnect()) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
                PartySystem.getPartySystem().getLogger().info("Connected successfully to MySQL-Database on " + this.hostname);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } else {
            return;
        }
    }

    public synchronized void disconnect() {
        if (this.isConnect()) {
            try {
                this.connection.close();
                PartySystem.getPartySystem().getLogger().info("Ending MySQL Connecting with " + this.hostname);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } else {
            return;
        }
    }

    public synchronized void update(String query) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    public synchronized ResultSet getResult(String query) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            preparedStatement = this.connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public synchronized void createTables() {
        if (this.isConnect()) {
            try {
                this.connection.createStatement().executeUpdate(
                        "CREATE TABLE IF NOT EXISTS PARTYS (PLAYERNAME VARCHAR(100), UNIQUEID VARCHAR(100), PARTYID VARCHAR(100), PARTYOWNER VARCHAR(100), LASTSERVER VARCHAR(100), DATE VARCHAR(100))");
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } else {
            return;
        }
    }

    private synchronized boolean isConnect() {
        return this.connection != null;
    }

}
