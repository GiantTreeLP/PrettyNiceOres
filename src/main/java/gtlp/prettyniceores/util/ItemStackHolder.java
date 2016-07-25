package gtlp.prettyniceores.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Class that holds an {@link ItemStack} with equals() and hashcode() methods
 *
 * @see net.minecraft.item.ItemStack
 */
public class ItemStackHolder implements Comparable {

    private ItemStack itemStack;

    public ItemStackHolder(ItemStack newItemStack) {
        this.setItemStack(newItemStack);
    }

    public ItemStackHolder(Block blockIn) {
        this(blockIn, 1);
    }

    public ItemStackHolder(Block blockIn, int amount) {
        this(blockIn, amount, 0);
    }

    public ItemStackHolder(Item itemIn) {
        this(itemIn, 1);
    }

    public ItemStackHolder(Item itemIn, int amount) {
        this(itemIn, amount, 0);
    }

    public ItemStackHolder(Item itemIn, int amount, int meta) {
        this.setItemStack(new ItemStack(itemIn, amount, meta));
    }

    @SuppressWarnings("ConstantConditions")
    public ItemStackHolder(Block blockIn, int amount, int meta) {
        this.setItemStack(new ItemStack(Item.getItemFromBlock(blockIn), amount, meta));
    }

    public final ItemStack getItemStack() {
        return itemStack;
    }

    public final void setItemStack(ItemStack newItemStack) {
        this.itemStack = newItemStack;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ItemStackHolder) {
            if (getItemStack() == ((ItemStackHolder) obj).getItemStack() || getItemStack().getItem() == ((ItemStackHolder) obj).getItemStack().getItem() || ItemStack.areItemStacksEqual(getItemStack(), ((ItemStackHolder) obj).getItemStack())) {
                return true;
            }
        } else if (obj instanceof ItemStack) {
            if (getItemStack() == obj || getItemStack().getItem() == ((ItemStack) obj).getItem() || ItemStack.areItemStacksEqual(getItemStack(), (ItemStack) obj)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final int hashCode() {
        //noinspection ConstantConditions
        if (getItemStack().getItem() == null) {
            return 0;
        }
        return Arrays.deepHashCode(new Object[]{Item.getIdFromItem(getItemStack().getItem()), getItemStack().getMetadata(), getItemStack().getEnchantmentTagList(), getItemStack().getItemDamage()});
    }

    @Override
    public final String toString() {
        return "ItemStackHolder{" +
                "itemStack=" + getItemStack().toString() +
                '}';
    }

    @Override
    public final int compareTo(@Nullable Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Can't compare to null");
        }
        if (!(o instanceof ItemStackHolder)) {
            throw new ClassCastException("Can't cast " + o.getClass().toString() + " to " + getClass().toString());
        } else {
            int ids = Integer.compare(Item.getIdFromItem(getItemStack().getItem()), Item.getIdFromItem(((ItemStackHolder) o).getItemStack().getItem()));
            if (ids != 0) {
                return ids;
            } else {
                return Integer.compare(getItemStack().getMetadata(), ((ItemStackHolder) o).getItemStack().getMetadata());
            }
        }
    }
}
