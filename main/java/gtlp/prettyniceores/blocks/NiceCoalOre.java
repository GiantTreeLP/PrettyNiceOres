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

import java.util.List;
import java.util.Random;

/**
 * Created by Marv1 on 23.05.2016.
 */
public class NiceCoalOre extends NiceOreBase implements ISmeltable, IOreDictCompatible {

    public static final String NAME = "nice_coal_ore";
    private static final String OREDICTTYPE = "oreCoal";

    public NiceCoalOre() {
        super(NAME);
        setHarvestLevel("pickaxe", 1);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.coal;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        return MathHelper.getRandomIntegerInRange(rand, 0, 2);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return super.getDrops(world, pos, state, fortune);
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
        return new ItemStack(Item.getByNameOrId("coal"), 4);
    }

    @Override
    public final float getSmeltingExp() {
        return 2f;
    }
}