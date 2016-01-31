package toast.utilityMobs.block;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityChestTrappedGolem extends EntityChestGolem
{
    /// The texture for this class.
    @SuppressWarnings("hiding")
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "block/chestTrappedGolem.png");

    public EntityChestTrappedGolem(World world) {
        super(world);
        this.texture = EntityChestTrappedGolem.TEXTURE;
        this.sitAI.setMutexBits(7);
        this.sitAI.sitAnywhere = true;
    }

    // Returns the bounding box for this entity. Prevents movement.
    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.isSitting() && _UtilityMobs.proxy.solidEntities() ? this.boundingBox : super.getBoundingBox();
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.trapped_chest);
    }

    /// Locks this entity's rotationYaw to a cardinal direction.
    public void snapRotationYaw() {
        this.rotationYaw %= 360.0F;
        if (this.rotationYaw < -135.0F || this.rotationYaw >= 135.0F) {
            this.rotationYaw = 180.0F;
        }
        else if (this.rotationYaw < -45.0F) {
            this.rotationYaw = -90.0F;
        }
        else if (this.rotationYaw < 45.0F) {
            this.rotationYaw = 0.0F;
        }
        else {
            this.rotationYaw = 90.0F;
        }
    }

    /// Sets the isSitting variable.
    @Override
    public void setSitting(boolean sitting) {
        super.setSitting(sitting);
        if (sitting) {
            this.posX = Math.floor(this.posX) + 0.5;
            this.posY = Math.ceil(this.posY);
            this.posZ = Math.floor(this.posZ) + 0.5;
            this.snapRotationYaw();
            this.rotationPitch = 0.0F;
        }
    }

    /// Moves this entity.
    @Override
    public void moveEntity(double x, double y, double z) {
        if (!this.isSitting()) {
            super.moveEntity(x, y, z);
        }
        else {
            this.motionY = 0.0;
        }
    }
}