package anon.seamlessauth.network.client;

import anon.seamlessauth.ClientProxy;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.network.packet.ChallengeRequest;
import anon.seamlessauth.network.packet.ChallengeResponse;
import anon.seamlessauth.network.packet.KeyRequest;
import anon.seamlessauth.network.packet.KeyResponse;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ChatComponentText;

public class NetHandlerAuthClient extends NetHandlerLoginClient implements INetHandlerAuthClient {
    public NetHandlerAuthClient(NetworkManager p_i45059_1_, Minecraft p_i45059_2_, GuiScreen p_i45059_3_) {
        super(p_i45059_1_, p_i45059_2_, p_i45059_3_);
    }

    @Override
    public void handleKeyRequest(KeyRequest packetIn) {
        field_147393_d.scheduleOutboundPacket(new KeyResponse(ClientProxy.keyManager.pubKey), new GenericFutureListener[0]);;
    }

    @Override
    public void handleChallengeRequest(ChallengeRequest packetIn) {
        byte[] encryptedChallenge = packetIn.payload;
        byte[] challenge;

        try {
            challenge = ClientProxy.keyManager.decrypt(encryptedChallenge);
        } catch (Exception e) {
            e.printStackTrace();
            field_147393_d.closeChannel(new ChatComponentText("failed to decrypt challenge!"));
            return;
        }
        
        SeamlessAuth.LOG.info("Challenge decrypted, responding to server...");

        field_147393_d.scheduleOutboundPacket(new ChallengeResponse(challenge), new GenericFutureListener[0]);
    }
}
