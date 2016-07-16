package gtlp.prettyniceores.util;

import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

/**
 * Created by Marv1 on 16.07.2016.
 */

/**
 * Class that holds an {@link ItemStack} with equals() and hashcode() methods
 *
 * @see net.minecraft.item.ItemStack
 */
public class ItemStackHolder implements Comparable {

    private ItemStack itemStack;

    public ItemStackHolder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStackHolder(Item itemIn, int amount, int meta) {
        this.itemStack = new ItemStack(itemIn, amount, meta);
    }

    public ItemStackHolder(Block blockIn, int amount, int meta) {
        this.itemStack = new ItemStack(Item.getItemFromBlock(blockIn), amount, meta);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStack && (itemStack == obj || itemStack.getItem() == ((ItemStack) obj).getItem() || ItemStack.areItemStacksEqual(itemStack, (ItemStack) obj));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemStack);
    }

    @Override
    public String toString() {
        return "ItemStackHolder{" +
                "itemStack=" + itemStack.toString() +
                '}';
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof ItemStackHolder) {
            int ids = Integer.compare(Item.getIdFromItem(itemStack.getItem()), Item.getIdFromItem(((ItemStackHolder) o).itemStack.getItem()));
            if (ids == 0) {
                return Integer.compare(itemStack.getMetadata(), ((ItemStackHolder) o).itemStack.getMetadata());
            } else {
                return ids;
            }
        } else {
            throw new ClassCastException("Can't cast " + o.getClass().toString() + " to " + getClass().toString());
        }
    }
}
