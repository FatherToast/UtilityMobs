package toast.utilityMobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum EnumUpgrade
{
    DEFAULT("default", null),
    MULTISHOT("multishot", null),
    KILLER("killer", Items.diamond),
    FIRE("fire", Items.iron_ingot),
    FEATHER("feather", Items.feather),
    SLOW("slow", Items.slime_ball),
    EGG("egg", Items.egg),
    SIGHT("sight", Items.ender_pearl),
    EXPLOSIVE("explosive", Items.gunpowder),
    POISON("poison", Items.spider_eye),
    FIRE_EXPLOSIVE("fire_explosive", Items.fire_charge);


    // Returns the appropriate upgrade out of the array of allowed upgrades.
    public static EnumUpgrade getUpgrade(EnumUpgrade[] upgrades, ItemStack upgradeStack) {
        if (upgradeStack != null) {
            Item upgradeItem = upgradeStack.getItem();
            for (EnumUpgrade upgrade : upgrades) {
                if (upgrade.upgradeItem == upgradeItem)
                    return upgrade;
            }
        }
        return EnumUpgrade.DEFAULT;
    }

    // Returns the appropriate upgrade to be applied from the item stack.
    public static EnumUpgrade getUpgrade(ItemStack upgradeStack) {
        return EnumUpgrade.getUpgrade(EnumUpgrade.values(), upgradeStack);
    }

    public final String upgradeName;
    public final Item upgradeItem;

    private EnumUpgrade(String id, Item upgrade) {
        this.upgradeName = id;
        this.upgradeItem = upgrade;
    }

    // Applies this arrow effect to the arrow and initializes arrow stats.
    public void applyToArrow(EntityArrow arrow) {
        switch (this) {
            case EXPLOSIVE:
                arrow.setDamage(arrow.getDamage() - 1.0);
                break;
            case FIRE:
                arrow.setDamage(arrow.getDamage() - 1.0);
                arrow.setFire(100);
                break;
            case FIRE_EXPLOSIVE:
                arrow.setDamage(arrow.getDamage() - 2.0);
                arrow.setFire(100);
                break;
            case KILLER:
                arrow.setDamage(arrow.getDamage() * 1.5 + 1.0);
                break;
            default:
                break;
        }
        if (arrow.getDamage() <= 0.0) {
            arrow.setDamage(Double.MIN_VALUE);
        }
        this.applyTo(arrow);
    }

    // Applies this arrow effect to the entity.
    public void applyTo(Entity entity) {
        entity.getEntityData().setBoolean("UM|" + this.upgradeName, true);
    }

    // Safely returns the arrow effect with the given ID.
    public boolean isApplied(Entity entity) {
        return entity.getEntityData().getBoolean("UM|" + this.upgradeName);
    }
}