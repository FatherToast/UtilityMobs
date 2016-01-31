package toast.utilityMobs;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import toast.utilityMobs.block.EntityAnvilGolem;
import toast.utilityMobs.block.EntityChestEnderGolem;
import toast.utilityMobs.block.EntityChestGolem;
import toast.utilityMobs.block.EntityChestTrappedGolem;
import toast.utilityMobs.block.EntityContainerGolem;
import toast.utilityMobs.block.EntityFurnaceGolem;
import toast.utilityMobs.block.EntityJukeboxGolem;
import toast.utilityMobs.block.EntityLanternGolem;
import toast.utilityMobs.block.EntityWorkbenchGolem;
import toast.utilityMobs.colossal.EntityArmorColossus;
import toast.utilityMobs.colossal.EntityObsidianColossus;
import toast.utilityMobs.colossal.EntityStoneColossus;
import toast.utilityMobs.event.BlockEvent;
import toast.utilityMobs.golem.EntityArmorGolem;
import toast.utilityMobs.golem.EntityBoundSoul;
import toast.utilityMobs.golem.EntityGildedGolem;
import toast.utilityMobs.golem.EntityMelonGolem;
import toast.utilityMobs.golem.EntityObsidianGolem;
import toast.utilityMobs.golem.EntityScarecrow;
import toast.utilityMobs.golem.EntitySteamGolem;
import toast.utilityMobs.golem.EntityStoneGolem;
import toast.utilityMobs.golem.EntityStoneLargeGolem;
import toast.utilityMobs.golem.EntityUMIronGolem;
import toast.utilityMobs.golem.EntityUMSnowGolem;
import toast.utilityMobs.golem.EntityUtilityGolem;
import toast.utilityMobs.turret.EntityBrickTurret;
import toast.utilityMobs.turret.EntityFireTurret;
import toast.utilityMobs.turret.EntityFireballTurret;
import toast.utilityMobs.turret.EntityGatlingTurret;
import toast.utilityMobs.turret.EntityGhastTurret;
import toast.utilityMobs.turret.EntityKillerTurret;
import toast.utilityMobs.turret.EntityObsidianTurret;
import toast.utilityMobs.turret.EntityShotgunTurret;
import toast.utilityMobs.turret.EntitySniperTurret;
import toast.utilityMobs.turret.EntitySnowTurret;
import toast.utilityMobs.turret.EntityStoneTurret;
import toast.utilityMobs.turret.EntityVolleyTurret;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BuildHelper
{
    /// A set of usernames for players right-clicking a Blocks.
    public final HashSet<String> clickingBlock = new HashSet<String>();

    public BuildHelper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called by EntityPlayer.
     * EntityPlayer entityPlayer = the player interacting.
     * PlayerInteractEvent.Action action = the action this event represents.
     * int x, y, z = the coords of the clicked-on block (if there is one).
     * int face = the side the block was clicked on (if there is one).
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        boolean targetBook = false;
        if (event.face < 0 || event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) != Blocks.crafting_table) {
            targetBook = BookHelper.checkBook(event.entityPlayer);
        }
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (!event.entityPlayer.worldObj.isRemote) {
                new BlockEvent(event.entityPlayer, event.x, event.y, event.z, event.face);
            }
            else if (targetBook) {
                this.clickingBlock.add(event.entityPlayer.getCommandSenderName());
            }
        }
        else if (targetBook && event.entityPlayer.worldObj.isRemote && !this.clickingBlock.remove(event.entityPlayer.getCommandSenderName())) {
            event.setCanceled(true);
        }
    }

    /**
     * Called by EntityPlayer.
     * EntityPlayer entityPlayer = the player interacting.
     * Entity target = the entity being interacted with.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.target instanceof EntityLivingBase && BookHelper.interact(event.entityPlayer, (EntityLivingBase)event.target)) {
            event.setCanceled(true);
        }
    }

    /// Spawns a golem, if possible.
    public static boolean place(World world, EntityPlayer player, boolean holdingGolemHead, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.pumpkin || block == Blocks.lit_pumpkin)
            return BuildHelper.placeGolem(world, player, x, y, z);
        if (block == Blocks.dispenser)
            return BuildHelper.placeTurret(world, player, x, y, z);
        if (block == Blocks.skull) {
            switch (BuildHelper.getSkullType(world, x, y, z)) {
                case 0:
                    return BuildHelper.placeBlock(world, player, x, y, z);
                case 1:
                    return BuildHelper.placeHostile(world, player, x, y, z);
                case 4:
                    return BuildHelper.placeColossal(world, player, x, y, z);
            }
            return false;
        }
        if (holdingGolemHead)
            return BuildHelper.replaceGolem(world, player, x, y, z);
        return false;
    }

    /// Returns the skull type, since the metadata only returns 1.
    public static int getSkullType(World world, int x, int y, int z) {
        try {
            return ((TileEntitySkull)world.getTileEntity(x, y, z)).func_145904_a(); // getSkullType
        }
        catch (Exception ex) {
            // Do nothing
        }
        return -1;
    }

    /// Spawns a golem, if possible.
    public static boolean placeGolem(World world, EntityPlayer player, int x, int y, int z) {
        if (!Properties.getBoolean("golems", "_all"))
            return false;
        EntityUtilityGolem golem;
        Block top = world.getBlock(x, y - 1, z);
        Block bottom = world.getBlock(x, y - 2, z);
        Block armLX = world.getBlock(x - 1, y - 1, z);
        Block armRX = world.getBlock(x + 1, y - 1, z);
        Block armLZ = world.getBlock(x, y - 1, z - 1);
        Block armRZ = world.getBlock(x, y - 1, z + 1);
        String owner = null;
        if (player != null) {
            owner = player.getCommandSenderName();
        }
        if (top == Blocks.cobblestone && bottom == Blocks.cobblestone) {
            boolean xAxis = armLX == Blocks.cobblestone && armRX == Blocks.cobblestone;
            boolean zAxis = armLZ == Blocks.cobblestone && armRZ == Blocks.cobblestone;
            if (!Properties.getBoolean("golems", "StoneLargeGolem")) {
                xAxis = false;
                zAxis = false;
            }
            if (xAxis || zAxis) {
                golem = new EntityStoneLargeGolem(world);
                golem.setOwner(owner);
                BuildHelper.init(golem, world, x, y, z);
                BuildHelper.removeLarge(world, xAxis, x, y, z);
                BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
                return true;
            }
            else if (!Properties.getBoolean("golems", "StoneGolem"))
                return false;
            golem = new EntityStoneGolem(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowshovel", x, y, z);
            return true;
        }
        else if ((top == Blocks.furnace || top == Blocks.lit_furnace) && bottom == Blocks.cobblestone) {
            if (!Properties.getBoolean("golems", "SteamGolem"))
                return false;
            boolean xAxis = armLX == Blocks.cobblestone && armRX == Blocks.cobblestone;
            boolean zAxis = armLZ == Blocks.cobblestone && armRZ == Blocks.cobblestone;
            if (xAxis || zAxis) {
                golem = new EntitySteamGolem(world);
                golem.setOwner(owner);
                BuildHelper.init(golem, world, x, y, z);
                BuildHelper.removeLarge(world, xAxis, x, y, z);
                BuildHelper.particleEffect(world, "largesmoke", x, y, z);
                return true;
            }
            return false;
        }
        else if (top == Blocks.iron_block && bottom == Blocks.iron_block) {
            if (armLX == Blocks.iron_block && armRX == Blocks.iron_block || armLZ == Blocks.iron_block && armRZ == Blocks.iron_block)
                return false;
            if (!Properties.getBoolean("golems", "ArmorGolem"))
                return false;
            golem = new EntityArmorGolem(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        else if (Properties.getBoolean("golems", "Scarecrow") && top == Blocks.wool && bottom == Blocks.fence) {
            boolean xAxis = armLX == Blocks.fence && armRX == Blocks.fence;
            boolean zAxis = armLZ == Blocks.fence && armRZ == Blocks.fence;
            if (xAxis || zAxis) {
                golem = new EntityScarecrow(world);
                golem.setOwner(owner);
                BuildHelper.init(golem, world, x, y, z);
                BuildHelper.removeLarge(world, xAxis, x, y, z);
                BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
                return true;
            }
            return false;
        }
        else if (top == Blocks.gold_block && bottom == Blocks.gold_block) {
            if (!Properties.getBoolean("golems", "GildedGolem"))
                return false;
            golem = new EntityGildedGolem(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "critical", x, y, z);
            return true;
        }
        else if (top == Blocks.melon_block && bottom == Blocks.melon_block) {
            if (!Properties.getBoolean("golems", "MelonGolem"))
                return false;
            golem = new EntityMelonGolem(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        else if (top == Blocks.soul_sand && bottom == Blocks.soul_sand) {
            if (!Properties.getBoolean("golems", "BoundSoul"))
                return false;
            golem = new EntityBoundSoul(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "magicCrit", x, y, z);
            return true;
        }
        else if (top == Blocks.obsidian && bottom == Blocks.obsidian) {
            if (!Properties.getBoolean("golems", "ObsidianGolem"))
                return false;
            boolean xAxis = armLX == Blocks.obsidian && armRX == Blocks.obsidian;
            boolean zAxis = armLZ == Blocks.obsidian && armRZ == Blocks.obsidian;
            if (xAxis || zAxis) {
                golem = new EntityObsidianGolem(world);
                golem.setOwner(owner);
                BuildHelper.init(golem, world, x, y, z);
                BuildHelper.removeLarge(world, xAxis, x, y, z);
                BuildHelper.particleEffect(world, "magicCrit", x, y, z);
                return true;
            }
            return false;
        }
        return false;
    }

    /// Spawns a turret, if possible.
    public static boolean placeTurret(World world, EntityPlayer player, int x, int y, int z) {
        if (!Properties.getBoolean("turrets", "_all"))
            return false;
        EntityUtilityGolem golem;
        Block top = world.getBlock(x, y - 1, z);
        if (top != Blocks.fence && top != Blocks.nether_brick_fence)
            return false;
        Block bottom = world.getBlock(x, y - 2, z);
        String owner = null;
        if (player != null) {
            owner = player.getCommandSenderName();
        }
        if (bottom == Blocks.cobblestone) {
            if (!Properties.getBoolean("turrets", "StoneTurret"))
                return false;
            golem = new EntityStoneTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.snow) {
            if (!Properties.getBoolean("turrets", "SnowTurret"))
                return false;
            golem = new EntitySnowTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.stonebrick) {
            if (!Properties.getBoolean("turrets", "BrickTurret"))
                return false;
            golem = new EntityBrickTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.gold_block) {
            if (!Properties.getBoolean("turrets", "GatlingTurret"))
                return false;
            golem = new EntityGatlingTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.iron_block) {
            if (!Properties.getBoolean("turrets", "ShotgunTurret"))
                return false;
            golem = new EntityShotgunTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.lapis_block) {
            if (!Properties.getBoolean("turrets", "SniperTurret"))
                return false;
            golem = new EntitySniperTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.redstone_block) {
            if (!Properties.getBoolean("turrets", "FireTurret"))
                return false;
            golem = new EntityFireTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.netherrack) {
            if (!Properties.getBoolean("turrets", "FireballTurret"))
                return false;
            golem = new EntityFireballTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (top == Blocks.nether_brick_fence && bottom == Blocks.nether_brick) {
            if (!Properties.getBoolean("turrets", "GhastTurret"))
                return false;
            golem = new EntityGhastTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.emerald_block) {
            if (!Properties.getBoolean("turrets", "VolleyTurret"))
                return false;
            golem = new EntityVolleyTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (bottom == Blocks.diamond_block) {
            if (!Properties.getBoolean("turrets", "KillerTurret"))
                return false;
            golem = new EntityKillerTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (top == Blocks.nether_brick_fence && bottom == Blocks.obsidian) {
            if (!Properties.getBoolean("turrets", "ObsidianTurret"))
                return false;
            golem = new EntityObsidianTurret(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x, y, z);
            BuildHelper.removeStandard(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        return false;
    }

    /// Spawns a block golem, if possible.
    public static boolean placeBlock(World world, EntityPlayer player, int x, int y, int z) {
        if (!Properties.getBoolean("blocks", "_all"))
            return false;
        EntityUtilityGolem golem;
        Block block = world.getBlock(x, y - 1, z);
        String owner = null;
        if (player != null) {
            owner = player.getCommandSenderName();
        }
        if (block == Blocks.crafting_table) {
            if (!Properties.getBoolean("blocks", "WorkbenchGolem"))
                return false;
            golem = new EntityWorkbenchGolem(world);
            golem.setOwner(owner);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.lit_pumpkin) {
            if (!Properties.getBoolean("blocks", "LanternGolem"))
                return false;
            golem = new EntityLanternGolem(world);
            golem.setOwner(owner);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.chest) {
            if (!Properties.getBoolean("blocks", "ChestGolem"))
                return false;
            golem = new EntityChestGolem(world);
            golem.setOwner(owner);
            BuildHelper.getContents(world, (EntityContainerGolem)golem, x, y - 1, z);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.trapped_chest) {
            if (!Properties.getBoolean("blocks", "ChestTrappedGolem"))
                return false;
            golem = new EntityChestTrappedGolem(world);
            golem.setOwner(owner);
            BuildHelper.getContents(world, (EntityContainerGolem)golem, x, y - 1, z);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.ender_chest) {
            if (!Properties.getBoolean("blocks", "ChestEnderGolem"))
                return false;
            golem = new EntityChestEnderGolem(world);
            golem.setOwner(owner);
            BuildHelper.getContents(world, (EntityContainerGolem)golem, x, y - 1, z);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.furnace || block == Blocks.lit_furnace) {
            if (!Properties.getBoolean("blocks", "FurnaceGolem"))
                return false;
            golem = new EntityFurnaceGolem(world);
            golem.setOwner(owner);
            BuildHelper.getContents(world, (EntityContainerGolem)golem, x, y - 1, z);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.anvil) {
            if (!Properties.getBoolean("blocks", "AnvilGolem"))
                return false;
            int damage = Blocks.anvil.damageDropped(world.getBlockMetadata(x, y - 1, z));
            golem = new EntityAnvilGolem(world);
            golem.setOwner(owner);
            ((EntityAnvilGolem)golem).setDamage(damage);
            BuildHelper.getContents(world, (EntityContainerGolem)golem, x, y - 1, z);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        if (block == Blocks.jukebox) {
            if (!Properties.getBoolean("blocks", "JukeboxGolem"))
                return false;
            golem = new EntityJukeboxGolem(world);
            golem.setOwner(owner);
            golem.sitAI.sit = true;
            BuildHelper.init(golem, world, x, y + 1, z);
            BuildHelper.removeShort(world, x, y, z);
            BuildHelper.particleEffect(world, "snowballpoof", x, y, z);
            return true;
        }
        return false;
    }

    /// Spawns a hostile golem, if possible.
    public static boolean placeHostile(World world, EntityPlayer player, int x, int y, int z) {
        if (!Properties.getBoolean("hostiles", "_all"))
            return false;
        EntityUtilityGolem golem;
        Block top = world.getBlock(x, y - 1, z);
        Block bottom = world.getBlock(x, y - 2, z);
        String owner = null;
        if (player != null) {
            owner = player.getCommandSenderName();
        }
        /// There are no hostile golems yet. (Except the Wither.)
        return false;
    }

    /// Spawns a golem, if possible.
    public static boolean placeColossal(World world, EntityPlayer player, int x, int y, int z) {
        if (!Properties.getBoolean("colossals", "_all"))
            return false;
        EntityUtilityGolem golem;
        int direction;
        int[] xOff = { -1, 1, 0, 0 };
        int[] zOff = { 0, 0, -1, 1 };

        String owner = null;
        if (player != null) {
            owner = player.getCommandSenderName();
        }
        direction = BuildHelper.checkColossal(world, Blocks.cobblestone, x, y, z);
        if (direction >= 0) {
            if (!Properties.getBoolean("colossals", "StoneColossus"))
                return false;
            golem = new EntityStoneColossus(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x + xOff[direction], y - 1, z + zOff[direction]);
            BuildHelper.removeColossal(world, direction, x, y, z);
            BuildHelper.particleEffect(world, "critical", x, y, z);
            return true;
        }
        direction = BuildHelper.checkColossal(world, Blocks.obsidian, x, y, z);
        if (direction >= 0) {
            if (!Properties.getBoolean("colossals", "ObsidianColossus"))
                return false;
            golem = new EntityObsidianColossus(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x + xOff[direction], y - 1, z + zOff[direction]);
            BuildHelper.removeColossal(world, direction, x, y, z);
            BuildHelper.particleEffect(world, "critical", x, y, z);
            return true;
        }
        direction = BuildHelper.checkColossal(world, Blocks.iron_block, x, y, z);
        if (direction >= 0) {
            if (!Properties.getBoolean("colossals", "ArmorColossus"))
                return false;
            golem = new EntityArmorColossus(world);
            golem.setOwner(owner);
            BuildHelper.init(golem, world, x + xOff[direction], y - 1, z + zOff[direction]);
            BuildHelper.removeColossal(world, direction, x, y, z);
            BuildHelper.particleEffect(world, "critical", x, y, z);
            return true;
        }
        return false;
    }

    // Attempts to replace a vanilla golem just placed by the player.
    public static boolean replaceGolem(World world, EntityPlayer player, int x, int y, int z) {
        if (!Properties.getBoolean("golems", "_all"))
            return false;
        String owner = null;
        if (player != null) {
            owner = player.getCommandSenderName();
        }
        for (Object entity : new ArrayList(world.loadedEntityList)) {
            if (entity instanceof EntityGolem) {
                EntityGolem golem = (EntityGolem) entity;
                if (golem.posY == y - 1.95 && golem.posX == x + 0.5 && golem.posZ == z + 0.5) {
                    EntityUtilityGolem newGolem;
                    if (golem instanceof EntityIronGolem && ((EntityIronGolem)golem).isPlayerCreated()) {
                        if (!Properties.getBoolean("golems", "UMIronGolem"))
                            return false;
                        newGolem = new EntityUMIronGolem(world);
                        newGolem.setOwner(owner);
                        BuildHelper.init(newGolem, world, x, y, z);
                        golem.setDead();
                        return true;
                    }
                    if (golem instanceof EntitySnowman) {
                        if (!Properties.getBoolean("golems", "UMSnowGolem"))
                            return false;
                        newGolem = new EntityUMSnowGolem(world);
                        newGolem.setOwner(owner);
                        BuildHelper.init(newGolem, world, x, y, z);
                        golem.setDead();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /// Sets the golem to a standard position based on the head block.
    public static void init(EntityUtilityGolem golem, World world, int x, int y, int z) {
        golem.setLocationAndAngles(x + 0.5, y - 1.95, z + 0.5, 0.0F, 0.0F);
        golem.onSpawnWithEgg((IEntityLivingData)null);
        world.spawnEntityInWorld(golem);
    }

    /// Checks the area around the given creeper head for a colossal golem made of the given block.
    /// Returns -1 if none is found, otherwise 0-3 represents the direction one was found in.
    public static int checkColossal(World world, Block block, int x, int y, int z) {
        boolean failedXn = false;
        boolean failedXp = false;
        boolean failedZn = false;
        boolean failedZp = false;
        for (int i = -2; i <= 2; i++) {
            for (int j = 0; j >= -3; j--) {
                if (j < -1 && (i == -2 || i == 2)) {
                    continue;
                }
                if (j == -3 && i == 0) {
                    continue;
                }
                if (!failedXn && world.getBlock(x - 1, y + j, z + i) != block) {
                    failedXn = true;
                }
                if (!failedXp && world.getBlock(x + 1, y + j, z + i) != block) {
                    failedXp = true;
                }
                if (!failedZn && world.getBlock(x + i, y + j, z - 1) != block) {
                    failedZn = true;
                }
                if (!failedZp && world.getBlock(x + i, y + j, z + 1) != block) {
                    failedZp = true;
                }
            }
        }
        if (!failedXn)
            return 0;
        if (!failedXp)
            return 1;
        if (!failedZn)
            return 2;
        if (!failedZp)
            return 3;
        return -1;
    }

    /// Removes the blocks to make a colossus.
    public static void removeColossal(World world, int direction, int x, int y, int z) {
        BuildHelper.removeBlock(world, x, y, z);
        for (int i = -2; i <= 2; i++) {
            for (int j = 0; j >= -3; j--) {
                if (j < -1 && (i == -2 || i == 2)) {
                    continue;
                }
                if (j == -3 && i == 0) {
                    continue;
                }
                switch (direction) {
                    case 0:
                        BuildHelper.removeBlock(world, x - 1, y + j, z + i);
                        break;
                    case 1:
                        BuildHelper.removeBlock(world, x + 1, y + j, z + i);
                        break;
                    case 2:
                        BuildHelper.removeBlock(world, x + i, y + j, z - 1);
                        break;
                    case 3:
                        BuildHelper.removeBlock(world, x + i, y + j, z + 1);
                }
            }
        }
    }

    /// Removes the block and marks it to be updated.
    public static void removeBlock(World world, int x, int y, int z) {
        world.setBlock(x, y, z, Blocks.air, 0, 2);
    }

    /// Removes the standard two blocks used to make short golems, the coords passed being the head.
    public static void removeShort(World world, int x, int y, int z) {
        BuildHelper.removeBlock(world, x, y, z);
        BuildHelper.removeBlock(world, x, y - 1, z);
    }

    /// Removes the standard three blocks used to make golems, the coords passed being the head.
    public static void removeStandard(World world, int x, int y, int z) {
        BuildHelper.removeBlock(world, x, y, z);
        BuildHelper.removeBlock(world, x, y - 1, z);
        BuildHelper.removeBlock(world, x, y - 2, z);
    }

    /// Removes the standard five blocks used to make large golems, the coords passed being the head.
    public static void removeLarge(World world, boolean xAxis, int x, int y, int z) {
        BuildHelper.removeStandard(world, x, y, z);
        if (xAxis) {
            BuildHelper.removeBlock(world, x - 1, y - 1, z);
            BuildHelper.removeBlock(world, x + 1, y - 1, z);
        }
        else {
            BuildHelper.removeBlock(world, x, y - 1, z - 1);
            BuildHelper.removeBlock(world, x, y - 1, z + 1);
        }
    }

    /// Loads the given tile entity's data to the golem.
    public static void getContents(World world, EntityContainerGolem golem, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tileEntity.writeToNBT(tag);
            golem.takeContentsFromNBT(tag);
            tileEntity.readFromNBT(tag);
        }
    }

    /// Creates the particle effect when a golem is spawned.
    public static void particleEffect(World world, String particle, int x, int y, int z) {
        for (int i = 120; i-- > 0;) {
            world.spawnParticle(particle, x + world.rand.nextDouble(), y - 2 + world.rand.nextDouble() * 2.5, z + world.rand.nextDouble(), 0.0, 0.0, 0.0);
        }
    }
}