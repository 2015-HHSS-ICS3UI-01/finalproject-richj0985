/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author richj0985
 */
public class Soccer extends JComponent implements KeyListener{

    // Height and Width of our game
    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    
    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000)/desiredFPS;
    
    int playerX = 50;
    int playerY = 440;
    int dy = 0;
    boolean jumping = false;
    int gravity = 1;
    
    int ballPositionX = 370;
    int ballPositionY = 460;
    long airTime = 3000;
    
    boolean right = false;
    boolean left = false;
    boolean up = false;
    
    
    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g)
    {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);
        
        // GAME DRAWING GOES HERE
        Color grass = new Color(22, 196, 16);
        Color sky = new Color(175, 240, 236);
        
        // sky
        g.setColor(sky);
        g.fillRect(0, 0, 800, 600);
        
        // soccer field
        g.setColor(grass);
        g.fillRect(0, 400, 800, 200);
        
        // detail on field
        g.setColor(Color.WHITE);
        g.fillRect(380, 400, 20, 200);
        g.drawOval(365, 475, 50, 50);
        
        // nets
        g.setColor(Color.WHITE);
        g.fillRect(0, 300, 40, 200);
        g.fillRect(760, 300, 40, 200);
        
        // player1
        g.setColor(Color.RED);
        g.fillRect(playerX, playerY, 60, 60);
        
        // ball
        g.setColor(Color.BLACK);
        g.fillOval(ballPositionX, ballPositionY, 40, 40);
        // GAME DRAWING ENDS HERE
    }
    
    
    // The main game loop
    // In here is where all the logic for my game will go
    public void run()
    {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;
        
        // the main game loop section
        // game will end if you set done = false;
        boolean done = false; 
        while(!done)
        {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();
            
            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            
            // turn right
            if(right){
                playerX = playerX + 2;
            }
            
            // turn left
            if(left){
                playerX = playerX - 2;
            }
            
            System.out.println("PY: " + playerY + "    BY: " + ballPositionY);
            // run with ball
            if(playerY + 60 > ballPositionY){
                if(playerX + 60 == ballPositionX){
                    ballPositionX = ballPositionX + 2;
                }else if(playerX == ballPositionX + 40){
                    ballPositionX = ballPositionX - 2;
                }
                
                ballPositionX = ballPositionX + 2;
            }
            
            if(up && !jumping){
                dy = -20;
                jumping = true;
            }
            
            dy = dy + gravity;
            if( dy > 20){
                dy = 20;
            }
            playerY = playerY + dy;
            if(playerY > 440){
                playerY = 440;
                jumping = false;
            }
            // GAME LOGIC ENDS HERE 
            
            // update the drawing (calls paintComponent)
            repaint();
            
            
            
            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            if(deltaTime > desiredTime)
            {
                //took too much time, don't wait
            }else
            {
                try
                {
                    Thread.sleep(desiredTime - deltaTime);
                }catch(Exception e){};
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates a windows to show my game
        JFrame frame = new JFrame("My Game");
       
        // creates an instance of my game
        Soccer game = new Soccer();
        // sets the size of my game
        game.setPreferredSize(new Dimension(WIDTH,HEIGHT));
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
        if(key == KeyEvent.VK_RIGHT){
            right = true;
        }else if(key == KeyEvent.VK_LEFT){
            left = true;
        }else if(key == KeyEvent.VK_UP){
            up = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_RIGHT){
            right = false;
        }else if(key == KeyEvent.VK_LEFT){
            left = false;
        }else if(key == KeyEvent.VK_UP){
            up = false;
        }
    }
}