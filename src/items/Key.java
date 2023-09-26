package items;

public class Key extends Item {
    private String material;

    public Key() {
        super(Item.KEY);

        material = "iron";
    }

    public Key(String material) {
        super(Item.KEY);

        this.material = material;
    }

    @Override
    public void inspect() {
        System.out.println("You look closely at the key and notice that it appears to made from the same material" +
                " as the lock on the door in the room you awoke...");
    }

    @Override
    public String getItemSaveData() {
        return "K," + material;
    }

    @Override
    public String toString() {
        return String.format("A key. It appears to be made of %s.", material);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }

        if(!(o instanceof Key key)) {
            return false;
        }

        return this.material.equalsIgnoreCase(key.material) &&
                this.getName().equalsIgnoreCase(key.getName());
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + this.getName().hashCode();

        return result;
    }
}
