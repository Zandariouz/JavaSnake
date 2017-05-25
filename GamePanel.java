package game;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;
	
	//render
	private Graphics2D g2d;
	private BufferedImage image;
	
	//loop
	private Thread thread;
	private boolean running;
	private long targetTime;
	
	//game
	private final int SIZE = 10;
	private Entity head, apple;
	private ArrayList<Entity> snake;
	private int score;
	private int level;
	private boolean gameover;
	private int previousMove;
	private int nextMove;
	private int addition = 1;
	
	//movement
	private int dx, dy;
	
	//key input
	private boolean up, down, left, right, start;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	
	public void addNotify() {
		super.addNotify();
		thread = new Thread(this);
		thread.start();
	}
	
	private void setFPS(int fps) {
		targetTime = 1000 / fps;
	}
	
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_UP) up = true;
		if(k == KeyEvent.VK_DOWN) down = true;
		if(k == KeyEvent.VK_LEFT) left = true;
		if(k == KeyEvent.VK_RIGHT) right = true;
		if(k == KeyEvent.VK_ENTER) start = true;
	}
	
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_UP) up = false;
		if(k == KeyEvent.VK_DOWN) down = false;
		if(k == KeyEvent.VK_LEFT) left = false;
		if(k == KeyEvent.VK_RIGHT) right = false;
		if(k == KeyEvent.VK_ENTER) start = false;
	}
	
	public void keyTyped(KeyEvent arg0) {
	
	}
	
	public void run() {
		if(running) return;
		init();
		long startTime;
		long elapsed;
		long wait;
		while(running) {
			startTime = System.nanoTime();
			update();
			requestRender();
			elapsed = System.nanoTime() - startTime;
			wait = targetTime - elapsed / 1000000;
			if(wait > 0) {
				try {
					Thread.sleep(wait);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		running = true;
		setLevelUp();
	}
	
	private void setLevelUp() {
		
		snake = new ArrayList<Entity>();
		head = new Entity(SIZE);
		head.setPosition(WIDTH / 2, HEIGHT / 2);
		snake.add(head);
		
		for(int i = 1; i < 1; i++) {
			Entity e = new Entity(SIZE);
			e.setPosition(head.getX() + (i * SIZE), head.getY());
			snake.add(e);
		}
		
		addition = 1;
		apple = new Entity(SIZE);
		setApple();
		score = 0;
		gameover = false;
		level = 1;
		previousMove = -1;
		dx = dy = 0;
		setFPS(level * 10);
	}
	
	public void setApple() {
		if((int)(Math.random() * 3) == 0) {
			int x = (int)(Math.random() * (WIDTH - SIZE));
			int y = (int)(Math.random() * (HEIGHT - SIZE));
			x = x - (x % SIZE);
			if(x == 0)
				x += SIZE;
			if(x == WIDTH - SIZE)
				x -= SIZE;
			y = y - (y % SIZE);
			if(y == 0)
				y += SIZE;
			if(y == WIDTH - SIZE)
				y -= SIZE;
			addition = 3;
			apple.setSize(SIZE * 3);
			apple.setPosition(x, y);
		}
		else {
			apple.setSize(SIZE);
			int x = (int)(Math.random() * (WIDTH - SIZE));
			int y = (int)(Math.random() * (HEIGHT - SIZE));
			x = x - (x % SIZE);
			y = y - (y % SIZE);
			addition = 1;
			apple.setPosition(x, y);
		}
	}
	
	private void requestRender() {
		render(g2d);
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}
	
	private void update() {
		if(gameover) {
			if(start) {
				setLevelUp();
			}
			return;
		}
		if(up == true && dy == 0 && previousMove != 2) {
			dy = -SIZE;
			dx = 0;
			nextMove = 1;
		}
		
		if(down == true && dy == 0 && previousMove != 1) {
			dy = SIZE;
			dx = 0;
			nextMove = 2;
		}
		
		if(left == true && dx == 0 && previousMove != 4) {
			dy = 0;
			dx = -SIZE;
			nextMove = 3;
		}
		
		if(right == true && dx == 0 && previousMove != 3) {
			dy = 0;
			dx = SIZE;
			nextMove = 4;
		}
		
		if(dx != 0 || dy != 0) {
			for(int i = snake.size() - 1; i > 0; i--) {
				snake.get(i).setPosition(
						snake.get(i - 1).getX(),
						snake.get(i - 1).getY());
			}
			head.move(dx, dy);
		}
		
		if(head.getX() < 0 || head.getY() < 0 || head.getX() > WIDTH || head.getX() > HEIGHT) {
			gameover = true;
		}
		
		for(Entity e : snake) {
			if(e.isCollision(head)) {
				gameover = true;
				break;
			}
		}
		
		if(apple.isCollision(head)) {
			score += addition;
			Entity e = new Entity(SIZE);
			e.setPosition(-100, -100);
			snake.add(e);
			for(int a = 1; a < addition; a++) {
				Entity r = new Entity(SIZE);
				r.setPosition(-100, -100);
				snake.add(r);
			}
			setApple();
				
			if(score %10 == 0) {
				level++;
				if(level > 10) level = 10;
					setFPS(level * 5 + 5);
			}
		}
		
		if(head.getX() < 0)
			head.setX(WIDTH);
		
		if(head.getY() < 0)
			head.setY(HEIGHT);
		
		if(head.getX() > WIDTH) 
			head.setX(0);
		
		if(head.getY() > WIDTH) 
			head.setY(0);
	}
	
	public void render(Graphics2D g2d) {
		previousMove = nextMove;
		g2d.clearRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(Color.RED);
		int interval = 0;
		for(Entity e : snake) {
			if(interval == 5) {
				g2d.setColor(Color.BLUE);
				interval = 0;
			}
			e.render(g2d);
			g2d.setColor(Color.WHITE);
			interval++;
		}
		g2d.setColor(Color.YELLOW);
		if(addition == 3)
			g2d.setColor(Color.GREEN);
		apple.render(g2d);
		
		if(gameover) {
			g2d.setColor(Color.WHITE);
			g2d.drawString("GameOver", 150, 200);
		}
		g2d.setColor(Color.WHITE);
		g2d.drawString("Score : " + score + " Level : " + level, 10, 10);
		
		if(dx == 0 && dy == 0) {
			g2d.setColor(Color.WHITE);
			g2d.drawString("Ready", 150, 200);
		}
	}
	
}
