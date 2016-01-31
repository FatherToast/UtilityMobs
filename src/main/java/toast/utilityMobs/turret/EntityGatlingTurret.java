package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntityGatlingTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/gatlingTurret.png");

    {
        this.maxAttackTime = 15;
    }

    public EntityGatlingTurret(World world) {
        super(world);
        this.texture = EntityGatlingTurret.TEXTURE;
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 6);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.gold_block);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntityArrow arrow = new EntityArrow(this.worldObj, this, target, 1.6F, 12.0F);
            arrow.setDamage(Double.MIN_VALUE);
            this.targetHelper.setOwned(arrow);
            this.upgrade.applyToArrow(arrow);
            EnumUpgrade.MULTISHOT.applyToArrow(arrow);
            this.worldObj.spawnEntityInWorld(arrow);
        }
        this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}