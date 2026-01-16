package cc.aesth.aethermenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AetherMenu extends JavaPlugin implements Listener {

    private final Map<Integer, String> slotToServer = new HashMap<>();
    private String menuTitle;
    private int menuSize;
    private boolean freeze;
    private boolean hidePlayers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadAetherMenu();

        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    /* loading config */

    private void reloadAetherMenu() {
        reloadConfig();

        menuTitle = getConfig().getString("menu.title");
        menuSize = getConfig().getInt("menu.size");
        freeze = getConfig().getBoolean("menu.freeze-player");
        hidePlayers = getConfig().getBoolean("menu.hide-players");

        slotToServer.clear();
        ConfigurationSection items = getConfig().getConfigurationSection("menu.items");

        if (items == null) {
            getLogger().warning("no menu items found in config.yml");
            return;
        }

        for (String key : items.getKeys(false)) {
            int slot = items.getInt(key + ".slot");
            String server = items.getString(key + ".server");
            slotToServer.put(slot, server);
        }

        getLogger().info("aethermenu config reloaded");
    }

    /* command */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("aethermenu.reload")) {
                sender.sendMessage("§cyou need operator");
                return true;
            }

            reloadAetherMenu();
            sender.sendMessage("§aaethermenu configuration reloaded");
            return true;
        }

        sender.sendMessage("§cusage: /aethermenu reload");
        return true;
    }

    /* events */

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (hidePlayers) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(this, other);
                other.hidePlayer(this, p);
            }
        }

        Bukkit.getScheduler().runTaskLater(this, () -> openMenu(p), 1);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!freeze) return;
        if (!e.getFrom().getBlock().equals(e.getTo().getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!e.getView().getTitle().equals(menuTitle)) return;

        e.setCancelled(true);

        int slot = e.getSlot();
        if (!slotToServer.containsKey(slot)) return;

        connect(p, slotToServer.get(slot));
    }

    /* menu */

    private void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, menuSize, menuTitle);
        ConfigurationSection items = getConfig().getConfigurationSection("menu.items");

        if (items == null) return;

        for (String key : items.getKeys(false)) {
            Material mat = Material.valueOf(items.getString(key + ".material"));
            String name = items.getString(key + ".name");
            int slot = items.getInt(key + ".slot");

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
        }

        p.openInventory(inv);
    }

    /* velocity connect */

    private void connect(Player p, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (Exception ignored) {}
    }
}
