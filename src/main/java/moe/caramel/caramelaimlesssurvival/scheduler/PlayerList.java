package moe.caramel.caramelaimlesssurvival.scheduler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
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
        PacketContainer container = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> playerInfoData = new ArrayList<>();

        int i = 0;
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            WrappedGameProfile profile = (offlinePlayer instanceof Player) ?
                    WrappedGameProfile.fromPlayer((Player) offlinePlayer) :
                    WrappedGameProfile.fromOfflinePlayer(offlinePlayer).withName(offlinePlayer.getName());

            playerInfoData.add(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.NOT_SET,
                            WrappedChatComponent.fromText(offlinePlayer.getName())));

            // 접속한 오프라인 플레이어가 많을 경우, 플레이어 리스트가 변동될 때마다 TPS가 심각하게 저하됨
            if (++i > 100) break;
        }

        container.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        container.getPlayerInfoDataLists().write(0, playerInfoData);

        try {
            for (Player player : Bukkit.getOnlinePlayers())
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
        } catch (InvocationTargetException exception) {
            throw new RuntimeException("Cannot send packet.", exception);
        }
    }

}
