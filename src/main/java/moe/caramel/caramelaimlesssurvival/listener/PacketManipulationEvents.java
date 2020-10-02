package moe.caramel.caramelaimlesssurvival.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;

import moe.caramel.caramelaimlesssurvival.Main;
import moe.caramel.caramelaimlesssurvival.scheduler.PlayerList;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PacketManipulationEvents implements Listener {

    private final Main plugin;

    public PacketManipulationEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPaperServerListPing(PaperServerListPingEvent event) {
        Calendar c = Calendar.getInstance();

        event.getPlayerSample().clear();
        event.setNumPlayers(c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH));
        event.setMaxPlayers(c.get(Calendar.HOUR) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND));
        event.setMotd(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Server.MOTD")));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerList.update();
        event.getPlayer().setPlayerListHeader(plugin.tabString);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerList.update();
    }
}
