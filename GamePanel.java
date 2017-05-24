package game;

import java.awt.even.KeyEvent;

public class GamePanel extends JPanel implements Runnable, KeyListener {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;
	
	public GamePanel() {
		setPreferedSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent arg0) {
	
	}
	
	public void keyReleased(KeyEvent arg0) {
	
	}
	
	public void keyTyped(KeyEvent arg0) {
	
	}
	
	public void run() {
	
	}
	
}
