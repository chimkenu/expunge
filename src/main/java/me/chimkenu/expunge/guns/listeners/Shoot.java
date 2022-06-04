package me.chimkenu.expunge.guns.listeners;

import me.chimkenu.expunge.Utils;
import me.chimkenu.expunge.enums.Weapons;
import me.chimkenu.expunge.game.SetSet;
import me.chimkenu.expunge.guns.RayTrace;
import me.chimkenu.expunge.guns.shoot.ShootGrenade;
import me.chimkenu.expunge.guns.shoot.ShootParticle;
import me.chimkenu.expunge.guns.guns.Gun;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Shoot implements Listener {
    public static final HashMap<Player, HashMap<Weapons.Guns, Integer>> playerAmmo = new HashMap<>();
    public static int getAmmo(Player player, Weapons.Guns gun) {
        playerAmmo.putIfAbsent(player, new HashMap<>());
        playerAmmo.get(player).putIfAbsent(gun, gun.getGun().getMaxAmmo());
        return playerAmmo.get(player).get(gun);
    }
    public static HashMap<Weapons.Guns, Integer> getAmmo(Player player) {
        playerAmmo.putIfAbsent(player, SetSet.getDefaultAmmo());
        return playerAmmo.get(player);
    }
    public static void setAmmo(Player player, Weapons.Guns gun, int amount) {
        playerAmmo.putIfAbsent(player, new HashMap<>());
        HashMap<Weapons.Guns, Integer> ammoMap = playerAmmo.get(player);
        ammoMap.put(gun, Math.max(0, amount));
        playerAmmo.put(player, ammoMap);
    }
    public static void setAmmo(Player player, HashMap<Weapons.Guns, Integer> ammoMap) {
        playerAmmo.put(player, ammoMap);
    }

    private void fireGun(Player player, Gun gun) {
        ItemStack item = player.getInventory().getItemInMainHand();

        Weapons.Guns weaponGun = Utils.getEnumFromGun(gun.getClass());
        if (getAmmo(player, weaponGun) < 1) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§lOut of Ammo!"));
            return;
        }

        if (item.isSimilar(gun.getGun()) && player.getCooldown(gun.getMaterial()) < 1) {
            setAmmo(player, weaponGun, getAmmo(player, weaponGun) - 1);
            player.setLevel(getAmmo(player, weaponGun));

            // OFFSET MODIFICATION : Offset is increased based on certain actions/conditions
            double offset = 0.001;
            Location loc = player.getLocation();

            // is on ladder
            if (loc.subtract(0, 0.1, 0).getBlock().getType() == Material.LADDER) offset += 0.005;

            // is not on solid ground
            if (!(loc.subtract(.31, 0.1, .31).getBlock().getType().isSolid() ||
                    loc.subtract(-.31, 0.1, -.31).getBlock().getType().isSolid() ||
                    loc.subtract(.31, 0.1, -.31).getBlock().getType().isSolid() ||
                    loc.subtract(-.31, 0.1, .31).getBlock().getType().isSolid() ||
                    loc.subtract(0, 0.1, 0).getBlock().getType().isSolid()
                    ))
                offset += 0.01;

            // is moving a lot
            if (player.getVelocity().getX() != 0) offset += 0.005;
            if (player.getVelocity().getZ() != 0) offset += 0.005;
            if (player.isSprinting()) offset += 0.005;

            // is using a smg
            if (gun.getName().contains("SMG")) offset += 0.005;
            else if (gun.getName().contains("MP5")) offset += 0.005;

            if (gun.getName().contains("Grenade")) ShootGrenade.shoot(player);
            else {
                if (gun.getPellets() > 1) {
                    offset += ThreadLocalRandom.current().nextDouble(0.05, 0.1);
                }

                for (int i = 0; i < gun.getPellets(); i++) {
                    ShootParticle.shoot(gun.getParticle(), gun.getRange(), gun.getDamage(), player, gun.getEntitiesToHit(), offset, gun.getPellets() > 1);
                }
            }

            player.getWorld().playSound(player.getLocation(), gun.getSound(), SoundCategory.PLAYERS, 1, gun.getPitch());
            player.setCooldown(gun.getMaterial(), gun.getCooldown());

            if (item.getAmount() == 1) {
                Reload.reload(player);
            } else {
                item.setAmount(item.getAmount()-1);
            }
        }
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            Player player = e.getPlayer();
            Gun gun = Utils.getPlayerHeldGun(player.getInventory().getItemInMainHand());

            if (gun != null) {
                e.setCancelled(true);
                fireGun(player, gun);
            }
        }
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent e) {
        // see if it was an ammo pile
        if (e.getRightClicked() instanceof ArmorStand armorStand && armorStand.getScoreboardTags().contains("AMMO_PILE")) {
            return;
        }

        Player player = e.getPlayer();
        Gun gun = Utils.getPlayerHeldGun(player.getInventory().getItemInMainHand());
        if (gun != null) {
            e.setCancelled(true);
            fireGun(player, gun);
        }
    }

    // custom weapon really broken
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType().equals(Material.SHEARS) && item.getItemMeta() != null && item.getItemMeta().getDisplayName().contains("RAILGUN")) {
            item.setAmount(item.getAmount()-1);

            Vector direction = e.getPlayer().getEyeLocation().getDirection();
            RayTrace ray = new RayTrace(e.getPlayer().getEyeLocation().toVector(), direction);
            ArrayList<Vector> positions = ray.traverse(50, 1);

            for (Vector v : positions) {
                e.getPlayer().getWorld().createExplosion(v.toLocation(e.getPlayer().getWorld()), 4, false, false);
            }
        }
    }
}
