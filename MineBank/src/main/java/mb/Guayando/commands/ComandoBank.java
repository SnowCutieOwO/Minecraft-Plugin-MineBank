package mb.Guayando.commands;

import mb.Guayando.MineBank;
import mb.Guayando.config.MainConfigManager;
import mb.Guayando.config.MessagesConfigManager;
import mb.Guayando.utils.MessageUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ComandoBank implements CommandExecutor {

    private MineBank plugin;
    private Economy economy;
    private FileConfiguration bankConfig;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    int interestPercentage, bankBalance, targetBalance, unlockPrice, maxStorage, amount, totalAmount, maxLevel, level;
    String targetPlayerName;

    public ComandoBank(MineBank plugin) {
        this.plugin = plugin;
        reloadConfigs(); // Inicializar variables en el constructor
    }

    public void reloadConfigs() {
        this.economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        this.bankConfig = plugin.getBankConfigManager().getConfig();
        this.messagesConfig = plugin.getMessagesConfigManager().getConfig();
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        reloadConfigs();
        if (!(sender instanceof Player)) {
            notPlayer(sender);
            return true;
        }

        if (!config.getBoolean("config.bank-use")) {
            MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
            String mensaje = messagesConfigManager.getBankDisabledMessage();
            if (mensaje != null) {
                mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
                sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
            }
            return true;
        }

        if (!sender.hasPermission("minebank.use") && !sender.hasPermission("minebank.admin")) {
            noPerm(sender);
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            bankUsage(sender);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("balance") || action.equals("bal")) {
            handleBalance(player, args.length > 1 ? args[1] : null);
        } else if (action.equals("add") || action.equals("deposit")) {
            if (args.length < 2) {
                bankAdd(sender);
                return true;
            }
            handleAdd(player, args[1]);
        } else if (action.equals("take") || action.equals("withdraw")) {
            if (args.length < 2) {
                bankTake(sender);
                return true;
            }
            handleTake(player, args[1]);
        } else if (action.equals("top") || action.equals("baltop")) {
            handleTop(player);
        } else if (action.equals("level") || action.equals("lvl")) {
            handleLevel(player, args.length > 1 ? args[1] : null);
        } else if (action.equals("levelup")) {
            handleLevelUp(player);
        } else if (action.equals("max")) {
            if (args.length < 2) {
                bankMax(sender);
                return true;
            }
            handleMax(player, args.length > 1 ? args[1] : null);
        } else if (action.equals("set")) {
            if (!sender.hasPermission("minebank.admin")) {
                noPerm(sender);
                return true;
            }

            // Verifica si se proporcionaron suficientes argumentos
            if (args.length < 4) {
                bankSet(sender);
                return true;
            }

            // Si hay 5 argumentos, pasa todos a handleSet
            if (args.length == 5) {
                handleSet(sender, args[1], args[2], args[3], args[4]);
            } else {
                // Si hay 4 argumentos, pasa los primeros 4 a handleSet sin el cuarto argumento opcional
                handleSet(sender, args[1], args[2], args[3]);
            }
            return true; // Asegúrate de retornar true para que el comando se marque como manejado
        } else if (action.equals("help")) {
            getBankHelp(sender);
        } else {
            bankUsage(sender);
        }
        return true;
    }

    private String getMessage(String key) {
        return MessageUtils.getColoredMessage(messagesConfig.getString(key, "%plugin% &cMessage not found: " + key).replaceAll("%plugin%", MineBank.prefix).replaceAll("%plugin%", MineBank.prefix));
    }

    private void handleBalance(Player player, String targetPlayerName) {
        this.targetPlayerName = targetPlayerName; // Inicializa targetPlayerName aquí
        String playerPath = "bank." + player.getUniqueId() + "." + player.getName();
        bankBalance = bankConfig.getInt(playerPath + ".balance", 0);
        if (targetPlayerName == null) {
            yourBalance(player);
        } else {
            String uuid = getPlayerUUIDByName(targetPlayerName);
            if (uuid != null) {
                targetBalance = bankConfig.getInt("bank." + uuid + "." + targetPlayerName + ".balance", 0);
                playerBalance(player);
            } else {
                notFoundPlayer(player);
            }
        }
    }

    private void handleAdd(Player player, String amountString) {
        try {
            String playerPath = "bank." + player.getUniqueId() + "." + player.getName();
            int bankBalance = bankConfig.getInt(playerPath + ".balance", 0);
            int level = bankConfig.getInt(playerPath + ".level", 1);
            maxStorage = getMaxStorageAmount(level);

            if (amountString.equalsIgnoreCase("max")) {
                amount = maxStorage - bankBalance; // Asignar el total restante
                if (amount <= 0) { // Si ya está en el máximo, no hace nada
                    balanceExceeds(player);
                    return;
                }
                // Verificar si el jugador tiene suficiente dinero
                if (economy.getBalance(player) < amount) {
                    amount = (int) economy.getBalance(player); // Si no tiene suficiente, usar todo su dinero
                }
            } else {
                amount = Integer.parseInt(amountString); // Convertir el string a entero
                if (amount <= 0) {
                    depositFailure(player);
                    return;
                }
                // Verificar si el jugador tiene suficiente dinero
                if (economy.getBalance(player) < amount) {
                    notEnoughMoneyAdd(player);
                    return;
                }
            }

            if (bankBalance + amount > maxStorage) {
                balanceExceeds(player);
                return;
            }

            // Retirar el dinero del jugador
            economy.withdrawPlayer(player, amount);
            // Actualizar el saldo del banco del jugador
            bankConfig.set(playerPath + ".balance", bankBalance + amount);
            plugin.getBankConfigManager().saveConfig();

            depositSuccess(player);
        } catch (NumberFormatException e) {
            depositFailure(player);
        }
    }
/*
bank:
  interests:
    min-amount-to-lose: 500 # Min amount of money withdraw to the bank to rest interest
*/
    private void handleTake(Player player, String amountString) {
        try {
            String playerPath = "bank." + player.getUniqueId() + "." + player.getName();
            int bankBalance = bankConfig.getInt(playerPath + ".balance", 0);
            int level = bankConfig.getInt(playerPath + ".level", 1);
            int maxStorage = getMaxStorageAmount(level);

            if (amountString.equalsIgnoreCase("max")) {
                // Calcula la máxima cantidad que puede retirarse sin exceder el maxStorage con intereses
                interestPercentage = config.getInt("bank.interests.withdraw", 0);
                double maxWithdrawableAmount = bankBalance / (1 + (double) interestPercentage / 100);

                amount = (int) Math.floor(maxWithdrawableAmount); // Redondear hacia abajo para evitar exceder el límite

                if (amount <= 0) {
                    withdrawFailure(player);
                    return;
                }
            } else {
                amount = Integer.parseInt(amountString);
                if (amount <= 0) {
                    withdrawFailure(player);
                    return;
                }
            }

            if (bankBalance < amount) {
                withdrawExceeds(player);
                return;
            }

            double interest = amount * ((double) interestPercentage / 100);
            totalAmount = amount + (int) Math.round(interest);

            // Verificar si el total a retirar excede el maxStorage
            if (totalAmount > maxStorage) {
                withdrawExceeds(player);
                return;
            }

            // Verificar si el saldo después de la retirada sería negativo
            if (bankBalance - totalAmount < 0) {
                interestsWithdrawExceeds(player);
                return;
            }

            // Realizar el retiro
            economy.depositPlayer(player, amount);
            bankConfig.set(playerPath + ".balance", bankBalance - totalAmount);
            plugin.getBankConfigManager().saveConfig();

            withdrawSuccess(player);
        } catch (NumberFormatException e) {
            withdrawFailure(player);
        }
    }

    private void handleMax(Player player, String type) {
        String playerPath = "bank." + player.getUniqueId() + "." + player.getName();

        if (type.equalsIgnoreCase("bal") || type.equalsIgnoreCase("balance")) {
            int level = bankConfig.getInt(playerPath + ".level", 1);
            maxStorage = getMaxStorageAmount(level);
            maxStorage(player);
        } else if (type.equalsIgnoreCase("level") || type.equalsIgnoreCase("lvl")) {
            maxLevel = getMaxBankLevel();
            maxLevelBank(player);
        } else {
            bankMax(player);
        }
    }

    private void handleTop(Player player) {
        Map<String, Integer> topBalances = new HashMap<>();

        for (String key : bankConfig.getConfigurationSection("bank").getKeys(false)) {
            for (String subKey : bankConfig.getConfigurationSection("bank." + key).getKeys(false)) {
                int balance = bankConfig.getInt("bank." + key + "." + subKey + ".balance");
                topBalances.put(subKey, balance);
            }
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(topBalances.entrySet());
        sortedList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        title(player);
        for (int i = 0; i < Math.min(10, sortedList.size()); i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);
            player.sendMessage(MessageUtils.getColoredMessage(getMessage("bank.top.entry").replaceAll("%plugin%", MineBank.prefix).replaceAll("%position%", String.valueOf(i + 1)).replace("%player%", entry.getKey()).replace("%balance%", String.valueOf(entry.getValue()))));
        }
    }

    private void handleLevel(Player player, String targetPlayerName) {
        String uuid;
        String name;
        this.targetPlayerName = targetPlayerName;

        if (targetPlayerName != null) {
            uuid = getPlayerUUIDByName(targetPlayerName);
            name = targetPlayerName;

            // Verifica si el UUID del jugador es válido
            if (uuid == null) {
                notFoundPlayer(player);
                return;
            }

            // Construye la ruta para el archivo de configuración
            String playerPath = "bank." + uuid + "." + name;

            // Obtén el nivel del jugador
            level = bankConfig.getInt(playerPath + ".level", 1);

            // Envía el mensaje adecuado usando playerLevel
            playerLevel(player);
        } else {
            // Construye la ruta para el archivo de configuración del jugador que ejecuta el comando
            String playerPath = "bank." + player.getUniqueId().toString() + "." + player.getName();

            // Obtén el nivel del jugador
            level = bankConfig.getInt(playerPath + ".level", 1);

            // Envía el mensaje adecuado usando yourLevel
            yourLevel(player);
        }
    }

    private void handleLevelUp(Player player) {
        String playerPath = "bank." + player.getUniqueId() + "." + player.getName();
        level = bankConfig.getInt(playerPath + ".level", 1);
        String levelData = config.getString("bank.level." + level);

        // Check if the next level exists
        String nextLevelData = config.getString("bank.level." + (level + 1));
        if (nextLevelData == null) {
            alreadyMaxLevel(player);
            return;
        }

        if (levelData == null) {
            unlockPriceError(player);
            return;
        }

        String[] parts = levelData.split(";");
        if (parts.length < 2) {
            unlockPriceError(player);
            return;
        }

        try {
            unlockPrice = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            unlockPriceError(player);
            return;
        }

        bankBalance = bankConfig.getInt(playerPath + ".balance", 0);

        if (bankBalance >= unlockPrice) {
            // Deduct from bank balance
            bankConfig.set(playerPath + ".balance", bankBalance - unlockPrice);
        }else {
            // Not enough money in both bank and player's balance
            notEnoughMoneyLevelUp(player);
            return;
        }

        // Increase the player's bank level
        bankConfig.set(playerPath + ".level", level + 1);
        plugin.getBankConfigManager().saveConfig();

        successLevelUp(player);
    }

    private void handleSet(CommandSender sender, String targetPlayerName, String type, String amountString, String... changeBalanceString) {
        Player player = (Player) sender;
        this.targetPlayerName = targetPlayerName;

        String uuid = getPlayerUUIDByName(targetPlayerName);
        if (uuid == null) {
            notFoundPlayer(player);
            return;
        }

        String playerPath = "bank." + uuid + "." + targetPlayerName;

        if (type.equalsIgnoreCase("bal") || type.equalsIgnoreCase("balance")) {
            try {
                int level = bankConfig.getInt(playerPath + ".level", 1);
                maxStorage = getMaxStorageAmount(level);

                if (amountString.equalsIgnoreCase("max")) {
                    this.amount = maxStorage;
                } else {
                    this.amount = Integer.parseInt(amountString);
                }

                if (this.amount > maxStorage) {
                    maxBalance(player);
                    return;
                }

                bankConfig.set(playerPath + ".balance", this.amount);
                plugin.getBankConfigManager().saveConfig();
                setBalanceSuccess(player);

            } catch (NumberFormatException e) {
                depositFailure(player);
            }
        } else if (type.equalsIgnoreCase("level") || type.equalsIgnoreCase("lvl")) {
            try {
                int maxLevel = getMaxBankLevel();

                if (amountString.equalsIgnoreCase("max")) {
                    this.level = maxLevel;
                } else {
                    this.level = Integer.parseInt(amountString);
                }

                if (this.level > maxLevel) {
                    maxLevel(player);
                    return;
                }

                // Obtener el nuevo maxStorage basado en el nivel que se va a establecer
                maxStorage = getMaxStorageAmount(this.level);

                // Cambiar el nivel del banco
                bankConfig.set(playerPath + ".level", this.level);
                plugin.getBankConfigManager().saveConfig();
                setLevelSuccess(player);

                // Manejo de changeBalanceString
                if (changeBalanceString.length > 0) {
                    boolean changeBalance = Boolean.parseBoolean(changeBalanceString[0]);

                    if (changeBalance) {
                        // Si es true, establecer el balance al maxStorage del nuevo nivel
                        this.amount = maxStorage;
                        bankConfig.set(playerPath + ".balance", maxStorage);
                        plugin.getBankConfigManager().saveConfig();
                        setBalanceSuccess(player);
                    } else {
                        // Si es false, ajustar el balance si excede el maxStorage
                        int currentBalance = bankConfig.getInt(playerPath + ".balance", 0);
                        if (currentBalance > maxStorage) {
                            this.amount = maxStorage;
                            bankConfig.set(playerPath + ".balance", maxStorage);
                        } else {
                            this.amount = currentBalance; // Mantener el balance actual si no excede maxStorage
                        }
                        // Guardar los cambios en la configuración después de ajustar el balance
                        plugin.getBankConfigManager().saveConfig();
                        setBalanceSuccess(player);
                    }
                }

            } catch (NumberFormatException e) {
                depositFailure(player);
            }
        } else {
            bankSet(sender);
        }
    }

    public void getBankHelp(CommandSender sender) {
        if (!sender.hasPermission("minebank.admin") && !sender.hasPermission("minebank.use") && !sender.hasPermission("minebank.helpbank")) {
            noPerm(sender);
            return;
        }
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        List<String> message = messagesConfigManager.getHelpBankMessage();
        for(String m : message){
            sender.sendMessage(MessageUtils.getColoredMessage(m));
        }
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

    private int getMaxBankLevel() {
        int maxLevel = 1;
        Set<String> levels = config.getConfigurationSection("bank.level").getKeys(false);
        for (String level : levels) {
            try {
                int levelNumber = Integer.parseInt(level);
                if (levelNumber > maxLevel) {
                    maxLevel = levelNumber;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return maxLevel;
    }

    private String getPlayerUUIDByName(String playerName) {
        // Recorre todas las entradas en bank.yml para encontrar el UUID correspondiente al nombre del jugador
        for (String uuid : bankConfig.getConfigurationSection("bank").getKeys(false)) {
            ConfigurationSection playerSection = bankConfig.getConfigurationSection("bank." + uuid);
            for (String name : playerSection.getKeys(false)) {
                if (name.equalsIgnoreCase(playerName)) {
                    return uuid;
                }
            }
        }
        return null; // Retorna null si no se encuentra el jugador
    }

    public void noPerm(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNoPermMessage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void notPlayer(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNotPlayer();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void bankUsage(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getBankUsage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void bankAdd(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getBankAdd();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void bankTake(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getBankTake();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void bankMax(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getBankMax();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void bankSet(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getBankSet();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void yourBalance(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getYourBalance();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%balance%", String.valueOf(bankBalance));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void playerBalance(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getPlayerBalance();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%player%", targetPlayerName).replace("%balance%", String.valueOf(targetBalance));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void notFoundPlayer(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNotFoundPlayer();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%player%", targetPlayerName);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void depositFailure(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getDepositFailure();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void notEnoughMoneyAdd(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNotEnoughMoneyAdd();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void notEnoughMoneyLevelUp(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getNotEnoughMoneyLevelUp();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%unlockPrice%", String.valueOf(unlockPrice).replaceAll("%balance%", String.valueOf(bankBalance)));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void balanceExceeds(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getBalanceExceeds();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%maxStorage%", String.valueOf(maxStorage));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void depositSuccess(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getDepositSuccess();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%amount%", String.valueOf(amount));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void withdrawFailure(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getWithdrawFailure();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void withdrawExceeds(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getWithdrawExceeds();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void interestsWithdrawExceeds(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getInterestsWithdrawExceeds();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void withdrawSuccess(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getWithdrawSuccess();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replace("%amount%", String.valueOf(amount)).replace("%percentage%", String.valueOf(interestPercentage)).replace("%totalDeducted%", String.valueOf(totalAmount));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void maxStorage(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getMaxStorage();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%maxStorage%", String.valueOf(maxStorage));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void maxLevelBank(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getMaxLevelBank();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%maxLevel%", String.valueOf(maxLevel));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void title(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getTitle();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void yourLevel(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getYourLevel();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%level%", String.valueOf(level));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void playerLevel(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getPlayerLevel();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%player%", targetPlayerName).replaceAll("%level%", String.valueOf(level));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void alreadyMaxLevel(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getAlreadyMaxLevel();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix);
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void unlockPriceError(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getUnlockPriceError();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%unlockLevel%", String.valueOf(level + 1));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void successLevelUp(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getSuccessLevelUp();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%newLevel%", String.valueOf(level + 1));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void setLevelSuccess(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getSetLevelSuccess();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%player%", targetPlayerName).replace("%amount%", String.valueOf(level));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void setBalanceSuccess(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getSetBalanceSuccess();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%player%", targetPlayerName).replace("%amount%", String.valueOf(amount));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void maxLevel(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getMaxLevel();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%maxLevel%", String.valueOf(maxLevel));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }
    public void maxBalance(CommandSender sender){
        MessagesConfigManager messagesConfigManager = plugin.getMessagesConfigManager();
        String mensaje = messagesConfigManager.getMaxBalance();
        if (mensaje != null) {
            mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%maxStorage%", String.valueOf(maxStorage));
            sender.sendMessage(MessageUtils.getColoredMessage(mensaje));
        }
    }

}