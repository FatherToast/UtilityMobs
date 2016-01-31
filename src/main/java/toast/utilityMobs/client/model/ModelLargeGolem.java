package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import toast.utilityMobs.golem.EntityLargeGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class ModelLargeGolem extends ModelBase
{
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer armRight;
    public ModelRenderer armLeft;
    public ModelRenderer legLeft;
    public ModelRenderer legRight;

    public ModelLargeGolem() {
        this(0.0F);
    }

    public ModelLargeGolem(float f) {
        this(f, -7.0F);
    }

    public ModelLargeGolem(float f, float f1) {
        this.head = new ModelRenderer(this).setTextureSize(128, 128);
        this.head.setRotationPoint(0.0F, 0.0F + f1, -2F);
        this.head.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, f);
        this.head.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, f);
        this.body = new ModelRenderer(this).setTextureSize(128, 128);
        this.body.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        this.body.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, f);
        this.body.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, f + 0.5F);
        this.armRight = new ModelRenderer(this).setTextureSize(128, 128);
        this.armRight.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.armRight.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, f);
        this.armLeft = new ModelRenderer(this).setTextureSize(128, 128);
        this.armLeft.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.armLeft.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, f);
        this.legLeft = new ModelRenderer(this, 0, 22).setTextureSize(128, 128);
        this.legLeft.setRotationPoint(-4.0F, 18.0F + f1, 0.0F);
        this.legLeft.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
        this.legRight = new ModelRenderer(this, 0, 22).setTextureSize(128, 128);
        this.legRight.mirror = true;
        this.legRight.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + f1, 0.0F);
        this.legRight.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
    }

    @Override
    public void render(Entity entity, float time, float moveSpeed, float rotationFloat, float rotationYaw, float rotationPitch, float scale) {
        EntityUtilityGolem golem = (EntityUtilityGolem)entity;
        if (golem.isSitting()) {
            moveSpeed = 0.0F;
        }
        this.head.rotateAngleY = rotationYaw / (180.0F / (float)Math.PI);
        this.head.rotateAngleX = rotationPitch / (180.0F / (float)Math.PI);
        this.legLeft.rotateAngleX = -1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.legRight.rotateAngleX = 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, golem.isSitting() ? 0.25F : 0.0F, 0.0F);
        this.head.render(scale);
        this.body.render(scale);
        this.armRight.render(scale);
        this.armLeft.render(scale);
        GL11.glPopMatrix();
        this.legLeft.render(scale);
        this.legRight.render(scale);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase e, float par2, float par3, float par4) {
        EntityLargeGolem e1 = (EntityLargeGolem)e;
        int i = e1.getHitTime();
        if (i > 0) {
            this.armRight.rotateAngleX = -2.0F + 1.5F * this.adjust(i - par4, 10.0F);
            this.armLeft.rotateAngleX = -2.0F + 1.5F * this.adjust(i - par4, 10.0F);
        }
        else {
            int j = e1.getAnimationTime();
            if (j > 0) {
                this.armRight.rotateAngleX = -0.8F + 0.025F * this.adjust(j, 70.0F);
                this.armLeft.rotateAngleX = 0.0F;
            }
            else {
                this.armRight.rotateAngleX = (-0.2F + 1.5F * this.adjust(par2, 13.0F)) * par3;
                this.armLeft.rotateAngleX = (-0.2F - 1.5F * this.adjust(par2, 13.0F)) * par3;
            }
        }
    }

    private float adjust(float f, float f1) {
        return (Math.abs(f % f1 - f1 * 0.5F) - f1 * 0.25F) / (f1 * 0.25F);
    }
}