package anon.seamlessauth.skin.network;

import net.minecraft.entity.player.EntityPlayerMP;

import anon.seamlessauth.Config;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.Tags;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketDispatcher {

    public static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);

    public static void registerPackets() {
        int discriminator = 0;

        PacketDispatcher.dispatcher.registerMessage(SkinQuery.class, SkinQuery.class, discriminator, Side.CLIENT);
        PacketDispatcher.dispatcher.registerMessage(SkinQuery.class, SkinQuery.class, discriminator, Side.SERVER);
        discriminator++;

        PacketDispatcher.dispatcher.registerMessage(SkinAnswer.class, SkinAnswer.class, discriminator, Side.CLIENT);
        PacketDispatcher.dispatcher.registerMessage(SkinAnswer.class, SkinAnswer.class, discriminator, Side.SERVER);
        discriminator++;

        PacketDispatcher.dispatcher.registerMessage(SkinRequest.class, SkinRequest.class, discriminator, Side.CLIENT);
        PacketDispatcher.dispatcher.registerMessage(SkinRequest.class, SkinRequest.class, discriminator, Side.SERVER);
        discriminator++;

        PacketDispatcher.dispatcher.registerMessage(SkinResponse.class, SkinResponse.class, discriminator, Side.CLIENT);
        PacketDispatcher.dispatcher.registerMessage(SkinResponse.class, SkinResponse.class, discriminator, Side.SERVER);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        if (!Config.enableSkinSharing) {
            SeamlessAuth.LOG.warn("attempted to send packet while sharing disabled");
            return;
        }

        dispatcher.sendTo(message, player);
    }

    public static void sendToServer(IMessage message) {
        if (!Config.enableSkinSharing) {
            SeamlessAuth.LOG.warn("attempted to send packet while sharing disabled");
            return;
        }

        dispatcher.sendToServer(message);
    }
}
