package toast.utilityMobs.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityAIGolemTargetSelector implements IEntitySelector
{
    public final EntityUtilityGolem golem;

    public EntityAIGolemTargetSelector(EntityUtilityGolem entity) {
        this.golem = entity;
    }

    @Override
    public boolean isEntityApplicable(Entity entity) {
        return this.golem.canAttack(entity);
    }
}