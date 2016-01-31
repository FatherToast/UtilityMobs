package toast.utilityMobs;

import java.io.File;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import toast.utilityMobs.golem.EntityUtilityGolem;
import toast.utilityMobs.network.GuiHelper;
import toast.utilityMobs.network.MessageExplosion;
import toast.utilityMobs.network.MessageFetchTargetHelper;
import toast.utilityMobs.network.MessageTargetHelper;
import toast.utilityMobs.network.MessageUseGolem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = _UtilityMobs.MODID, name = "Utility Mobs", version = _UtilityMobs.VERSION)
public class _UtilityMobs
{
    /* TO DO *\
    >> currentTasks
     * Make targeting books their own items, along with manuals (possibly).
    >> tasks
     * New golems with new AI patterns.
     ? Allow option for per-world target helper.
    >> goals
     * Improve AI and targeting.
    \* ** ** */

    // This mod's id.
    public static final String MODID = "UtilityMobs";
    // This mod's version.
    public static final String VERSION = "3.1.1";

    // If true, this mod starts up in debug mode.
    public static final boolean debug = false;
    // The common proxy for this mod.
    @SidedProxy(clientSide = "toast.utilityMobs.client.ClientProxy", serverSide = "toast.utilityMobs.CommonProxy")
    public static CommonProxy proxy;
    // The mod's random number generator.
    public static final Random random = new Random();
    // The network channel for this mod.
    public static SimpleNetworkWrapper CHANNEL;

    // The mod's random number generator.
    public static final String TEXTURE = _UtilityMobs.MODID + ":textures/models/";

    // Utility mob type array. Based on the block used to create them.
    public static final String[] UTILITY_TYPES = {
        "Block", "Golem", "Hostile", "Turret", "Colossal"
    };
    // Utility mob sub-type array. First dimension is the UTILITY_TYPES[].
    public static final String[][] UTILITY_NAMES = {
        {/* skeleton skull */ "AnvilGolem", "ChestEnderGolem", "ChestGolem", "ChestTrappedGolem", "FurnaceGolem", "JukeboxGolem", "LanternGolem", "WorkbenchGolem" },
        {/* pumpkin */ "ArmorGolem", "BoundSoul", "GildedGolem", "MelonGolem", "ObsidianGolem", "Scarecrow", "SteamGolem", "StoneGolem", "StoneLargeGolem", "UMIronGolem", "UMSnowGolem" },
        {/* wither skull */ },
        {/* dispenser */ "BrickTurret", "FireballTurret", "FireTurret", "GatlingTurret", "GhastTurret", "KillerTurret", "ObsidianTurret", "ShotgunTurret", "SniperTurret", "SnowTurret", "StoneTurret", "VolleyTurret" },
        {/* creeper head */ "ArmorColossus", "ObsidianColossus", "StoneColossus" }
    };
    static {
        int length = 0;
        for (int i = 0; i < _UtilityMobs.UTILITY_NAMES.length; i++) {
            for (int j = 0; j < _UtilityMobs.UTILITY_NAMES[i].length; j++) {
                length++;
            }
        }
    }

    // Registers the entities in this mod.
    private void registerMobs() {
        int id = 0;
        String path;
        EntityRegistry.registerModEntity(EntityUtilityGolem.class, "GenericGolem", id++, this, 80, 3, true);
        for (int i = 0; i < _UtilityMobs.UTILITY_NAMES.length; i++) {
            path = "toast.utilityMobs." + _UtilityMobs.decap(_UtilityMobs.UTILITY_TYPES[i]) + ".Entity";
            for (int j = 0; j < _UtilityMobs.UTILITY_NAMES[i].length; j++) {
                try {
                    EntityRegistry.registerModEntity((Class)Class.forName(path + _UtilityMobs.UTILITY_NAMES[i][j]), _UtilityMobs.UTILITY_NAMES[i][j], id++, this, 80, 3, true);
                }
                catch (ClassNotFoundException ex) {
                    _UtilityMobs.debugException("@Entity" + _UtilityMobs.UTILITY_NAMES[i][j] + ": class not found!");
                }
            }
        }

        EntityRegistry.registerModEntity(EntityGolemFishHook.class, "UMFishHook", id++, this, 64, 5, true);
    }

    // Registers the recipes for this mod.
    @SuppressWarnings("boxing")
    private static void addRecipes() {
        Item book = Properties.getBoolean(Properties.GENERAL, "alternate_manuals") ? Items.writable_book : Items.book;
        ItemStack[] items = { new ItemStack(Items.skull, 1, 0), new ItemStack(Blocks.pumpkin), new ItemStack(Items.skull, 1, 1), new ItemStack(Blocks.dispenser, 1, 0), new ItemStack(Items.skull, 1, 4) };
        for (int i = items.length; i-- > 0;) {
            GameRegistry.addShapelessRecipe(ManualHelper.manual(i), new Object[] {
                book, items[i]
            });
        }
        GameRegistry.addShapelessRecipe(ManualHelper.upgradeManual(), new Object[] {
            book, Items.ender_pearl
        });
        GameRegistry.addShapelessRecipe(TargetHelper.book(0), new Object[] {
            book, Items.bone
        });
        GameRegistry.addShapelessRecipe(TargetHelper.book(1), new Object[] {
            book, Items.rotten_flesh
        });
        GameRegistry.addRecipe(new RecipeSavePermissions());
        if (Properties.getBoolean(Properties.GENERAL, "wither_conversion")) {
            GameRegistry.addRecipe(new ItemStack(Items.skull), new Object[] {
                "&&&", "&@&", "&&&", '@', new ItemStack(Items.skull, 1, 1), '&', Items.speckled_melon
            });
        }
    }

    // Called before initialization. Loads the properties/configurations.
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        _UtilityMobs.debugConsole("Loading in debug mode!");
        Properties.init(new Configuration(event.getSuggestedConfigurationFile()));
        TargetHelper.SAVE_DIRECTORY = new File(event.getModConfigurationDirectory(), "UtilityMobs");

        _UtilityMobs.CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("UM|Info");
        int id = 0;
        _UtilityMobs.CHANNEL.registerMessage(MessageUseGolem.Handler.class, MessageUseGolem.class, id++, Side.SERVER);
        _UtilityMobs.CHANNEL.registerMessage(MessageTargetHelper.Handler.class, MessageTargetHelper.class, id, Side.SERVER);
        if (event.getSide() == Side.CLIENT) {
            _UtilityMobs.CHANNEL.registerMessage(MessageTargetHelper.Handler.class, MessageTargetHelper.class, id++, Side.CLIENT);
            _UtilityMobs.CHANNEL.registerMessage(MessageFetchTargetHelper.Handler.class, MessageFetchTargetHelper.class, id++, Side.CLIENT);
            _UtilityMobs.CHANNEL.registerMessage(MessageExplosion.Handler.class, MessageExplosion.class, id++, Side.CLIENT);
        }
    }

    // Called during initialization. Registers entities, mob spawns, and renderers.
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.registerMobs();
        _UtilityMobs.addRecipes();
        _UtilityMobs.proxy.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHelper());
        new BuildHelper();
        new TickHandler();
        new EventHandler();
    }

    // Inserts a space before every capital letter (except the first).
    public static String parseName(String name) {
        if (name.length() > 1) {
            for (int i = 1; i < name.length(); i++) {
                if (Character.isUpperCase(name.charAt(i)))
                    return name.substring(0, i) + " " + _UtilityMobs.parseName(name.substring(i));
            }
        }
        return name;
    }

    // Capitalizes or decapitalizes the given string.
    public static String cap(String string) {
        if (string.length() > 0)
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        return string;
    }
    public static String decap(String string) {
        if (string.length() > 0)
            return string.substring(0, 1).toLowerCase() + string.substring(1);
        return string;
    }

    // Prints the message to the console with this mod's name tag.
    public static void console(String... messages) {
        String message = "[" + _UtilityMobs.MODID + "] [" + FMLCommonHandler.instance().getSide().name() + "] ";
        for (String part : messages) {
            message += part;
        }
        System.out.println(message);
    }

    // Prints the message to the console with this mod's name tag if debugging is enabled.
    public static void debugConsole(String... messages) {
        if (_UtilityMobs.debug) {
            String message = "[" + _UtilityMobs.MODID + "] [" + FMLCommonHandler.instance().getSide().name() + "] ";
            for (String part : messages) {
                message += part;
            }
            System.out.println(message);
        }
    }

    // Throws a runtime exception with a message and this mod's name tag if debugging is enabled.
    public static void debugException(String... messages) {
        if (_UtilityMobs.debug) {
            String message = "[" + _UtilityMobs.MODID + "] [" + FMLCommonHandler.instance().getSide().name() + "] ";
            for (String part : messages) {
                message += part;
            }
            throw new RuntimeException(message);
        }
        if (messages.length > 0) {
            messages[0] = "[ERROR] " + messages[0];
        }
        _UtilityMobs.console(messages);
    }
}