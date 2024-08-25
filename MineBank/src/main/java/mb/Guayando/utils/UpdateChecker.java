package mb.Guayando.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import mb.Guayando.MineBank;
import mb.Guayando.config.MainConfigManager;

public class UpdateChecker implements Listener{
    private MineBank plugin;
    public UpdateChecker(MineBank plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void CheckUpdate(PlayerJoinEvent event) {
        try{
            Player jugador = event.getPlayer();
            boolean updateChecker = plugin.getConfig().getBoolean("config.update-checker");
            boolean isOutdatedVersion = !(plugin.getVersion().equals(plugin.getLatestVersion()));

            // Verificar si el mensaje de actualización debe enviarse
            if (updateChecker && isOutdatedVersion) {
                if (jugador.isOp() || jugador.hasPermission("minebank.updatechecker") && !jugador.hasPermission("minebank.admin")) {
                    MainConfigManager configManager = plugin.getMainConfigManager();
                    String mensaje = configManager.getUpdateCheckerMessage();
                    if (mensaje != null) {
                        mensaje = mensaje.replaceAll("%plugin%", MineBank.prefix).replaceAll("%version%", plugin.getVersion()).replaceAll("%latestversion%", plugin.getLatestVersion()).replaceAll("%link%", "https://www.spigotmc.org/resources/118952/");
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje));
                    }
                }
            }
        }catch (NullPointerException e){
            //plugin.getLogger().warning(MessageUtils.getColoredMessage("[CustomDrop] &7NullPointerException, PlayerJoinEvent, UpdateChecker"));
        }
    }
}