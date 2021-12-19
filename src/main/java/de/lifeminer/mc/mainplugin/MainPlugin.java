package de.lifeminer.mc.mainplugin;

import de.lifeminer.mc.mainplugin.commandexecution.*;
import de.lifeminer.mc.mainplugin.eventlisteners.ChatEventListener;
import de.lifeminer.mc.mainplugin.eventlisteners.CommandEventListener;
import de.lifeminer.mc.mainplugin.eventlisteners.JoinQuitEventListener;
import de.lifeminer.mc.mainplugin.eventlisteners.SettingsMenuEventListener;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class MainPlugin extends JavaPlugin {

    // Declaration of Config-Files

    private File infoMenuConfigFile;
    private File userSettingsConfigFile;
    private File groupsFile;

    // Declaration of Configs

    private FileConfiguration standardConfig;
    private FileConfiguration infoMenuConfig;
    private FileConfiguration userSettingsConfig;
    private FileConfiguration groupsConfig;

    // Declaration of the scheduler used to schedule actions

    private BukkitScheduler scheduler;

    /**
     * onEnable handles all tasks that should be done on enabling the plugin.
     * It gets executed whenever the server is started or reloaded.
     */

    @Override
    public void onEnable(){
        // initialising default config

        this.saveDefaultConfig();
        standardConfig = this.getConfig();

        // creating custom configs

        this.createInfoMenuConfig();
        this.createUserSettingsConfig();
        this.createGroupsConfig();

        // This will set the class GeneralCommandExecution as the Executor for general commands

        this.getCommand("info").setExecutor(new GeneralCommandExecution(this));
        this.getCommand("ping").setExecutor(new GeneralCommandExecution(this));
        this.getCommand("help").setExecutor(new GeneralCommandExecution(this));
        this.getCommand("clearchat").setExecutor(new GeneralCommandExecution(this));
        this.getCommand("reloadconfig").setExecutor(new GeneralCommandExecution(this));

        // This will set the class MessageCommandExecution as the Executor for the following commands

        this.getCommand("msg").setExecutor(new MessageCommandExecution(this));

        // This will set the class GroupCommandExecution as the Executor for the following commands

        this.getCommand("group").setExecutor(new GroupCommandExecution(this));

        // This will set the class TeleportationCommandExecution as the Executor for the following commands

        this.getCommand("spawn").setExecutor(new TeleportationCommandExecution(this));
        this.getCommand("setspawn").setExecutor(new TeleportationCommandExecution(this));
        this.getCommand("home").setExecutor(new TeleportationCommandExecution(this));
        this.getCommand("sethome").setExecutor(new TeleportationCommandExecution(this));
        this.getCommand("tpa").setExecutor(new TeleportationCommandExecution(this));
        this.getCommand("tpaccept").setExecutor(new TeleportationCommandExecution(this));

        // This will set the class SettingsCommandExecutor as the Executor for the following commands
        // SettingsCommandExecutor is used to handle all commands related to the userSettingsConfig

        this.getCommand("settings").setExecutor(new SettingsCommandExecution(this));
        this.getCommand("nickname").setExecutor(new SettingsCommandExecution(this));
        this.getCommand("resetnickname").setExecutor(new SettingsCommandExecution(this));

        // Register EventListeners

        getServer().getPluginManager().registerEvents(new JoinQuitEventListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatEventListener(this), this);
        getServer().getPluginManager().registerEvents(new SettingsMenuEventListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandEventListener(this), this);

        // Initialise Bukkit Scheduler

        scheduler = getServer().getScheduler();
    }

    /**
     * onDisable handles all tasks that should be done on disabling the plugin.
     * It gets executed whenever the server is stopped or reloaded.
     */

    public void onDisable(){

    }

    /**
     * getInfoMenu is the getter-method for the infoMenuConfig.
     * @return FileConfiguration infoMenuConfig
     */

    public FileConfiguration getInfoMenuConfig() {
        return this.infoMenuConfig;
    }

    /**
     * getUserSettingsConfig is the getter-method for the userSettingsConfig.
     * @return FileConfiguration userSettingsConfig
     */

    public FileConfiguration getUserSettingsConfig() {
        return this.userSettingsConfig;
    }

    /**
     * getGroupsConfig is the getter-method for the groupsConfig.
     * @return FileConfiguration groupsConfig
     */

    public FileConfiguration getGroupsConfig() {
        return this.groupsConfig;
    }

    /**
     * getScheduler is the getter-method for the used scheduler
     * @return used BukkitScheduler
     */

    public BukkitScheduler getScheduler(){
        return this.scheduler;
    }

    /**
     * createInfoMenuConfig creates the custom config infoMenuConfig
     */

    private void createInfoMenuConfig() {
        infoMenuConfigFile = new File(getDataFolder(), "infomenu.yml");
        if (!infoMenuConfigFile.exists()) {
            infoMenuConfigFile.getParentFile().mkdirs();
            saveResource("infomenu.yml", false);
        }

        infoMenuConfig = new YamlConfiguration();
        try {
            infoMenuConfig.load(infoMenuConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * createUserSettingsConfig creates the custom config userSettingsConfig.
     */

    private void createUserSettingsConfig() {
        userSettingsConfigFile = new File(getDataFolder(), "usersettings.yml");
        if (!userSettingsConfigFile.exists()) {
            userSettingsConfigFile.getParentFile().mkdirs();
            saveResource("usersettings.yml", false);
        }

        userSettingsConfig = new YamlConfiguration();
        try {
            userSettingsConfig.load(userSettingsConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * saveUserSettingsConfig saves the userSettingsConfig.
     */

    public void saveUserSettingsConfig(){
        try {
            this.userSettingsConfig.save(this.userSettingsConfigFile);
            this.getLogger().log(Level.INFO, "Successfully saved User Settings Config!");
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to save" + userSettingsConfigFile.getName());
        }
    }

    /**
     * createUserSettings creates the standard configuration for a specific player.
     * @param player Player for which the standard configuration shall be established.
     */

    public void createUserSettings(Player player){
        if(!userSettingsConfig.contains(player.getUniqueId() + ".noteOnChat")) {
            userSettingsConfig.set(player.getUniqueId() + ".noteOnChat", true);
            saveUserSettingsConfig();
        }
        if(!userSettingsConfig.contains(player.getUniqueId() + ".configCreated")){
            userSettingsConfig.set(player.getUniqueId() + ".configCreated", true);
            saveUserSettingsConfig();
        }
    }

    /**
     * createGroupsConfig creates the custom config groups.
     */

    private void createGroupsConfig() {
        groupsFile = new File(getDataFolder(), "groups.yml");
        if (!groupsFile.exists()) {
            groupsFile.getParentFile().mkdirs();
            saveResource("groups.yml", false);
        }

        groupsConfig = new YamlConfiguration();
        try {
            groupsConfig.load(groupsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * saveGroupsConfig saves the groups.
     */

    public void saveGroupsConfig(){
        try {
            this.groupsConfig.save(this.groupsFile);
            this.getLogger().log(Level.INFO, "Successfully saved groups!");
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to save" + groupsFile.getName());
        }
    }

    public void reloadConfigs(){
        try {
            userSettingsConfig.load(new File(getDataFolder(), "usersettings.yml"));
            infoMenuConfig.load(new File(getDataFolder(), "infomenu.yml"));
            groupsConfig.load(new File(getDataFolder(), "groups.yml"));
            standardConfig.load(new File(getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }



    /**
     * replaceChatColor replaces all the chat color codes with '&' in them with the minecraft color codes with '§' in them.
     * @param in String in which the color codes shall be replaced.
     * @return a String that only contains minecraft color codes.
     */

    public String replaceChatColor(String in){
        in = in.replace("&0", "§0");
        in = in.replace("&1", "§1");
        in = in.replace("&2", "§2");
        in = in.replace("&3", "§3");
        in = in.replace("&4", "§4");
        in = in.replace("&5", "§5");
        in = in.replace("&6", "§6");
        in = in.replace("&7", "§7");
        in = in.replace("&8", "§8");
        in = in.replace("&9", "§9");
        in = in.replace("&a", "§a");
        in = in.replace("&b", "§b");
        in = in.replace("&c", "§c");
        in = in.replace("&d", "§d");
        in = in.replace("&e", "§e");
        in = in.replace("&f", "§f");
        in = in.replace("&k", "§k");
        in = in.replace("&l", "§l");
        in = in.replace("&m", "§m");
        in = in.replace("&n", "§n");
        in = in.replace("&o", "§o");
        in = in.replace("&r", "§r");
        return in;
    }




}
