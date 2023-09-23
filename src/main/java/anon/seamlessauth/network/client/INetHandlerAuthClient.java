package anon.seamlessauth.network.client;

import net.minecraft.network.login.INetHandlerLoginClient;

import anon.seamlessauth.network.packet.ChallengeRequest;
import anon.seamlessauth.network.packet.KeyRequest;

public interface INetHandlerAuthClient extends INetHandlerLoginClient {

    void handleKeyRequest(KeyRequest packetIn);

    void handleChallengeRequest(ChallengeRequest packetIn);
}
