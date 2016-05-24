package gtlp.prettyniceores.generators;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceOresGenerator implements IWorldGenerator {

    private static final int CHUNK_SIZE = 16;
    private static final int MAX_HEIGHT = 256;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = chunkProvider.getLoadedChunk(chunkX, chunkZ);
        PrettyNiceOres.blockList.entrySet().parallelStream().forEach(entry -> {
            if (entry.getValue() instanceof IOreDictCompatible) {
                OreDictionary.getOres(((IOreDictCompatible) entry.getValue()).getOreDictType()).parallelStream().filter(itemStack ->
                        !itemStack.isItemEqual(new ItemStack(entry.getValue()))).forEach(itemStack ->
                        IntStream.range(0, MAX_HEIGHT).parallel().forEach(y ->
                                IntStream.range(0, CHUNK_SIZE).parallel().forEach(z ->
                                        IntStream.range(0, CHUNK_SIZE).parallel().forEach(x -> {
                                            BlockPos blockPos = new BlockPos(x, y, z);
                                            if ((itemStack.isItemEqual(new ItemStack(chunk.getBlockState(blockPos).getBlock())))) {
                                                synchronized (NiceOresGenerator.class) {
                                                    chunk.setBlockState(blockPos, entry.getValue().getDefaultState());
                                                }
                                            }
                                        }))));
            }
        });
    }

    private void runGenerator(WorldGenerator generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
        if (minHeight < 0 || maxHeight > MAX_HEIGHT || minHeight > maxHeight) {
            throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");
        }

        int heightDiff = maxHeight - minHeight + 1;
        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunk_X * CHUNK_SIZE + rand.nextInt(CHUNK_SIZE);
            int y = minHeight + rand.nextInt(heightDiff);
            int z = chunk_Z * CHUNK_SIZE + rand.nextInt(CHUNK_SIZE);
            generator.generate(world, rand, new BlockPos(x, y, z));
        }
    }
}
