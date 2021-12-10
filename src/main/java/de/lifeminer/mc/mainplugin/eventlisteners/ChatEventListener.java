package de.lifeminer.mc.mainplugin.eventlisteners;

import de.lifeminer.mc.mainplugin.MainPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.logging.Level;

public class ChatEventListener implements Listener {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;

    public ChatEventListener(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
    }

    @EventHandler
    void onPlayerChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        String message = e.getMessage();
        String[] words = message.split(" ");
        for (String word : words) {
            Player temp = Bukkit.getPlayer(word);
            if (temp != null && temp != player) {
                if (userSettingsConfig.getBoolean(temp.getName() + ".noteOnChat")){
                    Location loc = temp.getLocation();
                    temp.playNote(loc, Instrument.XYLOPHONE, new Note(4));
                    break;
                }
            }

            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            for(Player j : onlinePlayers){
                if(j.getDisplayName().equalsIgnoreCase(word) && userSettingsConfig.getBoolean(j.getName() + ".noteOnChat")){
                    j.playNote(j.getLocation(), Instrument.XYLOPHONE, new Note(4));
                    break;
                }
            }

            if (word.equals("@all") && player.isOp()) {
                for(Player j : onlinePlayers){
                    j.playNote(j.getLocation(), Instrument.COW_BELL, new Note(10));
                    break;
                }
            }
        }
        String prefix = standardConfig.getString("chat.prefix").replaceAll("%player%", player.getDisplayName());

        String textMessage = plugin.replaceChatColor(e.getMessage()).replaceAll("@all ", "");

        TextComponent clickAbleMessage = new TextComponent(prefix);
        TextComponent normalMessage = new TextComponent(textMessage);

        clickAbleMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
        clickAbleMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/msg " + player.getName())));
        Bukkit.spigot().broadcast(clickAbleMessage, normalMessage);
        Bukkit.getLogger().log(Level.INFO, prefix + message);

        e.setCancelled(true);


    }
}
