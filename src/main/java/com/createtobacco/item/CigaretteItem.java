package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;

/**
 * Finished cigarette with the common five-puff smoking profile.
 */
public final class CigaretteItem extends AbstractSmokingItem {
    public CigaretteItem(Properties properties) {
        super(properties, SmokingItemState.cigarette(), 0.9F, 6_000, 0.6F);
    }
}
