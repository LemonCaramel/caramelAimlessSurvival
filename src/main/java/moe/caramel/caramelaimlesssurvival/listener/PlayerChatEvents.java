package moe.caramel.caramelaimlesssurvival.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerChatEvents implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().sendMessage("§c§l[!] §c채팅이 불가능한 게임입니다.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        if (!cmd.equals("/pl") && !cmd.equals("/plugins")) {
            event.getPlayer().sendMessage("§c§l[!] §c명령어 사용이 불가능한 게임입니다.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        List<String> blockedCommands = new ArrayList<>();
        for (String cmd : event.getCommands()) {
            if (!cmd.equals("pl") && !cmd.equals("plugins"))
                blockedCommands.add(cmd);
        }
        event.getCommands().removeAll(blockedCommands);
    }

}
