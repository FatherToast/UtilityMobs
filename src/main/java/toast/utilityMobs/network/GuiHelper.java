package toast.utilityMobs.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.block.ContainerAnvilGolem;
import toast.utilityMobs.block.ContainerFurnaceGolem;
import toast.utilityMobs.block.ContainerLanternGolem;
import toast.utilityMobs.block.ContainerWorkbenchGolem;
import toast.utilityMobs.block.EntityAnvilGolem;
import toast.utilityMobs.block.EntityContainerGolem;
import toast.utilityMobs.block.EntityFurnaceGolem;
import toast.utilityMobs.block.EntityLanternGolem;
import toast.utilityMobs.client.GuiGenericInventory;
import toast.utilityMobs.client.GuiSteamGolem;
import toast.utilityMobs.golem.ContainerSteamGolem;
import toast.utilityMobs.golem.EntitySteamGolem;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHelper implements IGuiHandler
{
    /*
     * @see cpw.mods.fml.common.network.IGuiHandler#getServerGuiElement(int, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int)
     */
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Entity entity = world.getEntityByID(ID);
        if (entity instanceof EntitySteamGolem)
            return new ContainerSteamGolem(player.inventory, (EntitySteamGolem) entity);
        if (entity instanceof EntityLanternGolem)
            return new ContainerLanternGolem(player.inventory, (EntityLanternGolem) entity);
        return null;
    }

    /*
     * @see cpw.mods.fml.common.network.IGuiHandler#getClientGuiElement(int, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int)
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Entity entity = world.getEntityByID(ID);
        if (entity instanceof EntitySteamGolem)
            return new GuiSteamGolem(player.inventory, (EntitySteamGolem) entity);
        if (entity instanceof EntityLanternGolem)
            return new GuiGenericInventory(new ContainerLanternGolem(player.inventory, (EntityLanternGolem) entity), (IInventory) entity, GuiGenericInventory.TEXTURE_DISPENSER);
        return null;
    }

    /// Opens an anvil GUI using a container golem instead of a block position.
    public static void displayGUIAnvil(EntityPlayer player, EntityAnvilGolem golem) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).getNextWindowId();
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(((EntityPlayerMP)player).currentWindowId, 8, "Repairing", 9, true));
            player.openContainer = new ContainerAnvilGolem(player.inventory, golem, player);
            player.openContainer.windowId = ((EntityPlayerMP)player).currentWindowId;
            player.openContainer.addCraftingToCrafters((EntityPlayerMP)player);
        }
    }

    /// Opens a furnace GUI using a furnace golem instead of a furnace tile entity.
    public static void displayGUIFurnace(EntityPlayer player, EntityFurnaceGolem golem) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).getNextWindowId();
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(((EntityPlayerMP)player).currentWindowId, 2, golem.getInventoryName(), golem.getSizeInventory(), golem.hasCustomInventoryName()));
            player.openContainer = new ContainerFurnaceGolem(player.inventory, golem);
            player.openContainer.windowId = ((EntityPlayerMP)player).currentWindowId;
            player.openContainer.addCraftingToCrafters((EntityPlayerMP)player);
        }
    }

    /// Opens a workbench GUI using a container golem instead of a block position.
    public static void displayGUIWorkbench(EntityPlayer player, EntityContainerGolem golem) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).getNextWindowId();
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(((EntityPlayerMP)player).currentWindowId, 1, "Crafting", 9, true));
            player.openContainer = new ContainerWorkbenchGolem(player.inventory, golem);
            player.openContainer.windowId = ((EntityPlayerMP)player).currentWindowId;
            player.openContainer.addCraftingToCrafters((EntityPlayerMP)player);
        }
    }

    // Opens a custom GUI based on the entity passed.
    public static void displayGUICustom(EntityPlayer player, Entity golem) {
        if (player instanceof EntityPlayerMP) {
            player.openGui(_UtilityMobs.MODID, golem.getEntityId(), player.worldObj, 0, 0, 0);
        }
    }
}