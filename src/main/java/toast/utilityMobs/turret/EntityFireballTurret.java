package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EnumUpgrade;
import toast.utilityMobs._UtilityMobs;

public class EntityFireballTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/fireballTurret.png");

    {
        this.upgrades = new EnumUpgrade[] {
                EnumUpgrade.FEATHER, EnumUpgrade.SLOW, EnumUpgrade.SIGHT, EnumUpgrade.EXPLOSIVE, EnumUpgrade.POISON, EnumUpgrade.FIRE_EXPLOSIVE
        };
    }

    public EntityFireballTurret(World world) {
        super(world);
        this.texture = EntityFireballTurret.TEXTURE;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.netherrack);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            EntitySmallFireball fireball = new EntitySmallFireball(this.worldObj, this, target.posX - this.posX, target.boundingBox.minY + target.height / 2.0F - (this.posY + this.height / 2.0F), target.posZ - this.posZ);
            this.targetHelper.setOwned(fireball);
            this.upgrade.applyTo(fireball);
            fireball.posY = this.posY + this.height - 0.5;
            this.worldObj.spawnEntityInWorld(fireball);
        }
        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}