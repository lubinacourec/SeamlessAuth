package anon.seamlessauth;

import anon.seamlessauth.util.KeyManager;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static KeyManager keyManager;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        keyManager = new KeyManager(expandPath(Config.pubKeyPath), expandPath(Config.prvKeyPath));
    }
}
