package WarpGUI.Commands.Sub;

import WarpGUI.Objects.SubCommand;
import org.bukkit.command.CommandSender;

public class Reload extends SubCommand {

    public Reload() {
        super("Reload", "Reload the configuration", "", "WarpGui.reload");
    }

    @Override
    public void run(CommandSender cs, String[] args) {
        if(!cs.hasPermission(getPermission())){
            instance.getFileUtil().warpGuiReloadCommandNoPermission.forEach(cs::sendMessage);
            return;
        }
        instance.getFileUtil().loadValues(true);
        instance.getFileUtil().warpGuiReloadCommandReloaded.forEach(cs::sendMessage);
    }
}
