package toast.utilityMobs.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import toast.utilityMobs.BuildHelper;

public class BlockEvent extends UtilityMobsEvent
{
    // The player involved with this event.
    public EntityPlayer entityPlayer;
    // True if the player was holding a pumpkin.
    public boolean holdingGolemHead;
    // The location of this event.
    public int posX, posY, posZ;

    public BlockEvent(EntityPlayer player, int x, int y, int z, int face) {
        this(player, x + Facing.offsetsXForSide[face], y + Facing.offsetsYForSide[face], z + Facing.offsetsZForSide[face]);
    }

    public BlockEvent(EntityPlayer player, int x, int y, int z) {
        this.entityPlayer = player;
        ItemStack held = player.getHeldItem();
        this.holdingGolemHead = held != null && (held.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || held.getItem() == Item.getItemFromBlock(Blocks.lit_pumpkin));
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    /// Actually triggers this event's effects.
    @Override
    public void execute() {
        BuildHelper.place(this.entityPlayer.worldObj, this.entityPlayer, this.holdingGolemHead, this.posX, this.posY, this.posZ);
    }
}