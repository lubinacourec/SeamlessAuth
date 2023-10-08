package anon.seamlessauth;

import net.minecraftforge.client.ClientCommandHandler;

import anon.seamlessauth.auth.KeyManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static KeyManager keyManager;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        keyManager = new KeyManager(Config.expandPath(Config.pubKeyPath), Config.expandPath(Config.prvKeyPath));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SACommand());
    }
}
