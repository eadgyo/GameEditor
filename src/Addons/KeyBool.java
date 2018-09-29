package Addons;

public class KeyBool extends Key
{
	public final static long serialVersionUID = 2469929514906410126L;
	public boolean value;
	
	public KeyBool(boolean value, float time)
	{
		this.value = value;
		this.time = time;
		isLinear = true;
	}
	public KeyBool(boolean value, float time, boolean linear)
	{
		this.value = value;
		this.time = time;
		this.isLinear = linear;
	}
	public KeyBool()
	{
		this.value = false;
		this.time = 0;
		this.isLinear = false;
	}
}
