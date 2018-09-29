package AI;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class GridAI
{
	private static final int size = 32;
	private int[][] grid;
	private NodeAI[][] gridAI;
	
	public GridAI()
	{
		grid = new int[40][20];
		gridAI = new NodeAI[grid.length][grid[0].length];
		
		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				grid[i][j] = 0;
				gridAI[i][j] = new NodeAI(i, j);
			}
		}
		
		createObstacle();
		createNodes();
	}
	
	public void createNodes()
	{
		int dx, dy;
		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				ArrayList<NodeAI> nodes = gridAI[i][j].nodes;
				
				//8 possibilités
				for(dx=-1; dx<2; dx++)
				{
					for(dy=-1; dy<2; dy++)
					{
						if(dx != 0 || dy != 0)
							addNode(nodes, i+dx, j+dy);
					}
				}
			}
		}
	}
	private void addNode(ArrayList<NodeAI> nodes, int x, int y)
	{
		if(x < 0 || x > grid.length-1 || y < 0 || y>grid[0].length-1 || grid[x][y] != 0)
			return;
		nodes.add(gridAI[x][y]);
	}
	
	public NodeAI[][] getNodes()
	{
		return gridAI;
	}
	
	public void createObstacle()
	{
		for(int i=6; i<14; i++)
		{
			grid[4][i] = 1;
		}
		
		for(int i=2; i<11; i++)
		{
			grid[7][i] = 1;
		}
		grid[8][0] = 1;
		grid[8][1] = 1;
		grid[8][2] = 1;
	}
	
	public void paint(Graphics g)
	{
		paintOnly(g);
		paintGrid(g);
	}
	public void paintOnly(Graphics g)
	{
		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				if(grid[i][j] == 0)
				{
					g.setColor(Color.white);
				}
				else if(grid[i][j] == 1)
				{
					g.setColor(Color.gray);
				}
				
				g.fillRect(size*i, size*j, size, size);
			}
		}
	}
	public void paintGrid(Graphics g)
	{
		int maxX = grid.length;
		int maxY = grid[0].length;
		
		g.setColor(Color.black);
		for(int i=0; i<grid.length + 1; i++)
		{
			g.drawLine(i*size, 0, i*size, maxY*size);
		}
		for(int i=0; i<grid[0].length + 1; i++)
		{
			g.drawLine(0, i*size, maxX*size, i*size);
		}
	}
	
	public void paint(Graphics g, HashSet<NodeAI> openList, HashSet<NodeAI> closedList, NodeAI start)
	{
		paintOnly(g);
		Iterator<NodeAI> opLi = openList.iterator();
		Iterator<NodeAI> clLi = closedList.iterator();

		NodeAI currentOp = null;
		NodeAI currentCl = null;
		
		g.setColor(new Color(48,48,255));
		while(opLi.hasNext())
		{
			currentOp = (NodeAI) opLi.next();
			g.fillRect((int) currentOp.pos.x*size, (int) currentOp.pos.y*size, size, size);
		}
		
		g.setColor(new Color(120,120,255));
		while(clLi.hasNext())
		{
			currentCl = (NodeAI) clLi.next();
			g.fillRect((int) currentCl.pos.x*size, (int) currentCl.pos.y*size, size, size);
		}
		
		
		if(start.child != null)
		{
			NodeAI current = start;
			g.setColor(Color.RED);
			while(current != null)
			{
				g.fillRect((int) current.pos.x*size, (int) current.pos.y*size, size, size);
				current = current.child;
			}
		}
		paintGrid(g);
	}
}
