package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Marv1 on 23.05.2016.
 */
public class NiceDiamondOre extends NiceOreBase implements ISmeltable, IOreDictCompatible {

    public static final String NAME = "nice_diamond_ore";
    private static final String OREDICTTYPE = "oreDiamond";
    private static final int SMELTING_AMOUNT = 1;

    public NiceDiamondOre() {
        super(NAME);
        setLightLevel(1f);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public final Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.diamond;
    }

    @Override
    public final int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        return MathHelper.getRandomIntegerInRange(rand, 3, 7);
    }

    @Override
    public final String getOreDictType() {
        return OREDICTTYPE;
    }

    @Override
    public final String getName() {
        return NAME;
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return new ItemStack(Items.coal, SMELTING_AMOUNT);
    }

    @Override
    public final float getSmeltingExp() {
        return 4f;
    }
}