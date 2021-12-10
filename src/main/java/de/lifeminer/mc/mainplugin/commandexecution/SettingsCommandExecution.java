package de.lifeminer.mc.mainplugin.commandexecution;

import de.lifeminer.mc.mainplugin.MainPlugin;
import de.lifeminer.mc.mainplugin.SettingsMenuHandler;
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
                    if(args[0].equalsIgnoreCase("toneOnChatMention")){
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

                    if(Objects.requireNonNull(Bukkit.getPlayer(playerName)).isOnline()){
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
