package anon.seamlessauth;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SAServerCommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public String getCommandName() {
        return "seamlessauth_server";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <reload-config|reload-keys>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        IChatComponent usage = new ChatComponentText("usage: " + getCommandUsage(sender));
        if (args.length != 1) {
            sender.addChatMessage(usage);
            return;
        }

        switch (args[0]) {
            case "reload-config":
                Config.synchronizeConfiguration(null);
                break;
            case "reload-keys":
                ServerProxy.keyDatabase.reloadKeys();
                break;
            default:
                sender.addChatMessage(usage);
                return;
        }
    }
}
