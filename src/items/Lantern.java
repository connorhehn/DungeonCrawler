package items;

public class Lantern extends Item {
    private boolean isLit;

    private Lantern() {
        super(Item.LANTERN);
    }

    public Lantern(boolean isLit) {
        super(Item.LANTERN);

        this.isLit = isLit;
    }

    @Override
    public void inspect() {
        if(isLit) {
            System.out.println("You hold the lantern up to your eyes and are momentarily blinded");
        } else {
            System.out.println("You hold the lantern up to your face and notice the oil smells faintly of citrus");
        }
    }

    @Override
    public String getItemSaveData() {
        return "L," + isLit;
    }

    @Override
    public String toString() {
        return String.format("A lantern. The lantern is %s", isLit ? "lit." : "not lit.");
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }

        if(!(o instanceof Lantern lantern)) {
            return false;
        }

        return this.isLit == lantern.isLit &&
                this.getName().equalsIgnoreCase(lantern.getName());
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(isLit);
        result = 31 * result + this.getName().hashCode();

        return result;
    }
}
