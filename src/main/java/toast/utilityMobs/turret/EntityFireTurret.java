package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntityFireTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/fireTurret.png");

    {
        this.upgrades = new EnumUpgrade[] {
                EnumUpgrade.KILLER, EnumUpgrade.FEATHER, EnumUpgrade.SLOW, EnumUpgrade.EGG, EnumUpgrade.SIGHT, EnumUpgrade.EXPLOSIVE, EnumUpgrade.POISON, EnumUpgrade.FIRE_EXPLOSIVE
        };
    }

    public EntityFireTurret(World world) {
        super(world);
        this.texture = EntityFireTurret.TEXTURE;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.redstone_block);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntityArrow arrow = new EntityArrow(this.worldObj, this, target, 1.6F, 12.0F);
            arrow.setFire(100);
            this.targetHelper.setOwned(arrow);
            this.upgrade.applyToArrow(arrow);
            this.worldObj.spawnEntityInWorld(arrow);
        }
        this.playSound("random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}