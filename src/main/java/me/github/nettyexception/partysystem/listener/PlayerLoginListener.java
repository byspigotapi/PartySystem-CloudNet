package me.github.nettyexception.partysystem.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import me.github.nettyexception.partysystem.PartyService;
import me.github.nettyexception.partysystem.PartySystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * PartySystem copyright (©) 10 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

public class PlayerLoginListener implements Listener {

    @EventHandler(priority = 127)
    public void handlePlayerLogin(final LoginEvent loginEvent)
    {

        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(loginEvent.getConnection().getUniqueId());

        CloudProxy.getInstance().getCachedPlayer(
                cloudPlayer.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).setPrefix(" §8[§cnull8]");

        ProxyServer.getInstance().getScheduler().schedule(PartySystem.getPartySystem(), new Runnable() {
            public void run() {
                final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(loginEvent.getConnection().getUniqueId());
                //PartyService.PARTY_INVITES.put(cloudPlayer, new ArrayList<CloudPlayer>());
            }
        },1L, TimeUnit.SECONDS);
    }

    @EventHandler(priority = 127)
    public void handlePlayerDisconnect(PlayerDisconnectEvent playerDisconnectEvent)
    {
        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(playerDisconnectEvent.getPlayer().getUniqueId());
        cloudPlayer.getPermissionEntity().setSuffix(null);

        PartyService partyService = PartyService.getParty(cloudPlayer);

        if (partyService.checkPartyOwner(cloudPlayer)) {

            final Iterator partyCloudPlayers = CloudAPI.getInstance().getOnlinePlayers().iterator();

            while (partyCloudPlayers.hasNext())
            {
                final CloudPlayer partyPlayers = (CloudPlayer) partyCloudPlayers.next();

                PlayerExecutorBridge.INSTANCE.sendMessage(partyPlayers,
                        PartySystem.PARTY_PREFIX + "§6" + cloudPlayer.getName() + " §chat die Party verlassen§8!");
                PlayerExecutorBridge.INSTANCE.sendMessage(partyPlayers,
                        PartySystem.PARTY_PREFIX + "§cDie Party wurde ausgelöst§8!");

            }

            partyService.deleteParty();

        } else
        {

            partyService.removePartyPlayer(cloudPlayer);

            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                    PartySystem.PARTY_PREFIX + "§cDu hast die §eParty §cverlassen§8!");
            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                    PartySystem.PARTY_PREFIX + "§6" + cloudPlayer.getName() + " §chat die Party verlassen§8!");


        }
    }

}
