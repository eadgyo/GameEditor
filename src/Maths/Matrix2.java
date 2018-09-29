package Maths;

public class Matrix2 implements java.io.Serializable
{
	static final long serialVersionUID = -5874085396674674183L;
	
	float data[];

	public Matrix2()
	{
		data = new float[4];
	}
	public Matrix2(Matrix2 m)
	{
		data = new float[4];
		this.set(m);
	}
	public Matrix2(float f0, float f1, float f2, float f3)
	{
		data[0] = f0;
		data[1] = f1;
		data[2] = f2;
		data[3] = f3;
	}
	public Matrix2 clone()
	{
		return new Matrix2(this);
	}
	
	public void set(Matrix2 m)
	{
		for(int i=0; i<data.length; i++)
		{
			data[i] = m.data[i];
		}
	}
	
	//Multiplication
	public Matrix2 multiply(Matrix2 m)
	{
		//this == gauche
		Matrix2 result = new Matrix2();
		result.data[0] = this.data[0]*m.data[0] + data[1]*m.data[2];
		result.data[1] = this.data[0]*m.data[1] + data[1]*m.data[3];
		result.data[2] = this.data[2]*m.data[0] + data[3]*m.data[2];
		result.data[3] = this.data[2]*m.data[1] + data[3]*m.data[3];
		return result;
	}
	public void selfMultiply(Matrix2 m)
	{
		Matrix2 result = this.multiply(m);
		this.set(result);
	}
	public Vector2D multiply(Vector2D v)
	{
		Vector2D vec = new Vector2D();
		vec.x = v.x*data[0] + v.y*data[1];
		vec.y = v.x*data[2] + v.y*data[3];
		return vec;
	}
	public float multiplyX(Vector2D v)
	{
		return v.x*data[0] + v.y*data[1];
	}
	public float multiplyY(Vector2D v)
	{
		return v.x*data[2] + v.y*data[3];
	}
	
	//Transformations
	public void scale(float factor)
	{
		data[0] *= factor;
		data[1] *= factor;
		data[2] *= factor;
		data[3] *= factor;
	}
	public void flipH()
	{
		data[0] *= -1f;
		data[2] *= -1f;
	}
	public void flipV()
	{
		data[1] *= -1f;
		data[3] *= -1f;
	}
	public void transform(float factor, boolean flipH, boolean flipV)
	{
		float e = factor*((flipH)?-1:1);
		float f = factor*((flipV)?-1:1);
		data[0] *= e;
		data[1] *= f;
		data[2] *= e;
		data[3] *= f;
	}
	
	//Creation
	public void setOrientation(float omega)
	{
		data[0] = (float) Math.cos(omega);
		data[1] = (float)-Math.sin(omega);
		data[2] = (float) Math.sin(omega);
		data[3] = (float) Math.cos(omega);
	}
	public void setOrientation(float omega, float scale, boolean flipH, boolean flipV)
	{
		this.setOrientation(omega);
		
		float e = scale*((flipH)?-1:1);
		float f = scale*((flipV)?-1:1);
		data[0] *= e;
		data[1] *= f;
		data[2] *= e;
		data[3] *= f;

	}
	public void setZero()
	{
		for(int i=0; i<data.length; i++)
		{
			data[i] = 0;
		}
	}
	
	public void setIdentity()
	{
		data[0] = 1f;
		data[1] = 0;
		data[2] = 0;
		data[3] = 1f;
	}
	
	public float getDeterminant()
	{
		return data[0]*data[3] - data[2]*data[1];
	}
	public void setInverse(Matrix2 m)
	{
		float determinant = m.getDeterminant();
		assert(determinant != 0): "Matrice non inversible";
		float inverse = 1/determinant;
		
		data[0] = m.data[3] * inverse;
		data[1] =-m.data[1] * inverse;
		data[2] =-m.data[2] * inverse;
		data[3] = m.data[0] * inverse;
	}
	public Matrix2 inverse()
	{
		Matrix2 result = new Matrix2();
		result.setInverse(this);
		return result;
	}
	
	public static Matrix2 orientation(float omega)
	{
		Matrix2 result = new Matrix2();
		result.setOrientation(omega);
		return result;
	}
	public static Matrix2 orientation(float omega, float scale, boolean flipH, boolean flipV)
	{
		Matrix2 result = Matrix2.orientation(omega);
		result.transform(scale, flipH, flipV);
		return result;
	}
	
	public static Matrix2 zero()
	{
		Matrix2 result = new Matrix2();
		result.setZero();
		return result;
	}
	public static Matrix2 identity()
	{
		Matrix2 result = new Matrix2();
		result.setIdentity();
		return result;
	}
	

}
