package player;

import combat.CombatType;
import combat.Combatable;
import items.Item;

import java.util.HashSet;
import java.util.Set;

public class Player implements Combatable {
    private String name;
    private int playerLoc; // Needed to keep track of which room the player is in
    private Set<Item> items;
    private int intelligenceLvl;
    private int strengthLvl;
    private int magicLvl;
    private int healthPoints;

    private Player() {}

    public Player(String name, int playerLocation) {
        this.name      = name;
        items          = new HashSet<>();
        this.playerLoc = playerLocation;
        generateStats();
    }

    public Player(String name, int playerLoc, int intelligenceLvl, int strengthLvl, int magicLvl, int healthPoints) {
        this.name = name;
        this.playerLoc = playerLoc;
        this.items = new HashSet<>();
        this.intelligenceLvl = intelligenceLvl;
        this.strengthLvl = strengthLvl;
        this.magicLvl = magicLvl;
        this.healthPoints = healthPoints;
    }

    public String getName() {
        return this.name;
    }

    public int getPlayerLocation() {
        return this.playerLoc;
    }

    public void setPlayerLocation(int playerLocation) {
        this.playerLoc = playerLocation;
    }

    public int getAttackLevel(CombatType combatType) {
        switch(combatType) {
            case INTELLIGENCE:
                return intelligenceLvl;
            case STRENGTH:
                return strengthLvl;
            case MAGIC:
                return magicLvl;
            default:
                return 1;
        }
    }

    public boolean hasItem(String itemName) {
        for(Item item : items) {
            if(item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }

        return false;
    }

    public boolean takeItem(Item item) {
        return items.add(item);
    }

    public Item getItem(String itemName) {
        for(Item item : items) {
            if(item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }

        return null;
    }

    public void printInventory() {
        if(items.size() == 0) {
            System.out.println("You have no items in your inventory");
        } else {
            System.out.println("These are the items in your inventory:");

            for (Item item : items) {
                System.out.println("\t" + item);
            }
        }
    }

    public void generateStats() {
        System.out.println("Your character levels are now being generated...");

        intelligenceLvl = (int)(Math.random() * 10 + 1);
        strengthLvl = (int)(Math.random() * 10 + 1);
        magicLvl = (int)(Math.random() * 10 + 1);
        healthPoints = (int)(Math.random() * 11 + 20);

        System.out.printf("Intelligence: %d. Strength: %d. Magic %d. HP: %d.%n",
                intelligenceLvl,
                strengthLvl,
                magicLvl,
                healthPoints);
    }

    public String getPlayerSaveData() {
        return name + "," +
                playerLoc + "," +
                intelligenceLvl + "," +
                strengthLvl + "," +
                magicLvl + "," +
                healthPoints;
    }

    public String getPlayerInventorySaveData() {
        StringBuilder saveString = new StringBuilder();

        for(Item item : items) {
            saveString.append(item.getItemSaveData()).append("\n");
        }

        return saveString.isEmpty() ? "" : saveString.toString();
    }

    public void printStats() {
        System.out.printf("Intelligence: %d. Strength: %d. Magic %d. HP: %d.%n",
                intelligenceLvl,
                strengthLvl,
                magicLvl,
                healthPoints);
    }

    @Override
    public int attack(CombatType combatType) {
        switch(combatType) {
            case INTELLIGENCE:
                return (int)(Math.random() * intelligenceLvl + 1);
            case STRENGTH:
                return (int)(Math.random() * strengthLvl + 1);
            case MAGIC:
                return (int)(Math.random() * magicLvl + 1);
            default:
                return 1;
        }
    }

    @Override
    public boolean defend(int attackLevel, CombatType combatType) {
        switch(combatType) {
            case INTELLIGENCE:
                return intelligenceLvl >= attackLevel;
            case STRENGTH:
                return strengthLvl >= attackLevel;
            case MAGIC:
                return magicLvl >= attackLevel;
            default:
                return false;
        }
    }

    @Override
    public boolean takeDamage(int damage) {
        healthPoints = Math.max(healthPoints - damage, 0);
        return healthPoints == 0;
    }
}
