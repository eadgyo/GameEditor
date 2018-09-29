package TestEngine;

import Addons.AdvForm;
import Addons.Entity;
import Collision.Contact;
import Collision.Joint;
import Maths.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PanelTest extends JPanel implements Runnable
{
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public Joint joint;
	private Thread runner;
	public boolean ok;
	
	public PanelTest()
	{
		runner = null;
		joint = new Joint();
	}
	public void start()
	{
		ok = true;
		runner = null;
		if(runner == null)
		{
			runner = new Thread(this);
			runner.start();
		}
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
		
		for(int i=0; i<entities.size(); i++)
		{
			//entities.get(i).draw(g);
			float rot = entities.get(i).getDegrees();
			AdvForm a = entities.get(i).getAdvFormsCompute().get(0);
			
			entities.get(i).getAdvFormsCompute().get(0).draw(g, new Vector2D(200, -200), 2);
			joint.draw(g, new Vector2D(200,-200), 2);
		}
	}
	public void update(float dt)
	{
		for(int i=0; i<entities.size(); i++)
		{
			entities.get(i).addForce(new Vector2D(0, 10*entities.get(i).getMass()));
		}
	}
	@Override
	public void run()
	{
		while(ok)
		{
			float dt = 0.090f;
			int wait = (int) (dt*1000);
			this.repaint();
			update(dt);
			for(int i=0; i<entities.size(); i++)
			{
				Entity.updateDamping(dt);
				entities.get(i).update(dt);
				//joint.check();
			}
			Contact.testCollision(entities.get(0), entities.get(1), dt);
			//Contact.testCollision(entities.get(1), entities.get(2), dt);
			//Contact.testCollision(entities.get(0), entities.get(2), dt);
			try
			{
				Thread.sleep(wait);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}