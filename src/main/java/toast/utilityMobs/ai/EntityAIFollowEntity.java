package toast.utilityMobs.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityGolem;

public class EntityAIFollowEntity extends EntityAIBase
{
    private final Class followClass;
    private final float rangeMin, rangeMax;
    private final EntityGolem golem;
    private EntityLivingBase followEntity;
    private double moveSpeed;
    private boolean isFollowing;

    public EntityAIFollowEntity(EntityGolem entity, Class<? extends EntityLivingBase> target, double speed, float min, float max) {
        this.isFollowing = false;
        this.followEntity = null;
        this.golem = entity;
        this.followClass = target;
        this.moveSpeed = speed;
        this.rangeMin = min;
        this.rangeMax = max;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.followClass == null || this.golem.getAttackTarget() != null)
            return false;
        List l = this.golem.worldObj.getEntitiesWithinAABB(this.followClass, this.golem.boundingBox.expand(this.rangeMax, this.rangeMax, this.rangeMax));
        if (l.size() == 0)
            return false;
        Iterator itr = l.iterator();
        do {
            if (!itr.hasNext()) {
                break;
            }
            this.followEntity = (EntityLivingBase)itr.next();
            break;
        }
        while (true);
        if (this.followEntity == null)
            return false;
        if (this.golem.getDistanceSqToEntity(this.followEntity) < this.rangeMin * this.rangeMin || !this.golem.getEntitySenses().canSee(this.followEntity))
            return false;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !(this.golem.getNavigator().noPath() || this.golem.getAttackTarget() != null);
    }

    @Override
    public void startExecuting() {
        this.isFollowing = false;
        this.golem.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask() {
        this.followEntity = null;
        this.golem.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        this.golem.getLookHelper().setLookPositionWithEntity(this.followEntity, 30.0F, 30.0F);
        this.golem.getNavigator().tryMoveToEntityLiving(this.followEntity, this.moveSpeed);
        this.isFollowing = true;
        if (this.isFollowing && this.golem.getDistanceSqToEntity(this.followEntity) < this.rangeMin * this.rangeMin && this.golem.getRNG().nextInt(10) == 0) {
            this.golem.getNavigator().clearPathEntity();
        }
        else if ((this.isFollowing && this.golem.getDistanceSqToEntity(this.followEntity) > this.rangeMax * this.rangeMax || !this.golem.getEntitySenses().canSee(this.followEntity)) && this.golem.getRNG().nextInt(60) == 0) {
            this.golem.getNavigator().clearPathEntity();
        }
    }
}