package moe.caramel.caramelaimlesssurvival;

import moe.caramel.caramelaimlesssurvival.listener.*;
import moe.caramel.caramelaimlesssurvival.scheduler.PlayerList;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;

import java.util.Random;
import java.util.logging.Level;

/**
 *
 * 유지보수하기 어렵게 코딩하는 방법: 평생 개발자로 먹고 살 수 있다
 *
 * @author LemonCaramel
 * @since 2020
 */
public final class Main extends JavaPlugin {

    public String tabString;

    @Override
    public void onEnable() {
        if (!this.getConfig().getBoolean("Content.Allow.ChatCommand"))
            this.getServer().getPluginManager().registerEvents(new PlayerChatEvents(), this);
        if (!this.getConfig().getBoolean("Content.Allow.JoinMessage"))
            this.getServer().getPluginManager().registerEvents(new PlayerJoinEvents(this), this);
        if (!this.getConfig().getBoolean("Content.Allow.DeathEvents"))
            this.getServer().getPluginManager().registerEvents(new PlayerDeathEvents(this), this);
        if (!this.getConfig().getBoolean("Content.Allow.UseSignBook"))
            this.getServer().getPluginManager().registerEvents(new PlayerSignBookEvents(this), this);
        if (this.getConfig().getBoolean("Content.Allow.PacketManipulation")) {
            this.getServer().getPluginManager().registerEvents(new PacketManipulationEvents(this), this);
            this.getServer().getScheduler().runTaskTimer(this, new PlayerList(), 0L, 1L);
        }

        this.configInit();
        StringBuilder tabListString = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            tabListString.append("\n");
            for (int j = 0; j < 20; j++) {
                tabListString.append("  ");
            }
        }
        tabString = tabListString.toString();

        this.getServer().setSpawnRadius(1);

        for (World world : getServer().getWorlds()) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(getConfig().getDouble("WorldBorder.WorldSize"));

            world.setSpawnLocation(world.getHighestBlockAt(0, 0).getLocation());
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, getConfig().getBoolean("GameRule.ReduceDebugInfo"));
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, getConfig().getBoolean("GameRule.AnnounceAdvancements"));
        }
        this.getLogger().log(Level.INFO, "월드 설정이 완료되었습니다.");
    }

    public void configInit() {
        this.getConfig().options().copyDefaults(true);

        this.getConfig().addDefault("WorldBorder.WorldSize", 5000);

        this.getConfig().addDefault("GameRule.ReduceDebugInfo", true);
        this.getConfig().addDefault("GameRule.AnnounceAdvancements", false);

        this.getConfig().addDefault("Content.Allow.ChatCommand", false);
        this.getConfig().addDefault("Content.Allow.JoinMessage", false);
        this.getConfig().addDefault("Content.Allow.DeathEvents", false);
        this.getConfig().addDefault("Content.Allow.UseSignBook", false);
        this.getConfig().addDefault("Content.Allow.PacketManipulation", true);
        this.getConfig().addDefault("Content.ChatRange", 10);

        this.getConfig().addDefault("Server.DeathMessage", "§c누군가가 죽었다.");
        this.getConfig().addDefault("Server.MOTD", "Aimless Survival");

        this.saveConfig();
    }

    /* Utils */
    public double getDistance(double x1, double z1, double x2, double z2) {
        return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(z2 - z1), 2));
    }

    public Location randomLocation(Player player) {
        Random random = new Random(player.getName().hashCode() ^ 0x12345678);

        World world = this.getServer().getWorlds().get(0);
        double size = (this.getConfig().getDouble("WorldBorder.WorldSize") / 2.0) - 100;
        double x = this.getRandom(random, size), z = this.getRandom(random, size);

        return world.getHighestBlockAt(NumberConversions.floor(x), NumberConversions.floor(z))
                .getLocation().add(0.5, 1.0, 0.5);
    }

    private double getRandom(Random random, double size) {
        return (random.nextDouble() * size) - size;
    }

}
