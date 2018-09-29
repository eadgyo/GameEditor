package Addons;

public class AdvAnimKey implements java.io.Serializable
{
	public final static long serialVersionUID = -8893392654495565425L;
	
	public float duration;
	public String name;
	public boolean isLooping;
	
	public AdvAnimKey(String name, float duration)
	{
		this.name = name;
		this.duration = duration;
		isLooping = false;
	}
	public AdvAnimKey(String name, float duration, boolean isLooping)
	{
		this.name = name;
		this.duration = duration;
		this.isLooping = isLooping;
	}
}