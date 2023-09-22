package anon.seamlessauth;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    public String configDir;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        configDir = event.getModConfigurationDirectory().getAbsolutePath();
        SeamlessAuth.keyManager = new KeyManager(expandPath(Config.pubKeyPath), expandPath(Config.prvKeyPath));
    }

    public String expandPath(String original) {
        String result = original;
        if (result.startsWith("~")) result = result.replaceFirst("~", configDir);
        return result;
    }
}
