package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityBrickTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/brickTurret.png");

    {
        this.maxAttackTime = 35;
    }

    public EntityBrickTurret(World world) {
        super(world);
        this.texture = EntityBrickTurret.TEXTURE;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.stonebrick);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntityArrow arrow = new EntityArrow(this.worldObj, this, target, 1.6F, 12.0F);
            arrow.setDamage(1.0);
            this.targetHelper.setOwned(arrow);
            this.upgrade.applyToArrow(arrow);
            this.worldObj.spawnEntityInWorld(arrow);
        }
        this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}