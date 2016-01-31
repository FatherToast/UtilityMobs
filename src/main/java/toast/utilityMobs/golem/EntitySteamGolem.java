package toast.utilityMobs.golem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;
import toast.utilityMobs.network.GuiHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySteamGolem extends EntityLargeGolem implements IInventory
{
    /// The texture for this class.
    public static final ResourceLocation[] TEXTURES = { new ResourceLocation(_UtilityMobs.TEXTURE + "golem/steamGolem.png"), new ResourceLocation(_UtilityMobs.TEXTURE + "golem/steamGolem_fire.png") };

    /// The number of ticks that the furnace will keep burning.
    public int burnTime = 0;
    /// The number of ticks that a fresh copy of the currently-burning item would burn for.
    public int maxBurnTime = 0;

    // The contents of this chest.
    private ItemStack[] contents = new ItemStack[3];

    public EntitySteamGolem(World world) {
        super(world);
        this.texture = EntitySteamGolem.TEXTURES[0];
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, this.sitAI);
        this.sitAI.setMutexBits(7);
        this.sitAI.sitAnywhere = true;
        this.tasks.addTask(2, new EntityAIWeaponAttack(this, 1.0));
        this.tasks.addTask(3, new EntityAIWander(this, 0.6));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0);
    }

    /// Returns the armor of this entity.
    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 2);
    }

    /// Used to initialize dataWatcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        /// burningState; While this is 1, the golem will be in its "on" state.
        this.dataWatcher.addObject(30, Byte.valueOf((byte)0));
    }

    /// Gets/sets this lava monster's burningState variable. Used for rendering.
    public boolean getBurningState() {
        return this.dataWatcher.getWatchableObjectByte(30) == 1;
    }
    public void setBurningState(boolean state) {
        this.dataWatcher.updateObject(30, Byte.valueOf(state ? (byte)1 : (byte)0));
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.furnace);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(this.getDropItem(), 1);
        }
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

    /// Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return TileEntityFurnace.isItemFuel(itemStack);
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
    public boolean interact(EntityPlayer player) {
        if (this.canInteract(player) && !player.isSneaking()) {
            if (this.openGUI(player))
                return true;
        }
        return super.interact(player);
    }

    /// Opens this golem's GUI.
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            GuiHelper.displayGUICustom(player, this);
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
            this.texture = this.getBurningState() ? EntitySteamGolem.TEXTURES[1] : EntitySteamGolem.TEXTURES[0];
        }
    }

    /// Called each tick this entity is alive.
    @Override
    public void onLivingUpdate() {
        if (this.burnTime > 0) {
            this.burnTime--;
        }
        if (!this.worldObj.isRemote) {
            // Load fuel from other inventory slots.
            if (this.getStackInSlot(1) == null || TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1)) <= 0) {
                ItemStack current = this.getStackInSlot(1);
                for (int slot = 0; slot < this.contents.length; slot++) {
                    if (slot != 1 && this.getStackInSlot(slot) != null && TileEntityFurnace.getItemBurnTime(this.getStackInSlot(slot)) > 0) {
                        this.setInventorySlotContents(1, this.getStackInSlot(slot));
                        this.setInventorySlotContents(slot, current);
                        break;
                    }
                }
            }
            // Burn fuel.
            if (this.burnTime == 0) {
                this.maxBurnTime = this.burnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1));
                if (this.burnTime > 0 && this.getStackInSlot(1) != null) {
                    this.getStackInSlot(1).stackSize--;
                    if (this.getStackInSlot(1).stackSize == 0) {
                        this.setInventorySlotContents(1, this.getStackInSlot(1).getItem().getContainerItem(this.getStackInSlot(1)));
                    }
                }
            }
            boolean burnState = this.burnTime > 0;
            if (this.getBurningState() != burnState) {
                this.setBurningState(burnState);
            }
            this.sitAI.sit = !burnState;
        }
        super.onLivingUpdate();
    }

    /// Saves this entity to NBT.
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
        tag.setShort("BurnTime", (short)this.burnTime);
        tag.setShort("MaxBurnTime", (short)this.maxBurnTime);
    }

    /// Loads this entity from NBT.
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
        this.burnTime = tag.getShort("BurnTime");
        this.maxBurnTime = tag.getShort("MaxBurnTime");
    }

    // Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel.
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int max) {
        if (this.maxBurnTime == 0) {
            this.maxBurnTime = 200;
        }
        return this.burnTime * max / this.maxBurnTime;
    }

    /*
     * @see net.minecraft.inventory.IInventory#getStackInSlot(int)
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents[slot];
    }

    /*
     * @see net.minecraft.inventory.IInventory#decrStackSize(int, int)
     */
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

    /*
     * @see net.minecraft.inventory.IInventory#getStackInSlotOnClosing(int)
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.contents[slot] != null) {
            ItemStack itemStack = this.contents[slot];
            this.contents[slot] = null;
            return itemStack;
        }
        return null;
    }

    /*
     * @see net.minecraft.inventory.IInventory#setInventorySlotContents(int, net.minecraft.item.ItemStack)
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        this.contents[slot] = itemStack;
        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /*
     * @see net.minecraft.inventory.IInventory#hasCustomInventoryName()
     */
    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    /*
     * @see net.minecraft.inventory.IInventory#getInventoryStackLimit()
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /*
     * @see net.minecraft.inventory.IInventory#markDirty()
     */
    @Override
    public void markDirty() {
        // Do nothing
    }

    /*
     * @see net.minecraft.inventory.IInventory#isUseableByPlayer(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.canInteract(player);
    }

    /*
     * @see net.minecraft.inventory.IInventory#openInventory()
     */
    @Override
    public void openInventory() {
        // Do nothing
    }

    /*
     * @see net.minecraft.inventory.IInventory#closeInventory()
     */
    @Override
    public void closeInventory() {
        // Do nothing
    }
}