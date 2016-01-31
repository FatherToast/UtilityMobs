package toast.utilityMobs.golem;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs.EffectHelper;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.ai.EntityAIGolemTarget;
import toast.utilityMobs.ai.EntityAIWeaponAttack;

public class EntityGildedGolem extends EntityUtilityGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "golem/gildedGolem.png");

    public EntityGildedGolem(World world) {
        super(world);
        this.texture = EntityGildedGolem.TEXTURE;
        this.tasks.addTask(1, new EntityAIWeaponAttack(this, 1.0));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIGolemTarget(this));
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
        this.setCurrentItemOrArmor(4, new ItemStack(Items.golden_helmet));
        this.setCurrentItemOrArmor(3, new ItemStack(Items.golden_chestplate));
        this.setCurrentItemOrArmor(2, new ItemStack(Items.golden_leggings));
        this.setCurrentItemOrArmor(1, new ItemStack(Items.golden_boots));
        for (int i = 5; i-- > 0;) {
            EffectHelper.enchantItem(this.getEquipmentInSlot(i), Enchantment.thorns, 1);
            this.equipmentDropChances[i] = 0.0F;
        }
        return data;
    }

    @Override
    protected Item getDropItem() {
        return Items.gold_ingot;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting, float dropChance) {
        for (int i = this.rand.nextInt(3) + 3; i-- > 0;) {
            this.dropItem(this.getDropItem(), 1);
        }
    }

    @Override
    public void hitEffects(Entity entity) {
        this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, entity.posX, entity.posY, entity.posZ, 1));
    }
}