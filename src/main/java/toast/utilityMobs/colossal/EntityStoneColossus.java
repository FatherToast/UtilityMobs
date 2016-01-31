package toast.utilityMobs.colossal;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityStoneColossus extends EntityColossalGolem
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "colossal/stoneColossus.png");

    public EntityStoneColossus(World world) {
        super(world);
        this.texture = EntityStoneColossus.TEXTURE;
    }

    // Returns the armor of this entity.
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
        super.dropFewItems(recentlyHit, looting, dropChance);
        for (int i = this.rand.nextInt(3) + 1; i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }
}