package com.createtobacco.item;

import com.createtobacco.smoking.SmokingProduct;

/**
 * Finished cigarette with the common five-puff smoking profile.
 */
public final class CigaretteItem extends AbstractSmokingItem {
    public CigaretteItem(Properties properties, SmokingProduct product) {
        super(properties, product);
    }
}
