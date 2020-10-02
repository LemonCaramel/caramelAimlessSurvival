package moe.caramel.caramelaimlesssurvival.listener;

import moe.caramel.caramelaimlesssurvival.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathEvents implements Listener {

    private final Main plugin;

    public PlayerDeathEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerDeathEvent event) {
        event.setDeathMessage(plugin.getConfig().getString("Server.DeathMessage"));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (event.isBedSpawn() || event.isAnchorSpawn()) return;

        event.setRespawnLocation(plugin.randomLocation(player));
    }

}
