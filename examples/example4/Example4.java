package example4;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import moulton.scalable.containers.Container;

public class Example4 extends JPanel implements Container, MouseListener, KeyListener,
		MouseMotionListener, MouseWheelListener{
	private static final long serialVersionUID = 1L;
	private Manager4 manager = null;
	private JFrame frame;
	private boolean running = true;
	
	public static void main(String args[]){
		new Example4();
	}
	
	public Example4(){
		frame = new JFrame("Example 4");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		manager = new Manager4(this);
		manager.createMenu();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		//start run loop so the screen can refresh
		run();
	}
	
	private void run() {
		//we have the spinning animation to accomodate for
		final int FRAME_PER_MS = 100;
		long currentTime;
		long lastTime = System.currentTimeMillis();
		while (running) {
			currentTime = System.currentTimeMillis();
			if (currentTime > FRAME_PER_MS + lastTime) {
				this.repaint();
				lastTime = System.currentTimeMillis();
			} else {
				try {
					Thread.sleep(FRAME_PER_MS + lastTime - currentTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(manager != null)
			manager.render(g);
		requestFocus();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500,500);
	}

	@Override
	public int getMenuWidth() {
		return getWidth();
	}

	@Override
	public int getMenuHeight() {
		return getHeight();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(manager != null){
			manager.mouseMoved(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(manager != null){
			manager.mouseMoved(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(manager != null){
			manager.keyTyped(e.getKeyChar());
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(manager != null){
			manager.keyPressed(e.getExtendedKeyCode());
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(manager != null){
			manager.mousePressed(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(manager != null){
			manager.mouseReleased(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(manager != null) {
			manager.mouseScrolled(e.getX(), e.getY(), e.getWheelRotation());
			repaint();
		}
	}
	
	@Override
	public void setCursor(int cursorType) {
		frame.setCursor(Cursor.getPredefinedCursor(cursorType));
	}

}
