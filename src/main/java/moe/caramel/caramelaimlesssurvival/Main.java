package moe.caramel.caramelaimlesssurvival;

import moe.caramel.caramelaimlesssurvival.listener.*;
import moe.caramel.caramelaimlesssurvival.scheduler.PlayerList;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;

import java.util.Random;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    public String tabString;

    @Override
    public void onEnable() {
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
            getServer().getScheduler().runTaskTimer(this, new PlayerList(), 0L, 1L);
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

        for (World world : getServer().getWorlds()) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(getConfig().getDouble("WorldBorder.WorldSize"));

            world.setSpawnLocation(world.getHighestBlockAt(0, 0).getLocation());
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, getConfig().getBoolean("GameRule.ReduceDebugInfo"));
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, getConfig().getBoolean("GameRule.AnnounceAdvancements"));
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

        getConfig().addDefault("Server.DeathMessage", "§c누군가가 죽었다.");
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

    public Location randomLocation(Player player) {
        int hash = player.getName().hashCode();
        Random random = new Random(hash ^ 0x12345678);

        World world = player.getWorld();
        WorldBorder border = world.getWorldBorder();
        double size = border.getSize() / 2.0;
        double x = getRandom(random, size), z = getRandom(random, size);

        return world.getHighestBlockAt(NumberConversions.floor(x), NumberConversions.floor(z))
                .getLocation().add(0.5, 1.0, 0.5);
    }

    private double getRandom(Random random, double size) {
        return random.nextDouble() * size - size / 2.0;
    }

}
