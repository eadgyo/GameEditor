package Base;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import Maths.Form;
import Maths.Vector2D;
import Maths.Rectangle;
import Maths.Vector2D;
import Maths.sRectangle;

public class Image implements java.io.Serializable
{
	public final static long serialVersionUID = 2912367492515536496L;
	
	protected Graphics graphics;
	protected SpriteData spriteData;
	protected Color colorFilter;
	protected int cols;
	protected int currentFrame;
	protected boolean isDisplayingRec;
	
	protected Rectangle rec; 
	
	protected float visible;
	protected boolean isInitialized;
	protected int startFrame;
	protected int endFrame;
	
	public Image()
	{
		isDisplayingRec = false;
		visible = 1;
		isInitialized = false;
		spriteData = new SpriteData();
		colorFilter = Color.white;
		rec = new Rectangle();
	}
	public void set(Image image)
	{
		SpriteData l_spriteData = image.getSpriteData();
		this.initialize(image.getGraphics(), (int)l_spriteData.rect.getWidth(), (int)l_spriteData.rect.getHeight(), image.getCols(), l_spriteData.texture, image.getTextureName());
		this.isDisplayingRec = image.getIsRectDisplaying();
		this.setCurrentFrame(image.getCurrentFrame());
		this.rec.set(image.getRectangle());	
		visible = image.getVisible();
	}
	public Image(Image image)
	{
		isDisplayingRec = false;
		visible = 1;
		isInitialized = false;
		spriteData = new SpriteData();
		colorFilter = Color.white;
		SpriteData l_spriteData = image.getSpriteData();
		rec = new Rectangle();
		this.initialize(image.getGraphics(), (int)l_spriteData.rect.getWidth(), (int)l_spriteData.rect.getHeight(), image.getCols(), l_spriteData.texture, image.getTextureName());
		this.setCurrentFrame(image.getCurrentFrame());
		rec.set(image.getRectangle());
	}
	public Image clone()
	{
		Image l_image = new Image(this);
		return l_image;
	}
	public void reset()
	{
		this.setFlipV(false);
		this.setFlipH(false);
		this.setVisible(1);
		this.rec.set(new Vector2D(), new Vector2D(spriteData.rect.getWidth(), spriteData.rect.getHeight()), 0);
	}
	///////////////////
	//		Get      //
	///////////////////
	public boolean getIsRectDisplaying()
	{
		return isDisplayingRec;
	}
	public Rectangle getRectangle()
	{
		return rec;
	}
	public int getCols() 
	{
		return cols;
	}
	public float getVisible() 
	{
		return visible;
	}
	public int getOrigWidth() 
	{
		return (int) spriteData.rect.getWidth();
	}
	public int getOrigHeight() 
	{
		return (int) spriteData.rect.getHeight();
	}
	public float getWidth() 
	{
		return rec.getLength().x;
	}
	public float getHeight() 
	{
		return rec.getLength().y;
	}
	public float getX() 
	{
		//float centerX = (float) (spriteData.pos.x + 0.5*spriteData.width*Math.cos(spriteData.angle) - 0.5*spriteData.height*Math.sin(spriteData.angle));
		return rec.getCenter().x;
	}
	public float getY() 
	{
		//float centerY = (float) (spriteData.pos.y + 0.5*spriteData.width*Math.sin(spriteData.angle) + 0.5*spriteData.height*Math.cos(spriteData.angle));
		return rec.getCenter().y;
	}
	public Vector2D getPos()
	{
		//Point2D l_point = new Point2D(getCenterX(), getCenterY());
		return rec.getCenter();
	}
	public Vector2D getLeftPos()
	{
		return rec.getLeft();
	}
	public void setFrames(int start, int end) 
	{
		this.startFrame = start; this.endFrame = end;
	}
	public float getRadians() 
	{
		return rec.getAngle();
	}
	public float getRadians(Vector2D vec)
	{
		return rec.getAngle(vec);
	}
	public float getDegrees() 
	{
		return (float) ((rec.getAngle()*180)/Math.PI);
	}
	public float getDegrees(Vector2D vec) 
	{
		return (float) ((rec.getAngle(vec)*180)/Math.PI);
	}
	public int getCurrentFrame() 
	{
		return currentFrame;
	}
	public sRectangle getSpriteDataRect() 
	{
		return spriteData.rect;
	}
	public Color getColorFilter() 
	{
		return colorFilter;
	}
	public SpriteData getSpriteData() 
	{
		return spriteData;
	}
	public boolean getFlipV() 
	{
		return spriteData.flipV;
	}
	public boolean getFlipH() 
	{
		return spriteData.flipH;
	}
	public Graphics getGraphics() 
	{
		return graphics;
	}
	public float getScale() 
	{
		//float scale = this.rec.getScale();
		//float scale2 = getRecWidth()/getSpriteDataWidth();
		
		//assert(scale == scale2);
		return rec.getScale();
	}
	public boolean isInitialized() 
	{
		return isInitialized;
	}
	public String getTextureName()
	{
		return spriteData.textureName;
	}
	public float getRecWidth()
	{
		return this.rec.getWidth();
	}
	public float getSpriteDataWidth()
	{
		return this.spriteData.rect.getWidth();
	}
	
	///////////////////
	//		Set      //
	///////////////////
	public void setCols(int cols)
	{
		this.cols = cols;
	}
	public void setIsDisplayingRec(boolean isDisplayingRec)
	{
		this.isDisplayingRec = isDisplayingRec;
	}
	public void setX(float x) 
	{
		setPos(new Vector2D(x,this.getX()));
	}
	public void setY(float y) 
	{
		setPos(new Vector2D(this.getY(),y));
	}
	public void setPos(Vector2D center) 
	{
		Vector2D vec = new Vector2D(this.getPos(), center);
		translate(vec);
	}
	public void setLeftPos(Vector2D left)
	{
		Vector2D vec = new Vector2D(this.getLeftPos(), left);
		translate(vec);
	}
	public void setDegrees(float degrees) 
	{
		setRadians((float) ((degrees*Math.PI)/180), new Vector2D(1,0)); 
	}
	public void setDegrees(float degrees, Vector2D vec)
	{
		setRadians((float) ((degrees*Math.PI)/180), vec);
	}

	public void setRadians(float radians, Vector2D vec)
	{
		float angle = radians - this.rec.getAngle(vec);
		this.rotateRadians(angle, this.getPos());
	}
	public void setRadians(float radians)
	{
		this.setRadians(radians, new Vector2D(1,0));
	}
	
	public void setSpriteDataRect(sRectangle rect) 
	{
		this.spriteData.rect = rect;
	}
	public void setColorFilter(Color colorFilter) 
	{
		this.colorFilter = colorFilter;
	}
	public void setFlipH(boolean flipHorizontal) 
	{
		if(spriteData.flipH != flipHorizontal)
		{
			this.rec.flipH(this.getPos());
		}
		spriteData.flipH = flipHorizontal;
	}
	public void setFlipV(boolean flipVertical) 
	{
		if(spriteData.flipV != flipVertical)
		{
			this.rec.flipV(this.getPos());
		}
		spriteData.flipV = flipVertical;
	}
	public void setScale(float scale) 
	{
		float factor = scale*(1/this.getScale());

		this.scale(factor, this.getPos());
	}
	public void setGraphics(Graphics g) {graphics = g;}
	public void setSize(int size)
	{
		this.setScale((size)/this.getSpriteDataWidth());
	}
	public void setSpriteData(SpriteData spriteData)
	{
		this.spriteData.flipH = spriteData.flipH;
		this.spriteData.flipV = spriteData.flipV;
		
		this.spriteData.texture = spriteData.texture;
		this.spriteData.textureName = spriteData.textureName;
		
		this.spriteData.rect.set(spriteData.rect);
	}
	public void setRec(Rectangle rec)
	{
		this.rec.set(rec);
	}
	public void setRec(Form rec)
	{
		this.rec.set(rec);
	}
	
	public void clearTexture()
	{
		this.spriteData.texture = null;
	}
	public void loadTexture()
	{
		this.spriteData.texture = FileManager.getInstance().getDefTexture(spriteData.textureName);
	}
	
	///////////////////////////////
	//		Transformations      //
	///////////////////////////////
	public void translate(Vector2D vec)
	{
		rec.translate(vec);
	}
	public void translateX(float vecX)
	{
		rec.translate(new Vector2D(vecX, 0));
	}
	public void translateY(float vecY)
	{
		rec.translate(new Vector2D(0, vecY));
	}
	public void flipH(Vector2D center) 
	{
		rec.flipH(center);
		spriteData.flipH = !spriteData.flipH;
	}
	public void flipV(Vector2D center) 
	{
		rec.flipV(center);
		spriteData.flipV = !spriteData.flipV;
	}
	public void scale(float factor, Vector2D center)
	{
		if(factor != 0)
			rec.scale(factor, center);
		else
			rec.scale(0.0001f, center);
	}
	public void rotateRadians(float radians, Vector2D center) 
	{
		rec.rotateRadians(radians, center);
		//Math.Extend.this.spriteData.angle
	}
	public void visible(float f)
	{
		visible *= f;
		if(visible > 1)
			visible = 1f;
		else if(visible < 0.01)
			visible = 0.005f;
	}
	
	//set
	public void setScale(float scale, Vector2D center)
	{
 		float factor = (scale*this.getSpriteDataWidth())/this.getRecWidth();
		this.scale(factor, center);
	}
	public void setDegrees(float degrees, Vector2D vec, Vector2D center)
	{
		float radians = (float) (degrees*Math.PI)/180;
		this.setRadians(radians, vec, center);
	}
	public void setRadians(float radians, Vector2D vec, Vector2D center)
	{
		float angle = radians - this.rec.getAngle(vec);
		this.rotateRadians(angle, center);
	}
	public void setPositionX(float x, Vector2D vec)
	{
		float scalar = vec.scalarProduct(new Vector2D(this.getPos()));
		translate(vec.multiply(x - scalar));
	}
	public void setPositionY(float y, Vector2D vec)
	{
		Vector2D l_vec = vec.getPerpendicular();
		float scalar = l_vec.scalarProduct(new Vector2D(this.getPos()));
		translate(l_vec.multiply(y - scalar));
	}
	public void setPosition(Vector2D p)
	{
		Vector2D vec = new Vector2D(this.getPos(), p);
		translate(vec);
	}
	public void setPositionX(float x)
	{
		translateX(x - this.getX());
	}
	public void setPositionY(float y)
	{
		translateY(y - this.getY());
	}
	public void setFlipH(boolean b, Vector2D center)
	{
		if(this.spriteData.flipH != b)
			this.flipH(center);
	}
	public void setFlipV(boolean b, Vector2D center)
	{
		if(this.spriteData.flipV != b)
			this.flipV(center);
	}
	
	public void setVisible(float visible)
	{
		visible(visible/this.visible);
	}

	
	///////////////////////////////
	//		Other Functions      //
	///////////////////////////////
	public void initialize(Graphics graphics, int width, int height, int cols, BufferedImage texture, String textureName)
	{
		this.graphics = graphics;
		spriteData.texture = texture;
		if(cols == 0)
			this.cols = 1;
		else
			this.cols = cols;
		spriteData.rect.setLeft((currentFrame % this.cols)*width,
				(currentFrame / this.cols)*height,
				width,
				height);
		this.rec.set(new Vector2D(width*0.5f,  height*0.5f), new Vector2D(width, height), 0);
		spriteData.textureName = textureName;
		isInitialized = true;
	}
	public void draw()
	{
		this.draw(this.graphics);
	}
	public void draw(Graphics g)
	{
		if(g == null || spriteData.texture == null || visible < 0.01f)
			return;
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visible));
		
		g2d.translate(rec.getCenter().x + (int) (rec.getLength().x)*((spriteData.flipH)?0:0),
				rec.getCenter().y + (int) (rec.getLength().y)*((spriteData.flipV)?0:0));
		
		g2d.scale(rec.getLength().x/spriteData.rect.getWidth(), rec.getLength().y/spriteData.rect.getHeight());
		
		float rot = (float) (-rec.getAngle() + ((spriteData.flipH)?Math.PI:0));
		if(Math.abs(rot) > 0.001 && Math.abs(rot - Math.PI*2) > 0.001)
			g2d.rotate(rot, 0,  0);
		
		g2d.translate(-spriteData.rect.getWidth()*(0.5 - ((spriteData.flipH)?1:0)), -spriteData.rect.getHeight()*(0.5 - ((spriteData.flipV)?1:0)));
		
        g2d.drawImage(spriteData.texture,
    			0,
    			0,
    			(int) (spriteData.rect.getWidth())*((spriteData.flipH)?-1:1),
    			(int) (spriteData.rect.getHeight())*((spriteData.flipV)?-1:1),
    			(int) (spriteData.rect.getX()),
    			(int) (spriteData.rect.getY()),
    			(int) (spriteData.rect.getX() + spriteData.rect.getWidth()),
    			(int) (spriteData.rect.getY() + spriteData.rect.getHeight()),
    			null, null);
		g2d.setTransform(old);
		
		if(isDisplayingRec)
		{
			g2d.setColor(Color.CYAN);
			Polygon pol = this.rec.getPolygon();
	    	g2d.drawPolygon(pol);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	public void draw(Vector2D translation)
	{
		this.draw(this.graphics, translation);
	}
	public void draw(Graphics g, Vector2D translation)
	{
		if(g == null || spriteData.texture == null || visible < 0.01f)
			return;
		
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visible));
		
		g2d.translate(translation.x + rec.getCenter().x + (int) (rec.getLength().x)*((spriteData.flipH)?-0:0),
				translation.y + rec.getCenter().y + (int) (rec.getLength().y)*((spriteData.flipV)?0:0));
		
		g2d.scale(rec.getLength().x/spriteData.rect.getWidth(), rec.getLength().y/spriteData.rect.getHeight());
		
		float rot = (float) (-rec.getAngle() + ((spriteData.flipH)?Math.PI:0));
		if(Math.abs(rot) > 0.001 && Math.abs(rot - Math.PI*2) > 0.001)
			g2d.rotate(rot, 0,  0);
		
		g2d.translate(-spriteData.rect.getWidth()*(0.5 - ((spriteData.flipH)?1:0)), -spriteData.rect.getHeight()*(0.5 - ((spriteData.flipV)?1:0)));
		
        g2d.drawImage(spriteData.texture,
    			0,
    			0,
    			(int) (spriteData.rect.getWidth())*((spriteData.flipH)?-1:1),
    			(int) (spriteData.rect.getHeight())*((spriteData.flipV)?-1:1),
    			(int) (spriteData.rect.getX()),
    			(int) (spriteData.rect.getY()),
    			(int) (spriteData.rect.getX() + spriteData.rect.getWidth()),
    			(int) (spriteData.rect.getY() + spriteData.rect.getHeight()),
    			null, null);
		g2d.setTransform(old);
		
		if(isDisplayingRec)
		{
			g2d.setColor(Color.CYAN);
			Rectangle rec = this.rec.clone();
			rec.translate(translation);
			Polygon pol = rec.getPolygon();
	    	g2d.drawPolygon(pol);
		}
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	public void draw(Graphics g, Vector2D translation, float scale)
	{
		if(g == null || spriteData.texture == null || visible < 0.01f || scale == 0)
			return;
		
		Rectangle rec = new Rectangle(this.rec);
		rec.scale(scale, new Vector2D());
		
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visible));
		
		g2d.translate(translation.x + rec.getCenter().x + (int) (rec.getLength().x)*((spriteData.flipH)?-0:0),
				translation.y + rec.getCenter().y + (int) (rec.getLength().y)*((spriteData.flipV)?0:0));
		
		g2d.scale(rec.getLength().x/spriteData.rect.getWidth(), rec.getLength().y/spriteData.rect.getHeight());
		
		float rot = (float) (-rec.getAngle() + ((spriteData.flipH)?Math.PI:0));
		if(Math.abs(rot) > 0.001 && Math.abs(rot - Math.PI*2) > 0.001)
			g2d.rotate(rot, 0,  0);
		
		g2d.translate(-spriteData.rect.getWidth()*(0.5 - ((spriteData.flipH)?1:0)), -spriteData.rect.getHeight()*(0.5 - ((spriteData.flipV)?1:0)));
		
        g2d.drawImage(spriteData.texture,
    			0,
    			0,
    			(int) (spriteData.rect.getWidth())*((spriteData.flipH)?-1:1),
    			(int) (spriteData.rect.getHeight())*((spriteData.flipV)?-1:1),
    			(int) (spriteData.rect.getX()),
    			(int) (spriteData.rect.getY()),
    			(int) (spriteData.rect.getX() + spriteData.rect.getWidth()),
    			(int) (spriteData.rect.getY() + spriteData.rect.getHeight()),
    			null, null);
		g2d.setTransform(old);
		
		if(isDisplayingRec)
		{
			g2d.setColor(Color.CYAN);
			rec.translate(translation);
			Polygon pol = rec.getPolygon();
	    	g2d.drawPolygon(pol);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

	public void setCurrentFrame(int current)
	{
		if(current > 0)
		{
			currentFrame = current;
			setRect();
		}
	}
	
	public boolean hasAlpha()
	{
		try 
		{
			PixelGrabber pg = new PixelGrabber(spriteData.texture, 0, 0, 1, 1, false);
			pg.grabPixels();

			return pg.getColorModel().hasAlpha();
		}
		catch (InterruptedException e) 
		{
			return false;
		}
	}
	
	public void setRect()
	{
		spriteData.rect.setLeft((currentFrame % cols)*spriteData.rect.getWidth(),
				(currentFrame / cols)*spriteData.rect.getHeight(),
				spriteData.rect.getWidth(),
				spriteData.rect.getHeight());
	}
}
