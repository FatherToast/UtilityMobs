package toast.utilityMobs;

import toast.utilityMobs.block.EntityJukeboxGolem;
import cpw.mods.fml.server.FMLServerHandler;

public class CommonProxy
{
    // Returns the username of the player if this is the client side.
    public String getPlayer() {
        return null;
    }

    // Registers render files if this is the client side.
    public void registerRenderers() {
        // Client method
    }

    // Plays a record for the jukebox golem.
    public void playRecordGolem(EntityJukeboxGolem golem, String record) {
        // Client method
    }

    // Called at the end of each client tick.
    public void handleClientTick() {
        // Client method
    }

    // Returns true if entities are allowed to block movement. Namely, if they can be stood on.
    public boolean solidEntities() {
        return !FMLServerHandler.instance().getServer().isDedicatedServer();
    }
}