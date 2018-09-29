package EntityEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.*;
import javax.swing.* ;

import Addons.Animation;
import Base.Image;
import Maths.Vector2D;

@SuppressWarnings("serial")
public class FramesDialog extends JDialog implements KeyListener, Runnable, WindowListener
{
	private BufferedImage surface;
	private JScrollPane surfScroller;
	private boolean ok;
	private Graphics g;
	private ArrayList<Image> images;
	private Animation anim;
	private Thread runner;
	
	public FramesDialog(JDialog frame)
	{
		super(frame, "Play animation", false);
		
		this.setResizable(false);
		this.setSize(530,550);
		this.setLocationRelativeTo(null);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		surface = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		surfScroller = new JScrollPane(new JLabel(new ImageIcon(surface)));
		surfScroller.setPreferredSize(new Dimension(512,512));
		
		ok = false;
		runner = null;
		images = new ArrayList<Image>();
		
		g = surface.getGraphics();
		
		this.add(surfScroller);
		this.addKeyListener(this);
		this.addWindowListener(this);
	}
	public void launchDialog(ArrayList<Image> images, Animation anim) throws InterruptedException
	{
		setVisible(true);
		ArrayList<Image> l_images = new ArrayList<Image>();
		this.anim = anim;
		
		this.images.clear();
		for(int i=0; i<images.size(); i++)
		{
			this.images.add(images.get(i).clone());
			this.images.get(i).setGraphics(g);
			
			this.images.get(i).setSize(500);
			this.images.get(i).setLeftPos(new Vector2D(0,0));
		}
		ok = true;
		if(runner == null)
		{
			runner = new Thread(this);
			runner.start();
		}
	}
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			ok = false;
			setVisible(false);
			if(runner != null)
			{
				runner.interrupt();
				runner = null;
			}
		}
	}
	public void render()
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, surface.getWidth(), surface.getHeight());
		images.get(anim.getCurrentFrame()).draw();
		
		//images.get(0).draw();
		this.repaint();
		try 
		{
			Thread.sleep((long) anim.getDelay());
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		anim.nextFrame();
	}
	public void paint(Graphics g) 
	{
		
		super.paint(g);
		surfScroller.repaint();
		
	}
	@Override
	public void keyReleased(KeyEvent e) 
	{
			
	}
	@Override
	public void keyTyped(KeyEvent e) 
	{
		
	}
	@Override
	public void run() 
	{
		int lastFrame = anim.getCurrentFrame();
		while(ok)
		{
			render();
		}
		anim.setCurrentFrame(lastFrame);
	}
	@Override
	public void windowActivated(WindowEvent e) 
	{
		
		
	}
	@Override
	public void windowClosed(WindowEvent e) 
	{
		
		
	}
	@Override
	public void windowClosing(WindowEvent e) 
	{
		if(runner != null)
		{
			runner.stop();
			runner = null;
		}
	}
	@Override
	public void windowDeactivated(WindowEvent e) 
	{

	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		
		
	}
	@Override
	public void windowOpened(WindowEvent e) 
	{
		
		
	}
}
