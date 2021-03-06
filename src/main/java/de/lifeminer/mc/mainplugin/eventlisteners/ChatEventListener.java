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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class ChatEventListener implements Listener {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;
    private final FileConfiguration groupsConfig;

    public ChatEventListener(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
        groupsConfig = plugin.getGroupsConfig();
    }

    @EventHandler
    void onPlayerChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        String message = e.getMessage();
        String[] words = message.split(" ");
        if(!message.startsWith("@")){
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            this.notifyPlayer(player, words, onlinePlayers);

            for (String word : words) {
                if (word.equals("!all") && player.isOp()) {
                    message = message.replace("!all ", "");
                    for(Player j : onlinePlayers){
                        j.playNote(j.getLocation(), Instrument.COW_BELL, new Note(10));
                        break;
                    }
                }
            }

            String prefix = standardConfig.getString("chat.prefix").replace("%player%", player.getDisplayName());

            String textMessage = plugin.replaceChatColor(message);

            TextComponent clickAbleMessage = new TextComponent(prefix);
            TextComponent normalMessage = new TextComponent(textMessage);

            clickAbleMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
            clickAbleMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/msg " + player.getName())));
            Bukkit.spigot().broadcast(clickAbleMessage, normalMessage);
            Bukkit.getLogger().log(Level.INFO, prefix + message);
        } else {

            String groupTag = words[0].substring(1);
            if(groupsConfig.contains(groupTag) && groupsConfig.getStringList(groupTag + ".members").contains(player.getName())){
                List<String> members = groupsConfig.getStringList(groupTag + ".members");
                message = message.replace("@" + groupTag + " ", "");
                message = plugin.replaceChatColor(message);
                Collection<Player> playersInGroup = new ArrayList<>();
                for(String s : members){
                    if(Bukkit.getPlayer(s) != null){
                        Player receiver = Bukkit.getPlayer(s);
                        playersInGroup.add(receiver);
                        if(receiver.isOnline()){
                            receiver.sendMessage(standardConfig.getString("groups.prefix").replace("%group%", groupTag).replace("%sender%", player.getDisplayName()) + message);
                        }
                    }
                }
                if(!playersInGroup.isEmpty()){
                    this.notifyPlayer(player, words, playersInGroup);
                }



            } else {
                player.sendMessage(standardConfig.getString("groups.messageGroupNotFound"));
            }
        }

        e.setCancelled(true);


    }

    private void notifyPlayer(Player player, String[] words, Collection<? extends Player> targetedPlayers){
        for (String word : words) {
            Player temp = Bukkit.getPlayer(word);
            if (temp != null && temp != player && targetedPlayers.contains(temp)) {
                if (userSettingsConfig.getBoolean(temp.getName() + ".noteOnChat")){
                    Location loc = temp.getLocation();
                    temp.playNote(loc, Instrument.XYLOPHONE, new Note(4));
                    break;
                }
            }

            for(Player j : targetedPlayers){
                if(j.getDisplayName().equalsIgnoreCase(word) && userSettingsConfig.getBoolean(j.getName() + ".noteOnChat")){
                    j.playNote(j.getLocation(), Instrument.XYLOPHONE, new Note(4));
                    break;
                }
            }
        }
    }
}
