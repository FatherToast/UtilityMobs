package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class ModelTurret extends ModelBase
{
    public ModelRenderer head;
    public ModelRenderer leg;
    public ModelRenderer foot;

    public ModelTurret() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.head = new ModelRenderer(this, 0, 8);
        this.head.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12);
        this.head.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.leg = new ModelRenderer(this, 56, 12);
        this.leg.addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
        this.leg.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.foot = new ModelRenderer(this, 36, 0);
        this.foot.addBox(-6.0F, -6.0F, -2.0F, 12, 12, 2);
        this.foot.setRotationPoint(0.0F, 22.0F, 0.0F);
    }

    @Override
    public void render(Entity e, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(e, f, f1, f2, f3, f4, f5);
        this.head.rotateAngleY = f3 / (180.0F / (float)Math.PI);
        this.head.rotateAngleX = f4 / (180.0F / (float)Math.PI);
        this.foot.rotateAngleY = ((EntityLiving)e).renderYawOffset / (180.0F / (float)Math.PI);
        this.foot.rotateAngleX = 1.570796F;
        this.head.render(f5);
        this.leg.render(f5);
        this.foot.render(f5);
    }
}