package toast.utilityMobs.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.golem.ContainerSteamGolem;
import toast.utilityMobs.golem.EntitySteamGolem;

public class GuiSteamGolem extends GuiContainer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.MODID.toLowerCase(), "textures/gui/guiSteamGolem.png");
    private EntitySteamGolem steamGolem;

    public GuiSteamGolem(InventoryPlayer player, EntitySteamGolem golem) {
        super(new ContainerSteamGolem(player, golem));
        this.steamGolem = golem;
    }

    // Draw the foreground layer for the GuiContainer (everything in front of the items).
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.steamGolem.hasCustomInventoryName() ? this.steamGolem.getInventoryName() : I18n.format(this.steamGolem.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiSteamGolem.TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        if (this.steamGolem.getBurningState()) {
            int fireSize = this.steamGolem.getBurnTimeRemainingScaled(13);
            this.drawTexturedModalRect(x + 80, y + 27 + 12 - fireSize, 176, 12 - fireSize, 14, fireSize + 1);
        }
    }
}
