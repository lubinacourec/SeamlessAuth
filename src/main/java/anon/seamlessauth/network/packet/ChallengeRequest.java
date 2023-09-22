package anon.seamlessauth.network.packet;

import java.io.IOException;

import anon.seamlessauth.network.client.INetHandlerAuthClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class ChallengeRequest extends Packet {
    public byte[] payload;

    public ChallengeRequest() {}
    public ChallengeRequest(byte[] data) {
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

    public void processPacket(INetHandlerAuthClient handler) {
        handler.handleChallengeRequest(this);
    }

    @Override
    public void processPacket(INetHandler handler) {
        this.processPacket((INetHandlerAuthClient)handler);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }
}
