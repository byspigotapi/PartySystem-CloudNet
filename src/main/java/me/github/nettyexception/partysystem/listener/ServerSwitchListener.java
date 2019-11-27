package me.github.nettyexception.partysystem.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import me.github.nettyexception.partysystem.PartyService;
import me.github.nettyexception.partysystem.PartySystem;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;

/**
 * PartySystem copyright (©) 10 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

public class ServerSwitchListener implements Listener {

    @EventHandler(priority = 127)
    public void handleServerSwitch(final ServerConnectedEvent serverSwitchEvent)
    {
        final CloudPlayer cloudNetworkPlayer = CloudAPI.getInstance().getOnlinePlayer(serverSwitchEvent.getPlayer().getUniqueId());

        if (PartyService.checkPartyPlayer(cloudNetworkPlayer)) {
            final PartyService partyService = PartyService.getParty(cloudNetworkPlayer);

            if (partyService.checkPartyOwner(cloudNetworkPlayer))
            {
                final Iterator partyCloudPlayers = CloudAPI.getInstance().getOnlinePlayers().iterator();

                while (partyCloudPlayers.hasNext())
                {
                    final CloudPlayer partyPlayers = (CloudPlayer)partyCloudPlayers.next();

                    if (partyService.getPartyMembers().contains(partyPlayers))
                    {
                        PlayerExecutorBridge.INSTANCE.sendPlayer(partyPlayers, serverSwitchEvent.getServer().getInfo().getName());

                        if (CloudAPI.getInstance().getServiceId().getServerId().startsWith("Lobby-"))
                        {
                            PlayerExecutorBridge.INSTANCE.sendMessage(partyPlayers,
                                    PartySystem.PARTY_PREFIX + "§7Die Party wurde auf §7eine §eLobby §7verlegt§8!");
                        } else
                        {
                            PlayerExecutorBridge.INSTANCE.sendMessage(partyPlayers,
                                    PartySystem.PARTY_PREFIX + "§7Die Party wurde auf §e" + serverSwitchEvent.getServer().getInfo().getName() + " §7verlegt§8!");
                        }

                    }

                }
            } else
            {
                PlayerExecutorBridge.INSTANCE.sendMessage(cloudNetworkPlayer,
                        PartySystem.PARTY_PREFIX + "§cDu kannst keinem Server betreten während du dich in einer Party befindest§8!");
                return;
            }
        }
    }

}
