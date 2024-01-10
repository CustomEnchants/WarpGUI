package WarpGUI.Utils;

import WarpGUI.Objects.Warp;
import WarpGUI.WarpGUIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileUtil {

    private final WarpGUIPlugin instance = WarpGUIPlugin.getInstance();

    private File conf;

    public HashMap<Integer,Inventory> warpInventories = new HashMap<>();
    public ItemStack nextPageItem;
    public ItemStack previousPageItem;

    public ArrayList<String> warpGuiReloadCommandNoPermission = new ArrayList<>();
    public ArrayList<String> warpGuiReloadCommandReloaded = new ArrayList<>();

    public String fixColour(String input){
        return ChatColor.translateAlternateColorCodes('&',input);
    }

    private ArrayList<String> fixColours(List<String> input){
        ArrayList<String> result = new ArrayList<>();
        input.forEach(string -> result.add(fixColour(string)));
        return result;
    }

    private void saveWarp(Warp warp){
        FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
        ConfigurationSection warps = config.getConfigurationSection("Warps");
        ConfigurationSection warpsSection = warps.createSection(warp.getName());
        warpsSection.set("name",warp.getName());
        warpsSection.set("displayItem",warp.getDisplayItem());
        warpsSection.set("inventorySlot",warp.getInventorySlot());
        warpsSection.set("page",warp.getPage());
        try{
            config.save(conf);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean doesWarpInventoryPageExist(int page){
        return warpInventories.containsKey(page);
    }

    public void createWarpInventoryPage(int page){
        if(doesWarpInventoryPageExist(page)){
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
        ConfigurationSection warpInventory = config.getConfigurationSection("Warp-Inventory");
        Inventory inventory = Bukkit.createInventory(null,warpInventory.getInt("size"),fixColour(warpInventory.getString("name")));
        ConfigurationSection warpInventoryFillerBlock = warpInventory.getConfigurationSection("fillerBlock");
        ItemStack fillerBlock = warpInventoryFillerBlock.getItemStack("itemStack");
        warpInventoryFillerBlock.getIntegerList("itemSlots").forEach(slot -> inventory.setItem(slot,fillerBlock));

        ConfigurationSection warpInventoryNextPage = warpInventory.getConfigurationSection("nextPage");
        warpInventoryNextPage.getIntegerList("itemSlots").forEach(slot -> inventory.setItem(slot,nextPageItem));

        ConfigurationSection warpInventoryPreviousPage = warpInventory.getConfigurationSection("previousPage");
        warpInventoryPreviousPage.getIntegerList("itemSlots").forEach(slot -> inventory.setItem(slot, previousPageItem));


        warpInventories.put(page,inventory);
    }

    private void loadWarps(boolean reload){
        if(reload){
            instance.getWarps().clear();
            warpInventories.clear();
        }
        boolean madeChanges = false;
        FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
        ConfigurationSection warps = config.getConfigurationSection("Warps");
        for(String string : warps.getKeys(false)){
            ConfigurationSection section = warps.getConfigurationSection(string);
            Warp warp = new Warp(section.getString("name"),section.getItemStack("displayItem"),section.getInt("inventorySlot"),section.getInt("page"));
            instance.getWarps().add(warp);
            if(!doesWarpInventoryPageExist(warp.getPage())){
                createWarpInventoryPage(warp.getPage());
            }
            warpInventories.get(warp.getPage()).setItem(warp.getInventorySlot(),warp.getDisplayItem());
        }
        if(!madeChanges){
            return;
        }
        try{
            config.save(conf);
        }catch(IOException e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void loadValues(boolean reload){
        FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
        ConfigurationSection warpGuiCommand = config.getConfigurationSection("WarpGuiCommand");
        ConfigurationSection warpGuiReloadCommand = warpGuiCommand.getConfigurationSection("Reload");
        warpGuiReloadCommandNoPermission = fixColours(warpGuiReloadCommand.getStringList("noPermission"));
        warpGuiReloadCommandReloaded = fixColours(warpGuiReloadCommand.getStringList("reloaded"));

        ConfigurationSection warpInventory = config.getConfigurationSection("Warp-Inventory");
        ConfigurationSection warpInventoryNextPage = warpInventory.getConfigurationSection("nextPage");
        nextPageItem = warpInventoryNextPage.getItemStack("itemStack");
        ConfigurationSection warpInventoryPreviousPage = warpInventory.getConfigurationSection("previousPage");
        previousPageItem = warpInventoryPreviousPage.getItemStack("itemStack");
        loadWarps(reload);
    }

    public void setup(File dir){
        if(!dir.exists()){
            dir.mkdirs();
        }
        conf = new File(dir + File.separator + "Config.yml");
        if(!conf.exists()){
            try{
                FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
                config.set("Author","CustomEnchants");
                ConfigurationSection warpGuiCommand = config.createSection("WarpGuiCommand");
                ConfigurationSection warpGuiReloadCommand = warpGuiCommand.createSection("Reload");
                warpGuiReloadCommand.set("noPermission",Collections.singletonList("&b&lWarpGUI &cYou do not have permission to execute this command!"));
                warpGuiReloadCommand.set("reloaded",Collections.singletonList("&b&lWarpGUI &7You have reloaded the config"));

                ConfigurationSection warpInventory = config.createSection("Warp-Inventory");
                warpInventory.set("name","&l» &rSelect a Warp...");
                warpInventory.set("size",27);
                warpInventory.set("previousPage.itemStack",getDefaultPreviousPageItem());
                warpInventory.set("previousPage.itemSlots",Collections.singletonList(18));
                warpInventory.set("nextPage.itemStack",getDefaultNextPageItem());
                warpInventory.set("nextPage.itemSlots",Collections.singletonList(26));

                warpInventory.set("fillerBlock.itemStack",getDefaultFillerBlock());
                warpInventory.set("fillerBlock.itemSlots", Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,19,20,21,22,23,24,25));


                config.createSection("Warps");
                config.save(conf);
                saveWarp(new Warp("Example",getExampleWarpItem(),10,1));
            }catch(IOException e){
                System.out.println(e.getLocalizedMessage());
            }
        }
        loadValues(false);
    }

    private ItemStack getExampleWarpItem(){
        ItemStack itemStack = new ItemStack(Material.GRASS);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§eExample");
        itemMeta.setLore(Collections.singletonList("§eExample lore"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack getDefaultPreviousPageItem(){
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6§l* §b§lGo back to previous page §6§l*");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack getDefaultNextPageItem(){
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6§l* §b§lGo to next page §6§l*");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack getDefaultFillerBlock(){
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)15);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§d");
        itemMeta.setLore(Arrays.asList("",""));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }



}
