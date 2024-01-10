package WarpGUI.Commands;

import WarpGUI.Commands.Sub.Reload;
import WarpGUI.Objects.SubCommand;
import WarpGUI.WarpGUIPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class WarpGuiCommand implements CommandExecutor, TabCompleter {

    private final WarpGUIPlugin instance = WarpGUIPlugin.getInstance();
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public WarpGuiCommand(){
        subCommands.add(new Reload());
    }

    public boolean onCommand(CommandSender cs, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("WarpGui")) {
            if (args.length == 0) {
                sendHelp(cs, cmd, 1);
                return false;
            }
            ArrayList<String> a = new ArrayList<>(Arrays.asList(args));
            a.remove(0);
            Optional<SubCommand> subCommandOptional = subCommands.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(args[0])).findFirst();
            if(!subCommandOptional.isPresent()){
                sendHelp(cs, cmd, 1);
                return false;
            }
            subCommandOptional.get().run(cs, a.toArray(new String[0]));
            return false;
        }
        return true;
    }

    private void sendHelp(CommandSender cs, Command cmd, int page) {
        new BukkitRunnable() {
            public void run() {
                cs.sendMessage(instance.getFileUtil().fixColour("&6_________________.[ &2WarpGUI Help Page &c%page% &6]._________________".replace("%page%", "" + page)));
                int perPage = 7;
                int maxPage = subCommands.size() == 0 ? 1 : Math.max((int) Math.ceil((double) subCommands.size() / perPage), 1);
                int actualPage = Math.min(page, maxPage);
                int min = actualPage == 1 ? 0 : actualPage * perPage - perPage;
                int max = actualPage == 1 ? perPage : min + perPage;
                ArrayList<SubCommand> subCommandsForPlayer = subCommands.stream().filter(subCommand -> subCommand.hasAccess(cs)).collect(Collectors.toCollection(ArrayList::new));
                for (int i = min; i < max; i++) {
                    if (subCommandsForPlayer.size() <= i) break;
                    SubCommand subCommand = subCommandsForPlayer.get(i);
                    cs.sendMessage(instance.getFileUtil().fixColour("&b/" + cmd.getName() + " &7" + subCommand.getName() + " &7" + subCommand.getArgs() + " &7"+subCommand.getDescription()));
                }
                cancel();
            }
        }.runTaskAsynchronously(instance);
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("WarpGUI")) {
            return subCommands.stream().filter(subCommand -> subCommand.hasAccess(cs)).map(SubCommand::getName).collect(Collectors.toCollection(ArrayList::new));
        }
        return null;
    }
}
