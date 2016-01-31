package toast.utilityMobs;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeSavePermissions implements IRecipe
{
    // Used to check if a recipe matches current crafting inventory.
    @Override
    public boolean matches(InventoryCrafting craftMatrix, World world) {
        ItemStack targetBook = null;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack ingredient = craftMatrix.getStackInSlot(i);
            if (ingredient == null) {
                // Do nothing
            }
            else if (targetBook == null && (ingredient.getItem() == Items.writable_book || ingredient.getItem() == Items.written_book) && ingredient.stackTagCompound != null && ingredient.stackTagCompound.hasKey("umt")) {
                targetBook = ingredient;
            }
            else
                return false;
        }
        return targetBook != null;
    }

    // Returns an item stack that is the result of this recipe.
    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack ingredient = craftMatrix.getStackInSlot(i);
            if (ingredient != null) {
                ItemStack book = new ItemStack(Items.writable_book);
                book.stackTagCompound = (NBTTagCompound)ingredient.stackTagCompound.copy();
                book.stackTagCompound.setByte("umu", (byte)0);
                return book;
            }
        }
        return null;
    }

    // Returns the size of the recipe area.
    @Override
    public int getRecipeSize() {
        return 1;
    }

    // Returns the output for the recipe.
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}