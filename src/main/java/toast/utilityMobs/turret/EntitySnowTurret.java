package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntitySnowTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/snowTurret.png");

    {
        this.maxAttackTime = 20;
        this.upgrades = new EnumUpgrade[] {
                EnumUpgrade.FEATHER, EnumUpgrade.SLOW, EnumUpgrade.SIGHT, EnumUpgrade.POISON
        };
    }

    public EntitySnowTurret(World world) {
        super(world);
        this.texture = EntitySnowTurret.TEXTURE;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.snow);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntitySnowball snowball = new EntitySnowball(this.worldObj, this);
            this.targetHelper.setOwned(snowball);
            this.upgrade.applyTo(snowball);
            double dX = target.posX - this.posX;
            double dY = target.posY + target.getEyeHeight() - 1.1 - snowball.posY;
            double dZ = target.posZ - this.posZ;
            double v = Math.sqrt(dX * dX + dZ * dZ) * 0.2;
            snowball.setThrowableHeading(dX, dY + v, dZ, 1.6F, 12.0F);
            this.worldObj.spawnEntityInWorld(snowball);
        }
        this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}