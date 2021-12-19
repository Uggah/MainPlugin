package de.lifeminer.mc.mainplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsMenuHandler {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;

    public SettingsMenuHandler(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
    }

    public void openSettingsMenu(Player player){
        Inventory gui = getSettingsInventory(player);

        player.openInventory(gui);
    }

    private Inventory getSettingsInventory(Player player){
        Inventory gui = Bukkit.createInventory(player, 9, standardConfig.getString("settings.guiTitle"));
        ItemStack noteOnChat;
        {
            if(userSettingsConfig.getBoolean(player.getUniqueId() + ".noteOnChat")){
                String loreString = standardConfig.getString("settings.noteOnChatTitleActiveLore");
                String[] splittedLore = loreString.split("&break;");
                String displayName = "§f" + standardConfig.getString("settings.noteOnChatTitleActive");
                noteOnChat = getNewItemStack(Material.NOTE_BLOCK, displayName, splittedLore, true);
            } else {
                String loreString = standardConfig.getString("settings.noteOnChatTitleInactiveLore");
                String[] splittedLore = loreString.split("&break;");
                String displayName = "§f" + standardConfig.getString("settings.noteOnChatTitleInactive");
                noteOnChat = getNewItemStack(Material.NOTE_BLOCK, displayName, splittedLore, false);
            }
        }

        ItemStack[] menuItems = {noteOnChat};
        gui.setContents(menuItems);

        return gui;
    }

    private ItemStack getNewItemStack(Material itemOrBlock, String displayName, String[] splittedLore, boolean enchanted){
        ItemStack out;
        out = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta outMeta = out.getItemMeta();
        outMeta.setDisplayName(displayName);
        ArrayList<String> outLore = new ArrayList<>();
        outLore.addAll(Arrays.asList(splittedLore));
        outMeta.setLore(outLore);
        outMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if(enchanted){
            outMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        }
        out.setItemMeta(outMeta);
        return out;
    }

    public void updateSettings(Inventory settings, Player player){
        Inventory gui = getSettingsInventory(player);
        settings.setContents(gui.getStorageContents());
    }

}
