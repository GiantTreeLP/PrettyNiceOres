package gtlp.prettyniceores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gtlp.prettyniceores.blocks.modded.*;
import gtlp.prettyniceores.blocks.vanilla.*;
import gtlp.prettyniceores.common.CommonProxy;
import gtlp.prettyniceores.config.NiceConfig;
import gtlp.prettyniceores.events.OnPlayerLoginEvent;
import gtlp.prettyniceores.generators.NiceOresGenerator;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import gtlp.prettyniceores.recipes.ShapelessOreDictRecipe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
@Mod(modid = Constants.MOD_ID,
        version = Constants.VERSION,
        name = Constants.NAME,
        updateJSON = Constants.UPDATE_URL,
        dependencies = Constants.DEPENDENCIES,
        guiFactory = Constants.GUI_FACTORY,
        acceptedMinecraftVersions = Constants.MC_VERSION)

public class PrettyNiceOres {
    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(Constants.MOD_ID) {
        @SuppressWarnings("ConstantConditions")
        @SideOnly(Side.CLIENT)
        @Override
        @Nonnull
        public Item getTabIconItem() {
            return ItemBlock.getItemFromBlock(Blocks.DIAMOND_ORE);
        }
    };

    private static final Map<String, Block> BLOCK_MAP = Maps.newHashMap();
    private static final Map<String, Item> ITEM_MAP = Maps.newHashMap();
    private static final Map<String, ItemBlock> ITEMBLOCK_MAP = Maps.newHashMap();
    private static final List<IRecipe> recipeList = Lists.newArrayList();

    @SidedProxy(clientSide = "gtlp.prettyniceores.client.ClientProxy", serverSide = "gtlp.prettyniceores.common.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    private static PrettyNiceOres instance;
    private NiceConfig config;

    public static Map<String, Block> getBlockMap() {
        return BLOCK_MAP;
    }

    /**
     * Registers a default item renderer for the inventory
     *
     * @param item to register the item renderer for
     */
    @SideOnly(Side.CLIENT)
    private static void registerItemRenderer(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    /**
     * Adds a smelting recipe for the given {@link Item}, if the item is an {@link ISmeltable} (takes all items, though)
     *
     * @param item to register the smelting recipe for
     */
    private static void addSmeltingRecipe(Item item) {
        if (item instanceof ISmeltable) {
            ItemStack result = ((ISmeltable) item).getSmeltingResult();
            if (result != null) {
                GameRegistry.addSmelting(item, result, ((ISmeltable) item).getSmeltingExp());
            }
        }
    }

    /**
     * Adds a smelting recipe for the given {@link Block}, if the item is an {@link ISmeltable} (takes all blocks, though)
     *
     * @param block to register the smelting recipe for
     */
    private static void addSmeltingRecipe(Block block) {
        if (block instanceof ISmeltable) {
            ItemStack result = ((ISmeltable) block).getSmeltingResult();
            if (result != null) {
                GameRegistry.addSmelting(block, result, ((ISmeltable) block).getSmeltingExp());
            }
        }
    }

    private static void filterBlocksAndApplyConfig(Configuration config) {
        config.setCategoryComment(Constants.CATEGORY_ENABLED_BLOCKS, I18n.format(String.format(Constants.CONFIG_S_CATEGORY, Constants.CATEGORY_ENABLED_BLOCKS)));
        ConcurrentMap<String, Block> newBlockList = Maps.newConcurrentMap();
        PrettyNiceOres.BLOCK_MAP.entrySet().forEach(entry -> {
                    Property prop = config.get(Constants.CATEGORY_ENABLED_BLOCKS, entry.getKey(), "true", I18n.format("config.enabled_blocks.block.comment", entry.getValue().getLocalizedName()), Property.Type.BOOLEAN);
                    if (prop.getBoolean()) {
                        newBlockList.put(entry.getKey(), entry.getValue());
                    }
                }
        );
        PrettyNiceOres.BLOCK_MAP.clear();
        PrettyNiceOres.BLOCK_MAP.putAll(newBlockList);
    }

    /**
     * Adds all replacements for the basic vanilla ores
     */
    private static void addVanillaOres(Map<String, Block> blockMap) {
        blockMap.put(NiceIronOre.NAME, new NiceIronOre());
        blockMap.put(NiceGoldOre.NAME, new NiceGoldOre());
        blockMap.put(NiceCoalOre.NAME, new NiceCoalOre());
        blockMap.put(NiceRedstoneOre.NAME, new NiceRedstoneOre());
        blockMap.put(NiceLapisOre.NAME, new NiceLapisOre());
        blockMap.put(NiceDiamondOre.NAME, new NiceDiamondOre());
        blockMap.put(NiceEmeraldOre.NAME, new NiceEmeraldOre());
        blockMap.put(NiceNetherQuartzOre.NAME, new NiceNetherQuartzOre());
    }

    /**
     * Adds all replacements for mod ores, if they have been created by any other mod.
     *
     * @param blockMap list to add blocks to
     */
    private static void addModOres(Map<String, Block> blockMap) {

        Stream.of(new NiceCopperOre(),
                new NiceTinOre(),
                new NiceSilverOre(),
                new NiceLeadOre(),
                new NiceNickelOre(),
                new NicePlatinumOre(),
                new NiceZincOre(),
                new NiceMercuryOre()).filter(block -> block instanceof IOreDictCompatible && block instanceof INamedBlock)
                .forEach(block -> blockMap.put(block.getName(), block));
    }

    public static NiceConfig getConfig() {
        return instance.config;
    }

    /**
     * Preinitialization of the mod.
     *
     * @param event the preinit event sent by Forge, unused.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        config = new NiceConfig(event.getSuggestedConfigurationFile());

        RecipeSorter.register(Constants.MOD_ID + ":shapelessoredict", ShapelessOreDictRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        addVanillaOres(BLOCK_MAP);
        addModOres(BLOCK_MAP);

        filterBlocksAndApplyConfig(config);
        addItems();

        BLOCK_MAP.forEach((name, block) -> {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            ITEMBLOCK_MAP.put(block instanceof INamedBlock ? ((INamedBlock) block).getName() : block.getRegistryName().getResourcePath(), itemBlock);
            GameRegistry.register(block);
        });
        ITEMBLOCK_MAP.forEach((name, item) -> {
            GameRegistry.register(item);
            Block block = item.getBlock();
            if (block instanceof IOreDictCompatible) {
                OreDictionary.registerOre(((IOreDictCompatible) block).getOreDictType(), item);
            }
            addSmeltingRecipe(block);
        });
        ITEM_MAP.forEach((name, item) -> {
            GameRegistry.register(item);
            if (item instanceof IOreDictCompatible) {
                OreDictionary.registerOre(((IOreDictCompatible) item).getOreDictType(), item);
            }
            addSmeltingRecipe(item);
        });
        MinecraftForge.EVENT_BUS.register(new OnPlayerLoginEvent());
        LOGGER.info("PreInit done.");
        config.save();
    }

    /**
     * Adds items.
     */
    private void addItems() {

    }

    /**
     * Initialization of the mod.
     *
     * @param event the init event sent by Forge, used to determine whether or not to register renderers
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ITEM_MAP.forEach((name, item) -> {
            if (event.getSide().isClient()) {
                registerItemRenderer(item);
            }
        });
        ITEMBLOCK_MAP.forEach((name, item) -> {
            if (event.getSide().isClient()) {
                registerItemRenderer(item);
            }
        });
        BLOCK_MAP.entrySet().stream().filter(entry -> entry.getValue() instanceof IOreDictCompatible).forEach(entry -> {
            IOreDictCompatible block = (IOreDictCompatible) entry.getValue();
            GameRegistry.addRecipe(new ShapelessOreDictRecipe(block.getOreDictType(), block.getOreDictType()));
        });
        recipeList.forEach(GameRegistry::addRecipe);

        recipeList.forEach(GameRegistry::addRecipe);
        LOGGER.info("Init done.");
    }

    /**
     * Postinitialization of the mod.
     *
     * @param event the postinit event sent by Forge, unused.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new NiceOresGenerator(), Short.MAX_VALUE);
        LOGGER.info("PostInit done.");
    }
}
