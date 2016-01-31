package toast.utilityMobs.client.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import toast.utilityMobs.client.model.ModelLargeGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class RenderLargeGolem extends RenderLiving
{
    public RenderLargeGolem() {
        super(new ModelLargeGolem(), 0.5F);
    }

    /// Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityUtilityGolem)entity).getTexture();
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float yaw, float pitch, float partialTicks) {
        super.rotateCorpse(entity, yaw, pitch, partialTicks);
        if (entity.limbSwingAmount >= 0.01) {
            float f3 = 13.0F;
            float f4 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f5 = (Math.abs(f4 % f3 - f3 * 0.5F) - f3 * 0.25F) / (f3 * 0.25F);
            GL11.glRotatef(6.5F * f5, 0.0F, 0.0F, 1.0F);
        }
    }
}