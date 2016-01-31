package toast.utilityMobs.golem;

import java.util.List;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.TargetHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIFollowEntity;

public class EntityMelonGolem extends EntityStackGolem
{
    // The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "golem/melonGolem.png");

    public EntityMelonGolem(World world) {
        super(world);
        this.texture = EntityMelonGolem.TEXTURE;
        this.tasks.addTask(1, this.sitAI);
        this.sitAI.setMutexBits(7);
        this.tasks.addTask(2, new EntityAIFollowEntity(this, EntityGolem.class, 1.0, 4.0F, 32.0F));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
    }

    // Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(4, new ItemStack(Blocks.pumpkin));
        this.equipmentDropChances[4] = 0.0F;
        return data;
    }

    // Called each tick while this entity is alive.
    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && this.golemAttackTime <= 0) {
            this.golemAttackTime = 80;
            float healRange = 16.0F;
            List<EntityGolem> nearbyGolems = this.worldObj.getEntitiesWithinAABB(EntityGolem.class, this.boundingBox.expand(healRange, healRange, healRange));
            for (EntityGolem golem : nearbyGolems) {
                if (!(golem instanceof EntityMelonGolem) && golem.getHealth() < golem.getMaxHealth() && this.getDistanceSqToEntity(golem) <= healRange * healRange) {
                    if (golem instanceof EntityUtilityGolem && this.targetHelper.owner != ((EntityUtilityGolem) golem).targetHelper.owner && this.targetHelper.canDamagePlayer(((EntityUtilityGolem) golem).targetHelper.owner)) {
                        continue;
                    }
                    if (this.getEntitySenses().canSee(golem)) {
                        golem.heal(1.0F);
                        this.worldObj.playAuxSFX(2005, (int) Math.floor(golem.posX), (int) Math.floor(golem.posY + golem.getEyeHeight()), (int) Math.floor(golem.posZ), 0);
                    }
                }
            }
        }
        super.onLivingUpdate();
    }

    @Override
    protected Item getDropItem() {
        return Items.melon;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        for (int i = this.rand.nextInt(16); i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.canInteract(player)) {
            ItemStack playerHeld = player.getEquipmentInSlot(0);
            if (player.isSneaking()) {
                this.sitAI.sit = !this.isSitting();
            }
            else if (playerHeld != null && playerHeld.getItem() == Items.melon && this.getHealth() < this.getMaxHealth()) {
                if (!player.capabilities.isCreativeMode) {
                    playerHeld.stackSize--;
                }
                if (playerHeld.stackSize <= 0) {
                    player.setCurrentItemOrArmor(0, null);
                }
                this.heal(1.0F);
                return true;
            }
        }
        return super.interact(player);
    }

    @Override
    public int getUsePermissions() {
        return TargetHelper.PERMISSION_TARGET;
    }
}