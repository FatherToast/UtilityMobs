package toast.utilityMobs.golem;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityLargeGolem extends EntityUtilityGolem
{
    private int hitTime;
    private int animationTime;

    public EntityLargeGolem(World world) {
        super(world);
        this.setSize(1.4F, 2.9F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.hitTime > 0) {
            this.hitTime--;
        }
        if (this.animationTime > 0) {
            this.animationTime--;
        }
        this.setSprinting(this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5E-007);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        this.hitTime = 10;
        this.worldObj.setEntityState(this, (byte)4);
        this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
        return super.attackEntityAsMob(entity);
    }

    @Override
    public void hitEffects(Entity entity) {
        entity.motionY += 0.4;
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 4) {
            this.hitTime = 10;
            this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
        }
        else if (b == 11) {
            this.animationTime = 400;
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

    public int getHitTime() {
        return this.hitTime;
    }

    public int getAnimationTime() {
        return this.animationTime;
    }
}