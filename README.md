# MainPlugin
## About
This plugin was build on the Spigot 1.18 API. This it will run on Spigot 1.18 and PaperMC 1.18. 
However, I can't and won't guarantee support for CraftBukkit, Bukkit, Sponge and so on, as well as any other Minecraft Versions. 

This plugin will add a few general commands, 
change the chat behavior such that you can click on names in order to message the person who sent the message privately 
and last but not least change the join and leave messages to anything you desire it to be.

Every message sent by the plugin can be changed inside the config.yml file or in the infomenu.yml file

### Disclaimer
This is my first own-developed plugin. So don't expect to find refined code without any obvious flaws.

### List of added commands
- /info: A list of commands
- /msg: This will change the design a message looks to anything you want.
- /tpa: Request to be teleported to someone.
- /tpaccept: Accept a tpa request.
- /sethome: Set a home.
- /setspawn: Set a spawn (only OP)
- /home: Go home.
- /spawn: Teleport to the spawn.
- /group: Manage groups

### Additional features
- This plugin prohibits using the vanilla /help command and redirects to the /info command
- Whenever a command containing a colon (e.g. /minecraft:help) is used, it returns a message pointing towards missing permissions. Only OPs can circumvent this regulation.
- The /plugin command is suppressed.
- In chat, the message prefix can be changed. When it is clicked in-game, the player can privately message the sender of the particular message.
- When using an '@<groupTag>' in the beginning of a chat message, the plugin tries to send your message only to the group members.
- When your name is mentioned in the chat, you get a notification. This can be turned off individually using the /settings command.

### Permissions
This plugin does not work with permissions as it is intended to be as light weight as possible. Also, the original purpose of it didn't need it to have permissions.
All permission related things are handled by checking the operator status.

## How to build
Dependency for building: Maven 3.6.3 or newer and JDK 17.
To build this plugin, you may run `mvn package` in your cloned repository. You will find the jar file in the target folder.

## How to install
Just plug it into your plugin folder and you're good to go.

## Why does this exist?
The sole purpose of this plugin is to be used in the context of a minecraft server a few fellow students and I organize for ourselves. Never, did I have the idea that this plugin should run on any other servers than ours, but due to transparency reasons I'm publishing it here under the Affero GPL License v3.0.

### Known issue
Due to it being solely made for a single private server, I didn't bother to use UUIDs instead of Minecraft names. Therefore, you will encounter problems once a player changes their name.

I don't plan to fix this issue as it would not fit my usecase. See it as a case of "It's not a bug, it's a feature!".

## Configuration
There are four config files:
- config (main config file)
- usersettings
- infomenu
- groups

### Main config
Here, you can change the messages sent by the plugin. By default all messages are in German as this plugin was written for a private German minecraft server.

### User settings config
This is the file where every setting a user makes is stored. It may be replaced with a database in the future.

### Info menu config
Here, you can adjust what is sent to the user when he uses the /info command.

### Groups config
In this config, all groups, their members and owners are stored.

# License
MainPlugin (c) 2021 Lucca Greschner and contributors

SPDX-License-Identifier: AGPL-3.0
