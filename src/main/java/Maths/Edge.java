package Maths;

public class Edge //implements Comparable<Edge>
{
	public Edge prev;
	public Edge next;
	public PointType p0;
	public PointType p1;
	
	public float getMinX()
	{
		if(p0.x < p1.x)
			return p0.x;
		else
			return p1.x;
	}
	/*
	@Override
	public int compareTo(Edge edge) 
	{
		if(edge.hashCode() >= this.hashCode())
			return -1;
		else
			return 1;
	}*/
}
