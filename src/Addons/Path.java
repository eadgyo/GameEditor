package Addons;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import Maths.Vector2D;
import Maths.Rectangle;
import Maths.Vector2D;
import Maths.sRectangle;

public class Path extends Entity
{
	private ArrayList<Vector2D> points;
	
	public Path()
	{
		points = new ArrayList<Vector2D>();
	}
	public Path(Vector2D p)
	{
		points = new ArrayList<Vector2D>();
		points.add(p);
	}
	public Path(Entity entity)
	{
		super(entity);
		points = new ArrayList<Vector2D>();
	}
	public Path clone()
	{
		Path path = new Path(this);
		for(int i=0; i<points.size(); i++)
		{
			path.addPoint(points.get(i).clone());
		}
		return path;
	}
	
	
	
	public Vector2D getPoint(int i)
	{
		return points.get(i);
	}
	public void addPoint(Vector2D p)
	{
		points.add(p);
		if(points.size() == 1)
		{
			this.rec.setCenter(points.get(0));
		}
	}
	public void remove(int i)
	{
		points.remove(i);
		if(i == 0 && points.size() != 0)
		{
			this.rec.setCenter(points.get(0));
		}
	}
	public void remove(Vector2D p)
	{
		if(p == points.get(0) && points.size() > 1)
		{
			this.rec.setCenter(points.get(1));
		}
		points.remove(p);
	}
	public int size()
	{
		return points.size();
	}
	
	@Override
	public String toString()
	{
		return new String(this.name);
	}
	
	
	
	//Override
	public void draw()
	{
		this.draw(this.graphics);
	}
	public void draw(Graphics g)
	{
		if(g == null || visible < 0.01f)
			return;
		super.draw(g);
		
		g.setColor(Color.RED);
		//Draw segment
		for(int i=0; i<points.size() - 1; i++)
		{
			g.drawLine((int) points.get(i).x, (int) points.get(i).y, (int) points.get(i+1).x, (int) points.get(i+1).y);
		}
		for(int i=0; i<points.size(); i++)
		{
			g.fillOval((int) points.get(i).x - 3, (int) points.get(i).y - 3, 6, 6);
		}
	}
	public void draw(Vector2D translation)
	{
		this.draw(this.graphics, translation);
	}
	public void draw(Graphics g, Vector2D translation)
	{
		if(g == null || visible < 0.01f)
			return;
		super.draw(g, translation);
		
		g.setColor(Color.RED);
		//Draw segment
		for(int i=0; i<points.size() - 1; i++)
		{
			g.drawLine((int) (points.get(i).x + translation.x), (int) (points.get(i).y + translation.y), (int) (points.get(i+1).x + translation.x), (int) (points.get(i+1).y + translation.y));
		}
		for(int i=0; i<points.size(); i++)
		{
			g.fillOval((int) (points.get(i).x + translation.x - 3), (int) (points.get(i).y + translation.y - 3), 6, 6);
		}
	}
	public void draw(Graphics g, Vector2D translation, float scale)
	{
		if(g == null || visible < 0.01f || scale == 0)
			return;
		super.draw(g, translation, scale);
		
		Path path = this.clone();
		
		path.scale(scale, new Vector2D());
		g.setColor(Color.red);
		for(int i=0; i<points.size() - 1; i++)
		{
			g.drawLine((int) (path.getPoint(i).x + translation.x), (int) (path.getPoint(i).y + translation.y), (int) (path.getPoint(i+1).x + translation.x), (int) (path.getPoint(i+1).y + translation.y));
		}
		for(int i=0; i<points.size(); i++)
		{
			g.fillOval((int) (path.getPoint(i).x + translation.x - 3), (int) (path.getPoint(i).y + translation.y - 3), 6, 6);
		}
	}
	
	//Transformations
	@Override
	public void translate(Vector2D vec)
	{
		super.translate(vec);
		for(int i=0; i<points.size(); i++)
		{
			points.get(i).translate(vec);
		}
	}
	@Override
	public void translateX(float vecX)
	{
		super.translateX(vecX);
		for(int i=0; i<points.size(); i++)
		{
			points.get(i).translateX(vecX);
		}
	}
	@Override
	public void translateY(float vecY)
	{
		super.translateY(vecY);
		for(int i=0; i<points.size(); i++)
		{
			points.get(i).translateY(vecY);
		}
	}
	public void flipH(Vector2D center) 
	{
		super.flipH(center);
		for(int i=0; i<points.size(); i++)
		{
			points.get(i).flipH(center);
		}
	}
	public void flipV(Vector2D center) 
	{
		super.flipV(center);
		for(int i=0; i<points.size(); i++)
		{
			points.get(i).flipV(center);
		}
	}
	public void scale(float factor, Vector2D center)
	{
		super.scale(factor, center);
		if(factor != 0)
			for(int i=0; i<points.size(); i++)
			{
				points.get(i).scale(factor, center);
			}
		else
			for(int i=0; i<points.size(); i++)
			{
				points.get(i).scale(0.0001f, center);
			}
	}
	public void rotateRadians(float radians, Vector2D center) 
	{
		super.rotateRadians(radians, center);
		for(int i=0; i<points.size(); i++)
		{
			points.get(i).rotateRadians(radians, center);
		}
	}
	
	public float getMinX()
	{
		float xMin = points.get(0).x;
		for(int i=1; i<points.size(); i++)
		{
			if(points.get(i).x < xMin)
				xMin = points.get(i).x; 
		}
		return xMin;
	}
	public float getMinY()
	{
		float yMin = points.get(0).y;
		for(int i=1; i<points.size(); i++)
		{
			if(points.get(i).y < yMin)
				yMin = points.get(i).y; 
		}
		return yMin;
	}
	public float getMaxX()
	{
		float xMax = points.get(0).x;
		for(int i=1; i<points.size(); i++)
		{
			if(points.get(i).x > xMax)
				xMax = points.get(i).x; 
		}
		return xMax;
	}
	public float getMaxY()
	{
		float yMax = points.get(0).y;
		for(int i=1; i<points.size(); i++)
		{
			if(points.get(i).y > yMax)
				yMax = points.get(i).y; 
		}
		return yMax;
	}
	
	public sRectangle getBounds()
	{
		float xMin = getMinX();
		float xMax = getMaxX();
		float yMin = getMinY();
		float yMax = getMaxY();
		return new sRectangle(xMin ,
				yMin,
				xMax - xMin,
				yMax - yMin);
	}
}
