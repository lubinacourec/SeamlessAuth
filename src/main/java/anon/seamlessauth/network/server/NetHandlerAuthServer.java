package anon.seamlessauth.network.server;

import java.util.Arrays;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import com.mojang.authlib.GameProfile;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.network.packet.ChallengeRequest;
import anon.seamlessauth.network.packet.ChallengeResponse;
import anon.seamlessauth.network.packet.KeyResponse;
import cpw.mods.fml.common.FMLCommonHandler;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;

public class NetHandlerAuthServer extends NetHandlerLoginServer implements INetHandlerAuthServer {
    private static final SecureRandom challengeGenerator = new SecureRandom();
    
    private byte[] challenge = new byte[64];
    private Cipher cipher;

    public NetHandlerAuthServer(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_, GameProfile user) {
        super(p_i45298_1_, p_i45298_2_);
        field_147337_i = user;

        try {
            cipher = Cipher.getInstance("RSA");
        } catch (Exception e) {
            SeamlessAuth.LOG.fatal("failed to get RSA cipher", e);
            FMLCommonHandler.instance().exitJava(1, false);
        }
    }

    @Override
    public void handleKeyResponse(KeyResponse packetIn) {
        PublicKey key = packetIn.key;
        NetHandlerAuthServer.challengeGenerator.nextBytes(challenge);
        byte[] encryptedChallenge;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedChallenge = cipher.doFinal(challenge);
        } catch (Exception e) {
            func_147322_a("invalid key");
            return;
        }

        field_147333_a.scheduleOutboundPacket(new ChallengeRequest(encryptedChallenge), new GenericFutureListener[0]);
    }

    @Override
    public void handleChallengeResponse(ChallengeResponse packetIn) {
        if (!Arrays.equals(challenge, packetIn.payload)) {
            func_147322_a("challenge failed");
            return;
        }

        field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
    }
}
