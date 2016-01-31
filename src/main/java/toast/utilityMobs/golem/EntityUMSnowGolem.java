package toast.utilityMobs.golem;

import net.minecraft.block.material.Material;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;

public class EntityUMSnowGolem extends EntityStackGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/snowman.png");

    public EntityUMSnowGolem(World world) {
        super(world);
        this.texture = EntityUMSnowGolem.TEXTURE;
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIWeaponAttack(this, 1.0));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(4, new ItemStack(Blocks.pumpkin));
        this.equipmentDropChances[4] = 0.0F;
        this.setCurrentItemOrArmor(0, new ItemStack(Items.snowball));
        this.equipmentDropChances[0] = 0.0F;
        return data;
    }

    // Called each tick while this entity is alive.
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.posY);
        int blockZ = MathHelper.floor_double(this.posZ);

        if (this.isWet()) {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }
        if (this.worldObj.getBiomeGenForCoords(blockX, blockZ).getFloatTemperature(blockX, blockY, blockZ) > 1.0F) {
            this.attackEntityFrom(DamageSource.onFire, 1.0F);
        }

        for (int l = 0; l < 4; l++) {
            blockX = MathHelper.floor_double(this.posX + (l % 2 * 2 - 1) * 0.25F);
            blockY = MathHelper.floor_double(this.posY);
            blockZ = MathHelper.floor_double(this.posZ + (l / 2 % 2 * 2 - 1) * 0.25F);
            if (this.worldObj.getBlock(blockX, blockY, blockZ).getMaterial() == Material.air && this.worldObj.getBiomeGenForCoords(blockX, blockZ).getFloatTemperature(blockX, blockY, blockZ) < 0.8F && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, blockX, blockY, blockZ)) {
                this.worldObj.setBlock(blockX, blockY, blockZ, Blocks.snow_layer);
            }
        }
    }

    @Override
    protected Item getDropItem() {
        return Items.snowball;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        for (int i = this.rand.nextInt(16); i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }
}