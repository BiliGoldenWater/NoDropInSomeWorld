package indi.goldenwater.nodropinsomeworld.command;

import indi.goldenwater.nodropinsomeworld.NoDropInSomeWorld;
import indi.goldenwater.nodropinsomeworld.utils.CheckPermissions;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

public class MainCommand {
    private static final String commandName = "nodropinsomeworld";

    public static void register() {
        PluginCommand command = NoDropInSomeWorld.getInstance().getCommand(commandName);
        if (command != null) {
            command.setExecutor(getExecutor());
        }
    }

    private static CommandExecutor getExecutor() {
        return (sender, command, label, args) -> {
            boolean isPlayer = sender instanceof Player;
            Configuration config = NoDropInSomeWorld.getInstance().getConfig();
            Plugin plugin = NoDropInSomeWorld.getInstance();
            if (args.length > 0) {
                switch (args[0]) {
                    case "help":
                        if (CheckPermissions.hasPermissions_Tips(sender, "nodropinsomeworld.commands.help")) {
                            sendHelpMessage(sender, HelpLvL.fullHelp);
                            return true;
                        }
                        break;
                    case "listworld":
                        if (CheckPermissions.hasPermissions_Tips(sender, "nodropinsomeworld.commands.listworld")) {
                            List<String> worldList = config.getStringList("denyWorlds");
                            StringBuilder message = new StringBuilder();
                            message.append("Worlds: ");
                            worldList.forEach((value) -> {
                                message.append(value);
                                message.append(Objects.equals(value, worldList.get(worldList.size() - 1)) ? "." : ", ");
                            });
                            sender.sendMessage(message.toString());
                            return true;
                        }
                        break;
                    case "addworld":
                        if (CheckPermissions.hasPermissions_Tips(sender, "nodropinsomeworld.commands.addworld")) {
                            if (args.length == 1) {
                                if (isPlayer) {
                                    Player player = (Player) sender;
                                    addWorld(sender, config, plugin, player.getWorld().getName());
                                } else {
                                    sender.sendMessage("在控制台使用请填写世界名.");
                                }
                                return true;
                            } else if (args.length == 2) {
                                if (plugin.getServer().getWorld(args[1]) != null) {
                                    addWorld(sender, config, plugin, args[1]);
                                } else {
                                    sender.sendMessage("不存在的世界.");
                                }

                                return true;
                            } else {
                                sendHelpMessage(sender, HelpLvL.addWorld);
                            }
                            return true;
                        }
                        break;
                    case "removeworld":
                        if (CheckPermissions.hasPermissions_Tips(sender, "nodropinsomeworld.commands.removeworld")) {
                            if (args.length == 1) { // 未填写世界名
                                if (isPlayer) {
                                    Player player = (Player) sender;
                                    removeWorld(sender, config, plugin, player.getWorld().getName());
                                } else {
                                    sender.sendMessage("在控制台使用请填写世界名.");
                                }
                                return true;
                            } else if (args.length == 2) { // 已填写世界名
                                if (plugin.getServer().getWorld(args[1]) != null) {
                                    removeWorld(sender, config, plugin, args[1]);
                                } else {
                                    sender.sendMessage("不存在的世界.");
                                }
                                return true;
                            } else {
                                sendHelpMessage(sender, HelpLvL.removeWorld);
                            }
                            return true;
                        }
                        break;
                    case "reload":
                        if (CheckPermissions.hasPermissions_Tips(sender, "nodropinsomeworld.commands.reload")) {
                            plugin.reloadConfig();
                            sender.sendMessage("重载成功.");
                            return true;
                        }
                        break;
                }
            }
            return false;
        };
    }

    private static void addWorld(CommandSender sender, Configuration config, Plugin plugin, String worldName) {
        List<String> worldList = config.getStringList("denyWorlds");
        boolean hasWorld = false;

        for (String value : worldList) {
            if (value.equals(worldName)) {
                hasWorld = true;
                break;
            }
        }

        if (hasWorld) {
            sender.sendMessage("已经存在于列表中.");
        } else {
            worldList.add(worldName);
            config.set("denyWorlds", worldList);
            plugin.saveConfig();
            sender.sendMessage("添加成功.");
        }
    }

    private static void removeWorld(CommandSender sender, Configuration config, Plugin plugin, String worldName) {
        List<String> worldList = config.getStringList("denyWorlds");
        boolean removeSuccess = false;

        for (int i = 0; i < worldList.size(); i++) {
            if (worldList.get(i).equals(worldName)) {
                worldList.remove(i);
                config.set("denyWorlds", worldList);
                plugin.saveConfig();
                sender.sendMessage("移除成功.");
                removeSuccess = true;
                break;
            }
        }
        if (!removeSuccess) sender.sendMessage("在列表中没有这个世界.");
    }

    private static void sendHelpMessage(CommandSender sender, int helpLvl) {
        String pluginSignature = "NoDropInSomeWorld by§7.§eGolden§7_§bWater§r",
                commandAlias = "指令别名§7:§d/ndisw§7, §d/nodrop§r",
                commandHelp_simple = "§d/nodrop§b help§r 获取帮助信息",
                commandListWorld_simple = "§d/nodrop§b listworld§r 获取所有禁用物品丢弃的世界",
                commandAddWorld_simple = "§d/nodrop§b addworld [worldName]§r 添加当前世界到禁用列表",
                commandRemoveWorld_simple = "§d/nodrop§b removeworld [worldName]§r 从禁用列表移除当前世界",
                commandReload_simple = "§d/nodrop§b reload§r 获取所有禁用物品丢弃的世界";

        switch (helpLvl) {
            case HelpLvL.fullHelp:
                sender.sendMessage(pluginSignature);
                sender.sendMessage(commandAlias);
                sender.sendMessage(commandHelp_simple);
                sender.sendMessage(commandListWorld_simple);
                sender.sendMessage(commandAddWorld_simple);
                sender.sendMessage(commandRemoveWorld_simple);
                sender.sendMessage(commandReload_simple);
                break;
            case HelpLvL.addWorld:
                sender.sendMessage(pluginSignature);
                sender.sendMessage(commandAlias);
                sender.sendMessage(commandAddWorld_simple);
                break;
            case HelpLvL.removeWorld:
                sender.sendMessage(pluginSignature);
                sender.sendMessage(commandAlias);
                sender.sendMessage(commandRemoveWorld_simple);
                break;
        }
    }

    private static class HelpLvL {
        static final int fullHelp = 0;
        static final int addWorld = 1;
        static final int removeWorld = 2;
    }

}
