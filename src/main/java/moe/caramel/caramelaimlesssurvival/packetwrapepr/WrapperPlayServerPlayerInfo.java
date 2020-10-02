package moe.caramel.caramelaimlesssurvival.packetwrapepr;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;

public class WrapperPlayServerPlayerInfo extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_INFO;

    public WrapperPlayServerPlayerInfo() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public void setAction(PlayerInfoAction value) {
        handle.getPlayerInfoAction().write(0, value);
    }

    public void setData(List<PlayerInfoData> value) {
        handle.getPlayerInfoDataLists().write(0, value);
    }
}