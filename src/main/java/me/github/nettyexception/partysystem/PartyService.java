package me.github.nettyexception.partysystem;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * PartySystem copyright (©) 10 2019 by Sören Simons (NettyException)
 * NettyException and his team are authorized to use and edit any code for an unlimited period of time.
 */

@SuppressWarnings("RedundantCast")
public class PartyService {

    /**
     * @PartySystem HashMap for the Partyleader
     */

    public static HashMap<String, CloudPlayer> PARTY_LEADER = new HashMap<String, CloudPlayer>();

    /**
     * @PartySystem HashMap for the Partymembers
     */

    public HashMap<String, ArrayList<CloudPlayer>> PARTY_MEMBERS = new HashMap<String, ArrayList<CloudPlayer>>();

    /**
     * @PartySystem HashMap for Party invites
     */

    public HashMap<CloudPlayer, ArrayList<CloudPlayer>> PARTY_INVITES = new HashMap<CloudPlayer, ArrayList<CloudPlayer>>();

    /**
     * @PartySystem HashMap for Partylist
     */

    public static HashMap<CloudPlayer, String> PARTY_LIST = new HashMap<CloudPlayer, String>();

    /**
     * @PartySystem HashMap for Partychat
     */

    public static HashMap<String, ArrayList<CloudPlayer>> PARTY_CHAT_MEMBERS = new HashMap<>();

    /**
     * @PartySystem UniqueID for the Party
     */

    public static String PARTY_ID;

    public PartyService(String partyUniqueId) {
        PartyService.PARTY_ID = partyUniqueId;
    }

    /**
     * @PartySystem Ask the respective party with the corresponding ID from
     * @param uniqueId
     */

    public static PartyService getParty(final String uniqueId)
    {
        return new PartyService(PARTY_ID);
    }

    /**
     * @PartySystem Ask the respective party with the corresponding owner from
     * @param cloudPlayer
     */

    public static PartyService getParty(final CloudPlayer cloudPlayer)
    {
        return new PartyService((String)PARTY_LIST.get(cloudPlayer));
    }

    /**
     * @PartySystem Check the Partyowner!
     * @param cloudPlayer
     */

    public boolean checkPartyOwner(final CloudPlayer cloudPlayer)
    {
        return PARTY_LEADER.containsValue(cloudPlayer);
    }

    /**
     * @PartySystem Invite a player!
     * @param fromCloudPlayer
     * @param toCloudPlayer
     */

    public synchronized void invitePlayer(final CloudPlayer fromCloudPlayer, final CloudPlayer toCloudPlayer)
    {
        final ArrayList<CloudPlayer> partyInvites = (ArrayList)PARTY_INVITES.get(toCloudPlayer);
        partyInvites.add(fromCloudPlayer); // Invite the Player

        PARTY_INVITES.remove(toCloudPlayer);
        PARTY_INVITES.put(toCloudPlayer, partyInvites);
    }

    /**
     * @PartySystem Create a new party
     * @throws ArrayIndexOutOfBoundsException
     * @param cloudPlayer
     */

    public static synchronized void createParty(final CloudPlayer cloudPlayer)
    {
        final int intenger = (int) (Math.random() * 99999);
        final String partyId = "#" + intenger + "-" + PartySystem.SERVER_HOSTNAME;

        PartyService partyService = new PartyService(partyId);

        try {
            PartyService.PARTY_LIST.put(cloudPlayer, partyId);
            PARTY_LEADER.put(partyId, cloudPlayer);
        } catch (ArrayIndexOutOfBoundsException arrayException) {
            arrayException.printStackTrace();
        }

        final ArrayList<CloudPlayer> partyMember = new ArrayList<CloudPlayer>();

        try {
            partyMember.add(cloudPlayer);
        } catch (ArrayIndexOutOfBoundsException arrayException) {
            arrayException.printStackTrace();
        }

        try {
            partyService.PARTY_MEMBERS.put(partyId, partyMember);
        } catch (ArrayIndexOutOfBoundsException arrayException) {
            arrayException.printStackTrace();
        }

    }

    /**
     * @PartySystem delete a Party!
     */

    public synchronized void deleteParty()
    {

        final ArrayList partyList = (ArrayList)PARTY_MEMBERS.get(PARTY_ID);

        for (int players = 0; players < partyList.size(); ++players)
            PartyService.PARTY_LIST.remove(partyList.get(players));

        PARTY_MEMBERS.remove(PARTY_ID);
        PARTY_LEADER.remove(PARTY_ID);

    }

    /**
     * @PartySystem Add a Player to the Party!
     * @param cloudPlayer
     */

    public synchronized void addPartyPlayer(final CloudPlayer cloudPlayer)
    {

        final ArrayList partyList = (ArrayList)PARTY_MEMBERS.get(PARTY_ID);
        partyList.add(cloudPlayer); // Adding the new player!

        PARTY_MEMBERS.remove(PARTY_ID);
        PARTY_MEMBERS.put(PARTY_ID, partyList);

        PartyService.PARTY_LIST.put(cloudPlayer, PARTY_ID);

        CloudProxy.getInstance().getCachedPlayer(
                cloudPlayer.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).setPrefix(" §8[§dParty§8]");

    }

    /**
     * @PartySystem Remove a Player from the Party!
     * @param cloudPlayer
     */

    public synchronized void removePartyPlayer(final CloudPlayer cloudPlayer)
    {

        final ArrayList partyList = (ArrayList)PARTY_MEMBERS.get(PARTY_ID);
        partyList.remove(cloudPlayer); // Removing the player from party!

        cloudPlayer.getPermissionEntity().setSuffix(null);

        PARTY_MEMBERS.remove(cloudPlayer);
        PARTY_MEMBERS.put(PARTY_ID, partyList);
        PartyService.PARTY_LIST.remove(cloudPlayer);

        CloudProxy.getInstance().getCachedPlayer(
                cloudPlayer.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).setSuffix(null);

    }

    /**
     * @PartySystem Get the Partymembers!
     */

    public ArrayList<CloudPlayer> getPartyMembers()
    {
        return (ArrayList)PARTY_MEMBERS.get(PARTY_ID);
    }

    /**
     * @PartySystem Checking Partyplayer
     * @param cloudPlayer
     */

    public static boolean checkPartyPlayer(final CloudPlayer cloudPlayer)
    {
        return PartyService.PARTY_LIST.containsKey(cloudPlayer);
    }

    /**
     * @PartySystem Sending all PartyPlayers a message.
     * @param cloudPlayer
     * @param message
     */

    public synchronized void sendPartyMessage(CloudPlayer cloudPlayer, String message)
    {
        final PartyService partyService = PartyService.getParty(cloudPlayer);

        CloudAPI.getInstance().getOnlinePlayers().forEach(onlinePlayers ->
        {

            if (partyService.getPartyMembers().contains(onlinePlayers))
            {
                PlayerExecutorBridge.INSTANCE.sendMessage(onlinePlayers, message);
            }

        });
    }

    public CloudPlayer getPartyLeader()
    {
        return (CloudPlayer)PARTY_LEADER.get(PARTY_ID);
    }

    public HashMap<CloudPlayer, ArrayList<CloudPlayer>> getPartyInvites() {
        return PARTY_INVITES;
    }

    public HashMap<CloudPlayer, String> getPartyList() {
        return PARTY_LIST;
    }

    public String getPartyId() {
        return PARTY_ID;
    }
}

