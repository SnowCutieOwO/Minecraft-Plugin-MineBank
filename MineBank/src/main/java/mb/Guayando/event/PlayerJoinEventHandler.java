package mb.Guayando.event;

import mb.Guayando.MineBank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerJoinEventHandler implements Listener {

    private final MineBank plugin;
    private final FileConfiguration bankConfig;

    public PlayerJoinEventHandler(MineBank plugin) {
        this.plugin = plugin;
        this.bankConfig = plugin.getBankConfigManager().getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerPath = "bank." + player.getUniqueId() + "." + player.getName();
        if (!bankConfig.contains(playerPath)) {
            // Establecer saldo inicial y nivel de banco al unirse
            bankConfig.set(playerPath + ".balance", 0);
            bankConfig.set(playerPath + ".level", 1);
            plugin.getBankConfigManager().saveConfig(); // Guarda la configuración
        }
    }
}
