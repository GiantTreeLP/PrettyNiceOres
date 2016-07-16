package gtlp.prettyniceores.generators;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.INiceOre;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.util.ItemStackHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceOresGenerator implements IWorldGenerator {

    //Size of storage array (cube with indices 0 to 15, ie. 16)
    private static final int STORAGE_ARRAY_SIZE = 16;
    private final ConcurrentSkipListMap<ItemStackHolder, IBlockState> replacementMap = new ConcurrentSkipListMap<>();

    public NiceOresGenerator() {
        PrettyNiceOres.getBlockList().entrySet().stream().filter(entry -> entry.getValue() instanceof INiceOre && entry.getValue() instanceof IOreDictCompatible).forEach(niceOre -> {
            OreDictionary.getOres(((IOreDictCompatible) niceOre.getValue()).getOreDictType()).forEach(stack -> {
                if (stack.getItem() instanceof ItemBlock && !(stack.getItem() instanceof INiceOre)) {
                    replacementMap.put(new ItemStackHolder(stack), niceOre.getValue().getDefaultState());
                }
            });
        });
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = chunkProvider.provideChunk(chunkX, chunkZ);
        //Fairly quick nested loop to replace vanilla and ore dictionary ores with ours upon generation of a chunk.
        Stream.of(chunk.getBlockStorageArray()).filter(blockStorage -> blockStorage != null).parallel().forEach(blockStorage ->
                IntStream.range(0, STORAGE_ARRAY_SIZE).forEach(y ->
                        IntStream.range(0, STORAGE_ARRAY_SIZE).forEach(z -> {
                            IntStream.range(0, STORAGE_ARRAY_SIZE).forEach(x -> {
                                IBlockState state = blockStorage.get(x, y, z);
                                ItemStackHolder key = new ItemStackHolder(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
                                if (replacementMap.containsKey(key)) {
                                    setBlock(blockStorage, y, z, x, replacementMap.get(key));
                                }
                            });
                        })));
    }

    private synchronized void setBlock(ExtendedBlockStorage blockStorage, int y, int z, int x, IBlockState state) {
        synchronized (this) {
            blockStorage.getData().set(x, y, z, state);
        }
    }
}
