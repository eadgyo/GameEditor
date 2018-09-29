package TestEngine;

import Addons.AdvForm;
import Addons.Animation;
import Addons.Entity;
import Maths.Form;
import Maths.PointInt;
import Maths.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class TestWin extends JDialog implements ActionListener, ComponentListener, KeyListener
{
	private PanelTest panel;
	public TestWin(JFrame parent)
	{
		super(parent, "Test Engine", true);
		this.setResizable(false);
		this.setSize(1600, 900);
		panel = new PanelTest();
		panel.setDoubleBuffered(true);
		panel.setBackground(Color.green);
		this.setLocationRelativeTo(null);
		this.setContentPane(panel);
		this.repaint();
		
		this.addComponentListener(this);
		this.addKeyListener(this);

	}
	public void start(ArrayList<Entity> entities)
	{
		if(entities.size() != 0)
		{
			panel.entities.clear();
			panel.entities.add(entities.get(0).clone());
			panel.entities.add(entities.get(1).clone());
			panel.entities.get(0).reset();
			panel.entities.get(1).reset();
			panel.entities.get(0).setPos(new Vector2D(50,50));
			panel.entities.get(1).setPos(new Vector2D(50,0));	
			
			Entity entities1[] = new Entity[2];
			entities1[0] = panel.entities.get(0);
			entities1[1] = panel.entities.get(1);
			Vector2D points[] = new Vector2D[2];
			points[0] = new Vector2D();
			points[1] = new Vector2D(-4.857095f, 3.736898f);
			
			panel.joint.createJointLocal(entities1, points);
			panel.joint.setLength(100f);
			
			Entity A = new Entity();
			panel.entities.add(A);
			
			
			AdvForm formA = new AdvForm();
			AdvForm formB = new AdvForm();
			formA.addPoint(new Vector2D(-50, -60));
			formA.addPoint(new Vector2D(50, -50));
			formA.addPoint(new Vector2D(50, 50));
			formA.addPoint(new Vector2D(-50, 50));
			A.setPos(new Vector2D(50,150));
			Animation animA = new Animation();
			animA.add("test", new PointInt(-2,-2), 0);
			animA.addAForm(0,0, formA);
			animA.setCurrentAnim(0);
			animA.setCurrentFrame(0);
			A.setAnim(animA);
		}
		AdvForm a = entities.get(0).getAdvFormsCompute().get(0);
		AdvForm b = entities.get(1).getAdvFormsCompute().get(0);
		panel.repaint();
		panel.start();
		this.setVisible(true);
	}
	public void start()
	{
		Entity A = new Entity();
		Entity B = new Entity();
		B.setMass(22.802258f);
		
		Animation animA = new Animation();
		Animation animB = new Animation();
		animA.add("test", new PointInt(-2,-2), 0);
		animB.add("test", new PointInt(-2,-2), 0);
		
		AdvForm formA = new AdvForm();
		AdvForm formB = new AdvForm();
		formA.addPoint(new Vector2D(-50, -50));
		formA.addPoint(new Vector2D(50, -50));
		formA.addPoint(new Vector2D(50, 50));
		formA.addPoint(new Vector2D(-50, 50));
		A.setPos(new Vector2D(50,-49));
		
		formB.addPoint(new Vector2D(-1.0686367f, 2.6374860f));
		formB.addPoint(new Vector2D(-3.4581804f, -2.4244233e-007f));
		formB.addPoint(new Vector2D(-1.0686369f, -2.6374860f));
		formB.addPoint(new Vector2D(2.7977266f, -1.6300561f));
		formB.addPoint(new Vector2D(2.7977269f, 1.6300560f));
		B.setPos(new Vector2D(56.358532f, 0.12512589f));//-2.0508742f
		B.setRadians(-2.0508742f);
		animA.addAForm(0,0, formA);
		animB.addAForm(0,0, formB);
		A.setAnim(animA);
		B.setAnim(animB);
		
		animA.setCurrentAnim(0);
		animA.setCurrentFrame(0);
		animB.setCurrentAnim(0);
		animB.setCurrentFrame(0);
		
		Form a = A.getAdvFormsCompute().get(0);
		Form b = B.getAdvFormsCompute().get(0);
		
		ArrayList<Vector2D> aa = a.getPointsWorld();
		ArrayList<Vector2D> bb = b.getPointsWorld();
		
		panel.entities.add(A);
		panel.entities.add(B);
		
		Entity entities[] = new Entity[2];
		entities[0] = A;
		entities[1] = B;
		Vector2D points[] = new Vector2D[2];
		points[0] = new Vector2D();
		points[1] = new Vector2D();
		
		panel.joint.createJointLocal(entities, points);
		panel.joint.setLength(10f);
		
		panel.start();
		this.setVisible(true);
	}
	public void start1()
	{
		Entity A = new Entity();
		Entity B = new Entity();
		A.setMass(289.00000f);
		
		Animation animA = new Animation();
		Animation animB = new Animation();
		animA.add("test", new PointInt(-2,-2), 0);
		animB.add("test", new PointInt(-2,-2), 0);
		
		AdvForm formA = new AdvForm();
		AdvForm formB = new AdvForm();
		formA.addPoint(new Vector2D(-50.208333f, -9.208335f));
		formA.addPoint(new Vector2D(-90.208333f, 7.7916675f));
		formA.addPoint(new Vector2D(50.916666f, 9.916668f));
		formA.addPoint(new Vector2D(7.7916675f, -9.208334f));
		formA.setRadians(2.12f);
		A.setPos(new Vector2D(150,150));


		formB.addPoint(new Vector2D(-50, -30));
		formB.addPoint(new Vector2D(50, -30));
		formB.addPoint(new Vector2D(50, 30));
		formB.addPoint(new Vector2D(-40, 30));

		B.setPosition(new Vector2D(150,500));
		animA.addAForm(0,0, formA);
		animB.addAForm(0,0, formB);
		A.setAnim(animA);
		B.setAnim(animB);
		
		animA.setCurrentAnim(0);
		animA.setCurrentFrame(0);
		animB.setCurrentAnim(0);
		animB.setCurrentFrame(0);
		
		Form a = A.getAdvFormsCompute().get(0);
		Form b = B.getAdvFormsCompute().get(0);
		
		ArrayList<Vector2D> aa = a.getPointsWorld();
		ArrayList<Vector2D> bb = b.getPointsWorld();
		
		panel.entities.add(A);
		panel.entities.add(B);
		
		Entity entities[] = new Entity[2];
		entities[0] = A;
		entities[1] = B;
		Vector2D points[] = new Vector2D[2];
		points[0] = new Vector2D();
		points[1] = new Vector2D();
		
		panel.joint.createJointLocal(entities, points);
		panel.joint.setLength(10f);
		
		panel.start();
		this.setVisible(true);
	}
	
	@Override
	public void componentHidden(ComponentEvent e)
	{
	}
	@Override
	public void componentMoved(ComponentEvent e)
	{
	}
	@Override
	public void componentResized(ComponentEvent e)
	{
	}
	@Override
	public void componentShown(ComponentEvent e)
	{
	}
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
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
		
	}
	@Override
	public void keyTyped(KeyEvent arg0)
	{
		
	}        
}