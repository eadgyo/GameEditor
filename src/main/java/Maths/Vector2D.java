package Maths;

public class Vector2D implements java.io.Serializable
{
	static final long serialVersionUID = 7416259511882249308L;
	
	public float x, y;
	public int z;//z == 1 point, z == 0 vector
	
	public Vector2D(float x, float y)
	{
		this.x = x;
		this.y = y;
		this.z = 1;
	}
	public Vector2D(float x, float y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector2D(Vector2D p1, Vector2D p2)
	{
		x = p2.x - p1.x;
		y = p2.y - p1.y;
		z = p2.z - p1.z;
	}
	public Vector2D()
	{
		x = 0;
		y = 0;
		z = 1;
	}
	public Vector2D(int z)
	{
		x = 0;
		y = 0;
		this.z = z;
	}
	public Vector2D(Vector2D p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
	}
	public Vector2D clone()
	{
		return new Vector2D(this);
	}
	
	public void reset()
	{
		x = 0;
		y = 0;
	}
	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	public void set(Vector2D p)
	{
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	public void set(Vector2D p1, Vector2D p2)
	{
		x = p2.x - p1.x;
		y = p2.y - p1.y;
		z = p2.z - p1.z;
	}
	
	//Tools
	public float getMagnitude()
	{
		return (float) Math.sqrt(x*x+y*y);
	}
	public float getSqMagnitude()
	{
		return x*x+y*y;
	}
	public float normalize()
	{
		float m = getMagnitude();
		if(m != 0)
		{
			x *= (float)1.0/m;
			y *= (float)1.0/m;
		}
		return m;
	}
	public Vector2D getNormalize()
	{
		Vector2D l_vec = new Vector2D(x, y);
		l_vec.normalize();
		return l_vec;
	}
	public float scalarProduct(Vector2D v)
	{
		return x*v.x + y*v.y;
	}
	public void scalarProduct(float f)
	{
		x *= f;
		y *= f;
	}
	public float crossProductZ(Vector2D v)
	{
		return x*v.y - y*v.x;
	}
	
	public boolean isColinear(Vector2D v)
	{
		if(x*v.y - y*v.x == 0) //vectoriel
			return true;
		else
			return false;
	}
	public void perpendicular()
	{
		float l_x = this.x;
		this.x = -this.y;
		this.y = l_x;
	}
	
	
	public Vector2D getPerpendicular()
	{
		Vector2D l_vec = new Vector2D(x, y);
		l_vec.perpendicular();
		return l_vec;
	}
	public float getAngle(Vector2D v)
	{
		float scalar = (float) getNormalize().scalarProduct(v.getNormalize());
		float l_fTheta = 0;
		if(scalar > 1)
			l_fTheta = 0;
		else if(scalar < -1)
			l_fTheta = (float) Math.PI;
		else
			l_fTheta = (float) Math.acos(scalar);
		
		/*if(Math.abs(l_fTheta) < 0.001) //erase imprecision
			l_fTheta = 0;*/

		if(this.crossProductZ(v) < 0) //&& Math.abs(tempVec.z) > 0.001f
			l_fTheta = - l_fTheta;
		
		
		return l_fTheta;
	}
	
	
	
	public Vector2D add(Vector2D p)
	{
		Vector2D l_p = new Vector2D(x+p.x, y+p.y);
		return l_p;
	}
	public Vector2D addScaledVector(Vector2D p, float factor)
	{
		return this.add(p.multiply(factor));
	}
	public Vector2D sub(Vector2D p)
	{
		Vector2D l_p = new Vector2D(x-p.x, y-p.y, z-p.z);
		return l_p;
	}
	public Vector2D multiply(float scalar)
	{
		Vector2D l_p = new Vector2D(x*scalar,y*scalar);
		return l_p;
	}
	
	public void selfAdd(Vector2D p)
	{
		this.x += p.x;
		this.y += p.y;
	}
	public void selfAddScaledVector(Vector2D p, float factor)
	{
		this.selfAdd(p.multiply(factor));
	}
	public void selfSub(Vector2D p)
	{
		this.x += p.x;
		this.y += p.y;
	}
	public void selfMultiply(float scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
	}
	
	//Transformations
	public void translate(Vector2D p)
	{
		this.x += p.x;
		this.y += p.y;
	}
	public void translateX(float vecX) 
	{
		x += vecX;
	}
	public void translateY(float vecY)
	{
		y += vecY;
	}
	public void rotateDegrees(float omega, Vector2D center)
	{
		//omega = Extend.modulo(omega, 360f);
		rotateRadians((float) (omega*Math.PI)/180, center);
	}
	public void rotateRadians(float omega, Vector2D center)
	{
		omega = -omega;
		float l_x = x;
		float l_y = y;
		x = (float) (Math.cos(omega)*(l_x - center.x) - Math.sin(omega)*(l_y - center.y) + center.x);
		y = (float) (Math.cos(omega)*(l_y - center.y) + Math.sin(omega)*(l_x - center.x) + center.y);
	}
	public void scale(float factor, Vector2D center)
	{
		x = factor*(x - center.x) + center.x;
		y = factor*(y - center.y) + center.y;
	}
	public void flipV(Vector2D center)
	{
		x = -x + 2*center.x;
	}
	public void flipH(Vector2D center)
	{
		y = -y + 2*center.y;
	}

	public float getAngle(Vector2D A, Vector2D C)
	{
		//http://math.stackexchange.com/questions/149959/how-to-find-the-interior-angle-of-an-irregular-pentagon-or-polygon
		Vector2D AB = new Vector2D(this, A);
		Vector2D CB = new Vector2D(C, this);
		float theta = (float) (Math.PI - AB.getAngle(CB));
		return theta;
	}

	public float computeDistance(Vector2D vec, Vector2D p)
	{
		Vector2D projection = this.getProjection(vec, p);
		return this.computeDistance(projection);
	}
	public Vector2D getProjection(Vector2D vec, Vector2D p)
	{
		Vector2D l_vec = vec.getNormalize();
		Vector2D vec1 = new Vector2D(p, this);
		float projectScalar = l_vec.scalarProduct(vec1);
		Vector2D projection = p.add(l_vec.multiply(projectScalar));
		return projection;
	}
	public float computeDistance(Vector2D p)
	{
		Vector2D vec = new Vector2D(this, p);
		return vec.getMagnitude();
	}
			
	public boolean computeIntersection(Vector2D v1, Vector2D v2, Vector2D p1, Vector2D p2)
	{
		if(v1.x*v2.y -v2.x*v1.y != 0)
		{
			if(v1.y != 0)
			{
				y = (-(p1.x*v1.y-p1.y*v1.x)*v2.y + (p2.x*v2.y-p2.y*v2.x)*v1.y)/(v1.x*v2.y-v2.x*v1.y);
				x = (p1.x*v1.y-p1.y*v1.x+v1.x*this.y)/v1.y;
			}
			else
			{
				y = (-(p2.x*v2.y-p2.y*v2.x)*v1.y + (p1.x*v1.y-p1.y*v1.x)*v2.y)/(v2.x*v1.y-v1.x*v2.y);
				x = (p2.x*v2.y-p2.y*v2.x+v2.x*this.y)/v2.y;
			}
			return true;
		}
		return false;//Parallele
	}
	public boolean computeIntersectionSegment(Vector2D v1, Vector2D v2, Vector2D p1, Vector2D p2)
	{
		if(!computeIntersection(v1, v2, p1, p2))
			return false;
		Vector2D l_vec = new Vector2D(p1, this);
		float scalar = v1.getNormalize().scalarProduct(l_vec);
		
		if(scalar < 0 || scalar > v1.getMagnitude())
			return false;
		
		l_vec.set(p2, this);
		scalar = v2.getNormalize().scalarProduct(l_vec);
		if(scalar < 0 || scalar > v2.getMagnitude())
			return false;
		
		return true;
	}

	public static float getSqMagnitude(Vector2D p0, Vector2D p1)
	{
		return ((p1.x - p0.x)*(p1.x - p0.x)) + ((p1.y - p0.y)*(p1.y - p0.y));
	}
	public static float getMagnitude(Vector2D p0, Vector2D p1)
	{
		return (float) Math.sqrt(getSqMagnitude(p0, p1));
	}
}
