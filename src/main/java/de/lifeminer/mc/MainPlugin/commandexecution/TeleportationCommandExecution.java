package de.lifeminer.mc.MainPlugin.commandexecution;

import de.lifeminer.mc.MainPlugin.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class TeleportationCommandExecution implements CommandExecutor {

    private final MainPlugin plugin;
    private final FileConfiguration standardConfig;
    private final FileConfiguration userSettingsConfig;


    private static HashMap<String, String> pendingTpaRequests= new HashMap<>();

    public TeleportationCommandExecution (MainPlugin plugin){
        this.plugin = plugin;
        standardConfig = plugin.getConfig();
        userSettingsConfig = plugin.getUserSettingsConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("spawn")){

            Location location = Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation();
            location.setX(standardConfig.getInt("spawnlocation.x"));
            location.setY(standardConfig.getInt("spawnlocation.y"));
            location.setZ(standardConfig.getInt("spawnlocation.z"));
            Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).teleport(location);

            sender.sendMessage(standardConfig.getString("spawn.message"));

            return true;
        }

        if(cmd.getName().equalsIgnoreCase("setspawn")){
            if(sender.isOp()){
                if (args.length == 3){
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);

                    standardConfig.set("spawnlocation.x", x);
                    standardConfig.set("spawnlocation.y", y);
                    standardConfig.set("spawnlocation.z", z);
                    plugin.saveConfig();

                    sender.sendMessage(standardConfig.getString("setspawn.message"));

                    return true;
                } else if (args.length == 0){
                    Location senderLocation = ((Player) sender).getLocation();

                    standardConfig.set("spawnlocation.x", senderLocation.getBlockX());
                    standardConfig.set("spawnlocation.y", senderLocation.getBlockY());
                    standardConfig.set("spawnlocation.z", senderLocation.getBlockZ());
                    plugin.saveConfig();

                    sender.sendMessage(standardConfig.getString("setspawn.message"));

                    return true;
                } else {
                    sender.sendMessage(standardConfig.getString("setspawn.messageSyntax"));
                }
            }
        }

        if(cmd.getName().equalsIgnoreCase("tpa")){
            if(args.length == 1){
                Player receiver = Bukkit.getPlayer(args[0]);
                if(receiver != null){
                    pendingTpaRequests.put(receiver.getName(), ((Player) sender).getName());

                    String prompt = standardConfig.getString("tpa.prompt").replaceAll("%player%", ((Player) sender).getDisplayName());
                    TextComponent[] promptComponent = {new TextComponent(prompt.split("/tpaccept")[0]), new TextComponent(prompt.split("/tpaccept")[1])};
                    TextComponent acceptCommand = new TextComponent("/tpaccept " + sender.getName());
                    acceptCommand.setColor(ChatColor.RED);
                    acceptCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));
                    acceptCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Klick!")));
                    receiver.spigot().sendMessage(promptComponent[0], acceptCommand, promptComponent[1]);

                    sender.sendMessage(standardConfig.getString("tpa.requestSent").replaceAll("%player%", receiver.getDisplayName()));

                    plugin.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            pendingTpaRequests.remove(receiver.getName());
                        }
                    }, 3600);
                    return true;
                }
            } else {
                String text = standardConfig.getString("tpa.messageSyntax");
                sender.sendMessage(text);
            }
        }

        if(cmd.getName().equalsIgnoreCase("tpaccept")){
            if(args.length == 1){
                Player requestingPlayer = Bukkit.getPlayer(args[0]);
                Player player = Bukkit.getPlayer(sender.getName());

                if(pendingTpaRequests.containsKey(player.getName())){
                    Location location = player.getLocation();
                    requestingPlayer.teleport(location);
                    pendingTpaRequests.remove(requestingPlayer.getName());
                    sender.sendMessage(standardConfig.getString("tpa.teleportationSuccessful").replaceAll("%player%", requestingPlayer.getDisplayName()));
                    requestingPlayer.sendMessage(standardConfig.getString("tpaccept.teleportationSuccessful").replaceAll("%player%", player.getDisplayName()));
                    return true;
                } else {
                    String text = standardConfig.getString("tpaccept.requestNotFound").replaceAll("%player%", args[0]);
                    sender.sendMessage(text);
                    return false;
                }
            } else {
                String text = standardConfig.getString("tpaccept.messageSyntax");
                sender.sendMessage(text);
            }
        }

        if(cmd.getName().equalsIgnoreCase("home")){
            if (sender instanceof Player){
                if (args.length == 0){
                    if (userSettingsConfig.isSet(sender.getName() + ".homeLocation.world") && userSettingsConfig.isSet(sender.getName() + ".homeLocation.x") && userSettingsConfig.isSet(sender.getName() + ".homeLocation.y") && userSettingsConfig.isSet(sender.getName() + ".homeLocation.z") ){

                        Player player = ((Player) sender);

                        World world = Bukkit.getServer().getWorld(userSettingsConfig.getString(sender.getName() + ".homeLocation.world"));
                        int x = userSettingsConfig.getInt(sender.getName() + ".homeLocation.x");
                        int y = userSettingsConfig.getInt(sender.getName() + ".homeLocation.y");
                        int z = userSettingsConfig.getInt(sender.getName() + ".homeLocation.z");

                        Location home = new Location(world, x, y, z);

                        player.teleport(home);

                        return true;
                    } else {
                        sender.sendMessage(standardConfig.getString("home.noHomeFound"));
                    }
                } else {
                    sender.sendMessage(standardConfig.getString("home.messageSyntax"));
                }
            } else {
                sender.sendMessage("This command is only usuable by players!");
            }
        }

        if(cmd.getName().equalsIgnoreCase("sethome")){
            if (sender instanceof Player){
                Player player = ((Player) sender);
                if (args.length == 0){
                    Location loc = player.getLocation();

                    userSettingsConfig.set(player.getName() + ".homeLocation.world", loc.getWorld().getName());
                    userSettingsConfig.set(player.getName() + ".homeLocation.x", loc.getX());
                    userSettingsConfig.set(player.getName() + ".homeLocation.y", loc.getY());
                    userSettingsConfig.set(player.getName() + ".homeLocation.z", loc.getZ());

                    plugin.saveUserSettingsConfig();

                    sender.sendMessage(standardConfig.getString("sethome.message"));
                    return true;
                } else
                if (args.length == 3 && player.isOp()){
                    Location loc = player.getLocation();

                    int x;
                    int y;
                    int z;

                    try {
                        x = Integer.parseInt(args[0]);
                        y = Integer.parseInt(args[1]);
                        z = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage(standardConfig.getString("sethome.messageSyntaxOp"));
                        return false;
                    }

                    userSettingsConfig.set(player.getName() + ".homeLocation.world", loc.getWorld().getName());
                    userSettingsConfig.set(player.getName() + ".homeLocation.x", x);
                    userSettingsConfig.set(player.getName() + ".homeLocation.y", y);
                    userSettingsConfig.set(player.getName() + ".homeLocation.z", z);

                    plugin.saveUserSettingsConfig();

                    sender.sendMessage(standardConfig.getString("sethome.message"));

                    return true;
                } else
                if (args.length == 4 && player.isOp()) {
                    String world = args[0];

                    if(Bukkit.getWorld(world) == null){
                        sender.sendMessage(standardConfig.getString("sethome.messageSyntaxOp"));
                        return false;
                    }

                    int x;
                    int y;
                    int z;

                    try {
                        x = Integer.parseInt(args[0]);
                        y = Integer.parseInt(args[1]);
                        z = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage(standardConfig.getString("sethome.messageSyntaxOp"));
                        return false;
                    }

                    userSettingsConfig.set(player.getName() + ".homeLocation.world", world);
                    userSettingsConfig.set(player.getName() + ".homeLocation.x", x);
                    userSettingsConfig.set(player.getName() + ".homeLocation.y", y);
                    userSettingsConfig.set(player.getName() + ".homeLocation.z", z);

                    plugin.saveUserSettingsConfig();

                    sender.sendMessage(standardConfig.getString("sethome.message"));

                    return true;
                } else {
                    sender.sendMessage(standardConfig.getString("sethome.messageSyntaxOp"));
                    return false;
                }
            }
        }
        return false;
    }
}
