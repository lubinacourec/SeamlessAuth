package anon.seamlessauth.auth.network.client;

import net.minecraft.network.login.INetHandlerLoginClient;

import anon.seamlessauth.auth.network.packet.ChallengeRequest;
import anon.seamlessauth.auth.network.packet.KeyRequest;

public interface INetHandlerAuthClient extends INetHandlerLoginClient {

    void handleKeyRequest(KeyRequest packetIn);

    void handleChallengeRequest(ChallengeRequest packetIn);
}
