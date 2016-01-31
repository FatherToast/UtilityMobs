package toast.utilityMobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityGolemFishHook extends Entity
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile;
    private boolean inGround = false;
    public int shake = 0;
    public EntityUtilityGolem angler = null;
    private int ticksInGround = 0;
    private int ticksInAir = 0;

    public EntityGolemFishHook(World world) {
        super(world);
    }

    public EntityGolemFishHook(World world, EntityUtilityGolem golem, Entity target) {
        super(world);
        this.angler = golem;
        this.setLocationAndAngles(golem.posX, golem.posY + 1.62 - golem.yOffset, golem.posZ, golem.rotationYaw, golem.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.posY -= 0.1;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (target.posX - golem.posX) * 0.7;
        this.motionY = (target.posY + target.getEyeHeight() - 0.7 - this.posY) * 0.7;
        this.motionZ = (target.posZ - golem.posZ) * 0.7;
        double vH = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (vH >= 1E-7) {
            this.rotationYaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0F;
            this.rotationPitch = (float)(-Math.atan2(this.motionY, vH) * 180.0 / Math.PI);
            double dX = this.motionX / vH;
            double dZ = this.motionZ / vH;
            this.setLocationAndAngles(golem.posX + dX, this.posY, golem.posZ + dZ, this.rotationYaw, this.rotationPitch);
            this.yOffset = 0.0F;
            this.calculateVelocity(this.motionX, this.motionY + vH * 0.2, this.motionZ, 1.0F, 14 - (this.worldObj.difficultySetting.getDifficultyId() << 2));
        }
    }

    @Override
    protected void entityInit() {
        this.setSize(0.25F, 0.25F);
    }

    @Override
    public boolean isInRangeToRenderDist(double d) {
        double d1 = this.boundingBox.getAverageEdgeLength() * 256.0;
        return d < d1 * d1;
    }

    public void calculateVelocity(double vX, double vY, double vZ, float v, float variance) {
        float vi = MathHelper.sqrt_double(vX * vX + vY * vY + vZ * vZ);
        vX /= vi;
        vY /= vi;
        vZ /= vi;
        vX += this.rand.nextGaussian() * 0.0075 * variance;
        vY += this.rand.nextGaussian() * 0.0075 * variance;
        vZ += this.rand.nextGaussian() * 0.0075 * variance;
        vX *= v;
        vY *= v;
        vZ *= v;
        this.motionX = vX;
        this.motionY = vY;
        this.motionZ = vZ;
        float vH = MathHelper.sqrt_double(vX * vX + vZ * vZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(vX, vZ) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(vY, vH) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    public void setVelocity(double vX, double vY, double vZ) {
        this.motionX = vX;
        this.motionY = vY;
        this.motionZ = vZ;
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float vH = MathHelper.sqrt_double(vX * vX + vZ * vZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(vX, vZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(vY, vH) * 180.0 / Math.PI);
        }
    }

    @Override
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();
        if (this.angler == null || this.angler.isDead || this.getDistanceSqToEntity(this.angler) > 1024.0) {
            this.setDead();
        }
        if (this.shake > 0) {
            this.shake--;
        }
        if (this.inGround) {
            Block inBlock = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
            if (inBlock == this.inTile) {
                this.ticksInGround++;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }
                return;
            }
            this.inGround = false;
            this.motionX *= this.rand.nextFloat() * 0.2F;
            this.motionY *= this.rand.nextFloat() * 0.2F;
            this.motionZ *= this.rand.nextFloat() * 0.2F;
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        }
        else {
            this.ticksInAir++;
        }
        Vec3 posVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 motionVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition object = this.worldObj.rayTraceBlocks(posVec, motionVec);
        posVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        motionVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (object != null) {
            motionVec = Vec3.createVectorHelper(object.hitVec.xCoord, object.hitVec.yCoord, object.hitVec.zCoord);
        }
        if (!this.worldObj.isRemote) {
            Entity entityHit = null;
            List entitiesInPath = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d = Double.POSITIVE_INFINITY;
            for (int i = 0; i < entitiesInPath.size(); i++) {
                Entity entityInPath = (Entity)entitiesInPath.get(i);
                if (entityInPath.canBeCollidedWith() && !entityInPath.isEntityEqual(this.angler)) {
                    AxisAlignedBB aabb = entityInPath.boundingBox.expand(0.3, 0.3, 0.3);
                    MovingObjectPosition object1 = aabb.calculateIntercept(posVec, motionVec);
                    if (object1 != null) {
                        double d1 = posVec.distanceTo(object1.hitVec);
                        if (d1 < d) {
                            entityHit = entityInPath;
                            d = d1;
                        }
                    }
                }
            }
            if (entityHit != null) {
                object = new MovingObjectPosition(entityHit);
            }
        }
        if (object != null) {
            this.onImpact(object);
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float var16 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
        for (this.rotationPitch = (float)(Math.atan2(this.motionY, var16) * 180.0 / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            // Do nothing
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        if (this.isInWater()) {
            this.setDead();
        }
        this.motionX *= 0.99;
        this.motionY *= 0.99;
        this.motionZ *= 0.99;
        this.motionY -= this.getGravityVelocity();
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public void onImpact(MovingObjectPosition object) {
        if (object.entityHit != null) {
            double vX = this.angler.posX - this.posX;
            double vY = this.angler.posY - this.posY;
            double vZ = this.angler.posZ - this.posZ;
            double v = Math.sqrt(vX * vX + vY * vY + vZ * vZ);
            double mult = 0.31;
            object.entityHit.motionX = vX * mult;
            object.entityHit.motionY = vY * mult + Math.sqrt(v) * 0.1;
            object.entityHit.motionZ = vZ * mult;
            object.entityHit.onGround = false;
            if (object.entityHit instanceof EntityPlayerMP) {
                try {
                    ((EntityPlayerMP) object.entityHit).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(object.entityHit));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.setDead();
    }

    protected float getGravityVelocity() {
        return 0.03F;
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public void setDead() {
        if (this.angler != null) {
            this.angler.setFishingRod(true);
        }
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        tag.setShort("xTile", (short)this.xTile);
        tag.setShort("yTile", (short)this.yTile);
        tag.setShort("zTile", (short)this.zTile);
        tag.setByte("inTile", (byte)Block.getIdFromBlock(this.inTile));
        tag.setByte("shake", (byte)this.shake);
        tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        this.xTile = tag.getShort("xTile");
        this.yTile = tag.getShort("yTile");
        this.zTile = tag.getShort("zTile");
        this.inTile = Block.getBlockById(tag.getByte("inTile") & 0xff);
        this.shake = tag.getByte("shake") & 0xff;
        this.inGround = tag.getByte("inGround") == 1;
    }
}