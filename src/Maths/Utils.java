package Maths;

public class Utils 
{
	public static float orientation(Vector2D p, Vector2D q, Vector2D r)
	{
		return (q.x*r.y + p.x*q.y + r.x*p.y) - (p.x*r.y + q.x*p.y + r.x*q.y);
	}
}
