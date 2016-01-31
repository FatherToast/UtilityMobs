package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSkeletonGolem extends ModelZombie
{
    public ModelSkeletonGolem() {
        this(0.0F);
    }

    public ModelSkeletonGolem(float f) {
        super(f, 0.0F, 64, 32);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float time, float moveSpeed, float partialTicks) {
        this.aimedBow = entity.getEquipmentInSlot(0) != null && entity.getEquipmentInSlot(0).getItem() instanceof ItemBow;
        super.setLivingAnimations(entity, time, moveSpeed, partialTicks);
    }
}