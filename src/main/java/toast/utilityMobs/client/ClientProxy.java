package toast.utilityMobs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;
import toast.utilityMobs.CommonProxy;
import toast.utilityMobs.EntityGolemFishHook;
import toast.utilityMobs._UtilityMobs;
import toast.utilityMobs.block.EntityAnvilGolem;
import toast.utilityMobs.block.EntityBlockGolem;
import toast.utilityMobs.block.EntityChestGolem;
import toast.utilityMobs.block.EntityJukeboxGolem;
import toast.utilityMobs.client.model.ModelSkeletonGolem;
import toast.utilityMobs.client.renderer.RenderAnvilGolem;
import toast.utilityMobs.client.renderer.RenderBlockGolem;
import toast.utilityMobs.client.renderer.RenderChestGolem;
import toast.utilityMobs.client.renderer.RenderColossalGolem;
import toast.utilityMobs.client.renderer.RenderGolem;
import toast.utilityMobs.client.renderer.RenderGolemFishHook;
import toast.utilityMobs.client.renderer.RenderLargeGolem;
import toast.utilityMobs.client.renderer.RenderStackGolem;
import toast.utilityMobs.client.renderer.RenderTurret;
import toast.utilityMobs.colossal.EntityColossalGolem;
import toast.utilityMobs.golem.EntityLargeGolem;
import toast.utilityMobs.golem.EntityScarecrow;
import toast.utilityMobs.golem.EntityStackGolem;
import toast.utilityMobs.golem.EntityStoneGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;
import toast.utilityMobs.network.MessageUseGolem;
import toast.utilityMobs.turret.EntityTurretGolem;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private int packetCooldown;

    // Returns the username of the player if this is the client side.
    @Override
    public String getPlayer() {
        return FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName();
    }

    // Registers render files if this is the client side.
    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemFishHook.class, new RenderGolemFishHook());

        RenderingRegistry.registerEntityRenderingHandler(EntityUtilityGolem.class, new RenderGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityLargeGolem.class, new RenderLargeGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityStackGolem.class, new RenderStackGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityScarecrow.class, new RenderGolem(new ModelSkeletonGolem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityStoneGolem.class, new RenderGolem(new ModelZombie(0.0F, true)));

        RenderingRegistry.registerEntityRenderingHandler(EntityTurretGolem.class, new RenderTurret());

        RenderingRegistry.registerEntityRenderingHandler(EntityBlockGolem.class, new RenderBlockGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityAnvilGolem.class, new RenderAnvilGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityChestGolem.class, new RenderChestGolem());

        RenderingRegistry.registerEntityRenderingHandler(EntityColossalGolem.class, new RenderColossalGolem());
    }

    // Plays a record for the jukebox golem.
    @Override
    public void playRecordGolem(EntityJukeboxGolem golem, String record) {
        if ("".equals(record))
            return;
        try {
            Minecraft client = FMLClientHandler.instance().getClient();
            String recordId = "records." + record;

            ItemRecord itemRecord = ItemRecord.getRecord(recordId);
            ResourceLocation resource = null;
            if (itemRecord != null) {
                client.ingameGUI.setRecordPlayingMessage(itemRecord.getRecordNameLocal());
                resource = itemRecord.getRecordResource(recordId);
            }

            if (resource == null) {
                resource = new ResourceLocation(recordId);
            }
            MovingSoundRecord recordSound = new MovingSoundRecord(golem, record, PositionedSoundRecord.func_147675_a(resource, (float)golem.posX, (float)golem.posY, (float)golem.posZ));
            client.getSoundHandler().playSound(recordSound);
        }
        catch (Exception ex) {
            _UtilityMobs.console("[WARNING] Unable to play record \"", record, "\" in record golem!");
            ex.printStackTrace();
        }
    }

    // Called at the end of each client tick.
    @Override
    public void handleClientTick() {
        if (this.packetCooldown > 0) {
            this.packetCooldown--;
        }
        else {
            EntityClientPlayerMP player = FMLClientHandler.instance().getClientPlayerEntity();
            if (player != null && player.movementInput != null && player.movementInput.jump && player.ridingEntity instanceof EntityColossalGolem && ((EntityColossalGolem) player.ridingEntity).getAnimId() == 0) {
                this.packetCooldown = 20;
                _UtilityMobs.CHANNEL.sendToServer(new MessageUseGolem());
            }
        }
    }

    // Returns true if entities are allowed to block movement. Namely, if they can be stood on.
    @Override
    public boolean solidEntities() {
        return FMLClientHandler.instance().getServer() != null;
    }
}