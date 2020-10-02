package moe.caramel.caramelaimlesssurvival.packetwrapepr;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;

public abstract class AbstractPacket {

    protected PacketContainer handle;

    protected AbstractPacket(PacketContainer handle, PacketType type) {
        if (handle == null)
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        if (!Objects.equal(handle.getType(), type))
            throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);

        this.handle = handle;
    }

    public PacketContainer getHandle() {
        return handle;
    }

    public void sendPacket(Player receiver) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, getHandle());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }

}