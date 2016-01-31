package toast.utilityMobs.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import toast.utilityMobs.EntityGolemFishHook;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityAIWeaponAttack extends EntityAIBase
{
    public final EntityUtilityGolem golem;
    public final double moveSpeed;
    public EntityLivingBase target;
    public PathEntity path = null;
    public int pathDelay = 0;
    public int sightTime = 0;
    public boolean avoidsWater;

    public int rodTime = 0;

    public EntityAIWeaponAttack(EntityUtilityGolem entity, double speed) {
        this.golem = entity;
        this.moveSpeed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entity = this.golem.getAttackTarget();
        if (entity == null)
            return false;
        this.target = entity;
        ItemStack weapon = this.golem.getEquipmentInSlot(0);
        if (weapon != null && (this.isRangedWeapon(weapon) || weapon.getItem() instanceof ItemFishingRod))
            return true;
        this.path = this.golem.getNavigator().getPathToEntityLiving(this.target);
        return this.path != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.golem.getRNG().nextInt(200) != 0 && this.golem.canAttack(this.target) && (!this.golem.getNavigator().noPath() || this.isRangedWeapon(this.golem.getEquipmentInSlot(0)));
    }

    @Override
    public void startExecuting() {
        this.pathDelay = 0;
        this.avoidsWater = this.golem.getNavigator().getAvoidsWater();
        this.golem.getNavigator().setAvoidsWater(false);
        if (!this.isRangedWeapon(this.golem.getEquipmentInSlot(0))) {
            this.golem.getNavigator().setPath(this.path, this.moveSpeed);
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.sightTime = 0;
        this.golem.setAttackTarget(null);
        this.golem.getNavigator().setAvoidsWater(this.avoidsWater);
        this.golem.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        ItemStack weapon = this.golem.getEquipmentInSlot(0);
        this.golem.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
        if (this.isRangedWeapon(weapon)) {
            double distanceSq = this.golem.getDistanceSqToEntity(this.target);
            boolean canSee = this.golem.getEntitySenses().canSee(this.target);
            if (distanceSq <= 100.0 && canSee) {
                this.sightTime++;
            }
            else {
                this.sightTime = 0;
            }
            if (this.sightTime < 20) {
                this.golem.getNavigator().tryMoveToEntityLiving(this.target, this.moveSpeed);
            }
            else {
                this.golem.getNavigator().clearPathEntity();
            }
            if (this.golem.golemAttackTime > 0)
                return;
            if (distanceSq > 100.0 || !canSee)
                return;
            this.golem.doRangedAttack(this.target);
            Item item = weapon.getItem();
            if (item instanceof ItemBow) {
                this.golem.golemAttackTime = 60;
            }
            else {
                this.golem.golemAttackTime = 20;
            }
        }
        else {
            if (weapon != null && weapon.getItem() instanceof ItemFishingRod) {
                if (this.rodTime > 0) {
                    this.rodTime--;
                }
                if (this.rodTime <= 0 && this.golem.getFishingRod()) {
                    float distanceSq = (float)this.golem.getDistanceSqToEntity(this.target);
                    if (distanceSq > 9.0F && distanceSq < 100.0F && this.golem.getEntitySenses().canSee(this.target)) {
                        this.golem.worldObj.spawnEntityInWorld(new EntityGolemFishHook(this.golem.worldObj, this.golem, this.target));
                        this.golem.worldObj.playSoundAtEntity(this.golem, "random.bow", 0.5F, 0.4F / (this.golem.getRNG().nextFloat() * 0.4F + 0.8F));
                        this.golem.setFishingRod(false);
                        this.rodTime = this.golem.getRNG().nextInt(11) + 32;
                    }
                }
            }

            if (this.golem.getEntitySenses().canSee(this.target) && --this.pathDelay <= 0) {
                this.pathDelay = 4 + this.golem.getRNG().nextInt(7);
                this.golem.getNavigator().tryMoveToEntityLiving(this.target, this.moveSpeed);
            }
            double reach = this.golem.width * this.golem.width * 4.0F + this.golem.width;
            if (this.golem.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ) <= reach) {
                if (this.golem.golemAttackTime <= 0) {
                    this.golem.golemAttackTime = 20;
                    this.golem.swingItem();
                    this.golem.attackEntityAsMob(this.target);
                }
            }
        }
    }

    public boolean isRangedWeapon(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemSnowball);
    }
}