package moe.caramel.caramelaimlesssurvival;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import moe.caramel.caramelaimlesssurvival.listener.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    public String tabString;
    public ProtocolManager protocolManager;
    public int playerRenderSize;

    @Override
    public void onEnable() {

        protocolManager = ProtocolLibrary.getProtocolManager();
        playerRenderSize = Bukkit.getServer().spigot().getSpigotConfig().getInt("world-settings.default.entity-tracking-range.players") + 15;

        if (!getConfig().getBoolean("Content.Allow.ChatCommand"))
            getServer().getPluginManager().registerEvents(new PlayerChatEvents(), this);
        if (!getConfig().getBoolean("Content.Allow.JoinMessage"))
            getServer().getPluginManager().registerEvents(new PlayerJoinEvents(this), this);
        if (!getConfig().getBoolean("Content.Allow.DeathEvents"))
            getServer().getPluginManager().registerEvents(new PlayerDeathEvents(this), this);
        if (!getConfig().getBoolean("Content.Allow.UseSignBook"))
            getServer().getPluginManager().registerEvents(new PlayerSignBookEvents(this), this);
        if (getConfig().getBoolean("Content.Allow.PacketManipulation")) {
            getServer().getPluginManager().registerEvents(new PacketManipulationEvents(this), this);
            protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    ArrayList<PlayerInfoData> playerInfoData = new ArrayList<>();
                    Player player = event.getPlayer();
                    Location playerLoc = player.getLocation();
                    if (packet.getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            Location onlineLoc = onlinePlayer.getLocation();
                            if (getDistance(playerLoc.getX(), playerLoc.getZ(), onlineLoc.getX(), onlineLoc.getZ()) <= playerRenderSize) {
                                playerInfoData.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(onlinePlayer), 0,
                                        EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(onlinePlayer.getName())));
                            }
                        }

                        packet.getPlayerInfoDataLists().write(0, playerInfoData);
                        event.setPacket(packet);
                    }
                }
            });
        }

        getLogger().log(Level.INFO, "Classes Load Success.");

        configInit();
        getLogger().log(Level.INFO, "Config Load Success.");

        StringBuilder tabListString = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            tabListString.append("\n");
            for (int j = 0; j < 20; j++) {
                tabListString.append("  ");
            }
        }
        tabString = tabListString.toString();
        getLogger().log(Level.INFO, "TabList String Load Success.");

        getServer().setSpawnRadius(1);
        for (World worlds : getServer().getWorlds()) {
            worlds.setGameRule(GameRule.REDUCED_DEBUG_INFO, getConfig().getBoolean("GameRule.ReduceDebugInfo"));
            worlds.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, getConfig().getBoolean("GameRule.AnnounceAdvancements"));
            WorldBorder worldBorder = worlds.getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(getConfig().getDouble("WorldBorder.WorldSize"));
        }
        getLogger().log(Level.INFO, "World Setting Success.");

        getLogger().log(Level.INFO, "Starting Plugin.");
    }

    public void configInit() {
        getConfig().options().copyDefaults(true);

        getConfig().addDefault("WorldBorder.WorldSize", 5000);

        getConfig().addDefault("GameRule.ReduceDebugInfo", true);
        getConfig().addDefault("GameRule.AnnounceAdvancements", false);

        getConfig().addDefault("Content.Allow.ChatCommand", false);
        getConfig().addDefault("Content.Allow.JoinMessage", false);
        getConfig().addDefault("Content.Allow.DeathEvents", false);
        getConfig().addDefault("Content.Allow.UseSignBook", false);
        getConfig().addDefault("Content.Allow.PacketManipulation", true);
        getConfig().addDefault("Content.ChatRange", 10);

        getConfig().addDefault("Server.MOTD", "Aimless Survival");

        saveConfig();
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Bye.");
    }

    /* Utils */
    public double getDistance(double x1, double z1, double x2, double z2) {
        return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(z2 - z1), 2));
    }

    public Location randomLocation(Player player, int range) {
        int x = randomRange(range), z = randomRange(range);
        Location _temp = new Location(player.getWorld(), x, 0, z);
        _temp.setY(player.getLocation().getWorld().getHighestBlockYAt(_temp));
        return _temp;
    }

    public int randomRange(int range) {
        return ThreadLocalRandom.current().nextInt((range * -1), range + 1);
    }

}
