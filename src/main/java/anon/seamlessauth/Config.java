package anon.seamlessauth;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String pubKeyPath;
    public static String prvKeyPath;
    
    public static String databasePath;
    public static boolean implicitRegistration;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        
        pubKeyPath = configuration.getString("publicKeyPath", "client", "~/authkey.pub", "Path to the public component of the keypair used for authentication. An initial ~ expands to the configuration directory.");
        prvKeyPath = configuration.getString("privateKeyPath", "client", "~/authkey.prv", "Path to the private component of the keypair used for authentication. An initial ~ expands to the configuration directory.");
        
        databasePath = configuration.getString("databasePath", "server", "~/authorized_users", "Path to the file containing the pinned keys of registered users. An initial ~ expands to the configuration directory.");
        implicitRegistration = configuration.getBoolean("implicitRegistration", "server", true, "Decides whether the server will accept and pin keys for usernames that aren't otherwise registered already. Disabling this will mean only keys that you specifically authorize will be able to join.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
