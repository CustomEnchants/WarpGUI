package WarpGUI.Objects;

import WarpGUI.WarpGUIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    public final WarpGUIPlugin instance = WarpGUIPlugin.getInstance();
    private final String name;
    private final String desc;
    private final String args;
    private final String permission;

    public SubCommand(String name, String desc, String args, String permission) {
        this.name = name;
        this.desc = desc;
        this.args = args;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }

    public String getArgs() {
        return args;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPermissionRequired() {
        return !getPermission().isEmpty();
    }


    public abstract void run(CommandSender cs, String[] args);

    public String fixColour(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public boolean hasAccess(CommandSender cs){
        return getPermission().isEmpty() || cs.hasPermission(getPermission());
    }


}
