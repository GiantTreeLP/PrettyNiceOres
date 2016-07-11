package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.Constants;
import gtlp.prettyniceores.PrettyNiceOres;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public abstract class NiceOreBase extends BlockOre {

    private static final float HARDNESS = 10f;
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
        setRegistryName(Constants.MOD_ID, name);
        setUnlocalizedName(name);
        setDefaultState(blockState.getBaseState());
        setHardness(HARDNESS);
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
     * @return true, if the player is in creative mode, otherwise we handle block destruction manually.
     * @see net.minecraft.block.Block#removedByPlayer
     */
    @Override
    public final boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            if (player.isCreative()) {
                return super.removedByPlayer(state, world, pos, player, willHarvest);
            }
            if (willHarvest) {
                ItemStack itemMainhand = player.getHeldItemMainhand();

                if (itemMainhand != null && itemMainhand.canHarvestBlock(state) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage()) {

                    //Count the amount of destroyed blocks
                    AtomicInteger blocks = new AtomicInteger(0);

                    //Stop the time it takes to destroy all blocks.
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    getAdjacentBlocks(world, pos, world.getBlockState(pos).getBlock(), player, itemMainhand, blocks);
                    stopWatch.stop();
                    PrettyNiceOres.LOGGER.printf(Level.INFO, "Removed %d blocks in %d ms", blocks.get(), stopWatch.getTime());
                    itemMainhand.attemptDamageItem(itemMainhand.getItemDamage() % 2 == 0 ? 1 : 2, world.rand);
                }
            }
        }
        //Prevent duplication.
        return false;
    }

    /**
     * @param world        The current world.
     * @param pos          Block position in world.
     * @param block        The source block to search for.
     * @param player       Determines whether or not to drop the item.
     * @param itemMainhand Item to deal damage to.
     * @param blocks       Integer to count the amount of destroyed blocks
     * @see #removedByPlayer
     */
    private void getAdjacentBlocks(World world, BlockPos pos, Block block, EntityPlayer player, ItemStack itemMainhand, AtomicInteger blocks) {
        if (!world.getChunkFromBlockCoords(pos).isLoaded()) {
            return;
        }
        if (itemMainhand != null && itemMainhand.canHarvestBlock(world.getBlockState(pos)) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage()) {
            int silktouchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemMainhand);
            if (silktouchLvl == 0) {
                int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemMainhand);
                world.getBlockState(pos).getBlock().dropBlockAsItem(world, player.getPosition(), world.getBlockState(pos), fortune);
                if (block.getExpDrop(world.getBlockState(pos), world, pos, fortune) > 0) {
                    world.spawnEntityInWorld(new EntityXPOrb(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), block.getExpDrop(world.getBlockState(pos), world, pos, fortune)));
                }
            } else if (silktouchLvl >= 1) {
                ItemStack itemStack = createStackedBlock(world.getBlockState(pos));
                if (itemStack != null) {
                    Block.spawnAsEntity(world, player.getPosition(), itemStack);
                }
            }

            //Destroy the block without any effects (prevents crashes caused by too many sounds or particles)
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
            //Increase amount of destroyed blocks
            blocks.getAndAdd(1);
            itemMainhand.attemptDamageItem(itemMainhand.getItemDamage() % 2 == 0 || itemMainhand.getMaxDamage() - itemMainhand.getItemDamage() == 1 ? 1 : 2, world.rand);
            for (Vec3i vector : ADJACENT) {
                if (Thread.currentThread().getStackTrace().length < STACK_LIMIT - 1) {
                    BlockPos posAdjacent = pos.add(vector);
                    IBlockState candidateBlockState = world.getBlockState(posAdjacent);
                    if (candidateBlockState.getBlock() == block) {
                        ((NiceOreBase) candidateBlockState.getBlock()).getAdjacentBlocks(world, posAdjacent, block, player, itemMainhand, blocks);
                    }
                }
            }
        }
    }

}
