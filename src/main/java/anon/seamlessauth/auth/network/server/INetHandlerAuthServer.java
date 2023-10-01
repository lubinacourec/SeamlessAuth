package anon.seamlessauth.auth.network.server;

import net.minecraft.network.login.INetHandlerLoginServer;

import anon.seamlessauth.auth.network.packet.ChallengeResponse;
import anon.seamlessauth.auth.network.packet.KeyResponse;

public interface INetHandlerAuthServer extends INetHandlerLoginServer {

    void handleKeyResponse(KeyResponse packetIn);

    void handleChallengeResponse(ChallengeResponse packetIn);
}
