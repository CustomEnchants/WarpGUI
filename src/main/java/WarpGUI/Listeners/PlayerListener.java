package WarpGUI.Listeners;

import WarpGUI.Objects.Warp;
import WarpGUI.WarpGUIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.Optional;


public class PlayerListener implements Listener {

    private final WarpGUIPlugin instance = WarpGUIPlugin.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getWhoClicked() == null) return;
        if(event.getClickedInventory() == null) return;
        if(instance.getFileUtil().warpInventories.entrySet().stream().noneMatch(entry -> entry.getValue().getName().equalsIgnoreCase(event.getView().getTitle()))){
            return;
        }
        event.setCancelled(true);
        if(event.getCurrentItem() == null) return;
        if(event.getCurrentItem().isSimilar(instance.getFileUtil().nextPageItem)){
            int page = instance.getSelectedPage().getOrDefault(event.getWhoClicked().getUniqueId(),1);
            int nextPage = page + 1;
            if(!instance.getFileUtil().doesWarpInventoryPageExist(nextPage)){
                return;
            }
            event.getWhoClicked().openInventory(instance.getFileUtil().warpInventories.get(nextPage));
            instance.getSelectedPage().put(event.getWhoClicked().getUniqueId(),nextPage);
            return;
        }
        if(event.getCurrentItem().isSimilar(instance.getFileUtil().previousPageItem)){
            int page = instance.getSelectedPage().getOrDefault(event.getWhoClicked().getUniqueId(),1);
            int previousPage = page - 1;
            if(!instance.getFileUtil().doesWarpInventoryPageExist(previousPage)){
                return;
            }
            event.getWhoClicked().openInventory(instance.getFileUtil().warpInventories.get(previousPage));
            instance.getSelectedPage().put(event.getWhoClicked().getUniqueId(),previousPage);
            return;
        }
        Optional<Warp> warpOptional = instance.getWarps().stream().filter(warp -> warp.getDisplayItem().isSimilar(event.getCurrentItem())).findFirst();
        if(!warpOptional.isPresent()){
            return;
        }
        event.getWhoClicked().closeInventory();
        Bukkit.dispatchCommand(event.getWhoClicked(),"warp "+warpOptional.get());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event){
        if(event.getWhoClicked() == null) return;
        if(instance.getFileUtil().warpInventories.entrySet().stream().noneMatch(entry -> entry.getValue().getName().equalsIgnoreCase(event.getView().getTitle()))){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        instance.getSelectedPage().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerCommandPreProcessEvent(PlayerCommandPreprocessEvent event){
        String message = event.getMessage().toLowerCase();
        if(!message.startsWith("/warp") && !message.contains("/warpgui")){
            return;
        }
        if(instance.getFileUtil().warpInventories.isEmpty()){
            return;
        }
        if(!instance.getFileUtil().doesWarpInventoryPageExist(1)){
            return;
        }
        String[] split = message.split(" ");
        if(split.length > 1){
            return;
        }
        event.setCancelled(true);
        event.getPlayer().openInventory(instance.getFileUtil().warpInventories.get(1));
    }
}
