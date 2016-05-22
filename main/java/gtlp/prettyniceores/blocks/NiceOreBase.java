package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.PrettyNiceOres;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public abstract class NiceOreBase extends BlockOre {

    public static final PropertyBool SCHEDULED = PropertyBool.create("scheduled");
    public static final Vec3i[] ADJACENT = new Vec3i[]{new Vec3i(1, 0, 0), new Vec3i(0, 1, 0), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(0, -1, 0), new Vec3i(0, 0, -1)};

    protected NiceOreBase(String name) {
        super();
        setRegistryName(PrettyNiceOres.MODID, name);
        setUnlocalizedName(name);
        setDefaultState(blockState.getBaseState().withProperty(SCHEDULED, false));
        setHardness(10f);
    }

    public static boolean isSmeltable() {
        return true;
    }

    public abstract ItemStack getSmeltingResult();

    public abstract String getOreDictType();

    public abstract String getName();

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SCHEDULED);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            Map<BlockPos, Block> toCheck = new HashMap<>();
            toCheck.put(pos, this);
            toCheck.putAll(getAdjacentBlocks(world, pos, state));
            toCheck.forEach((position, block) -> world.destroyBlock(position, !player.isCreative()));
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, false);
    }

    protected Map<BlockPos, Block> getAdjacentBlocks(World world, BlockPos pos, IBlockState state) {
        Map<BlockPos, Block> adjacent = new HashMap<>();
        adjacent.put(pos, world.getBlockState(pos).getBlock());
        world.setBlockState(pos, state.withProperty(SCHEDULED, true));
        for (Vec3i vector : ADJACENT) {
            BlockPos adjacentPos = pos.add(vector);
            IBlockState blockState = world.getBlockState(adjacentPos);
            if (blockState.getBlock().getRegistryName().equals(state.getBlock().getRegistryName()) && !blockState.getValue(SCHEDULED)) {
                adjacent.putAll(((NiceOreBase) blockState.getBlock()).getAdjacentBlocks(world, adjacentPos, state));
            }
        }
        return adjacent;
    }
}
