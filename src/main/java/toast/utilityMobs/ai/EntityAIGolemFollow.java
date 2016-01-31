package toast.utilityMobs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityAIGolemFollow extends EntityAIBase
{
    public final EntityUtilityGolem golem;
    public double moveSpeed;
    public float minDistance, maxDistance;

    public EntityPlayer owner;
    public int pathDelay = 0;
    public boolean avoidsWater;

    public EntityAIGolemFollow(EntityUtilityGolem entity, double speed, float min, float max) {
        this.golem = entity;
        this.moveSpeed = speed;
        this.minDistance = min;
        this.maxDistance = max;
        this.setMutexBits(3);
    }

    /// Returns whether the EntityAIBase should begin execution.
    @Override
    public boolean shouldExecute() {
        EntityPlayer player = this.golem.getOwner();
        if (player == null || this.golem.isSitting() || this.golem.getDistanceSqToEntity(player) < this.minDistance * this.minDistance)
            return false;
        this.owner = player;
        return true;
    }

    /// Returns whether an in-progress EntityAIBase should continue executing.
    @Override
    public boolean continueExecuting() {
        return !this.golem.getNavigator().noPath() && this.golem.getDistanceSqToEntity(this.owner) > this.maxDistance * this.maxDistance && !this.golem.isSitting();
    }

    /// Execute a one shot task or start executing a continuous task.
    @Override
    public void startExecuting() {
        this.pathDelay = 0;
        this.avoidsWater = this.golem.getNavigator().getAvoidsWater();
        this.golem.getNavigator().setAvoidsWater(false);
    }

    /// Resets the task.
    @Override
    public void resetTask() {
        this.owner = null;
        this.golem.getNavigator().clearPathEntity();
        this.golem.getNavigator().setAvoidsWater(this.avoidsWater);
    }

    /// Updates the task
    @Override
    public void updateTask() {
        this.golem.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, this.golem.getVerticalFaceSpeed());
        if (!this.golem.isSitting() && --this.pathDelay <= 0) {
            this.pathDelay = 10;
            if (!this.golem.getNavigator().tryMoveToEntityLiving(this.owner, this.moveSpeed)) {
                if (this.golem.getDistanceSqToEntity(this.owner) >= 144.0) {
                    int i = (int)Math.floor(this.owner.posX) - 2;
                    int j = (int)Math.floor(this.owner.posZ) - 2;
                    int k = (int)Math.floor(this.owner.boundingBox.minY);
                    for (int l = 0; l <= 4; ++l) {
                        for (int i1 = 0; i1 <= 4; ++i1) {
                            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && World.doesBlockHaveSolidTopSurface(this.golem.worldObj, i + l, k - 1, j + i1) && !this.golem.worldObj.isBlockNormalCubeDefault(i + l, k, j + i1, true) && !this.golem.worldObj.isBlockNormalCubeDefault(i + l, k + 1, j + i1, true)) {
                                this.golem.setLocationAndAngles(i + l + 0.5F, k, j + i1 + 0.5F, this.golem.rotationYaw, this.golem.rotationPitch);
                                this.golem.getNavigator().clearPathEntity();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}