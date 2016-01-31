package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntityGhastTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/ghastTurret.png");

    {
        this.maxAttackTime = 100;
        this.upgrades = new EnumUpgrade[] {
                EnumUpgrade.FEATHER, EnumUpgrade.SLOW, EnumUpgrade.SIGHT, EnumUpgrade.POISON
        };
    }

    public EntityGhastTurret(World world) {
        super(world);
        this.texture = EntityGhastTurret.TEXTURE;
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 8);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.nether_brick);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntityLargeFireball fireball = new EntityLargeFireball(this.worldObj, this, target.posX - this.posX, target.boundingBox.minY + target.height / 2.0F - (this.posY + this.height / 2.0F), target.posZ - this.posZ);
            this.targetHelper.setOwned(fireball);
            this.upgrade.applyTo(fireball);
            fireball.posY = this.posY + this.height - 0.5;
            this.worldObj.spawnEntityInWorld(fireball);
        }
        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}