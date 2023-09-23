package anon.seamlessauth.network.server;

import net.minecraft.network.login.INetHandlerLoginServer;

import anon.seamlessauth.network.packet.ChallengeResponse;
import anon.seamlessauth.network.packet.KeyResponse;

public interface INetHandlerAuthServer extends INetHandlerLoginServer {

    void handleKeyResponse(KeyResponse packetIn);

    void handleChallengeResponse(ChallengeResponse packetIn);
}
