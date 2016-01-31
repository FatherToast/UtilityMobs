package toast.utilityMobs.block;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs.ai.EntityAIGolemFollow;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class EntityBlockGolem extends EntityUtilityGolem
{
    // Save the reference to the follow AI so it can be easily removed or altered.
    public EntityAIGolemFollow aiFollow = new EntityAIGolemFollow(this, 1.0, 10.0F, 5.0F);

    public EntityBlockGolem(World world) {
        super(world);
        this.setSize(0.9375F, 0.9375F);
        this.tasks.addTask(1, this.sitAI);
        this.tasks.addTask(2, this.aiFollow);
        this.tasks.addTask(3, new EntityAIWander(this, 1.0));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.chest);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        if (recentlyHit) {
            this.dropItem(this.getDropItem(), 1);
            if (this.rand.nextFloat() < dropChance / 4.0F) {
                this.dropItem(Items.skull, 1);
            }
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.canInteract(player)) {
            if (player.isSneaking()) {
                this.sitAI.sit = !this.isSitting();
                if (!this.sitAI.sit) {
                    this.setClosed();
                }
            }
            else if (this.openGUI(player))
                return true;
        }
        return super.interact(player);
    }

    /// Called when this block golem is told to get up.
    public void setClosed() {
        // To be overridden
    }

    /// Opens this block golem's GUI.
    public boolean openGUI(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean canInteract(EntityPlayer player) {
        if (player.isSneaking())
            return this.func_152113_b() == "" || this.func_152113_b().equals(player.getCommandSenderName()) || this.targetHelper.playerHasPermission(player.getCommandSenderName(), TargetHelper.PERMISSION_TARGET | TargetHelper.PERMISSION_USE);
        return super.canInteract(player) && player.getDistanceSqToEntity(this) <= 64.0;
    }
}