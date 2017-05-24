package game;

import java.awt.even.KeyEvent;

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
	
	//movement
	private int dx, dy;
	
	//key input
	private boolean up, down, left, right, start;
	
	public GamePanel() {
		setPreferedSize(new Dimension(WIDTH, HEIGHT));
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
		setLevelUp
		setFPS(10);
	}
	
	private void setLevelUp() {
		snake = new ArrayList<Entity>();
		head = new Entity(SIZE);
		head.setPosition(WIDTH / 2, HEIGHT / 2);
		snake.add(head);
		for(int i = 1; i < 10; i++) {
			Entity e = new Entity(SIZE);
			e.setPosition(head.getX() + (i * SIZE), head.getY());
			snake.add(e);
		}
		apple = new Entity(SIZE);
		setApple();
		score = 0;
	}
	
	public void setApple() {
		int x = (int)(Math.random() * (WIDTH - SIZE));
		int y = (int)(Math.random() * (HEIGHT - SIZE));
		apple.setPosition(x, y);
	}
	private void requestRender() {
		render(g2d)
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}
	private void update() {
		if(up && dy == 0) {
			dy = -SIZE;
			dx = 0;
		}
		if(down && dy == 0) {
			dy = SIZE;
			dx = 0;
		}
		if(left && dx == 0) {
			dy = 0;
			dx = -SIZE;
		}
		if(right && dx == 0) {
			dy = 0;
			dx = SIZE;
		}
		if(dx != 0 || dy != 0) {
			for(int i = snake.size() - 1; i > 0; i--) {
				snake.get(i).setPosition(snake.get(i - 1).getX(), snake.get(i - 1).getX());
			}
			head.move(dx, dy);
		}
		if(apple.isCollision(head)) {
			score++;
			setApple();
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
		g2d.clearRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(Color.RED);
		for(Entity e : snake) {
			e.render(g2d);
			g2d.setColor(Color.WHITE);
		}
		g2d.setColor(Color.YELLOW)
		apple.render(g2d);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Score : " + score, 10, 10);
	}
	
}
