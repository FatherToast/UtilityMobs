package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityEnderChest;

public class TileEntityEnderChestProxy extends TileEntityEnderChest
{
    EntityContainerGolem golem;

    public TileEntityEnderChestProxy(EntityContainerGolem chest) {
        this.golem = chest;
    }

    // Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count ticks and creates a new spawn inside its implementation.
    @Override
    public void updateEntity() {
        // Do nothing
    }

    // Called when a GUI using this inventory is opened.
    @Override
    public void func_145969_a() {
        this.golem.openInventory();
    }

    // Called when a GUI using this inventory is closed.
    @Override
    public void func_145970_b() {
        this.golem.closeInventory();
    }

    // Do not make give this method the name canInteractWith because it clashes with Container.
    @Override
    public boolean func_145971_a(EntityPlayer player) {
        return this.golem.isUseableByPlayer(player);
    }
}