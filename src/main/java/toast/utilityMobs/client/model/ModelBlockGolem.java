package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import toast.utilityMobs.golem.EntityUtilityGolem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlockGolem extends ModelBase
{
    public ModelRenderer body;
    public ModelRenderer legFrontLeft;
    public ModelRenderer legFrontRight;
    public ModelRenderer legBackLeft;
    public ModelRenderer legBackRight;

    public ModelBlockGolem() {
        this.body = new ModelRenderer(this).addBox(-8.0F, -16.0F, -8.0F, 16, 16, 16);
        this.body.setRotationPoint(0.0F, 20.0F, 0.0F);

        this.legFrontLeft = new ModelRenderer(this).addBox(-2.0F, -1.0F, -4.0F, 4, 6, 4);
        this.legFrontLeft.setRotationPoint(8.0F, 19.0F, -6.0F);
        this.legFrontLeft.mirror = true;

        this.legFrontRight = new ModelRenderer(this).addBox(-2.0F, -1.0F, -4.0F, 4, 6, 4);
        this.legFrontRight.setRotationPoint(-8.0F, 19.0F, -6.0F);

        this.legBackLeft = new ModelRenderer(this).addBox(-2.0F, -1.0F, 0.0F, 4, 6, 4);
        this.legBackLeft.setRotationPoint(8.0F, 19.0F, 6.0F);
        this.legBackLeft.mirror = true;

        this.legBackRight = new ModelRenderer(this).addBox(-2.0F, -1.0F, 0.0F, 4, 6, 4);
        this.legBackRight.setRotationPoint(-8.0F, 19.0F, 6.0F);
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
        this.body.render(scale);
        GL11.glPopMatrix();
        this.legFrontLeft.render(scale);
        this.legFrontRight.render(scale);
        this.legBackLeft.render(scale);
        this.legBackRight.render(scale);
    }
}