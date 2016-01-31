package toast.utilityMobs.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import toast.utilityMobs.turret.EntityTurretGolem;

public class EntityAITurretAttack extends EntityAIBase
{
    public final EntityTurretGolem golem;
    public EntityLivingBase target;

    public EntityAITurretAttack(EntityTurretGolem entity) {
        this.golem = entity;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entity = this.golem.getAttackTarget();
        if (entity == null)
            return false;
        this.target = entity;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        if (this.golem.targetAI.shouldExecute()) {
            this.golem.targetAI.startExecuting();
            this.target = this.golem.getAttackTarget();
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        // Do nothing
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.golem.setAttackTarget(null);
    }

    @Override
    public void updateTask() {
        this.golem.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
        if (this.golem.getRNG().nextInt(40) == 0) {
            if (this.golem.getRNG().nextInt(2) == 0) {
                this.golem.golemAttackTime--;
            }
            else {
                this.golem.golemAttackTime++;
            }
        }
        if (this.golem.golemAttackTime > 0)
            return;
        this.golem.doRangedAttack(this.target);
        this.golem.golemAttackTime = this.golem.maxAttackTime;
    }
}