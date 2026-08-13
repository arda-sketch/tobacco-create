package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;

public final class CigarItem extends AbstractSmokingItem {
    public CigarItem(Properties properties) {
        super(properties, SmokingItemState.cigar());
    }
}
