package moe.caramel.caramelaimlesssurvival.scheduler;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import moe.caramel.caramelaimlesssurvival.packetwrapepr.WrapperPlayServerPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerList implements Runnable {

    private static boolean update = false;

    public static void update() {
        update = true;
    }

    @Override
    public void run() {
        if (update) {
            update = false;
            updatePlayerList();
        }
    }

    private void updatePlayerList() {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> playerInfoData = new ArrayList<>();

        int i = 0;
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            WrappedGameProfile profile =
                    (offlinePlayer instanceof Player) ? WrappedGameProfile.fromPlayer((Player) offlinePlayer) :
                            WrappedGameProfile.fromOfflinePlayer(offlinePlayer).withName(offlinePlayer.getName());

            playerInfoData.add(
                    new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.NOT_SET,
                            WrappedChatComponent.fromText(offlinePlayer.getName())));

            if (++i > 100) break;
        }

        packet.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        packet.setData(playerInfoData);

        for (Player player : Bukkit.getOnlinePlayers()) {
            packet.sendPacket(player);
        }
    }

}
