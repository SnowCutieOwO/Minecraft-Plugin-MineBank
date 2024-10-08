package mb.Guayando.tasks;

import mb.Guayando.MineBank;
import mb.Guayando.config.MessagesConfigManager;
import mb.Guayando.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class BankTask extends BukkitRunnable {
    private MineBank plugin;
    private int profitPercentage, roundedProfit, minAmountToWin;
    private FileConfiguration bankConfig;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    private boolean bankUseEnabled;

    public BankTask(MineBank plugin) {
        this.plugin = plugin;
        this.bankConfig = plugin.getBankConfigManager().getConfig();
        this.messagesConfig = plugin.getMessagesConfigManager().getConfig();
        this.config = plugin.getConfig();
        updateConfig();
    }

    @Override
    public void run() {
        if (shouldRunTask()) {
            executeBankTask();
        }
    }

    private boolean shouldRunTask() {
        return bankUseEnabled;
    }

    private void executeBankTask() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            String playerPath = "bank." + player.getUniqueId() + "." + player.getName();
            int balance = plugin.getBankConfigManager().getConfig().getInt(playerPath + ".balance", 0);
            int level = bankConfig.getInt(playerPath + ".level", 1);
            int maxStorage = getMaxStorageAmount(level);

            if (balance < 0) { balance = 0; }
            if (balance > maxStorage) { balance = maxStorage; }

            if (balance >= minAmountToWin) {
                // Calcular el beneficio
                long profit = (long) Math.floor(balance * (profitPercentage / 100.0)); // Cambia a 100.0 para evitar la división entera
                roundedProfit = (int) profit; // Asignar profit directamente a roundedProfit

                // Verificar si el saldo total con el beneficio excede el máximo permitido
                if (balance + roundedProfit > maxStorage) {
                    maxStorageProfit(player);
                } else {
                    // Actualizar el saldo con el beneficio redondeado
                    plugin.getBankConfigManager().getConfig().set(playerPath + ".balance", balance + roundedProfit);
                    plugin.getBankConfigManager().saveConfig();
                    receivedProfit(player);
                }
            } else {
                minStorageProfit(player);
            }
        });
    }

    private void updateConfig() {
        this.profitPercentage = config.getInt("bank.profit.keep-in-bank");
        this.minAmountToWin = config.getInt("bank.profit.min-amount-to-win");
        this.bankUseEnabled = config.getBoolean("config.bank-use");
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
        updateConfig();
    }

    private int getMaxStorageAmount(int level) {
        String levelData = config.getString("bank.level." + level);
        if (levelData != null && levelData.contains(";")) {
            String[] parts = levelData.split(";");
            if (parts.length > 0) {
                try {
                    return Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public void receivedProfit(CommandSender sender) {
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getReceivedProfit();
        if (mensaje != null) {
            mensaje = mensaje.replace("%plugin%", MineBank.prefix)
                    .replace("%profit%", String.valueOf(roundedProfit))
                    .replace("%percentage%", String.valueOf(profitPercentage));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }

    public void maxStorageProfit(CommandSender sender) {
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getMaxStorageProfit();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }

    public void minStorageProfit(CommandSender sender) {
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getMinStorageProfit();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix)
                    .replaceAll("%amount%", String.valueOf(minAmountToWin));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
}
