package toast.utilityMobs.golem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIFollowEntity;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;

public class EntityBoundSoul extends EntityUtilityGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "golem/boundSoul.png");

    public EntityBoundSoul(World world) {
        super(world);
        this.texture = EntityBoundSoul.TEXTURE;
        this.tasks.addTask(1, new EntityAIWeaponAttack(this, 1.0));
        this.tasks.addTask(2, new EntityAIFollowEntity(this, EntityPlayer.class, 1.0, 10.0F, 16.0F));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemFromBlock(Blocks.soul_sand);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        for (int i = this.rand.nextInt(3); i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!this.canInteract(player))
            return super.interact(player);
        ItemStack playerHeld = player.getEquipmentInSlot(0);
        if (playerHeld == null) {
            if (!this.worldObj.isRemote) {
                if (!this.setEquipment(null))
                    return super.interact(player);
            }
            return true;
        }
        if (player.isSneaking())
            return super.interact(player);
        if (playerHeld.getItem() instanceof ItemFood) {
            this.heal(((ItemFood)playerHeld.getItem()).func_150905_g(playerHeld));
        }
        else if (!this.worldObj.isRemote) {
            ItemStack split = playerHeld.copy();
            split.stackSize = 1;
            this.setEquipment(split);
        }
        if (!player.capabilities.isCreativeMode) {
            playerHeld.stackSize--;
        }
        if (playerHeld.stackSize <= 0) {
            player.setCurrentItemOrArmor(0, null);
        }
        return true;
    }

    @Override
    public int getUsePermissions() {
        return super.getUsePermissions() | TargetHelper.PERMISSION_OPEN;
    }
}