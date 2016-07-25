package gtlp.prettyniceores.items;

import gtlp.prettyniceores.Constants;
import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.util.ItemStackHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.junit.Assert;

import javax.annotation.Nonnull;

/**
 * Created by Marv1 on 16.07.2016.
 */
public class DebugAndTestingItem extends Item {

    public static final String NAME = "pno_debugItem";

    public DebugAndTestingItem() {
        setCreativeTab(PrettyNiceOres.CREATIVE_TAB);
        setMaxStackSize(1);
        setRegistryName(Constants.MOD_ID, NAME);
        setUnlocalizedName(NAME);
    }

    @Override
    @Nonnull
    public final ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStackHolderTest test = new ItemStackHolderTest();
        try {
            test.testEquals();
        } catch (Exception e) {
            PrettyNiceOres.LOGGER.error(e);
            return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
        }
        try {
            test.testHashCode();
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
        } catch (Exception e) {
            PrettyNiceOres.LOGGER.error(e);
            return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
        }
    }

    private static class ItemStackHolderTest {

        void testEquals() throws Exception {
            ItemStackHolder apple = new ItemStackHolder(Items.APPLE);
            ItemStackHolder apple2 = new ItemStackHolder(Item.getByNameOrId("apple"));
            Assert.assertEquals("Checks whether the static APPLE and the registered apple create equal ItemStackHolders", apple, apple2);

            ItemStack apple3 = new ItemStack(Items.APPLE, 1, 0);
            //noinspection AssertEqualsBetweenInconvertibleTypes
            Assert.assertEquals("Checks whether the equals() method correctly check equality of ItemStacks", apple, apple3);

            PrettyNiceOres.LOGGER.info("Equality check OK");
        }

        void testHashCode() throws Exception {
            ItemStackHolder apple = new ItemStackHolder(Items.APPLE);
            ItemStackHolder apple2 = new ItemStackHolder(Item.getByNameOrId("apple"));
            Assert.assertEquals("Checks whether the static APPLE and the registered apple create equal hashcodes", apple.hashCode(), apple2.hashCode());

            PrettyNiceOres.LOGGER.info("Hashcode check OK");
        }

    }
}
