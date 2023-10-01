package anon.seamlessauth.auth.network.packet;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import anon.seamlessauth.auth.network.server.INetHandlerAuthServer;

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
        this.processPacket((INetHandlerAuthServer) handler);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }
}
