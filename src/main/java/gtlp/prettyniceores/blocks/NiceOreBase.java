package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.PrettyNiceOres;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public abstract class NiceOreBase extends BlockOre {

    //Vectors pointing to adjacent and diagonal positions around the block
    private static final Vec3i[] ADJACENT = new Vec3i[]{new Vec3i(-1, -1, -1), new Vec3i(-1, -1, 0), new Vec3i(-1, -1, 1), new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 0),
                                                        new Vec3i(-1, 0, 1), new Vec3i(-1, 1, -1), new Vec3i(-1, 1, 0), new Vec3i(-1, 1, 1), new Vec3i(0, -1, -1),
                                                        new Vec3i(0, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(0, 1, -1), new Vec3i(0, 1, 0),
                                                        new Vec3i(0, 1, 1), new Vec3i(1, -1, -1), new Vec3i(1, -1, 0), new Vec3i(1, -1, 1), new Vec3i(1, 0, -1), new Vec3i(1, 0, 0),
                                                        new Vec3i(1, 0, 1), new Vec3i(1, 1, -1), new Vec3i(1, 1, 0), new Vec3i(1, 1, 1)};
    //Tested thread stack limit. Global constant, no matter what the actual set stack size is.
    private static final int STACK_LIMIT = 1024;

    /**
     * Base block for nice ores.
     *
     * @param name registry name of the block
     */
    protected NiceOreBase(String name) {
        super();
        setRegistryName(PrettyNiceOres.MOD_ID, name);
        setUnlocalizedName(name);
        setDefaultState(blockState.getBaseState());
        setHardness(10f);
    }

    /**
     * Removes an instance of this block and all adjacent ones {@link #getAdjacentBlocks}
     * Determines fortune level and starts recursive calls.
     * Deals damage to the tool in the players main hand (the used tool)
     *
     * @param state       The current block state.
     * @param world       The current world
     * @param player      The player damaging the block, may be null
     * @param pos         Block position in world
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *                    Can be useful to delay the destruction of tile entities till after harvestBlock
     * @return false, we handle block destruction manually
     * @see net.minecraft.block.Block#removedByPlayer
     */
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            if (willHarvest || player.isCreative()) {
                ItemStack itemMainhand = player.getHeldItemMainhand();

                if (player.isCreative() || (itemMainhand.canHarvestBlock(state) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage())) {
                    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.fortune, itemMainhand);
                    //Final variables to make lambdas happy.
                    //Hacky way of allowing a lambda to manipulate a final variable.
                    AtomicInteger blocks = new AtomicInteger(0);

                    //Profile the time it takes to destroy all blocks.
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    getAdjacentBlocks(world, pos, world.getBlockState(pos).getBlock(), player.isCreative(), itemMainhand, fortune, blocks);
                    stopWatch.stop();
                    PrettyNiceOres.LOGGER.printf(Level.INFO, "Removed %d blocks in %d ns", blocks.get(), stopWatch.getNanoTime());
                    if (itemMainhand != null) {
                        itemMainhand.attemptDamageItem(itemMainhand.getItemDamage() % 2 == 0 ? 1 : 2, world.rand);
                    }
                }
            }
        }
        //Prevent duplication.
        return false;
    }

    /**
     * @param world            The current world.
     * @param pos              Block position in world.
     * @param block            The source block to search for.
     * @param isPlayerCreative Determines whether or not to drop the item.
     * @param itemMainhand     Item to deal damage to.
     * @param fortune          Determine additional drops.
     * @param blocks           Integer to count the amount of destroyed blocks
     * @see #removedByPlayer
     */
    private void getAdjacentBlocks(World world, BlockPos pos, Block block, boolean isPlayerCreative, ItemStack itemMainhand, int fortune, AtomicInteger blocks) {
        if (!world.getChunkFromBlockCoords(pos).isLoaded()) {
            return;
        }
        if (isPlayerCreative || (itemMainhand.canHarvestBlock(world.getBlockState(pos)) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage())) {
            if (!isPlayerCreative) {
                world.getBlockState(pos).getBlock().dropBlockAsItem(world, pos, world.getBlockState(pos), fortune);
            }
            //Destroy the block without any effects (prevents crashes caused by too many sounds or particles)
            world.setBlockState(pos, Blocks.air.getDefaultState(), 3);
            //Increase amount of destroyed blocks
            blocks.getAndAdd(1);
            if (itemMainhand != null) {
                itemMainhand.attemptDamageItem(itemMainhand.getItemDamage() % 2 == 0 ? 1 : 2, world.rand);
            }
            List<Thread> threads = new ArrayList<>();
            Stream.of(ADJACENT).forEach(vector -> {
                //Workaround for the 1024 stack limit, increase of stack size is ignored by the JVM
                if (Thread.currentThread().getStackTrace().length >= STACK_LIMIT - 1) {
                    Thread t = new Thread(() -> recurse(world, pos.add(vector), block, isPlayerCreative, itemMainhand, fortune, blocks));
                    t.start();
                    threads.add(t);
                } else {
                    recurse(world, pos.add(vector), block, isPlayerCreative, itemMainhand, fortune, blocks);
                }
            });
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Determines whether or not to recurse to the next block.
     * Single method to avoid repeating code.
     *
     * @param world            The current world.
     * @param pos              Block position in world.
     * @param block            The source block to search for.
     * @param isPlayerCreative Determines whether or not to drop the item.
     * @param itemMainhand     Item to deal damage to.
     * @param fortune          Determine additional drops.
     * @param blocks           Integer to count the amount of destroyed blocks
     * @see #removedByPlayer
     * @see #getAdjacentBlocks
     */
    private void recurse(World world, BlockPos pos, Block block, boolean isPlayerCreative, ItemStack itemMainhand, int fortune, AtomicInteger blocks) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() == block) {
            ((NiceOreBase) blockState.getBlock()).getAdjacentBlocks(world, pos, block, isPlayerCreative, itemMainhand, fortune, blocks);
        }
    }
}
