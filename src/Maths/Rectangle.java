package Maths;

import java.awt.Polygon;
import java.util.ArrayList;

public class Rectangle extends Form
{
	public final static long serialVersionUID = -1976089904607398674L;
	
	protected Vector2D length;
	
	public Rectangle()
	{
		super(4);
		length = new Vector2D(0);
		set(new Vector2D(0, 0), new Vector2D(0, 0), 0);
	}
	public Rectangle(Rectangle rec)
	{
		super(rec);
		length = new Vector2D();
		length.set(rec.getLength());
	}
	public Rectangle(Form form)
	{
		super(form);
		assert(form.size() == 4);
		
		//Les 2 points doivent être opposés
		Vector2D vec = new Vector2D(points.get(0), points.get(2));
		orientation.setPos(points.get(0).add(vec.multiply(0.5f)));
		
		Vector2D side = new Vector2D(points.get(0), points.get(1));
		length = new Vector2D();
		length.x = side.getMagnitude();
		
		side.set(points.get(0), points.get(1));
		length.y = side.getMagnitude();
	}
	@Override
	public Rectangle clone()
	{
		return new Rectangle(this);
	}
	public Rectangle(Vector2D center, Vector2D length, float omega)
	{
		super(4);
		this.length = new Vector2D();
		set(center, length, omega);
	}
	/*public Rectangle(Point2D center, Point2D length, float omega, boolean setLeft)
	{
		//Constructeur pour construire à partir du point en au à gauche
		super(4);
		this.length = new Point2D();
		if(setLeft)
			setLeft(center, length, omega);
		else
			set(center, length, omega);
	}*/
	
	public void set(Vector2D center, Vector2D length, float omega)
	{
		this.clearTransformations();
		orientation.setPos(center);
		this.length.set(length);
		
		points.get(0).set(- 0.5f*length.x,- 0.5f*length.y);
		points.get(1).set(- 0.5f*length.x,+ 0.5f*length.y);
		points.get(2).set(+ 0.5f*length.x,+ 0.5f*length.y);
		points.get(3).set(+ 0.5f*length.x,- 0.5f*length.y);
		
		this.rotateRadians(omega, center);
	}
	public void set(Rectangle rec)
	{
		this.set(rec.getCenter(), rec.getLength(), rec.getAngle());
		this.setInit(rec);
	}
	public void set(Form form)
	{
		assert(form.size() == 4);
		
		for(int i=0; i<4; i++)
			points.set(i, form.getLocal(i));
		
		//Les 2 points doivent être opposés
		Vector2D vec = new Vector2D(form.getLocal(0), form.getLocal(2));
		orientation.setPos(form.getLocal(0).add(vec.multiply(0.5f)));
		
		Vector2D side = new Vector2D(form.getLocal(0), form.getLocal(1));
		length.x = side.getMagnitude();
		
		side.set(form.getLocal(0), form.getLocal(3));
		length.y = side.getMagnitude();
		
		super.setInit(form);
	}
	/*public void setLeft(Point2D left, Point2D length, float omega)
	{
		set(center, length, omega);
		Point2D vec = new Point2D(this.getLeft(), left);
		this.translate(vec);
	}*/
	
	public Vector2D getVecLocal()
	{
		return new Vector2D(this.points.get(0), this.points.get(3));
	}
	public Vector2D getVecWorld()
	{
		Vector2D vec = new Vector2D(this.points.get(0), this.points.get(3));
		return orientation.multiply(vec);
	}
	public Vector2D getLeft()
	{
		return this.get(0);
	}
	public Vector2D getLength()
	{
		return length;
	}
	public float getWidth()
	{
		return length.x;
	}
	public float getHeight()
	{
		return length.y;
	}
	
	@Override
	public void rotateDegrees(float omega, Vector2D center)
	{
		//omega = Extend.modulo(omega, 360f);
		this.rotateRadians((float) (omega*Math.PI)/180, center);
	}
	@Override
	public void rotateRadians(float omega, Vector2D center)
	{
		super.rotateRadians(omega, center);
	}
	public float getAngle(Vector2D vec)
	{
		Vector2D l_vec1 = this.getVecWorld();
		if(l_vec1.x == 0 && l_vec1.y == 0)
			return this.getAngle();
		return l_vec1.getAngle(vec);
	}
	public float getAngle()
	{
		return this.omega;
	}
	public void scale(float factor, Vector2D center)
	{
		super.scale(factor, center);
		length.selfMultiply(factor);
	}
}
