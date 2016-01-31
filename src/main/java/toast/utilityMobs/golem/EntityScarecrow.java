package toast.utilityMobs.golem;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIFollowEntity;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;

public class EntityScarecrow extends EntityUtilityGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "golem/scarecrow.png");

    public EntityScarecrow(World world) {
        super(world);
        this.texture = EntityScarecrow.TEXTURE;
        this.tasks.addTask(1, new EntityAIWeaponAttack(this, 1.0));
        this.tasks.addTask(2, new EntityAIFollowEntity(this, EntityPlayer.class, 1.0, 4.0F, 16.0F));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(4, new ItemStack(Blocks.pumpkin));
        this.equipmentDropChances[4] = 0.0F;
        return data;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.wool);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        for (int i = this.rand.nextInt(3); i-- > 0;) {
            this.dropItem(Item.getItemFromBlock(Blocks.fence), 1);
        }
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!this.canInteract(player))
            return super.interact(player);
        ItemStack playerHeld = player.getEquipmentInSlot(0);
        if (playerHeld == null && this.getEquipmentInSlot(0) == null)
            return super.interact(player);
        if (playerHeld == null) {
            this.setEquipment(0, null);
        }
        else if (player.isSneaking())
            return super.interact(player);
        else {
            ItemStack heldItem = this.getEquipmentInSlot(0);
            if (!this.worldObj.isRemote) {
                ItemStack split = playerHeld.copy();
                split.stackSize = 1;
                this.setEquipment(0, split);
            }
            if (!player.capabilities.isCreativeMode) {
                playerHeld.stackSize--;
            }
            if (playerHeld.stackSize <= 0) {
                player.setCurrentItemOrArmor(0, null);
            }
        }
        return true;
    }

    @Override
    public int getUsePermissions() {
        return super.getUsePermissions() | TargetHelper.PERMISSION_OPEN;
    }
}