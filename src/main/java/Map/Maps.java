package Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Maps 
{
	private ArrayList<Map> maps;
	private HashMap<String, Map> mapsHash;
	
	public Maps()
	{
		maps = new ArrayList<Map>();
		mapsHash = new HashMap<String, Map>();
	}
	
	public String getName(int i)
	{
		assert(i < maps.size());
		return maps.get(i).getName();
	}
	
	public void addMap(Map map)
	{
		map.setName(this.getName(mapsHash, map.getName()));
		maps.add(map);
		mapsHash.put(map.getName(), map);
	}
	public void removeMap(int i)
	{
		mapsHash.remove(maps.get(i).getName());
		maps.remove(i);
	}
	
	public ArrayList<Map> getMaps()
	{
		return maps;
	}
	public Map get(int i)
	{
		assert(i < maps.size());
		return maps.get(i);
	}
	public Map get(String name)
	{
		if(mapsHash.containsKey(name))
		{
			return mapsHash.get(name);
		}
		return null;
	}
	public int size()
	{
		return maps.size();
	}
	
	private String getName(HashMap<String, Map> isUsed, String name)
	{
		String newName = name;
		String append = "";
		int sameName = 0;
		while(isUsed.containsKey(newName + append))
		{
			sameName++;
			append = "(" + sameName + ")";
		}
		
		newName += append;
		return newName;
	}
}
