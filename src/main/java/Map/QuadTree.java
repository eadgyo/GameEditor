package Map;
//http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import Addons.Entity;
import Maths.Vector2D;
import Maths.Vector2D;
import Maths.sRectangle;

public class QuadTree 
{
	private int MAX_OBJECTS = 5;
	private int MAX_LEVELS = 50;
	
	private int level;
	private ArrayList<Entity> entities;
	private sRectangle rect;
	private QuadTree[] nodes;

	public QuadTree(int level, sRectangle rect)
	{
		this.level = level;
		this.rect = rect;
		
		this.entities = new ArrayList<Entity>();
		nodes = new QuadTree[4];
	}
	/*public QuadTree() 
	{
		this.level = 0;
		this.rect = new sRectangle();

		this.entities = new ArrayList<Entity>();
		nodes = new QuadTree[4];
	}*/
	public void setRect(sRectangle rect)
	{
		this.rect.set(rect);
	}

	public void clear()
	{
		entities.clear();
		if(nodes[0] != null)
			for(int i=0; i< nodes.length; i++)
			{
				nodes[i].clear();
				nodes[i] = null;
			}
	}
	
	public void split()
	{
		int subWidth = (int)(rect.getWidth()/2);
		int subHeight = (int)(rect.getHeight()/2);
		int x = (int)rect.getX();
		int y = (int)rect.getY();
		nodes[0] = new QuadTree(level+1, new sRectangle(x + subWidth, y, subWidth, subHeight));
		nodes[1] = new QuadTree(level+1, new sRectangle(x, y, subWidth, subHeight));
		nodes[2] = new QuadTree(level+1, new sRectangle(x, y + subHeight, subWidth, subHeight));
		nodes[3] = new QuadTree(level+1, new sRectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}
	
	public int getIndex(sRectangle rect) 
	{
		int index = -1;
		Vector2D l_rectCenter = new Vector2D((float) (this.rect.getCenterX()), (float) (this.rect.getCenterY()));
		if(rect.getX(2) < l_rectCenter.x)
		{
			if(rect.getY(2) < l_rectCenter.y)
				index = 1;
			else if(rect.getY() > l_rectCenter.y)
				index = 2;
		}
		else if(rect.getX() > l_rectCenter.x)
		{
			if(rect.getY(2) < l_rectCenter.y)
				index = 0;
			else if(rect.getY() > l_rectCenter.y)
				index = 3;
		}
		return index;
	}
	
	public void insert(Entity entity)
	{
		if(nodes[0] != null)
		{
			int index = getIndex(entity.getBounds());
			if(index != -1)//si le rectangle rentre dans l'une des quatres cases
			{
				nodes[index].insert(entity);
				return;
			}
		}
		
		if(entities.size() + 1 > MAX_OBJECTS && level + 1 < MAX_LEVELS)
		{
			if(nodes[0]==null)
			{
				split();
				
				//change here
				//restructuration de la liste
				int i = 0;
				while(i < entities.size())
				{
					int index = getIndex(entities.get(i).getBounds());
					if(index != -1)
						nodes[index].insert(entities.remove(i));
					else
						i++;
				} 
			}
			int index = getIndex(entity.getBounds());
			if(index != -1)
				nodes[index].insert(entity);
			else
				entities.add(entity);
		}
		else
			entities.add(entity);
	}
	public void inserts(ArrayList<Entity> entities)
	{
		for(int i=0; i<entities.size(); i++)
		{
			this.insert(entities.get(i));
		}
	}
	
	public void retrieve(Entity entity, ArrayList<Entity> entities)
	{
		retrieve(entity.getBounds(), entities);
	}
	
	public void retrieve(sRectangle rect, ArrayList<Entity> entities)
	{
		int index = getIndex(rect);
		if(index != -1 && nodes[0] != null)
			nodes[index].retrieve(rect, entities);
		else
			addEntities(entities);
		entities.addAll(this.entities);
	}
	public void addEntities(ArrayList<Entity> entities)
	{
		if(nodes[0] == null)
			return;
		for(int i=0; i<4; i++)
			nodes[i].addEntities(entities);
	}
	
	public void draw(Graphics g, Vector2D vec)
	{
		rect.draw(g, vec);
		if(nodes[0] != null)
		{
			for(int i=0; i<4; i++)
			{
				nodes[i].draw(g, vec);
			}
		}
	}
	public void draw(Graphics g, Vector2D vec, float scale)
	{
		rect.draw(g, vec, scale);
		if(nodes[0] != null)
		{
			for(int i=0; i<4; i++)
			{
				nodes[i].draw(g, vec, scale);
			}
		}
	}
}
