package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLanternGolem extends Container
{
    private final EntityLanternGolem golem;

    public ContainerLanternGolem(InventoryPlayer inventory, EntityLanternGolem lanternGolem) {
        this.golem = lanternGolem;
        this.golem.openInventory();
        int i, j;
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(lanternGolem, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    /// Callback for when the crafting gui is closed.
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        this.golem.closeInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.golem.isUseableByPlayer(player);
    }

    /// Called when a player shift-clicks on a slot.
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStackInSlot = slot.getStack();
            itemStack = itemStackInSlot.copy();
            if (slotIndex >= 9) {
                // Merge from player inventory to golem inventory.
                if (!this.mergeItemStack(itemStackInSlot, 0, 9, false))
                    return null;
            }
            else {
                // Merge from golem inventory to player inventory.
                if (!this.mergeItemStack(itemStackInSlot, 9, 45, false))
                    return null;
            }

            if (itemStackInSlot.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }

            if (itemStackInSlot.stackSize == itemStack.stackSize)
                return null;
            slot.onPickupFromSlot(player, itemStackInSlot);
        }
        return itemStack;
    }
}