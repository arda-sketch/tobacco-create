package com.createtobacco.item;

import net.minecraft.world.item.Item;

/**
 * Base item type for finished cigarettes.
 *
 * <p>Smoking behavior is intentionally deferred to a later phase.</p>
 */
public final class CigaretteItem extends Item {
    public CigaretteItem(Properties properties) {
        super(properties);
    }
}
