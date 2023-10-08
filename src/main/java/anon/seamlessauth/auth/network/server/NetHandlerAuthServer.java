package anon.seamlessauth.auth.network.server;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import anon.seamlessauth.Config;
import anon.seamlessauth.ServerProxy;
import anon.seamlessauth.auth.network.packet.ChallengeRequest;
import anon.seamlessauth.auth.network.packet.ChallengeResponse;
import anon.seamlessauth.auth.network.packet.KeyResponse;
import anon.seamlessauth.util.CryptoInstances;
import anon.seamlessauth.util.Pair;
import io.netty.util.concurrent.GenericFutureListener;

public class NetHandlerAuthServer extends NetHandlerLoginServer implements INetHandlerAuthServer {

    private static final SecureRandom challengeGenerator = new SecureRandom();

    private byte[] challenge = new byte[64];

    public NetHandlerAuthServer(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_, GameProfile user) {
        super(p_i45298_1_, p_i45298_2_);
        field_147337_i = func_152506_a(user);
    }

    @Override
    protected GameProfile func_152506_a(GameProfile original) {
        Pair<UUID, PublicKey> user = ServerProxy.keyDatabase.authorized.get(original.getName());

        UUID uuid;
        if (user == null)
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
        else uuid = user.first;

        return new GameProfile(uuid, original.getName());
    }

    @Override
    public void handleKeyResponse(KeyResponse packetIn) {
        PublicKey key = packetIn.key;
        Pair<UUID, PublicKey> user = ServerProxy.keyDatabase.authorized.get(field_147337_i.getName());
        if (user == null) {
            if (Config.implicitRegistration) {
                ServerProxy.keyDatabase.addUser(field_147337_i.getName(), field_147337_i.getId(), key);
            } else func_147322_a("implicit registration is disabled");
        } else if (!key.equals(user.second)) func_147322_a("mismatched key");

        NetHandlerAuthServer.challengeGenerator.nextBytes(challenge);
        byte[] encryptedChallenge;

        try {
            CryptoInstances.rsaCipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedChallenge = CryptoInstances.rsaCipher.doFinal(challenge);
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
