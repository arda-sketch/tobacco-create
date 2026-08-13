package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateTobacco.MOD_ID);

    public static final DeferredItem<Item> VIRGINIA_SEEDS = ITEMS.registerItem(
            "virginia_seeds", properties -> new ItemNameBlockItem(ModBlocks.VIRGINIA_TOBACCO.get(), properties));
    public static final DeferredItem<Item> BURLEY_SEEDS = ITEMS.registerItem(
            "burley_seeds", properties -> new ItemNameBlockItem(ModBlocks.BURLEY_TOBACCO.get(), properties));
    public static final DeferredItem<Item> HAVANA_SEEDS = ITEMS.registerItem(
            "havana_seeds", properties -> new ItemNameBlockItem(ModBlocks.HAVANA_TOBACCO.get(), properties));

    public static final DeferredItem<Item> FRESH_VIRGINIA_LEAF = ITEMS.registerSimpleItem("fresh_virginia_leaf");
    public static final DeferredItem<Item> FRESH_BURLEY_LEAF = ITEMS.registerSimpleItem("fresh_burley_leaf");
    public static final DeferredItem<Item> FRESH_HAVANA_LEAF = ITEMS.registerSimpleItem("fresh_havana_leaf");

    public static final DeferredItem<Item> CURED_VIRGINIA_LEAF = ITEMS.registerSimpleItem("cured_virginia_leaf");
    public static final DeferredItem<Item> CURED_BURLEY_LEAF = ITEMS.registerSimpleItem("cured_burley_leaf");
    public static final DeferredItem<Item> CURED_HAVANA_LEAF = ITEMS.registerSimpleItem("cured_havana_leaf");

    public static final DeferredItem<Item> CUT_VIRGINIA_TOBACCO = ITEMS.registerSimpleItem("cut_virginia_tobacco");
    public static final DeferredItem<Item> CUT_BURLEY_TOBACCO = ITEMS.registerSimpleItem("cut_burley_tobacco");
    public static final DeferredItem<Item> CUT_HAVANA_TOBACCO = ITEMS.registerSimpleItem("cut_havana_tobacco");

    public static final DeferredItem<Item> CIGARETTE_PAPER = ITEMS.registerSimpleItem("cigarette_paper");
    public static final DeferredItem<Item> CIGARETTE_FILTER = ITEMS.registerSimpleItem("cigarette_filter");

    public static final DeferredItem<Item> MARLBORE_RED_BLEND = ITEMS.registerSimpleItem("marlbore_red_blend");
    public static final DeferredItem<Item> WINSTONE_BLUE_BLEND = ITEMS.registerSimpleItem("winstone_blue_blend");
    public static final DeferredItem<Item> CREPERFIELD_BLEND = ITEMS.registerSimpleItem("creperfield_blend");
    public static final DeferredItem<Item> CRAFTMEL_BLEND = ITEMS.registerSimpleItem("craftmel_blend");
    public static final DeferredItem<Item> CHUNKMAN_BLEND = ITEMS.registerSimpleItem("chunkman_blend");
    public static final DeferredItem<Item> KEND_BLEND = ITEMS.registerSimpleItem("kend_blend");
    public static final DeferredItem<Item> PIGLIAMENT_BLEND = ITEMS.registerSimpleItem("pigliament_blend");
    public static final DeferredItem<Item> ROTHMINES_BLEND = ITEMS.registerSimpleItem("rothmines_blend");
    public static final DeferredItem<Item> BEDROMORKANAL_BLEND = ITEMS.registerSimpleItem("bedromorkanal_blend");

    public static final List<DeferredItem<Item>> CREATIVE_TAB_ITEMS = List.of(
            VIRGINIA_SEEDS,
            BURLEY_SEEDS,
            HAVANA_SEEDS,
            FRESH_VIRGINIA_LEAF,
            FRESH_BURLEY_LEAF,
            FRESH_HAVANA_LEAF,
            CURED_VIRGINIA_LEAF,
            CURED_BURLEY_LEAF,
            CURED_HAVANA_LEAF,
            CUT_VIRGINIA_TOBACCO,
            CUT_BURLEY_TOBACCO,
            CUT_HAVANA_TOBACCO,
            CIGARETTE_PAPER,
            CIGARETTE_FILTER,
            MARLBORE_RED_BLEND,
            WINSTONE_BLUE_BLEND,
            CREPERFIELD_BLEND,
            CRAFTMEL_BLEND,
            CHUNKMAN_BLEND,
            KEND_BLEND,
            PIGLIAMENT_BLEND,
            ROTHMINES_BLEND,
            BEDROMORKANAL_BLEND
    );

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
