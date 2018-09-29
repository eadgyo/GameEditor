package AI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class PathFinding
{
	private HashSet<NodeAI> openList;
	private HashSet<NodeAI> closedList;
	private ArrayList<NodeAI> openListT = new ArrayList<NodeAI>();
	private ArrayList<NodeAI> closedListT = new ArrayList<NodeAI>();
	
	private NodeAI start, end;
	private NodeAI current;
	
	public PathFinding()
	{
		openList = new HashSet<NodeAI>();
		closedList = new HashSet<NodeAI>();
	}
	
	public NodeAI findPath()
	{
		start.parent = null;
		current = start;
		start.computeDistance(start, end);
		addNeighbours(current);
		addClosedList(current);
		while(current != end && openList.size() != 0)
		{
			current = bestOpenList();
			addNeighbours(current);
			addClosedList(current);
		}
		
		if(current == end)
		{
			while(current.parent != null)
			{
				current.parent.child = current;
				current = current.parent;
			}
		}
		else
			start.child = null;
		return start;
	}
	
	public void startIterate()
	{
		current = start;
		start.computeDistance(start, end);
		addClosedList(current);
		addNeighbours(current);
	}
	public boolean iterate()
	{
		if(current != end && openList.size() != 0)
		{
			current = bestOpenList();
			addClosedList(current);
			addNeighbours(current);
			return false;
		}
		
		if(current == end)
		{
			while(current.parent != null)
			{
				current.parent.child = current;
				current = current.parent;
			}
		}
		else
			start.child = null;
		
		return true;
	}
	
	public NodeAI bestOpenList()
	{
		Iterator<NodeAI> opLi = openList.iterator();
		NodeAI best = (NodeAI) opLi.next();
		while(opLi.hasNext())
		{
			NodeAI tmp = (NodeAI) opLi.next();
			if(best.t > tmp.t)
			{
				best = tmp;
			}
			else if(best.t == tmp.t)
			{
				if(best.f > tmp.f)
					best = tmp;
			}
		}
		
		openList.remove(best);
		openListT.remove(best);
		return best;
	}
	
	public void addNeighbours(NodeAI current)
	{
		ArrayList<NodeAI> nodes = current.nodes;
		for(int i=0; i<nodes.size(); i++)
		{
			if(openList.contains(nodes.get(i)))
			{
				NodeAI clone = nodes.get(i).clone();
				clone.computeDistance(current, end);
				if(clone.t < nodes.get(i).t)
				{
					nodes.get(i).parent = current;
					nodes.get(i).child = null;
					nodes.get(i).s = clone.s;
					nodes.get(i).f = clone.f;
					nodes.get(i).t = clone.t;
				}
			}
			else if(!closedList.contains(nodes.get(i)))
			{
				//on ajoute dans l'openList
				nodes.get(i).computeDistance(current, end);
				nodes.get(i).parent = current;
				openList.add(nodes.get(i));
				openListT.add(nodes.get(i));
			}
		}
	}
	public void addClosedList(NodeAI current)
	{
		openList.remove(current);
		closedList.add(current);
	}

	//getter
	public HashSet<NodeAI> getOpenList()
	{
		return openList;
	}
	public HashSet<NodeAI> getClosedList()
	{
		return closedList;
	}
	
	//setter
	public void setStart(NodeAI start)
	{
		this.start = start;
		start.child = null;
		start.parent = null;
	}
	public void setEnd(NodeAI end)
	{
		this.end = end;
		end.child = null;
		end.parent = null;
	}
}
