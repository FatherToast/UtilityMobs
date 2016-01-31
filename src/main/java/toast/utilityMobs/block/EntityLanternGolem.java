package toast.utilityMobs.block;

import net.minecraft.block.Block;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.network.GuiHelper;

public class EntityLanternGolem extends EntityContainerGolem
{
    /// The textures for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "block/lanternGolem.png");

    public EntityLanternGolem(World world) {
        super(world);
        this.texture = EntityLanternGolem.TEXTURE;
        this.aiFollow.minDistance = 2.0F;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(4, new ItemStack(Blocks.lit_pumpkin));
        this.equipmentDropChances[4] = 0.0F;
        return data;
    }

    /// Returns the number of slots in the inventory.
    @Override
    public int getSizeInventory() {
        return 9;
    }

    /// Returns the name of the inventory.
    @Override
    public String getInventoryName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag() : "Jack o'Lantern Golem";
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.pumpkin);
    }

    /// Opens this block golem's GUI.
    @Override
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

    /// Called each tick this entity is alive.
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Place a light if it is dark enough.
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.posY);
        int blockZ = MathHelper.floor_double(this.posZ);
        if (!this.worldObj.isRemote && !this.sitAI.sit && this.worldObj.getBlockLightValue(blockX, blockY, blockZ) <= 7 && this.worldObj.getBlock(blockX, blockY, blockZ).getMaterial().isReplaceable()) {
            for (int i = this.getSizeInventory(); i-- > 0;) {
                ItemStack itemStack = this.getStackInSlot(i);
                if (itemStack != null) {
                    Block block = Block.getBlockFromItem(itemStack.getItem());
                    if (block != Blocks.air && block.getLightValue() > 7 && !block.isOpaqueCube()) {
                        int data = itemStack.getItem().getMetadata(itemStack.getItemDamage());
                        this.worldObj.setBlock(blockX, blockY, blockZ, block, data, 2);
                        itemStack.stackSize--;
                        if (itemStack.stackSize <= 0) {
                            this.setInventorySlotContents(i, null);
                        }
                        break;
                    }
                }
            }
        }
    }
}