package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.INamedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public abstract class NiceOreBase extends BlockOre implements INamedBlock {

    public static final PropertyBool SCHEDULED = PropertyBool.create("scheduled");
    private static final Vec3i[] ADJACENT = new Vec3i[]{new Vec3i(-1, -1, -1), new Vec3i(-1, -1, 0), new Vec3i(-1, -1, 1), new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 0),
                                                        new Vec3i(-1, 0, 1), new Vec3i(-1, 1, -1), new Vec3i(-1, 1, 0), new Vec3i(-1, 1, 1), new Vec3i(0, -1, -1),
                                                        new Vec3i(0, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(0, 1, -1), new Vec3i(0, 1, 0),
                                                        new Vec3i(0, 1, 1), new Vec3i(1, -1, -1), new Vec3i(1, -1, 0), new Vec3i(1, -1, 1), new Vec3i(1, 0, -1), new Vec3i(1, 0, 0),
                                                        new Vec3i(1, 0, 1), new Vec3i(1, 1, -1), new Vec3i(1, 1, 0), new Vec3i(1, 1, 1)};

    protected NiceOreBase(String name) {
        super();
        setRegistryName(PrettyNiceOres.MODID, name);
        setUnlocalizedName(name);
        setDefaultState(blockState.getBaseState().withProperty(SCHEDULED, false));
        setHardness(10f);
    }

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
        if (willHarvest || player.isCreative()) {
            Map<BlockPos, Block> toDestroy = new ConcurrentHashMap<>();
            toDestroy.put(pos, this);
            toDestroy.putAll(getAdjacentBlocks(world, pos, state, new ConcurrentHashMap<>()));
            world.setBlockState(pos, state.withProperty(SCHEDULED, true));
            System.out.println(String.format("Removing %d blocks", toDestroy.size()));
            toDestroy.entrySet().parallelStream().forEach(entry -> {
                ItemStack itemMainhand = player.getHeldItemMainhand();
                if (player.isCreative() || (itemMainhand.canHarvestBlock(state) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage())) {
                    if (!player.isCreative()) {
                        entry.getValue().dropBlockAsItem(world, pos, world.getBlockState(entry.getKey()), 0);
                    }
                    world.setBlockState(entry.getKey(), Blocks.air.getDefaultState());
                    if (itemMainhand != null) {
                        itemMainhand.attemptDamageItem(1, world.rand);
                    }
                }

            });
            toDestroy.clear();
        }
        //Prevent duplication
        return false;
    }

    protected Map<BlockPos, Block> getAdjacentBlocks(World world, BlockPos pos, IBlockState state, Map<BlockPos, Block> adjacent) {
        if (adjacent.containsKey(pos) || Thread.currentThread().getStackTrace().length > 1000) {
            return adjacent;
        }
        adjacent.put(pos, world.getBlockState(pos).getBlock());
        world.setBlockState(pos, state.withProperty(SCHEDULED, true));
        Stream.of(ADJACENT).parallel().forEach(vector -> {
            BlockPos adjacentPos = pos.add(vector);
            IBlockState blockState = world.getBlockState(adjacentPos);
            if (!adjacent.containsKey(adjacentPos) && blockState.getBlock().getRegistryName().equals(state.getBlock().getRegistryName()) && !blockState.getValue(SCHEDULED)) {
                adjacent.putAll(((NiceOreBase) blockState.getBlock()).getAdjacentBlocks(world, adjacentPos, state, adjacent));
            }
        });

        return adjacent;
    }
}
