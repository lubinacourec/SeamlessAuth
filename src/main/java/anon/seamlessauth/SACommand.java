package anon.seamlessauth;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import anon.seamlessauth.skin.ClientSkinHandler;
import anon.seamlessauth.util.SkinMixinHelper;

public class SACommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "seamlessauth";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <reload-skin|requery-all>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        IChatComponent usage = new ChatComponentText(EnumChatFormatting.RED + "usage: " + getCommandUsage(sender));
        if (args.length != 1) {
            sender.addChatMessage(usage);
            return;
        }

        switch (args[0]) {
            case "reload-skin":
                Config.synchronizeConfiguration(null);
                ClientSkinHandler.instance.reload();
                SkinMixinHelper.loadOwnSkin();
                break;
            case "requery-all":
                ClientSkinHandler.instance.queryCache.keySet()
                    .forEach(uuid -> ClientSkinHandler.instance.querySkin(uuid, null));
                break;
            default:
                sender.addChatMessage(usage);
                return;
        }
    }
}
