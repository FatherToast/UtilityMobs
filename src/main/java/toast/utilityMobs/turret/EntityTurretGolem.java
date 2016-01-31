package toast.utilityMobs.turret;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAITurretAttack;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityTurretGolem extends EntityUtilityGolem
{
    /// All applicable upgrades.
    public static final EnumUpgrade[] upgradesAll = {
        EnumUpgrade.KILLER, EnumUpgrade.FIRE, EnumUpgrade.FEATHER, EnumUpgrade.SLOW, EnumUpgrade.EGG, EnumUpgrade.SIGHT, EnumUpgrade.EXPLOSIVE, EnumUpgrade.POISON, EnumUpgrade.FIRE_EXPLOSIVE
    };
    /// The UUID for the sight upgrade's modifier.
    private static final UUID sightBoostUUID = UUID.fromString("70A27B59-9566-4402-BC1F-2EE2A276D836");
    /// The modifier applied by the sight upgrade.
    private static final AttributeModifier sightBoost = new AttributeModifier(EntityTurretGolem.sightBoostUUID, "Ender pearl upgrade", 10.0, 0).setSaved(false);

    /// Possible upgrades for this turret type.
    public EnumUpgrade[] upgrades = {
            EnumUpgrade.KILLER, EnumUpgrade.FIRE, EnumUpgrade.FEATHER, EnumUpgrade.SLOW, EnumUpgrade.EGG, EnumUpgrade.SIGHT, EnumUpgrade.EXPLOSIVE, EnumUpgrade.POISON, EnumUpgrade.FIRE_EXPLOSIVE
    };
    /// This turret's targeting AI.
    public EntityAIGolemTarget targetAI = new EntityAIGolemTarget(this);
    /// This turret's current upgrade.
    public EnumUpgrade upgrade;
    /// Attack time counter.
    public int maxAttackTime = 60;

    public EntityTurretGolem(World world) {
        super(world);
        this.equipmentDropChances[0] = 2.0F;
        this.sinks = 1;
        this.tasks.addTask(1, new EntityAITurretAttack(this));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, this.targetAI);
        this.updateTurretStats();
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.0);
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        if (this.getEquipmentInSlot(0) != null && this.getEquipmentInSlot(0).getItem() == Items.feather) {
            super.moveEntity(x, y, z);
        }
        else {
            super.moveEntity(0.0, y, 0.0);
        }
    }

    // Returns the Y Offset of this entity when riding another.
    @Override
    public double getYOffset() {
        return super.getYOffset() - this.height / 4.0;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.dispenser);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (this.rand.nextFloat() < dropChance / 2.0F + 0.3F) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!this.canInteract(player))
            return super.interact(player);
        ItemStack playerHeld = player.getEquipmentInSlot(0);
        if (playerHeld == null) {
            if (this.getEquipmentInSlot(0) != null) {
                player.setCurrentItemOrArmor(0, this.getEquipmentInSlot(0));
                this.setCurrentItemOrArmor(0, null);
                player.swingItem();
                return true;
            }
        }
        else if (EnumUpgrade.getUpgrade(this.upgrades, playerHeld) != EnumUpgrade.DEFAULT) {
            player.swingItem();
            ItemStack heldItem = this.getEquipmentInSlot(0);
            if (heldItem != null && playerHeld == heldItem) {
                this.playSound("note.bass", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
                return true;
            }
            if (!this.worldObj.isRemote) {
                this.setCurrentItemOrArmor(0, playerHeld.copy());
                this.getEquipmentInSlot(0).stackSize = 1;
            }
            if (!player.capabilities.isCreativeMode) {
                playerHeld.stackSize--;
            }
            if (playerHeld.stackSize <= 0) {
                player.setCurrentItemOrArmor(0, heldItem);
            }
            else if (!this.worldObj.isRemote && heldItem != null && !player.capabilities.isCreativeMode) {
                this.entityDropItem(heldItem, 0.0F);
            }
            this.playSound("note.pling", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            this.spawnExplosionParticle();
            return true;
        }
        else if (EnumUpgrade.getUpgrade(EntityTurretGolem.upgradesAll, playerHeld) != EnumUpgrade.DEFAULT) {
            this.playSound("note.bass", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            player.swingItem();
            return true;
        }
        return super.interact(player);
    }

    @Override
    public int getUsePermissions() {
        return super.getUsePermissions() | TargetHelper.PERMISSION_OPEN;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.updateTurretStats();
    }

    /// Sets the equipped item at the given index to the given item stack.
    @Override
    public void setCurrentItemOrArmor(int index, ItemStack itemStack) {
        super.setCurrentItemOrArmor(index, itemStack);
        if (index == 0) {
            this.updateTurretStats();
        }
    }

    /// Updates this turret's range and effect based on its held Items.
    public void updateTurretStats() {
        IAttributeInstance range = this.getEntityAttribute(SharedMonsterAttributes.followRange);
        range.removeModifier(EntityTurretGolem.sightBoost);
        this.upgrade = EnumUpgrade.getUpgrade(this.upgrades, this.getEquipmentInSlot(0));
        if (this.upgrade == EnumUpgrade.SIGHT) {
            range.applyModifier(EntityTurretGolem.sightBoost);
        }
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntityArrow arrow = new EntityArrow(this.worldObj, this, target, 1.6F, 12.0F);
            this.targetHelper.setOwned(arrow);
            this.upgrade.applyToArrow(arrow);
            this.worldObj.spawnEntityInWorld(arrow);
        }
        this.playSound("random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}