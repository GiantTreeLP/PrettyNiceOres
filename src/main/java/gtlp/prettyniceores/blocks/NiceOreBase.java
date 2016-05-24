package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.INamedBlock;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;

import java.util.stream.Stream;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public abstract class NiceOreBase extends BlockOre implements INamedBlock {

    private static final Vec3i[] ADJACENT = new Vec3i[]{new Vec3i(-1, -1, -1), new Vec3i(-1, -1, 0), new Vec3i(-1, -1, 1), new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 0),
                                                        new Vec3i(-1, 0, 1), new Vec3i(-1, 1, -1), new Vec3i(-1, 1, 0), new Vec3i(-1, 1, 1), new Vec3i(0, -1, -1),
                                                        new Vec3i(0, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(0, 1, -1), new Vec3i(0, 1, 0),
                                                        new Vec3i(0, 1, 1), new Vec3i(1, -1, -1), new Vec3i(1, -1, 0), new Vec3i(1, -1, 1), new Vec3i(1, 0, -1), new Vec3i(1, 0, 0),
                                                        new Vec3i(1, 0, 1), new Vec3i(1, 1, -1), new Vec3i(1, 1, 0), new Vec3i(1, 1, 1)};
    private static final int STACK_LIMIT = 1024;

    protected NiceOreBase(String name) {
        super();
        setRegistryName(PrettyNiceOres.MOD_ID, name);
        setUnlocalizedName(name);
        setDefaultState(blockState.getBaseState());
        setHardness(10f);
    }

    public abstract String getName();

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            if (willHarvest || player.isCreative()) {
                ItemStack itemMainhand = player.getHeldItemMainhand();

                if (player.isCreative() || (itemMainhand.canHarvestBlock(state) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage())) {
                    int fortune = 0;
                    if (!player.isCreative()) {
                        NBTTagList enchantmentTagList = itemMainhand.getEnchantmentTagList();
                        for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
                            fortune = enchantmentTagList.getCompoundTagAt(i).getShort("id") == Enchantment.getEnchantmentID(Enchantments.fortune) ? enchantmentTagList.getCompoundTagAt(i).getShort("lvl") : 0;
                        }
                    }
                    final int finalFortune = fortune;
                    final Integer[] blocks = {0};
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    getAdjacentBlocks(world, pos, world.getBlockState(pos).getBlock().getRegistryName(), player.isCreative(), itemMainhand, finalFortune, blocks);
                    stopWatch.stop();
                    PrettyNiceOres.LOGGER.printf(Level.INFO, "Removed %d blocks in %d ns", blocks[0], stopWatch.getNanoTime());
                    if (itemMainhand != null) {
                        itemMainhand.attemptDamageItem(itemMainhand.getItemDamage() % 2 == 0 ? 1 : 2, world.rand);
                    }
                }
            }
        }
        //Prevent duplication
        return false;
    }

    private void getAdjacentBlocks(World world, BlockPos pos, ResourceLocation registryName, boolean isPlayerCreative, ItemStack itemMainhand, int fortune, Integer[] blocks) {
        if (!world.getChunkFromBlockCoords(pos).isLoaded()) {
            return;
        }
        if (isPlayerCreative || (itemMainhand.canHarvestBlock(world.getBlockState(pos)) && itemMainhand.getItemDamage() <= itemMainhand.getMaxDamage())) {
            if (!isPlayerCreative) {
                world.getBlockState(pos).getBlock().dropBlockAsItem(world, pos, world.getBlockState(pos), fortune);
            }
            blocks[0]++;
            world.setBlockState(pos, Blocks.air.getDefaultState(), 3);
            if (itemMainhand != null) {
                itemMainhand.attemptDamageItem(itemMainhand.getItemDamage() % 2 == 0 ? 1 : 2, world.rand);
            }
            Stream.of(ADJACENT).forEach(vector -> {
                if (Thread.currentThread().getStackTrace().length >= STACK_LIMIT - 1) {
                    Thread t = new Thread(() -> recurse(pos.add(vector), world, registryName, isPlayerCreative, itemMainhand, fortune, blocks));
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    recurse(pos.add(vector), world, registryName, isPlayerCreative, itemMainhand, fortune, blocks);
                }
            });
        }
    }

    private void recurse(BlockPos pos, World world, ResourceLocation registryName, boolean isPlayerCreative, ItemStack itemMainhand, int fortune, Integer[] blocks) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().getRegistryName().equals(registryName)) {
            ((NiceOreBase) blockState.getBlock()).getAdjacentBlocks(world, pos, registryName, isPlayerCreative, itemMainhand, fortune, blocks);
        }
    }
}
