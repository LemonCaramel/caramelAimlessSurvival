package moe.caramel.caramelaimlesssurvival.listener;

import moe.caramel.caramelaimlesssurvival.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinEvents implements Listener {

    private final Main plugin;

    public PlayerJoinEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPlayedBefore()) {
            event.setJoinMessage(null);
        } else {
            int range = (plugin.getConfig().getInt("WorldBorder.WorldSize") / 2) - 100;
            player.teleport(plugin.randomLocation(player, range));
            event.setJoinMessage("§e누군가가 처음으로 입장했다.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

}
