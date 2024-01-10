package WarpGUI.Objects;

import org.bukkit.inventory.ItemStack;

public class Warp {

    private final String name;
    private final int inventorySlot;
    private final int page;
    private final ItemStack displayItem;

    public Warp(String name, ItemStack displayItem, int itemSlot,int page){
        this.name = name;
        this.inventorySlot = itemSlot;
        this.displayItem = displayItem;
        this.page = page;
    }

    public ItemStack getDisplayItem(){
        return displayItem;
    }

    public int getInventorySlot(){
        return inventorySlot;
    }

    public String getName(){
        return name;
    }

    public int getPage(){
        return page;
    }
}
