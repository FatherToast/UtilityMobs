package toast.utilityMobs.colossal;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityObsidianColossus extends EntityColossalGolem
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "colossal/obsidianColossus.png");

    public EntityObsidianColossus(World world) {
        super(world);
        this.texture = EntityObsidianColossus.TEXTURE;
        this.isImmuneToFire = true;
        this.tasks.addTask(2, new EntityAIWander(this, 0.8));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
    }

    // Returns the armor of this entity.
    @Override
    public int getTotalArmorValue() {
        return 20;
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(500.0);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.obsidian);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        super.dropFewItems(recentlyHit, looting, dropChance);
        for (int i = this.rand.nextInt(3) + 1; i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }
}