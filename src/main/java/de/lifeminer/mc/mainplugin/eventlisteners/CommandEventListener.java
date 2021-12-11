package de.lifeminer.mc.mainplugin.eventlisteners;

import de.lifeminer.mc.mainplugin.MainPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEventListener implements Listener {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;

    public CommandEventListener(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
    }

    @EventHandler
    void onCommandProcessing(PlayerCommandPreprocessEvent e){
        if(!e.getPlayer().isOp()){
            if(e.getMessage().split(" ")[0].contains(":")){
                e.setCancelled(true);
                e.getPlayer().sendMessage(standardConfig.getString("colonCommand.message"));
            } else
            if (e.getMessage().split(" ")[0].contains("plugins") || e.getMessage().split(" ")[0].contains("pl")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(standardConfig.getString("plugin.message"));
            }

        }
    }

}
