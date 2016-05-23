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

    private static final int CHUNKSIZE = 16;
    private static final int MAX_HEIGHT = 256;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = chunkProvider.getLoadedChunk(chunkX, chunkZ);
        switch (world.provider.getDimension()) {
            case 0:
                PrettyNiceOres.blockList.entrySet().parallelStream().forEach(entry -> {
                    if (entry.getValue() instanceof IOreDictCompatible) {
                        OreDictionary.getOres(((IOreDictCompatible) entry.getValue()).getOreDictType()).forEach(itemStack -> {
                            IntStream.rangeClosed(0, world.getActualHeight()).parallel().forEach(y ->
                                    IntStream.rangeClosed(0, CHUNKSIZE).parallel().forEach(z ->
                                            IntStream.rangeClosed(0, CHUNKSIZE).parallel().forEach(x -> {
                                                BlockPos blockPos = new BlockPos(x, y, z);
                                                if ((itemStack.isItemEqual(new ItemStack(chunk.getBlockState(blockPos).getBlock())))) {
                                                    synchronized (this) {
                                                        chunk.setBlockState(blockPos, entry.getValue().getDefaultState());
                                                    }
                                                }
                                            })));
                        });
                    }
                });
                break;
        }
    }

    private void runGenerator(WorldGenerator generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
        if (minHeight < 0 || maxHeight > MAX_HEIGHT || minHeight > maxHeight) {
            throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");
        }

        int heightDiff = maxHeight - minHeight + 1;
        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunk_X * CHUNKSIZE + rand.nextInt(CHUNKSIZE);
            int y = minHeight + rand.nextInt(heightDiff);
            int z = chunk_Z * CHUNKSIZE + rand.nextInt(CHUNKSIZE);
            generator.generate(world, rand, new BlockPos(x, y, z));
        }
    }
}
