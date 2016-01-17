package hsenfow.pongh;

import java.io.Serializable;

public class Player implements Serializable{

    // The name of the player's save file
    private static final String SAVE_FILE = "save.txt";

    // The number of coins the player currently has
    private long coins = 0;

    /**
     * Creates an object representing the player.
     */
    private Player(){
	    save();
    }

	/**
	 * Increases the coin count by the specified amount.
	 */
	public void increaseCoins(int increase){
	    coins += increase;
    }

    /**
     * Saves the player's current progress.
     */
    public void save(){
	    // Write the this object to a file
	    Utils.writeObjects(SAVE_FILE, this);
    }

    /**
     * Attempts to load the player's progress from the save file. If a save file cannot be found or it cannot be loaded,
     * then a new, blank player is created.
     * @return The loaded/created player object.
     */
    public static Player load(){
	    // Try to load the player from the save file
	    Object[] loadedObjects = Utils.readObjects(SAVE_FILE);

	    // If an object was loaded from the file, make sure it's a player object and return it
	    if(loadedObjects != null && loadedObjects.length > 0){
		    try{
			    // Try to cast the loaded object into a player object
			    return (Player)loadedObjects[0];
		    } catch(ClassCastException cce){
			    Utils.log("Invalid save file - deleting");
			    Utils.deleteFile(SAVE_FILE);
		    }
	    }

	    // We couldn't load the save file, so create a new player
	    return new Player();
    }

}
