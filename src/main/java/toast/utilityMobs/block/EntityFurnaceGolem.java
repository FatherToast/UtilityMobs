package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.network.GuiHelper;

public class EntityFurnaceGolem extends EntityContainerGolem implements ISidedInventory
{
    /// The textures for this class.
    public static final ResourceLocation[] TEXTURES = { new ResourceLocation(_UtilityMobs.TEXTURE + "block/furnaceGolem.png"), new ResourceLocation(_UtilityMobs.TEXTURE + "block/furnaceGolem_fire.png") };

    /// The number of ticks that the furnace will keep burning.
    public int burnTime = 0;
    /// The number of ticks that a fresh copy of the currently-burning item would burn for.
    public int itemBurnTime = 0;
    /// The number of ticks that the current item has been cooking for.
    public int cookTime = 0;

    public EntityFurnaceGolem(World world) {
        super(world);
        this.texture = EntityFurnaceGolem.TEXTURES[0];
        this.isImmuneToFire = true;
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 2);
    }

    /// Used to initialize dataWatcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        /// burningState; While this is 1, the furnace will be in its "on" state.
        this.dataWatcher.addObject(30, Byte.valueOf((byte)0));
    }

    /// Returns true if this mob is on fire. Used for rendering.
    @Override
    public boolean isBurning() {
        return this.getBurningState();
    }

    /// Gets/sets this lava monster's burningState variable. Used for rendering.
    public boolean getBurningState() {
        return this.dataWatcher.getWatchableObjectByte(30) == 1;
    }
    public void setBurningState(boolean state) {
        this.dataWatcher.updateObject(30, Byte.valueOf(state ? (byte)1 : (byte)0));
    }

    /// Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return slot == 2 ? false : slot == 1 ? TileEntityFurnace.isItemFuel(itemStack) : true;
    }

    /// Returns an array containing the indices of the slots that can be accessed by automation on the given side of this block.
    @Override /// ISidedInventory
    public int[] getAccessibleSlotsFromSide(int side) {
        return side == 0 ? new int[] { 2, 1 } : side == 1 ? new int[] { 0 } : new int[] { 1 };
    }

    /// Returns true if automation can insert the given item in the given slot from the given side.
    @Override /// ISidedInventory
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return this.isItemValidForSlot(slot, itemStack);
    }

    /// Returns true if automation can extract the given item in the given slot from the given side.
    @Override /// ISidedInventory
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return side != 0 || slot != 1 || itemStack.getItem() == Items.bucket;
    }

    /// Returns the number of slots in the inventory.
    @Override
    public int getSizeInventory() {
        return 3;
    }

    /// Returns the name of the inventory.
    @Override
    public String getInventoryName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag() : "Furnace Golem";
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.furnace);
    }

    /// Opens this block golem's GUI.
    @Override
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            GuiHelper.displayGUIFurnace(player, this);
        }
        return true;
    }

    @Override
    public int getUsePermissions() {
        return super.getUsePermissions() | TargetHelper.PERMISSION_OPEN;
    }

    /// Called each tick this entity exists.
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote) {
            this.texture = this.getBurningState() ? EntityFurnaceGolem.TEXTURES[1] : EntityFurnaceGolem.TEXTURES[0];
        }
    }

    /// Called each tick this entity is alive.
    @Override
    public void onLivingUpdate() {
        if (this.burnTime > 0) {
            this.burnTime--;
        }
        if (!this.worldObj.isRemote) {
            if (this.burnTime == 0 && this.canSmelt()) {
                this.itemBurnTime = this.burnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1));
                if (this.burnTime > 0 && this.getStackInSlot(1) != null) {
                    this.getStackInSlot(1).stackSize--;
                    if (this.getStackInSlot(1).stackSize == 0) {
                        this.setInventorySlotContents(1, this.getStackInSlot(1).getItem().getContainerItem(this.getStackInSlot(1)));
                    }
                }
            }
            if (this.getBurningState() && this.canSmelt()) {
                this.cookTime++;
                if (this.cookTime == 200) {
                    this.cookTime = 0;
                    this.smeltItem();
                }
            }
            else {
                this.cookTime = 0;
            }
            boolean burnState = this.burnTime > 0;
            if (this.getBurningState() != burnState) {
                this.setBurningState(burnState);
            }
        }
        super.onLivingUpdate();
    }

    /// Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
    public boolean canSmelt() {
        if (this.getStackInSlot(0) == null)
            return false;
        ItemStack itemStack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0));
        if (itemStack == null)
            return false;
        if (this.getStackInSlot(2) == null)
            return true;
        if (!this.getStackInSlot(2).isItemEqual(itemStack))
            return false;
        int result = this.getStackInSlot(2).stackSize + itemStack.stackSize;
        return result <= this.getInventoryStackLimit() && result <= itemStack.getMaxStackSize();
    }

    /// Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack.
    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack itemStack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0));
            if (this.getStackInSlot(2) == null) {
                this.setInventorySlotContents(2, itemStack.copy());
            }
            else if (this.getStackInSlot(2).isItemEqual(itemStack)) {
                this.getStackInSlot(2).stackSize += itemStack.stackSize;
            }
            this.getStackInSlot(0).stackSize--;
            if (this.getStackInSlot(0).stackSize <= 0) {
                this.setInventorySlotContents(0, null);
            }
        }
    }

    /// Saves this entity to NBT.
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setShort("BurnTime", (short)this.burnTime);
        tag.setShort("CookTime", (short)this.cookTime);
    }

    /// Loads this entity from NBT.
    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.itemBurnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1));
    }

    /// Loads this entity from NBT.
    @Override
    public void takeContentsFromNBT(NBTTagCompound tag) {
        super.takeContentsFromNBT(tag);
        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.itemBurnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1));
        tag.setShort("BurnTime", (short)0);
        tag.setShort("CookTime", (short)0);
    }
}