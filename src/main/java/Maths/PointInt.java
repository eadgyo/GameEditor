package Maths;

public class PointInt implements java.io.Serializable
{
	public final static long serialVersionUID = 3712578391244325293L;
	
	public int x, y;
	
	public PointInt(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public PointInt()
	{
		x = 0;
		y = 0;
	}
	public PointInt clone()
	{
		return new PointInt(x,y);
	}
	
	public void reset()
	{
		x = 0;
		y = 0;
	}
	
	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public PointInt add(PointInt p)
	{
		PointInt l_p = new PointInt(x+p.x, y+p.y);
		return l_p;
	}
	public PointInt sub(PointInt p)
	{
		PointInt l_p = new PointInt(x-p.x, y-p.y);
		return l_p;
	}
	public PointInt multiply(float scalar)
	{
		PointInt l_p = new PointInt((int) (x*scalar),(int) (y*scalar));
		return l_p;
	}
}
