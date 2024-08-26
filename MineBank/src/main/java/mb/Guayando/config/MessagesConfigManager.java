package mb.Guayando.config;

import mb.Guayando.MineBank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MessagesConfigManager {

    private MineBank plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    private String reloadMessage, versionMessage, authorMessage, noPermMessage, noArgumentMessage, consoleErrorMessage;
    private List<String> helpMessage, permissionsMessage, helpBankMessage;
    private String bankUsage, bankAdd, bankTake, bankSet, bankTop, bankBal, bankMax, bankLevel, bankLevelup;
    private String yourBalance, playerBalance;
    private String withdrawSuccess, withdrawFailure, withdrawExceeds, interestsWithdrawExceeds;
    private String depositSuccess, balanceExceeds, depositFailure, notEnoughMoneyAdd;
    private String maxBalance, maxLevel, setBalanceSuccess, setLevelSuccess;
    private String title, entry;
    private String currentLevel;
    private String unlockPriceError, notEnoughMoneyLevelUp, successLevelUp, alreadyMaxLevel;
    private String maxStorage, maxLevelBank;
    private String receivedProfit, maxStorageProfit, minStorageProfit;
    private String notPlayer, notFoundPlayer;

    public MessagesConfigManager(MineBank plugin) {
        this.plugin = plugin;
        createMessagesConfig();
        loadMessages();
    }

    private void createMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false); // Copia el archivo por defecto si no existe
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return messagesConfig;
    }

    public void reloadConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        loadMessages();
    }

    private void loadMessages() {
        helpMessage = messagesConfig.getStringList("messages.help");
        helpBankMessage = messagesConfig.getStringList("messages.help-bank");
        reloadMessage = messagesConfig.getString("messages.reload");
        versionMessage = messagesConfig.getString("messages.version");
        authorMessage = messagesConfig.getString("messages.author");
        noPermMessage = messagesConfig.getString("messages.no-perm");
        noArgumentMessage = messagesConfig.getString("messages.command-no-argument");
        consoleErrorMessage = messagesConfig.getString("messages.console-error");
        permissionsMessage = messagesConfig.getStringList("messages.permissions");

        bankUsage = messagesConfig.getString("bank.usage.bank");
        bankAdd = messagesConfig.getString("bank.usage.add");
        bankTake = messagesConfig.getString("bank.usage.take");
        bankSet = messagesConfig.getString("bank.usage.set");
        //bankTop = messagesConfig.getString("bank.usage.top");
        bankBal = messagesConfig.getString("bank.usage.bal");
        bankMax = messagesConfig.getString("bank.usage.max");
        //bankLevel = messagesConfig.getString("bank.usage.level");
        //bankLevelup = messagesConfig.getString("bank.usage.levelup");

        yourBalance = messagesConfig.getString("bank.bal.yourBalance");
        playerBalance = messagesConfig.getString("bank.bal.playerBalance");

        withdrawSuccess = messagesConfig.getString("bank.take.withdrawSuccess");
        withdrawFailure = messagesConfig.getString("bank.take.withdrawFailure");
        withdrawExceeds = messagesConfig.getString("bank.take.withdrawExceeds");
        interestsWithdrawExceeds = messagesConfig.getString("bank.take.interestsWithdrawExceeds");

        depositSuccess = messagesConfig.getString("bank.add.depositSuccess");
        balanceExceeds = messagesConfig.getString("bank.add.balanceExceeds");
        depositFailure = messagesConfig.getString("bank.add.depositFailure");
        notEnoughMoneyAdd = messagesConfig.getString("bank.add.notEnoughMoney");

        maxBalance = messagesConfig.getString("bank.set.maxBalance");
        maxLevel = messagesConfig.getString("bank.set.maxLevel");
        setBalanceSuccess = messagesConfig.getString("bank.set.setBalanceSuccess");
        setLevelSuccess = messagesConfig.getString("bank.set.setLevelSuccess");
        depositFailure = messagesConfig.getString("bank.set.depositFailure");

        title = messagesConfig.getString("bank.top.title");
        //entry = messagesConfig.getString("bank.top.entry");

        currentLevel = messagesConfig.getString("bank.level.current");

        unlockPriceError = messagesConfig.getString("bank.levelup.unlockPriceError");
        notEnoughMoneyLevelUp = messagesConfig.getString("bank.levelup.notEnoughMoney");
        successLevelUp = messagesConfig.getString("bank.levelup.success");
        alreadyMaxLevel = messagesConfig.getString("bank.levelup.alreadyMax");

        maxStorage = messagesConfig.getString("bank.max.maxStorage");
        maxLevelBank = messagesConfig.getString("bank.max.maxLevel");

        receivedProfit = messagesConfig.getString("bank.profit.received");
        minStorageProfit = messagesConfig.getString("bank.profit.min-storage");
        maxStorageProfit = messagesConfig.getString("bank.profit.max-storage");

        notPlayer = messagesConfig.getString("bank.notPlayer");
        notFoundPlayer = messagesConfig.getString("bank.notFoundPlayer");
    }

    public List<String> getHelpMessage() {
        return helpMessage;
    }

    public List<String> getHelpBankMessage() {
        return helpBankMessage;
    }

    public String getReloadMessage() {
        return reloadMessage;
    }

    public String getVersionMessage() {
        return versionMessage;
    }

    public String getAuthorMessage() {
        return authorMessage;
    }

    public String getNoPermMessage() {
        return noPermMessage;
    }

    public String getNoArgumentMessage() {
        return noArgumentMessage;
    }

    public String getConsoleErrorMessage() {
        return consoleErrorMessage;
    }

    public List<String> getPermissionsMessage() {
        return permissionsMessage;
    }

    public String getBankUsage() {
        return bankUsage;
    }

    public String getBankAdd() {
        return bankAdd;
    }

    public String getBankTake() {
        return bankTake;
    }

    public String getBankSet() {
        return bankSet;
    }

    public String getBankTop() {
        return bankTop;
    }

    public String getBankBal() {
        return bankBal;
    }

    public String getBankMax() {
        return bankMax;
    }

    public String getBankLevel() {
        return bankLevel;
    }

    public String getBankLevelup() {
        return bankLevelup;
    }

    public String getYourBalance() {
        return yourBalance;
    }

    public String getPlayerBalance() {
        return playerBalance;
    }

    public String getWithdrawSuccess() {
        return withdrawSuccess;
    }

    public String getWithdrawFailure() {
        return withdrawFailure;
    }

    public String getWithdrawExceeds() {
        return withdrawExceeds;
    }

    public String getInterestsWithdrawExceeds() {
        return interestsWithdrawExceeds;
    }

    public String getDepositSuccess() {
        return depositSuccess;
    }

    public String getBalanceExceeds() {
        return balanceExceeds;
    }

    public String getDepositFailure() {
        return depositFailure;
    }

    public String getNotEnoughMoneyAdd() {
        return notEnoughMoneyAdd;
    }

    public String getMaxLevel() {
        return maxLevel;
    }

    public String getSetBalanceSuccess() {
        return setBalanceSuccess;
    }

    public String getSetLevelSuccess() {
        return setLevelSuccess;
    }

    public String getTitle() {
        return title;
    }

    public String getEntry() {
        return entry;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public String getUnlockPriceError() {
        return unlockPriceError;
    }

    public String getNotEnoughMoneyLevelUp() {
        return notEnoughMoneyLevelUp;
    }

    public String getSuccessLevelUp() {
        return successLevelUp;
    }

    public String getAlreadyMaxLevel() {
        return alreadyMaxLevel;
    }

    public String getMaxStorage() {
        return maxStorage;
    }

    public String getMaxLevelBank() {
        return maxLevelBank;
    }

    public String getReceivedProfit() {
        return receivedProfit;
    }

    public String getMinStorageProfit(){
        return minStorageProfit;
    }

    public String getMaxStorageProfit() {
        return maxStorageProfit;
    }

    public String getNotPlayer() {
        return notPlayer;
    }

    public String getNotFoundPlayer() {
        return notFoundPlayer;
    }

    public String getMaxBalance() {
        return maxBalance;
    }
}