package toast.utilityMobs;

import java.util.Random;

import toast.utilityMobs.network.MessageExplosion;

import net.minecraft.block.BlockColored;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.Explosion;

public abstract class EffectHelper
{
    // Clears the entity's AI tasks.
    public static void clearAI(EntityLiving entity) {
        EntityAITaskEntry[] oldAI = (EntityAITaskEntry[])entity.tasks.taskEntries.toArray(new EntityAITaskEntry[0]);
        int length = oldAI.length;
        for (int i = 0; i < length; i++) {
            entity.tasks.removeTask(oldAI[i].action);
        }
    }

    // Applies the potion's effect on the entity. If the potion is already active, its duration is increased up to the given duration and its amplifier is increased by the given amplifier + 1.
    public static void stackEffect(EntityLivingBase entity, Potion potion, int duration, int amplifier) {
        if (entity.isPotionActive(potion)) {
            PotionEffect potionEffect = entity.getActivePotionEffect(potion);
            entity.addPotionEffect(new PotionEffect(potion.id, Math.max(duration, potionEffect.getDuration()), potionEffect.getAmplifier() + amplifier + 1));
        }
        else {
            entity.addPotionEffect(new PotionEffect(potion.id, duration, amplifier));
        }
    }

    // Applies the potion's effect on the entity. If the potion is already active, its duration is increased up to the given duration and its amplifier is increased by the given amplifier + 1 up to the given amplifierMax.
    public static void stackEffect(EntityLivingBase entity, Potion potion, int duration, int amplifier, int amplifierMax) {
        if (amplifierMax < 0) {
            EffectHelper.stackEffect(entity, potion, duration, amplifier);
            return;
        }
        if (entity.isPotionActive(potion)) {
            PotionEffect potionEffect = entity.getActivePotionEffect(potion);
            entity.addPotionEffect(new PotionEffect(potion.id, Math.max(duration, potionEffect.getDuration()), Math.min(amplifierMax, potionEffect.getAmplifier() + amplifier + 1)));
        }
        else if (amplifier >= 0) {
            entity.addPotionEffect(new PotionEffect(potion.id, duration, Math.min(amplifier, amplifierMax)));
        }
    }

    // Causes the itemStack to glow as if it is enchanted.
    public static void setItemGlowing(ItemStack itemStack) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (!itemStack.stackTagCompound.hasKey("ench")) {
            itemStack.stackTagCompound.setTag("ench", new NBTTagList());
        }
    }

    // Sets the item stack's name.
    public static void setItemName(ItemStack itemStack, int rarityColor, String name) {
        EffectHelper.setItemName(itemStack, "\u00a7" + Integer.toHexString(rarityColor) + name);
    }
    public static void setItemName(ItemStack itemStack, String name) {
        if (itemStack != null) {
            itemStack.setStackDisplayName(name);
        }
    }

    // Removes all info text from an item stack.
    public static void clearItemText(ItemStack itemStack) {
        if (itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("display")) {
            itemStack.stackTagCompound.getCompoundTag("display").removeTag("Lore");
        }
    }

    // Sets or adds item stack info text.
    public static void setItemText(ItemStack itemStack, int rarityColor, String... text) {
        EffectHelper.clearItemText(itemStack);
        EffectHelper.addItemText(itemStack, rarityColor, text);
    }
    public static void setItemText(ItemStack itemStack, String... text) {
        EffectHelper.clearItemText(itemStack);
        EffectHelper.addItemText(itemStack, text);
    }
    public static void addItemText(ItemStack itemStack, int rarityColor, String... text) {
        String color = "\u00a7" + Integer.toHexString(rarityColor);
        for (int i = text.length; i-- > 0;) {
            text[i] = color + text[i];
        }
        EffectHelper.addItemText(itemStack, text);
    }
    public static void addItemText(ItemStack itemStack, String... text) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (!itemStack.stackTagCompound.hasKey("display")) {
            itemStack.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        NBTTagCompound displayTag = itemStack.stackTagCompound.getCompoundTag("display");
        if (!displayTag.hasKey("Lore")) {
            displayTag.setTag("Lore", new NBTTagList());
        }
        NBTTagList lore = displayTag.getTagList("Lore", new NBTTagString().getId());
        for (int i = 0; i < text.length; i++) {
            lore.appendTag(new NBTTagString(text[i]));
        }
    }

    // Applies the enchantment to the itemStack at the given level or changes an existing enchantment's level.
    public static void overrideEnchantment(ItemStack itemStack, Enchantment enchantment, int level) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (!itemStack.stackTagCompound.hasKey("ench")) {
            itemStack.stackTagCompound.setTag("ench", new NBTTagList());
        }
        NBTTagList enchList = (NBTTagList)itemStack.stackTagCompound.getTag("ench");
        NBTTagCompound enchTag;
        for (int i = enchList.tagCount(); i-- > 0;) {
            enchTag = enchList.getCompoundTagAt(i);
            if (enchTag.getShort("id") != enchantment.effectId) {
                continue;
            }
            enchTag.setShort("lvl", (byte)level);
            return;
        }
        enchTag = new NBTTagCompound();
        enchTag.setShort("id", (short)enchantment.effectId);
        enchTag.setShort("lvl", (byte)level);
        enchList.appendTag(enchTag);
    }

    // Applies the enchantment to the itemStack at the given level. Called by all other enchantItem methods to do the actual enchanting.
    public static void enchantItem(ItemStack itemStack, Enchantment enchantment, int level) {
        itemStack.addEnchantment(enchantment, level);
    }

    // Applies the enchantment with the given enchantment id and level.
    public static void enchantItem(ItemStack itemStack, int enchantmentID, int level) {
        EffectHelper.enchantItem(itemStack, Enchantment.enchantmentsList[enchantmentID], level);
    }

    // Randomly enchants the itemStack based on the level (identical to using an enchantment table).
    public static boolean enchantItem(ItemStack itemStack, int level) {
        return EffectHelper.enchantItem(_UtilityMobs.random, itemStack, level);
    }
    public static boolean enchantItem(Random random, ItemStack itemStack, int level) {
        if (level <= 0 || itemStack == null || !itemStack.isItemEnchantable())
            return false;
        EnchantmentHelper.addRandomEnchantment(random, itemStack, level);
        return true;
    }

    // Dyes the given itemStack. Only works on leather armor, returns true if it works.
    public static boolean dye(ItemStack itemStack, String colorName) {
        for (int i = ItemDye.field_150922_c.length; i-- > 0;)
            if (colorName.equalsIgnoreCase(ItemDye.field_150921_b[i]))
                return EffectHelper.dye(itemStack, (byte)i);
        _UtilityMobs.debugException("Tried to dye with an invalid dye name (" + colorName + ")! Valid dye names: black, red, green, brown, blue, purple, cyan, silver, gray, pink, lime, yellow, lightBlue, magenta, orange, white.");
        return false;
    }
    public static boolean dye(ItemStack itemStack, byte colorIndex) {
        if (colorIndex < 0 || colorIndex >= ItemDye.field_150922_c.length) {
            _UtilityMobs.debugException("Tried to dye with an invalid dye index (" + colorIndex + ")!");
            return false;
        }
        float[] rgb = EntitySheep.fleeceColorTable[BlockColored.func_150031_c(colorIndex)];
        return EffectHelper.dye(itemStack, (int)(rgb[0] * 255.0F), (int)(rgb[1] * 255.0F), (int)(rgb[2] * 255.0F));
    }
    public static boolean dye(ItemStack itemStack, int red, int green, int blue) {
        if (red > 255 || green > 255 || blue > 255 || red < 0 || green < 0 || blue < 0) {
            _UtilityMobs.debugException("Tried to dye with an invalid RGB value (" + red + ", " + green + ", " + blue + ")!");
            return false;
        }
        return EffectHelper.dye(itemStack, (red << 16) + (green << 8) + blue);
    }
    public static boolean dye(ItemStack itemStack, int color) {
        if (color < 0 || color > 0xffffff) {
            _UtilityMobs.debugException("Tried to dye with an invalid color value (" + color + ")!");
            return false;
        }
        try {
            ((ItemArmor)itemStack.getItem()).func_82813_b(itemStack, color); /// Dyes the armor if it is leather.
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    // Returns true if the itemstack is a lava or fire weapon.
    public static boolean isFireWeapon(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        return itemStack.getItem() == Items.flint_and_steel || itemStack.getItem() == Item.getItemFromBlock(Blocks.fire);
    }
    public static boolean isLavaWeapon(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        return itemStack.getItem() == Items.lava_bucket || itemStack.getItem() == Item.getItemFromBlock(Blocks.lava);
    }

    // Creates an instance of an explosion at the exploder with the given power.
    public static Explosion explosion(Entity exploder, float power) {
        Explosion explosion = new Explosion(exploder.worldObj, exploder, exploder.posX, exploder.posY, exploder.posZ, power);
        explosion.isSmoking = exploder.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
        return explosion;
    }

    /// Causes a standard explosion at the exploder with the given power.
    public static Explosion explode(Entity exploder, float power) {
        return EffectHelper.explode(EffectHelper.explosion(exploder, power));
    }

    /// Triggers an explosion that damages entities and blocks.
    public static Explosion explode(Explosion explosion) {
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        if (!explosion.exploder.worldObj.isRemote) {
            _UtilityMobs.CHANNEL.sendToDimension(new MessageExplosion(explosion), explosion.exploder.dimension);
        }
        return explosion;
    }

    /// Causes a fiery explosion at the exploder with the given power.
    public static Explosion explodeFire(Entity exploder, float power) {
        return EffectHelper.explodeFire(EffectHelper.explosion(exploder, power));
    }

    /// Makes the explosion fiery before calling explode(explosion).
    public static Explosion explodeFire(Explosion explosion) {
        explosion.isFlaming = true;
        return EffectHelper.explode(explosion);
    }

    /// Causes an explosion that does not destroy blocks at the exploder with the given power.
    public static Explosion explodeSafe(Entity exploder, float power) {
        return EffectHelper.explodeSafe(EffectHelper.explosion(exploder, power));
    }

    /// Triggers an explosion that damages entities without destroying blocks.
    public static Explosion explodeSafe(Explosion explosion) {
        explosion.isSmoking = false;
        return EffectHelper.explode(explosion);
    }

    /// Causes a fiery explosion at the exploder with the given power.
    public static Explosion explodeFireSafe(Entity exploder, float power) {
        return EffectHelper.explodeFireSafe(EffectHelper.explosion(exploder, power));
    }

    /// Makes the explosion fiery before calling explode(explosion).
    public static Explosion explodeFireSafe(Explosion explosion) {
        explosion.isFlaming = true;
        return EffectHelper.explodeSafe(explosion);
    }
}