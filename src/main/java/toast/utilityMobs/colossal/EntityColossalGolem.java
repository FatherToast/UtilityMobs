package toast.utilityMobs.colossal;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityColossalGolem extends EntityUtilityGolem
{
    public static final int ANIM_R_ARM_SWING = 1;
    public static final int ANIM_L_ARM_SWING = 2;

    private int lastAnimId;
    private int animTick;

    public EntityColossalGolem(World world) {
        super(world);
        this.setSize(1.8F, 3.2F);
        this.stepHeight = 1.0F;

        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0, false));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    /// Used to initialize dataWatcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        /// animId; The animation currently being played. 0 is no animation.
        this.dataWatcher.addObject(30, Byte.valueOf((byte)0));
    }

    /// Gets/sets this lava monster's burningState variable. Used for rendering.
    public int getAnimId() {
        return this.dataWatcher.getWatchableObjectByte(30);
    }
    public void setAnimId(int id) {
        this.dataWatcher.updateObject(30, Byte.valueOf((byte)id));
    }

    public int getAnimTick() {
        return this.animTick;
    }

    // Returns the bounding box for this entity. Prevents movement.
    @Override
    public AxisAlignedBB getBoundingBox() {
        return _UtilityMobs.proxy.solidEntities() ? this.boundingBox : super.getBoundingBox();
    }

    // Returns if this entity is in water and will end up adding the waters velocity to the entity.
    @Override
    public boolean handleWaterMovement() {
        return false;
    }

    // Whether or not this entity is in lava.
    @Override
    public boolean handleLavaMovement() {
        return false;
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.15);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        int animId = this.getAnimId();
        if (this.lastAnimId != animId) {
            this.lastAnimId = animId;
            this.animTick = 0;
        }
        if (animId != 0) {
            this.animTick++;
            if (!this.worldObj.isRemote && this.animTick > 16) {
                this.setAnimId(0);
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getAnimTick() == 2) {
            Vec3 lookVec = this.getLookVec();
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.getOffsetBoundingBox(lookVec.xCoord * this.width, 0.0, lookVec.zCoord * this.width).expand(0.5, 2.0, 0.5));
            double reach = this.width * this.width * 4.0F + this.width;
            for (Object entity : list) {
                if (entity != this.riddenByEntity && entity instanceof EntityLivingBase && this.canDamage((Entity) entity)) {
                    if (this.getDistanceSq(((Entity) entity).posX, ((Entity) entity).boundingBox.minY, ((Entity) entity).posZ) <= reach) {
                        this.attackEntityAsMobFinish((Entity) entity);
                    }
                }
            }
        }
        this.setSprinting(this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5E-007);
    }

    // Checks to see if the golem can damage the passed entity.
    private boolean canDamage(Entity entity) {
        if (this.riddenByEntity == null || entity instanceof IEntityOwnable || entity instanceof EntityPlayer)
            return this.targetHelper.isValidTarget(entity);
        return true;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if(!this.worldObj.isRemote && this.getAnimId() == 0 && (!(this.riddenByEntity instanceof EntityPlayer) || entity == null)) {
            this.setAnimId(this.rand.nextBoolean() ? EntityColossalGolem.ANIM_L_ARM_SWING : EntityColossalGolem.ANIM_R_ARM_SWING);
        }
        return true;
    }

    // The actual method that causes damage.
    public boolean attackEntityAsMobFinish(Entity entity) {
        this.worldObj.setEntityState(this, (byte)4);
        this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
        double dX = entity.posX - this.posX;
        double dZ = entity.posZ - this.posZ;
        double dH = Math.sqrt(dX * dX + dZ * dZ);
        entity.motionX = dX / dH * 0.5 + this.motionX * 1.2;
        entity.motionY = 0.8;
        entity.motionZ = dZ / dH * 0.5 + this.motionZ * 1.2;
        if (entity instanceof EntityPlayerMP) {
            try {
                ((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return super.attackEntityAsMob(entity);
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 4) {
            this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
        }
        else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    protected String getHurtSound() {
        return "mob.irongolem.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.irongolem.death";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        this.worldObj.playSoundAtEntity(this, "mob.irongolem.walk", 1.0F, 1.0F);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.canInteract(player) && !player.isSneaking()) {
            if (!player.onGround) {
                if (this.riddenByEntity == null) {
                    player.mountEntity(this);
                }
                else if (!(this.riddenByEntity instanceof EntityPlayer)) {
                    Entity rider = this.riddenByEntity;
                    this.riddenByEntity.mountEntity(null);
                    rider.setPosition(player.posX, player.posY, player.posZ);
                }
                return true;
            }
        }
        return super.interact(player);
    }

    // Returns the Y offset from the entity's position for any entity riding this one.
    @Override
    public double getMountedYOffset() {
        return this.height;
    }

    // Moves the entity based on the specified heading.
    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.riddenByEntity instanceof EntityPlayer && !this.targetHelper.isValidTarget(this.riddenByEntity)) {
            this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
            this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafe = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.15F;
            forward = ((EntityLivingBase)this.riddenByEntity).moveForward * 0.3F;
            if (forward <= 0.0F) {
                forward *= 0.25F;
            }

            if (!this.worldObj.isRemote) {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
                super.moveEntityWithHeading(strafe, forward);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double vX = this.posX - this.prevPosX;
            double vZ = this.posZ - this.prevPosZ;
            float vH = MathHelper.sqrt_double(vX * vX + vZ * vZ) * 4.0F;
            if (vH > 1.0F) {
                vH = 1.0F;
            }

            this.limbSwingAmount += (vH - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else {
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (recentlyHit) {
            if (this.rand.nextFloat() < dropChance / 4.0F) {
                this.entityDropItem(new ItemStack(Items.skull, 1, 4), 0.0F);
            }
        }
    }
}