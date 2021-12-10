package de.lifeminer.mc.mainplugin.commandexecution;

import de.lifeminer.mc.mainplugin.MainPlugin;
import de.lifeminer.mc.mainplugin.SettingsMenuHandler;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


import java.util.Objects;


public class SettingsCommandExecution implements CommandExecutor {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;
    private final SettingsMenuHandler settingsMenuHandler;
    private Inventory gui = null;



    public SettingsCommandExecution (MainPlugin plugin){
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
        settingsMenuHandler = new SettingsMenuHandler(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        if (cmd.getName().equalsIgnoreCase("settings")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length == 0) {
                    settingsMenuHandler.openSettingsMenu(player);
                    return true;
                } else
                if (args.length == 2) {
                    if(args[0].equalsIgnoreCase("noteOnChatMention")){
                        if(args[1].equalsIgnoreCase("true")){
                            userSettingsConfig.set(player.getName() + ".noteOnChat", true);
                            plugin.saveUserSettingsConfig();
                            player.sendMessage(standardConfig.getString("settings.messageSuccessful"));
                            return true;
                        } else
                        if(args[1].equalsIgnoreCase("false")){
                            userSettingsConfig.set(player.getName() + ".noteOnChat", false);
                            plugin.saveUserSettingsConfig();
                            player.sendMessage(standardConfig.getString("settings.messageSuccessful"));
                            return true;
                        } else {
                            player.sendMessage(standardConfig.getString("settings.messageSyntax"));
                            return false;
                        }
                    }
                } else
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")){
                        sender.sendMessage(standardConfig.getString("settings.info.message"));
                        TextComponent username = new TextComponent(standardConfig.getString("settings.info.username"));
                        TextComponent uuid = new TextComponent(standardConfig.getString("settings.info.uuid"));
                        TextComponent nickname = new TextComponent(standardConfig.getString("settings.info.nickname"));
                        TextComponent ipTag = new TextComponent(standardConfig.getString("settings.info.ip"));
                        TextComponent noteOnChat = new TextComponent(standardConfig.getString("settings.info.noteOnChat"));
                        TextComponent home = new TextComponent(standardConfig.getString("settings.info.home"));

                        username.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(standardConfig.getString("settings.info.usernameHover"))));
                        uuid.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(standardConfig.getString("settings.info.uuidHover"))));
                        nickname.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(standardConfig.getString("settings.info.nicknameHover"))));
                        ipTag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(standardConfig.getString("settings.info.ipHover"))));
                        noteOnChat.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(standardConfig.getString("settings.info.noteOnChatHover"))));
                        home.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(standardConfig.getString("settings.info.homeHover"))));

                        sender.spigot().sendMessage(username, new TextComponent(standardConfig.getString("settings.info.colon") + sender.getName()));
                        sender.spigot().sendMessage(uuid, new TextComponent(standardConfig.getString("settings.info.colon") + ((Player) sender).getUniqueId()));
                        sender.spigot().sendMessage(nickname, new TextComponent(standardConfig.getString("settings.info.colon") + ((Player) sender).getDisplayName()));
                        sender.spigot().sendMessage(ipTag, new TextComponent(standardConfig.getString("settings.info.colon") + ((Player) sender).getAddress().getHostName()));
                        sender.spigot().sendMessage(noteOnChat, new TextComponent(standardConfig.getString("settings.info.colon")
                                + (userSettingsConfig.getBoolean(sender.getName() + ".noteOnChat")
                                ? standardConfig.getString("settings.info.onTrue") : standardConfig.getString("settings.info.onFalse"))));

                        if(userSettingsConfig.isSet(sender.getName() + ".homeLocation.x")){
                            TextComponent homeString = new TextComponent(standardConfig.getString("settings.info.homeInfo")
                                    .replaceAll("%X%", Integer.toString(userSettingsConfig.getInt(sender.getName() + ".homeLocation.x")))
                                    .replaceAll("%Y%", Integer.toString(userSettingsConfig.getInt(sender.getName() + ".homeLocation.y")))
                                    .replaceAll("%Z%", Integer.toString(userSettingsConfig.getInt(sender.getName() + ".homeLocation.z")))
                                    .replaceAll("%world%", userSettingsConfig.getString(sender.getName() + ".homeLocation.world")));

                            sender.spigot().sendMessage(home, new TextComponent(standardConfig.getString("settings.info.colon")), homeString);
                        } else {
                            sender.spigot().sendMessage(home, new TextComponent(standardConfig.getString("settings.info.colon") + standardConfig.getString("settings.info.noHomeSet")));
                        }
                        return true;
                    }
                } else {
                    player.sendMessage(standardConfig.getString("settings.messageSyntax"));
                    return false;
                }
            }
        }

        if(cmd.getName().equalsIgnoreCase("nickname")){
            if(sender.isOp()){
                if(args.length == 2){
                    String playerName = args[0];
                    String nickname = args[1];

                    userSettingsConfig.set(playerName + ".nickname", plugin.replaceChatColor(nickname));
                    plugin.saveUserSettingsConfig();

                    if(Bukkit.getPlayer(playerName) != null && Bukkit.getPlayer(playerName).isOnline()){
                        Bukkit.getPlayer(playerName).setDisplayName(plugin.replaceChatColor(nickname));
                    }
                    String text = standardConfig.getString("nickname.message").replaceAll("%player%" , playerName).replaceAll("%nickname%", nickname);
                    sender.sendMessage(text);
                    return true;
                } else {
                    sender.sendMessage(standardConfig.getString("nickname.messageSyntax"));
                }
            }
        }

        if(cmd.getName().equalsIgnoreCase("resetnickname")){
            if(args.length == 1){
                Player player = Bukkit.getPlayer(args[0]);
                standardConfig.set(player.getName() + ".nickname", player.getName());
                if(player.isOnline()){
                    player.setDisplayName(player.getName());
                }
                String text = standardConfig.getString("resetnickname.message").replaceAll("%player%" , player.getName());
                sender.sendMessage(text);
                return true;
            } else {
                sender.sendMessage(standardConfig.getString("resetnickname.messageSyntax"));
            }
        }


        return false;
    }



}
