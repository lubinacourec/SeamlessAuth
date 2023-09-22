package anon.seamlessauth.network.packet;

import java.io.IOException;

import anon.seamlessauth.network.server.INetHandlerAuthServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class ChallengeResponse extends Packet {
    public byte[] payload;

    public ChallengeResponse() {}
    public ChallengeResponse(byte[] data) {
        payload = data;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        payload = readBlob(data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        writeBlob(data, payload);
    }
    
    public void processPacket(INetHandlerAuthServer handler) {
        handler.handleChallengeResponse(this);
    }

    @Override
    public void processPacket(INetHandler handler) {
        this.processPacket((INetHandlerAuthServer)handler);
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
}
