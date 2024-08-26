package mb.Guayando.utils;

import mb.Guayando.MineBank;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class CheckConfigDefaults {

    public static void checkConfigDefaults(MineBank plugin) {
        boolean modified = false;

        // Obtener configuraciones
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessagesConfigManager().getConfig();

        // Valores por defecto para config.yml
        Map<String, Object> defaultConfigValues = new LinkedHashMap<>();
        defaultConfigValues.put("config.bank-use", true);
        defaultConfigValues.put("config.bank-disabled-message", "%plugin% &cThe bank function is currently disabled");
        defaultConfigValues.put("config.update-checker", true);
        defaultConfigValues.put("config.update-message", "%plugin% &bNew version available &e(&f%latestversion%&e)&b. Download it here: &7%link%");
        defaultConfigValues.put("bank.level.1", "5000;2000");
        defaultConfigValues.put("bank.profit.min-amount-to-win", 100);
        defaultConfigValues.put("bank.profit.keep-in-bank", 2);
        defaultConfigValues.put("bank.profit.interval", 300);
        defaultConfigValues.put("bank.interests.withdraw", 5);

        // Valores por defecto para messages.yml
        Map<String, Object> defaultMessageValues = new LinkedHashMap<>();
        defaultMessageValues.put("messages.help", new String[]{
                "&b<-----------------&aCOMMANDS MINEBANK&b----------------->",
                "&e/minebank help &7- Show this message",
                "&e/minebank reload &7- Reload plugin config",
                "&e/minebank version &7- Show plugin version",
                "&e/minebank author &7- Show plugin author",
                "&e/minebank permissions &7- Show plugin permission list",
                "&e/minebank plugin &7- Show plugin download link",
                "&e/bank help &7- Show bank command list",
                "&7Aliases: &eminebank&7, &emb"
        });
        defaultMessageValues.put("messages.help-bank", new String[]{
                "&b<-----------------&aCOMMANDS MINEBANK&b----------------->",
                "&e/bank help &7- Show this message",
                "&e/bank add <amount> &7- Deposit money",
                "&e/bank take <amount> &7- Withdraw money",
                "&e/bank set <player> bal <amount> &7- Set player balance",
                "&e/bank set <player> level <amount> &7- Set player level",
                "&e/bank top &7- Show top 10 players with the most money",
                "&e/bank bal &7- Check your balance",
                "&e/bank bal <player> &7- Check another player`s balance",
                "&e/bank max bal &7- Check maximum capacity",
                "&e/bank max level &7- Check maximum level",
                "&e/bank level &7- Check your bank level",
                "&e/bank levelup &7- Level up your bank account"
        });
        defaultMessageValues.put("messages.reload", "%plugin% &aThe plugin has been reloaded.");
        defaultMessageValues.put("messages.version", "%plugin% &aPlugin version: &b%version%");
        defaultMessageValues.put("messages.author", "%plugin% &aPlugin author: &b%author%");
        defaultMessageValues.put("messages.permissions", new String[]{
                "&b<-----------------&aPERMISSIONS MINEBANK&b----------------->",
                "&eminebank.* &7- OP permissions",
                "&eminebank.help &7- Allow to use &e/minebank help",
                "&eminebank.reload &7- Allow to use &e/minebank reload",
                "&eminebank.version &7- Allow to use &e/minebank version",
                "&eminebank.author &7- Allow to use &e/minebank author",
                "&eminebank.plugin &7- Allow to use &e/minebank plugin",
                "&eminebank.permissions &7- Allow to use &e/minebank permissions",
                "&eminebank.updatechecker &7- Allow receive message if there is an update",
                "&eminebank.admin &7- Allow to use /bank set and other admin commands",
                "&eminebank.use &7- Allow to use the bank",
                "&eminebank.helpbank &7- Allow to use &e/bank help"
        });
        defaultMessageValues.put("messages.command-no-argument", "%plugin% &7Use &e/minebank help &7to see the command list.");
        defaultMessageValues.put("messages.no-perm", "%plugin% &cYou don`t have permission to use that command.");
        defaultMessageValues.put("messages.console-error", "%plugin% &cYou can`t use that command in console.");
        defaultMessageValues.put("bank.usage.bank", "%plugin% &eUsage: /bank <top/bal/max/level/levelup/add/take/set>");
        defaultMessageValues.put("bank.usage.add", "%plugin% &eUsage: /bank add <amount>");
        defaultMessageValues.put("bank.usage.take", "%plugin% &eUsage: /bank take <amount>");
        defaultMessageValues.put("bank.usage.set", "%plugin% &eUsage: /bank set <player> <bal/level> <amount>");
        defaultMessageValues.put("bank.usage.top", "%plugin% &eUsage: /bank top");
        defaultMessageValues.put("bank.usage.bal", "%plugin% &eUsage: /bank bal, /bank bal <player>");
        defaultMessageValues.put("bank.usage.max", "%plugin% &eUsage: /bank max <bal/level>");
        defaultMessageValues.put("bank.usage.level", "%plugin% &eUsage: /bank level");
        defaultMessageValues.put("bank.usage.levelup", "%plugin% &eUsage: /bank levelup");
        defaultMessageValues.put("bank.bal.yourBalance", "%plugin% &eYour current bank balance is: &a$%balance%");
        defaultMessageValues.put("bank.bal.playerBalance", "%plugin% &e%player%`s balance is: &a$%balance%");
        defaultMessageValues.put("bank.take.withdrawSuccess", "%plugin% &aYou have withdrawn $%amount% from your bank. The bank charge is: $%totalDeducted%");
        defaultMessageValues.put("bank.take.withdrawFailure", "%plugin% &cThe amount must be positive and integer number.");
        defaultMessageValues.put("bank.take.withdrawExceeds", "%plugin% &cYou do not have enough money in your bank to withdraw that amount.");
        defaultMessageValues.put("bank.take.interestsWithdrawExceeds", "%plugin% &cThe interest exceed the amount in your bank.");
        defaultMessageValues.put("bank.add.depositSuccess", "%plugin% &aYou have deposited $%amount% into your bank.");
        defaultMessageValues.put("bank.add.balanceExceeds", "%plugin% &cYou cannot store more than $%maxStorage% at your current bank level.");
        defaultMessageValues.put("bank.add.depositFailure", "%plugin% &cThe amount must be positive and integer number.");
        defaultMessageValues.put("bank.add.notEnoughMoney", "%plugin% &cYou do not have enough money to deposit that amount.");
        defaultMessageValues.put("bank.set.maxBalance", "%plugin% &cThe amount cannot be higher than $%maxStorage%");
        defaultMessageValues.put("bank.set.maxLevel", "%plugin% &cThe level cannot be higher than %maxLevel%");
        defaultMessageValues.put("bank.set.setBalanceSuccess", "%plugin% &aSet %player%`s balance to $%amount%");
        defaultMessageValues.put("bank.set.setLevelSuccess", "%plugin% &aSet %player%`s bank level to %amount%");
        defaultMessageValues.put("bank.top.title", "%plugin% &eTop 10 players with the most money in the bank:");
        defaultMessageValues.put("bank.top.entry", "&e%position%. %player%: &a$%balance%");
        defaultMessageValues.put("bank.level.current", "%plugin% &eYour bank level is: &a%level%");
        defaultMessageValues.put("bank.levelup.notEnoughMoney", "%plugin% &cYou don`t have enough money in the bank to level up, you need $%unlockPrice%");
        defaultMessageValues.put("bank.levelup.unlockPriceError", "%plugin% &cInvalid unlock price in configuration for level %unlockLevel%");
        defaultMessageValues.put("bank.levelup.success", "%plugin% &aYou have successfully leveled up to level %newLevel%");
        defaultMessageValues.put("bank.levelup.alreadyMax", "%plugin% &cYou are already at the maximum level.");
        defaultMessageValues.put("bank.max.maxStorage", "%plugin% &eThe maximum amount you can store at your current level is: &a$%maxStorage%");
        defaultMessageValues.put("bank.max.maxLevel", "%plugin% &aThe maximum bank level is &e%maxLevel%.");
        defaultMessageValues.put("bank.profit.received", "%plugin% &aYou have received $%profit% for keeping your money in the bank!");
        defaultMessageValues.put("bank.profit.min-storage", "%plugin% &cYou need at least $%amount% in the bank to start earning benefits.");
        defaultMessageValues.put("bank.profit.max-storage", "%plugin% &cYou need to withdraw money from the bank to continue earning benefits.");
        defaultMessageValues.put("bank.notPlayer", "%plugin% &cOnly players can use this command.");
        defaultMessageValues.put("bank.notFoundPlayer", "%plugin% &cPlayer %player% not found.");

        // Verificar y agregar valores por defecto a config.yml
        for (Map.Entry<String, Object> entry : defaultConfigValues.entrySet()) {
            String path = entry.getKey();
            Object defaultValue = entry.getValue();
            if (!config.contains(path)) {
                config.set(path, defaultValue);
                modified = true;
            }
        }

        // Verificar y agregar valores por defecto a messages.yml
        for (Map.Entry<String, Object> entry : defaultMessageValues.entrySet()) {
            String path = entry.getKey();
            Object defaultValue = entry.getValue();
            if (!messages.contains(path)) {
                messages.set(path, defaultValue);
                modified = true;
            }
        }

        // Guardar los archivos si se realizaron cambios
        if (modified) {
            plugin.saveConfig();
            plugin.getMessagesConfigManager().saveConfig();
            plugin.getLogger().info("Config and Messages files were modified and saved.");
        }
    }
}
