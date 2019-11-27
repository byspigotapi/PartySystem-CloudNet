package me.github.nettyexception.partysystem.commands;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import me.github.nettyexception.partysystem.PartyService;
import me.github.nettyexception.partysystem.PartySystem;
import me.github.nettyexception.partysystem.utilities.InquiryMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * PartySystem copyright (©) 11 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

@SuppressWarnings("AccessStaticViaInstance")
public class PartyCommand extends Command {

    public PartyCommand() {
        super("party", null, "partie");
    }

    @Override
    public void execute(CommandSender commandSender, String[] arguments) {
        if (commandSender instanceof ProxiedPlayer)
        {
            ArrayList partyInvites;

            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
            CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(proxiedPlayer.getUniqueId());

            if (arguments.length == 2)
            {


                if (arguments[0].equalsIgnoreCase("invite"))
                {


                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(arguments[1]);
                    CloudPlayer targetCloudPlayer = CloudAPI.getInstance().getOnlinePlayer(targetPlayer.getUniqueId());

                    if (targetPlayer != null)
                    {

                        if (targetCloudPlayer == cloudPlayer)
                        {
                            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDu kannst dich nicht selbst in eine Party einladen§8!");
                            return;
                        }


                        if (PartyService.checkPartyPlayer(cloudPlayer))
                        {
                            PartyService partyService = PartyService.getParty(targetCloudPlayer);
                            if (partyService.getPartyMembers().contains(targetCloudPlayer))
                            {
                                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDieser Spieler befindet sich bereits in der Party§8!");
                                return;
                            }

                            if (partyService.getPartyLeader().equals(cloudPlayer))
                            {
                                if (!partyService.PARTY_INVITES.get(targetCloudPlayer).contains(cloudPlayer))
                                {
                                    partyService.invitePlayer(cloudPlayer, targetCloudPlayer);
                                    InquiryMessage.sendInquiryMessage(targetPlayer, cloudPlayer.getName());

                                    PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§7Du hast §e" + targetCloudPlayer.getName() + " §7in die Party eingeladen§8!");
                                } else
                                {
                                    PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDu hast §e" + targetCloudPlayer.getName() + " §cbereits in die Party eingeladen§8!");
                                    return;
                                }
                            } else
                            {
                                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDu kannst Spieler nur als Partyleitung einladen§8!");
                                return;
                            }
                        } else
                        {

                            PartyService.createParty(cloudPlayer);
                            PartyService partyService = PartyService.getParty(cloudPlayer);

                            try
                            {

                                if (!partyService.PARTY_INVITES.get(targetCloudPlayer).contains(cloudPlayer))
                                {
                                    partyService.invitePlayer(cloudPlayer, targetCloudPlayer);
                                    InquiryMessage.sendInquiryMessage(targetPlayer, cloudPlayer.getName());

                                    PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§7Du hast §e" + targetCloudPlayer.getName() + " §7in die Party eingeladen§8!");
                                } else
                                {
                                    PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDu hast §e" + targetCloudPlayer.getName() + " §cbereits in die Party eingeladen§8!");
                                    return;
                                }

                            } catch (NullPointerException nullPotinerException)
                            {

                                nullPotinerException.printStackTrace();
                                PartySystem.getPartySystem().getLogger().info(nullPotinerException.getMessage());

                            }
                        }

                    } else
                    {
                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDieser Spieler befindet sich nicht auf dem Netzwerk§8!");
                        return;
                    }

                } else if (arguments[0].equalsIgnoreCase("accept"))
                {

                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(arguments[1]);
                    CloudPlayer targetCloudPlayer = CloudAPI.getInstance().getOnlinePlayer(targetPlayer.getUniqueId());

                    if (targetPlayer != null)
                    {
                        PartyService partyService = PartyService.getParty(targetCloudPlayer);
                        if (partyService != null && partyService.PARTY_INVITES.get(targetCloudPlayer).contains(cloudPlayer))
                        {

                            partyInvites = partyService.PARTY_INVITES.get(targetCloudPlayer);
                            partyInvites.remove(targetCloudPlayer);

                            partyService.PARTY_INVITES.put(targetCloudPlayer, partyInvites);

                            PartyService inquiryParty = PartyService.getParty(targetCloudPlayer);
                            inquiryParty.addPartyPlayer(cloudPlayer);

                            final Iterator partyCloudPlayers = CloudAPI.getInstance().getOnlinePlayers().iterator();

                            while (partyCloudPlayers.hasNext())
                            {
                                final CloudPlayer partyPlayers = (CloudPlayer) partyCloudPlayers.next();

                                PlayerExecutorBridge.INSTANCE.sendMessage(partyPlayers, PartySystem.PARTY_PREFIX + "§e" + cloudPlayer.getName() + " §aist der Party beigetreten§8!");
                            }

                        } else
                        {
                            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§e" + cloudPlayer.getName() + " §chat dich nicht eingeladen§8!");
                            return;
                        }

                    } else
                    {
                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDieser Spieler befindet sich nicht auf dem Netzwerk§8!");
                        return;
                    }

                } else if (arguments[0].equalsIgnoreCase("deny"))
                {

                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(arguments[1]);
                    CloudPlayer targetCloudPlayer = CloudAPI.getInstance().getOnlinePlayer(targetPlayer.getUniqueId());

                    PartyService partyService = PartyService.getParty(cloudPlayer);

                    if (targetPlayer != null)
                    {

                        if (partyService != null && partyService.PARTY_INVITES.get(cloudPlayer).contains(targetCloudPlayer))
                        {

                            partyInvites = partyService.PARTY_INVITES.get(cloudPlayer);
                            partyInvites.remove(targetCloudPlayer);

                            partyService.PARTY_INVITES.put(cloudPlayer, partyInvites);

                            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§e" + targetCloudPlayer.getName() + " §chat die Partyanfrage abgelehnt§8!");

                        } else
                        {
                            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§e" + cloudPlayer.getName() + " §chat dich nicht eingeladen§8!");
                            return;
                        }

                    } else
                    {
                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, PartySystem.PARTY_PREFIX + "§cDieser Spieler befindet sich nicht auf dem Netzwerk§8!");
                        return;
                    }
                }


            } else if (arguments.length == 1)
            {

                if (arguments[0].equalsIgnoreCase("id") || arguments[0].equalsIgnoreCase("currentid"))
                {

                    PartyService partyService = PartyService.getParty(cloudPlayer);

                    if (partyService.checkPartyPlayer(cloudPlayer))
                    {

                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                                PartySystem.PARTY_PREFIX + "§7Du befindest dich in Party§8: §e" + partyService.getPartyId());

                    } else
                    {

                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                                PartySystem.PARTY_PREFIX + "§cAktuell befindest du dich in keiner Party§8!");

                        return;

                    }

                } else if (arguments[0].equalsIgnoreCase("list") || arguments[0].equalsIgnoreCase("playerlist"))
                {

                    PartyService partyService = PartyService.getParty(cloudPlayer);

                    if (partyService.checkPartyPlayer(cloudPlayer))
                    {

                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                                PartySystem.PARTY_PREFIX + "§7Alle Partymitglieder§8:");

                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                                PartySystem.PARTY_PREFIX + "§7Partyleitung§8: §c" + partyService.getPartyLeader().getName());

                        StringBuilder textMessage = null;

                        if (partyService.getPartyMembers().size() == 1)
                        {
                            textMessage.append(PartySystem.PARTY_PREFIX + "§cKeine Spieler vorhanden§8!");
                        } else
                        {

                            for (int members = 0; members < partyService.getPartyMembers().size(); ++members)
                            {
                                textMessage.append("§e").append(partyService.getPartyMembers().get(members)).append("§8, ");
                            }

                            textMessage = new StringBuilder(textMessage.toString().replace("§e" + partyService.getPartyLeader().getName() + "§8, ", ""));

                        }

                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                                PartySystem.PARTY_PREFIX + "§7Partymitglieder§8: §e" + partyService.getPartyLeader().getName());


                    } else
                    {

                        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                                PartySystem.PARTY_PREFIX + "§cAktuell befindest du dich in keiner Party§8!");

                        return;

                    }

                }

            }
            {
                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                        PartySystem.PARTY_PREFIX + "§e/party invite <Spieler> §8- §7Lade einen Spieler ein");
                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                        PartySystem.PARTY_PREFIX + "§e/party accept <Spieler> §8- §7Akzeptiere Partyanfragen");
                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                        PartySystem.PARTY_PREFIX + "§e/party deny <Spieler> §8- §7Lehne Partyanfragen ab");
                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                        PartySystem.PARTY_PREFIX + "§e/party list §8- §7Sehe alle Partymitglieder");
                PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer,
                        PartySystem.PARTY_PREFIX + "§e/party id §8- §7Sehe die Party-ID");
            }
        }
    }
}
