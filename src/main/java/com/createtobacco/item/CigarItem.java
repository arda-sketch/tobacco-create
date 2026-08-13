package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;

public final class CigarItem extends AbstractSmokingItem {
    public CigarItem(Properties properties) {
        super(properties, SmokingItemState.cigar(), 1.4F, 8_400, 0.9F);
    }
}
