/*
 * SoccerBall class

 * The SoccerBall object contains all attributes for the soccer ball
 * this class extends Rectangle to allow the use of the Rectangle methods to be 
 * used include intersection
 * 
 * @author richj0985
 */
import java.awt.Rectangle;

public class SoccerBall extends Rectangle {
    // create variable to represent the ball kick height
    int dy = 0;
    
    // create varaible to represent the gravity within the game for the ball
    int gravity = 0;
    
    // create variable to represent the speed at which the ball is travelling at
    int speed = 0;
}
