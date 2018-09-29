package Addons;

public class KeyFloat extends Key
{
	public final static long serialVersionUID = 6799141753966439532L;
	public float value;
	
	public KeyFloat(float value, float time)
	{
		this.value = value;
		this.time = time;
		isLinear = true;
	}
	public KeyFloat(float value, float time, boolean isLinear)
	{
		this.value = value;
		this.time = time;
		this.isLinear = isLinear;
	}
	public KeyFloat()
	{
		this.value = 0;
		this.time = 0;
		this.isLinear = false;
	}
}
