package com.github.avisagie.uuid;

import java.util.UUID;

class UUIDLexicographicComparator implements java.util.Comparator<java.util.UUID> {

    public static final UUIDLexicographicComparator instance = new UUIDLexicographicComparator();

    @Override
    public int compare(UUID o1, UUID o2) {
        return UUIDUtil.lexicographicCompare(o1.getMostSignificantBits(), o1.getLeastSignificantBits(), o2.getMostSignificantBits(), o2.getLeastSignificantBits());
    }
}
