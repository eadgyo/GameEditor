package Addons;

import java.awt.Toolkit;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import Base.FileManager;

public class EntitiesGroup implements java.io.Serializable
{
	static final long serialVersionUID = -8541167084528408847L;
	
	private class Action
	{
		public int group;
		public int pos;
		public String name;
	}
	private ArrayList<ArrayList<Entity>> entities;
	private ArrayList<ArrayList<AdvEntity>> advEntities;
	private ArrayList<String> groups;
	private HashMap<String, Integer> groupsHash;
	
	private ArrayList<HashMap<String, Entity>> usedNameEnt;
	private ArrayList<HashMap<String, Entity>> usedNameAdv;
	
	public EntitiesGroup()
	{
		entities = new ArrayList<ArrayList<Entity>>();
		advEntities = new ArrayList<ArrayList<AdvEntity>>();
		groups = new ArrayList<String>();
		
		groupsHash = new HashMap<String, Integer>();
		usedNameEnt = new ArrayList<HashMap<String, Entity>>();
		usedNameAdv = new ArrayList<HashMap<String, Entity>>();
	
		if(groups.size() == 0)
			addGroup("Defaut");
	}
	public void addGroup(String groupsName)
	{
		if(!groupsHash.containsKey(groupsName))
		{
			usedNameEnt.add(new HashMap<String, Entity>());
			usedNameAdv.add(new HashMap<String, Entity>());
			
			groupsHash.put(groupsName, groups.size());
			groups.add(groupsName);
			entities.add(new ArrayList<Entity>());
			advEntities.add(new ArrayList<AdvEntity>());
		}
	}
	public void removeGroup(int n)
	{
		assert(n < groups.size());
		
		usedNameEnt.remove(n);
		usedNameAdv.remove(n);
		
		groupsHash.remove(groups.get(n));
		groups.remove(n);
		entities.remove(n);
		advEntities.remove(n);
	}
	public String getNameGroup(int n)
	{
		assert(n < groups.size());
		return groups.get(n);
	}
	public int size()
	{
		return groups.size();
	}
	
	public Entity getObject(int type, int group, int i)
	{
		if(type == 0)
		{
			return getEntity(group, i);
		}
		else
		{
			return getAdvEntity(group, i);
		}
	}
	
	//Entities
	public void addEntity(int n, Entity entity)
	{
		assert(n < groups.size());
		entity.setGroup(groups.get(n));
		entity.setName(getName(usedNameEnt, n, entity.getName()));
		entity.setGraphics(null);
		entity.setScale(1);
		
		entities.get(n).add(entity);
		
		usedNameEnt.get(n).put(entity.getName(), entity);
	}
	public void removeEntity(int n, int i)
	{
		assert(n < groups.size() && i < entities.get(n).size());
		Entity entity = entities.get(n).remove(i);
		
		usedNameEnt.get(n).remove(entity.getName());
	}
	
	public Entity getEntity(int n, int i)
	{
		return entities.get(n).get(i);
	}
	public ArrayList<Entity> getEntities(int n)
	{
		assert(n < groups.size());
		return entities.get(n);
	}
	public int sizeEntities(int n)
	{
		assert(n < groups.size());
		return entities.get(n).size();
	}

	//AEntities
	public void addAdvEntity(int n, AdvEntity advEntity)
	{
		assert(n < groups.size());
		advEntity.setGroup(groups.get(n));
		advEntity.setName(getName(usedNameAdv, n, advEntity.getName()));
		advEntity.setGraphics(null);
		advEntity.setScale(1);
		
		advEntities.get(n).add(advEntity);
		
		usedNameAdv.get(n).put(advEntity.getName(), advEntity);
	}
	public void removeAdvEntity(int n, int i)
	{
		assert(n < groups.size() && i < advEntities.get(n).size());
		AdvEntity advEntity = advEntities.get(n).remove(i);
		
		usedNameAdv.get(n).remove(advEntity.getName());
	
	}
	public AdvEntity getAdvEntity(int n, int i)
	{
		return advEntities.get(n).get(i);
	}	public ArrayList<AdvEntity> getAdvEntities(int n)
	{
		assert(n < groups.size());
		return advEntities.get(n);
	}
	public int sizeAdvEntities(int n)
	{
		assert(n < groups.size());
		return advEntities.get(n).size();
	}

	public void clearTexture()
	{
		for(int i=0; i<groups.size(); i++)
		{
			for(int j=0; j<entities.get(i).size(); j++)
			{
				entities.get(i).get(j).clearTexture();
			}

			for(int j=0; j<advEntities.get(i).size(); j++)
			{
				advEntities.get(i).get(j).clearTexture();
			}
		}
	}
	public void loadTexture()
	{
		for(int i=0; i<groups.size(); i++)
		{
			for(int j=0; j<entities.get(i).size(); j++)
			{
				entities.get(i).get(j).loadTexture();
			}

			for(int j=0; j<advEntities.get(i).size(); j++)
			{
				advEntities.get(i).get(j).loadTexture();
			}
		}
	}
	public String getName(ArrayList<HashMap<String, Entity>> isUsed, int n, String name)
	{
		String newName = name;
		String append = "";
		int sameName = 0;
		while(isUsed.get(n).containsKey(newName + append))
		{
			sameName++;
			append = "(" + sameName + ")";
		}
		
		newName += append;
		return newName;
	}

}
