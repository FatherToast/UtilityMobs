package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import toast.utilityMobs.colossal.EntityColossalGolem;


public class ModelColossalGolem extends ModelBase
{
    private static final float TO_RADS = (float) Math.PI / 180.0F;

    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer lower;
    public ModelRenderer armRight;
    public ModelRenderer forearmRight;
    public ModelRenderer armLeft;
    public ModelRenderer legRight;
    public ModelRenderer lowerLegRight;
    public ModelRenderer legLeft;
    public ModelRenderer lowerLegLeft;
    public ModelRenderer forearmLeft;

    public ModelColossalGolem() {
        this.head = new ModelRenderer(this).setTextureSize(128, 128);
        this.head.setRotationPoint(0.0F, -9.0F, -5.0F);
        this.head.setTextureOffset(0, 0).addBox(-5.0F, -8.0F, -10.5F, 10, 12, 10);
        this.head.setTextureOffset(30, 0).addBox(-1.5F, 0.0F, -13.5F, 3, 5, 3);

        this.body = new ModelRenderer(this).setTextureSize(128, 128);
        this.body.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.body.setTextureOffset(0, 22).addBox(-12.0F, -4.0F, -8.0F, 24, 18, 16);
        this.lower = new ModelRenderer(this).setTextureSize(128, 128);
        this.lower.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.lower.setTextureOffset(4, 56).addBox(-6.0F, 8.0F, 3.0F, 12, 11, 10);

        this.armRight = new ModelRenderer(this).setTextureSize(128, 128);
        this.armRight.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.armRight.setTextureOffset(56, 56).addBox(-16.0F, -4.5F, -3.0F, 4, 18, 6);
        this.forearmRight = new ModelRenderer(this).setTextureSize(128, 128);
        this.forearmRight.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.forearmRight.setTextureOffset(52, 80).addBox(-17.0F, 10.0F, -1.0F, 6, 18, 8);

        this.armLeft = new ModelRenderer(this).setTextureSize(128, 128);
        this.armLeft.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.armLeft.setTextureOffset(84, 56).addBox(12.0F, -4.5F, -3.0F, 4, 18, 6);
        this.forearmLeft = new ModelRenderer(this).setTextureSize(128, 128);
        this.forearmLeft.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.forearmLeft.setTextureOffset(80, 80).addBox(11.0F, 10.0F, -1.0F, 6, 18, 8);

        this.legRight = new ModelRenderer(this).setTextureSize(128, 128);
        this.legRight.setRotationPoint(-5.0F, 9.0F, 8.0F);
        this.legRight.setTextureOffset(2, 77).addBox(-3.5F, -5.0F, -3.0F, 6, 12, 5);
        this.lowerLegRight = new ModelRenderer(this).setTextureSize(128, 128);
        this.lowerLegRight.setRotationPoint(-5.0F, 9.0F, 8.0F);
        this.lowerLegRight.setTextureOffset(0, 94).addBox(-4.0F, 2.0F, -7.0F, 7, 13, 6);

        this.legLeft = new ModelRenderer(this).setTextureSize(128, 128);
        this.legLeft.setRotationPoint(5.0F, 9.0F, 8.0F);
        this.legLeft.setTextureOffset(28, 77).addBox(-2.5F, -5.0F, -3.0F, 6, 12, 5);
        this.lowerLegLeft = new ModelRenderer(this).setTextureSize(128, 128);
        this.lowerLegLeft.setRotationPoint(5.0F, 9.0F, 8.0F);
        this.lowerLegLeft.setTextureOffset(26, 94).addBox(-3.0F, 2.0F, -7.0F, 7, 13, 6);
    }

    @Override
    public void render(Entity entity, float time, float moveSpeed, float rotationFloat, float rotationYaw, float rotationPitch, float scale) {
        this.animate((EntityColossalGolem)entity, time, moveSpeed, rotationFloat, rotationYaw, rotationPitch, scale);

        this.head.render(scale);

        this.body.render(scale);
        this.lower.render(scale);

        this.armRight.render(scale);
        this.forearmRight.render(scale);
        this.armLeft.render(scale);
        this.forearmLeft.render(scale);

        this.legRight.render(scale);
        this.lowerLegRight.render(scale);
        this.legLeft.render(scale);
        this.lowerLegLeft.render(scale);
    }

    private float adjust(float f, float f1) {
        return (Math.abs(f % f1 - f1 * 0.5F) - f1 * 0.25F) / (f1 * 0.25F);
    }

    private void setAngles(EntityColossalGolem golem, float time, float moveSpeed, float rotationFloat, float rotationYaw, float rotationPitch, float scale) {
        this.head.rotateAngleY = this.rad(rotationYaw);
        this.head.rotateAngleX = this.rad(rotationPitch);

        this.body.rotateAngleX = this.rad(48.0F);
        this.body.rotateAngleY = 0.0F;
        this.body.rotateAngleZ = 0.0F;
        this.lower.rotateAngleX = this.rad(12.0F);

        this.armRight.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.armRight.rotateAngleX = this.rad(12.0F) - 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.armRight.rotateAngleY = 0.0F;
        this.armRight.rotateAngleZ = this.rad(6.0F);
        this.forearmRight.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.forearmRight.rotateAngleX = this.rad(-5.0F) - 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.forearmRight.rotateAngleY = 0.0F;
        this.forearmRight.rotateAngleZ = this.rad(6.0F);

        this.armLeft.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.armLeft.rotateAngleX = this.rad(12.0F) + 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.armLeft.rotateAngleY = 0.0F;
        this.armLeft.rotateAngleZ = this.rad(-6.0F);
        this.forearmLeft.setRotationPoint(0.0F, -9.0F, -2.0F);
        this.forearmLeft.rotateAngleX = this.rad(-5.0F) + 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.forearmLeft.rotateAngleY = 0.0F;
        this.forearmLeft.rotateAngleZ = this.rad(-6.0F);

        this.legLeft.rotateAngleX = this.rad(-43.0F) - 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.lowerLegLeft.rotateAngleX = -1.5F * this.adjust(time, 13.0F) * moveSpeed;

        this.legRight.rotateAngleX = this.rad(-43.0F) + 1.5F * this.adjust(time, 13.0F) * moveSpeed;
        this.lowerLegRight.rotateAngleX = 1.5F * this.adjust(time, 13.0F) * moveSpeed;
    }

    public void animate(EntityColossalGolem golem, float time, float moveSpeed, float rotationFloat, float rotationYaw, float rotationPitch, float scale) {
        if (golem.isSitting()) {
            moveSpeed = 0.0F;
        }
        this.setAngles(golem, time, moveSpeed, rotationFloat, rotationYaw, rotationPitch, scale);

        int anim = golem.getAnimId();
        if (anim == 0)
            return;

        int tick = golem.getAnimTick();
        float progress = 0.0F;
        if (tick <= 6) {
            progress = tick / 6.0F;
        }
        else if (tick <= 16) {
            progress = 1.0F - (tick - 6) / 10.0F;
        }

        if (anim == EntityColossalGolem.ANIM_R_ARM_SWING) {

            this.rotate(this.body, progress, 0.0F, -16.0F, -16.0F);

            this.rotate(this.armRight, progress, -140.0F, -16.0F, -16.0F);
            this.rotate(this.forearmRight, progress, -140.0F, -20.0F, -16.0F);

            this.rotate(this.armLeft, progress, 0.0F, -16.0F, -29.0F);
            this.rotate(this.forearmLeft, progress, 0.0F, -16.0F, -16.0F);
            this.move(this.forearmLeft, progress, 0.0F, -5.0F, 0.0F);
        }
        else if (anim == EntityColossalGolem.ANIM_L_ARM_SWING) {
            this.rotate(this.body, progress, 0.0F, 16.0F, 16.0F);

            this.rotate(this.armLeft, progress, -140.0F, 16.0F, -16.0F);
            this.rotate(this.forearmLeft, progress, -140.0F, 20.0F, -16.0F);

            this.rotate(this.armRight, progress, 0.0F, 16.0F, 29.0F);
            this.rotate(this.forearmRight, progress, 0.0F, 16.0F, 16.0F);
            this.move(this.forearmRight, progress, 0.0F, -5.0F, 0.0F);
        }
    }

    private void rotate(ModelRenderer box, float progress, float x, float y, float z) {
        if (x != 0.0F) {
            box.rotateAngleX += this.rad(x) * progress;
        }
        if (y != 0.0F) {
            box.rotateAngleY += this.rad(y) * progress;
        }
        if (z != 0.0F) {
            box.rotateAngleZ += this.rad(z) * progress;
        }
    }

    private void move(ModelRenderer box, float progress, float x, float y, float z) {
        if (x != box.rotationPointX) {
            box.rotationPointX += x * progress;
        }
        if (y != box.rotationPointY) {
            box.rotationPointY += y * progress;
        }
        if (z != box.rotationPointZ) {
            box.rotationPointZ += z * progress;
        }
    }

    private float rad(float deg) {
        return deg * ModelColossalGolem.TO_RADS;
    }
}
