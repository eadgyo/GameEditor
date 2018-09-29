package Maths;

public class Matrix3 implements java.io.Serializable
{
	public final static long serialVersionUID = 360495774787526395L;
	
	float data[];
	
	public Matrix3()
	{
		data = new float[6];
	}
	public Matrix3(Matrix3 m)
	{
		data = new float[6];
		for(int i=0; i<data.length; i++)
		{
			data[i] = m.data[i];
		}
	}
	public Matrix3(float f0, float f1, float f2, float f3, float f4, float f5)
	{
		data = new float[6];
		
		data[0] = f0;
		data[1] = f1;
		data[2] = f2;
		data[3] = f3;
		data[4] = f4;
		data[5] = f5;
	}
	public Matrix3 clone()
	{
		return new Matrix3(this);
	}
	
	public void set(Matrix3 m)
	{
		for(int i=0; i<data.length; i++)
		{
			data[i] = m.data[i];
		}
	}
	
	//Multiplication
	public Matrix3 multiply(Matrix3 o)
	{
		return new Matrix3(
				data[0]*o.data[0] + data[1]*o.data[3],
				data[0]*o.data[1] + data[1]*o.data[4],
				data[0]*o.data[2] + data[1]*o.data[5] + data[2],
				
				data[3]*o.data[0] + data[4]*o.data[3],
				data[3]*o.data[1] + data[4]*o.data[4],
				data[3]*o.data[2] + data[4]*o.data[5] + data[5]
				);
	}
	public void selfMultiply(Matrix3 o)
	{
		float t1;
		float t2;
		float t3;
		t1 = data[0]*o.data[0] + data[1]*o.data[3];
		t2 = data[0]*o.data[1] + data[1]*o.data[4];
		t3 = data[0]*o.data[2] + data[1]*o.data[5] + data[2]*o.data[8];
		data[0] = t1;
		data[1] = t2;
		data[2] = t3;
		
		t1 = data[3]*o.data[0] + data[4]*o.data[3];
		t2 = data[3]*o.data[1] + data[4]*o.data[4];
		t3 = data[3]*o.data[2] + data[4]*o.data[5] + data[5];
		data[3] = t1;
		data[4] = t2;
		data[5] = t3;
	}
	public Vector2D multiply(Vector2D v)
	{
		return new Vector2D(v.x*data[0] + v.y*data[1] + v.z*data[2],
							v.x*data[3] + v.y*data[4] + v.z*data[5],
							v.z);
	}
	public float multiplyX(Vector2D v)
	{
		return v.x*data[0] + v.y*data[1] + v.z*data[2];
	}
	public float multiplyY(Vector2D v)
	{
		return v.x*data[3] + v.y*data[4] + v.z*data[5];
	}
	
	//Transformations
	public void scale(float factor)
	{
		this.scale(factor, new Vector2D());
	}
	public void flipH()
	{
		this.flipH(new Vector2D());
	}
	public void flipV()
	{
		this.flipV(new Vector2D());
	}
	public void transformFree(float factor, boolean flipH, boolean flipV)
	{
		float e = factor*((flipH)?-1:1);
		float f = factor*((flipV)?-1:1);
		data[0] *= e;
		data[3] *= e;
		
		data[1] *= f;
		data[4] *= f;
	}
	public void scale(float factor, Vector2D center)
	{
		data[0] *= factor;
		data[1] *= factor;
		data[3] *= factor;
		data[4] *= factor;
		Vector2D pos = getPos();
		pos.scale(factor, center);
		this.setPos(pos);
	}
	public void flipH(Vector2D center)
	{
		data[0] *= -1f;
		data[3] *= -1f;
		Vector2D pos = getPos();
		pos.flipH(center);
		this.setPos(pos);
	}
	public void flipV(Vector2D center)
	{
		data[1] *= -1f;
		data[4] *= -1f;
		Vector2D pos = getPos();
		pos.flipV(center);
		this.setPos(pos);
	}
	public void rotateRadiansFree(float omega, Vector2D center)
	{
		Vector2D pos = getPos();
		pos.rotateRadians(omega, center);
		this.setPos(pos);
	}
	
	public void translate(Vector2D translate)
	{
		data[2] += translate.x;
		data[5] += translate.y;
	}
	public void translateX(float x)
	{
		data[2] += x;
	}
	public void translateY(float y)
	{
		data[5] += y;
	}
	
	public void setOrientation(float omega)
	{
		data[0] = (float) Math.cos(omega);
		data[1] = (float)-Math.sin(omega);
		data[3] = (float) Math.sin(omega);
		data[4] = (float) Math.cos(omega);
		
	}
	public void setPos(Vector2D pos)
	{
		data[2] = pos.x;
		data[5] = pos.y;
	}
	public void setX(float x)
	{
		data[2] = x;
	}
	public void setY(float y)
	{
		data[5] = y;
	}
	public void setOrientation(float omega, float scale, boolean flipH, boolean flipV)
	{
		this.setOrientation(omega);
		this.transformFree(scale, flipH, flipV);
	}
	public void setOrientation(float omega, float scale, boolean flipH, boolean flipV, Vector2D pos)
	{
		this.setOrientation(omega);
		this.transformFree(scale, flipH, flipV);
		this.setPos(pos);
	}
	
	
	public void setIdentity()
	{
		data[0] = 1f;
		data[1] = 0;
		data[2] = 0;
		data[3] = 0;
		data[4] = 1f;
		data[5] = 0;

	}
	
	//Inverse
	public float getDeterminant()
	{
		return data[0]*data[4] - data[3]*data[1];
	}
	public void setInverse(Matrix3 m)
	{
		float t4 = m.data[0]*m.data[4];
		float t6 = m.data[0]*m.data[5];
		float t8 = m.data[1]*m.data[3];
		float t10 = m.data[2]*m.data[3];
		
		// Calculate the determinant.
		float t16 = (t4 - t8);
		
		// determinant == 0
		if (t16 == 0.0f) 
			return;
		float t17 = 1/t16;
		
		data[0] = (m.data[4])*t17;
		data[1] =-(m.data[1])*t17;
		data[2] = (m.data[1]*m.data[5]-m.data[2]*m.data[4])*t17;
		data[3] =-(m.data[3])*t17;
		data[4] = (m.data[0])*t17;
		data[5] =-(t6-t10)*t17;
	}
	public Matrix3 inverse()
	{
		Matrix3 result = new Matrix3();
		result.setInverse(this);
		return result;
	}
	public void invert()
	{
		setInverse(this);
	}
	
	public Matrix2 convertMatrix2()
	{
		Matrix2 result = new Matrix2();
		result.data[0] = data[0];
		result.data[1] = data[1];
		result.data[2] = data[3];
		result.data[3] = data[4];
		return result;
	}
	
	public static Matrix3 orientation(float omega, float scale, boolean flipH, boolean flipV, Vector2D pos)
	{
		Matrix3 result = new Matrix3();
		result.setOrientation(omega, scale, flipH, flipV, pos);
		return result;
	}
	
	//getter
	public float getX()
	{
		return data[2];
	}
	public float getY()
	{
		return data[5];
	}
	public Vector2D getPos()
	{
		return new Vector2D(data[2], data[5]);
	}
}