package constants;

import items.Key;
import items.Lantern;

public class Room {
    public static final int ROOM_ONE = 1;
    public static final int ROOM_TWO = 2;

    private static final Lantern ROOM_ONE_LANTERN = new Lantern(true);
    private static final Key ROOM_TWO_KEY = new Key();

    public static Lantern getLantern() {
        return ROOM_ONE_LANTERN;
    }

    public static Key getKey() {
        return ROOM_TWO_KEY;
    }
}
