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
    private final FileConfiguration groupsConfig;


    public MessageCommandExecution(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        groupsConfig = plugin.getGroupsConfig();
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
                            String textReceiver = standardConfig.getString("messages.messageReceived").replaceAll("%sender%", ((Player) sender).getDisplayName()).replaceAll("%receiver%", receiver.getDisplayName());
                            receiver.sendMessage(textReceiver + message);
                            String textSender = standardConfig.getString("messages.messageSent").replaceAll("%sender%", ((Player) sender).getDisplayName()).replaceAll("%receiver%", receiver.getDisplayName());
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
        if(cmd.getName().equalsIgnoreCase("group")){
            if(args.length == 2){
                if(args[0].equalsIgnoreCase("add")){
                    String groupTag = args[1];
                    List<String> members = new ArrayList<>();
                    members.add(sender.getName());
                    groupsConfig.set(groupTag + ".members", members);
                    plugin.saveGroupsConfig();

                    sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyAddedGroup").replaceAll("%groupTag%", groupTag));

                    return true;
                } else {
                    return false;
                }
            } else
                if (args.length == 4){
                if(args[0].equalsIgnoreCase("member")){
                    String groupTag = args[3];
                    if(groupsConfig.getString(groupTag + ".owner").equals(sender.getName())){
                        if(args[1].equalsIgnoreCase("add")){
                            if(Bukkit.getPlayer(args[2]) != null){
                                Player newMember = Bukkit.getPlayer(args[2]);
                                if(groupsConfig.contains(groupTag + ".members")){
                                    groupsConfig.set(groupTag + ".members", groupsConfig.getStringList(groupTag + ".members").add(newMember.getName()));
                                    plugin.saveGroupsConfig();
                                    sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyAddedMember").replaceAll("%player%", newMember.getName()));
                                } else {
                                    sender.sendMessage(standardConfig.getString("groups.messageGroupNotFound"));
                                }
                            } else {
                                sender.sendMessage(standardConfig.getString("groups.messagePlayerNotFound"));
                            }
                        }
                        if(args[1].equalsIgnoreCase("remove")){
                            String member = args[2];
                            if(groupsConfig.getStringList(groupTag + ".members").contains(member)){
                                List<String> newMemberList = groupsConfig.getStringList(groupTag + ".members");
                                newMemberList.remove(member);
                                groupsConfig.set(groupTag + ".members", newMemberList);

                                sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyRemovedMember").replaceAll("%player%", member));
                            } else {
                                sender.sendMessage(standardConfig.getString("groups.messagePlayerNotFoung"));
                            }
                        }
                    } else {
                        sender.sendMessage(standardConfig.getString("groups.noPermissionForGroup"));
                    }
                }
            } else
                if (args.length == 1){
                    if(args[0].equalsIgnoreCase("list")){
                        if(sender.isOp()){
                            Set<String> groups = groupsConfig.getKeys(false);
                            for(String s : groups){
                                sender.sendMessage(standardConfig.getString("groups.messageList"));
                                sender.sendMessage(standardConfig.getString("groups.bullet") + s);
                            }
                        } else {
                            return false;
                        }
                    }
                }

        }
        return false;
    }
}
