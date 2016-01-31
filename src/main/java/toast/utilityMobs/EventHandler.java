package toast.utilityMobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
    private static final int SKULL_RARITY = Properties.getInt(Properties.GENERAL, "skull_rarity");
    private static final int CREEPER_RARITY = Properties.getInt(Properties.GENERAL, "creeper_head_rarity");

    public EventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called by EntityLiving.onDeath().
     * EntityLivingBase entityLiving = the entity dropping the items.
     * DamageSource source = the source of the lethal damage.
     * ArrayList<EntityItem> drops = the items being dropped.
     * int lootingLevel = the attacker's looting level.
     * boolean recentlyHit = if the entity was recently hit by another player.
     * int specialDropValue = recentlyHit ? entityLiving.getRNG().nextInt(200) - lootingLevel : 0.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDrops(LivingDropsEvent event) {
        if (EventHandler.SKULL_RARITY > 0 && event.entityLiving != null && !event.entityLiving.worldObj.isRemote && event.recentlyHit && event.entityLiving instanceof EntitySkeleton && ((EntitySkeleton)event.entityLiving).getSkeletonType() != 1) {
            int rarity = EventHandler.SKULL_RARITY - event.lootingLevel;
            if (rarity <= 0 || event.entityLiving.getRNG().nextInt(rarity) == 0) {
                EntityItem drop = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(Items.skull));
                drop.delayBeforeCanPickup = 10;
                event.drops.add(drop);
            }
        }
        else if (EventHandler.CREEPER_RARITY > 0 && event.entityLiving != null && !event.entityLiving.worldObj.isRemote && event.recentlyHit && event.entityLiving instanceof EntityCreeper) {
            int rarity = EventHandler.CREEPER_RARITY - event.lootingLevel;
            if (((EntityCreeper) event.entityLiving).getPowered()) {
                rarity >>= 1;
            }
            if (rarity <= 0 || event.entityLiving.getRNG().nextInt(rarity) == 0) {
                EntityItem drop = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(Items.skull, 1, 4));
                drop.delayBeforeCanPickup = 10;
                event.drops.add(drop);
            }
        }
    }

    /**
     * Called by EntityLivingBase.attackEntityFrom().
     * EntityLivingBase entityLiving = the entity being damaged.
     * DamageSource source = the source of the damage.
     * int ammount = the amount of damage to be applied.
     * 
     * @param event the event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.source != null) {
            Entity attacker = null;
            attacker = event.source.getSourceOfDamage();
            if (attacker instanceof EntityArrow || attacker instanceof IProjectile || attacker instanceof EntityFireball) {
                if (TargetHelper.hasOwner(attacker)) {
                    TargetHelper targetHelper = TargetHelper.getOwnerTargetHelper(attacker);
                    if (!targetHelper.isValidTarget(event.entityLiving)) {
                        event.setCanceled(true);
                        return;
                    }
                }

                if (EnumUpgrade.MULTISHOT.isApplied(attacker)) {
                    event.entityLiving.hurtResistantTime = 0;
                }
                if (EnumUpgrade.POISON.isApplied(attacker)) {
                    EffectHelper.stackEffect(event.entityLiving, Potion.poison, 3 * 20, 0, 1);
                }
                if (EnumUpgrade.SLOW.isApplied(attacker)) {
                    EffectHelper.stackEffect(event.entityLiving, Potion.moveSlowdown, 3 * 20, 0, 4);
                }
                if (EnumUpgrade.EXPLOSIVE.isApplied(attacker)) {
                    EffectHelper.explodeSafe(attacker, 1.0F);
                }
                if (EnumUpgrade.FIRE_EXPLOSIVE.isApplied(attacker)) {
                    EffectHelper.explodeFireSafe(attacker, 1.0F);
                }
                if (EnumUpgrade.EGG.isApplied(attacker)) {
                    if (!event.entityLiving.worldObj.isRemote && attacker instanceof EntityArrow && !(event.entityLiving instanceof EntityPlayer) && !(event.entityLiving instanceof IBossDisplayData) && event.entityLiving.getHealth() < ((EntityArrow) attacker).getDamage() * 2) {
                        EntityChicken chicken = new EntityChicken(event.entityLiving.worldObj);
                        chicken.setLocationAndAngles(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, event.entityLiving.rotationYaw, event.entityLiving.rotationPitch);
                        event.entityLiving.worldObj.spawnEntityInWorld(chicken);
                        event.entityLiving.setDead();
                    }
                }
            }

        }
    }
}