package Map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import Addons.AdvEntity;
import Addons.Entity;
import Maths.Vector2D;
import Maths.Vector2D;
import Maths.sRectangle;

public class Map 
{
	private String name;
	
	private ArrayList<Entity> entities;
	
	private ArrayList<Entity> objectColliding;
	private QuadTree quadTree;
	private Layer layer;
	private sRectangle rect;
	
	public Map()
	{
		entities = new ArrayList<Entity>();
		rect = new sRectangle(new Vector2D(0,0), new Vector2D((float) Math.pow(10,2), (float) Math.pow(10,2)));
		quadTree = new QuadTree(0, rect);
		layer = new Layer();
		name = "default";
	}
	public Map(String name)
	{
		entities = new ArrayList<Entity>();
		rect = new sRectangle(new Vector2D(0,0), new Vector2D((float) Math.pow(10,3), (float) Math.pow(10,3)));
		quadTree = new QuadTree(0, rect);
		layer = new Layer();
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	//layer
	public Layer getLayer()
	{
		return layer;
	}
	public void setLayer(Layer layer)
	{
		this.layer = layer;
		reloadEntities();
	}
	public void reloadEntities()
	{
		entities.clear();
		layer.getObjects(entities);
	}
	public void updateLayers()
	{
		layer.updateBoundsRoot();
		sRectangle rect = layer.getBounds();
		float max = Math.max(rect.getHeight(), rect.getWidth());
		quadTree.setRect(new sRectangle(rect.getLeft().x, rect.getLeft().y ,max, max));
	}
	
	public void draw(Graphics g, sRectangle vision)
	{
		layer.draw(g, vision);
		Polygon poly = rect.getPolygon();
		g.setColor(Color.red);
		g.drawPolygon(poly);
		
	}
	public void draw(sRectangle vision)
	{
		layer.draw(vision);
	}
	public void draw(Graphics g, Vector2D vec, sRectangle vision)
	{
		layer.draw(g, vec, vision);
	}
	public void draw(Vector2D vec, sRectangle vision)
	{
		layer.draw(vec, vision);
	}
	public void draw(Graphics g, Vector2D vec, sRectangle vision, float scale)
	{
		layer.draw(g, vec, vision, scale);
	}
	
	public void drawBounds(Graphics g, Vector2D vec, float scale)
	{
		layer.drawBounds(g, vec, scale);
	}
	public void drawQuadTree(Graphics g, Vector2D vec)
	{
		quadTree.draw(g, vec);
	}
	public void drawQuadTree(Graphics g, Vector2D vec, float scale)
	{
		quadTree.draw(g, vec, scale);
	}
	
	public void loadQuadTree()
	{
		reloadEntities();
		quadTree.clear();
		quadTree.inserts(entities);
	}

	public ArrayList<Entity> getObject(sRectangle rect)
	{
		ArrayList<Entity> myEntities = new ArrayList<Entity>();
		quadTree.retrieve(rect, myEntities);
		return myEntities;
	}

	public void computeEntitiesBounds()
	{
		for(int i=0; i<entities.size(); i++)
		{
			entities.get(i).computeBounds();
		}
	}
}
