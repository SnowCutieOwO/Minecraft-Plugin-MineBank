package mb.Guayando;

import mb.Guayando.utils.Metrics;
import mb.Guayando.config.MessagesConfigManager;
import mb.Guayando.event.PlayerJoinEventHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import mb.Guayando.commands.ComandoPrincipal;
import mb.Guayando.commands.ComandoBank;
import mb.Guayando.config.MainConfigManager;
import mb.Guayando.config.BankConfigManager;
import mb.Guayando.utils.MessageUtils;
import mb.Guayando.utils.UpdateChecker;
import mb.Guayando.tasks.BankTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MineBank extends JavaPlugin implements Listener {

    private Economy economy;
    public static String prefix = "&4[&6&lMineBank&4] ";
    public String version = getDescription().getVersion();
    public String latestversion;
    private MainConfigManager configManager;
    private MessagesConfigManager messagesConfigManager;
    private BankConfigManager bankConfigManager;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&9<------------------------------------>"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&fEnabled, (&aVersion: &b"+version+"&f)"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&6Thanks for using my plugin :)"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&eMade by &ddavigamer161"));
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&cVault not found!"));
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&cPlease ensure Vault and an economy plugin are installed."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix + "&aVault found and linked successfully."));
        }
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&9<------------------------------------>"));
        configManager = new MainConfigManager(this);
        messagesConfigManager = new MessagesConfigManager(this);
        bankConfigManager = new BankConfigManager(this);
        long interval = getConfig().getLong("bank.profit.interval") * 20L; // Convertir segundos en ticks
        new BankTask(this).runTaskTimer(this, interval, interval);
        registrarComandos();
        registrarEventos();
        comprobarActualizaciones();
        // Bstats
        int pluginId = 23185; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&fDisabled, (&aVersion: &b"+version+"&f)"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&6Thanks for use my plugin :)"));
    }

    public void registrarComandos(){
        this.getCommand("minebank").setExecutor(new ComandoPrincipal(this));
        this.getCommand("bank").setExecutor(new ComandoBank(this));

    }
    public void registrarEventos(){
        getServer().getPluginManager().registerEvents(new UpdateChecker(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(this), this);
    }

    public MainConfigManager getMainConfigManager() {
        return configManager;
    }
    public MessagesConfigManager getMessagesConfigManager() {
        return messagesConfigManager;
    }
    public BankConfigManager getBankConfigManager() {
        return bankConfigManager;
    }

    public void comprobarActualizaciones(){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=119147").openConnection();
            int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (latestversion.length() <= 10) {
                if(!version.equals(latestversion)){
                    Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&bThere is a new version available. &e(&7"+latestversion+"&e)"));
                    Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&bYou can download it at:&f https://www.spigotmc.org/resources/119147/"));
                }
            }
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(prefix+"&cError while checking update."));
        }
    }

    public String getVersion(){
        return this.version;
    }
    public String getLatestVersion(){
        return this.latestversion;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) { return false; }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) { return false; } economy = rsp.getProvider(); return economy != null;
    }
    public Economy getEconomy() {
        return economy;
    }
}