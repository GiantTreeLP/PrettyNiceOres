package gtlp.prettyniceores.generators;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceOresGenerator implements IWorldGenerator {

    private static final int CHUNKSIZE = 16;
    private static final int MAX_HEIGHT = 256;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimension()) {
            case 0:
                for (int x = chunkX * CHUNKSIZE; x < chunkX * CHUNKSIZE + CHUNKSIZE; x++) {
                    for (int z = chunkZ * CHUNKSIZE; z < chunkZ * CHUNKSIZE + CHUNKSIZE; z++) {
                        for (int y = 0; y < MAX_HEIGHT; y++) {
                            BlockPos blockPos = new BlockPos(x, y, z);
                            PrettyNiceOres.blockList.forEach((name, block) -> {
                                if (block instanceof IOreDictCompatible) {
                                    Block blockIn = world.getBlockState(blockPos).getBlock();
                                    OreDictionary.getOres(((IOreDictCompatible) block).getOreDictType()).forEach(itemStack -> {
                                        if (ItemStack.areItemStacksEqual(itemStack, new ItemStack(blockIn))) {
                                            world.setBlockState(blockPos, block.getDefaultState());
                                        }
                                    });
                                }
                            });

                        }
                    }
                }
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
