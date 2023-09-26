import combat.CombatType;
import constants.Command;
import constants.Environment;
import constants.Room;
import constants.State;
import items.Item;
import items.Key;
import items.Lantern;
import mobs.Enemy;
import player.Player;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class Game {
    /* -------- player.Player Code --------*/
    private static Player player;

    /* -------- Game Code --------*/
    private static final String PLAYER_FILE_LOCATION = "src/files/player_data.csv";
    private static final String INVENTORY_FILE_LOCATION = "src/files/inventory_data.csv";

    private static final Scanner input = new Scanner(System.in);
    private static final Map<String, Boolean> GAME_STATE = new HashMap<>();

    public static void main(String[] args) {
        printWelcome();
        System.out.println("Your adventure now begins...");
        System.out.println("====================================");

        GAME_STATE.put(State.CHEST_OPENED, Boolean.FALSE);

        /* GAME LOOP */
        System.out.print("Enter a command to play, enter 'help' to see all commands: ");
        String command = "";
        String root = "";
        String item = "";
        String[] commandComponents;

        while(true) {
            command = input.nextLine(); // e.g., "open left door"
            commandComponents = command.split(" ");
            root = commandComponents[0]; // gets first word of the command (e.g., "open")
            item = ""; // need to make sure we clear out any old items

            for(int i = 1; i < commandComponents.length; i++) {
                item = item + commandComponents[i] + " ";
            } // "left door " <- notice the space at the end!
            item = item.stripTrailing(); // "left door" <- no more space

            boolean shouldExit = processCommand(root, item);
            if(shouldExit) {
                break;
            }
        }

        System.out.println("Thanks for playing!");
//        deletePreviousSaveData();
    }

    private static void printWelcome() {
        System.out.println("Welcome to Dungeon Crawler!");

        // TODO: Validate that the user has entered a number and that is it either 1 or 2
        int choice = -1; // Initializing the conditional variable

        while (choice == -1) {
            System.out.print("Would you like to (1) start a new game or (2) load an existing game? ");
            try {
                choice = input.nextInt();
                if (choice == 1 || choice == 2){
                    break;
                }else {
                    input.nextLine();
                    System.out.println("Invalid Number. Please enter (1) or (2)!");
                    choice = -1;
                }
            } catch (InputMismatchException e) {
                input.nextLine();
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        input.nextLine(); // KEEP THIS! Needed because of the Scanner bug after using input.nextInt()
        if(choice == 1) {
            newGame();
        } else {
            load();
        }
    }

    private static void newGame() {
        deletePreviousSaveData();

        System.out.print("What is your name brave adventurer? ");

        String name = input.nextLine();
        player = new Player(name, Room.ROOM_ONE);

        System.out.println("Good luck, " + player.getName() + "!\n");
    }

    private static void deletePreviousSaveData() {
        File playerData = new File(PLAYER_FILE_LOCATION);
        if(playerData.exists()) {
            if(!playerData.delete()) {
                System.err.println("Error deleting state player save data");
            }
        }

        File inventoryData = new File(INVENTORY_FILE_LOCATION);
        if(inventoryData.exists()) {
            if(!inventoryData.delete()) {
                System.err.println("Error deleting state inventory save data");
            }
        }
    }

    private static boolean processCommand(String root, String item) {
        if(root.equalsIgnoreCase(Command.HELP)) {
            help();
        } else if(root.equalsIgnoreCase(Command.EXIT)) {
            return true;
        } else if(root.equalsIgnoreCase(Command.SAVE)) {
            save();
        } else if(root.equalsIgnoreCase(Command.STATS)) {
            player.printStats();
        } else if(root.equalsIgnoreCase(Command.LOOK)) {
            look();
        } else if(root.equalsIgnoreCase(Command.INVENTORY)) {
            player.printInventory();
        } else if(root.equalsIgnoreCase(Command.OPEN)) {
            return open(item);
        } else if(root.equalsIgnoreCase(Command.TAKE)) {
            take(item);
        } else if(root.equalsIgnoreCase(Command.INSPECT)) {
            inspect(item);
        } else {
            System.out.printf("'%s %s' is an invalid command.%n", root, item);
        }

        if(Math.random() < 0.2) {
            return fight();
        } else {
            return false;
        }
    }

    private static void help() {
        System.out.println("The following commands are available for you to use:");
        System.out.format("\t%-15s - %s.%n", "exit", "exits the game");
        System.out.format("\t%-15s - %s.%n", "save","saves the current game");
        System.out.format("\t%-15s - %s.%n", "stats","lists your characters current stats");
        System.out.format("\t%-15s - %s.%n", "look", "describes the room you are currently in");
        System.out.printf("\t%-15s - %s.%n", "inventory", "lists all the items in your inventory");
        System.out.format("\t%-15s - %s.%n", "open <item>", "try to open the item you specify");
        System.out.format("\t%-15s - %s.%n", "take <item>", "puts the specified item in your inventory");
        System.out.format("\t%-15s - %s.%n", "inspect <item>", "closely inspect the specified item");

    }

    private static void look() {
        // Different things are seen in different rooms
        if(player.getPlayerLocation() == Room.ROOM_ONE) {
            if(player.hasItem(Item.LANTERN)) {
                System.out.println("You hold up the lantern and see the hook on which the lantern used to hang.");
            } else {
                System.out.println("You are in a dark room with a single lantern on the wall in front of you.");

            }
            System.out.println("You see a door on the left with a large lock and a " +
                    "door on the right with no visible lock.");
        } else {
            if(player.hasItem(Item.LANTERN)) {
                System.out.println("You hold the lantern up and see a chest at the back of the room." +
                        " It doesn't have a visible lock.");
            } else {
                System.out.println("The room is pitch black and you cannot see anything..." +
                        "maybe you should go back to the main chamber? The door is behind you.");
            }
        }
    }

    private static boolean open(String item) {
        // Different items for different rooms
        if(player.getPlayerLocation() == Room.ROOM_ONE) {
            if(item.equalsIgnoreCase(Environment.LEFT_DOOR)) {
                if(player.hasItem(Item.KEY)) {
                    System.out.println("You insert the key and the lock opens! " +
                            "You see a light at the end of the hallway and escape the dungeon!");
                    deletePreviousSaveData();
                    return true;
                } else {
                    System.out.println("This door is securely locked. There is a keyhole in the lock," +
                            " so maybe the key is around here somewhere...");
                }
            } else if(item.equalsIgnoreCase(Environment.RIGHT_DOOR)) {
                System.out.println("The door opens easily and you walk through.");
                player.setPlayerLocation(Room.ROOM_TWO); // Change room location after going through a door
            } else {
                System.out.println("Hmm, I don't see anything like that around here." +
                        " Please look around again.");
            }
        } else {
            if(item.equalsIgnoreCase(Environment.BACK_DOOR) || item.equalsIgnoreCase(Environment.DOOR)) {
                System.out.println("You turn around and go back to the main chamber.");
                player.setPlayerLocation(Room.ROOM_ONE);
            } else if(item.equalsIgnoreCase(Environment.CHEST) && player.hasItem(Item.LANTERN)) {
                if(!GAME_STATE.get(State.CHEST_OPENED)) {
                    GAME_STATE.put(State.CHEST_OPENED, Boolean.TRUE);
                    System.out.println("There is a key inside the chest!");
                } else {
                    System.out.println("The chest is already opened.");
                }
            } else {
                System.out.println("Hmm, I don't see anything like that around here." +
                        " Please look around again.");
            }
        }

        return false;
    }

    private static void take(String item) {
        // Different items for different rooms
        if(player.getPlayerLocation() == Room.ROOM_ONE) {
            if(item.equalsIgnoreCase(Item.LANTERN)) {
                if(player.takeItem(Room.getLantern())) {
                    System.out.println("You take the lantern off the wall. Let there be light!");
                } else {
                    System.out.println("You have this item already!");
                }
            } else {
                System.out.println("Hmm, I don't see anything like that around here." +
                        " Please look around again.");
            }
        } else {
            if(GAME_STATE.get(State.CHEST_OPENED) && item.equalsIgnoreCase(Item.KEY)) {
                if(player.takeItem(Room.getKey())) {
                    System.out.println("You take the key and put it in your pocket.");
                } else {
                    System.out.println("You have this item already!");
                }
            } else {
                System.out.println("Hmm, I don't see anything like that around here." +
                        " Please look around again.");
            }
        }
    }

    private static void inspect(String item) {
        if(item.equalsIgnoreCase(Item.LANTERN)) {
            Item playerItem = player.getItem(Item.LANTERN);

            if(playerItem != null) {
                playerItem.inspect();
            } else if(player.getPlayerLocation() == Room.ROOM_ONE) {
                System.out.println("You lean close to the lantern and singe your eyebrows");
            } else {
                System.out.println("Hmm, I don't see anything like that around here. Please look around again");
            }
        } else if(item.equalsIgnoreCase(Item.KEY)) {
            Item playerItem = player.getItem(Item.KEY);

            if(playerItem != null) {
                playerItem.inspect();
            } else {
                System.out.println("Hmm, I don't see anything like that around here. Please look around again");
            }
        } else {
            System.out.println("You do not have this item in your inventory");
        }
    }

    private static void load() {
        // TODO: Load from the player's saved data
        try(Scanner inputStreamPlayer = new Scanner(new BufferedReader(new FileReader(PLAYER_FILE_LOCATION)))){
            inputStreamPlayer.useDelimiter(",");
            String name = inputStreamPlayer.next();
            int playerLoc = inputStreamPlayer.nextInt();
            int intelligenceLvl = inputStreamPlayer.nextInt();
            int strengthLvl = inputStreamPlayer.nextInt();
            int magicLvl = inputStreamPlayer.nextInt();
            int healthPts = inputStreamPlayer.nextInt();
            player = new Player(name,playerLoc,intelligenceLvl,strengthLvl,magicLvl,healthPts);
        } catch(FileNotFoundException e){
            System.out.println("Player File Not Found! Starting a new game!");
            newGame();
            return;
        }

        // TODO: Load from the saved inventory data
        try(Scanner inputStreamInventory = new Scanner(new BufferedReader(new FileReader(INVENTORY_FILE_LOCATION)))){
            String line;
            while (inputStreamInventory.hasNext()){
                line = inputStreamInventory.nextLine();
                String[] lineComponents = line.split(",");

                if (lineComponents[0].equalsIgnoreCase("L")) {
                    Boolean isLit =  Boolean.parseBoolean(lineComponents[1]);
                    player.takeItem(new Lantern(isLit));
                    System.out.println("Lantern Added to Inventory");
                } else if (lineComponents[0].equalsIgnoreCase("K")) {
                    String material = lineComponents[1];
                    player.takeItem(new Key(material));
                    System.out.println("Key added to inventory");
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("Inventory File not found! Starting a new game!");
            newGame();
            return;
        }

        // TODO: Print a message saying the data has been loaded
        System.out.println("Save data loaded!");
    }

    private static void save() {
        try(BufferedWriter outputStreamPlayer = new BufferedWriter(new FileWriter(PLAYER_FILE_LOCATION));
            BufferedWriter outputStreamInventory = new BufferedWriter(new FileWriter(INVENTORY_FILE_LOCATION))) {
            // TODO: Write the player's save string to the text file
            outputStreamPlayer.write(player.getPlayerSaveData());

            // TODO: Write the inventory's save string to the text file
            outputStreamInventory.write(player.getPlayerInventorySaveData());

            // TODO: Print a message that their progress has been saved
            System.out.println("Save Created!");
        } catch (IOException e){
            System.err.println("IO Exception Reached!");
        }
    }

    private static boolean fight() {
        Enemy enemy = new Enemy();
        System.out.println("An enemy suddenly appears before you!");
        System.out.println("The enemy surprises you and attacks first!");

        int damageToDeal;
        int playerDefenseInput, playerAttackInput;
        CombatType playerDefenseType, playerAttackType;
        boolean successfulDefense;
        boolean fatality;
        while (true) {
            // Enemy's turn
            damageToDeal = enemy.attack(CombatType.DEFAULT);
            System.out.println("What will you defend with? Please enter 1, 2, or 3.");
            System.out.println("\t1: Intelligence");
            System.out.println("\t2: Strength");
            System.out.println("\t3: Magic");

            playerDefenseInput = input.nextInt();
            input.nextLine();
            while(playerDefenseInput < 1 || playerDefenseInput > 3) {
                System.out.println("Invalid defence type. Please enter a number between 1 and 3.");
                playerDefenseInput = input.nextInt();
                input.nextLine();
            }

            switch(playerDefenseInput) {
                case 1:
                    playerDefenseType = CombatType.INTELLIGENCE;
                    break;
                case 2:
                    playerDefenseType = CombatType.STRENGTH;
                    break;
                case 3:
                    playerDefenseType = CombatType.MAGIC;
                    break;
                default:
                    playerDefenseType = CombatType.DEFAULT;
                    break;
            }

            successfulDefense = player.defend(enemy.getAttackLevel(), playerDefenseType);
            if(!successfulDefense) {
                System.out.println("The enemy lands a hit!");
                fatality = player.takeDamage(damageToDeal);
                if(fatality) {
                    System.out.println("You have succumbed to the enemy's power...");
                    return true;
                }
            } else {
                System.out.println("You evade the enemy's blow!");
            }

            // Player's turn
            System.out.println("You move to the offense!");
            System.out.println("What will you use to attack?");

            System.out.println("\t1: Intelligence");
            System.out.println("\t2: Strength");
            System.out.println("\t3: Magic");

            playerAttackInput = input.nextInt();
            input.nextLine();
            while(playerAttackInput < 1 || playerAttackInput > 3) {
                System.out.println("Invalid defence type. Please enter a number between 1 and 3.");
                playerAttackInput = input.nextInt();
                input.nextLine();
            }

            switch(playerAttackInput) {
                case 1:
                    playerAttackType = CombatType.INTELLIGENCE;
                    break;
                case 2:
                    playerAttackType = CombatType.STRENGTH;
                    break;
                case 3:
                    playerAttackType = CombatType.MAGIC;
                    break;
                default:
                    playerAttackType = CombatType.DEFAULT;
                    break;
            }

            damageToDeal = player.attack(playerAttackType);

            successfulDefense = enemy.defend(player.getAttackLevel(playerAttackType), CombatType.DEFAULT);
            if(!successfulDefense) {
                System.out.println("You land a hit on the enemy!");
                fatality = enemy.takeDamage(damageToDeal);
                if(fatality) {
                    System.out.println("You have defeated the enemy!!");
                    break;
                }
            } else {
                System.out.println("The enemy is too quick and dodges the attack!");
            }
        }

        return false;
    }
}
