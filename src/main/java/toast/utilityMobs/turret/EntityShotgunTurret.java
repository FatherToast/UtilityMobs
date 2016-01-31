package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntityShotgunTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/shotgunTurret.png");

    public EntityShotgunTurret(World world) {
        super(world);
        this.texture = EntityShotgunTurret.TEXTURE;
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 12);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.iron_block);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntityArrow arrow;
            for (int i = 6; i-- > 0;) {
                arrow = new EntityArrow(this.worldObj, this, target, 1.6F, 16.0F);
                arrow.setDamage(1.0);
                this.targetHelper.setOwned(arrow);
                this.upgrade.applyToArrow(arrow);
                EnumUpgrade.MULTISHOT.applyToArrow(arrow);
                this.worldObj.spawnEntityInWorld(arrow);
            }
        }
        this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}