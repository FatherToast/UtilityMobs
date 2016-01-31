package toast.utilityMobs.turret;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntityKillerTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/killerTurret.png");

    public EntityKillerTurret(World world) {
        super(world);
        this.texture = EntityKillerTurret.TEXTURE;
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 18);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.diamond_block);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            double range = this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
            List<Entity> entityList = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(range * 1.5, range * 1.5, range * 1.5));
            EntityArrow arrow = null;
            for (Entity entity : entityList) {
                if (this.canAttack(entity)) {
                    arrow = new EntityArrow(this.worldObj, this, (EntityLivingBase)entity, 1.6F, 12.0F);
                    arrow.setDamage(3.0);
                    this.targetHelper.setOwned(arrow);
                    this.upgrade.applyToArrow(arrow);
                    EnumUpgrade.MULTISHOT.applyToArrow(arrow);
                    this.worldObj.spawnEntityInWorld(arrow);
                }
            }
        }
        this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}