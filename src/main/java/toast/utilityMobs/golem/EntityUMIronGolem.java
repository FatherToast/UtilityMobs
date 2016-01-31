package toast.utilityMobs.golem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;

public class EntityUMIronGolem extends EntityLargeGolem
{
    // The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/iron_golem.png");

    public EntityUMIronGolem(World world) {
        super(world);
        this.texture = EntityUMIronGolem.TEXTURE;
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIWeaponAttack(this, 1.0));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32.0F));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(6, new EntityAIWander(this, 0.6));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    // Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(17.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    protected Item getDropItem() {
        return Items.iron_ingot;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        for (int i = this.rand.nextInt(3); i-- > 0;) {
            this.dropItem(Item.getItemFromBlock(Blocks.red_flower), 1);
        }
        for (int i = this.rand.nextInt(3) + 3; i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }
}