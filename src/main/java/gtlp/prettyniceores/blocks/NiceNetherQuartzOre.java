package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.interfaces.INamedBlock;
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
public class NiceNetherQuartzOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock {

    public static final String NAME = "nice_netherquartz_ore";
    public static final String OREDICTTYPE = "oreQuartz";
    private static final int SMELTING_AMOUNT = 4;

    public NiceNetherQuartzOre() {
        super(NAME);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public final Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.QUARTZ;
    }

    @Override
    public final int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        return MathHelper.getRandomIntegerInRange(rand, 1, 3);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return super.quantityDropped(state, fortune, random);
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
        return new ItemStack(Items.QUARTZ, SMELTING_AMOUNT);
    }

    @Override
    public final float getSmeltingExp() {
        return 2f;
    }
}