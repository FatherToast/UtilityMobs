package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerAnvilGolemSlot extends Slot
{
    /// The anvil golem container.
    public final ContainerAnvilGolem anvilContainer;
    /// The world object.
    public final World world;

    public ContainerAnvilGolemSlot(ContainerAnvilGolem container, IInventory inventory, int slotIndex, int xDisplay, int yDisplay) {
        super(inventory, slotIndex, xDisplay, yDisplay);
        this.anvilContainer = container;
        this.world = container.golem.worldObj;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return false;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return (player.capabilities.isCreativeMode || player.experienceLevel >= this.anvilContainer.maximumCost) && this.anvilContainer.maximumCost > 0 && this.getHasStack();
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
        if (!player.capabilities.isCreativeMode)
        {
            player.addExperienceLevel(-this.anvilContainer.maximumCost);
        }

        this.anvilContainer.inputSlots.setInventorySlotContents(0, (ItemStack)null);

        if (this.anvilContainer.stackSizeToBeUsedInRepair > 0)
        {
            ItemStack itemstack1 = this.anvilContainer.inputSlots.getStackInSlot(1);

            if (itemstack1 != null && itemstack1.stackSize > this.anvilContainer.stackSizeToBeUsedInRepair)
            {
                itemstack1.stackSize -= this.anvilContainer.stackSizeToBeUsedInRepair;
                this.anvilContainer.inputSlots.setInventorySlotContents(1, itemstack1);
            }
            else
            {
                this.anvilContainer.inputSlots.setInventorySlotContents(1, (ItemStack)null);
            }
        }
        else
        {
            this.anvilContainer.inputSlots.setInventorySlotContents(1, (ItemStack)null);
        }

        this.anvilContainer.maximumCost = 0;

        if (!this.world.isRemote) {
            if (!player.capabilities.isCreativeMode && player.getRNG().nextFloat() < 0.12F) {
                int damage = this.anvilContainer.golem.getDamage() + 1;
                if (damage > 2) {
                    this.world.playAuxSFX(1020, (int)Math.floor(this.anvilContainer.golem.posX), (int)Math.floor(this.anvilContainer.golem.posY), (int)Math.floor(this.anvilContainer.golem.posZ), 0);
                    this.anvilContainer.golem.setDead();
                }
                else {
                    this.world.playAuxSFX(1021, (int)Math.floor(this.anvilContainer.golem.posX), (int)Math.floor(this.anvilContainer.golem.posY), (int)Math.floor(this.anvilContainer.golem.posZ), 0);
                    this.anvilContainer.golem.setDamage(damage);
                }
            }
            else {
                this.world.playAuxSFX(1021, (int)Math.floor(this.anvilContainer.golem.posX), (int)Math.floor(this.anvilContainer.golem.posY), (int)Math.floor(this.anvilContainer.golem.posZ), 0);
            }
        }
    }
}