package Maths;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Circle extends Form
{
	private float radius;
	public Circle()
	{
		super();
		radius = 0;
	}
	public Circle(Vector2D center, float radius)
	{
		super();
		this.radius = radius;
		orientation.setPos(center);
	}
	public Circle(float radius)
	{
		super();
		this.radius = radius;
	}
	public Circle(Circle circle)
	{
		super();
		this.radius = circle.getRadius();
		orientation.setPos(circle.getCenter());
	}
	@Override
	public Circle clone()
	{
		return new Circle(this);
	}
	
	@Override
	public void scale(float factor, Vector2D center)
	{
		super.scale(factor, center);
		this.radius *= factor;
	}
	
	public void setRadius(float radius)
	{
		this.radius = radius;
	}
	public float getRadius()
	{
		return radius;
	}
	public float getMinX()
	{
		return getCenterX() - radius;
	}
	public float getMinY()
	{
		return getCenterY() - radius;
	}
	public float getMaxX()
	{
		return getCenterX() + radius;
	}
	public float getMaxY()
	{
		return getCenterY() + radius;
	}
	
	public boolean isColliding(Circle circle)
	{
		Vector2D vec = new Vector2D(circle.getCenter(), this.getCenter());
		if(vec.getMagnitude() <= this.radius + circle.radius)
			return true;
		return false;
	}
	
	@Override
	public void draw(Graphics g)
	{
		g.setColor(Color.red);
		g.drawOval((int) (getCenterX()- radius), (int) (getCenterY() - radius), (int) (radius*2), (int) (radius*2));
	}
	@Override
	public void draw(Graphics g, Vector2D vec)
	{
		g.setColor(Color.red);
		g.drawOval((int) (getCenterX() + vec.x - radius), (int) (getCenterY()+ vec.y - radius), (int) (radius*2), (int) (radius*2));
	}
	@Override
	public void draw(Graphics g, Vector2D vec, float scale)
	{
		Circle circle = this.clone();
		circle.scale(scale, new Vector2D());
		
		g.setColor(Color.red);
		g.drawOval((int) (getCenterX() + vec.x - circle.radius), (int) (getCenterY() + vec.y - circle.radius), (int) (radius*2), (int) (radius*2));
	}
	@Override
	public void drawRW(Graphics g, Vector2D vec)
	{
		assert(false);
	}
	@Override
	public void drawRW(Graphics g, Vector2D vec, float scale)
	{
		assert(false);
	}
	@Override
	public void fillForm(Graphics g, Vector2D vec)
	{
		g.setColor(Color.red);
		g.fillOval((int) (getCenterX() - radius + vec.x), (int) (getCenterY() - radius + vec.y), (int) (radius*2), (int) (radius*2));
	}
	@Override
	public void fillForm(Graphics g, Vector2D vec, float scale)
	{
		//assert(false);
		Circle circle = this.clone();
		circle.scale(scale, new Vector2D());
		
		g.setColor(Color.red);
		g.fillOval((int) (circle.getCenterX() + vec.x - circle.radius), (int) (circle.getCenterY() + vec.y - circle.radius), (int) (circle.getRadius()*2), (int) (circle.getRadius()*2));
	}

	
}
