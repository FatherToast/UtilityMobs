package toast.utilityMobs.block;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;

public class EntityJukeboxGolem extends EntityBlockGolem
{
    /// The textures for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "block/jukeboxGolem.png");

    public String lastRecord = "";

    public EntityJukeboxGolem(World world) {
        super(world);
        this.equipmentDropChances[0] = 2.0F;
        this.texture = EntityJukeboxGolem.TEXTURE;
    }

    // Used to initialize data watcher variables.
    @Override
    protected void entityInit() {
        super.entityInit();
        // record; The music disc currently playing.
        this.dataWatcher.addObject(31, "");
    }

    /// Get/set functions for the record name.
    public String getRecord() {
        return this.dataWatcher.getWatchableObjectString(31);
    }
    public void setRecord(ItemRecord record) {
        if (record == null) {
            if (!this.getRecord().equals("")) {
                this.dataWatcher.updateObject(31, "");
            }
        }
        else if (!this.getRecord().equals(record.recordName)) {
            this.dataWatcher.updateObject(31, record.recordName);
        }
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.jukebox);
    }

    /// Called each tick this entity is alive.
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.worldObj.isRemote && !this.getRecord().equals(this.lastRecord)) {
            this.lastRecord = this.getRecord();
            _UtilityMobs.proxy.playRecordGolem(this, this.lastRecord);
        }
    }

    /// Opens this block golem's GUI.
    @Override
    public boolean openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            ItemStack heldItem = this.getEquipmentInSlot(0);
            if (heldItem != null) {
                if (!player.capabilities.isCreativeMode) {
                    float power = 0.7F;
                    double xOff = this.rand.nextFloat() * power - power * 0.5;
                    double yOff = this.rand.nextFloat() * power + (1.0F - power) * 0.2 + 0.6;
                    double zOff = this.rand.nextFloat() * power - power * 0.5;
                    ItemStack dropItem = heldItem.copy();
                    EntityItem entityItem = new EntityItem(this.worldObj, this.posX + xOff, this.posY + yOff, this.posZ + zOff, dropItem);
                    entityItem.delayBeforeCanPickup = 10;
                    this.worldObj.spawnEntityInWorld(entityItem);
                }

                this.setCurrentItemOrArmor(0, null);
                this.setRecord(null);
            }
            else {
                ItemStack playerHeld = player.getEquipmentInSlot(0);
                if (playerHeld != null && playerHeld.getItem() instanceof ItemRecord) {
                    this.setCurrentItemOrArmor(0, playerHeld.copy());
                    this.getEquipmentInSlot(0).stackSize = 1;
                    this.setRecord((ItemRecord) playerHeld.getItem());

                    if (!player.capabilities.isCreativeMode) {
                        playerHeld.stackSize--;
                    }
                    if (playerHeld.stackSize <= 0) {
                        player.setCurrentItemOrArmor(0, null);
                    }
                    player.swingItem();
                }
            }
        }
        return true;
    }

    @Override
    public int getUsePermissions() {
        return super.getUsePermissions() | TargetHelper.PERMISSION_OPEN;
    }
}