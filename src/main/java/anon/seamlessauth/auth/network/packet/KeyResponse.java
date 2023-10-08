package anon.seamlessauth.auth.network.packet;

import java.io.IOException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import anon.seamlessauth.auth.network.server.INetHandlerAuthServer;
import anon.seamlessauth.util.CryptoInstances;

public class KeyResponse extends Packet {

    public PublicKey key;

    public KeyResponse() {}

    public KeyResponse(PublicKey userKey) {
        key = userKey;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        byte[] blob = readBlob(data);

        try {
            key = CryptoInstances.rsaFactory.generatePublic(new X509EncodedKeySpec(blob));
        } catch (InvalidKeySpecException e) {
            key = null;
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        writeBlob(data, key.getEncoded());
    }

    public void processPacket(INetHandlerAuthServer handler) {
        handler.handleKeyResponse(this);
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
