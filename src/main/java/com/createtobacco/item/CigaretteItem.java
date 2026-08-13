package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;

/**
 * Finished cigarette with the common five-puff smoking profile.
 */
public final class CigaretteItem extends AbstractSmokingItem {
    public CigaretteItem(Properties properties) {
        super(properties, SmokingItemState.cigarette());
    }
}
