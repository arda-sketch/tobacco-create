package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import com.createtobacco.item.CigaretteItem;
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

    public static final DeferredItem<CigaretteItem> MARLBORE_RED = registerCigarette("marlbore_red");
    public static final DeferredItem<CigaretteItem> WINSTONE_BLUE = registerCigarette("winstone_blue");
    public static final DeferredItem<CigaretteItem> CREPERFIELD = registerCigarette("creperfield");
    public static final DeferredItem<CigaretteItem> CRAFTMEL = registerCigarette("craftmel");
    public static final DeferredItem<CigaretteItem> CHUNKMAN = registerCigarette("chunkman");
    public static final DeferredItem<CigaretteItem> KEND = registerCigarette("kend");
    public static final DeferredItem<CigaretteItem> PIGLIAMENT = registerCigarette("pigliament");
    public static final DeferredItem<CigaretteItem> ROTHMINES = registerCigarette("rothmines");
    public static final DeferredItem<CigaretteItem> BEDROMORKANAL = registerCigarette("bedromorkanal");

    public static final DeferredItem<Item> FERMENTED_HAVANA_TOBACCO_BUNDLE =
            ITEMS.registerSimpleItem("fermented_havana_tobacco_bundle");
    public static final DeferredItem<Item> MIXED_FERMENTED_TOBACCO_BUNDLE =
            ITEMS.registerSimpleItem("mixed_fermented_tobacco_bundle");
    public static final DeferredItem<Item> CIGAR_FILLER = ITEMS.registerSimpleItem("cigar_filler");
    public static final DeferredItem<Item> MIXED_CIGAR_FILLER = ITEMS.registerSimpleItem("mixed_cigar_filler");
    public static final DeferredItem<Item> CIGAR_WRAPPER = ITEMS.registerSimpleItem("cigar_wrapper");
    public static final DeferredItem<Item> MINECRISTO_NO_1 = ITEMS.registerSimpleItem("minecristo_no_1");
    public static final DeferredItem<Item> COBBLIBA_MADURO = ITEMS.registerSimpleItem("cobbliba_maduro");

    public static final DeferredItem<Item> INCOMPLETE_CIGARETTE = ITEMS.registerSimpleItem("incomplete_cigarette");
    public static final DeferredItem<Item> INCOMPLETE_CIGAR = ITEMS.registerSimpleItem("incomplete_cigar");

    public static final List<DeferredItem<? extends Item>> CREATIVE_TAB_ITEMS = List.of(
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
            BEDROMORKANAL_BLEND,
            MARLBORE_RED,
            WINSTONE_BLUE,
            CREPERFIELD,
            CRAFTMEL,
            CHUNKMAN,
            KEND,
            PIGLIAMENT,
            ROTHMINES,
            BEDROMORKANAL,
            FERMENTED_HAVANA_TOBACCO_BUNDLE,
            MIXED_FERMENTED_TOBACCO_BUNDLE,
            CIGAR_FILLER,
            MIXED_CIGAR_FILLER,
            CIGAR_WRAPPER,
            MINECRISTO_NO_1,
            COBBLIBA_MADURO
    );

    private ModItems() {
    }

    private static DeferredItem<CigaretteItem> registerCigarette(String name) {
        return ITEMS.registerItem(name, CigaretteItem::new);
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
