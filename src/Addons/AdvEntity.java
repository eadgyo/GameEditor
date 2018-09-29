package Addons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Base.Image;
import Maths.Extend;
import Maths.Vector2D;
import Maths.Rectangle;
import Maths.Vector2D;
import Maths.sRectangle;

public class AdvEntity extends Entity
{
	public final static long serialVersionUID = 5685600427694131355L;
	
	private AdvEntity parent;
	
	private ArrayList<AdvEntity> advEntities;
	private ArrayList<AdvEntity> displayAdvEntities;
	private AdvAnimation advAnimation;
	private Vector2D advCenter;
	private Vector2D advCenterSave;
	
	private sRectangle bounds;
	
	public AdvEntity()
	{
		super();
		advEntities = new ArrayList<AdvEntity>();
		displayAdvEntities = new ArrayList<AdvEntity>();
		advCenter = new Vector2D();
		advCenterSave = new Vector2D();
		advAnimation = null;
		
		bounds = new sRectangle();
		parent = null;
	}
	public AdvEntity(Graphics graphics, int width, int height, int cols,
			BufferedImage texture, String textureName, Animation anim) 
	{
		super(graphics, width, height, cols, texture, textureName, anim);
		advEntities = new ArrayList<AdvEntity>();
		displayAdvEntities = new ArrayList<AdvEntity>();
		advCenter = new Vector2D();
		advCenterSave = new Vector2D();
		advAnimation = null;
		this.getAdvCenter().set(super.getPos());
		
		bounds = new sRectangle();
		parent = null;
	}
	public AdvEntity(AdvEntity advEntity)
	{
		super(advEntity);
		advEntities = new ArrayList<AdvEntity>();
		displayAdvEntities = new ArrayList<AdvEntity>();
		advCenter = new Vector2D();
		advCenterSave = new Vector2D();
		advCenter.set(advEntity.getAdvCenter());
		for(int i=0; i<advEntity.getAdvEntities().size(); i++)
		{
			addAdvEntity(advEntity.getAdvEntities().get(i).clone());
		}
		if(advEntity.getAdvAnimation() != null)
		{
			advAnimation = advEntity.getAdvAnimation().clone();
			advAnimation.setAdvEntity(this);
		}
		reconstructDisplay(advEntity);
		
		bounds = new sRectangle();
		parent = null;
	}
	public AdvEntity(Entity entity)
	{
		super(entity);
		advEntities = new ArrayList<AdvEntity>();
		displayAdvEntities = new ArrayList<AdvEntity>();
		advCenter = new Vector2D();
		advCenterSave = new Vector2D();
		this.getAdvCenter().set(super.getPos());
		advAnimation = null;
		
		bounds = new sRectangle();
		parent = null;
	}
	public AdvEntity(Image image)
	{
		super(image);
		advEntities = new ArrayList<AdvEntity>();
		displayAdvEntities = new ArrayList<AdvEntity>();
		advCenter = new Vector2D();
		advCenterSave = new Vector2D();
		this.getAdvCenter().set(super.getPos());
		advAnimation = null;
		
		bounds = new sRectangle();
		parent = null;
	}
	public void set(AdvEntity advEntity)
	{
		super.set(advEntity);
		advEntities.clear();
		for(int i=0; i<advEntity.getAdvEntities().size(); i++)
		{
			addAdvEntity(advEntity.getAdvEntities().get(i).clone());
		}
		if(advEntity.getAdvAnimation() != null)
		{
			advAnimation = advEntity.getAdvAnimation().clone();
			advAnimation.setAdvEntity(this);
		}
		setAdvCenter(advEntity.getAdvCenter());
		setAdvCenterSave(advEntity.getAdvCenterSave());
		reconstructDisplay(advEntity);
	}
	public void reconstructDisplay(AdvEntity advEntity)
	{//Reconstruit la liste display à partir d'un autre advEntity
		
		ArrayList<AdvEntity> l_advDisp = advEntity.getDisplayAdvEntities();
		if(l_advDisp.size() == 0)
			return;
		reconstructDispFromLinks(advEntity.getLinksDisplay());
	}
	public void reconstructDispFromLinks(ArrayList<Integer> links)
	{//Reconstruit la liste display à partir d'une liste de liens
		
		ArrayList<AdvEntity> l_myAdvEntities = new ArrayList<AdvEntity>();
		getAllAdvEntities(l_myAdvEntities);
		
		for(int i=0; i<links.size(); i++)
		{
			displayAdvEntities.add(l_myAdvEntities.get(links.get(i)));
		}
	}
	public ArrayList<Integer> getLinksDisplay()
	{//Récupère les liens entre tous les advEntities sous cette advEntity et la liste des entité à afficher
		ArrayList<Integer> links = new ArrayList<Integer>(displayAdvEntities.size());
		
		ArrayList<AdvEntity> l_advEntities = new ArrayList<AdvEntity>();
		getAllAdvEntities(l_advEntities);
		
		for(int i=0; i<displayAdvEntities.size(); i++)
		{
			for(int j=0; j<l_advEntities.size(); j++)
			{
				if(displayAdvEntities.get(i) == l_advEntities.get(j))
				{
					links.add(j);
					break;
				}
			}
		}
		return links;
	}
	public AdvEntity clone()
	{
		AdvEntity l_advEntity = new AdvEntity(this);
		return l_advEntity;
	}
	public void clear()
	{
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).clear();
			advEntities.remove(i);
		}
		displayAdvEntities.clear();
		advCenter.set(0,0);
		isInitialized = false;
	}
	public void clear(int n)
	{
		assert(n < advEntities.size());
		advEntities.get(n).clear();
		advEntities.remove(n);
	}
	
	public void addAdvEntity(AdvEntity advEntity)
	{
		advEntities.add(advEntity);
		advEntity.setParent(this);
	}
	public void addAdvEntity(int i, AdvEntity advEntity)
	{
		advEntities.add(i, advEntity);
		advEntity.setParent(this);
	}
	public AdvEntity removeAdvEntity(int i)
	{
		advEntities.get(i).setParent(null);
		return advEntities.remove(i);
	}
	public void removeAdvEntity(AdvEntity advEntity)
	{
		advEntities.remove(advEntity);
	}
	public AdvEntity getAdvEntity(int n)
	{
		assert(n<advEntities.size());
		return advEntities.get(n);
	}
	public ArrayList<AdvEntity> getAdvEntities()
	{
		return advEntities;
	}
	@Override
	public void reset()
	{
		super.reset();
		advCenter.set(advCenterSave);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).reset();
		}
	}
	public void resetAdvCenter()
	{
		setAdvCenter(new Vector2D(0, 0));
	}
	
	public AdvAnimation getAdvAnimation()
	{
		return advAnimation;
	}
	public void setAdvAnimation(AdvAnimation advAnimation)
	{
		advAnimation.setAdvEntity(this);
		this.advAnimation = advAnimation;
	}
	
	public void setAdvCenter(Vector2D point)
	{
		advCenter.x = point.x;
		advCenter.y = point.y;
		advCenterSave.set(advCenter);
	}
	public void setAdvCenterSave(Vector2D point)
	{
		advCenterSave.x = point.x;
		advCenterSave.y = point.y;
	}
	public void setAdvCenterX(float x)
	{
		advCenter.x = x;
		advCenterSave.set(advCenter);
	}
	public void setAdvCenterY(float y)
	{
		advCenter.y = y;
		advCenterSave.set(advCenter);
	}
	public Vector2D getAdvCenter()
	{
		return advCenter;
	}
	public Vector2D getAdvCenterSave()
	{
		return advCenterSave;
	}
	
	public void drawAlone()
	{
		super.draw();
		if(advCenter != null)
			if(isDisplayingRec)
				graphics.fillRect((int)this.getAdvCenter().x - 3, (int)this.getAdvCenter().y - 3, 6, 6);
	}
	public void drawAlone(Graphics g)
	{
		super.draw(g);
		if(advCenter != null)
			if(isDisplayingRec)
				graphics.fillRect((int)this.getAdvCenter().x - 3, (int)this.getAdvCenter().y - 3, 6, 6);
	}
	public void drawAlone(Graphics g, Vector2D vec)
	{
		super.draw(g, vec);
		if(advCenter != null)
			if(isDisplayingRec)
				graphics.fillRect((int) (this.getAdvCenter().x + vec.x - 3), (int) (this.getAdvCenter().y + vec.y - 3), 6, 6);
	}
	public void drawAlone(Vector2D vec)
	{
		super.draw(vec);
		if(advCenter != null)
			if(isDisplayingRec)
				graphics.fillRect((int) (this.getAdvCenter().x + vec.x - 3), (int) (this.getAdvCenter().y + vec.y - 3), 6, 6);
	}
	public void drawAlone(Graphics g, Vector2D vec, float scale)
	{
		super.draw(g, vec, scale);
	}
	
	@Override
	public void draw()
	{
		draw(this.graphics);
	}
	@Override
	public void draw(Graphics g)
	{
		if(displayAdvEntities.size() == 0)
		{
			super.draw(g);
			for(int i=0; i<advEntities.size(); i++)
			{
				advEntities.get(i).draw(g);
			}
			if(advCenter != null)
				if(isDisplayingRec)
				{
					graphics.setColor(Color.black);
					graphics.fillRect((int)this.getAdvCenter().x - 3, (int)this.getAdvCenter().y - 3, 6, 6);
				}
		}
		else
		{
			for(int i=displayAdvEntities.size() - 1; i>-1; i--)
			{
				displayAdvEntities.get(i).drawAlone(g);
			}
		}
	}
	@Override
	public void draw(Vector2D vec)
	{
		draw(this.graphics, vec);
	}
	@Override
	public void draw(Graphics g, Vector2D vec)
	{
		if(displayAdvEntities.size() == 0)
		{
			super.draw(g, vec);
			
			for(int i=0; i<advEntities.size(); i++)
			{
				advEntities.get(i).draw(g, vec);
			}
			if(advCenter != null)
				if(isDisplayingRec)
				{
					graphics.setColor(Color.black);
					graphics.fillRect((int) (this.getAdvCenter().x + vec.x - 3), (int) (this.getAdvCenter().y  + vec.y - 3), 6, 6);
				}
		}
		else
		{
			for(int i=displayAdvEntities.size() - 1; i>-1; i--)
			{
				displayAdvEntities.get(i).drawAlone(g, vec);
			}
		}
	}
	@Override
	public void draw(Graphics g, Vector2D vec, float scale)
	{
		if(displayAdvEntities.size() == 0)
		{
			super.draw(g, vec, scale);
			
			for(int i=0; i<advEntities.size(); i++)
			{
				advEntities.get(i).draw(g, vec, scale);
			}
			/*if(advCenter != null)
				if(isDisplayingRec)
				{
					graphics.setColor(Color.black);
					graphics.fillRect((int) (this.getAdvCenter().x + vec.x - 3), (int) (this.getAdvCenter().y  + vec.y - 3), 6, 6);
				}*/
		}
		else
		{
			for(int i=displayAdvEntities.size() - 1; i>-1; i--)
			{
				displayAdvEntities.get(i).drawAlone(g, vec, scale);
			}
		}
	}
	
	@Override
	public void setGraphics(Graphics g)
	{
		super.setGraphics(g);
		for(int i=0; i<advEntities.size(); i++)
			advEntities.get(i).setGraphics(g);
	}
	
	//Transformations
	@Override
	public void translate(Vector2D v)
	{
		super.translate(v);
		advCenter.translate(v);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).translate(v);
		}
	}
	@Override
	public void translateX(float vecX)
	{
		super.translateX(vecX);
		advCenter.translateX(vecX);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).translateX(vecX);
		}
	}
	@Override
	public void translateY(float vecY)
	{
		super.translateY(vecY);
		advCenter.translateY(vecY);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).translateY(vecY);
		}
	}
	@Override
	public void rotateRadians(float omega, Vector2D center)
	{
		super.rotateRadians(omega, center);
		advCenter.rotateRadians(omega, center);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).rotateRadians(omega, center);
		}
	}
	public void rotateDegrees(float omega, Vector2D center)
	{
		float omegaRadians = (omega*(float)(Math.PI))/180;
		rotateRadians(omegaRadians, center);
	}
	@Override
	public void scale(float factor, Vector2D center)
	{
		super.scale(factor, center);
		advCenter.scale(factor, center);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).scale(factor, center);
		}
	}
	@Override
	public void flipH(Vector2D center)
	{
		super.flipH(center);
		advCenter.flipH(center);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).flipH(center);
		}
	}
	@Override
	public void flipV(Vector2D center)
	{
		super.flipV(center);
		advCenter.flipV(center);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).flipV(center);
		}
	}
	public void flipH(Vector2D center, Vector2D vec)
	{
		super.flipH(center);
		advCenter.flipH(center);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).flipH(center);
		}
	}
	public void flipV(Vector2D center, Vector2D vec)
	{
		super.flipV(center);
		advCenter.flipV(center);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).flipV(center);
		}
	}
	@Override
	public void visible(float f)
	{
		super.visible(f);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).visible(f);
		}
	}
	@Override
	public void setLeftPos(Vector2D left)
	{
		if(parent == null)
		{
			if(advEntities.size() != 0)
			{
				advEntities.get(0).setLeftPos(left);
			}
		}
		else
		{
			super.setLeftPos(left);
		}
	}

	@Override
	public void clearTexture()
	{
		super.clearTexture();
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).clearTexture();
		}
	}
	@Override
	public void loadTexture()
	{
		super.loadTexture();
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).loadTexture();
		}
	}
	
	public ArrayList<AdvForm> getAdvFormsCompute()
	{
		ArrayList<AdvForm> l_advFormsCompute = new ArrayList<AdvForm>();
		for(int i=0; i<advEntities.size(); i++)
		{
			/*advEntities.get(i).setAdvCenterClone(advEntities.get(i).getAdvCenter());
			advEntities.get(i).getAdvCenterClone().add(this.getPos());
			advEntities.get(i).getAdvCenterClone().rotateRadians(this.getRadians(), this.getAdvCenter());
			advEntities.get(i).getAdvCenterClone().scale(this.getScale(), this.getAdvCenter());
			if(this.getFlipH())
				advEntities.get(i).getAdvCenterClone().flipH(this.getAdvCenter());
			if(this.getFlipV())
				advEntities.get(i).getAdvCenterClone().flipV(this.getAdvCenter());*/
			
			l_advFormsCompute.addAll(advEntities.get(i).getAdvFormsCompute());
		}
		for(int i=0; i<l_advFormsCompute.size(); i++)
		{
			l_advFormsCompute.get(i).translate(this.getPos());
			l_advFormsCompute.get(i).rotateRadians(this.getRadians(), this.getAdvCenter());
			l_advFormsCompute.get(i).scale(this.getScale(), this.getAdvCenter());
			if(this.getFlipH())
				l_advFormsCompute.get(i).flipH(this.getAdvCenter());
			if(this.getFlipV())
				l_advFormsCompute.get(i).flipV(this.getAdvCenter());
		}
		l_advFormsCompute.addAll(super.getAdvFormsCompute(this.getAdvCenter()));
		return l_advFormsCompute;
	}
	public void update(float dt, Vector2D vec, Vector2D translate, float visible)
	{
		spriteData.flipH = false;
		spriteData.flipV = false;
		Vector2D recVec = this.rec.getVecWorld();
		Vector2D newVec;
		if(recVec.getMagnitude() != 0)
			newVec = recVec.getNormalize().multiply(this.getScale());
		else
			newVec = new Vector2D(1, 0);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).update(dt, newVec, new Vector2D(this.getPos()), this.getVisible());
		}
		if(advAnimation != null)
			advAnimation.update(dt, vec, translate, visible);
		
		if(parent == null)//root
		{
			this.computeBounds();
		}
	}
	public void setTime(float time)
	{
		if(advAnimation != null)
			advAnimation.setCurrentTime(time);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).setTime(time);
		}
	}
	public void resetAdvAnim()
	{
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).resetAdvAnim();
		}
		if(advAnimation != null)
		{
			int selected = advAnimation.getCurrentAdvAnim();
			if(selected == -1)
			{
				if(advAnimation.getAll().size() > 0)
				{
					advAnimation.setCurrentAdvAnim(0);
					advAnimation.reset();
					advAnimation.initAll();
				}
			}
			else
			{
				advAnimation.reset();
				advAnimation.initAll();
			}
		}
	}

	public void getAllAdvEntities(ArrayList<AdvEntity> list)
	{
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).getAllAdvEntities(list);
		}
		if(parent != null)
			list.add(this);
	}
	public ArrayList<AdvEntity> getDisplayAdvEntities()
	{
		return displayAdvEntities;
	}
	public ArrayList<AdvEntity> getDisplayAdvEntitiesClone()
	{
		ArrayList<AdvEntity> l_dispAdvEntities = new ArrayList<AdvEntity>(displayAdvEntities.size());
		for(int i=0; i<displayAdvEntities.size(); i++)
		{
			l_dispAdvEntities.add(displayAdvEntities.get(i));
		}
		return l_dispAdvEntities;
	}

	public void play()
	{
		if(advAnimation != null)
			advAnimation.setIsPlaying(true);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).play();
		}
	}
	public void pause()
	{
		if(advAnimation != null)
			advAnimation.setIsPlaying(false);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).pause();
		}
	}
	public void stop()
	{
		if(advAnimation != null)
		{
			advAnimation.setIsPlaying(false);
			advAnimation.setCurrentTime(0);
		}
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).pause();
		}
	}

	@Override
	public void computeBounds()
	{
		if(advEntities.size() > 0)
		{
			for(int i=0; i<advEntities.size(); i++)
			{
				advEntities.get(i).computeBounds();
			}
			
			Rectangle rec1 = super.getRectangle();
			float xMin = rec1.getMinX();
			float xMax = rec1.getMaxX();
			float yMin = rec1.getMinY();
			float yMax = rec1.getMaxY();
			for(int i=0; i<advEntities.size(); i++)
			{
				sRectangle rec2 = advEntities.get(i).getBounds();
				float xMin2 = rec2.getMinX();
				float xMax2 = rec2.getMaxX();
				float yMin2 = rec2.getMinY();
				float yMax2 = rec2.getMaxY();
				
				if(xMin2 < xMin)
					xMin = xMin2;
				if(xMax2 > xMax)
					xMax = xMax2;
				if(yMin2 < yMin)
					yMin = yMin2;
				if(yMax2 > yMax)
					yMax = yMax2;
			}
			bounds.setLeft(xMin ,
					yMin,
					xMax - xMin,
					yMax - yMin);
		}
		else
			bounds = super.getBounds();
	}
	@Override
	public sRectangle getBounds()
	{
		return bounds;
	}
	@Override
	public ArrayList<Rectangle> getSelectionRectangle()
	{
		ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
		if(parent != null)
		{
			rectangles.add(this.rec);
		}

		for(int i=0; i<advEntities.size(); i++)
		{
			rectangles.addAll(advEntities.get(i).getSelectionRectangle());
		}
		return rectangles;
	}
	
	
	public AdvEntity getParent()
	{
		return parent;
	}
	public void setParent(AdvEntity parent)
	{
		this.parent = parent;
	}
	
	@Override
	public float getSpriteDataWidth()
	{
		if(parent == null)
		{
			if(advEntities.size() == 0)
				return 1;
			else
				return advEntities.get(0).getSpriteDataWidth();
		}
		else
		{
			return super.getSpriteDataWidth();
		}
	}
	@Override 
	public float getRecWidth()
	{
		if(parent == null)
		{
			if(advEntities.size() == 0)
				return 1;
			else
				return advEntities.get(0).getRecWidth();
		}
		else
		{
			return super.getRecWidth();
		}
	}

	@Override
	public void setIsDisplayingRec(boolean b)
	{
		super.setIsDisplayingRec(b);
		for(int i=0; i<advEntities.size(); i++)
		{
			advEntities.get(i).setIsDisplayingRec(b);
		}
	}
}
