package anon.seamlessauth;

import anon.seamlessauth.network.packet.ChallengeRequest;
import anon.seamlessauth.network.packet.ChallengeResponse;
import anon.seamlessauth.network.packet.KeyRequest;
import anon.seamlessauth.network.packet.KeyResponse;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.network.EnumConnectionState;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        SeamlessAuth.LOG.info(Tags.MODNAME + " (" + Tags.VERSION + ") loading...");
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
 
        /** packet registration **/
        /* server-side */
        EnumConnectionState.LOGIN.func_150751_a(2, KeyResponse.class);
        EnumConnectionState.LOGIN.func_150751_a(3, ChallengeResponse.class);
        /* client-side */
        EnumConnectionState.LOGIN.func_150756_b(3, KeyRequest.class);
        EnumConnectionState.LOGIN.func_150756_b(4, ChallengeRequest.class);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
