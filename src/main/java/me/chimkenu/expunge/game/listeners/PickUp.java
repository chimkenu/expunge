package me.chimkenu.expunge.game.listeners;

import me.chimkenu.expunge.Expunge;
import me.chimkenu.expunge.Utils;
import me.chimkenu.expunge.enums.Utilities;
import me.chimkenu.expunge.enums.Weapons;
import me.chimkenu.expunge.guns.listeners.Shoot;
import me.chimkenu.expunge.guns.weapons.guns.Gun;
import me.chimkenu.expunge.guns.utilities.healing.Healing;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class PickUp implements Listener {
    private HashMap<ItemStack, Integer> getItems() {
        HashMap<ItemStack, Integer> items = new HashMap<>();
        for (Weapons.Guns weapon : Weapons.Guns.values()) {
            items.put(weapon.getGun().getWeapon(), 0);
        }
        for (Weapons.Melees weapon : Weapons.Melees.values()) {
            items.put(weapon.getMelee().getWeapon(), 1);
        }
        for (Utilities.Throwables util : Utilities.Throwables.values()) {
            items.put(util.getUtility().getUtility(), 2);
        }
        for (Utilities.Healings util : Utilities.Healings.values()) {
            items.put(util.getUtility().getUtility(), util.isMain() ? 3 : 4);
        }
        return items;
    }

    private ItemStack getValidItemStack(ItemStack item) {
        Gun gun = Utils.getPlayerHeldGun(item);
        if (gun != null) return gun.getWeapon();
        for (ItemStack itemStack : getItems().keySet()) {
            if (item.isSimilar(itemStack)) {
                return itemStack;
            }
        }
        return null;
    }

    private boolean isItemInvalid(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getLore() != null && meta.getLore().contains("invulnerable")) return true;
        return getValidItemStack(item) == null;
    }

    private int getHotbarSlot(ItemStack item) {
        return getItems().get(getValidItemStack(item));
    }

    private final HashMap<Player, Long> pickUp = new HashMap<>();
    private boolean canPickUp(Player player) {
        pickUp.putIfAbsent(player, System.currentTimeMillis() - 501);
        return ((System.currentTimeMillis() - pickUp.get(player)) > 500);
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (!Expunge.isGameRunning) {
            e.setCancelled(true);
            return;
        }
        if (!Expunge.playing.getKeys().contains(player)) {
            e.setCancelled(true);
            return;
        }
        if (!Expunge.playing.isAlive(player)) {
            e.setCancelled(true);
            return;
        }
        if (Healing.usingUtility.contains(player)) {
            e.setCancelled(true);
            return;
        }
        if (!player.isSneaking()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("??eSneak to pick up."));
            e.setCancelled(true);
            return;
        }

        Item item = e.getItem();
        if (isItemInvalid(item.getItemStack())) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("??cYou can't pick this up."));
            e.setCancelled(true);
            return;
        }

        if (!canPickUp(player)) {
            e.setCancelled(true);
            return;
        }
        pickUp.put(player, System.currentTimeMillis());

        int hotbarSlot = getHotbarSlot(item.getItemStack());
        Gun gun = Utils.getPlayerHeldGun(item.getItemStack());

        if (player.getInventory().containsAtLeast(item.getItemStack(), 1)) {
            e.setCancelled(true);

            // add to ammo if gun
            if (gun != null) {
                ItemStack gunInHotbar = player.getInventory().getItem(hotbarSlot);
                if (gunInHotbar != null) Shoot.setAmmo(gunInHotbar, Math.min(Shoot.getAmmo(gunInHotbar) + Shoot.getAmmo(item.getItemStack()), gun.getMaxAmmo()));
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("??9+Ammo"));
                item.setPickupDelay(20);
                if (!item.isInvulnerable()) item.remove();
                return;
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("??cYou cannot carry any more of this item."));
            return;
        }

        e.setCancelled(true);
        player.getWorld().playSound(item.getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1f);

        if (gun != null && Utils.getEnumFromGun(gun.getClass()) == Weapons.Guns.PISTOL) hotbarSlot = 1;
        ItemStack hotbarItem = player.getInventory().getItem(hotbarSlot);

        if (hotbarItem != null && !(hotbarItem.getType().equals(Material.AIR))) {
            ItemMeta meta = hotbarItem.getItemMeta();
            if (!(meta != null && meta.getLore() != null && meta.getLore().contains("invulnerable"))) {
                Item itemSwapped = player.getWorld().dropItem(player.getLocation(), hotbarItem);
                itemSwapped.setPickupDelay(20);
                itemSwapped.addScoreboardTag("ITEM");
            }
        }

        player.getInventory().setItem(hotbarSlot, item.getItemStack());
        // invulnerable items can be picked up more than once
        if (item.isInvulnerable())
            item.setPickupDelay(20);
        else
            item.remove();
    }

    private void cancelDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getAmount() > 1) {
            e.getItemDrop().remove();
            mainHand.setAmount(mainHand.getAmount() + 1);
        } else e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (!Expunge.isGameRunning) {
            cancelDrop(e);
            return;
        }
        if (!Expunge.playing.getKeys().contains(player)) {
            cancelDrop(e);
            return;
        }
        if (!Expunge.playing.isAlive(player)) {
            cancelDrop(e);
            return;
        }

        ItemStack item = e.getItemDrop().getItemStack();
        if (isItemInvalid(item)) {
            // fix item if broken
            if (item.getItemMeta() != null && item.getItemMeta() instanceof Damageable damageable) {
                // this is added to fix reload as it breaks when opening the inventory, however it can be abused
                // to avoid such, add a cooldown longer than all reload times
                if (damageable.getDamage() > 0) {
                    damageable.setDamage(0);
                    item.setItemMeta(damageable);
                    player.setCooldown(item.getType(), 20 * 5);
                }
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("??cYou can't drop this."));
            cancelDrop(e);
            return;
        }

        item.setAmount(player.getInventory().getItemInMainHand().getAmount() + 1);
        player.getInventory().remove(item.getType());
        e.getItemDrop().setItemStack(item);
        e.getItemDrop().addScoreboardTag("ITEM");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent e) {
        if (Expunge.isGameRunning)
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent e) {
        if (Expunge.isGameRunning)
            e.setCancelled(true);
    }
}
