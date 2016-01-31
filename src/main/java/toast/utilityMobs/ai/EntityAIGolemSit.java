package toast.utilityMobs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import toast.utilityMobs.block.EntityContainerGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityAIGolemSit extends EntityAIBase
{
    // The golem using this AI.
    public final EntityUtilityGolem golem;
    // If set to true, this golem will sit whever it is.
    public boolean sitAnywhere = false;
    // If this AI should execute.
    public boolean sit = false;

    public EntityAIGolemSit(EntityUtilityGolem entity) {
        this.golem = entity;
        this.setMutexBits(5);
    }

    /// Returns whether the EntityAIBase should begin execution.
    @Override
    public boolean shouldExecute() {
        if (this.sitAnywhere || !this.golem.isInWater() && this.golem.onGround)
            return this.sit || this.golem instanceof EntityContainerGolem && ((EntityContainerGolem)this.golem).isOpen();
        return false;
    }

    /// Execute a one shot task or start executing a continuous task.
    @Override
    public void startExecuting() {
        this.golem.getNavigator().clearPathEntity();
        this.golem.setSitting(true);
    }

    /// Resets the task.
    @Override
    public void resetTask() {
        this.golem.setSitting(false);
    }
}