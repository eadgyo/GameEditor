package Map;

import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;

import Addons.Entity;
import Maths.Form;
import Maths.Vector2D;
import Maths.Rectangle;
import Maths.Vector2D;
import Maths.sRectangle;

public class Layer 
{
	private String name;
	private Entity object;
	private ArrayList<Layer> layers;
	
	private float scale, realScale;
	private float visible, realVisible;
	private float theta, realTheta;
	private boolean flipH, realFlipH;
	private boolean flipV, realFlipV;
	
	private Vector2D pos, realPos;
	
	private sRectangle bounds;
	
	
	public Layer(Entity object)
	{
		this.name = object.getName();
		this.object = object;
		layers = new ArrayList<Layer>();
		
		scale = 1;
		visible = 1;
		theta = 0;
		realScale = 1;
		realVisible = 1;
		realTheta = 0;
		
		bounds = new sRectangle();
		
		pos = new Vector2D();
		realPos = new Vector2D();
		realPos.set(object.getLeftPos());

	}
	public Layer(String name)
	{
		this.name = name;
		object = null;
		layers = new ArrayList<Layer>();
		
		scale = 1;
		visible = 1;
		theta = 0;
		
		realScale = 1;
		realVisible = 1;
		realTheta = 0;
		
		bounds = new sRectangle();
	
		pos = new Vector2D();
		realPos = new Vector2D();
	}
	public Layer()
	{
		this.name = "";
		object = null;
		layers = new ArrayList<Layer>();
		
		scale = 1;
		visible = 1;
		theta = 0;
		
		realScale = 1;
		realVisible = 1;
		realTheta = 0;
		
		bounds = new sRectangle();
		
		pos = new Vector2D();
		realPos = new Vector2D();
	}
	public Layer clone()
	{
		Layer layer;
		if(this.object != null)
		{
			layer = new Layer(this.object.clone());
		}
		else
		{
			layer = new Layer();
		}
		
		layer.setName(name);
		layer.init(pos, realPos, scale, realScale, visible, realVisible,
		  theta, realTheta, flipH, realFlipH, flipV, realFlipV, bounds);
		
		for(int i=0; i<layers.size(); i++)
		{
			layers.add(layers.get(i).clone());
		}
		return layer;
	}
	public void init(Vector2D pos, Vector2D realPos, float scale, float realScale, float visible, float realVisible, float theta,
			float realTheta, boolean flipH, boolean realFlipH, boolean flipV, boolean realFlipV, sRectangle bounds)
	{
		this.pos.set(pos);
		this.realPos.set(realPos);
		this.scale = scale;
		this.realScale = realScale;
		this.visible = visible;
		this.realVisible = realVisible;
		this.theta = theta;
		this.realTheta = realTheta;
		this.flipH = flipH;
		this.realFlipH = realFlipH;
		this.flipV = flipV;
		this.realFlipV = realFlipV;
		this.bounds.set(bounds);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public Vector2D getCenter() {return this.getBounds().getCenter();}
	//name
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	//object
	public Entity getObject()
	{
		return object;
	}
	public void getObjects(ArrayList<Entity> entities)
	{
		if(object != null)
			entities.add(object);
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).getObjects(entities);
			}
	}
	public ArrayList<Entity> getObjects()
	{
		ArrayList<Entity> entities = new ArrayList<Entity>();
		this.getObjects(entities);
		return entities;
	}
	public void setObject(Entity object)
	{
		this.object = object;
	}
	
	//Layers
	public ArrayList<Layer> getLayers()
	{
		return layers;
	}
	public Layer getLayer(int n)
	{
		assert(n < layers.size());
		return layers.get(n);
	}
	public void addLayer(Layer layer)
	{
		layer.reset();
		layer.inHerit(this);
		layer.build();
		layers.add(layer);
	}
	public void addLayer(Layer layer, int i)
	{
		layer.reset();
		layer.inHerit(this);
		layer.build();
		layers.add(i, layer);
	}
	
	public void draw(Graphics g, sRectangle vision)
	{
		if(object != null)
			object.draw(g);
		else
			for(int i=layers.size() - 1; i > -1; i--)
			{
				if(layers.get(i).getBounds().collision(vision))
					layers.get(i).draw(g, vision);
			}
	}
	public void draw(sRectangle vision)
	{
		if(object != null)
			object.draw();
		else
			for(int i=layers.size() - 1; i > -1; i--)
			{
				if(layers.get(i).getBounds().collision(vision))
					layers.get(i).draw(vision);
			}
	}
	public void draw(Graphics g, Vector2D vec, sRectangle vision)
	{
		if(object != null)
			object.draw(g, vec);
		else
			for(int i=layers.size() - 1; i > -1; i--)
			{
				if(layers.get(i).getBounds().collision(vision))
					layers.get(i).draw(g, vec, vision);
			}
	}
	public void draw(Graphics g, Vector2D vec, sRectangle vision, float scale)
	{
		if(object != null)
			object.draw(g, vec, scale);
		else
			for(int i=layers.size() - 1; i > -1; i--)
			{
				if(layers.get(i).getBounds().collisionSat(vision))
					layers.get(i).draw(g, vec, vision, scale);
			}
	}
	public void draw(Vector2D vec, sRectangle vision)
	{
		if(object != null)
			object.draw(vec);
		else
			for(int i=layers.size() - 1; i > -1; i--)
			{
				if(layers.get(i).getBounds().collision(vision))
					layers.get(i).draw(vec, vision);
			}
	}
	public boolean isEmpty() {return (object == null && layers.size() == 0);}
	
	public float getScale() {return scale;}
	public float getVisible() {return visible;}
	public float getTheta() {return theta;}
	public Vector2D getPos() {return pos;}
	public boolean getFlipH() {return flipH;}
	public boolean getFlipV() {return flipV;}
	
	public float getRealScale() {return realScale;}
	public float getRealVisible() {return realVisible;}
	public float getRealTheta() {return realTheta;}
	public Vector2D getRealPos() {return realPos;}
	public boolean getRealFlipH() {return realFlipH;}
	public boolean getRealFlipV() {return realFlipV;}
	
	public void setPosX(float x)
	{
		Vector2D translate = new Vector2D();
		translate.x = x - this.pos.x;
		translate(translate);
	}
	public void setPosY(float y)
	{
		Vector2D translate = new Vector2D();
		translate.y = y - this.pos.y;
		translate(translate);
	}
	public void setPos(Vector2D pos)
	{
		Vector2D translate = new Vector2D();
		translate.x = pos.x - this.pos.x;
		translate.y = pos.y - this.pos.y;
		translate(translate);
	}
	public void setPosFree(Vector2D pos)
	{
		Vector2D translate = new Vector2D();
		translate.x = pos.x - this.realPos.x;
		translate.y = pos.y - this.realPos.y;
		translateFree(translate);
	}
	public void translate(Vector2D vec)
	{
		pos.translate(vec);
		translateFree(vec);
	}
	public void translateFree(Vector2D vec)
	{
		realPos.translate(vec);
		if(object != null)
			object.translate(vec);
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).translateFree(vec);
			}
	}
	
	public void setScale(float scale, Vector2D center)
	{
		float factor = scale/this.scale;
		this.scale(factor, center);
	}
	public void setScaleFree(float scale, Vector2D center)
	{
		float factor = scale/this.realScale;
		this.scaleFree(factor, center);
	}
	public void scale(float scale, Vector2D center)
	{
		this.scale *= scale;
		this.scaleFree(scale, center);
	}
	public void scaleFree(float scale, Vector2D center)
	{
		this.realScale *= scale;
		//this.realCenter.scale(scale, center);
		if(object != null)
			object.scale(scale, center);
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).scaleFree(scale, center);
			}
	}
	
	public void setVisible(float f)
	{
		visible(f/this.visible);
	}
	public void setVisibleFree(float f)
	{
		visibleFree(f/this.realVisible);
	}
	public void visible(float f)
	{
		visible *= f;
		if(visible > 1)
			visible = 1f;
		else if(visible < 0.01)
			visible = 0.005f;
		
		this.visibleFree(f);
	}
	public void visibleFree(float visible)
	{
		this.realVisible *= visible;
		if(this.object != null)
			object.setVisible(realVisible);
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).visibleFree(visible);
			}
	}
	
	public void setRotationDegree(float f, Vector2D center)
	{
		float thetaRadians = (f*(float)(Math.PI))/180;
		setRotationRadians(thetaRadians, center);
	}
	public void setRotationRadians(float f, Vector2D center)
	{
		rotateRadians(f - this.theta, center);
	}
	public void setRotationRadiansFree(float f, Vector2D center)
	{
		rotateRadiansFree(f - this.realTheta, center);
	}
	public void rotateDegrees(float f,  Vector2D center)
	{
		float thetaRadians = (f*(float)(Math.PI))/180;
		rotateRadians(thetaRadians, center);
	}
	public void rotateRadians(float f, Vector2D center)
	{
		this.theta += f;
		this.rotateRadiansFree(f, center);
	}
	public void rotateRadiansFree(float f, Vector2D center)
	{
		this.realTheta += f;
		if(object != null)
		{
			object.rotateRadians(f, center);
		}
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).rotateRadiansFree(f, center);
			}
	}
	
	public void setFlipH(boolean b, Vector2D center)
	{
		if(flipH != b)
		{
			flipH(center);
		}
	}
	public void setFlipHFree(boolean b, Vector2D center)
	{
		if(flipH != b)
		{
			flipHFree(center);
		}
	}
	public void flipH(Vector2D center)
	{
		flipH = !flipH;
		flipHFree(center);
	}
	public void flipHFree(Vector2D center)
	{
		realFlipH = !realFlipH;
		if(this.object != null)
			object.flipH(center);
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).flipHFree(center);
			}
	}
	
	public void setFlipV(boolean b, Vector2D center)
	{
		if(flipV != b)
		{
			flipV(center);
		}
	}
	public void setFlipVFree(boolean b, Vector2D center)
	{
		if(flipV != b)
		{
			flipVFree(center);
		}
	}
	public void flipV(Vector2D center)
	{
		flipV = !flipV;
		flipVFree(center);
	}
	public void flipVFree(Vector2D center)
	{
		realFlipV = !realFlipV;
		if(this.object != null)
			object.flipV(center);
		else
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).flipVFree(center);
			}
	}
	
	public void inHerit(Layer parent)
	{
		this.setPosFree(parent.getRealPos());
		this.setVisibleFree(parent.getRealVisible());
		this.setScaleFree(parent.getRealScale(), parent.getCenter());
		this.setRotationRadiansFree(parent.getRealTheta(), parent.getCenter());
		this.setFlipHFree(parent.getRealFlipH(), parent.getCenter());
		this.setFlipVFree(parent.getRealFlipV(), parent.getCenter());
	}
	
	public void build()
	{
		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).build();
		}
		//translate build
		translateFree(new Vector2D(pos));
		
		//rotate build
		rotateRadiansFree(theta, this.getCenter());
		
		//scale build
		scaleFree(scale, this.getCenter());
		
		//visible build
		visibleFree(visible);
		
		//flipH build
		setFlipHFree(flipH, this.getCenter());
		
		//flipV build
		setFlipVFree(flipV, this.getCenter());
	}
	public void reset()
	{
		//translate reset
		translateFree(new Vector2D(pos.multiply(-1)));
		
		//rotate reset
		rotateRadiansFree(-theta, this.getCenter());
		
		//scale reset
		scaleFree(1/scale ,this.getCenter());
		
		//visible free
		visibleFree(1/visible);
		
		//flipH build
		setFlipHFree(false, this.getCenter());
		
		//flipV build
		setFlipVFree(false, this.getCenter());

		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).reset();
		}
	}
	
	public void updateBoundsRoot()
	{
		if(layers.size() > 0)
		{
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).updateBounds();
			}
			
			sRectangle rec1 = layers.get(0).getBounds();
			float xMin = rec1.getMinX();
			float xMax = rec1.getMaxX();
			float yMin = rec1.getMinY();
			float yMax = rec1.getMaxY();
			for(int i=1; i<layers.size(); i++)
			{
				sRectangle rec2 = layers.get(i).getBounds();
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
		{
			bounds.set(0,0,0,0);
		}
	}
	public void updateBounds()
	{
		if(object != null)
		{
			bounds.set(object.getBounds());
			
			Vector2D newPos = bounds.getLeft();
			Vector2D translate = new Vector2D(realPos, newPos);
			pos.translate(translate);
			realPos.translate(translate);
		}
		else if(layers.size() > 0)
		{
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).updateBounds();
			}
			
			sRectangle rec1 = layers.get(0).getBounds();
			float xMin = rec1.getMinX();
			float xMax = rec1.getMaxX();
			float yMin = rec1.getMinY();
			float yMax = rec1.getMaxY();
			for(int i=1; i<layers.size(); i++)
			{
				sRectangle rec2 = layers.get(i).getBounds();
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
			
			Vector2D newPos = bounds.getLeft();
			Vector2D translate = new Vector2D(realPos, newPos);
			pos.translate(translate);
			realPos.translate(translate);
			
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).getPos().translate(translate.multiply(-1));
			}
		}
		else
		{
			bounds.set(0,0,0,0);
		}
	}
	public sRectangle getBounds()
	{
		return bounds;
	}
	
	public void drawBounds(Graphics g, Vector2D vec, float scale)
	{
		Form formScale = bounds.clone();
		formScale.scale(scale, new Vector2D());
		formScale.draw(g, vec);
		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).drawBounds(g, vec, scale);
		}
	}
	public void drawBounds(Graphics g, Vector2D vec)
	{
		bounds.draw(g, vec);
		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).drawBounds(g, vec);
		}
	}
}
