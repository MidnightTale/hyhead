package xyz.hynse.hyhead;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Random;

public final class Hyhead extends JavaPlugin implements Listener {

    private double headDropChance;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hyheadreload")) {
            reloadConfig();
            loadConfig();
            sender.sendMessage("Hyhead reloaded");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("detach")) {
            if (sender instanceof Player player) {
                dropPlayerHead(player);
                player.setHealth(0);
            } else {
                sender.sendMessage("Only players can use this command.");
            }
            return true;
        }
        return false;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && shouldDropHead()) {
            dropPlayerHead(victim);
        }
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        headDropChance = config.getDouble("head_drop_chance", 0.7);
    }

    private boolean shouldDropHead() {
        return new Random().nextDouble() <= headDropChance;
    }

    private void dropPlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(player);
        playerHead.setItemMeta(skullMeta);

        player.getInventory().addItem(playerHead);
    }
}