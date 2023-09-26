package items;

public abstract class Item {
    public static final String LANTERN = "lantern";
    public static final String KEY = "key";

    private String name;

    private Item() {}

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract void inspect();

    public abstract String getItemSaveData();
}
