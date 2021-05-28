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
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(this.plugin.getConfig().getString("Server.DeathMessage"));
        System.out.println(event.getEntity().getName() + "님께서 사망하셨습니다.");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (event.isBedSpawn() || event.isAnchorSpawn()) return;

        event.setRespawnLocation(this.plugin.randomLocation(player));
    }

}
