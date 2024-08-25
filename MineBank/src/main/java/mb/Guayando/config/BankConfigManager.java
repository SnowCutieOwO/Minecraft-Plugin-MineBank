package mb.Guayando.config;

import mb.Guayando.MineBank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BankConfigManager {

    private MineBank plugin;
    private File bankFile;
    private FileConfiguration bankConfig;

    public BankConfigManager(MineBank plugin) {
        this.plugin = plugin;
        createBankConfig();
    }

    private void createBankConfig() {
        bankFile = new File(plugin.getDataFolder(), "bank.yml");
        if (!bankFile.exists()) {
            plugin.saveResource("bank.yml", false); // Copia el archivo por defecto si no existe
        }
        bankConfig = YamlConfiguration.loadConfiguration(bankFile);
    }

    public FileConfiguration getConfig() {
        return bankConfig;
    }

    public void saveConfig() {
        try {
            bankConfig.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //public void reloadConfig() { bankConfig = YamlConfiguration.loadConfiguration(bankFile); }
    public void reloadConfig() {
        if (bankFile == null) {
            bankFile = new File(plugin.getDataFolder(), "bank.yml");
        }
        bankConfig = YamlConfiguration.loadConfiguration(bankFile);

        // Recargar cualquier archivo de configuración predeterminado del plugin si existe
        InputStream defConfigStream = plugin.getResource("bank.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            bankConfig.setDefaults(defConfig);
        }
    }
}
