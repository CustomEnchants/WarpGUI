package WarpGUI;

import WarpGUI.Commands.WarpGuiCommand;
import WarpGUI.Listeners.PlayerListener;
import WarpGUI.Objects.Warp;
import WarpGUI.Utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WarpGUIPlugin extends JavaPlugin {

    private static WarpGUIPlugin instance;

    private FileUtil fileUtil;
    private final ArrayList<Warp> warps = new ArrayList<>();
    private final HashMap<UUID,Integer> selectedPage = new HashMap<>();

    public void onEnable(){
        instance = this;
        fileUtil = new FileUtil();
        getFileUtil().setup(getDataFolder());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("WarpGUI").setExecutor(new WarpGuiCommand());
        getCommand("WarpGUI").setTabCompleter(new WarpGuiCommand());
    }

    public void onDisable(){
        instance = null;
        getSelectedPage().clear();
        getWarps().clear();
    }

    public HashMap<UUID,Integer> getSelectedPage(){
        return selectedPage;
    }

    public ArrayList<Warp> getWarps() {
        return warps;
    }
    public FileUtil getFileUtil(){
        return fileUtil;
    }

    public static WarpGUIPlugin getInstance(){
        return instance;
    }
}
