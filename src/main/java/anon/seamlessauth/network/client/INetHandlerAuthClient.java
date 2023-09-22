package anon.seamlessauth.network.client;

import anon.seamlessauth.network.packet.ChallengeRequest;
import anon.seamlessauth.network.packet.KeyRequest;
import net.minecraft.network.login.INetHandlerLoginClient;

public interface INetHandlerAuthClient extends INetHandlerLoginClient {
    void handleKeyRequest(KeyRequest packetIn);
    void handleChallengeRequest(ChallengeRequest packetIn);
}
