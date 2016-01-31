package toast.utilityMobs.golem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;

public class EntityStoneLargeGolem extends EntityLargeGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "golem/stoneLargeGolem.png");

    public EntityStoneLargeGolem(World world) {
        super(world);
        this.texture = EntityStoneLargeGolem.TEXTURE;
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIWeaponAttack(this, 1.0));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0);
    }

    /// Returns the armor of this entity.
    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 2);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.cobblestone);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(this.getDropItem(), 1);
        }
    }
}