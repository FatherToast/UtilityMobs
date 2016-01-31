package toast.utilityMobs.client.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import toast.utilityMobs.client.model.ModelChestGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class RenderChestGolem extends RenderLiving
{
    public RenderChestGolem() {
        super(new ModelChestGolem(), 0.5F);
    }

    /// Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityUtilityGolem)entity).getTexture();
    }
}