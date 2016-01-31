package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.network.GuiHelper;

public class EntityWorkbenchGolem extends EntityContainerGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "block/workbenchGolem.png");

    public EntityWorkbenchGolem(World world) {
        super(world);
        this.texture = EntityWorkbenchGolem.TEXTURE;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.crafting_table);
    }

    /// Opens this block golem's GUI.
    @Override
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            GuiHelper.displayGUIWorkbench(player, this);
        }
        return true;
    }
}