package toast.utilityMobs.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntitySniperTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/sniperTurret.png");

    {
        this.maxAttackTime = 70;
    }

    public EntitySniperTurret(World world) {
        super(world);
        this.texture = EntitySniperTurret.TEXTURE;
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20.0);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.lapis_block);
    }

    /// Executes this golem's ranged attack.
    @Override
    public void doRangedAttack(EntityLivingBase target) {
        if (!this.worldObj.isRemote) {
            float power = 1.6F;
            float distance = this.getDistanceToEntity(target);
            if (20.0F < distance) {
                power += (distance - 20.0F) * 3.0F / 100.0F;
            }
            EntityArrow arrow = new EntityArrow(this.worldObj, this, target, power, 3.0F);
            arrow.setDamage(3.0);
            this.targetHelper.setOwned(arrow);
            this.upgrade.applyToArrow(arrow);
            this.worldObj.spawnEntityInWorld(arrow);
        }
        this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
    }
}