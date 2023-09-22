package anon.seamlessauth;

import anon.seamlessauth.util.KeyDatabase;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
    public static KeyDatabase keyDatabase;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        keyDatabase = new KeyDatabase(expandPath(Config.databasePath));
    }
}
