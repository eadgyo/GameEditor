package Addons;

import java.util.ArrayList;

import Maths.Circle;
import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;

public class Animation implements java.io.Serializable
{
	public final static long serialVersionUID = 7506448876402538779L;
	
	private ArrayList<PointInt> animFrames;
	private ArrayList<String> animNames;
	private ArrayList<Float> delays;
	private ArrayList<ArrayList<ArrayList<AdvForm>>> advForms;
	private ArrayList<ArrayList<Circle>> circles;
	private int currentAnim;
	private int currentFrame;
	private int lastAnim;
	private int lastFrame;

	protected float time;
	
	private Entity entity;
	
	public Animation()
	{
		currentAnim = -1;
		currentFrame = -1;
		animNames = new ArrayList<String>();
		animFrames = new ArrayList<PointInt>();
		delays = new ArrayList<Float>();
		advForms = new ArrayList<ArrayList<ArrayList<AdvForm>>>();
		circles = new ArrayList<ArrayList<Circle>>();
		time = 0;
	}
	public Animation(Animation anim)
	{
		currentAnim = -1;
		currentFrame = -1;
		animNames = new ArrayList<String>();
		animFrames = new ArrayList<PointInt>();
		delays = new ArrayList<Float>();
		advForms = new ArrayList<ArrayList<ArrayList<AdvForm>>>();
		circles = new ArrayList<ArrayList<Circle>>();
		time = 0;
		
		for(int i=0; i<anim.getAnimFrames().size(); i++)
		{
			add(anim.getNames().get(i), anim.getAnimFrames().get(i).clone(), anim.getDelays().get(i));
			setCurrentAnim(i);
			for(int j=0; j<anim.getAllAForms().get(i).size(); j++)
			{
				setCurrentFrame(j);
				for(int w=0; w<anim.getAllAForms().get(i).get(j).size(); w++)
				{
					addAForm(anim.getAllAForms().get(i).get(j).get(w).clone());
				}
			}
		}
		setCurrentAnim(anim.getCurrentAnim());
		setCurrentFrame(anim.getCurrentFrame());
	}
	public Animation clone()
	{
		return new Animation(this);
	}
	
	public void reset()
	{
		setCurrentAnim(-1);
		setCurrentFrame(-1);
		lastFrame = -1;
		lastAnim = -1;
		time = 0;
	}
	public void resetForms()
	{
		for(int i=0; i<advForms.size(); i++)
		{
			for(int j=0; j<advForms.get(i).size(); j++)
			{
				for(int w=0; w<advForms.get(i).get(j).size(); w++)
				{
					advForms.get(i).get(j).get(w).resetTransformations();
				}
			}
		}
	}
	public void clear()
	{
		reset();
		for(int i=0; i<advForms.size(); i++)
		{
			advForms.get(i).clear();
		}
		animNames.clear();
		advForms.clear();
		animFrames.clear();
		delays.clear();
		time = 0;
	}
	public void set(Animation anim)
	{
		clear();
		for(int i=0; i<anim.getAnimFrames().size(); i++)
		{
			add(anim.getNames().get(i), anim.getAnimFrames().get(i).clone(), anim.getDelays().get(i));
			setCurrentAnim(i);
			for(int j=0; j<anim.getAllAForms().get(i).size(); j++)
			{
				setCurrentFrame(j);
				for(int w=0; w<anim.getAllAForms().get(i).get(j).size(); w++)
				{
					addAForm(anim.getAllAForms().get(i).get(j).get(w).clone());
				}
			}
		}
		setCurrentAnim(anim.getCurrentAnim());
		setCurrentFrame(anim.getCurrentFrame());
	}
	
	public Entity getEntity()
	{
		return entity;
	}
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
	
	public void add(String name, PointInt animFrame, float delay)
	{
		animNames.add(name);
		animFrames.add(animFrame);
		delays.add(delay);
		advForms.add(new ArrayList<ArrayList<AdvForm>>());
		circles.add(new ArrayList<Circle>());
		for(int i=0; i<animFrame.y - animFrame.x + 1; i++)
		{
			advForms.get(delays.size() - 1).add(new ArrayList<AdvForm>());
			circles.get(delays.size() - 1).add(new Circle());
		}
	}
	//detruit une animation a la position spécifié.
	public void remove(int anim)
	{
		assert(anim < animFrames.size()) : "Pas d'animation à supprimer";
		animFrames.remove(anim);
		animNames.remove(anim);
		delays.remove(anim);
		advForms.remove(anim);
		circles.remove(anim);
	}
	public void remove(int anim, int frame)
	{
		assert(frame < advForms.get(anim).size()) : "Pas de aForms à supprimer";
		advForms.get(anim).remove(frame);
		circles.get(anim).remove(frame);
	}
	
	//Gestion des AForms
	public int getSizeAForms()
	{
		return getAForms().size();
	}
	public void addAForm(int currentAnim, int currentFrame, AdvForm advForm)
	{
		advForms.get(currentAnim).get(currentFrame).add(advForm);
	}
	public void addAForm(AdvForm advForm)
	{
		advForms.get(currentAnim).get(currentFrame).add(advForm);
	}
	public void removeAForm(int anim, int frame, int aForm)
	{
		advForms.get(anim).get(frame).remove(aForm);
	}
	public ArrayList<PointInt> getAnimFrames()
	{
		return animFrames;
	}
	public ArrayList<Float> getDelays()
	{
		return delays;
	}
	public ArrayList<String> getNames()
	{
		return animNames;
	}
	
	public AdvForm getAForm(int n)
	{
		assert(n < getAForms().size()): n;
		return getAForms().get(n);
	}
	public ArrayList<AdvForm> getAForms()
	{
		return advForms.get(currentAnim).get(currentFrame);
	}
	public ArrayList<ArrayList<ArrayList<AdvForm>>> getAllAForms()
	{
		return advForms;
	}
	public Circle getCircle()
	{
		return circles.get(currentAnim).get(currentFrame);
	}
	public ArrayList<Circle> getCircles()
	{
		assert(currentAnim != -1);
		return circles.get(currentAnim);
	}
	public ArrayList<ArrayList<Circle>> getAllCircles()
	{
		return circles;
	}
	
	public void computeRadius()
	{
		Vector2D l_pointMin = new Vector2D();
		Vector2D l_pointMax = new Vector2D();
		for(int i=0; i<circles.size(); i++)
		{
			for(int j=0; j<circles.get(i).size(); j++)
			{
				if(advForms.get(i).get(j).size() == 0)
				{
					circles.get(i).get(j).setRadius(0);
				}
				else
				{
					l_pointMin.set(advForms.get(i).get(j).get(0).getMinX(), advForms.get(i).get(j).get(0).getMinY());
					l_pointMax.set(advForms.get(i).get(j).get(0).getMaxX(), advForms.get(i).get(j).get(0).getMaxY());
					for(int k=1; k<advForms.get(i).get(j).size(); k++)
					{
						float l_minX = advForms.get(i).get(j).get(k).getMinX();
						float l_minY = advForms.get(i).get(j).get(k).getMinY();
						float l_maxX = advForms.get(i).get(j).get(k).getMaxX();
						float l_maxY = advForms.get(i).get(j).get(k).getMaxY();
						
						if(l_pointMin.x > l_minX)
						{
							l_pointMin.x = l_minX;
						}
						if(l_pointMin.y > l_minY)
						{
							l_pointMin.y = l_minY;
						}
						if(l_pointMax.x < l_maxX)
						{
							l_pointMax.x = l_maxX;
						}
						if(l_pointMax.y < l_maxY)
						{
							l_pointMax.y = l_maxY;
						}
					}
					float radius = Math.max(l_pointMax.x - l_pointMin.x, l_pointMax.y - l_pointMin.y); 
					circles.get(i).get(j).setRadius(radius);
				}
			}
		}
		
	}
	
	//Gestion des frames
	public int getSizeFrames()
	{
		if(currentAnim == -1 || this.size() == 0)
			return 0;
		return getY() - getX() + 1;
	}
	public int getX()
	{
		return animFrames.get(currentAnim).x;
	}
	public int getY()
	{
		return animFrames.get(currentAnim).y;
	}
	public void addFrameTop()
	{
		advForms.get(currentAnim).add(new ArrayList<AdvForm>());
		animFrames.get(currentAnim).y++;
	}
	public void addFrameBack()
	{
		advForms.get(currentAnim).add(0, new ArrayList<AdvForm>());
		animFrames.get(currentAnim).x--;
	}
	public void removeFrameTop()
	{
		if(advForms.get(currentAnim).size() < 1)
			return;
		advForms.get(currentAnim).remove(advForms.get(currentAnim).size() - 1);
		animFrames.get(currentAnim).y--;
	}
	public void removeFrameBack()
	{
		if(advForms.get(currentAnim).size() < 1)
			return;
		advForms.get(currentAnim).remove(0);
		animFrames.get(currentAnim).x++;
	}
	public int getCurrentAnim()
	{
		return currentAnim;
	}
	public boolean isChangedAnim()
	{
		if(lastAnim == currentAnim)
			return true;
		return false;
	}
	public void setCurrentAnim(int currentAnim)
	{
		this.lastAnim = currentAnim;
		this.currentAnim = currentAnim;
	}
	public void nextAnim()
	{
		setCurrentAnim((currentAnim+1)%this.getSizeFrames());
	}
	
	//Gestion des anims
	public void setName(String name)
	{
		assert(currentAnim != -1);
		setName(currentAnim, name);
	}
	public void setName(int n, String name)
	{
		assert(n < animNames.size());
		animNames.set(n, name);
	}
	public String getName()
	{
		assert(currentAnim != -1);
		return getName(currentAnim);
	}
	public String getName(int n)
	{
		assert(n < animNames.size());
		return animNames.get(n);
	}
	public void setDelay(float delay)
	{
		delays.set(currentAnim, delay);
	}
	public float getDelay()
	{
		return delays.get(currentAnim);
	}
	public int getCurrentFrame()
	{
		return currentFrame;
	}
	public void setCurrentFrame(int frame)
	{
		this.lastFrame = currentFrame;
		currentFrame = frame;
		if(currentFrame == -1)
			return;
		if(entity != null)
		{
			if(currentFrame != -2)
				entity.setCurrentFrame(currentFrame + getX());
			entity.updateInertia();
		}
	}
	public void nextFrame()
	{
		setCurrentFrame((currentFrame+1)%this.getSizeFrames());
	}
	public boolean isChangedFrame()
	{
		if(lastFrame == currentFrame)
			return true;
		return false;
	}
	public int size()
	{
		return animFrames.size();
	}
	public void setTime(float time)
	{
		this.time = time;
		currentFrame = 0;
		update(0.0f);
	}
	public float getTime()
	{
		return time;
	}
	
	public void update(float dt)
	{
		if(currentAnim == -1 || currentFrame == -1)
			return;
		assert(getDelay() != 0);
		time += dt;
		while(time >= getDelay())
		{
			time -= getDelay();
			nextFrame();
		}
	}

	/*public float getInvMass()
	{
		ArrayList<AdvForm> adv = this.getAForms();
		
		float inv = 0;
		for(int i=0; i<adv.size(); i++)
		{
		}
		return adv;
	}
	public float getInvInertia()
	{
	}*/
	
}
