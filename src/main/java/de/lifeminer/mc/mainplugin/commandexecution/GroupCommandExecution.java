package de.lifeminer.mc.mainplugin.commandexecution;

import de.lifeminer.mc.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupCommandExecution implements CommandExecutor {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration groupsConfig;


    public GroupCommandExecution(MainPlugin plugin) {
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        groupsConfig = plugin.getGroupsConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("group")) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    String groupTag = args[1];
                    List<String> members = new ArrayList<>();
                    if (!groupsConfig.contains(groupTag)) {
                        members.add(sender.getName());
                        groupsConfig.set(groupTag + ".members", members);
                        groupsConfig.set(groupTag + ".owner", sender.getName());
                        plugin.saveGroupsConfig();

                        sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyAddedGroup").replace("%groupTag%", groupTag));
                    } else {
                        sender.sendMessage(standardConfig.getString("groups.messageGroupAlreadyExists"));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("listmembers")) {
                    String groupTag = args[1];
                    if (groupsConfig.contains(groupTag) && (sender.isOp() || groupsConfig.getString(groupTag + ".owner").equals(sender.getName()))) {
                        List<String> members = groupsConfig.getStringList(groupTag + ".members");
                        sender.sendMessage(standardConfig.getString("groups.messageListMembers"));
                        for (String s : members) {
                            sender.sendMessage(standardConfig.getString("groups.bullet") + s);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
                if (args[0].equalsIgnoreCase("remove")){
                    String groupTag = args[1];
                    if(groupsConfig.contains(args[1]) && (groupsConfig.getString(groupTag + ".owner").equals(sender.getName()) || sender.isOp())){
                            groupsConfig.set(groupTag, null);
                            plugin.saveGroupsConfig();
                            sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyRemovedGroup").replace("%groupTag%", groupTag));
                            return true;
                    } else {
                        sender.sendMessage(standardConfig.getString("groups.messageGroupNotFound"));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("leave")){
                    String groupTag = args[1];
                    List<String> members = groupsConfig.getStringList(groupTag + ".members");
                    if(!groupsConfig.getString(groupTag + ".owner").equals(sender.getName())){
                        if(members.contains(sender.getName())){
                            members.remove(sender.getName());
                            groupsConfig.set(groupTag + ".members", members);
                            plugin.saveGroupsConfig();
                            sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyLeft").replace("%groupTag%", groupTag));
                            return true;
                        } else {
                            sender.sendMessage(standardConfig.getString("groups.messageGroupNotFound"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(standardConfig.getString("groups.messageRemoveOwner"));
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("list")){
                    if(args[1].equalsIgnoreCase("all")){
                        if (sender.isOp()) {
                            Set<String> groups = groupsConfig.getKeys(false);
                            sender.sendMessage(standardConfig.getString("groups.messageList"));
                            for (String s : groups) {
                                sender.sendMessage(standardConfig.getString("groups.bullet") + s);
                            }
                            return true;
                        }
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("member")) {
                    String groupTag = args[3];
                    if (groupsConfig.contains(groupTag)) {
                        if (groupsConfig.getString(groupTag + ".owner").equals(sender.getName()) || sender.isOp()) {
                            if (args[1].equalsIgnoreCase("add")) {
                                if (Bukkit.getPlayer(args[2]) != null) {
                                    Player newMember = Bukkit.getPlayer(args[2]);
                                    if(!groupsConfig.getStringList(groupTag + ".members").contains(newMember.getName())){
                                        List<String> members = groupsConfig.getStringList(groupTag + ".members");
                                        members.add(newMember.getName());
                                        groupsConfig.set(groupTag + ".members", members);
                                        plugin.saveGroupsConfig();
                                        sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyAddedMember").replace("%player%", newMember.getName()).replace("%groupTag%", groupTag));
                                        return true;
                                    } else {
                                        sender.sendMessage(standardConfig.getString("groups.messagePlayerAlreadyInGroup"));
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(standardConfig.getString("groups.messagePlayerNotFound"));
                                    return true;
                                }
                            }
                            if (args[1].equalsIgnoreCase("remove")) {
                                String member = args[2];
                                if(!groupsConfig.getString(groupTag + ".owner").equals(member)){
                                    if (groupsConfig.getStringList(groupTag + ".members").contains(member)) {
                                        List<String> newMemberList = groupsConfig.getStringList(groupTag + ".members");
                                        newMemberList.remove(member);
                                        groupsConfig.set(groupTag + ".members", newMemberList);
                                        plugin.saveGroupsConfig();
                                        sender.sendMessage(standardConfig.getString("groups.messageSuccessfullyRemovedMember").replace("%player%", member));
                                        return true;
                                    } else {
                                        sender.sendMessage(standardConfig.getString("groups.messagePlayerNotFound"));
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(standardConfig.getString("groups.messageRemoveOwner"));
                                    return true;
                                }
                            }
                        } else {
                            sender.sendMessage(standardConfig.getString("groups.noPermissionForGroup"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(standardConfig.getString("groups.messageGroupNotFound"));
                        return true;
                    }
                }
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    Set<String> groups = groupsConfig.getKeys(false);
                    sender.sendMessage(standardConfig.getString("groups.messageList"));
                    for (String groupTag : groups){
                        if(groupsConfig.getStringList(groupTag + ".members").contains(sender.getName())){
                            sender.sendMessage(standardConfig.getString("groups.bullet" + groupTag));
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("help")){
                    List<String> message = standardConfig.getStringList("groups.help.message");
                    for (String s : message){
                        sender.sendMessage(s);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}

