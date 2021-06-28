package com.goyanov.fear.utils;

import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.main.FearFeeling;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;

public class SQLManager
{
    private static Connection mainConnection;

    public static void openConnectionWithDB()
    {
        try {
            FearFeeling.inst().getDataFolder().mkdir();
            mainConnection = DriverManager.getConnection("jdbc:sqlite:" + FearFeeling.inst().getDataFolder() + File.separator + "players.db");
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }

    public static void closeConnectionWithDB()
    {
        try {
            mainConnection.close();
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }

    public static void createFearTable()
    {
        Statement statement;
        try
        {
            statement = mainConnection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS fears(player VARCHAR(16) PRIMARY KEY, current DOUBLE, final DOUBLE, disabled BOOL, blackworld BOOL, showstyle VARCHAR(16))";
            statement.executeUpdate(query);
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }

    public static void savePlayerToDB(Player p)
    {
        ScaredPlayer sp = Fear.getScaredPlayer(p);

        String playerName = p.getName().toLowerCase();

        try
        {
            String query = "UPDATE fears SET current = ?, final = ?, disabled = ?, blackworld = ?, showstyle = ? WHERE player = ?";

            PreparedStatement statement = mainConnection.prepareStatement(query);
            statement.setDouble(1, sp.getCurrentFear());
            statement.setDouble(2, sp.getFinalFear());
            statement.setBoolean(3, sp.getFearBlocked());
            statement.setBoolean(4, sp.getFearDisabledByWorldBlackList());
            statement.setString(5, sp.getFearShowStyle().name());
            statement.setString(6, playerName);
            statement.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static Object[] loadPlayerFromDB(Player p)
    {
        Object[] datas = new Object[5];

        String playerName = p.getName().toLowerCase();

        try
        {
            Statement statement = mainConnection.createStatement();
            String query = "SELECT * FROM fears WHERE player = '" + playerName + "'";
            ResultSet set = statement.executeQuery(query);

            datas[0] = set.getDouble("current");
            datas[1] = set.getDouble("final");
            datas[2] = set.getBoolean("disabled");
            datas[3] = set.getBoolean("blackworld");
            datas[4] = FearShowStyle.valueOf(set.getString("showstyle"));
        } catch (SQLException throwables)
        {
            try
            {
                String query = "INSERT INTO fears(player,current,final,disabled,blackworld,showstyle) VALUES(?,?,?,?,?,?)";
                PreparedStatement statement = mainConnection.prepareStatement(query);
                statement.setString(1, playerName);
                statement.setDouble(2, 0.0);
                statement.setDouble(3, 0.0);
                boolean disabled = false;
                if (PluginSettings.getWorldsBlacklist().contains(p.getWorld())) disabled = true;
                statement.setBoolean(4, disabled);
                statement.setBoolean(5, disabled);
                statement.setString(6, PluginSettings.FearSettings.getDefaultShowStyle().name());
                statement.executeUpdate();

                datas[0] = 0.0;
                datas[1] = 0.0;
                datas[2] = disabled;
                datas[3] = disabled;
                datas[4] = PluginSettings.FearSettings.getDefaultShowStyle();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        return datas;
    }
}
