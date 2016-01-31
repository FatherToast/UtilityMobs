package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class EntityContainerGolem extends EntityBlockGolem implements IInventory
{
    // The contents of this chest.
    private ItemStack[] contents = new ItemStack[36];

    public EntityContainerGolem(World world) {
        super(world);
    }

    // Used to initialize data watcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        // numUsingPlayers; The number of players using this chest golem.
        this.dataWatcher.addObject(31, Byte.valueOf((byte) 0));
    }

    // Functions for numUsingPlayers.
    public void incNumUsingPlayers() {
        this.dataWatcher.updateObject(31, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(31) + 1)));
    }
    public void decNumUsingPlayers() {
        this.dataWatcher.updateObject(31, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(31) - 1)));
    }
    public boolean isOpen() {
        return this.dataWatcher.getWatchableObjectByte(31) > 0;
    }

    // Called when this block golem is told to get up.
    @Override
    public void setClosed() {
        this.dataWatcher.updateObject(31, Byte.valueOf((byte)0));
    }

    // Returns the number of slots in the inventory.
    @Override
    public int getSizeInventory() {
        return 27;
    }

    // Returns the stack in slot i
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents[slot];
    }

    // Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.contents[slot] != null) {
            ItemStack itemStack;
            if (this.contents[slot].stackSize <= amount) {
                itemStack = this.contents[slot];
                this.contents[slot] = null;
                return itemStack;
            }
            itemStack = this.contents[slot].splitStack(amount);
            if (this.contents[slot].stackSize == 0) {
                this.contents[slot] = null;
            }
            return itemStack;
        }
        return null;
    }

    // When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.contents[slot] != null) {
            ItemStack itemStack = this.contents[slot];
            this.contents[slot] = null;
            return itemStack;
        }
        return null;
    }

    // Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        this.contents[slot] = itemStack;
        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    // Returns the name of the inventory.
    @Override
    public String getInventoryName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag() : "Chest Golem";
    }

    // Returns true if this inventory has a custom name.
    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    // Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    // Do not make give this method the name canInteractWith because it clashes with Container.
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.canInteract(player);
    }

    // Called when a GUI using this inventory is opened.
    @Override
    public void openInventory() {
        this.incNumUsingPlayers();
    }

    // Called when a GUI using this inventory is closed.
    @Override
    public void closeInventory() {
        this.decNumUsingPlayers();
    }

    // Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return true;
    }

    // For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it hasn't changed and skip it.
    @Override
    public void markDirty() {
        // Do nothing
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        super.dropFewItems(recentlyHit, looting, dropChance);
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (this.contents[i] != null) {
                ItemStack split = this.contents[i].copy();
                while (this.contents[i].stackSize > 0) {
                    int splitSize = this.rand.nextInt(21) + 10;
                    if (splitSize > this.contents[i].stackSize) {
                        splitSize = this.contents[i].stackSize;
                    }
                    this.contents[i].stackSize -= splitSize;
                    split.stackSize = splitSize;
                    this.entityDropItem(split, 0.0F);
                }
                this.contents[i] = null;
            }
        }
    }

    // Opens this block golem's GUI.
    @Override
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            player.displayGUIChest(this);
        }
        return true;
    }

    // Saves this entity to NBT.
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTTagList tagList = new NBTTagList();
        for (int slot = 0; slot < this.contents.length; slot++) {
            if (this.contents[slot] != null) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte)slot);
                this.contents[slot].writeToNBT(slotTag);
                tagList.appendTag(slotTag);
            }
        }
        tag.setTag("Items", tagList);
    }

    // Loads this entity from NBT.
    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        NBTTagList tagList = tag.getTagList("Items", tag.getId());
        this.contents = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.contents.length) {
                this.contents[slot] = ItemStack.loadItemStackFromNBT(slotTag);
            }
        }
    }

    // Steals the contents of the NBT given.
    public void takeContentsFromNBT(NBTTagCompound tag) {
        NBTTagList tagList = tag.getTagList("Items", tag.getId());
        this.contents = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.contents.length) {
                this.contents[slot] = ItemStack.loadItemStackFromNBT(slotTag);
            }
        }
        tag.setTag("Items", new NBTTagList());
        if (tag.hasKey("CustomName")) {
            this.setCustomNameTag(tag.getString("CustomName"));
        }
        tag.removeTag("CustomName");
    }
}