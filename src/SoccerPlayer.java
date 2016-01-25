/*
 * SoccerPlayer class 
 *
 * The player object that contains all soccer player attributes 
 * this class extends Rectangle to allow the use of the Rectangle methods to be 
 * used include intersection
 * 
 * @author richj0985
 */
import java.awt.Rectangle;

public class SoccerPlayer extends Rectangle {
    // create variable to determine the player number player number = 1 or 2
    int     playerNum = 0;
    
    // create variable to determine the direction - 1 = moving left to right, -1 = moving right to left
    int     direction = 0;
    
    // create variable to store the number of goals a player has
    int     goals     = 0;
    
    // create variable to store the foot position 
    int     foot2X    = 0;
    int     foot1X    = 0;
    int     foot1Y    = 0;
    int     foot2Y     = 0;
    
    // create variable to determine jumping
    // if the player is jumping
    boolean jumping  = false;
    // apply gravity to the player's jump
    int     gravity  = 1;
    // the feet movements of the player
    int     feetJump = 0;
    // apply the height of the player's jump
    int     dy       = 0;
    
    // create varaibles to move the feet when running
    // each foot
    int     footRun1 = 0;
    int     footRun2 = 0;
    
    // create variables to represent the movement of the player
    // move right
    boolean right = false;
    // move left
    boolean left  = false;
    // jump
    boolean jump  = false;
    // normal kick
    boolean kick  = false;
    // power kick
    boolean powerKick = false;
    
    // create varaible to represent the players last x position
    int lastXPos = 0;
    
    // create variable to store what how much power the player has to preform the power kick
    int powerKickCount = 0;
}
