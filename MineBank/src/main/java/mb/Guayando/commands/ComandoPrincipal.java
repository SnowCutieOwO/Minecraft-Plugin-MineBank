package mb.Guayando.commands;

import mb.Guayando.config.MessagesConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import mb.Guayando.MineBank;
import mb.Guayando.utils.MessageUtils;

import java.util.List;

public class ComandoPrincipal implements CommandExecutor {

    private MineBank plugin;

    public ComandoPrincipal(MineBank plugin) {
        this.plugin = plugin;
        reloadConfigs();
    }
    public void reloadConfigs() {
        Economy economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        FileConfiguration bankConfig = plugin.getBankConfigManager().getConfig();
        FileConfiguration messagesConfig = plugin.getMessagesConfigManager().getConfig();
        FileConfiguration config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        reloadConfigs();
        if (!(sender instanceof Player)) {
            // Consola
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadConfig();
                    plugin.getMainConfigManager().reloadConfig();
                    plugin.getMessagesConfigManager().reloadConfig();
                    plugin.getBankConfigManager().reloadConfig();

                    // Llama a loadConfig() después de recargar
                    plugin.getMainConfigManager().loadConfig();

                    // Actualizar la configuración en BankTask
                    if (plugin.getBankTask() != null) {
                        plugin.getBankTask().setConfig(plugin.getConfig());
                    }

                    // Reiniciar la tarea del banco después de recargar la configuración
                    plugin.scheduleBankTask();

                    Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(MineBank.prefix + "&aReload complete."));
                    return true;
                }

                consoleError(sender); // messages.console-error
                return true;
            }
            consoleError(sender); // messages.console-error
            return true;
        }
        // /minebank args[0] args[1] args[2]
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                // minebank reload
                subCommandReload(sender);
            } else if (args[0].equalsIgnoreCase("help")) {
                // minebank help
                help(sender);
            } else if (args[0].equalsIgnoreCase("version")) {
                // minebank version
                subCommandVersion(sender);
            } else if (args[0].equalsIgnoreCase("author")) {
                // minebank author
                subCommandAutor(sender);
            }else if (args[0].equalsIgnoreCase("plugin")) {
                // minebank plugin
                subCommandPlugin(sender);
            } else if (args[0].equalsIgnoreCase("permissions")) {
                // minebank permissions
                subCommandPermissions(sender);
            }else {
                noArg(sender); // minebank qwewe
            }
        } else {
            noArg(sender); // minebank
        }
        return true;
    }

    public void help(CommandSender sender) {
        if (!sender.hasPermission("minebank.help") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return;
        }
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        List<String> message = messagesConfigManager.getHelpMessage();
        for(String m : message){
            sender.sendMessage(MessageUtils.getColoredMessage(m));
        }
    }

    public void subCommandReload(CommandSender sender) {
        if (!sender.hasPermission("minebank.reload") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return;
        }
        plugin.reloadConfig();
        plugin.getMainConfigManager().reloadConfig();
        plugin.getMessagesConfigManager().reloadConfig();
        plugin.getBankConfigManager().reloadConfig();

        // Llama a loadConfig() después de recargar
        plugin.getMainConfigManager().loadConfig();

        // Actualizar la configuración en BankTask
        if (plugin.getBankTask() != null) {
            plugin.getBankTask().setConfig(plugin.getConfig());
        }

        // Reiniciar la tarea del banco después de recargar la configuración
        plugin.scheduleBankTask();
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getReloadMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }

    public void subCommandVersion(CommandSender sender) {
        if (!sender.hasPermission("minebank.version") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return;
        }
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getVersionMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%version%", plugin.version).replaceAll("%latestversion%", plugin.getLatestVersion());
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }

    public void subCommandAutor(CommandSender sender) {
        if (!sender.hasPermission("minebank.author") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return;
        }
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getAuthorMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%author%", plugin.getDescription().getAuthors().toString());
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }

    public void subCommandPlugin(CommandSender sender){
        if (!sender.hasPermission("minebank.plugin") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return;
        }
        String mensaje = "%plugin% &7https://www.spigotmc.org/resources/119147/".replaceAll("%plugin%", MineBank.prefix);
        sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
    }

    public void subCommandPermissions(CommandSender sender){
        if (!sender.hasPermission("minebank.permissions") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return;
        }
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        List<String> message = messagesConfigManager.getPermissionsMessage();
        for(String m : message){
            sender.sendMessage(MessageUtils.getColoredMessage(m));
        }
    }

    public void noPerm(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNoPermMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void noArg(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNoArgumentMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void consoleError(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getConsoleErrorMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
}