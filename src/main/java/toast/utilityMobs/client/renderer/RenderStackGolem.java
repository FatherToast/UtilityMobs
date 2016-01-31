package toast.utilityMobs.client.renderer;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import toast.utilityMobs.client.model.ModelStackGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class RenderStackGolem extends RenderLiving
{
    private ModelStackGolem snowmanModel;

    public RenderStackGolem() {
        super(new ModelStackGolem(), 0.5F);
        this.snowmanModel = (ModelStackGolem)super.mainModel;
        this.setRenderPassModel(this.snowmanModel);
    }

    // Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityUtilityGolem)entity).getTexture();
    }

    // Renders any equipped items.
    @Override
    protected void renderEquippedItems(EntityLivingBase entity, float partialTick) {
        super.renderEquippedItems(entity, partialTick);
        ItemStack helmet = entity.getEquipmentInSlot(4);
        if (helmet != null && helmet.getItem() instanceof ItemBlock) {
            GL11.glPushMatrix();
            this.snowmanModel.head.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(helmet, EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, helmet, BLOCK_3D);
            if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(helmet.getItem()).getRenderType())) {
                GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(0.625F, -0.625F, 0.625F);
            }
            this.renderManager.itemRenderer.renderItem(entity, helmet, 0);
            GL11.glPopMatrix();
        }
    }
}