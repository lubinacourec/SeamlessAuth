package anon.seamlessauth.auth.network.packet;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import anon.seamlessauth.auth.network.client.INetHandlerAuthClient;

public class KeyRequest extends Packet {

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {}

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {}

    @Override
    public void processPacket(INetHandler handler) {
        /* here we need to switch the client onto our own code path */
        /* NetHandlerLoginClient.onConnectionStateTransition is patched by mixin for this */
        handler.onConnectionStateTransition(null, null);

        /* mixin has run, get the new handler and call it */
        NetHandlerLoginClient realHandler = (NetHandlerLoginClient) handler;
        INetHandlerAuthClient newHandler = (INetHandlerAuthClient) realHandler.field_147393_d.getNetHandler();
        newHandler.handleKeyRequest(this);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }
}
