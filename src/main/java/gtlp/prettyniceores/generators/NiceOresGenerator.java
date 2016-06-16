package gtlp.prettyniceores.generators;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceOresGenerator implements IWorldGenerator {

    //Size of storage array (cube with indices 0 to 15, ie. 16)
    private static final int STORAGE_ARRAY_SIZE = 16;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = chunkProvider.provideChunk(chunkX, chunkZ);
        //Fairly quick nested loop to replace vanilla and ore dictionary ores with ours upon generation of a chunk.
        PrettyNiceOres.blockList.entrySet().parallelStream().filter(entry -> entry.getValue() instanceof IOreDictCompatible).forEach(entry ->
                OreDictionary.getOres(((IOreDictCompatible) entry.getValue()).getOreDictType()).stream().filter(itemStack -> !itemStack.isItemEqual(new ItemStack(entry.getValue()))).forEach(itemStack -> {
                    Stream.of(chunk.getBlockStorageArray()).filter(blockStorage -> blockStorage != null).forEach(blockStorage ->
                            IntStream.range(0, STORAGE_ARRAY_SIZE).forEach(y ->
                                    IntStream.range(0, STORAGE_ARRAY_SIZE).forEach(z ->
                                            IntStream.range(0, STORAGE_ARRAY_SIZE).forEach(x -> {
                                                        IBlockState state = blockStorage.get(x, y, z);
                                                        if (state != null && itemStack.isItemEqual(new ItemStack(state.getBlock()))) {
                                                            synchronized (this) {
                                                                blockStorage.getData().set(x, y, z, entry.getValue().getDefaultState());
                                                            }
                                                        }
                                                    }

                                            ))));
                }));
    }
}
