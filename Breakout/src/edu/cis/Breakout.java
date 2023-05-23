package edu.cis;

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 4.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static int NTURNS = 3;


	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject collider;

	public void run(){
		setupGame();
		play();
	}

	private void setupGame(){
		setUpBricks();
		createPaddle();
		createBall();
	}

	private void setUpBricks(){
		double x = (CANVAS_WIDTH - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP)) / 2;
		double y = BRICK_Y_OFFSET;
		for(int row = 0; row < NBRICK_ROWS; row++){
			for(int col = 0; col < NBRICK_COLUMNS; col++){
				GRect brick = new GRect(x + col * (BRICK_WIDTH + BRICK_SEP), y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				switch(row / 2){
					case 0:
						brick.setColor(Color.RED);
						break;
					case 1:
						brick.setColor(Color.ORANGE);
						break;
					case 2:
						brick.setColor(Color.YELLOW);
						break;
					case 3:
						brick.setColor(Color.GREEN);
						break;
					case 4:
						brick.setColor(Color.CYAN);
						break;
				}
				add(brick);
			}
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}

	private void createPaddle(){
		double x = (CANVAS_WIDTH - PADDLE_WIDTH) / 2;
		double y = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e){
		double x = e.getX() - PADDLE_WIDTH / 2;
		if(x < 0){
			x = 0;
		}
		else if(x > CANVAS_WIDTH - PADDLE_WIDTH){
			x = CANVAS_WIDTH - PADDLE_WIDTH;
		}
		paddle.setLocation(x, paddle.getY());
	}

	private void createBall(){
		double x = (CANVAS_WIDTH - BALL_RADIUS) / 2;
		double y = (CANVAS_HEIGHT - BALL_RADIUS) / 2;
		ball = new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)){
			vx = -vx;
		}
		vy = VELOCITY_Y;
	}

	private void play(){
		while(true){
			moveBall();
			checkForCollision();
			pause(DELAY);
		}
	}

	private void moveBall(){
		ball.move(vx, vy);
	}

	private void checkForCollision(){
		if(ball.getX() <= 0 || ball.getX() >= CANVAS_WIDTH - BALL_RADIUS * 2){
			vx = -vx; // Bounce off side walls
		}
		if(ball.getY() <= 0){
			vy = -vy; // Bounce off top wall
		}
		if(ball.getY() >= CANVAS_HEIGHT - BALL_RADIUS * 2){
			remove(ball); // Remove ball when it hits bottom wall
			if(NTURNS > 0){
				NTURNS--;
				createBall();
			}
		}
		collider = getCollidingObject();
		if(collider == paddle){
			vy = -vy; // Bounce off paddle
		}
		else if(collider instanceof GRect){
			remove(collider); // Remove brick when ball hits it
			vy = -vy; // Bounce off brick
		}
	}

	private GObject getCollidingObject(){
		if(getElementAt(ball.getX(), ball.getY()) != null){
			return getElementAt(ball.getX(), ball.getY());
		}
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null){
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
		else if(getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null){
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null){
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		else{
			return null;
		}
	}

	public static void main(String[] args){
		new Breakout().start();
	}
}
