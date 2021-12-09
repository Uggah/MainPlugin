package de.lifeminer.mc.MainPlugin.eventlisteners;

import de.lifeminer.mc.MainPlugin.MainPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinQuitEventListener implements Listener {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;

    public JoinQuitEventListener(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        plugin.createUserSettings(player);

        if(userSettingsConfig.contains(player.getName() + ".nickname")){
            player.setDisplayName(userSettingsConfig.getString(player.getName() + ".nickname"));
        }

        String text = standardConfig.getString("joinEvent.message").replaceAll("%player%", player.getDisplayName());
        e.setJoinMessage(text);

    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        String text = standardConfig.getString("quitEvent.message").replaceAll("%player%", player.getDisplayName());
        e.setQuitMessage(text);
    }






}
