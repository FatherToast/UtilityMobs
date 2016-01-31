package toast.utilityMobs.ai;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityAIGolemTarget extends EntityAINearestAttackableTarget
{
    private final EntityAINearestAttackableTarget.Sorter sorter;
    private final IEntitySelector targetSelector;
    public final EntityUtilityGolem golem;
    public EntityLivingBase targetEntity;

    public EntityAIGolemTarget(EntityUtilityGolem entity) {
        super(entity, EntityLivingBase.class, 0, true, false, (IEntitySelector)null);
        this.golem = entity;
        this.sorter = new EntityAINearestAttackableTarget.Sorter(entity);
        this.targetSelector = new EntityAIGolemTargetSelector(entity);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        double range = this.getTargetDistance();
        List entityList = this.golem.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, this.golem.boundingBox.expand(range, range, range), this.targetSelector);
        Collections.sort(entityList, this.sorter);

        if (entityList.isEmpty()) {
            this.targetEntity = null;
            return false;
        }
        this.targetEntity = (EntityLivingBase)entityList.get(0);
        return true;
    }

    @Override
    public void startExecuting() {
        this.golem.setAttackTarget(this.targetEntity);
    }
}