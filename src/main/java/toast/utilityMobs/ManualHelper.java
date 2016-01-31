package toast.utilityMobs;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public abstract class ManualHelper
{
    /// Returns a copy of the proper golem info book.
    public static ItemStack manual(String name) {
        return ManualHelper.manual(new ItemStack(Items.written_book), name);
    }
    public static ItemStack manual(int id) {
        return ManualHelper.manual(new ItemStack(Items.written_book), id);
    }
    public static ItemStack manual(ItemStack book, String name) {
        for (int id = _UtilityMobs.UTILITY_TYPES.length; id-- > 0;)
            if (_UtilityMobs.UTILITY_TYPES[id].equalsIgnoreCase(name))
                return ManualHelper.manual(id);
        if (name.equalsIgnoreCase("UPGRADE"))
            return ManualHelper.upgradeManual();
        return null;
    }
    public static ItemStack manual(ItemStack book, int id) {
        if (book == null)
            return null;
        if (id < 0)
            return ManualHelper.upgradeManual(book);
        BookHelper.removePages(book);
        if (_UtilityMobs.UTILITY_TYPES[id] == "Golem") {
            BookHelper.addPages(BookHelper.setTitle(book, "\u00a7bGolem Manual"),
                    " Snow Golem\n\n            [p]\n            [s]\n            [s]\n\np = pumpkin,\ns = snow block\n\nhealth: 4\narmor: 0\ndamage: 0\nmove speed: 2",

                    " Melon Golem\n\n            [p]\n            [m]\n            [m]\n\np = pumpkin,\nm = melon block\n\nhealth: 4\narmor: 0\ndamage: 0\nmove speed: 2",

                    " Stone Golem\n\n            [p]\n            [c]\n            [c]\n\np = pumpkin,\nc = cobblestone\n\nhealth: 10\narmor: 2\ndamage: 2\nmove speed: 2.4",

                    " Large Stone Golem\n\n            [p]\n         [c][c][c]\n            [c]\n\np = pumpkin,\nc = cobblestone\n\nhealth: 16\narmor: 2\ndamage: 3\nmove speed: 2",

                    " Steam Golem\n\n            [p]\n         [c][f][c]\n            [c]\n\np = pumpkin,\nf = furnace,\nc = cobblestone\n\nhealth: 40\narmor: 2\ndamage: 7\nmove speed: 2.5",

                    " Obsidian Golem\n\n            [p]\n         [o][o][o]\n            [o]\n\np = pumpkin,\no = obsidian\n\nhealth: 100\narmor: 20\ndamage: 2\nmove speed: 2.5",

                    " Armor Golem\n\n            [p]\n            [s]\n            [s]\n\np = pumpkin,\ns = steel block\n\nhealth: 40\narmor: 15\ndamage: 7\nmove speed: 2.8",

                    " Iron Golem\n\n            [p]\n         [s][s][s]\n            [s]\n\np = pumpkin,\ns = steel block\n\nhealth: 100\narmor: 0\ndamage: 17\nmove speed: 2.5",

                    " Gilded Armor Golem\n\n            [p]\n            [g]\n            [g]\n\np = pumpkin,\ng = gold block\n\nhealth: 20\narmor: 11\ndamage: 5\nmove speed: 2.8",

                    " Scarecrow\n\n            [p]\n         [f][w][f]\n            [f]\n\np = pumpkin,\nw = wool, f = fence\n\nhealth: 20\narmor: 0\ndamage: 1 + equipped\nmove speed: 2.5",

                    " Bound Soul\n\n            [p]\n            [s]\n            [s]\n\np = pumpkin,\ns = soul sand\n\nhealth: 20\narmor: equipped\ndamage: 1 + equipped\nmove speed: 3"
                    );
        }
        else if (_UtilityMobs.UTILITY_TYPES[id] == "Turret") {
            BookHelper.addPages(BookHelper.setTitle(book, "\u00a7bTurret Golem Manual"),
                    " Snow Turret\n\n            [d]\n            [f]\n            [s]\n\nd = dispenser,\nf = fence,\ns = snow block\n\nhealth: 20\narmor: 0\ndamage: 0\nattack speed: 10",

                    " Stone Turret\n\n            [d]\n            [f]\n            [c]\n\nd = dispenser,\nf = fence,\nc = cobblestone\n\nhealth: 20\narmor: 0\ndamage: 4\nattack speed: 3.3",

                    " Stone Brick Turret\n\n            [d]\n            [f]\n            [s]\n\nd = dispenser,\nf = fence,\ns = stone brick\n\nhealth: 20\narmor: 0\ndamage: 2\nattack speed: 5.7",

                    " Obsidian Turret\n\n            [d]\n            [n]\n            [o]\n\nd = dispenser,\nn = nether fence,\no = obsidian\n\nhealth: 100\narmor: 20\ndamage: 4\nattack speed: 3.3",

                    " Fireball Turret\n\n            [d]\n            [f]\n            [n]\n\nd = dispenser,\nf = fence,\nn = netherrack\n\nhealth: 20\narmor: 0\ndamage: 4\nattack speed: 3.3",

                    " Ghast Turret\n\n            [d]\n            [n]\n            [b]\n\nd = dispenser,\nn = nether fence,\nb = nether brick\n\nhealth: 20\narmor: 8\ndamage: 6\nattack speed: 2",

                    " Fire Turret\n\n            [d]\n            [f]\n            [b]\n\nd = dispenser,\nf = fence,\nb = redstone block\n\nhealth: 20\narmor: 0\ndamage: 4\nattack speed: 3.3",

                    " Sniper Turret\n\n            [d]\n            [f]\n            [b]\n\nd = dispenser,\nf = fence,\nb = lapis block\n\nhealth: 20\narmor: 0\ndamage: 6\nattack speed: 2.9",

                    " Shotgun Turret\n\n            [d]\n            [f]\n            [s]\n\nd = dispenser,\nf = fence,\ns = steel block\n\nhealth: 20\narmor: 12\ndamage: 2 x 6\nattack speed: 3.3",

                    " Gatling Turret\n\n            [d]\n            [f]\n            [g]\n\nd = dispenser,\nf = fence,\ng = gold block\n\nhealth: 20\narmor: 6\ndamage: 1\nattack speed: 13.3",

                    " Volley Turret\n\n            [d]\n            [f]\n            [e]\n\nd = dispenser,\nf = fence,\ne = emerald block\n\nhealth: 20\narmor: 6\ndamage: 3 x 6\nattack speed: 2.5",

                    " Killer Turret\n\n            [d]\n            [f]\n            [d]\n\nd = dispenser,\nf = fence,\nd = diamond block\n\nhealth: 20\narmor: 18\ndamage: 6 x ?\nattack speed: 3.3"
                    );
        }
        else if (_UtilityMobs.UTILITY_TYPES[id] == "Block") {
            BookHelper.addPages(BookHelper.setTitle(book, "\u00a7bBlock Golem Manual"),
                    " Workbench Golem\n\n            [s]\n            [w]\n\ns = skeleton skull,\nw = workbench\n\nhealth: 10\narmor: 0\nmove speed: 2.8",

                    " Furnace Golem\n\n            [s]\n            [f]\n\ns = skeleton skull,\nf = furnace\n\nhealth: 10\narmor: 2\nmove speed: 2.8",

                    " Anvil Golem\n\n            [s]\n            [a]\n\ns = skeleton skull,\na = anvil\n\nhealth: 10\narmor: 16\nmove speed: 2.8",

                    " Jack o\'Lantern Golem\n\n            [s]\n            [j]\n\ns = skeleton skull,\nj = jack o\'lantern\n\nhealth: 10\narmor: 0\nmove speed: 2.8",

                    " Chest Golem\n\n            [s]\n            [c]\n\ns = skeleton skull,\nc = chest\n\nhealth: 10\narmor: 0\nmove speed: 2.8",

                    " Trapped Chest Golem\n\n            [s]\n            [c]\n\ns = skeleton skull,\nc = trapped chest\n\nhealth: 10\narmor: 0\nmove speed: 2.8",

                    " Ender Chest Golem\n\n            [s]\n            [c]\n\ns = skeleton skull,\nc = ender chest\n\nhealth: 10\narmor: 20\nmove speed: 2.8",

                    " Jukebox Golem\n\n            [s]\n            [j]\n\ns = skeleton skull,\nj = jukebox\n\nhealth: 10\narmor: 0\nmove speed: 2.8"
                    );
        }
        else if (_UtilityMobs.UTILITY_TYPES[id] == "Hostile") {
            BookHelper.addPages(BookHelper.setTitle(book, "\u00a7bHostile Golem Manual"),
                    "What are you doing with this book?\nOh well, you can keep it - this book will automatically fill itself when hostile golems are actually added."
                    );
        }
        else if (_UtilityMobs.UTILITY_TYPES[id] == "Colossal") {
            BookHelper.addPages(BookHelper.setTitle(book, "\u00a7bColossal Golem Manual"),
                    " Colossal golem pattern:\n\n         [][]||||[][]\n         [][][][][]\n           [][][]\n           []  []\n\nEach [] represents the block required, while the |||| is where a creeper head must be placed on the side of the structure.",

                    " Stone Colossus\n\n\n\n\n\nblock required:\ncobblestone\n\nhealth: 100\narmor: 2\ndamage: 10\nmove speed: 1.5",

                    " Obsidian Colossus\n\n\n\n\n\nblock required:\nobsidian\n\nhealth: 500\narmor: 20\ndamage: 10\nmove speed: 1.5",

                    " Armor Colossus\n\n\n\n\n\nblock required:\nsteel block\n\nhealth: 500\narmor: 0\ndamage: 18\nmove speed: 2"
                    );
        }
        book.stackTagCompound.setByte("umb", (byte)id);
        return book;
    }

    /// Returns a copy of the turret upgrade info book.
    public static ItemStack upgradeManual() {
        return ManualHelper.upgradeManual(new ItemStack(Items.written_book));
    }
    public static ItemStack upgradeManual(ItemStack book) {
        if (book == null)
            return null;
        BookHelper.removePages(book);
        BookHelper.addPages(BookHelper.setTitle(book, "\u00a7bUpgrade Manual"),
                " \u00a77Turret Upgrades\u00a70\n\nTurret upgrades apply bonuses to your turrets. Each turret can only have one upgrade at a time, and the upgrade item is never used up. Right click a turret to give or take an upgrade item. Some turrets do not take all upgrades.",
                " \u00a77Upgrade List\u00a70\n\nDiamond:       +damage\nIron Ingot:         fire\nFeather:      pushable\nGunpowder:        bomb\nSlimeball:            slow\nEnder Pearl:   +range\nSpider Eye:      poison\nFire Charge:     ghast\n???:                  ???"
                ).stackTagCompound.setByte("umb", (byte)-1);
        return book;
    }
}