package Addons;

public class Key implements java.io.Serializable
{
	public final static long serialVersionUID = 2641480486157054947L;
	public float time;
	public boolean isLinear;
	
	public Key( float time)
	{
		this.time = time;
		isLinear = true;
	}
	public Key(float time, boolean linear)
	{
		this.time = time;
		this.isLinear = linear;
	}
	public Key()
	{
		this.time = 0;
		this.isLinear = false;
	}
}

