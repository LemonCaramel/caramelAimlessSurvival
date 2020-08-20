package moe.caramel.caramelaimlesssurvival.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;

import moe.caramel.caramelaimlesssurvival.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PacketManipulationEvents implements Listener {

    private final Main plugin;
    private final Map<UUID, ArrayList<UUID>> players = new HashMap<>();

    public PacketManipulationEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPaperServerListPing(PaperServerListPingEvent event) {
        event.setHidePlayers(true);
        event.setMotd(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Server.MOTD")));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setPlayerListHeader(plugin.tabString);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        resetTab(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        resetTab(event.getPlayer());
    }

    private void resetTab(Player player) {
        Location playerLoc = player.getLocation();
        PlayerInfoData playersData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 0,
                EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(player.getName()));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Location onlineLoc = onlinePlayer.getLocation();
            ArrayList<PlayerInfoData> addPlayerInfoData = new ArrayList<>();
            ArrayList<PlayerInfoData> removePlayerInfoData = new ArrayList<>();
            ArrayList<UUID> plList;

            if (players.containsKey(player.getUniqueId())) { plList = players.get(player.getUniqueId()); }
            else {
                plList = new ArrayList<>();
                players.put(player.getUniqueId(), plList);
            }

            if (plugin.getDistance(playerLoc.getX(), playerLoc.getZ(), onlineLoc.getX(), onlineLoc.getZ()) <= plugin.playerRenderSize) {
                if (!plList.contains(onlinePlayer.getUniqueId())) {
                    plList.add(onlinePlayer.getUniqueId());
                    addPlayerInfoData.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(onlinePlayer), 0,
                            EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(onlinePlayer.getName())));
                }
            } else {
                if (plList.contains(onlinePlayer.getUniqueId())) {
                    plList.remove(onlinePlayer.getUniqueId());
                    removePlayerInfoData.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(onlinePlayer), 0,
                            EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(onlinePlayer.getName())));
                }
            }
            players.put(player.getUniqueId(), plList);

            try {
                PacketContainer resetTab = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);

                if (addPlayerInfoData.size() != 0) {
                    resetTab.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    resetTab.getPlayerInfoDataLists().write(0, addPlayerInfoData);
                    plugin.protocolManager.sendServerPacket(player, resetTab);

                    for (PlayerInfoData pld : addPlayerInfoData) {
                        resetTab.getPlayerInfoDataLists().write(0, Collections.singletonList(playersData));
                        plugin.protocolManager.sendServerPacket(Bukkit.getPlayer(pld.getProfile().getUUID()), resetTab);
                    }
                }

                if (removePlayerInfoData.size() != 0) {
                    resetTab.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    resetTab.getPlayerInfoDataLists().write(0, removePlayerInfoData);
                    plugin.protocolManager.sendServerPacket(player, resetTab);

                    for (PlayerInfoData pld : removePlayerInfoData) {
                        resetTab.getPlayerInfoDataLists().write(0, Collections.singletonList(playersData));
                        plugin.protocolManager.sendServerPacket(Bukkit.getPlayer(pld.getProfile().getUUID()), resetTab);
                    }
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
