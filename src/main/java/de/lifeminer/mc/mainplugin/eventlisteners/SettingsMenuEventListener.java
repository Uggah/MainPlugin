package de.lifeminer.mc.mainplugin.eventlisteners;

import de.lifeminer.mc.mainplugin.MainPlugin;
import de.lifeminer.mc.mainplugin.SettingsMenuHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SettingsMenuEventListener implements Listener {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;
    private final SettingsMenuHandler settingsMenuHandler;

    public SettingsMenuEventListener(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
        settingsMenuHandler = new SettingsMenuHandler(plugin);
    }

    @EventHandler
    void settingsClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        if (e.getView().getTitle().equals(standardConfig.getString("settings.guiTitle"))){
            e.setCancelled(true);
            if(e.getSlot() == 0){
                if(userSettingsConfig.getBoolean(player.getName() + ".noteOnChat")){
                    userSettingsConfig.set(player.getName() + ".noteOnChat", false);
                    plugin.saveUserSettingsConfig();
                    settingsMenuHandler.updateSettings(inv, player);

                    player.sendMessage(standardConfig.getString("settings.messageSuccessful"));
                } else {
                    userSettingsConfig.set(player.getName() + ".noteOnChat", true);
                    plugin.saveUserSettingsConfig();
                    settingsMenuHandler.updateSettings(inv, player);

                    player.sendMessage(standardConfig.getString("settings.messageSuccessful"));
                }
            }
        }
    }



}
