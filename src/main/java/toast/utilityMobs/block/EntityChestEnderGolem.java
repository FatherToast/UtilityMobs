package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityChestEnderGolem extends EntityChestGolem
{
    /// The texture for this class.
    @SuppressWarnings("hiding")
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "block/chestEnderGolem.png");

    public EntityChestEnderGolem(World world) {
        super(world);
        this.texture = EntityChestEnderGolem.TEXTURE;
    }

    @Override
    public int getTotalArmorValue() {
        return 20;
    }

    // Called each tick this entity is alive.
    @Override
    public void onLivingUpdate() {
        if (this.worldObj.isRemote && this.rand.nextInt(4) == 0) {
            int xOff, zOff;
            double vX, vY, vZ;
            for (int i = 3; i-- > 0;) {
                xOff = this.rand.nextInt(2) * 2 - 1;
                zOff = this.rand.nextInt(2) * 2 - 1;
                vX = this.rand.nextFloat() * 1.0F * xOff;
                vY = (this.rand.nextFloat() - 0.5) * 0.125;
                vZ = this.rand.nextFloat() * 1.0F * zOff;
                this.worldObj.spawnParticle("portal", this.posX + 0.25 * xOff, this.posY + this.rand.nextFloat(), this.posZ + 0.25 * zOff, vX, vY, vZ);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.obsidian);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        super.dropFewItems(recentlyHit, looting, dropChance);
        for (int i = 7;  i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    /// Opens this block golem's GUI.
    @Override
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            InventoryEnderChest inventory = player.getInventoryEnderChest();
            if (inventory != null) {
                inventory.func_146031_a(new TileEntityEnderChestProxy(this)); // Sets this as a "tile entity" for the player's ender chests
                player.displayGUIChest(inventory);
            }
        }
        return true;
    }
}