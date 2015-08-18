package asv.uuid;

import java.util.UUID;

public class UUIDLexicographicComparator implements java.util.Comparator<java.util.UUID> {

    public static final UUIDLexicographicComparator instance = new UUIDLexicographicComparator();

    @Override
    public int compare(UUID o1, UUID o2) {
        return UUIDUtil.compare(o1.getMostSignificantBits(), o1.getLeastSignificantBits(), o2.getMostSignificantBits(), o2.getLeastSignificantBits());
    }
}
