package AI;

import java.awt.Graphics;
import java.util.ArrayList;
import Maths.Vector2D;

public class NodeAI
{
	public ArrayList<NodeAI> nodes;
	public NodeAI parent, child;
	public float s, f, t; //start, finish, total
	Vector2D pos;
	
	public NodeAI(NodeAI node)
	{
		nodes = new ArrayList<NodeAI>(node.nodes);
		parent = node.parent;
		child = node.child;
		s = node.s;
		f = node.f;
		t = node.t;
		
		this.pos = node.pos.clone();
	}
	
	public NodeAI(float x, float y)
	{
		nodes = new ArrayList<NodeAI>();
		parent = null;
		child = null;
		
		s = 0;
		f = 0;
		t = 0;
		
		pos = new Vector2D(x, y);
	}
	public NodeAI(Vector2D position)
	{
		nodes = new ArrayList<NodeAI>();
		parent = null;
		child = null;
		
		s = 0;
		f = 0;
		t = 0;
		
		this.pos = position.clone();
	}
	
	public NodeAI clone()
	{
		return new NodeAI(this);
	}
	
	public void computeDistance(NodeAI parent, NodeAI end)
	{
		s = octileDistance(this.pos, parent.pos) + parent.s;
		f = octileDistance(this.pos, end.pos);
		
		/*s = 0;
		f = 0;*/
		t = s + f;
		
	}
	public static float manhattanDistance(Vector2D p0, Vector2D p1)
	{
		return Math.abs(p1.x - p0.x) + Math.abs(p1.y - p0.y);
	}
	
	public static float chebyshevDistance(Vector2D p0, Vector2D p1)
	{
		float dx = Math.abs(p1.x - p0.x);
		float dy = Math.abs(p1.y - p0.y);
		return (dx + dy) - Math.min(dx, dy);
	}
	
	public static float octileDistance(Vector2D p0, Vector2D p1)
	{
		float dx = Math.abs(p1.x - p0.x);
		float dy = Math.abs(p1.y - p0.y);
		return (dx + dy) + (float) ((Math.sqrt(2) - 2)*Math.min(dx, dy));
	}
	
	public static float euclidianDistance(Vector2D p0, Vector2D p1)
	{
		return Vector2D.getMagnitude(p0, p1);
	}
}
