package anon.seamlessauth.network.server;

import anon.seamlessauth.network.packet.ChallengeResponse;
import anon.seamlessauth.network.packet.KeyResponse;
import net.minecraft.network.login.INetHandlerLoginServer;

public interface INetHandlerAuthServer extends INetHandlerLoginServer {
    void handleKeyResponse(KeyResponse packetIn);
    void handleChallengeResponse(ChallengeResponse packetIn);
}
