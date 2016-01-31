package toast.utilityMobs.golem;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EffectHelper;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs.ai.EntityAIGolemSit;
import toast.utilityMobs.colossal.EntityColossalGolem;

public abstract class EntityUtilityGolem extends EntityGolem implements IEntityOwnable
{
    /// The texture for this class.
    public ResourceLocation texture = null;
    /// This golem's target helper.
    public TargetHelper targetHelper = TargetHelper.getTargetHelper(null);
    /// This golem's sitting AI.
    public EntityAIGolemSit sitAI = new EntityAIGolemSit(this);
    /// Attack time counter.
    public int golemAttackTime = 0;
    /// Whether the golem sinks in water. Also doubles as another inWater flag.
    public byte sinks = -1;

    public EntityUtilityGolem(World world) {
        super(world);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
    }

    /// Used to initialize data watcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        /// owner; The username of this golem's owner.
        this.dataWatcher.addObject(17, "");
        /// isSitting; If this is 1, this golem is sitting.
        this.dataWatcher.addObject(20, Byte.valueOf((byte)0));
        /// fishingRod; (boolean) If this is is not true, the held item is rendered as a stick.
        this.dataWatcher.addObject(21, Byte.valueOf((byte)1));
    }

    /// Get/set functions for fishing rod. fishing rod == true, stick == false.
    public void setFishingRod(boolean rod) {
        this.dataWatcher.updateObject(21, Byte.valueOf((byte)(rod ? 1 : 0)));
    }
    public boolean getFishingRod() {
        return this.dataWatcher.getWatchableObjectByte(21) == 1;
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    /// Returns the texture for this mob.
    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public void onUpdate() {
        String owner = this.func_152113_b();
        if (this.targetHelper.destroyed() || !owner.equals(this.targetHelper.owner)) {
            this.targetHelper = TargetHelper.getTargetHelper(owner);
        }
        this.golemAttackTime = Math.max(this.golemAttackTime - 1, 0);
        super.onUpdate();
    }

    /// Returns the heldItem.
    @Override
    public ItemStack getHeldItem() {
        if (this.worldObj.isRemote && !this.getFishingRod())
            return new ItemStack(Items.stick);
        return super.getHeldItem();
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        float attackDamage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int knockback = 0;
        if (entity instanceof EntityLivingBase) {
            attackDamage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)entity);
            knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)entity);
        }

        boolean hit = entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        if (hit) {
            this.hitEffects(entity);

            if (knockback > 0) {
                entity.addVelocity(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F, 0.1, MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F);
                this.motionX *= 0.6;
                this.motionZ *= 0.6;
            }

            int fireAspect = EnchantmentHelper.getFireAspectModifier(this);
            if (this.getEquipmentInSlot(0) != null) {
                if (EffectHelper.isFireWeapon(this.getEquipmentInSlot(0))) {
                    fireAspect += 2;
                }
                else if (EffectHelper.isLavaWeapon(this.getEquipmentInSlot(0))) {
                    if (!entity.isImmuneToFire()) {
                        entity.attackEntityFrom(DamageSource.lava, 4);
                        fireAspect += 4;
                    }
                }
                else if (this.getEquipmentInSlot(0).getItem() == Item.getItemFromBlock(Blocks.tnt)) {
                    EffectHelper.explode(this, 3.0F);
                    this.setCurrentItemOrArmor(0, null);
                    this.setDead();
                }
            }
            if (fireAspect > 0) {
                entity.setFire(fireAspect << 2);
            }

            if (entity instanceof EntityLivingBase) {
                EnchantmentHelper.func_151384_a((EntityLivingBase) entity, this); // Triggers hit entity's enchants.
            }
            EnchantmentHelper.func_151385_b(this, entity); // Triggers attacker's enchants.
        }
        return hit;
    }

    public void hitEffects(Entity entity) {
        // To be overridden
    }

    // Returns the sound this mob makes while it's alive.
    @Override
    protected String getLivingSound() {
        return null; // Overridden to stop golems from spamming the console.
    }

    // Returns the sound this mob makes when it is hurt.
    @Override
    protected String getHurtSound() {
        return null; // Overridden to stop golems from spamming the console.
    }

    // Returns the sound this mob makes on death.
    @Override
    protected String getDeathSound() {
        return null; // Overridden to stop golems from spamming the console.
    }

    @Override
    protected boolean canTriggerWalking() {
        return true;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.pumpkin);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        this.dropFewItems(recentlyHit, looting, 0.0F);
    }

    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (recentlyHit) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!this.canInteract(player))
            return super.interact(player);
        ItemStack playerHeld = player.getEquipmentInSlot(0);
        if (player.isSneaking() && playerHeld != null && playerHeld.getItem() instanceof ItemShears) {
            if (!this.worldObj.isRemote) {
                float health = this.getHealth();
                float maxHealth = this.getMaxHealth();
                this.dropFewItems(true, 0, health * health / maxHealth / maxHealth);
                this.dropEquipment(true, 0);
            }
            this.playSound("step.stone", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            this.spawnExplosionParticle();
            player.swingItem();
            this.setDead();
        }
        else if (!(this instanceof EntityLargeGolem) && !(this instanceof EntityColossalGolem)) {
            double mountRange = 7.0;
            List<EntityColossalGolem> list = this.worldObj.getEntitiesWithinAABB(EntityColossalGolem.class, AxisAlignedBB.getBoundingBox(this.posX - mountRange, this.posY - mountRange, this.posZ - mountRange, this.posX + mountRange, this.posY + mountRange, this.posZ + mountRange));
            if (list != null) {
                for (EntityColossalGolem golem : list) {
                    if (golem.riddenByEntity == null && golem.getLeashed() && golem.getLeashedToEntity() == player) {
                        this.mountEntity(golem);
                        break;
                    }
                }
            }
        }
        return super.interact(player);
    }

    public boolean canInteract(EntityPlayer player) {
        if (player.isSneaking() && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemShears)
            return this.targetHelper.playerHasPermission(player.getCommandSenderName(), this.getUsePermissions() | TargetHelper.PERMISSION_OPEN);
        return this.isEntityAlive() && this.targetHelper.playerHasPermission(player.getCommandSenderName(), this.getUsePermissions());
    }

    public int getUsePermissions() {
        return TargetHelper.PERMISSION_TARGET | TargetHelper.PERMISSION_USE;
    }

    public boolean setEquipment(ItemStack itemStack) {
        if (itemStack == null) {
            for (int slot = 0; slot < 5; slot++) {
                if (this.getEquipmentInSlot(slot) != null)
                    return this.setEquipment(slot, itemStack);
            }
            return false;
        }
        int slot = 0;
        if (itemStack.getItem() instanceof ItemArmor) {
            slot = 4 - ((ItemArmor)itemStack.getItem()).armorType;
        }
        return this.setEquipment(slot, itemStack);
    }
    public boolean setEquipment(int slot, ItemStack itemStack) {
        if (!this.worldObj.isRemote && this.getEquipmentInSlot(slot) != null) {
            this.entityDropItem(this.getEquipmentInSlot(slot), 0.0F);
        }
        this.setCurrentItemOrArmor(slot, itemStack);
        this.equipmentDropChances[slot] = 2.0F;
        return true;
    }

    /// Executes this golem's ranged attack.
    public void doRangedAttack(EntityLivingBase target) {
        ItemStack held = this.getEquipmentInSlot(0);
        if (held == null)
            return;
        else if (held.getItem() instanceof ItemBow) {
            EntityArrow arrow = new EntityArrow(this.worldObj, this, target, 1.6F, 12.0F);
            this.targetHelper.setOwned(arrow);
            EnumUpgrade.DEFAULT.applyToArrow(arrow);
            this.playSound("random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, held);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, held);
            if (power > 0) {
                arrow.setDamage(arrow.getDamage() + power * 0.5 + 0.5);
            }
            if (punch > 0) {
                arrow.setKnockbackStrength(punch);
            }
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, held) > 0) {
                arrow.setFire(100);
            }
            this.worldObj.spawnEntityInWorld(arrow);
        }
        else {
            EntitySnowball snowball = new EntitySnowball(this.worldObj, this);
            this.targetHelper.setOwned(snowball);
            EnumUpgrade.DEFAULT.applyTo(snowball);
            double motionX = target.posX - this.posX;
            double motionY = target.posY + target.getEyeHeight() - 1.1 - snowball.posY;
            double motionZ = target.posZ - this.posZ;
            float velocity = (float)Math.sqrt(motionX * motionX + motionZ * motionZ) * 0.2F;
            snowball.setThrowableHeading(motionX, motionY + velocity, motionZ, 1.6F, 12.0F);
            this.playSound("random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            this.worldObj.spawnEntityInWorld(snowball);
        }
    }

    /// Called when the entity is attacked.
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if (this.isEntityInvulnerable())
            return false;
        this.sitAI.sit = false;
        return super.attackEntityFrom(damageSource, damage);
    }

    /// Returns the owner entity.
    @Override
    public EntityPlayer getOwner() {
        return this.worldObj.getPlayerEntityByName(this.func_152113_b());
    }

    /// Get/set functions for the owner name.
    @Override
    public String func_152113_b() {
        return this.dataWatcher.getWatchableObjectString(17);
    }
    public void setOwner(String username) {
        if (!this.func_152113_b().equals(username)) {
            this.dataWatcher.updateObject(17, username == null ? "" : username);
            this.targetHelper = TargetHelper.getTargetHelper(username);
        }
    }

    @Override
    public Team getTeam() {
        EntityLivingBase owner = this.getOwner();
        if (owner != null)
            return owner.getTeam();
        return super.getTeam();
    }

    @Override
    public boolean isOnSameTeam(EntityLivingBase entity) {
        EntityLivingBase owner = this.getOwner();
        if (entity == owner)
            return true;
        if (owner != null)
            return owner.isOnSameTeam(entity);
        else if (entity instanceof EntityUtilityGolem)
            return ((IEntityOwnable)entity).getOwner() == null;
        return super.isOnSameTeam(entity);
    }

    /// Gets/sets the isSitting variable.
    public boolean isSitting() {
        return this.dataWatcher.getWatchableObjectByte(20) == 1;
    }
    public void setSitting(boolean sitting) {
        this.dataWatcher.updateObject(20, Byte.valueOf(sitting ? (byte)1 : (byte)0));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean handleWaterMovement() {
        if (this.sinks < 0)
            return super.handleWaterMovement();
        if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0, -0.4, 0.0).contract(0.001, 0.001, 0.001), Material.water, this)) {
            if (this.sinks < 1) {
                float speed = (float)Math.sqrt(this.motionX * this.motionX * 0.2 + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2) * 0.2F;
                if (speed > 1.0F) {
                    speed = 1.0F;
                }
                this.playSound("liquid.splash", speed, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                float y = (float)Math.floor(this.boundingBox.minY);
                float x, z;
                for (float i = this.width * 20.0F + 1.0F; i-- > 0.0F;) {
                    x = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    z = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("bubble", this.posX + x, y + 1.0F, this.posZ + z, this.motionX, this.motionY - this.rand.nextFloat() * 0.2F, this.motionZ);
                    x = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    z = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + x, y + 1.0F, this.posZ + z, this.motionX, this.motionY, this.motionZ);
                }
            }
            this.fallDistance = 0.0F;
            this.sinks = 1;
            this.extinguish();
        }
        else {
            this.sinks = 0;
        }
        return false;
    }

    @Override
    public boolean handleLavaMovement() {
        if (this.sinks < 0)
            return super.handleLavaMovement();
        return false;
    }

    /// Saves this entity to NBT.
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("Sitting", this.isSitting());
        tag.setString("Owner", this.func_152113_b());
    }

    /// Loads this entity from NBT.
    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.sitAI.sit = tag.getBoolean("Sitting");
        this.setSitting(this.sitAI.sit);
        String name = null;
        if (tag.hasKey("Owner")) {
            name = tag.getString("Owner");
        }
        else if (tag.hasKey("owner")) {
            name = tag.getString("owner");
        }
        if (name == "") {
            name = null;
        }
        this.setOwner(name);
    }

    /// Returns true if this golem can attack the target.
    public boolean canAttack(Entity target) {
        double range = this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
        return target != this && this.targetHelper.isValidTarget(target) && this.getEntitySenses().canSee(target) && range * range >= this.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
    }
}