package toast.utilityMobs.colossal;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityArmorColossus extends EntityColossalGolem
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "colossal/armorColossus.png");

    public EntityArmorColossus(World world) {
        super(world);
        this.texture = EntityArmorColossus.TEXTURE;
        this.tasks.addTask(2, new EntityAIWander(this, 0.8));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(500.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(18.0);
    }

    @Override
    protected Item getDropItem() {
        return Items.iron_ingot;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        super.dropFewItems(recentlyHit, looting, dropChance);
        for (int i = this.rand.nextInt(25) + 3; i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }
}