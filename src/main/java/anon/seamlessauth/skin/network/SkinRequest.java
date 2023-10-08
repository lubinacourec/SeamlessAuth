package anon.seamlessauth.skin.network;

import java.util.Base64;

import anon.seamlessauth.Config;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.skin.ClientSkinHandler;
import anon.seamlessauth.skin.ServerSkinHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class SkinRequest implements IMessage, IMessageHandler<SkinRequest, SkinResponse> {

    public byte[] hash;

    public SkinRequest() {}

    public SkinRequest(byte[] hash) {
        this.hash = hash;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.readBytes(hash = new byte[32]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBytes(hash);
    }

    @Override
    public SkinResponse onMessage(SkinRequest message, MessageContext ctx) {
        SeamlessAuth.debug(
            "recieved SkinRequest for {}",
            Base64.getUrlEncoder()
                .encodeToString(message.hash));
        if (!Config.enableSkinSharing) return null;

        if (ctx.side == Side.CLIENT)
            return new SkinResponse(message.hash, ClientSkinHandler.instance.getDataFromHash(message.hash));
        if (ctx.side == Side.SERVER)
            ServerSkinHandler.instance.requestSkin(message.hash, ctx.getServerHandler().playerEntity);

        return null;
    }
}
