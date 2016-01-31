package toast.utilityMobs;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import toast.utilityMobs.golem.EntityUtilityGolem;
import toast.utilityMobs.network.MessageFetchTargetHelper;
import toast.utilityMobs.network.MessageTargetHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;

public class TargetHelper
{
    // All currently loaded target helpers.
    private static final HashMap<String, TargetHelper> TARGET_HELPERS = new HashMap<String, TargetHelper>();
    // The location of the target helper saves.
    public static File SAVE_DIRECTORY;
    // Permission values.
    public static final byte PERMISSION_TARGET = (byte)(1 << 0);
    public static final byte PERMISSION_USE = (byte)(1 << 1);
    public static final byte PERMISSION_OPEN = (byte)(1 << 2);
    // The highest permission value.
    public static final byte HIGHEST_PERMISSION = TargetHelper.PERMISSION_OPEN;
    // If true, mobs attack all players.
    public static final boolean HOSTILE = Properties.getBoolean(Properties.GENERAL, "hostile");

    // The owner of the golems using this target helper.
    public String owner;
    // If this is set to true, then this instance will be destroyed.
    private boolean destroy;
    // Player permissions for this target helper.
    private HashMap<String, Byte> permissions = new HashMap<String, Byte>();
    // Mob class blacklist for this target helper.
    private HashSet<Class> mobBlacklist = new HashSet<Class>();
    // Mob class whitelist for this target helper.
    private ArrayList<Class> mobWhitelist = new ArrayList<Class>();

    private TargetHelper(String username) {
        this.owner = username;
        TargetHelper.TARGET_HELPERS.put(this.owner, this);
        if (this.owner != null) {
            if (this.hasSave()) {
                this.load();
            }
            else {
                this.setPermissions(this.owner, 7);
                this.whitelist(EntityPlayer.class);
                this.whitelist(EntityUtilityGolem.class);
                this.whitelist(EntitySilverfish.class);
                this.whitelist(EntitySkeleton.class);
                this.whitelist(EntitySlime.class);
                this.whitelist(EntitySpider.class);
                this.whitelist(EntityWitch.class);
                this.whitelist(EntityZombie.class);
            }
        }
    }

    // Gets the target helper for the owner and loads it, if needed.
    public static TargetHelper getTargetHelper(String owner) {
        if (owner == "") {
            owner = null;
        }
        TargetHelper targetHelper = TargetHelper.TARGET_HELPERS.get(owner);
        if (targetHelper == null) {
            targetHelper = new TargetHelper(owner);
        }
        return targetHelper;
    }

    // Returns true if the entity has an owner tag.
    public static boolean hasOwner(Entity entity) {
        return entity.getEntityData().hasKey("UM|owner");
    }

    // Gets the target helper for the owner of the entity and loads it, if needed.
    public static TargetHelper getOwnerTargetHelper(Entity entity) {
        return TargetHelper.getTargetHelper(entity.getEntityData().getString("UM|owner"));
    }

    // Attempts to find the player on the server. Returns null if the player cannot be found.
    public EntityPlayer getOwner() {
        EntityPlayer player;
        for (World world : FMLCommonHandler.instance().getMinecraftServerInstance().worldServers) {
            player = world.getPlayerEntityByName(this.owner);
            if (player != null)
                return player;
        }
        return null;
    }

    // Sets the owner of a particular entity, usually an arrow or snowball.
    public void setOwned(Entity entity) {
        if (this.owner != null) {
            entity.getEntityData().setString("UM|owner", this.owner);
        }
    }

    // Returns true if the player can be damaged.
    public boolean canDamagePlayer(String username) {
        if (TargetHelper.HOSTILE)
            return true;
        else if (!this.permissions.containsKey(username))
            return !this.isBlacklisted(EntityPlayer.class) && this.isWhitelisted(EntityPlayer.class);
        else
            return (this.permissions.get(username).byteValue() & TargetHelper.PERMISSION_TARGET) == 0;
    }

    // Returns the player's permissions.
    public byte getPermissions(String username) {
        if (this.owner == null || !this.permissions.containsKey(username))
            return (byte)0;
        return this.permissions.get(username).byteValue();
    }

    // Returns true if the player has the given permissions (at least).
    public boolean playerHasPermission(String username, int value) {
        return this.owner == null || (this.getPermissions(username) & value) == value;
    }

    // Returns true if the given entity should continue to be attacked.
    public boolean maintainTarget(Entity entity) {
        if (!(entity instanceof EntityLivingBase) || !entity.isEntityAlive())
            return false;
        return true;
    }

    // Returns true if the given entity should be attacked.
    public boolean isValidTarget(Entity entity) {
        if (!this.maintainTarget(entity))
            return false;
        if (this.owner == null)
            return entity instanceof EntityUtilityGolem ? ((IEntityOwnable)entity).getOwner() != null : true;
        if (entity instanceof IEntityOwnable)
            if (this.owner.equals(((IEntityOwnable)entity).func_152113_b()) || !this.canDamagePlayer(((IEntityOwnable)entity).func_152113_b()))
                return false;
        if (entity instanceof EntityPlayer)
            return this.canDamagePlayer(((EntityPlayer)entity).getCommandSenderName());
        Class entityClass = entity.getClass();
        if (!this.isBlacklisted(entityClass) && this.isWhitelisted(entityClass))
            return true;
        return false;
    }

    // Marks this target helper for deletion. Called every now-and-then on every target helper. They will reload themselves if still being used.
    public static void destroyAll() {
        for (Map.Entry<String, TargetHelper> entry : TargetHelper.TARGET_HELPERS.entrySet()) {
            entry.getValue().softDestroy();
        }
        TargetHelper.TARGET_HELPERS.clear();
    }
    private void softDestroy() {
        this.destroy = true;
    }
    public void destroy() {
        this.destroy = true;
        TargetHelper.TARGET_HELPERS.remove(this);
    }
    public boolean destroyed() {
        return this.destroy;
    }

    // Functions for setting and removing player permissions.
    public void setPermissions(String username, int value) {
        if (value > 0) {
            this.permissions.put(username, Byte.valueOf((byte)value));
        }
        else {
            this.permissions.remove(username);
        }
    }
    public void addPermissions(String username, int value) {
        if (this.permissions.containsKey(username)) {
            byte prev = this.permissions.get(username).byteValue();
            value |= prev;
        }
        this.permissions.put(username, Byte.valueOf((byte)value));
    }
    public void remPermissions(String username, int value) {
        if (this.permissions.containsKey(username)) {
            byte prev = this.permissions.get(username).byteValue();
            prev &= ~value;
            if (prev > 0) {
                this.permissions.put(username, Byte.valueOf(prev));
            }
            else {
                this.permissions.remove(username);
            }
        }
    }

    // Functions for blacklisting/whitelisting mobs.
    public void blacklist(Class entityClass) {
        if (!this.isBlacklisted(entityClass) && this.isWhitelisted(entityClass)) {
            this.mobBlacklist.add(entityClass);
        }
    }
    public void unblacklist(Class entityClass) {
        this.mobBlacklist.remove(entityClass);
    }
    public void toggleBlacklist(Class entityClass) {
        if (this.isBlacklisted(entityClass)) {
            this.unblacklist(entityClass);
        }
        else {
            this.blacklist(entityClass);
        }
    }
    public void whitelist(Class entityClass) {
        if (!this.mobWhitelist.contains(entityClass) && EntityLivingBase.class.isAssignableFrom(entityClass) && this.clearWhitelistFor(entityClass)) {
            this.mobWhitelist.add(entityClass);
        }
    }
    public void unwhitelist(Class entityClass) {
        this.clearBlacklistFor(entityClass);
        this.mobWhitelist.remove(entityClass);
    }
    public void toggleWhitelist(Class entityClass) {
        if (this.mobWhitelist.contains(entityClass)) {
            this.unwhitelist(entityClass);
        }
        else {
            this.whitelist(entityClass);
        }
    }

    // Returns true if the entity class is whitelisted or extends a whitelisted class.
    public boolean isWhitelisted(Class entityClass) {
        for (Class allowedClass : this.mobWhitelist) if (allowedClass.isAssignableFrom(entityClass))
            return true;
        return false;
    }

    // Returns true if the entity class is whitelisted or extends a whitelisted class.
    public boolean isBlacklisted(Class entityClass) {
        return this.mobBlacklist.contains(entityClass);
    }

    // Ensures that there is no over-definition in the whitelist when a whitelist entry is added.
    private boolean clearWhitelistFor(Class entityClass) {
        Class allowedClass;
        for (Iterator<Class> iterator = this.mobWhitelist.iterator(); iterator.hasNext() && (allowedClass = iterator.next()) != null;) {
            if (allowedClass.isAssignableFrom(entityClass))
                return false;
            if (entityClass.isAssignableFrom(allowedClass)) {
                iterator.remove();
            }
        }
        return true;
    }

    // Ensures that there are no unneeded blacklist entries when a whitelist entry is removed.
    private void clearBlacklistFor(Class entityClass) {
        Class disallowedClass;
        for (Iterator<Class> iterator = this.mobBlacklist.iterator(); iterator.hasNext() && (disallowedClass = iterator.next()) != null;) if (entityClass.isAssignableFrom(disallowedClass)) {
            iterator.remove();
        }
    }

    // Returns true if this target helper has a save.
    public boolean hasSave() {
        if (this.owner == null)
            return false;
        try {
            return new File(TargetHelper.SAVE_DIRECTORY, this.owner + ".txt").exists();
        }
        catch (Exception ex) {
            _UtilityMobs.console("Failed to fetch target save data (" + this.owner + ".txt)!");
            ex.printStackTrace();
        }
        return false;
    }

    // Saves this target helper to the config.
    public void save() {
        if (this.owner == null || this.destroyed())
            return;

        this.updateTargetHelper();

        try {
            File saveTmp = new File(TargetHelper.SAVE_DIRECTORY, this.owner + ".txt.tmp");
            File save = new File(TargetHelper.SAVE_DIRECTORY, this.owner + ".txt");
            TargetHelper.SAVE_DIRECTORY.mkdirs();
            saveTmp.createNewFile();
            FileWriter out = new FileWriter(saveTmp);
            out.write("player_permissions");
            for (Map.Entry<String, Byte> entry : this.permissions.entrySet()) {
                out.write("\n" + Integer.toBinaryString(entry.getValue().intValue()) + " " + entry.getKey());
            }
            out.write("\n\nwhitelist");
            for (Class entityClass : this.mobWhitelist) {
                out.write("\n" + TargetHelper.classToString(entityClass));
            }
            out.write("\n\nblacklist");
            for (Class entityClass : this.mobBlacklist) {
                out.write("\n" + TargetHelper.classToString(entityClass));
            }
            out.close();
            save.delete();
            saveTmp.renameTo(save);
        }
        catch (Exception ex) {
            _UtilityMobs.console("Failed to save target data (" + this.owner + ".txt)!");
            ex.printStackTrace();
        }
    }

    // Saves this target helper to the byte buffer to send to the client.
    public void save(ByteBuf buf) {
        if (this.owner == null || this.destroyed()) {
            ByteBufUtils.writeUTF8String(buf, "");
            ByteBufUtils.writeUTF8String(buf, "");
            ByteBufUtils.writeUTF8String(buf, "");
            return;
        }
        String list;

        list = "";
        for (Map.Entry<String, Byte> entry : this.permissions.entrySet()) {
            list += "\n" + Integer.toBinaryString(entry.getValue().intValue()) + " " + entry.getKey();
        }
        ByteBufUtils.writeUTF8String(buf, list);

        list = "";
        for (Class entityClass : this.mobWhitelist) {
            list += "\n" + TargetHelper.classToString(entityClass);
        }
        ByteBufUtils.writeUTF8String(buf, list);

        list = "";
        for (Class entityClass : this.mobBlacklist) {
            list += "\n" + TargetHelper.classToString(entityClass);
        }
        ByteBufUtils.writeUTF8String(buf, list);
    }

    // Loads this target helper from the config.
    public void load() {
        if (this.owner == null || this.destroyed())
            return;
        try {
            this.permissions.clear();
            this.mobBlacklist.clear();
            this.mobWhitelist.clear();
            File save = new File(TargetHelper.SAVE_DIRECTORY, this.owner + ".txt");
            if (!save.exists())
                return;
            FileInputStream in = new FileInputStream(save);
            int dat;
            byte status = 0;
            String key = "";
            String value = "";
            while ((dat = in.read()) >= 0) {
                if (dat == 13) {
                    continue;
                }
                if (dat == 10) {
                    if (status == 0) {
                        if (key == "") {
                            continue;
                        }
                        if (!key.equalsIgnoreCase("player_permissions") && !key.equalsIgnoreCase("whitelist") && !key.equalsIgnoreCase("blacklist")) {
                            _UtilityMobs.console("Unrecognized value in player config: " + key + " (" + this.owner + ".txt)!");
                            key = "";
                            continue;
                        }
                        status = 1;
                    }
                    else if (status == 1) {
                        if (value == "") {
                            key = "";
                            status = 0;
                            continue;
                        }
                        if (key.equalsIgnoreCase("player_permissions")) {
                            for (int i = 0; i < value.length(); i++) {
                                if (value.charAt(i) == ' ') {
                                    try {
                                        this.setPermissions(value.substring(i + 1), Integer.parseInt(value.substring(0, i), 2));
                                    }
                                    catch (Exception ex) {
                                        _UtilityMobs.console("Invalid player permissions entry: " + value + " (" + this.owner + ".txt)!");
                                    }
                                }
                            }
                        }
                        else if (key.equalsIgnoreCase("whitelist")) {
                            Class entry = TargetHelper.stringToClass(value);
                            if (entry == null) {
                                _UtilityMobs.console("Invalid whitelist entry: " + value + " (" + this.owner + ".txt)!");
                            }
                            else {
                                this.whitelist(entry);
                            }
                        }
                        else if (key.equalsIgnoreCase("blacklist")) {
                            Class entry = TargetHelper.stringToClass(value);
                            if (entry == null) {
                                _UtilityMobs.console("Invalid blacklist entry: " + value + " (" + this.owner + ".txt)!");
                            }
                            else {
                                this.blacklist(entry);
                            }
                        }
                        value = "";
                    }
                    continue;
                }
                if (status == 0) {
                    key += Character.toString((char)dat);
                }
                else if (status == 1) {
                    value += Character.toString((char)dat);
                }
            }
            in.close();
        }
        catch (Exception ex) {
            _UtilityMobs.console("Failed to load target data (" + this.owner + ".txt)!");
            ex.printStackTrace();
        }
    }

    // Loads this target helper from the byte buffer.
    public void load(ByteBuf buf) {
        if (this.owner == null || this.destroyed())
            return;
        try {
            this.permissions.clear();
            this.mobBlacklist.clear();
            this.mobWhitelist.clear();
            String list = "";
            list += "player_permissions" + ByteBufUtils.readUTF8String(buf);
            list += "\n\nwhitelist" + ByteBufUtils.readUTF8String(buf);
            list += "\n\nblacklist" + ByteBufUtils.readUTF8String(buf);

            byte status = 0;
            String key = "";
            String value = "";
            for (int dat : list.toCharArray()) {
                if (dat == 13) {
                    continue;
                }
                if (dat == 10) {
                    if (status == 0) {
                        if (key == "") {
                            continue;
                        }
                        if (!key.equalsIgnoreCase("player_permissions") && !key.equalsIgnoreCase("whitelist") && !key.equalsIgnoreCase("blacklist")) {
                            _UtilityMobs.console("Unrecognized value in player config: " + key + " (" + this.owner + " packet)!");
                            key = "";
                            continue;
                        }
                        status = 1;
                    }
                    else if (status == 1) {
                        if (value == "") {
                            key = "";
                            status = 0;
                            continue;
                        }
                        if (key.equalsIgnoreCase("player_permissions")) {
                            for (int i = 0; i < value.length(); i++) {
                                if (value.charAt(i) == ' ') {
                                    try {
                                        this.setPermissions(value.substring(i + 1), Integer.parseInt(value.substring(0, i), 2));
                                    }
                                    catch (Exception ex) {
                                        _UtilityMobs.console("Invalid player permissions entry: " + value + " (" + this.owner + " packet)!");
                                    }
                                }
                            }
                        }
                        else if (key.equalsIgnoreCase("whitelist")) {
                            Class entry = TargetHelper.stringToClass(value);
                            if (entry == null) {
                                _UtilityMobs.console("Invalid whitelist entry: " + value + " (" + this.owner + " packet)!");
                            }
                            else {
                                this.whitelist(entry);
                            }
                        }
                        else if (key.equalsIgnoreCase("blacklist")) {
                            Class entry = TargetHelper.stringToClass(value);
                            if (entry == null) {
                                _UtilityMobs.console("Invalid blacklist entry: " + value + " (" + this.owner + " packet)!");
                            }
                            else {
                                this.blacklist(entry);
                            }
                        }
                        value = "";
                    }
                    continue;
                }
                if (status == 0) {
                    key += Character.toString((char)dat);
                }
                else if (status == 1) {
                    value += Character.toString((char)dat);
                }
            }
            this.save();
        }
        catch (Exception ex) {
            _UtilityMobs.console("Failed to load target data (" + this.owner + " packet)!");
            ex.printStackTrace();
        }
    }

    // Returns an empty target helper book.
    public static ItemStack book(int id) {
        ItemStack book = new ItemStack(Items.writable_book);
        BookHelper.addPages(book, "").stackTagCompound.setByte("umt", (byte)id);
        if (id == 0) {
            EffectHelper.setItemName(book, 0xb, "Player Permissions");
        }
        else if (id == 1) {
            EffectHelper.setItemName(book, 0xb, "Mob Target List");
        }
        EffectHelper.setItemGlowing(book);
        return book;
    }

    // Writes a target helper's specs to a book.
    public static ItemStack write(String username, int id) {
        return TargetHelper.write(username, new ItemStack(id == 0 ? Items.writable_book : Items.written_book), id);
    }
    public static ItemStack write(String username, ItemStack book, int id) {
        return TargetHelper.getTargetHelper(username).writeTo(book, id);
    }
    private ItemStack writeTo(ItemStack book, int id) {
        if (book == null)
            return null;
        BookHelper.removePages(book);
        if (id == 0) {
            book.func_150996_a(Items.writable_book); // setItem
            EffectHelper.setItemName(book, 0xb, "Player Permissions");
            EffectHelper.setItemText(book, 0x7, "by " + this.owner);
            EffectHelper.setItemGlowing(book);
            BookHelper.addPages(book,
                    " \u00a77Player Permissions\u00a70\n\nTo change a player's permissions, simply add a new line with the permissions you want that player to have - any previous lines will be overwritten when the book is saved.\nTo save any changes, simply craft this book by itself.",
                    " \u00a77Player Permissions\u00a70\n\nIf you do not save your changes, they will be erased the next time this book is right clicked!\n\nYou may also modify permissions by right clicking a player with this book, much like the Mob Target List.",
                    " \u00a77Player Permissions\u00a70\n\nRight clicking a player will grant that player the lowest permission he or she does not have.\n\nIf you are sneaking, instead it will remove the highest permission that player has!",
                    " \u00a77Quick Permission Guide\n\nid:                    name\u00a70\n\n000:                 none\n\n001:               target\n\n010:                   use\n\n100:                 open\n"
                    );
            if (this.permissions.size() <= 0) {
                BookHelper.addPages(book, " \u00a7lPermissions:\u00a7r\n<no permissions>");
            }
            else {
                String[] pages = new String[this.permissions.size()];
                int page = 0;
                byte line = 1;
                pages[0] = " \u00a7lPermissions:\u00a7r\n";
                for (Map.Entry<String, Byte> entry : this.permissions.entrySet()) {
                    if (line++ == 10) {
                        line = 0;
                        pages[++page] = "";
                    }
                    pages[page] += Integer.toBinaryString(entry.getValue().intValue()) + " " + entry.getKey() + "\n";
                }
                BookHelper.addPages(book, pages);
            }
        }
        else if (id == 1) {
            book.func_150996_a(Items.writable_book); // setItem
            EffectHelper.setItemName(book, 0xb, "Mob Target List");
            EffectHelper.setItemText(book, 0x7, "by " + this.owner);
            EffectHelper.setItemGlowing(book);
            BookHelper.addPages(book,
                    " \u00a77Mob Target List\u00a70\n\nTo change your target list, simply add a new line with the entity you want to toggle - no need to delete lines.\nAn entity starting with \'!\' will NOT be targeted.\nTo save any changes, simply craft this book by itself.",
                    " \u00a77Mob Target List\u00a70\n\nIf you do not save your changes, they will be erased the next time this book is right clicked!\nYou may also toggle entities by right clicking them.",
                    " \u00a77Mob Target List\u00a70\n\nIf you right click while sneaking, the entity will be toggled with a \'!\'.\nOtherwise, it will be toggled normally."
                    );
            if (this.mobWhitelist.size() + this.mobBlacklist.size() <= 0) {
                BookHelper.addPages(book, " \u00a7lTarget List:\u00a7r\n<no entries>");
            }
            else {
                String[] pages = new String[(int) Math.ceil(this.mobWhitelist.size() + this.mobBlacklist.size() / 10)];
                int page = 0;
                byte line = 1;
                pages[0] = " \u00a7lTarget List:\u00a7r\n";
                for (Class entityClass : this.mobWhitelist) {
                    if (line++ == 10) {
                        line = 0;
                        pages[++page] = "";
                    }
                    pages[page] += TargetHelper.classToString(entityClass) + "\n";
                }
                for (Class entityClass : this.mobBlacklist) {
                    if (++line == 10) {
                        line = 0;
                        pages[++page] = "";
                    }
                    pages[page] += "!" + TargetHelper.classToString(entityClass) + "\n";
                }
                BookHelper.addPages(book, pages);
            }
        }
        book.stackTagCompound.setByte("umt", (byte)id);
        return book;
    }

    // Writes a target helper's specs to a book.
    public static void read(String username, ItemStack book) {
        TargetHelper.getTargetHelper(username).readFrom(book);
    }
    private void readFrom(ItemStack book) {
        if (book == null || book.stackTagCompound == null || !book.stackTagCompound.hasKey("pages"))
            return;
        NBTTagList pages = book.stackTagCompound.getTagList("pages", new NBTTagString().getId());
        byte id = book.stackTagCompound.getByte("umt");
        String page;
        String line;
        int index;
        if (id == 0) {
            this.permissions.clear();
            for (int p = 0; p < pages.tagCount(); p++) {
                page = pages.getStringTagAt(p);
                while ((index = page.indexOf("\n")) >= 0) {
                    line = page.substring(0, index);
                    page = page.substring(index + 1);
                    this.readPermissionsLine(line);
                }
                this.readPermissionsLine(page);
            }
        }
        else if (id == 1) {
            this.mobBlacklist.clear();
            this.mobWhitelist.clear();
            for (int p = 0; p < pages.tagCount(); p++) {
                page = pages.getStringTagAt(p);
                while ((index = page.indexOf("\n")) >= 0) {
                    line = page.substring(0, index);
                    page = page.substring(index + 1);
                    this.readTargetListLine(line);
                }
                this.readTargetListLine(page);
            }
        }
        this.writeTo(book, id);
        this.save();
    }
    private void readPermissionsLine(String line) {
        int index = line.indexOf(" ");
        if (index <= 0)
            return;
        try {
            byte permission = (byte)Math.max(0, Integer.parseInt(line.substring(0, index), 2));
            String username = line.substring(index + 1);
            if (username.indexOf(" ") < 0) {
                this.setPermissions(username, permission);
            }
        }
        catch (Exception ex) {
            // Do nothing
        }
    }
    private void readTargetListLine(String line) {
        if (line.contains(" "))
            return;
        boolean blacklist = line.startsWith("!");
        if (blacklist) {
            line = line.substring(1);
        }
        try {
            Class entry = TargetHelper.stringToClass(line);
            if (entry != null) {
                if (blacklist) {
                    this.toggleBlacklist(entry);
                }
                else {
                    this.toggleWhitelist(entry);
                }
            }
        }
        catch (Exception ex) {
            // Do nothing
        }
    }

    // Updates a target helper based on the entity interacted with.
    public static void interact(String username, ItemStack book, int id, EntityLivingBase entity, boolean sneaking) {
        TargetHelper.getTargetHelper(username).interactWith(book, id, entity, sneaking);
    }
    private void interactWith(ItemStack book, int id, EntityLivingBase entity, boolean sneaking) {
        if (id == 0) {
            if (!(entity instanceof EntityPlayer))
                return;
            byte playerPermissions = this.getPermissions(((EntityPlayer)entity).getCommandSenderName());
            if (sneaking) {
                if (playerPermissions > 0) {
                    for (byte permission = TargetHelper.HIGHEST_PERMISSION; permission > 0; permission >>= 1) if ((permission & playerPermissions) > 0) {
                        this.remPermissions(((EntityPlayer)entity).getCommandSenderName(), permission);
                        this.save();
                        break;
                    }
                }
            }
            else {
                for (byte permission = 1; permission <= TargetHelper.HIGHEST_PERMISSION; permission <<= 1) if ((permission & playerPermissions) == 0) {
                    this.addPermissions(((EntityPlayer)entity).getCommandSenderName(), permission);
                    this.save();
                    break;
                }
            }
        }
        else if (id == 1) {
            if (sneaking) {
                this.toggleBlacklist(entity.getClass());
            }
            else {
                this.toggleWhitelist(entity.getClass());
            }
            this.save();
        }
        this.writeTo(book, id);
    }

    // Called when a player logs in, to send his/her target helper to the server and send the server's handlers to the player.
    public static void fetchTargetHelpers(EntityPlayer player) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER && player instanceof EntityPlayerMP) {
            _UtilityMobs.CHANNEL.sendTo(new MessageFetchTargetHelper(), (EntityPlayerMP)player);
            for (Map.Entry<String, TargetHelper> entry : TargetHelper.TARGET_HELPERS.entrySet()) {
                if (entry.getKey() != null && !entry.getValue().destroyed()) {
                    _UtilityMobs.CHANNEL.sendTo(new MessageTargetHelper(entry.getValue()), (EntityPlayerMP)player);
                }
            }
        }
    }

    // Called when the target handler is updated to send changes to the server/other players.
    private void updateTargetHelper() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (FMLCommonHandler.instance().getMinecraftServerInstance() == null && this.owner != null && this.owner.equals(_UtilityMobs.proxy.getPlayer())) {
                _UtilityMobs.CHANNEL.sendToServer(new MessageTargetHelper(this));
            }
        }
        else {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                for (WorldServer world : server.worldServers) {
                    for (Object entity : new ArrayList(world.playerEntities)) {
                        if (entity instanceof EntityPlayerMP && !this.owner.equals(((EntityPlayerMP) entity).getCommandSenderName())) {
                            _UtilityMobs.CHANNEL.sendTo(new MessageTargetHelper(this), (EntityPlayerMP)entity);
                        }
                    }
                }
            }
        }
    }

    // Returns a loadable string from the class, if possible.
    private static String classToString(Class entityClass) {
        String name = null;
        if (entityClass == EntityPlayer.class) {
            name = "Player";
        }
        else {
            try {
                name = (String)EntityList.classToStringMapping.get(entityClass);
            }
            catch (Exception ex) {
                // Do nothing
            }
            if (name == null) {
                name = entityClass.getName();
            }
        }
        return name;
    }

    // Attempts to load a class from the given string.
    private static Class stringToClass(String line) {
        Class entityClass = null;
        if (line.equals("Player")) {
            entityClass = EntityPlayer.class;
        }
        else {
            try {
                entityClass = (Class) EntityList.stringToClassMapping.get(line);
            }
            catch (Exception ex) {
                // Do nothing
            }
            if (entityClass == null) {
                try {
                    entityClass = Class.forName(line);
                }
                catch (Exception ex) {
                    // Do nothing
                }
            }
        }
        return entityClass;
    }
}