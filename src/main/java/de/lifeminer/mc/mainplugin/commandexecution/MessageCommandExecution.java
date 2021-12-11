package de.lifeminer.mc.mainplugin.commandexecution;

import de.lifeminer.mc.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageCommandExecution implements CommandExecutor {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;


    public MessageCommandExecution(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("msg")){
            if(args.length > 1){
                String receiverName = args[0];
                String message = "";
                for (int i = 1; i < args.length; i++){
                    message = message.concat(" ").concat(args[i]);
                }
                Player receiver = Bukkit.getPlayer(receiverName);
                if(receiver != null){
                    if(sender != receiver){
                        if (sender instanceof Player){
                            String textReceiver = standardConfig.getString("messages.messageReceived").replace("%sender%", ((Player) sender).getDisplayName()).replace("%receiver%", receiver.getDisplayName());
                            receiver.sendMessage(textReceiver + message);
                            String textSender = standardConfig.getString("messages.messageSent").replace("%sender%", ((Player) sender).getDisplayName()).replace("%receiver%", receiver.getDisplayName());
                            sender.sendMessage(textSender + message);
                        } else if (sender instanceof ConsoleCommandSender){
                            receiver.sendMessage(standardConfig.getString("messages.messageReceivedServeradmin") + message);
                        }
                        return true;
                    } else {
                        sender.sendMessage(standardConfig.getString("messages.messageSamePlayer"));
                        return true;
                    }
                } else {
                    sender.sendMessage(standardConfig.getString("messages.messageReceiverNotOnline"));
                    return true;
                }
            } else {
                sender.sendMessage(standardConfig.getString("messages.messageTooFewArguments"));
                return true;
            }
        }

        return false;
    }
}
