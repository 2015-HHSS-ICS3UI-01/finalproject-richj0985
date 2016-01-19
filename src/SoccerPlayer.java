import java.awt.Rectangle;

/*
 * SoccerPlayer class 
 *
 * The player object that contains all soccer player attributes 
 * this class extends Rectangle to allow the use of the Rectangle methods to be 
 * used include intersection
 * 
 * @author richj0985
 */
public class SoccerPlayer extends Rectangle {
    // player number = 1 or 2
    int     playerNum = 0;
    
    // direction - 1=moving left to right, -1=moving right to left
    int     direction = 0;
    
    int     goals     = 0;
    
    // foot 
    int     foot2X    = 0;
    int     foot1X    = 0;
    int     foot1Y    = 0;
    int     foot2Y     = 0;
    
    // jumping
    boolean jumping  = false;
    int     feetJump = 0;
    int     gravity  = 1;
    
    // running
    int     footRun1 = 0;
    int     footRun2 = 0;
    
    // kicking
    int     dy       = 0;

    // player controls
    boolean right = false;
    boolean left  = false;
    boolean jump  = false;
    boolean kick  = false;
    boolean powerKick = false;
    
    int lastXPos = 0;
    
    int powerKickCount = 0;
}
