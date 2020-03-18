package com.Game;

import com.Game.graphics.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable //we are using imp..Runnable to use the "This" function below
{
    public static int width = 300;
    public static int height = width/16*9;
    public static int scale = 3;
    public static  String title = "The GaMe";

    private  boolean running = false;
    private Thread thread;
    private JFrame frame;

    private Screen screen;

    private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private  int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    // it's a complex code.All you need to know is
    //we are converting image object into an array of int
    // that array signals which pixel signals which color,thus making the whole image

    //lets create an method to start the thread

    public Game(){
        Dimension size = new Dimension(width*scale,height*scale);
        setPreferredSize(size); //it is a method of canvas by which we are setting the window size
        screen = new Screen(width,height); //we are sending width and height to the Screen class
        frame = new JFrame();
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this,"Display");
        //Creating an object of thread so that we can manupulate it
        //by using "this" we are using the instance object of game class
        //"Display"is just a random name we gave to the new created object of thread
        thread.start(); //by this we can start the thread

    }
    //now we need a method to close the thread
    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } //dont know exactly what I did
    // all I know its necessary to write these to stop a thread
    public  void run() //it runs when we access the thread above
    {     //game-loop
        long lastTime = System.nanoTime(); //nanoTime() is more precise than currentTime()
        long timer =System.currentTimeMillis(); //a variable to show fps every 1 sec
        final double ns = 1000000000.0/60.0; //ns = nano seconds
        double delta = 0;
        int frames = 0; //this variable will keep track how many times we should render image or render() is called
        int updates = 0; //it will keep track how many time update() will be called
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime= now;
            while (delta >= 1) {
                update(); //method for updating game-logic(it is called 30/60/120 times)
                updates++;
                delta--;
            }
            render(); //method for rendering image(it is called countless of time)
            frames++;
            if(System.currentTimeMillis() - timer>1000) {
                timer += 1000;
                System.out.println(updates + "ups, " + frames + " fps"); //to show update per second/fps
                frame.setTitle(title + " | " +updates + "ups, " + frames + " fps");//this way we can show the fps counter in the game window
                updates = 0 ; // we are making update variabe 0 so that it never exceeds 60
                frames = 0;
            }
        }

    }
    public void update(){



    }
    public void render(){
        BufferStrategy bs = getBufferStrategy(); //it gets the the object of our canvas
        if (bs == null) {
            createBufferStrategy(3); // we are using 3
            // thus we are creating 2 buffer in the background
            return;
        }
        screen.clear();
        screen.render();
        for (int i = 0; i <pixels.length ; i++) {
            pixels[i]= screen.pixels[i];
        }
        Graphics g =bs.getDrawGraphics(); //initialize graphics
        g.setColor(Color.BLACK); // we are setting the window black
        g.fillRect(0,0,getWidth(),getHeight());//using this we fill a rectangle in the window
        g.drawImage(image,0,0,getWidth(),getHeight(),null);
        g.dispose(); //we dispose the graphics we created
        bs.show(); //it will show the buffer/next available buffer

    }
    public  static  void main(String[] args)
    {
        Game game = new Game();
        game.frame.setResizable(false); //by doing so we are fixing or making our window resolution unchangable
        game.frame.setTitle(Game.title);
        game.frame.add(game); // we are adding game components into the window
        game.frame.pack(); //set the size according to the components
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // the program gonna close when we close the window
        // if its not added the program will run even if we close the window
        game.frame.setLocationRelativeTo(null);
        // we use this method to centre our window
        game.frame.setVisible(true); // It's very important
        // by using this we allow to show our window

        game.start();
    }


}



