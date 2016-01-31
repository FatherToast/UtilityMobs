package toast.utilityMobs.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.network.GuiHelper;

public class EntityAnvilGolem extends EntityContainerGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURES[] = { new ResourceLocation(_UtilityMobs.TEXTURE + "block/anvilGolem_0.png"), new ResourceLocation(_UtilityMobs.TEXTURE + "block/anvilGolem_1.png"), new ResourceLocation(_UtilityMobs.TEXTURE + "block/anvilGolem_2.png") };

    public EntityAnvilGolem(World world) {
        super(world);
        this.texture = EntityAnvilGolem.TEXTURES[0];
        this.isImmuneToFire = true;
    }

    /// Returns the texture for this mob.
    @Override
    public ResourceLocation getTexture() {
        return EntityAnvilGolem.TEXTURES[this.getDamage()];
    }

    /// Used to initialize data watcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        /// damage; The amount of damage this anvil golem has taken from use.
        this.dataWatcher.addObject(19, Byte.valueOf((byte)0));
    }

    @Override
    public int getTotalArmorValue() {
        return Math.min(20, super.getTotalArmorValue() + 16);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.anvil);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (recentlyHit) {
            this.entityDropItem(new ItemStack(this.getDropItem(), 1, this.getDamage()), 0.0F);
            if (this.rand.nextFloat() < dropChance / 4.0F) {
                this.dropItem(Items.skull, 1);
            }
        }
    }

    /// Opens this block golem's GUI.
    @Override
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            GuiHelper.displayGUIAnvil(player, this);
        }
        return true;
    }

    /// Gets/sets the usage damage.
    public int getDamage() {
        return this.dataWatcher.getWatchableObjectByte(19);
    }
    public void setDamage(int damage) {
        this.dataWatcher.updateObject(19, Byte.valueOf((byte)damage));
    }

    /// Saves this entity to NBT.
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("Damage", (byte)this.getDamage());
    }

    /// Loads this entity from NBT.
    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setDamage(tag.getByte("Damage"));
    }
}