/*
 * SoccerFootFight - Main Game module
 *
 * @author richj0985
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

/**
 *
 * @author richj0985
 */
public class SoccerFootFight extends JComponent implements KeyListener {

    // game screen constants: height and width, field level and soccer match lengths
    static final int  WIDTH = 800 + 200;
    static final int  HEIGHT = 600;
    static final int  FIELD_LEVEL = 460;
    static final int  MATCH_TIME_SECONDS = 60;

    // Game modes/screens
    static final int GAME_MODE_MAIN_MENU         = 1;
    static final int GAME_MODE_PLAY_MATCH        = 2;
    static final int GAME_MODE_SHOW_CONTROLS     = 3;
    static final int GAME_MODE_SHOW_INSTRUCTIONS = 4;
    static final int GAME_MODE_MATCH_OVER        = 5;
     
    // Ball travel speeds
    static final int BALL_GRAVITY = 1;
    static final int BALL_SPEED = 10;
    static final int BALL_DY_MULTIPLIER = 2;

    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000) / desiredFPS;

    // image files used by the game for screen display
    BufferedImage imgSoccerField            = loadImage("Soccer Field.png");
    BufferedImage imgHomeScreen             = loadImage("soccer game home screen.jpg");
    BufferedImage imgInstructionsBackground = loadImage("soccer game writing screen.jpg");
    BufferedImage imgControls               = loadImage("controls1.jpg");
    BufferedImage imgSpaceBar               = loadImage("space bar1.jpg");
    BufferedImage imgWasDGame               = loadImage("wasdgame.jpg");
    BufferedImage imgShiftControls          = loadImage("shift.jpg");
    BufferedImage imgEndGame                = loadImage("END.jpg");
    BufferedImage[] imgBallImages;
    int ballImageIndex = 0;

    // sound effects used by the game
    SoundEffect soundGameMenu               = new SoundEffect("Game Menu.wav");
    SoundEffect soundBallKicked             = new SoundEffect("Ball Kicked.wav");
    SoundEffect soundBallBounce             = new SoundEffect("Ball Bounce.wav");
    SoundEffect soundStadiumCrowd           = new SoundEffect("Stadium Crowd.wav");
    SoundEffect soundGoal                   = new SoundEffect("Goal.wav");
    SoundEffect soundGameOver               = new SoundEffect("Game Over.wav");
       
   
    // players
    SoccerPlayer player1 = new SoccerPlayer();
    SoccerPlayer player2 = new SoccerPlayer();

    // soccer ball
    SoccerBall ball = new SoccerBall();
 
    // variable to record the balls last position when image rotated
    int ballLastXPositionWhenImageChanged = 0;
     
    // nets
    Rectangle net1 = new Rectangle(0, 300, 40, 200);
    Rectangle net2 = new Rectangle(760 + 200, 300, 40, 200);
    Rectangle crossBar1 = new Rectangle(0, 300, 40, 4);
    Rectangle crossBar2 = new Rectangle(760 + 200, 300, 40, 4);
 
    // mode of the game - determines what screens are being shown
    int gameMode = GAME_MODE_MAIN_MENU;
    
    // variables for keyboard
    boolean menuUp = false;
    boolean menuDown = false;
    boolean menuEnter = false;
    boolean menuBack = false;
    boolean restart = false;
    int     scroll = HEIGHT / 2;
    
    // store variable for score board
    long    startMatchTime = 0;
    long    showGoalScoredTime = 0;
    int     seconds = 0;
    int     minutes = 0;
    boolean matchOver = false;
    
    
    
    // method to load an graphic image file for use in screen displays
    public BufferedImage loadImage(String file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("bad file");
        }
        return img;
    }

    
    // mathod to reset player and balls positions after goal during a match
    public void resetPlayersAndBallAfterGoal() {
     
        // Reset Player 1
        player1.direction = 2;
        player1.x = WIDTH - 50 - 60;
        player1.y = 500 - 80;
        
        // Reset Player 2
        player2.direction = 1;
        player2.x = 50;
        player2.y = 500 - 80;
        
        // Reset ball
        ball.x = 470;
        ball.y = FIELD_LEVEL;
        ball.speed = 0;
  }
    
    // method to reset the game to allow for a brand new soccer match
    public void resetForNewMatch() {
        // Initialize time
        // set new match start time and compute end game time
        startMatchTime = System.currentTimeMillis();
        
        // reset the player and ball positions
        resetPlayersAndBallAfterGoal();
        
        // reset the player goals
        player1.goals = 0;
        player2.goals = 0;

        // new game
        matchOver = false;
    }
    
    // method to initialize the key varaibles for the game
    public void initializeGame() {

        // Initialize the players 
        player2.playerNum = 2;
        player2.x = 50;
        player2.y = 500 - 80;
        player2.width = 60;
        player2.height = 80;
        player2.direction = 1;
        player2.goals = 0;

        player1.playerNum = 1;
        player1.x = WIDTH - 50 - 60;
        player1.y = 500 - 80;
        player1.width  = 60;
        player1.height = 80;
        player1.direction = -1;
        player1.goals  = 0;

        // Initialize the ball
        ball.x      = 470;
        ball.y      = FIELD_LEVEL;
        ball.width  = 47;
        ball.height = 47;

        // Create the array of ball images and load them from 
        // files that are each rotated to simulate ball rotation. 
        // and load the images for the soccer ball
        imgBallImages = new BufferedImage[4];
        BufferedImage loadedImg = loadImage("Soccer Ball T1.png");
        imgBallImages[0] = loadedImg;
        imgBallImages[1] = loadImage("Soccer Ball T2.png");
        imgBallImages[2] = loadImage("Soccer Ball T3.png");
        imgBallImages[3] = loadImage("Soccer Ball T4.png");
    
    }

    // method to draw a player on the soccer field 
    // this method is reused to draw both players
    public void drawPlayer(Graphics g, SoccerPlayer player) {
        
        // skin and hair color
        Color whiteSkin = new Color(247, 238, 188);
        Color brown     = new Color(207, 136, 80);

        // player body
        if (player.playerNum == 1) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.RED);
        }
        g.fillOval(player.x + 15, player.y - 35 + 80, 30, 30);

        // face
        g.setColor(whiteSkin);
        g.fillOval(player.x, player.y - 80 + 80, 60, 60);

        // arms
        g.setColor(whiteSkin);
        g.fillOval(player.x + 2, player.y - 28 + 80, 15, 15);
        g.fillOval(player.x + 2 + 40, player.y - 28 + 80, 15, 15);

        // shoes
        g.setColor(Color.BLACK);
        g.fillOval(player.x + 5 + player.foot1X + player.footRun2, player.y - 15 + player.foot1Y + player.feetJump + 80, 25, 15);
        g.fillOval(player.x + 25 + player.foot2X + player.footRun1, player.y - 15 + player.foot2Y + player.feetJump + 80, 25, 15);

        // hair
        g.setColor(brown);
        g.fillOval(player.x - 5, player.y - 80 + 80, 20, 20);
        g.fillOval(player.x, player.y - 85 + 80, 20, 20);
        g.fillOval(player.x + 10, player.y - 87 + 80, 20, 20);
        g.fillOval(player.x + 20, player.y - 90 + 80, 20, 20);
        g.fillOval(player.x + 30, player.y - 87 + 80, 20, 20);
        g.fillOval(player.x + 40, player.y - 85 + 80, 20, 20);
        g.fillOval(player.x + 45, player.y - 80 + 80, 20, 20);

        // Draw face based on direction moving
        if (player.direction == 1) {
            // eyes
            // layer 1
            g.setColor(Color.WHITE);
            g.fillOval(player.x + 15, player.y - 60 + 80, 20, 20);
            g.fillOval(player.x + 40, player.y - 60 + 80, 20, 20);

            // layer 2
            g.setColor(Color.BLACK);
            g.fillOval(player.x + 25, player.y - 55 + 80, 12, 12);
            g.fillOval(player.x + 45, player.y - 55 + 80, 12, 12);

            // mouth
            g.setColor(Color.BLACK);
            g.drawLine(player.x + 30, player.y - 32 + 80, player.x + 50, player.y - 32 + 80);

            // nose
            g.setColor(Color.BLACK);
            g.drawLine(player.x + 40, player.y - 40 + 80, player.x + 40, player.y - 40 + 80);
        } else {
            // layer 1
            g.setColor(Color.WHITE);
            g.fillOval(player.x + 15 - 14, player.y - 60 + 80, 20, 20);
            g.fillOval(player.x + 40 - 14, player.y - 60 + 80, 20, 20);

            // layer 2
            g.setColor(Color.BLACK);
            g.fillOval(player.x + 25 - 20, player.y - 55 + 80, 12, 12);
            g.fillOval(player.x + 45 - 20, player.y - 55 + 80, 12, 12);

            // mouth
            g.setColor(Color.BLACK);
            g.drawLine(player.x + 30 - 20, player.y - 32 + 80, player.x + 50 - 20, player.y - 32 + 80);

            // nose
            g.setColor(Color.BLACK);
            g.drawLine(player.x + 40 - 22, player.y - 40 + 80, player.x + 40 - 22, player.y - 40 + 80);
        }
    
        //Debug - draw rectangles for objects
        //g.drawRect(player.x, player.y, player.width / 3, player.height / 3);
        //g.drawRect(player.x, player.y, player.width, player.height);
        //g.drawRect(ball.x, ball.y, ball.width, ball.height);
    }
  
    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)

    // method to draw all different screens of the game
    // the method uses a game mode variable to know which
    // screens to draw based on current mode of the game
    @Override
    public void paintComponent(Graphics g) {
        int ballSpin = 0;

        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // GAME DRAWING GOES HERE
        if ( gameMode == GAME_MODE_MAIN_MENU ) {
            // Draw Main Menu screen
            g.drawImage(imgHomeScreen, 0, 0, null);

            Font titleFont = new Font("Impact", Font.BOLD, 100);
            g.setFont(titleFont);
            g.setColor(Color.BLUE);
            g.drawString("Soccer Foot Fight", WIDTH / 10, HEIGHT / 2 - 150);

            Font byFont = new Font("Times New Roman", Font.BOLD, 20);
            g.setFont(byFont);
            g.setColor(Color.BLACK);
            g.drawString("JR Sports", WIDTH / 2 - 50, HEIGHT / 2 - 100);

            g.setColor(Color.WHITE);
            g.fillRect(WIDTH / 2 - 200, HEIGHT / 2, 400, 50);

            g.fillRect(WIDTH / 2 - 200, HEIGHT / 2 + 60, 400, 50);

            g.fillRect(WIDTH / 2 - 200, HEIGHT / 2 + 60 + 60, 400, 50);

            g.setColor(Color.RED);
            g.fillRect(WIDTH / 2 - 200, scroll, 400, 50);

            Font tabs = new Font("Arial", Font.BOLD, 40);
            g.setFont(tabs);
            if (scroll == HEIGHT / 2) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.RED);
            }
            g.drawString("PLAY NOW", WIDTH / 2 - 100, HEIGHT / 2 + 40);
            if (scroll == HEIGHT / 2 + 60) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.RED);
            }
            g.drawString("CONTROLS", WIDTH / 2 - 100 - 10, HEIGHT / 2 + 40 + 60);
            if (scroll == HEIGHT / 2 + 60 + 60) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.RED);
            }
            g.drawString("INSTRUCTIONS", WIDTH / 2 - 100 - 50, HEIGHT / 2 + 40 + 60 + 60);

         // Draw Main soccer game screen
        } else if ( gameMode == GAME_MODE_PLAY_MATCH ) {
            // Colors
            // grass
            Color grass = new Color(22, 196, 16);

            // sky
            Color sky = new Color(175, 240, 236);

            // sky
            g.setColor(sky);
            g.fillRect(0, 0, 800 + 200, 600);

            // draw the soccer background including stands and audience
            g.drawImage(imgSoccerField, 0, 0, null);

            // Draw the ball using a series of bitmaps
            // move through the ball images as the ball moves based on the 
            // ball direction to simulate the ball spinning on ground and in the air
            // determine the rate to move between images based on the amount
            // of movement since the last time the ball was moved
            ballSpin = (ball.x - ballLastXPositionWhenImageChanged) / 4;
            if (ballSpin > 0) {
                // Spin ball forward
                ballImageIndex += 1;
                if (ballImageIndex >= 4) {
                    ballImageIndex = 0;
                }
                ballLastXPositionWhenImageChanged = ball.x;

            } else if (ballSpin < 0) {
                // Spin ball backwards
                ballImageIndex -= 1;
                if (ballImageIndex < 0) {
                    ballImageIndex = 3;
                }
                ballLastXPositionWhenImageChanged = ball.x;
            }
            g.drawImage(imgBallImages[ballImageIndex], ball.x + 1, ball.y + 1, null);
            
            // draw the players
            drawPlayer(g, player1);
            drawPlayer(g, player2);
            
            // draw both nets
            g.setColor(Color.WHITE);
            for (int index = 1; index <= 4; index = index + 1) {
                g.drawLine(net1.x + index, net1.y, net1.x + index, net1.y + 200);
                g.drawLine(net1.x + index + net1.width - 4, net1.y, net1.x + index + net1.width - 4, net1.y + 200);
                g.drawLine(net2.x + index, net2.y, net2.x + index, net2.y + 200);
                g.drawLine(net2.x + index + net2.width - 4, net2.y, net2.x + index + net2.width - 4, net2.y + 200);
            }
            for (int index = 1; index <= 4; index = index + 1) {
                g.drawLine(net1.x, net1.y + index, net1.x + net1.width, net1.y + index);
                g.drawLine(net1.x, net1.y + index + net1.height - 4, net1.x + net1.width, net1.y + index + net1.height - 4);
                g.drawLine(net2.x, net2.y + index, net2.x + net2.width, net2.y + index);
                g.drawLine(net2.x, net2.y + index + net1.height - 4, net2.x + net2.width, net2.y + index + net1.height - 4);
            }
            for (int index = 0; index <= net1.width; index = index + 10) {
                g.drawLine(net1.x + index, net1.y, net1.x + index, net1.y + 200);
                g.drawLine(net2.x + index, net2.y, net2.x + index, net2.y + 200);
            }
            for (int index = 0; index <= net1.height; index = index + 10) {
                g.drawLine(net1.x, net1.y + index, net1.x + net1.width, net1.y + index);
                g.drawLine(net2.x, net2.y + index, net2.x + net2.width, net2.y + index);
            }

            // scoreboard
            Font scoreFont = new Font("Impact", Font.BOLD, 40 - 10);
            g.setFont(scoreFont);
            g.setColor(Color.BLUE);
            g.drawString("USA: " + player1.goals, WIDTH / 2 + 200 - 150 - 15, 40 + 20);
            g.setFont(scoreFont);
            g.setColor(Color.RED);
            g.drawString("CAN: " + player2.goals, WIDTH / 2 - 350 + 225 + 15, 40 + 20);
 

            // Draw the remaining time in the game
            g.setFont(scoreFont);
            g.setColor(Color.WHITE);
            if(seconds < 0){
               g.drawString(minutes + ":" + "00", WIDTH / 2 - 350 + 225 + 20 + 60 + 15, 40 + 20 + 20 + 20); 
            } else if(seconds < 10){
               g.drawString(minutes + ":" + "0" + seconds, WIDTH / 2 - 350 + 225 + 20 + 60 + 15, 40 + 20 + 20 + 20); 
            }else{
               g.drawString(minutes + ":" + seconds, WIDTH / 2 - 350 + 225 + 20 + 60 + 15, 40 + 20 + 20 + 20);
            }
            
            // If a goal was scored, show text of goals scored
            if(showGoalScoredTime > 0){
                Font goal = new Font("IMPACT", Font.BOLD, 100);
                g.setFont(goal);
                g.setColor(Color.RED);
                g.drawString("GOAL!", WIDTH / 2 - 130, 200);
                
                // determine if we have displayed the goal scored long enough 
                // if so then turn off 
                if ( showGoalScoredTime < System.currentTimeMillis() ) {
                    showGoalScoredTime = 0;
                }
            }
            
       
            // if the soccer match is over, indicate it is on the current soccer field view
            if( matchOver ){
                Font endGame = new Font("Arial", Font.BOLD, 100);
                g.setFont(endGame);
                g.setColor(Color.WHITE);
                g.drawString("GAME OVER", WIDTH / 2 - 300, HEIGHT / 2 + 300);
            }
            
        // Draw Game Controls screen
        } else if ( gameMode == GAME_MODE_SHOW_CONTROLS ) {
            g.drawImage(imgInstructionsBackground, 0, 0, null);
            Font title = new Font("Arial", Font.BOLD, 40);
            g.setFont(title);
            g.setColor(Color.WHITE);
            g.drawString("Controls:", WIDTH / 2 - 500, 40);

            Font player1 = new Font("Arial", Font.BOLD, 30);
            g.setFont(player1);
            g.setColor(Color.BLUE);
            g.drawString("Player 1:", WIDTH / 2 - 500 + 100, 175 - 50);
            g.drawImage(imgControls, 100, 200 - 50, null);
            g.drawImage(imgSpaceBar, 100, 400 - 50, null);
            g.setColor(Color.RED);
            g.drawString("Player 2:", WIDTH / 2 - 500 + 100 + 500, 175 - 50);
            g.drawImage(imgWasDGame, 100 + 500, 200 - 50, null);
            g.drawImage(imgShiftControls, 100 + 500, 400 - 50 + 25, null);

        // Draw Instructions screen
        } else if (gameMode == GAME_MODE_SHOW_INSTRUCTIONS ) {
            g.drawImage(imgInstructionsBackground, 0, 0, null);
            Font title2 = new Font("Arial", Font.BOLD, 40);
            g.setFont(title2);
            g.setColor(Color.BLUE);
            g.drawString("Instructions:", WIDTH / 2 - 500, 40);
            
            Font explain = new Font("Arial", Font.BOLD, 20);
            g.setFont(explain);
            g.setColor(Color.WHITE);
            g.drawString("Kick!...Shoot!...Score!...", WIDTH / 2 - 500, 100);
            g.drawString("Soccer Foot Fight is a game designed for users to enjoy 1 on 1 soccer at a competitive level.", WIDTH / 2 - 500, 100 + 20);
            g.drawString("How To Play:", WIDTH / 2 - 500, 100 + 20 + 40);
            g.drawString("      1. The clock starts to count down from 3 minutes to indicate the amount of time left in the match.", WIDTH / 2 - 500, 100 + 20 + 60);
            g.drawString("      2. Players start at opposite sides of the field. Player 1 starts on the right and Player 2 on the left.", WIDTH / 2 - 500, 100 + 20 + 80);
            g.drawString("      3. The ball is located at the centre of the field.", WIDTH / 2 - 500, 100 + 20 + 100);
            g.drawString("      4. Players compete to put the ball in the oppositing net.", WIDTH / 2 - 500, 100 + 20 + 120);
            g.drawString("      5. If a Player scores, the ball and the players reset at the centre.", WIDTH / 2 - 500, 100 + 20 + 140);
            g.drawString("      6. The Player with the most goals by the end of the time wins!", WIDTH / 2 - 500, 100 + 20 + 160);
            g.drawString("      (Controls can be viewed in the control section)", WIDTH / 2 - 500, 100 + 20 + 180);
            g.drawString("Credits:", WIDTH / 2 - 500, 100 + 20 + 240);
            g.drawString("      Programmed: Jon Richards.", WIDTH / 2 - 500, 100 + 20 + 260);
   
            
        // Draw soccer match over (gameover) screen    
        }else if(gameMode == GAME_MODE_MATCH_OVER ){
            // Game over display who won or tie
            if(player1.goals > player2.goals){
                // Player 1 Wins
                g.drawImage(imgEndGame, 0, 0, null);
                Font win1 = new Font("IMPACT", Font.BOLD, 100);
                g.setFont(win1);
                g.setColor(Color.BLUE);
                g.drawString("USA WINS!", WIDTH / 2 - 350, 250);
                
            }else if(player2.goals > player1.goals){
                // Player 2 Wins
                g.drawImage(imgEndGame, 0, 0, null);
                Font win2 = new Font("IMPACT", Font.BOLD, 100);
                g.setFont(win2);
                g.setColor(Color.RED);
                g.drawString("CANADA WINS!", WIDTH / 2 - 350, 250);

            }else if(player1.goals == player2.goals){
                // Tie Game
                g.drawImage(imgEndGame, 0, 0, null);
                Font tie = new Font("IMPACT", Font.BOLD, 100);
                g.setFont(tie);
                g.setColor(Color.RED);
                g.drawString("TIE GAME!", WIDTH / 2 - 250, 250);
            }
        }
        // GAME DRAWING ENDS HERE
    }

    // method to move the soccer player in the game based on 
    // move left, move right, jump and kick keyboard controls
    // the move player method also moves the soccer ball when 
    // the player comes into contract with the ball 
    public void movePlayer(SoccerPlayer player, SoccerBall ball ) {
        // if player collides with other player don't allow them to move
        if (player1.intersects(player2) && (player1.y + 60 >= player2.y && player2.y + 60 >= player1.y)) {
            if (player1.x > player2.x) {
                player1.left = false;
                player2.right = false;
            } else {
                player1.right = false;
                player2.left = false;
            }
        }     
        // Check to see if the player has been hit by the ball that is in 
        // motion.   If the ball hit the player then 
        // have the ball bounce off the players head 
        // If the ball hits the player's body then have the ball change 
        // direction but take energy out of the ball's motion
        if ( player.intersects(ball)) {
            // create rectangle for top portion of players head
            // to be used to see if the ball is in contact with it
            Rectangle playersHead = new Rectangle();
            playersHead.x          = player.x;
            playersHead.y          = player.y;
            playersHead.width      = player.width;
            playersHead.height     = player.height  / 10;
            if (playersHead.intersects(ball)) {
                // ball hit the players head
                soundBallBounce.Start();  
            
                // allow the ball to rise into the air after
                ball.gravity = 0;
            } else {
                // the ball hit another part of the player body
                // take most of the bounce out of ball
                ball.speed /= 4;
                ball.dy /= 4;
            }
            
            // The ball hit the players head
            // set the balls X direction based on side the player the ball hit
            // form a rectangle to represent the left side if the players 
            // head. 
            Rectangle playersLeftSide = new Rectangle();
            playersLeftSide.x = player.x;
            playersLeftSide.y = player.y;
            playersLeftSide.width = player.width / 3;
            playersLeftSide.height = player.height / 3;
            // Set the ball direction based on where the ball hits the player on the head
            if (playersLeftSide.intersects(ball)) {
                // if hit left side and ball is traveling right then change it's direction
                if (ball.speed > 0) {
                    ball.speed *= -1;

                    // increase the ball speed due to conflict
                    ball.speed -= 1;
                }
            } else {
                // ball hit the right side of player, 
                // check to see if the ball is traveling left and if it is then change it's direction
                // if hit left side and ball is traveling right then change it's direction
                if (ball.speed < 0) {

                    ball.speed *= -1;
                    // increase the ball speed due to conflict
                    ball.speed += 1;
                }
              
            }
        }
        
        // if the ball is in contact with the player then
        // push the ball ahead of the player 
        if ( player.intersects(ball) ) {
            if ( ball.x > player.x) { 
                ball.x += 5;
            } else {
                ball.x -= 5;               
            }
        }
        
        // if the player is in contact with the other player then
        // push the player over to the left or right 
        if ( player1.intersects(player2) ) {
            if(player2.y > player1.y){
                if ( player1.x > player2.x) { 
                     player1.x += 2;
                } else {
                     player1.x -= 2;               
                }
            }else if(player1.y > player2.y){
                if ( player1.x > player2.x) { 
                     player1.x += 2;
                } else {
                     player1.x -= 2;               
                }
            }
        }
        // Player controls indicate player moving right.
        // Move the player on to RIGHT on the field
        if (player.right) {
            player.x = player.x + 5;
            player.footRun1 = 4;
            player.direction = 1;

            // if player now collides with the ball and the ball colliding with other player 
            // then don't allow the player to advance
            if ( player1.intersects(ball) && player2.intersects(ball) ) {
                player.x = player.x - 5;
            }
         } else {
            player.footRun1 = 0;
        }
        // Player controls indicate player moving left
        // Move the player on to LEFTRIGHT on the field
         if (player.left) {
            player.x = player.x - 5;
            player.footRun2 = -4;
            player.direction = -1;
 
            // if player now collides with the ball and the ball colliding with other player 
            // then don't allow the player to advance
            if ( player1.intersects(ball) && player2.intersects(ball) ) {
                player.x = player.x + 5;
            }
        } else {
            player.footRun2 = 0;
        }

        // if player collides with game boundaries then stop the players move
        // ensure player is not allowed to move off the field
        if (player.x <= 0) {
            player.x = 0;
        } else if (player.x + 60 >= WIDTH) {
            player.x = WIDTH - 60;
        }

        // player jumping, set the players upward motion via .DY variable
        if (player.jump && !player.jumping) {
            player.dy = -18;
            player.jumping = true;
        }
        // apply gravity each frame to slow the player as he rises
        // ultimately the gravity will increase to the point that
        // the player will fall back to the field/ground
        player.dy = player.dy + player.gravity;
        if (player.dy > 18) {
            player.dy = 18;
        }

        // Check to see if the player has reach the ground
        // if yes then set the player to not jumping state
        player.y = player.y + player.dy;
        if (player.y > 500 - 80) {
            player.y = 500 - 80;
            player.jumping = false;
        }
        // If player jumping then set the players feet position to 
        // to indicate the player is jumping
        if (player.jumping) {
            player.feetJump = 5;
        } else {
            player.feetJump = 0;
        }

        // kicking - determine if the player is within half a ball width from the ball
        // if then all the player to kick the ball.  The easiest way to detemine
        // within half ball distance from the ball is to make a new rectangle
        // that is 50% bigger
        Rectangle playerFootRect = new Rectangle();
        if (player.direction == 1) {
            playerFootRect.x = player.x + player.width - ball.width / 4;
        } else {
            playerFootRect.x = player.x - ball.width / 2;
        }
        playerFootRect.y = player.y + player.height - ball.width / 4;
        playerFootRect.width = ball.width / 2;
        playerFootRect.height = ball.width / 2;
        
        // If player is atempting to kick the ball, determine if the ball is 
        // close enough to the player to allow the ball to be kicked
        // if it is then set the ball in motion using the speed and dx variables
        if (player.kick && playerFootRect.intersects(ball)) {
            // ball has been kicked, set it's speed and direction
            ball.speed = player.direction * BALL_SPEED;
            ball.x += ball.speed;
            if(player.powerKickCount == 600){
                ball.dy = 20;
                ball.speed = player.direction * 50;
                player.powerKickCount = 0;
            }else{

                // set the power of the kick based on random value
                ball.dy = (int) (Math.random() * (12 - 5 + 1) + 5) * BALL_DY_MULTIPLIER;
            }
            // play sound to indicate the ball has been kicked
            soundBallKicked.Start();
            
        }
        if (player.kick) {
            if (player.x + player.width / 2 <= ball.x) {
                player.foot2X = 15;
                player.foot2Y = - 5;
            } else if (player.x >= ball.x + ball.width / 2) {
                player.foot1X = - 15;
                player.foot1Y = - 5;
            }
        } else {
            player.foot1X = 0;
            player.foot1Y = 0;
            player.foot2X = 0;
            player.foot2Y = 0;
        }
        if(player.powerKickCount < 600){
            player.powerKickCount = player.powerKickCount + 1;
        }
    }
    
    // method to move the ball each frame based on the balls speed (x coordinate)
    // and on DY in the Y coordinate.   THe ball will bounce off the edges
    // of the field, off players heads, off the goal posts. 
    // If the ball enters a net without contacting a post then a goal will 
    // be recorded for the opposing team. 
    public void moveBall(SoccerBall ball) {
        // use variable to represent the drag from the ball to the ground
        int drag;

        // move the ball and apply both gravity to ball height
        // and apply a "drag" to the ball speed. 
        ball.x = ball.x + ball.speed;
        ball.y = ball.y - ball.dy + ball.gravity;
        // accumulate the gravity on the ball's flight
        ball.gravity = ball.gravity + BALL_GRAVITY;

        // bounce the ball off the field surface
        // reduce the bounce height of the ball each time it hits the ground
        if (ball.y > FIELD_LEVEL) {
            ball.y = FIELD_LEVEL;
            // reduce the size of the bounce by 40%
            // set gravity strength back to zero to allow the ball to begin to 
            // bounce up. 
            ball.dy -= (float) ball.dy * .20;
            ball.gravity = 0;

            // provided the ball is going to bounce then play a bounce sound
            if ( ball.dy != 0 ) {
                // ball will bounce off surface
                soundBallBounce.Start();            
            }
        }

        // if the ball moving and is rolling on the ground, apply drag to the 
        // to simulate the movement and drag of a ball rolling on the ground
        if (ball.speed != 0 && ball.y >= FIELD_LEVEL) {
            // reduce the ball speed by 33% each frame
            drag = ball.speed / 5;
            // ensure we apply a drag value
            if (drag == 0) {
                if (ball.speed > 0) {
                    drag = 1;
                } else {
                    drag = -1;
                }
            }
            ball.speed = ball.speed - drag;
        }

        // check the ball against the field boundaries   If the ball hits the edge of 
        // the field then change it's direction and take 50% of it's speed away 
        if (ball.x < 0) {
            ball.x = 0;
            ball.speed = ball.speed / 2 * -1;
            soundBallBounce.Start();            
       } else if (ball.x > WIDTH - 40) {
            ball.speed = ball.speed / 2 * -1;
            ball.x = WIDTH - 40;
            soundBallBounce.Start();            
       }

        // ball collides and bounces off the cross bar of the net
        if (ball.intersects(crossBar1) || ball.intersects(crossBar2)) {
            // reset the accumulated gravity on ball to allo ball to 
            // bounce up off the cross bar
            ball.gravity = 0;

            // determine the X direction based on which net's cross bar it hit
            if (ball.intersects(crossBar1)) {
                if (ball.speed < 0) {
                    ball.speed *= -1;
                    ball.speed += 1;
                }
            } else {
                if (ball.speed > 0) {
                    ball.speed *= -1;
                    ball.speed -= 1;
                }
            }
            soundBallBounce.Start();            
        }

        // ball entered the next witout hitting goal post
        // a goal was scored for player1
        if ( ball.intersects(net1) && !ball.intersects(crossBar1)) {
            // Count the goal and annouce goal
            player1.goals = player1.goals + 1;
            soundGoal.Start();
            
            // Indicate that a goal scored message should appear for 3 seconds
            showGoalScoredTime = System.currentTimeMillis() + 3000L;
            
            // reset the Players and Ball Positions 
            resetPlayersAndBallAfterGoal();

        // ball entered the next witout hitting goal post
        // a goal was scored for player2
        } else if ( ball.intersects(net2) && !ball.intersects(crossBar2)) {
            player2.goals = player2.goals + 1;
            soundGoal.Start();
            
            // Indicate that a goal scored message should appear for 3 seconds
            showGoalScoredTime = System.currentTimeMillis() + 3000L;
            
            // reset the Players and Ball Positions 
            resetPlayersAndBallAfterGoal();
        }
    }


    // The main game loop
    // In here is where all the logic for my game will go
    public void run() {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime = 0;
        long elapsedTime = 0;

        // Initialize the goal objects of the game
        initializeGame();

        // play the menu sound/music
        soundGameMenu.Start();
            
        // the main game loop section
        // game will end if you set done = false;
        boolean done = false;
        while (!done) {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();

            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 

            // Determine which mode/screen we are to know what to do/call
            if ( gameMode == GAME_MODE_MAIN_MENU  ) {
                // allow navigation of the menu
                if (menuUp && scroll != HEIGHT / 2) {
                    scroll = scroll - 60;
                    menuUp = false;
                } else if (menuDown && scroll != HEIGHT / 2 + 60 + 60) {
                    scroll = scroll + 60;
                    menuDown = false;
                }

                // If user selected the menu item and 
                // the menu item is "Play Match" then start new match
                if ( menuEnter ) {
                    // Check menu selection again Start New Match
                    if (scroll == HEIGHT / 2 ) {
                        gameMode = GAME_MODE_PLAY_MATCH;

                        // Start a new match
                        resetForNewMatch();    
                                           
                        // switch the sound to soccer match mode
                        soundGameMenu.Stop();
                        soundStadiumCrowd.Start();
                        matchOver = false;

                    // if on the Controls menu item then set game mode to controls mode
                    } else if (scroll == HEIGHT / 2 + 60 ) {
                        gameMode = GAME_MODE_SHOW_CONTROLS;
                        
                    // if on the Instructions menu item then set game mode to Instructions mode
                    } else if (scroll == HEIGHT / 2 + 60 + 60 ) {
                        gameMode = GAME_MODE_SHOW_INSTRUCTIONS;
                               
                    }
                    // reset that a menu selection has been made
                    menuEnter = false;
                }
                
            // Play Soccer Match Game Mode
            } else if ( gameMode == GAME_MODE_PLAY_MATCH ) {
                // main game mode
                // Compute the seconds and minutes left in the game
                elapsedTime = System.currentTimeMillis() - startMatchTime;
                seconds = (int)(MATCH_TIME_SECONDS - elapsedTime/1000);
                minutes = seconds/60;
                seconds = seconds - 60*minutes;
                System.out.println("player1: " + player1.powerKickCount + " player2: " + player2.powerKickCount);
                // determine if soccer match time has expired
                if ( minutes <= 0 && seconds <= 0 ) {
                    // Determine if the game has just ended, 
                    // if it has, then indicate matchOver
                    // and start matchOver sound
                    if ( matchOver == false) {
                        matchOver = true;
                        soundGameOver.Start();
                    } else {
                        // allow of the end of game whistle to play for 2 seconds
                        // and then switch to the new screen to indicate who won the game
                        if ( seconds <= -4 ) {
                            gameMode = GAME_MODE_MATCH_OVER;
                        }
                    }
                }
                    
                // move players and ball in game as long 
                // as the game is not over
                if ( !matchOver ) {
                    movePlayer(player1, ball);
                    movePlayer(player2, ball);
                    moveBall(ball);
                }
                                   
               
            
            // If showing screns for CONTROLS or INSTRUCTIONS then let them
            // return to the main menu
            } else if ( gameMode == GAME_MODE_SHOW_CONTROLS || 
                        gameMode == GAME_MODE_SHOW_INSTRUCTIONS ) {
                // In instructions mode or controls mode
                // allow user to exit the mode back to the menu
                if ( menuBack) {
                    gameMode = GAME_MODE_MAIN_MENU;
                    menuBack = false;
                }
            
            // The soccer match end show which player won the game
            } else if ( gameMode == GAME_MODE_MATCH_OVER ){
                elapsedTime = System.currentTimeMillis() - startMatchTime;
                seconds = (int)(MATCH_TIME_SECONDS - elapsedTime/1000);
                minutes = seconds/60;
            
                // check to see if the user has asked to restart back to menu
                // allow 10 seconds from the time of the game over whistle
                // to allow users to see the screen indicating who won
                if(restart && seconds <= -10 ) {                  
                    restart = false;
                    
                    // stop all of the game music
                    soundGameOver.Stop();
                    soundStadiumCrowd.Stop();
                    resetForNewMatch();
                    
                    // play the menu sound/music
                    soundGameMenu.Start();
                    
                    // Switch to menu mode
                    gameMode = GAME_MODE_MAIN_MENU;
                }
            }
       
            // GAME LOGIC ENDS HERE 
            // update the drawing (calls paintComponent)
            repaint();

            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            
            if (deltaTime > desiredTime) {
            } else {
                try {
                    Thread.sleep(desiredTime - deltaTime);
                } catch (Exception e) {
                };
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates a windows to show my game
        JFrame frame = new JFrame("Soccer Foot Fight");

        // creates an instance of my game
        SoccerFootFight game = new SoccerFootFight();
        // sets the size of my game
        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(game);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);
        frame.addKeyListener(game);
        // starts my game loop
        game.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Menu, Controls and Instructions mode key Controls
        if ( gameMode == GAME_MODE_MAIN_MENU ) {
            if (key == KeyEvent.VK_UP) {
                menuUp = true;
            } else if (key == KeyEvent.VK_DOWN) {
                menuDown = true;
            } else if (key == KeyEvent.VK_SPACE) {
                menuEnter = true;
            } else if (key == KeyEvent.VK_ENTER) {
                menuEnter = true;
            } else if (key == KeyEvent.VK_LEFT) {
                menuBack = true;
            }
        }
 
        // for the control and instructions information screens allow any 
        // key press to return to the main menu
        if ( gameMode == GAME_MODE_SHOW_CONTROLS ||
             gameMode == GAME_MODE_SHOW_INSTRUCTIONS ) {
            menuBack = true;
        }
 
        // Game Mode Key Controls
        if (gameMode == GAME_MODE_PLAY_MATCH) {
            // Player 1 key controls
            if (key == KeyEvent.VK_RIGHT) {
                player1.right = true;
            } else if (key == KeyEvent.VK_LEFT) {
                player1.left = true;
            } else if (key == KeyEvent.VK_UP) {
                player1.jump = true;
            } else if (key == KeyEvent.VK_SPACE) {
                player1.kick = true;
            
            // Player 2 key controls
            } else if (key == KeyEvent.VK_A) {
                player2.left = true;
            } else if (key == KeyEvent.VK_D) {
                player2.right = true;
            } else if (key == KeyEvent.VK_W) {
                player2.jump = true;
            } else if (key == KeyEvent.VK_SHIFT) {
                player2.kick = true;
            }
        }
 
        // GameOver over mode key 
        if(gameMode == GAME_MODE_MATCH_OVER ){
            restart = true;
        } 
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // Menu and Player1 Key Controls 
        if ( gameMode == GAME_MODE_MAIN_MENU || 
             gameMode == GAME_MODE_SHOW_CONTROLS ||
             gameMode == GAME_MODE_SHOW_INSTRUCTIONS ) {
            if (key == KeyEvent.VK_UP) {
                menuUp = false;
            } else if (key == KeyEvent.VK_DOWN) {
                menuDown = false;
            } else if (key == KeyEvent.VK_SPACE) {
                menuEnter = false;
            } else if (key == KeyEvent.VK_ENTER) {
                menuEnter = false;
            } else if (key == KeyEvent.VK_LEFT) {
                menuBack = false;
            }
        }
        
        // Game Mode Key Controls
        if (gameMode == GAME_MODE_PLAY_MATCH) {
            // Player 1 key controls
            if (key == KeyEvent.VK_RIGHT) {
                player1.right = false;
            } else if (key == KeyEvent.VK_LEFT) {
                player1.left = false;
            } else if (key == KeyEvent.VK_UP) {
                player1.jump = false;
            } else if (key == KeyEvent.VK_SPACE) {
                player1.kick = false;

            // Player2 Key Controls 
            } else if (key == KeyEvent.VK_A) {
                player2.left = false;
            } else if (key == KeyEvent.VK_D) {
                player2.right = false;
            } else if (key == KeyEvent.VK_W) {
                player2.jump = false;
            } else if (key == KeyEvent.VK_SHIFT) {
                player2.kick = false;
            }
        }
    }
}
