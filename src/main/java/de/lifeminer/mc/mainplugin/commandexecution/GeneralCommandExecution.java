package de.lifeminer.mc.mainplugin.commandexecution;

import de.lifeminer.mc.mainplugin.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GeneralCommandExecution implements CommandExecutor {

    private final MainPlugin plugin;
    private final FileConfiguration infoMenuConfig;
    private final FileConfiguration standardConfig;


    private static HashMap<UUID, UUID> pendingTpaRequests= new HashMap<>();

    public GeneralCommandExecution(MainPlugin plugin){
        this.plugin = plugin;
        infoMenuConfig = plugin.getInfoMenuConfig();
        standardConfig = plugin.getConfig();

    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(label.equalsIgnoreCase("info")){
            List<String> infoMenu = infoMenuConfig.getStringList("infoMenu");
            for (String menu : infoMenu) {
                sender.sendMessage(menu);
            }
            List<String> infoMenuCommands = infoMenuConfig.getStringList("commands");

            for (String j : infoMenuCommands){
                if (plugin.getDescription().getCommands().containsKey(j)){
                    Object text = plugin.getDescription().getCommands().get(j).get("description");

                    String commandString = "/" + j;

                    TextComponent command = new TextComponent(commandString);
                    command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandString));
                    command.setColor(ChatColor.YELLOW);

                    TextComponent description = new TextComponent(text.toString());
                    description.setColor(ChatColor.GRAY);

                    TextComponent colon = new TextComponent(": ");
                    colon.setColor(ChatColor.DARK_GRAY);

                    sender.spigot().sendMessage(command, colon , description);
                }
            }
            return true;
        }

        if(cmd.getName().equalsIgnoreCase("ping")){
            if(sender instanceof Player){
                int intPing = ((Player) sender).getPing();
                String ping = Integer.toString((intPing));
                String formattedPing = ping;
                if(intPing <= 20){
                    formattedPing = "§a" + ping + "§7";
                } else if (intPing <= 50){
                    formattedPing = "$2" + ping + "§7";
                } else if (intPing <= 100){
                    formattedPing = "§e" + ping + "§7";
                } else if (intPing > 100){
                    formattedPing = "§4" + ping + "§7";
                }
                String text = standardConfig.getString("ping.message").replace("%ping%", formattedPing);
                sender.sendMessage(text);
                return true;
            }
        }

        if(cmd.getName().equalsIgnoreCase("help")){
            if(sender.isOp()){
                sender.sendMessage("Hier entsteht eine OP-Infoseite!");
                label = "info";
                onCommand(sender, cmd, label, args);
            } else {
                label = "info";
                onCommand(sender, cmd, label, args);
            }
            return true;
        }

        if(cmd.getName().equalsIgnoreCase("clearchat")){
            for(int i = 0; i < 30; i++){
                sender.sendMessage("");
            }
            return true;
        }
        return false;
    }

}
