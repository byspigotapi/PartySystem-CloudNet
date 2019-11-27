package me.github.nettyexception.partysystem.utilities;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import me.github.nettyexception.partysystem.PartySystem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * PartySystem copyright (©) 11 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

public class InquiryMessage {

    public static void sendInquiryMessage(final ProxiedPlayer proxiedPlayer, final String friendname)
    {
        final TextComponent acceptTextMessage = new TextComponent("§8[§aAnnehmen§8]");
        acceptTextMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + friendname));
        acceptTextMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§aTrete der Party bei§8!").create())));

        final TextComponent denyTextMessage = new TextComponent("§8[§cAblehnen§8]");
        denyTextMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + friendname));
        denyTextMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§cLehne die Partyanfrage ab§8!").create())));

        final TextComponent spacerTextMessage = new TextComponent(" §8| ");
        final TextComponent textMessage = new TextComponent(PartySystem.PARTY_PREFIX + "§7Du hast eine Partyeinladung von §e" + friendname + "§7 erhalten§8!\n"
                + PartySystem.PARTY_PREFIX + " §7§o» ");

        textMessage.addExtra(acceptTextMessage);
        textMessage.addExtra(spacerTextMessage);
        textMessage.addExtra(denyTextMessage);

        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(proxiedPlayer.getUniqueId());

        PlayerExecutorBridge.INSTANCE.sendActionbar(cloudPlayer,
                PartySystem.PARTY_PREFIX + "§7Du hast eine §ePartyeinladung §7erhalten§8!");

        proxiedPlayer.sendMessage(textMessage);
    }

}
