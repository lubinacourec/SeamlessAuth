package anon.seamlessauth.skin.network;

import java.util.UUID;

import anon.seamlessauth.Config;
import anon.seamlessauth.skin.ClientSkinHandler;
import anon.seamlessauth.skin.ServerSkinHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class SkinQuery implements IMessage, IMessageHandler<SkinQuery, SkinAnswer> {

    UUID uuid;

    public SkinQuery() {}

    public SkinQuery(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public SkinAnswer onMessage(SkinQuery message, MessageContext ctx) {
        if (!Config.enableSkinSharing) return null;

        if (ctx.side == Side.CLIENT) return new SkinAnswer(
            message.uuid,
            ClientSkinHandler.instance.skinHash,
            ClientSkinHandler.instance.capeHash);
        if (ctx.side == Side.SERVER)
            ServerSkinHandler.instance.querySkin(message.uuid, ctx.getServerHandler().playerEntity);

        return null;
    }
}
