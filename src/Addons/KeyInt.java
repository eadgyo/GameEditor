package Addons;

public class KeyInt extends Key
{
	public final static long serialVersionUID = -39063271131764210L;
	public int value;
	
	public KeyInt(int value, float time)
	{
		this.value = value;
		this.time = time;
		isLinear = true;
	}
	public KeyInt(int value, float time, boolean isLinear)
	{
		this.value = value;
		this.time = time;
		this.isLinear = isLinear;
	}
	public KeyInt()
	{
		this.value = 0;
		this.time = 0;
		this.isLinear = false;
	}
}
