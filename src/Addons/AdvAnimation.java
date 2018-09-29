package Addons;

import java.util.ArrayList;

import AdvEntityEditor.KeyContainer;
import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;

public class AdvAnimation implements java.io.Serializable
{
	public static final long serialVersionUID = -283253520855936120L;
	
	public static final int OPTIONS = 8;
	
	private boolean isPlaying;
	
	private ArrayList<ArrayList<KeyFloat>> posXKeys;
	private ArrayList<ArrayList<KeyFloat>> posYKeys;
	private ArrayList<ArrayList<KeyFloat>> scaleKeys;
	private ArrayList<ArrayList<KeyFloat>> rotKeys;
	private ArrayList<ArrayList<KeyInt>> animKeys;
	private ArrayList<ArrayList<KeyBool>> symVKeys;
	private ArrayList<ArrayList<KeyBool>> symHKeys;
	private ArrayList<ArrayList<KeyFloat>> visibleKeys;
	
	private AdvEntity advEntity;
	
	private ArrayList<AdvAnimKey> advAnimKeys;
	
	private int currentAdvAnim;
	private int currentPosX;
	private int currentPosY;
	private int currentScale;
	private int currentRot;
	private int currentAnim;
	private int currentSymV;
	private int currentSymH;
	private int currentVisible;
	
	private float currentTime;
	private float savedTime;
	
	private float savedX;
	private float savedY;
	private float savedScale;
	private float savedRot;
	private float savedVisible;
	private boolean flipV;
	private boolean flipH;
	
	private float velocityPosX;
	private float velocityPosY;
	private float velocityScale;
	private float velocityRot;
	private float velocityVisible;
	
	public AdvAnimation()
	{
		isPlaying = true;
		
		advEntity = null;
		currentAdvAnim = -1;
		advAnimKeys = new ArrayList<AdvAnimKey>();
		posXKeys = new ArrayList<ArrayList<KeyFloat>>();
		posYKeys = new ArrayList<ArrayList<KeyFloat>>();
		rotKeys = new ArrayList<ArrayList<KeyFloat>>();
		scaleKeys = new ArrayList<ArrayList<KeyFloat>>();
		animKeys = new ArrayList<ArrayList<KeyInt>>();
		symVKeys = new ArrayList<ArrayList<KeyBool>>();
		symHKeys = new ArrayList<ArrayList<KeyBool>>();
		visibleKeys = new ArrayList<ArrayList<KeyFloat>>();
		
		reset();
	}
	public void add(String name, float duration)
	{
		add(name, duration, false);
	}
	public void add(String name, float duration, boolean isLooping)
	{
		advAnimKeys.add(new AdvAnimKey(name, duration, isLooping));
		posXKeys.add(new ArrayList<KeyFloat>());
		posYKeys.add(new ArrayList<KeyFloat>());
		rotKeys.add(new ArrayList<KeyFloat>());
		scaleKeys.add(new ArrayList<KeyFloat>());
		animKeys.add(new ArrayList<KeyInt>());
		symVKeys.add(new ArrayList<KeyBool>());
		symHKeys.add(new ArrayList<KeyBool>());
		visibleKeys.add(new ArrayList<KeyFloat>());
	}
	public void add(AdvAnimation advAnim, int n, int maxAnim)
	{
		assert(n < advAnim.getAll().size());
		add(advAnim.getAll().get(n).name, advAnim.getAll().get(n).duration, advAnim.getAll().get(n).isLooping);
		currentAdvAnim = advAnimKeys.size() - 1;
		for(int i=0; i<advAnim.getKeysPosX(n).size(); i++)
		{
			KeyFloat keyFloat = advAnim.getKeysPosX(n).get(i);
			addKeyPosX(keyFloat.value, keyFloat.time, keyFloat.isLinear);
		}
		for(int i=0; i<advAnim.getKeysPosY(n).size(); i++)
		{
			KeyFloat keyFloat = advAnim.getKeysPosY(n).get(i);
			addKeyPosY(keyFloat.value, keyFloat.time, keyFloat.isLinear);
		}
		for(int i=0; i<advAnim.getKeysScale(n).size(); i++)
		{
			KeyFloat keyFloat = advAnim.getKeysScale(n).get(i);
			addKeyScale(keyFloat.value, keyFloat.time, keyFloat.isLinear);
		}
		for(int i=0; i<advAnim.getKeysRot(n).size(); i++)
		{
			KeyFloat keyFloat = advAnim.getKeysRot(n).get(i);
			addKeyRot(keyFloat.value, keyFloat.time, keyFloat.isLinear);
		}
		
		for(int i=0; i<advAnim.getKeysAnim(n).size(); i++)
		{
			KeyInt keyInt = advAnim.getKeysAnim(n).get(i);
			if(maxAnim == -1 || keyInt.value < maxAnim)
				addKeyAnim(keyInt.value, keyInt.time);
		}
		for(int i=0; i<advAnim.getKeysSymV(n).size(); i++)
		{
			KeyBool keyBool = advAnim.getKeysSymV(n).get(i);
			addKeySymV(keyBool.value, keyBool.time);
		}
		for(int i=0; i<advAnim.getKeysSymH(n).size(); i++)
		{
			KeyBool keyBool = advAnim.getKeysSymH(n).get(i);
			addKeySymH(keyBool.value, keyBool.time);
		}
		
		for(int i=0; i<advAnim.getKeysVisible(n).size(); i++)
		{
			KeyFloat keyFloat = advAnim.getKeysVisible(n).get(i);
			addKeyVisible(keyFloat.value, keyFloat.time, keyFloat.isLinear);
		}
		
	}
	public void add(AdvAnimation advAnim, int maxAnim)
	{
		for(int i=0; i<advAnim.getAll().size(); i++)
		{
			add(advAnim, i, maxAnim);
		}
	}
	
	public void remove(int n)
	{
		assert(n < advAnimKeys.size());
		posXKeys.get(n).clear();
		posXKeys.remove(n);
		
		posYKeys.get(n).clear();
		posYKeys.remove(n);
		
		rotKeys.get(n).clear();
		rotKeys.remove(n);
		
		scaleKeys.get(n).clear();
		scaleKeys.remove(n);
		
		animKeys.get(n).clear();
		animKeys.remove(n);
		
		symVKeys.get(n).clear();
		symVKeys.remove(n);
		
		symHKeys.get(n).clear();
		symHKeys.remove(n);
		
		visibleKeys.get(n).clear();
		visibleKeys.remove(n);
		
		advAnimKeys.remove(n);
	}
	public AdvAnimation clone()
	{
		AdvAnimation l_advAnim = new AdvAnimation();
		//ajout des noms
		for(int i=0; i<advAnimKeys.size(); i++)
		{
			l_advAnim.add(advAnimKeys.get(i).name, advAnimKeys.get(i).duration, advAnimKeys.get(i).isLooping);
		}
		for(int i=0; i<posXKeys.size(); i++)
		{
			for(int j=0; j<posXKeys.get(i).size(); j++)
			{
				KeyFloat keyFloat = posXKeys.get(i).get(j);
				l_advAnim.addKeyPosX(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
			}
		}
		for(int i=0; i<posYKeys.size(); i++)
		{
			for(int j=0; j<posYKeys.get(i).size(); j++)
			{
				KeyFloat keyFloat = posYKeys.get(i).get(j);
				l_advAnim.addKeyPosY(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
			}
		}
		for(int i=0; i<scaleKeys.size(); i++)
		{
			for(int j=0; j<scaleKeys.get(i).size(); j++)
			{
				KeyFloat keyFloat = scaleKeys.get(i).get(j);
				l_advAnim.addKeyScale(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
			}
		}
		for(int i=0; i<rotKeys.size(); i++)
		{
			for(int j=0; j<rotKeys.get(i).size(); j++)
			{
				KeyFloat keyFloat = rotKeys.get(i).get(j);
				l_advAnim.addKeyRot(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
			}
		}
		for(int i=0; i<animKeys.size(); i++)
		{
			for(int j=0; j<animKeys.get(i).size(); j++)
			{
				KeyInt keyInt = animKeys.get(i).get(j);
				l_advAnim.addKeyAnim(i, keyInt.value, keyInt.time);
			}
		}
		for(int i=0; i<symVKeys.size(); i++)
		{
			for(int j=0; j<symVKeys.get(i).size(); j++)
			{
				KeyBool keyBool = symVKeys.get(i).get(j);
				l_advAnim.addKeySymV(i, keyBool.value, keyBool.time);
			}
		}
		for(int i=0; i<symHKeys.size(); i++)
		{
			for(int j=0; j<symHKeys.get(i).size(); j++)
			{
				KeyBool keyBool = symHKeys.get(i).get(j);
				l_advAnim.addKeySymV(i, keyBool.value, keyBool.time);
			}
		}
		for(int i=0; i<visibleKeys.size(); i++)
		{
			for(int j=0; j<visibleKeys.get(i).size(); j++)
			{
				KeyFloat keyFloat = visibleKeys.get(i).get(j);
				l_advAnim.addKeyVisible(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
			}
		}
		return l_advAnim;
	}
	
	public void setIsPlaying(boolean b)
	{
		isPlaying = b;
	}
	public boolean getIsPlaying()
	{
		return isPlaying;
	}
	
	public void setCurrentTime(float currentTime)
	{
		this.currentTime = currentTime;
		this.savedTime = currentTime;
		if(this.advEntity != null)
			this.advEntity.getAnim().setTime(currentTime*1000f);
	}
	public void setSavedTime(float savedTime)
	{
		this.savedTime = savedTime;
	}
	public float getCurrentTime()
	{
		return currentTime;
	}
	public float getSavedTime()
	{
		return savedTime;
	}
	
	public AdvEntity getAdvEntity()
	{
		return advEntity;
	}
	public void setAdvEntity(AdvEntity advEntity)
	{
		this.advEntity = advEntity;
	}
	
	public void clear()
	{
		reset();
		while(advAnimKeys.size() != 0)
		{
			this.remove(0);
		}
	}
	public void reset()
	{
		currentPosX = -1;
		currentPosY = -1;
		currentScale = -1;
		currentRot = -1;
		currentAnim = -1;
		currentSymV = -1;
		currentSymH = -1;
		currentVisible = -1;
		
		currentTime = 0;
		savedTime = 0;
		
		savedX = 0;
		savedY = 0;
		savedScale = 1;
		savedRot = 0;
		savedVisible = 100f;
		
		velocityPosX = 0;
		velocityPosY = 0;
		velocityScale = 0;
		velocityRot = 0;
		velocityVisible = 0;
		flipH = false;
		flipV = false;
	}
	public void initAll()
	{
		if(getKeysPosX().size() > 0)
			setCurrentPosX(0);
		if(getKeysPosY().size() > 0)
			setCurrentPosY(0);
		if(getKeysRot().size() > 0)
			setCurrentRot(0);
		if(getKeysScale().size() > 0)
			setCurrentScale(0);
		if(getKeysAnim().size() > 1)
			setCurrentAnim(1);
		if(getKeysSymV().size() > 1)
			setCurrentSymV(1);
		if(getKeysSymH().size() > 1)
			setCurrentSymH(1);
		if(getKeysVisible().size() > 0)
			setCurrentVisible(0);
	}
	
	//AdvAnim
	public void setCurrentAdvAnim(int currentAdvAnim)
	{
		this.currentAdvAnim = currentAdvAnim;
	}
	public int getCurrentAdvAnim()
	{
		return currentAdvAnim;
	}
	
	///Name
	public void setName(int n, String name)
	{
		assert(n<advAnimKeys.size());
		advAnimKeys.get(n).name = name;
	}
	public void setName(String name)
	{
		assert(currentAdvAnim!=-1);
		setName(currentAdvAnim, name);
	}
	public String getName(int n)
	{
		assert(n<advAnimKeys.size());
		return advAnimKeys.get(n).name;
	}
	public String getName()
	{
		assert(currentAdvAnim!=-1);
		return getName(currentAdvAnim);
	}
	
	///Duration
	public void setDuration(int n, float duration)
	{
		assert(n<advAnimKeys.size());
		advAnimKeys.get(n).duration = duration;
	}
	public void setDuration(float duration)
	{
		assert(currentAdvAnim!=-1);
		setDuration(currentAdvAnim, duration);
	}
	public float getDuration(int n)
	{
		assert(n<advAnimKeys.size());
		return advAnimKeys.get(n).duration;
	}
	public float getDuration()
	{
		assert(currentAdvAnim!=-1);
		return getDuration(currentAdvAnim);
	}
	
	///Looping
	public void setLooping(int n, boolean isLooping)
	{
		assert(n<advAnimKeys.size());
		advAnimKeys.get(n).isLooping = isLooping;
	}
	public void setLooping(boolean isLooping)
	{
		assert(currentAdvAnim!=-1);
		setLooping(currentAdvAnim, isLooping);
	}
	public boolean getLooping(int n)
	{
		assert(n<advAnimKeys.size());
		return advAnimKeys.get(n).isLooping;
	}
	public boolean getLooping()
	{
		assert(currentAdvAnim!=-1);
		return getLooping(currentAdvAnim);
	}
	
	///AnimInt
	public void set(int n, AdvAnimKey anim)
	{
		assert(n<advAnimKeys.size());
		advAnimKeys.get(n).duration = anim.duration;
		advAnimKeys.get(n).isLooping = anim.isLooping;
		advAnimKeys.get(n).name = anim.name;
	}
	public void set(AdvAnimKey anim)
	{
		assert(currentAdvAnim!=-1);
		set(currentAdvAnim, anim);
	}
	public AdvAnimKey get(int n)
	{
		assert(n<advAnimKeys.size());
		return advAnimKeys.get(n);
	}
	public AdvAnimKey get()
	{
		assert(currentAdvAnim!=-1);
		return get(currentAdvAnim);
	}
	public ArrayList<AdvAnimKey> getAll()
	{
		return advAnimKeys;
	}
	
	public static int getOption(int n)
	{
		switch(n)
		{
			case 0:
				return 0;
			case 1:
				return 0;
			case 2:
				return 0;
			case 3:
				return 0;
			case 4:
				return 1;
			case 5:
				return 2;
			case 6:
				return 2;
			case 7:
				return 0;
			default:
				return -1;
		}
	}
	public static String getOptionName(int n)
	{
		switch(n)
		{
			case 0:
				return "posXKeys";
			case 1:
				return "posYKeys";
			case 2:
				return "scaleKeys";
			case 3:
				return "rotKeys";
			case 4:
				return "animKeys";
			case 5:
				return "symVKeys";
			case 6:
				return "symHKeys";
			case 7:
				return "visibleKeys";
			default:
				return "null";
		}
	}
	public ArrayList<? extends Key> getKeys(int n, int j)
	{
		switch(n)
		{
			case 0:
				return getKeysPosX(j);
			case 1:
				return getKeysPosY(j);
			case 2:
				return getKeysScale(j);
			case 3:
				return getKeysRot(j);
			case 4:
				return getKeysAnim(j);
			case 5:
				return getKeysSymV(j);
			case 6:
				return getKeysSymH(j);
			default:
				return getKeysVisible(j);
		}
	}
	public ArrayList<? extends Key> getKeys(int n)
	{
		return getKeys(n, currentAnim);
	}
	public ArrayList<? extends ArrayList<? extends Key>> getAllKeys(int n)
	{
		switch(n)
		{
			case 0:
				return getAllKeysPosX();
			case 1:
				return getAllKeysPosY();
			case 2:
				return getAllKeysScale();
			case 3:
				return getAllKeysRot();
			case 4:
				return getAllKeysAnim();
			case 5:
				return getAllKeysSymV();
			case 6:
				return getAllKeysSymH();
			default:
				return getAllKeysVisible();
		}
	}
	public int getCurrent(int n)
	{
		switch(n)
		{
			case 0:
				return currentPosX;
			case 1:
				return currentPosY;
			case 2:
				return currentScale;
			case 3:
				return currentRot;
			case 4:
				return currentAnim;
			case 5:
				return currentSymV;
			case 6:
				return currentSymH;
			case 7:
				return currentVisible;
			default:
				return -1;
		}
	}
	public static String getCurrentName(int n)
	{
		switch(n)
		{
			case 0:
				return "currentPosX";
			case 1:
				return "currentPosY";
			case 2:
				return "currentScale";
			case 3:
				return "currentRot";
			case 4:
				return "currentAnim";
			case 5:
				return "currentSymV";
			case 6:
				return "currentSymH";
			case 7:
				return "currentVisible";
			default:
				return "null";
		}
	}
	public void addKey(int n, int i, Key key)
	{
		KeyFloat keyFloat = null;
		KeyInt keyInt = null;
		KeyBool keyBool = null;
		switch(n)
		{
			case 0:
				keyFloat = (KeyFloat) key;
				addKeyPosX(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
				break;
			case 1:
				keyFloat = (KeyFloat) key;
				addKeyPosY(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
				break;
			case 2:
				keyFloat = (KeyFloat) key;
				addKeyScale(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
				break;
			case 3:
				keyFloat = (KeyFloat) key;
				addKeyRot(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
				break;
			case 4:
				keyInt = (KeyInt) key;
				addKeyAnim(i, keyInt.value, keyInt.time, keyInt.isLinear);
				break;
			case 5:
				keyBool = (KeyBool) key;
				addKeySymV(i, keyBool.value, keyBool.time, keyBool.isLinear);
				break;
			case 6:
				keyBool = (KeyBool) key;
				addKeySymH(i, keyBool.value, keyBool.time, keyBool.isLinear);
				break;
			case 7:
				keyFloat = (KeyFloat) key;
				addKeyVisible(i, keyFloat.value, keyFloat.time, keyFloat.isLinear);
				break;
			default:
				assert(false);
		}
	}
	
	//PosX
	public void setSavedPosX(float savedX)
	{
		this.savedX = savedX;
	}
	public float getSavedPosX()
	{
		return savedX;
	}
	public void addKeyPosX(int n, float value, float time, boolean isLinear)
	{
		assert(n<posXKeys.size());
		posXKeys.get(n).add(new KeyFloat(value, time, isLinear));
		
	}
	public void addKeyPosX(float value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeyPosX(currentAdvAnim, value, time, isLinear);
	}
	public void addKeyPosX(int n, float value, float time)
	{
		assert(n<posXKeys.size());
		posXKeys.get(n).add(new KeyFloat(value, time));
	}
	public void addKeyPosX(float value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeyPosX(currentAdvAnim, value, time);
	}
	public KeyFloat getKeyPosX(int n, int i)
	{
		assert(n<posXKeys.size() && i<posXKeys.get(n).size());
		return posXKeys.get(n).get(i);
	}
	public KeyFloat getKeyPosX(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeyPosX(currentAdvAnim, i);
	}
	public ArrayList<KeyFloat> getKeysPosX(int n)
	{
		assert(n<posXKeys.size());
		return posXKeys.get(n);
	}
	public ArrayList<KeyFloat> getKeysPosX()
	{
		assert(currentAdvAnim!=-1);
		return getKeysPosX(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyFloat>> getAllKeysPosX()
	{
		return posXKeys;
	}
	public void setCurrentPosX(int n)
	{
		assert(currentAdvAnim != -1 && n<posXKeys.get(currentAdvAnim).size());
		currentPosX = n;
		
		if(n == 0)
		{
			velocityPosX = 0;
		}
		else
		{
			float dt = posXKeys.get(currentAdvAnim).get(currentPosX).time - posXKeys.get(currentAdvAnim).get(currentPosX - 1).time;
			if(Math.abs(dt) < 0.01)
				velocityPosX = 0;
			else
				velocityPosX = (posXKeys.get(currentAdvAnim).get(currentPosX).value - posXKeys.get(currentAdvAnim).get(currentPosX - 1).value)/
						(dt);
		}
	}
	public int getCurrentPosX()
	{
		return currentPosX;
	}
	public float getPosX()
	{
		if(currentAdvAnim == -1 || posXKeys.get(currentAdvAnim).size() == 0)
			return 0;
		else if(currentPosX == -1)
			return posXKeys.get(currentAdvAnim).get(posXKeys.get(currentAdvAnim).size() - 1).value;
		else if(currentPosX == 0)
			return (currentTime - savedTime)*velocityPosX + savedX;
		else if(posXKeys.get(currentAdvAnim).get(currentPosX).isLinear)
			return (currentTime - posXKeys.get(currentAdvAnim).get(currentPosX - 1).time)*velocityPosX + posXKeys.get(currentAdvAnim).get(currentPosX - 1).value;
		else	
			return posXKeys.get(currentAdvAnim).get(currentPosX - 1).value;
	}
	public void updatePosX(Vector2D vec, Vector2D translate)
	{
		if(posXKeys.size() != 0 && posXKeys.get(currentAdvAnim).size() != 0)
		{
			if(currentPosX != -1 && currentTime > posXKeys.get(currentAdvAnim).get(currentPosX).time)
			{
				do
				{
					currentPosX++;
					if(currentPosX >= posXKeys.get(currentAdvAnim).size())
					{
						currentPosX = -1;
						velocityPosX = 0;
						break;
					}
				}while(currentTime > posXKeys.get(currentAdvAnim).get(currentPosX).time);
				
				if(currentPosX != -1)
				{
					float dt = posXKeys.get(currentAdvAnim).get(currentPosX).time - posXKeys.get(currentAdvAnim).get(currentPosX - 1).time;
					if(Math.abs(dt) < 0.01)
						velocityPosX = 0;
					else
						velocityPosX = (posXKeys.get(currentAdvAnim).get(currentPosX).value - posXKeys.get(currentAdvAnim).get(currentPosX - 1).value)/
								(dt);
				}
			}
			else if(currentPosX == 0)
			{
				float computeX =  this.advEntity.getX() - translate.x;
				if(velocityPosX == 0)
				{
					savedX = computeX;
					float dt = posXKeys.get(currentAdvAnim).get(currentPosX).time - savedTime;
					if(Math.abs(dt) < 0.01)
						velocityPosX = 0;
					else
						velocityPosX = (posXKeys.get(currentAdvAnim).get(currentPosX).value - savedX)/
								(dt);
				}
			}
		}
		else
		{
			currentPosX = -1;
		}
		this.advEntity.setPositionX(this.getPosX()*vec.getMagnitude(), vec.getNormalize());
	}
	
	//PosY
	public void setSavedPosY(float savedY)
	{
		this.savedY = savedY;
	}
	public float getSavedPosY()
	{
		return savedY;
	}
	public void addKeyPosY(int n, float value, float time, boolean isLinear)
	{
		assert(n<posYKeys.size());
		posYKeys.get(n).add(new KeyFloat(value, time, isLinear));
	}
	public void addKeyPosY(float value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeyPosY(currentAdvAnim, value, time, isLinear);
	}
	public void addKeyPosY(int n, float value, float time)
	{
		assert(n<posYKeys.size());
		posYKeys.get(n).add(new KeyFloat(value, time));
	}
	public void addKeyPosY(float value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeyPosY(currentAdvAnim, value, time);
	}
	public KeyFloat getKeyPosY(int n, int i)
	{
		assert(n<posYKeys.size() && i<posYKeys.get(n).size());
		return posYKeys.get(n).get(i);
	}
	public KeyFloat getKeyPosY(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeyPosY(currentAdvAnim, i);
	}
	public ArrayList<KeyFloat> getKeysPosY(int n)
	{
		assert(n<posYKeys.size());
		return posYKeys.get(n);
	}
	public ArrayList<KeyFloat> getKeysPosY()
	{
		assert(currentAdvAnim!=-1);
		return getKeysPosY(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyFloat>> getAllKeysPosY()
	{
		return posYKeys;
	}
	public void setCurrentPosY(int n)
	{
		assert(currentAdvAnim != -1 && n<posYKeys.get(currentAdvAnim).size());
		currentPosY = n;
		
		if(n == 0)
		{
			velocityPosX = 0;
		}
		else
		{
			float dt = posYKeys.get(currentAdvAnim).get(currentPosY).time - posYKeys.get(currentAdvAnim).get(currentPosY - 1).time;
			if(Math.abs(dt) < 0.01)
				velocityPosY = 0;
			else
				velocityPosY = (posYKeys.get(currentAdvAnim).get(currentPosY).value - posYKeys.get(currentAdvAnim).get(currentPosY - 1).value)/
						(dt);
		}
	}
	public int getCurrentPosY()
	{
		return currentPosY;
	}
	public float getPosY()
	{
		if(currentAdvAnim == -1 || posYKeys.get(currentAdvAnim).size() == 0)
			return 0;
		else if(currentPosY == -1)
			return posYKeys.get(currentAdvAnim).get(posYKeys.get(currentAdvAnim).size() - 1).value;
		else if(currentPosY == 0)
			return (currentTime - savedTime)*velocityPosY + savedY;
		else if(posYKeys.get(currentAdvAnim).get(currentPosY).isLinear)
			return (currentTime - posYKeys.get(currentAdvAnim).get(currentPosY - 1).time)*velocityPosY + posYKeys.get(currentAdvAnim).get(currentPosY - 1).value;
		else	
			return posYKeys.get(currentAdvAnim).get(currentPosY - 1).value;
	}
	public void updatePosY(Vector2D vec, Vector2D translate)
	{
		if(posYKeys.size() != 0 && posYKeys.get(currentAdvAnim).size() != 0)
		{
			if(currentPosY != -1 && currentTime > posYKeys.get(currentAdvAnim).get(currentPosY).time)
			{
				do
				{
					currentPosY++;
					if(currentPosY >= posYKeys.get(currentAdvAnim).size())
					{
						currentPosY = -1;
						velocityPosY = 0;
						break;
					}
				}while(currentTime > posYKeys.get(currentAdvAnim).get(currentPosY).time);
				
				if(currentPosY != -1)
				{
					float dt = posYKeys.get(currentAdvAnim).get(currentPosY).time - posYKeys.get(currentAdvAnim).get(currentPosY - 1).time;
					if(Math.abs(dt) < 0.01)
						velocityPosY = 0;
					else
						velocityPosY = (posYKeys.get(currentAdvAnim).get(currentPosY).value - posYKeys.get(currentAdvAnim).get(currentPosY - 1).value)/
								(dt);
				}
			}
			else if(currentPosY == 0)
			{
				float computeY =  this.advEntity.getY() - translate.y;
				if(velocityPosY == 0)
				{
					savedY = computeY;
					float dt = posYKeys.get(currentAdvAnim).get(currentPosY).time - savedTime;
					if(Math.abs(dt) < 0.01)
						velocityPosY = 0;
					else
						velocityPosY = (posYKeys.get(currentAdvAnim).get(currentPosY).value - savedY)/
								(dt);
				}
			}
		}
		else
		{
			currentPosY = -1;
		}
		this.advEntity.setPositionY(this.getPosY()*vec.getMagnitude(), vec.getNormalize());
		
	}
	
	//Scale
	public void setSavedScale(float savedScale)
	{
		this.savedScale = savedScale;
	}
	public float getSavedScale()
	{
		return savedScale;
	}
	public void addKeyScale(int n, float value, float time, boolean isLinear)
	{
		assert(n<scaleKeys.size());
		if(value > 0.01f)
			scaleKeys.get(n).add(new KeyFloat(value, time, isLinear));
		else
			scaleKeys.get(n).add(new KeyFloat(0.005f, time, isLinear));
	}
	public void addKeyScale(float value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeyScale(currentAdvAnim, value, time, isLinear);
	}
	public void addKeyScale(int n, float value, float time)
	{
		assert(n<scaleKeys.size());
		if(value > 0.01f)
			scaleKeys.get(n).add(new KeyFloat(value, time));
		else
			scaleKeys.get(n).add(new KeyFloat(0.005f, time));
	}
	public void addKeyScale(float value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeyScale(currentAdvAnim, value, time);
	}
	public KeyFloat getKeyScale(int n, int i)
	{
		assert(n<scaleKeys.size() && i<scaleKeys.get(n).size());
		return scaleKeys.get(n).get(i);
	}
	public KeyFloat getKeyScale(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeyScale(currentAdvAnim, i);
	}
	public ArrayList<KeyFloat> getKeysScale(int n)
	{
		assert(n<scaleKeys.size());
		return scaleKeys.get(n);
	}
	public ArrayList<KeyFloat> getKeysScale()
	{
		assert(currentAdvAnim!=-1);
		return getKeysScale(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyFloat>> getAllKeysScale()
	{
		return scaleKeys;
	}
	public void setCurrentScale(int n)
	{
		assert(currentAdvAnim != -1 && n<scaleKeys.get(currentAdvAnim).size());
		currentScale = n;
		
		if(n == 0)
		{
			velocityScale = 1;
		}
		else
		{
			float dt = scaleKeys.get(currentAdvAnim).get(currentScale).time - scaleKeys.get(currentAdvAnim).get(currentScale - 1).time;
			if(Math.abs(dt) < 0.01)
				velocityScale = 1;
			else
				velocityScale = (float) Math.pow(scaleKeys.get(currentAdvAnim).get(currentScale).value/scaleKeys.get(currentAdvAnim).get(currentScale - 1).value, 1/dt);
		}
	}
	public int getCurrentScale()
	{
		return currentScale;
	}
	public float getScale()
	{
		if(currentAdvAnim == -1 || scaleKeys.get(currentAdvAnim).size() == 0)
			return 1;
		else if(currentScale == -1)
			return scaleKeys.get(currentAdvAnim).get(scaleKeys.get(currentAdvAnim).size() - 1).value;
		else if(currentScale == 0)
			return (float) Math.pow(velocityScale, (currentTime - savedTime))*savedScale;
		else if(scaleKeys.get(currentAdvAnim).get(currentScale).isLinear)
			return (float) Math.pow(velocityScale, (currentTime - scaleKeys.get(currentAdvAnim).get(currentScale - 1).time))*scaleKeys.get(currentAdvAnim).get(currentScale - 1).value;
		else	
			return scaleKeys.get(currentAdvAnim).get(currentScale - 1).value;
	}
	public void updateScale(Vector2D vec)
	{
		if(scaleKeys.size() != 0 && scaleKeys.get(currentAdvAnim).size() != 0)
		{
			if(currentScale != -1 && currentTime > scaleKeys.get(currentAdvAnim).get(currentScale).time)
			{
				do
				{
					currentScale++;
					if(currentScale >= scaleKeys.get(currentAdvAnim).size())
					{
						currentScale = -1;
						velocityScale = 1;
						break;
					}
				}while(currentTime > scaleKeys.get(currentAdvAnim).get(currentScale).time);
				
				if(currentScale != -1)
				{
					float dt = scaleKeys.get(currentAdvAnim).get(currentScale).time - scaleKeys.get(currentAdvAnim).get(currentScale - 1).time;
					if(Math.abs(dt) < 0.01)
						velocityScale = 1;
					else
						velocityScale = (float) Math.pow(scaleKeys.get(currentAdvAnim).get(currentScale).value/scaleKeys.get(currentAdvAnim).get(currentScale - 1).value, 1/dt);
				}
			}
			else if(currentScale == 0)
			{
				float computeScale =  this.advEntity.getScale()/vec.getMagnitude();
				if(velocityScale == 1)
				{
					savedScale = computeScale;
					float dt = scaleKeys.get(currentAdvAnim).get(currentScale).time - savedTime;
					if(Math.abs(dt) < 0.01)
						velocityScale = 1;
					else
						velocityScale = (float) Math.pow(scaleKeys.get(currentAdvAnim).get(currentScale).value/savedScale, 1/dt);
				}
			}
		}
		else
		{
			currentScale = -1;
		}
		this.advEntity.setScale(this.getScale()*vec.getMagnitude(), this.advEntity.getAdvCenter());
	}
	
	//Rot
	public void setSavedRot(float savedRot)
	{
		this.savedRot = savedRot;
	}
	public float getSavedRot()
	{
		return savedRot;
	}
	public void addKeyRot(int n, float value, float time, boolean isLinear)
	{
		assert(n<rotKeys.size());
		rotKeys.get(n).add(new KeyFloat(value, time, isLinear));
		
	}
	public void addKeyRot(float value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeyRot(currentAdvAnim, value, time, isLinear);
	}
	public void addKeyRot(int n, float value, float time)
	{
		assert(n<rotKeys.size());
		rotKeys.get(n).add(new KeyFloat(value, time));
	}
	public void addKeyRot(float value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeyRot(currentAdvAnim, value, time);
	}
	public KeyFloat getKeyRot(int n, int i)
	{
		assert(n<rotKeys.size() && i<rotKeys.get(n).size());
		return rotKeys.get(n).get(i);
	}
	public KeyFloat getKeyRot(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeyRot(currentAdvAnim, i);
	}
	public ArrayList<KeyFloat> getKeysRot(int n)
	{
		assert(n<rotKeys.size());
		return rotKeys.get(n);
	}
	public ArrayList<KeyFloat> getKeysRot()
	{
		assert(currentAdvAnim!=-1);
		return getKeysRot(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyFloat>> getAllKeysRot()
	{
		return rotKeys;
	}
	public void setCurrentRot(int n)
	{
		assert(currentAdvAnim != -1 && n<rotKeys.get(currentAdvAnim).size());
		currentRot = n;
		
		if(n == 0)
		{
			velocityRot = 0;
		}
		else
		{
			float dt = rotKeys.get(currentAdvAnim).get(currentRot).time - rotKeys.get(currentAdvAnim).get(currentRot - 1).time;
			if(Math.abs(dt) < 0.01)
				velocityRot = 0;
			else
				velocityRot = (rotKeys.get(currentAdvAnim).get(currentRot).value - rotKeys.get(currentAdvAnim).get(currentRot - 1).value)/
						(dt);
		}
	}
	public int getCurrentRot()
	{
		return currentRot;
	}
	public float getRot()
	{
		if(currentAdvAnim == -1 || rotKeys.get(currentAdvAnim).size() == 0)
			return 0;
		else if(currentRot == -1)
			return rotKeys.get(currentAdvAnim).get(rotKeys.get(currentAdvAnim).size() - 1).value;
		else if(currentRot == 0)
			return (currentTime - savedTime)*velocityRot + savedRot;
		else if(rotKeys.get(currentAdvAnim).get(currentRot).isLinear)
			return (currentTime - rotKeys.get(currentAdvAnim).get(currentRot - 1).time)*velocityRot + rotKeys.get(currentAdvAnim).get(currentRot - 1).value;
		else	
			return rotKeys.get(currentAdvAnim).get(currentRot - 1).value;
	}
	public void updateRot(Vector2D vec)
	{
		if(rotKeys.size() != 0 && rotKeys.get(currentAdvAnim).size() != 0 )
		{
			if(currentRot != -1 && currentTime > rotKeys.get(currentAdvAnim).get(currentRot).time)
			{
				do
				{
					currentRot++;
					if(currentRot >= rotKeys.get(currentAdvAnim).size())
					{
						currentRot = -1;
						velocityRot = 0;
						break;
					}
				}while(currentTime > rotKeys.get(currentAdvAnim).get(currentRot).time);
				
				if(currentRot != -1)
				{
					float dt = rotKeys.get(currentAdvAnim).get(currentRot).time - rotKeys.get(currentAdvAnim).get(currentRot - 1).time;
					if(Math.abs(dt) < 0.01)
						velocityRot = 0;
					else
						velocityRot = (rotKeys.get(currentAdvAnim).get(currentRot).value - rotKeys.get(currentAdvAnim).get(currentRot - 1).value)/
								(dt);
				}
			}
			else if(currentRot == 0)
			{
				float computeRot =  this.advEntity.getDegrees(vec.getNormalize());
				if(velocityRot == 0)
				{
					savedRot = computeRot;
					float dt = rotKeys.get(currentAdvAnim).get(currentRot).time - savedTime;
					if(Math.abs(dt) < 0.01)
						velocityRot = 0;
					else
						velocityRot = (rotKeys.get(currentAdvAnim).get(currentRot).value - savedRot)/
								(dt);
				}
			}
		}
		else
		{
			currentRot = -1;
		}
		this.advEntity.setDegrees(this.getRot(), vec.getNormalize(), this.advEntity.getAdvCenter());
	}
	
	//Anim
	public void addKeyAnim(int n, int value, float time, boolean isLinear)
	{
		assert(n<animKeys.size());
		animKeys.get(n).add(new KeyInt(value, time, isLinear));
	}
	public void addKeyAnim(int value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeyAnim(currentAdvAnim, value, time, isLinear);
	}
	public void addKeyAnim(int n, int value, float time)
	{
		assert(n<animKeys.size());
		animKeys.get(n).add(new KeyInt(value, time));
	}
	public void addKeyAnim(int value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeyAnim(currentAdvAnim, value, time);
	}
	public KeyInt getKeyAnim(int n, int i)
	{
		assert(n<animKeys.size() && i<animKeys.get(n).size());
		return animKeys.get(n).get(i);
	}
	public KeyInt getKeyAnim(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeyAnim(currentAdvAnim, i);
	}
	public ArrayList<KeyInt> getKeysAnim(int n)
	{
		assert(n<animKeys.size());
		return animKeys.get(n);
	}
	public ArrayList<KeyInt> getKeysAnim()
	{
		assert(currentAdvAnim!=-1);
		return getKeysAnim(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyInt>> getAllKeysAnim()
	{
		return animKeys;
	}
	public void setCurrentAnim(int n)
	{
		assert(currentAdvAnim != -1 && n<animKeys.get(currentAdvAnim).size());
		currentAnim = n;
		
	}
	public int getCurrentAnim()
	{
		return currentAnim;
	}
	public int getAnim()
	{
		if(currentAdvAnim == -1)
			return -1;
		else if(animKeys.get(currentAdvAnim).size() == 0)
			return 0;
		else if(currentAnim == -1)
			return animKeys.get(currentAdvAnim).get(animKeys.get(currentAdvAnim).size() - 1).value;
		else
			return animKeys.get(currentAdvAnim).get(currentAnim - 1).value;
		
	}
	public void updateAnim(float dt)
	{
		if(animKeys.size() != 0 && animKeys.get(currentAdvAnim).size() != 0 && animKeys.get(currentAdvAnim).size() != 1)
		{
			if(currentAnim != -1 && currentTime > animKeys.get(currentAdvAnim).get(currentAnim).time)
			{
				do
				{
					currentAnim++;
					if(currentAnim >= animKeys.get(currentAdvAnim).size())
					{
						currentAnim = -1;
						break;
					}
				}while(currentTime > animKeys.get(currentAdvAnim).get(currentAnim).time);
				
				if(currentAnim != -1)
				{
					this.advEntity.getAnim().setCurrentAnim(animKeys.get(currentAdvAnim).get(currentAnim).value);
				}
			}
		}
		else
		{
			currentAnim = -1;
		}
		int getAnim = getAnim();
		if(getAnim != -1 && getAnim != this.advEntity.getAnim().getCurrentAnim())
		{
			this.advEntity.getAnim().setCurrentAnim(getAnim);
		}
		this.advEntity.getAnim().update(dt);
	}
	
	//SymV
	public void setFlipV(boolean flipV)
	{
		this.flipV = flipV;
	}
	public boolean getFlipV()
	{
		return flipV;
	}
	public void addKeySymV(int n, boolean value, float time, boolean isLinear)
	{
		assert(n<symVKeys.size());
		symVKeys.get(n).add(new KeyBool(value, time, isLinear));
	}
	public void addKeySymV(boolean value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeySymV(currentAdvAnim, value, time, isLinear);
	}
	public void addKeySymV(int n, boolean value, float time)
	{
		assert(n<symVKeys.size());
		symVKeys.get(n).add(new KeyBool(value, time));
	}
	public void addKeySymV(boolean value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeySymV(currentAdvAnim, value, time);
	}
	public KeyBool getKeySymV(int n, int i)
	{
		assert(n<symVKeys.size() && i<symVKeys.get(n).size());
		return symVKeys.get(n).get(i);
	}
	public KeyBool getKeySymV(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeySymV(currentAdvAnim, i);
	}
	public ArrayList<KeyBool> getKeysSymV(int n)
	{
		assert(n<symVKeys.size());
		return symVKeys.get(n);
	}
	public ArrayList<KeyBool> getKeysSymV()
	{
		assert(currentAdvAnim!=-1);
		return getKeysSymV(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyBool>> getAllKeysSymV()
	{
		return symVKeys;
	}
	public void setCurrentSymV(int n)
	{
		assert(currentAdvAnim != -1 && n<symVKeys.get(currentAdvAnim).size());
		currentSymV = n;
	}
	public int getCurrentSymV()
	{
		return currentSymV;
	}
	public boolean getSymV()
	{
		if(currentAdvAnim == -1 || symVKeys.get(currentAdvAnim).size() == 0)
			return false;
		else if(currentSymV == -1)
			return symVKeys.get(currentAdvAnim).get(symVKeys.get(currentAdvAnim).size() - 1).value;
		else	
			return symVKeys.get(currentAdvAnim).get(currentSymV - 1).value;
	}
	public void updateSymV(Vector2D vec)
	{
		if(symVKeys.size() != 0 && symVKeys.get(currentAdvAnim).size() != 0 && symVKeys.get(currentAdvAnim).size() != 1)
		{

			if(currentSymV != -1 && currentTime > symVKeys.get(currentAdvAnim).get(currentSymV).time)
			{
				do
				{
					currentSymV++;
					if(currentSymV >= symVKeys.get(currentAdvAnim).size())
					{
						currentSymV = -1;
						break;
					}
				}while(currentTime > symVKeys.get(currentAdvAnim).get(currentSymV).time);
			}
		}
		else
		{
			currentSymV = -1;
		}
		if(this.getSymV())
		{
			this.advEntity.flipV(this.advEntity.getAdvCenter(), vec);
			flipV = !flipV;
		}
	}
	
	//SymH
	public void setFlipH(boolean flipH)
	{
		this.flipH = flipH;
	}
	public boolean getFlipH()
	{
		return flipH;
	}
	public void addKeySymH(int n, boolean value, float time, boolean isLinear)
	{
		assert(n<symHKeys.size());
		symHKeys.get(n).add(new KeyBool(value, time, isLinear));
	}
	public void addKeySymH(boolean value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeySymH(currentAdvAnim, value, time, isLinear);
	}
	public void addKeySymH(int n, boolean value, float time)
	{
		assert(n<symHKeys.size());
		symHKeys.get(n).add(new KeyBool(value, time));
	}
	public void addKeySymH(boolean value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeySymH(currentAdvAnim, value, time);
	}
	public KeyBool getKeySymH(int n, int i)
	{
		assert(n<symHKeys.size() && i<symHKeys.get(n).size());
		return symHKeys.get(n).get(i);
	}
	public KeyBool getKeySymH(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeySymH(currentAdvAnim, i);
	}
	public ArrayList<KeyBool> getKeysSymH(int n)
	{
		assert(n<symHKeys.size());
		return symHKeys.get(n);
	}
	public ArrayList<KeyBool> getKeysSymH()
	{
		assert(currentAdvAnim!=-1);
		return getKeysSymH(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyBool>> getAllKeysSymH()
	{
		return symHKeys;
	}
	public void setCurrentSymH(int n)
	{
		assert(currentAdvAnim != -1 && n<symHKeys.get(currentAdvAnim).size());
		currentSymH = n;
	}
	public int getCurrentSymH()
	{
		return currentSymH;
	}
	public boolean getSymH()
	{
		if(currentAdvAnim == -1 || symHKeys.get(currentAdvAnim).size() == 0)
			return false;
		else if(currentSymH == -1)
			return symHKeys.get(currentAdvAnim).get(symHKeys.get(currentAdvAnim).size() - 1).value;
		else	
			return symHKeys.get(currentAdvAnim).get(currentSymH - 1).value;
	}
	public void updateSymH(Vector2D vec)
	{
		if(symHKeys.size() != 0 && symHKeys.get(currentAdvAnim).size() != 0 && symHKeys.get(currentAdvAnim).size() != 1)
		{

			if(currentSymH != -1 && currentTime > symHKeys.get(currentAdvAnim).get(currentSymH).time)
			{
				do
				{
					currentSymH++;
					if(currentSymH >= symHKeys.get(currentAdvAnim).size())
					{
						currentSymH = -1;
						break;
					}
				}while(currentTime > symHKeys.get(currentAdvAnim).get(currentSymH).time);
			}
		}
		else
		{
			currentSymH = -1;
		}
		if(this.getSymH())
		{
			this.advEntity.flipH(this.advEntity.getAdvCenter(), vec);
			flipH = !flipH;
		}
	}
	
	//Visible
	public void setSavedVisible(float savedVisible)
	{
		this.savedVisible = savedVisible;
	}
	public float getSavedVisible()
	{
		return savedVisible;
	}
	public void addKeyVisible(int n, float value, float time, boolean isLinear)
	{
		assert(n<visibleKeys.size());
		if(value > 1.0f)
			visibleKeys.get(n).add(new KeyFloat(value, time, isLinear));
		else
			visibleKeys.get(n).add(new KeyFloat(0.5f, time, isLinear));
		
	}
	public void addKeyVisible(float value, float time, boolean isLinear)
	{
		assert(currentAdvAnim!=-1);
		addKeyVisible(currentAdvAnim, value, time, isLinear);
	}
	public void addKeyVisible(int n, float value, float time)
	{
		assert(n<visibleKeys.size());
		if(value > 1.0f)
			visibleKeys.get(n).add(new KeyFloat(value, time));
		else
			visibleKeys.get(n).add(new KeyFloat(0.5f, time));
	}
	public void addKeyVisible(float value, float time)
	{
		assert(currentAdvAnim!=-1);
		addKeyVisible(currentAdvAnim, value, time);
	}
	public KeyFloat getKeyVisible(int n, int i)
	{
		assert(n<visibleKeys.size() && i<visibleKeys.get(n).size());
		return visibleKeys.get(n).get(i);
	}
	public KeyFloat getKeyVisible(int i)
	{
		assert(currentAdvAnim!=-1);
		return getKeyVisible(currentAdvAnim, i);
	}
	public ArrayList<KeyFloat> getKeysVisible(int n)
	{
		assert(n<visibleKeys.size());
		return visibleKeys.get(n);
	}
	public ArrayList<KeyFloat> getKeysVisible()
	{
		assert(currentAdvAnim!=-1);
		return getKeysVisible(currentAdvAnim);
	}
	public ArrayList<ArrayList<KeyFloat>> getAllKeysVisible()
	{
		return visibleKeys;
	}
	public void setCurrentVisible(int n)
	{
		assert(currentAdvAnim != -1 && n<visibleKeys.get(currentAdvAnim).size());
		currentVisible = n;
		
		if(n == 0)
		{
			velocityVisible = 1;
		}
		else
		{
			float dt = visibleKeys.get(currentAdvAnim).get(currentVisible).time - visibleKeys.get(currentAdvAnim).get(currentVisible - 1).time;
			if(Math.abs(dt) < 0.01)
				velocityVisible = 1;
			else
				velocityVisible = (float) Math.pow(visibleKeys.get(currentAdvAnim).get(currentVisible).value/visibleKeys.get(currentAdvAnim).get(currentVisible - 1).value, 1/dt);
		}
	}
	public int getCurrentVisible()
	{
		return currentVisible;
	}
	public float getVisible()
	{
		if(currentAdvAnim == -1 || visibleKeys.get(currentAdvAnim).size() == 0)
			return 100;
		else if(currentVisible == -1)
			return visibleKeys.get(currentAdvAnim).get(visibleKeys.get(currentAdvAnim).size() - 1).value;
		else if(currentVisible == 0)
			return (float) Math.pow(velocityVisible, (currentTime - savedTime))*savedVisible;
		else if(visibleKeys.get(currentAdvAnim).get(currentVisible).isLinear)
			return (float) Math.pow(velocityVisible, (currentTime - visibleKeys.get(currentAdvAnim).get(currentVisible - 1).time))*visibleKeys.get(currentAdvAnim).get(currentVisible - 1).value;
		else	
			return visibleKeys.get(currentAdvAnim).get(currentVisible - 1).value;
	}
	public void updateVisible(float visible)
	{
		if(visibleKeys.size() != 0 && visibleKeys.get(currentAdvAnim).size() != 0)
		{
			if(currentVisible != -1 && currentTime > visibleKeys.get(currentAdvAnim).get(currentVisible).time)
			{
				do
				{
					currentVisible++;
					if(currentVisible >= visibleKeys.get(currentAdvAnim).size())
					{
						currentVisible = -1;
						velocityVisible = 1;
						break;
					}
				}while(currentTime > visibleKeys.get(currentAdvAnim).get(currentVisible).time);
				
				if(currentVisible != -1)
				{
					float dt = visibleKeys.get(currentAdvAnim).get(currentVisible).time - visibleKeys.get(currentAdvAnim).get(currentVisible - 1).time;
					if(Math.abs(dt) < 0.01)
						velocityVisible = 1;
					else
						velocityVisible = (float) Math.pow(visibleKeys.get(currentAdvAnim).get(currentVisible).value/visibleKeys.get(currentAdvAnim).get(currentVisible - 1).value, 1/dt);
				}
			}
			else if(currentVisible == 0)
			{
				float computeVisible =  100*this.advEntity.getVisible()/visible;
				if(velocityVisible == 1)
				{
					savedVisible = computeVisible;
					float dt = visibleKeys.get(currentAdvAnim).get(currentVisible).time - savedTime;
					if(Math.abs(dt) < 0.01)
						velocityVisible = 1;
					else
						velocityVisible = (float) Math.pow(visibleKeys.get(currentAdvAnim).get(currentVisible).value/savedVisible, 1/dt);
				}
			}
		}
		else
		{
			currentVisible = -1;
		}
		
		this.advEntity.setVisible(this.getVisible()*visible/100);
	}
	
	public void update(float dt, Vector2D vec, Vector2D translate, float visible)
	{
		//Nécessite un advEntity.reset et advAnim.reset
		//Beaucoup plus sûr que l'updateRelative, mais moins performant
		//Il faut en effet reinitialiser à chaque frame les 2 objets ci dessus
		//A utiliser quand il y a des sauts de frames, c'est à dire en dehors de l'advEntityEditor
		if(currentAdvAnim == -1 || !isPlaying)
			return;
		currentTime += dt;
		if(currentTime > advAnimKeys.get(currentAdvAnim).duration)
		{
			if(advAnimKeys.get(currentAdvAnim).isLooping)
			{
				this.setCurrentTime(currentTime - advAnimKeys.get(currentAdvAnim).duration);
				this.initAll();
			}
			else
			{
				currentTime = advAnimKeys.get(currentAdvAnim).duration;
			}
		}
		updatePosX(vec, translate);
		updatePosY(vec, translate);
		this.advEntity.translate(translate);
		updateScale(vec);
		updateRot(vec);
		updateAnim(dt*1000f);
		updateSymV(vec);
		updateSymH(vec);
		updateVisible(visible);
	}
}