package me.chimkenu.expunge;

import me.chimkenu.expunge.enums.Weapons;
import me.chimkenu.expunge.guns.guns.*;
import me.chimkenu.expunge.guns.melees.Melee;
import me.chimkenu.expunge.guns.utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Utils {
    public static ItemStack newItem(Material material, String displayName, boolean isGlowing, int amount) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            if (isGlowing) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack newItem(Material material, String displayName, boolean isGlowing) {
        return newItem(material, displayName, isGlowing, 1);
    }

    public static ItemStack newItem(Material material, String displayName) {
        return newItem(material, displayName, false);
    }

    public static ArrayList<Gun> getGuns() {
        ArrayList<Gun> guns = new ArrayList<>();
        for (Weapons.Guns gun : Weapons.Guns.values()) {
            guns.add(gun.getGun());
        }
        return guns;
    }

    public static ArrayList<Gun> getTier1Guns() {
        ArrayList<Gun> guns = new ArrayList<>();
        for (Weapons.Guns gun : Weapons.Guns.values()) {
            if (gun.getTier() == Weapons.Tier.TIER1) guns.add(gun.getGun());
        }
        return guns;
    }

    public static ArrayList<Gun> getTier2Guns() {
        ArrayList<Gun> guns = new ArrayList<>();
        for (Weapons.Guns gun : Weapons.Guns.values()) {
            if (gun.getTier() == Weapons.Tier.TIER2) guns.add(gun.getGun());
        }
        return guns;
    }

    public static ArrayList<Gun> getSpecialGuns() {
        ArrayList<Gun> guns = new ArrayList<>();
        for (Weapons.Guns gun : Weapons.Guns.values()) {
            if (gun.getTier() == Weapons.Tier.SPECIAL) guns.add(gun.getGun());
        }
        return guns;
    }

    public static ArrayList<Utility> getUtilities() {
        ArrayList<Utility> utilities = new ArrayList<>();
        utilities.add(new Grenade());
        utilities.add(new Smoke());
        utilities.add(new Molotov());
        utilities.add(new Flash());
        utilities.add(new Adrenaline());
        utilities.add(new Pills());
        utilities.add(new Medkit());
        return utilities;
    }

    public static ArrayList<Melee> getMelees() {
        ArrayList<Melee> melees = new ArrayList<>();
        for (Weapons.Melees weapon : Weapons.Melees.values()) {
            melees.add(weapon.getMelee());
        }
        return melees;
    }

    public static Gun getPlayerHeldGun(ItemStack item) {
        for (Gun gun : getGuns()) {
            if (item.isSimilar(gun.getGun())) {
                return gun;
            }
        }
        return null;
    }

    public static Utility getPlayerHeldUtility(ItemStack item) {
        for (Utility utility : getUtilities()) {
            if (item.isSimilar(utility.getUtility())) {
                return utility;
            }
        }
        return null;
    }

    public static Melee getPlayerHeldMelee(ItemStack item) {
        for (Melee melee : getMelees()) {
            if (item.isSimilar(melee.getMelee())) {
                return melee;
            }
        }
        return null;
    }

    public static <T extends Gun> Weapons.Guns getEnumFromGun(Class<T> gunToCheck) {
        for (Weapons.Guns gun : Weapons.Guns.values()) {
            if (gun.getGun().getClass().equals(gunToCheck)) {
                return gun;
            }
        }
        return null;
    }
}