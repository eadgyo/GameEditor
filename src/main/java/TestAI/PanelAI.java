package TestAI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import AI.GridAI;
import AI.NodeAI;
import AI.PathFinding;

public class PanelAI extends JPanel implements Runnable
{
	private Thread runner;
	public boolean ok;
	private GridAI grid;
	private PathFinding pathFinding;
	
	public PanelAI()
	{
		runner = null;
		ok = false;
		grid = new GridAI();
		pathFinding = new PathFinding();
	}
	public void init()
	{
		NodeAI start = grid.getNodes()[0][0];
		NodeAI end = grid.getNodes()[38][0];
		pathFinding.setStart(start);
		pathFinding.setEnd(end);
		pathFinding.startIterate();
	}
	public void start()
	{
		ok = true;
		init();
		runner = new Thread(this);
		runner.start();
	}
	public void stop()
	{
		ok = false;
		if(runner != null)
		{
			try
			{
				runner.join();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			runner = null;
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.yellow);
		g.fillRect(0,0,1600,900);
		grid.paint(g, pathFinding.getOpenList(), pathFinding.getClosedList(), grid.getNodes()[0][0]);
	}
	@Override
	public void run()
	{
		boolean isFinished = false;
		while(ok)
		{
			this.repaint();
			if(!isFinished)
				isFinished = pathFinding.iterate();
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
