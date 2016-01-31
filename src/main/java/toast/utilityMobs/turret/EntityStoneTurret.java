package toast.utilityMobs.turret;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityStoneTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/stoneTurret.png");

    public EntityStoneTurret(World world) {
        super(world);
        this.texture = EntityStoneTurret.TEXTURE;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.cobblestone);
    }
}