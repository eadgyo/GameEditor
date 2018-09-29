package TestAI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class TestAI extends JDialog implements ActionListener, ComponentListener, KeyListener
{
	private PanelAI panel;
	
	public TestAI(JFrame parent)
	{
		super(parent, "Test AI", true);
		this.setResizable(false);
		this.setSize(1600, 900);
		panel = new PanelAI();
		panel.setDoubleBuffered(true);
		panel.setBackground(Color.green);
		this.setLocationRelativeTo(null);
		this.setContentPane(panel);
		this.repaint();
		
		this.addComponentListener(this);
		this.addKeyListener(this);
		this.setVisible(false);
	}
	
	public void start()
	{
		panel.start();
		this.setVisible(true);
	}


	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
		{
			panel.stop();
			this.setVisible(false);
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void componentHidden(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
}
