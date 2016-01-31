package toast.utilityMobs;

import java.util.ArrayDeque;

import net.minecraft.init.Items;
import toast.utilityMobs.event.UtilityMobsEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickHandler
{
    /// Stack of block events that need to be triggered.
    public static ArrayDeque<UtilityMobsEvent> eventStack = new ArrayDeque<UtilityMobsEvent>();
    /// Counter for target helper cleanup.
    private static int cleanupTicks = 0;

    public TickHandler() {
        FMLCommonHandler.instance().bus().register(this);
    }

    /**
     * Called when a player logs in.
     * EntityPlayer player = the player that just logged in.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        TargetHelper.fetchTargetHelpers(event.player);
    }

    /**
     * Called when an item is crafted.
     * EntityPlayer player = the player that crafted the item.
     * ItemStack crafting = the item that was crafted.
     * IInventory craftMatrix = the crafting inventory.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting == null || event.crafting.getItem() != Items.writable_book && event.crafting.getItem() != Items.written_book || event.crafting.stackTagCompound == null || !event.crafting.stackTagCompound.hasKey("umu"))
            return;
        event.crafting.stackTagCompound.removeTag("umu");
        TargetHelper.read(event.player.getCommandSenderName(), event.crafting);
    }

    /**
     * Called each tick.
     * TickEvent.Type type = the type of tick.
     * Side side = the side this tick is on.
     * TickEvent.Phase phase = the phase of this tick (START, END).
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            _UtilityMobs.proxy.handleClientTick();
        }
    }

    /**
     * Called each tick.
     * TickEvent.Type type = the type of tick.
     * Side side = the side this tick is on.
     * TickEvent.Phase phase = the phase of this tick (START, END).
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (++TickHandler.cleanupTicks > 12000) {
                TargetHelper.destroyAll();
                TickHandler.cleanupTicks = 0;
            }
        }
        else if (event.phase == TickEvent.Phase.END) {
            if (!TickHandler.eventStack.isEmpty()) {
                UtilityMobsEvent modEvent;
                byte limit = 10;
                while (limit-- > 0 && (modEvent = TickHandler.eventStack.pollFirst()) != null) {
                    modEvent.execute();
                }
            }
        }
    }

    /// Puts the event into the stack.
    public static void register(UtilityMobsEvent event) {
        TickHandler.eventStack.add(event);
    }
}