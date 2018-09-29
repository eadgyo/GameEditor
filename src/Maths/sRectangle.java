package Maths;


public class sRectangle extends Form
{
	public final static long serialVersionUID = 9197968313933241114L;
	
	protected Vector2D length;
	public sRectangle()
	{
		super(4);
		this.length = new Vector2D();
		set(0, 0, 0, 0);
	}
	
	public static sRectangle createSRectangleCenter(Vector2D center, Vector2D length)
	{
		sRectangle rec = new sRectangle(center, length);
		rec.translate(new Vector2D(rec.getCenter(), center));
		return rec;
	}
	public sRectangle(Vector2D left, Vector2D length)
	{
		super(4);
		this.length = new Vector2D();
		setLeft(left, length);
	}
	public sRectangle(float x, float y, float width, float height)
	{
		super(4);
		this.length = new Vector2D();
		setLeft(x, y, width, height);
	}
	public sRectangle(Form form) 
	{
		super(form);
		assert(form.size() == 4);
		
		//Les 2 points doivent être opposés
		Vector2D vec = new Vector2D(points.get(0), points.get(2));
		orientation.setPos(points.get(0).add(vec.multiply(0.5f)));
		
		Vector2D side = new Vector2D(points.get(0), points.get(1));
		length = new Vector2D();
		length.x = side.getMagnitude();
		
		side.set(points.get(0), points.get(1));
		length.y = side.getMagnitude();
	}

	public void set(Form form)
	{
		assert(form.size() == 4);
		
		for(int i=0; i<4; i++)
			points.set(i, form.getLocal(i));
		
		//Les 2 points doivent être opposés
		Vector2D vec = new Vector2D(form.getLocal(0), form.getLocal(2));
		orientation.setPos(form.getLocal(0).add(vec.multiply(0.5f)));
		
		Vector2D side = new Vector2D(form.getLocal(0), form.getLocal(1));
		length.x = side.getMagnitude();
		
		side.set(form.getLocal(0), form.getLocal(1));
		length.y = side.getMagnitude();
	}
	public void set(sRectangle rec)
	{
		this.setLeft(rec.getLeft(), rec.getLength());
	}
	public void set(Vector2D center, Vector2D length)
	{
		orientation.setPos(center);
		this.length.set(length);
		points.get(0).set(center.x - 0.5f*length.x, center.y - 0.5f*length.y);
		points.get(1).set(center.x - 0.5f*length.x, center.y + 0.5f*length.y);
		points.get(2).set(center.x + 0.5f*length.x, center.y + 0.5f*length.y);
		points.get(3).set(center.x + 0.5f*length.x, center.y - 0.5f*length.y);
	}
	public void set(float x, float y, float width, float height)
	{
		orientation.setX(x);
		orientation.setY(y);
		this.length.x = width;
		this.length.y = height;
		points.get(0).set(- 0.5f*length.x, - 0.5f*length.y);
		points.get(1).set(- 0.5f*length.x, + 0.5f*length.y);
		points.get(2).set(+ 0.5f*length.x, + 0.5f*length.y);
		points.get(3).set(+ 0.5f*length.x, - 0.5f*length.y);
	}
	
	public void setLeft(Vector2D left, Vector2D length)
	{
		this.length.set(length);
		orientation.setPos(new Vector2D(left.x + length.x*0.5f, left.y + length.y*0.5f));

		points.get(0).set(- 0.5f*length.x, - 0.5f*length.y);
		points.get(1).set(- 0.5f*length.x, + 0.5f*length.y);
		points.get(2).set(+ 0.5f*length.x, + 0.5f*length.y);
		points.get(3).set(+ 0.5f*length.x, - 0.5f*length.y);
	}
	
	public void setLeft(float x, float y, float width, float height)
	{
		this.length.set(width, height);
		orientation.setPos(new Vector2D(x + width*0.5f, y + height*0.5f));
		
		points.get(0).set(- 0.5f*length.x, - 0.5f*length.y);
		points.get(1).set(- 0.5f*length.x, + 0.5f*length.y);
		points.get(2).set(+ 0.5f*length.x, + 0.5f*length.y);
		points.get(3).set(+ 0.5f*length.x, - 0.5f*length.y);
	}
	
	public float getWidth() {return length.x;}
	public float getHeight() {return length.y;}
	public Vector2D getLength() {return length;}
	
	public boolean isInside(sRectangle rect)
	{
		if(rect.getX() > points.get(0).x && rect.getX(2) < points.get(2).x)
			if(rect.getY() > points.get(0).y &&  rect.getY(2) < points.get(2).y)
				return true;
		return false;
	}
	//with border
	public boolean isInsideBorder(sRectangle rect)
	{
		if(rect.getX() >= points.get(0).x && rect.getX(2) <= points.get(2).x)
			if(rect.getY() >= points.get(0).y &&  rect.getY(2) <= points.get(2).y)
				return true;
		return false;
	}
	public boolean isInside(Vector2D p)
	{
		if(p.x > points.get(0).x && p.x < points.get(2).x)
			if(p.y > points.get(0).y &&  p.y < points.get(2).y)
				return true;
		return false;
	}	
	//with border
	public boolean isInsideBorder(Vector2D p)
	{
		if(p.x >= points.get(0).x && p.x <= points.get(2).x)
			if(p.y >= points.get(0).y &&  p.y <= points.get(2).y)
				return true;
		return false;
	}
	public boolean collision(sRectangle rect)
	{
		assert(false):"Pas fini";
		return collisionA(rect) || rect.collisionA(this);
	}
	public boolean collisionA(sRectangle rect)
	{
		assert(false):"Pas fini";
		for(int i=0; i<4; i++)
			if(isInsideBorder(rect.get(i)))
				return true;
		return false;
	}
	//with border	
	public boolean collisionBorder(sRectangle rect)
	{
		assert(false):"Pas fini";
		for(int i=0; i<4; i++)
			if(isInsideBorder(rect.get(i)))
				return true;
		return false;
	}
	
	public Vector2D getLeft()
	{
		return points.get(0);
	}
}
