package me.github.nettyexception.partysystem;

import de.dytanic.cloudnet.api.config.CloudConfigLoader;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.network.NetworkConnection;
import lombok.Getter;
import me.github.nettyexception.partysystem.commands.PartyCommand;
import me.github.nettyexception.partysystem.listener.PlayerLoginListener;
import me.github.nettyexception.partysystem.listener.ServerSwitchListener;
import me.github.nettyexception.partysystem.mysql.MySQLConnector;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * PartySystem copyright (©) 10 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

public class PartySystem extends Plugin {

    /**
     * @PartySystem Instance of Partysystem!
     */

    @Getter
    private static PartySystem partySystem;

    /**
     * @PartySystem Prefix of PartySystem!
     */

    public static final String PARTY_PREFIX = "§7» §aParty §8§l┃ §7";


    /**
     * @PartySystem Hostname of PartySystem!
     */

    public static final String SERVER_HOSTNAME = "Tropien.de";

    /**
     * @PartySystem ExecutorService of Partysystem!
     */

    @Getter
    private ExecutorService executorService;

    /**
     * @PartySystem CloudNetworkConnection of Partysystem!
     */

    @Getter
    private NetworkConnection networkConnection;

    /**
     * @PartySystem CloudNetworkConnection of Partysystem!
     */

    @Getter
    private CloudConfigLoader cloudConfigLoader;

    /**
     * @PartySystem MySQL-Connector to connecting database!
     */

    @Getter
    private MySQLConnector mySQLConnector;

    @Override
    public void onEnable()
    {
        partySystem = this;

        this.getLogger().info("PartySystem enabled with success!");
        this.getLogger().info("@" + this.getDescription().getAuthor() +
                " | Version: " + this.getDescription().getVersion());

        this.initializeCommands();
        this.initializeListener();
        this.initializeUtilities();

    }

    private void initializeListener()
    {
        final PluginManager pluginManager = this.getProxy().getPluginManager();

        pluginManager.registerListener(this, new PlayerLoginListener());
        pluginManager.registerListener(this, new ServerSwitchListener());
    }

    private void initializeCommands()
    {
        final PluginManager pluginManager = this.getProxy().getPluginManager();

        pluginManager.registerCommand(this, new PartyCommand());
    }

    private void initializeUtilities()
    {

        this.executorService = Executors.newSingleThreadExecutor();

        this.mySQLConnector = new MySQLConnector(
                "127.0.0.1",
                "party_database",
                "party_database",
                "xTwEjsl1EmWPX2kX",
                3360
        );

    }

    @Override
    public void onDisable()
    {
        partySystem = null;

        this.getLogger().info("PartySystem disabled with success!");
        this.getLogger().info("@" + this.getDescription().getAuthor() +
                " | Version: " + this.getDescription().getVersion());
    }

    @Override
    public Logger getLogger()
    {
        return super.getLogger();
    }
}
