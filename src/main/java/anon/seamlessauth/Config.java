package anon.seamlessauth;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String pubKeyPath;
    public static String prvKeyPath;

    public static String skinPath;
    public static String capePath;

    public static String databasePath;
    public static boolean implicitRegistration;

    public static boolean enableSkinSharing;

    public static boolean debugLogging;

    private static File lastRead;

    public static void synchronizeConfiguration(File configFile) {
        if (configFile == null) {
            if (lastRead == null) return;
            configFile = lastRead;
        } else lastRead = configFile;

        Configuration configuration = new Configuration(configFile);

        pubKeyPath = configuration.getString(
            "publicKeyPath",
            "client",
            "authkey.pub",
            "Path to the public component of the keypair used for authentication. An initial ~ expands to the current user's home directory.");
        prvKeyPath = configuration.getString(
            "privateKeyPath",
            "client",
            "authkey.prv",
            "Path to the private component of the keypair used for authentication. An initial ~ expands to the current user's home directory.");

        skinPath = configuration.getString(
            "skinPath",
            "client",
            "skin.png",
            "Path to the image that will be used as this client's skin. An initial ~ expands to the current user's home directory.");
        capePath = configuration.getString(
            "capePath",
            "client",
            "cape.png",
            "Path to the image that will be used as this client's cape. An initial ~ expands to the current user's home directory.");

        databasePath = configuration.getString(
            "databasePath",
            "server",
            "authorized_users",
            "Path to the file containing the pinned keys of registered users. An initial ~ expands to the current user's home directory.");
        implicitRegistration = configuration.getBoolean(
            "implicitRegistration",
            "server",
            true,
            "Decides whether the server will accept and pin keys for usernames that aren't otherwise registered already. Disabling this will mean only keys that you specifically authorize will be able to join.");

        enableSkinSharing = configuration.getBoolean(
            "enableSkinSharing",
            "general",
            true,
            "Decides whether the client or server will send or forward skin queries or requests.");

        debugLogging = configuration.getBoolean(
            "debugLogging",
            "general",
            false,
            "Decides whether extra information will be printed in the log.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static String expandPath(String original) {
        String result = original;
        if (result.startsWith("~/")) result = result.replaceFirst("~", System.getProperty("user.home"));
        return result;
    }
}
