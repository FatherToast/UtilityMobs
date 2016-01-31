package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import toast.utilityMobs.block.EntityChestGolem;
import toast.utilityMobs.block.EntityChestTrappedGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChestGolem extends ModelBase
{
    public ModelRenderer legFrontLeft;
    public ModelRenderer legFrontRight;
    public ModelRenderer legBackLeft;
    public ModelRenderer legBackRight;
    public ModelRenderer teethBottom;
    public ModelRenderer top;
    public ModelRenderer bottom;

    public ModelChestGolem() {
        this.textureHeight = 64;
        this.setTextureOffset("top.head", 0, 0);
        this.setTextureOffset("top.nose", 0, 0);
        this.setTextureOffset("top.teethTop", 0, 43);

        this.top = new ModelRenderer(this, "top")
        .addBox("head", -7.0F, -5.0F, -14.0F, 14, 5, 14)
        .addBox("nose", -1.0F, -2.0F, -15.0F, 2, 4, 1)
        .addBox("teethTop", -6.0F, 0.0F, -13.0F, 12, 1, 12);
        this.top.setRotationPoint(0.0F, 11.0F, 7.0F);

        this.bottom = new ModelRenderer(this, 0, 19).addBox(-7.0F, 0.0F, -14.0F, 14, 10, 14);
        this.bottom.setRotationPoint(0.0F, 10.0F, 7.0F);

        this.teethBottom = new ModelRenderer(this, 0, 43).addBox(-6.0F, 0.0F, -13.0F, 12, 1, 12);
        this.teethBottom.setRotationPoint(0.0F, 10.0F, 7.0F);
        this.teethBottom.rotateAngleZ = (float)Math.PI;
        this.teethBottom.mirror = true;

        this.legFrontLeft = new ModelRenderer(this, 42, 0).addBox(-2.0F, -1.0F, -4.0F, 4, 6, 4);
        this.legFrontLeft.setRotationPoint(7.0F, 19.0F, -5.0F);
        this.legFrontLeft.mirror = true;

        this.legFrontRight = new ModelRenderer(this, 42, 0).addBox(-2.0F, -1.0F, -4.0F, 4, 6, 4);
        this.legFrontRight.setRotationPoint(-7.0F, 19.0F, -5.0F);

        this.legBackLeft = new ModelRenderer(this, 42, 0).addBox(-2.0F, -1.0F, 0.0F, 4, 6, 4);
        this.legBackLeft.setRotationPoint(7.0F, 19.0F, 5.0F);
        this.legBackLeft.mirror = true;

        this.legBackRight = new ModelRenderer(this, 42, 0).addBox(-2.0F, -1.0F, 0.0F, 4, 6, 4);
        this.legBackRight.setRotationPoint(-7.0F, 19.0F, 5.0F);
    }

    @Override
    public void render(Entity entity, float time, float moveSpeed, float rotationFloat, float rotationYaw, float rotationPitch, float scale) {
        EntityUtilityGolem golem = (EntityUtilityGolem)entity;
        if (golem.isSitting()) {
            moveSpeed = 0.0F;
        }
        this.legFrontLeft.rotateAngleX = (float)Math.cos(time * 0.6662F) * 1.4F * moveSpeed;
        this.legFrontRight.rotateAngleX = (float)Math.cos(time * 0.6662F + (float)Math.PI) * 1.4F * moveSpeed;
        this.legBackLeft.rotateAngleX = (float)Math.cos(time * 0.6662F + (float)Math.PI) * 1.4F * moveSpeed;
        this.legBackRight.rotateAngleX = (float)Math.cos(time * 0.6662F) * 1.4F * moveSpeed;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, golem.isSitting() ? 0.25F : 0.0F, 0.0F);
        this.top.render(scale);
        this.bottom.render(scale);
        this.teethBottom.render(scale);
        GL11.glPopMatrix();
        if (!golem.isSitting() || !(golem instanceof EntityChestTrappedGolem)) {
            this.legFrontLeft.render(scale);
            this.legFrontRight.render(scale);
            this.legBackLeft.render(scale);
            this.legBackRight.render(scale);
        }
    }

    // Used for easily adding entity-dependent animations.
    @Override
    public void setLivingAnimations(EntityLivingBase entity, float time, float moveSpeed, float partialTicks) {
        EntityChestGolem golem = (EntityChestGolem)entity;
        float angle = 1.0F - golem.prevLidAngle - (golem.lidAngle - golem.prevLidAngle) * partialTicks;
        angle = 1.0F - angle * angle * angle;
        this.top.rotateAngleX = -angle * (float)Math.PI / 2.0F;
    }
}