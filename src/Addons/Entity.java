package Addons;

import Base.Image;
import Maths.*;
import Maths.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Entity extends Image
{
	public static final long serialVersionUID = -2383654512432562609L;
	public static final float damping = 0.999f;
	public static final float dampingRotation = 0.95f;
	public static float damping_pow = 0f;
	public static float dampingRotation_pow = 0f;
	
	protected float rotation;
	protected Vector2D velocity;
	protected Vector2D acceleration;
	protected Vector2D lastAcceleration;
	protected Vector2D forceAccum;
	protected float torqueAccum;
	
	protected float health;
	protected boolean isActivated;
	protected boolean isActivatedCollision;
	protected Animation anim;
	protected String name;
	protected String group;
	
	//Physics
	protected float inverseMass; //0 fixed object, 1 moveable object
	protected float inverseInertia;
	
	
	public Entity()
	{
		super();
		velocity = new Vector2D();
		acceleration = new Vector2D();
		health = 0;
		isInitialized = false;
		isActivated = false;
		isActivatedCollision = false;
		anim = null;
		name = "NoName";
		group = "Default";
		
		init();
	}
	public Entity clone()
	{
		Entity l_entity = new Entity(this);
		return l_entity;
	}
	public Entity(Entity entity)
	{
		super(entity);
		velocity = new Vector2D();
		acceleration = new Vector2D();
		health = 0;
		isInitialized = false;
		isActivated = false;
		isActivatedCollision = false;
		anim = null;
		name = "NoName";
		group = "Default";
		
		init();
		
		set(entity);
	}
	public Entity(Image image)
	{
		super(image);
		velocity = new Vector2D();
		acceleration = new Vector2D();
		health = 0;
		isInitialized = false;
		isActivated = false;
		isActivatedCollision = false;
		anim = null;
		name = "NoName";
		group = "Default";
		init();
	}
	public void set(Entity entity)
	{
		super.set(entity);
		if(entity.getAnim() != null)
			this.setAnim(entity.getAnim().clone());
		this.setName(entity.getName());
		this.setGroup(entity.getGroup());
		this.setVelocity(entity.getVelocity());
		this.setAcceleration(entity.getAcceleration());
		this.setInverseMass(entity.getInverseMass());
		this.setInverseInertia(entity.getInverseInertia());
		this.setForceAccum(entity.getForceAccum());
		this.setTorqueAccum(entity.getTorqueAccum());
	}
	
	public void init()
	{
		velocity = new Vector2D();
		acceleration = new Vector2D();
		health = 0;
		forceAccum = new Vector2D();
		torqueAccum = 0;
		inverseMass = 0;
		inverseInertia = 0;
		lastAcceleration = new Vector2D();
	}
	public void ready()
	{
		anim.resetForms();
		this.updateInertia();
	}
	
	//get
	public ArrayList<AdvForm> getAdvFormsCompute(Vector2D pos)
	{
		Vector2D center = this.getPos();
		ArrayList<AdvForm> l_advForms = anim.getAForms();
		ArrayList<AdvForm> l_advFormsCompute = new ArrayList<AdvForm>(l_advForms.size());
		for(int i=0; i<l_advForms.size(); i++)
		{
			AdvForm l_advForm = l_advForms.get(i).clone();
			assert(l_advForm.getScale() == 1f && l_advForm.getOmega() == 0 && !l_advForm.getFlipH() && !l_advForm.getFlipV());
			
			float f = this.getScale();
			float rot = this.getRadians();

			
			l_advForm.scale(this.getScale(), center);
			l_advForm.translate(pos);
			l_advForm.rotateRadians(this.getRadians(), center);
			
			
			if(this.getFlipH())
				l_advForm.flipH(center);
			if(this.getFlipV())
				l_advForm.flipV(center);
			l_advFormsCompute.add(l_advForm);

			l_advForm.setPos(this.getPos());
		}
		return l_advFormsCompute;
	}
	public ArrayList<AdvForm> getAdvFormsCompute()
	{
		return this.getAdvFormsCompute(this.getPos());
	}
	public Vector2D getVelocity() {return velocity;}
	public Vector2D getAcceleration() {return acceleration;}
	public float getMass() {return 1f/inverseMass;}
	public float getHealth() {return health;}
	public boolean getActive() {return isActivated;}
	public boolean getActiveCollision() {return isActivatedCollision;}
	public int getSizeAnim() {return anim.size();}
	
	public sRectangle getBounds()
	{
		float xMin = rec.getMinX();
		float xMax = rec.getMaxX();
		float yMin = rec.getMinY();
		float yMax = rec.getMaxY();
		return new sRectangle(xMin ,
				yMin,
				xMax - xMin,
				yMax - yMin);
	}
	public ArrayList<Rectangle> getSelectionRectangle()
	{
		ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
		rectangles.add(this.rec);
		return rectangles;
	}
	
	public Animation getAnim()
	{
		return anim;
	}	
	public String getName() 
	{
		return name;
	}
	public String getGroup() 
	{
		return group;
	}
	public float getInverseMass()
	{
		return inverseMass;
	}
	public float getInverseInertia()
	{
		return inverseInertia;
	}
	public float getRotation()
	{
		return rotation;
	}
	public Vector2D getLastAcceleration()
	{
		return lastAcceleration;
	}
	
	public Vector2D getForceAccum()
	{
		return forceAccum;
	}
	public float getTorqueAccum()
	{
		return torqueAccum;
	}
	
	//set
	public void setVelocity(Vector2D velocity) {this.velocity.set(velocity);}
	public void setVelocity(float x, float y) {this.velocity.x = x; this.velocity.y = y;}
	public void setVelocityX(float x) {this.velocity.x = x;}
	public void setVelocityY(float y) {this.velocity.y = y;}
	public void setAcceleration(Vector2D acceleration) {this.acceleration.set(acceleration);}
	public void setAcceleration(float x, float y) {this.acceleration.x = x; this.acceleration.y = y;}
	public void setAccelerationX(float x) {this.acceleration.x = x;}
	public void setAccelerationY(float y) {this.acceleration.y = y;}
	public void setMass(float mass) {this.inverseMass = 1f/mass;}
	public void setHealth(float health) {this.health = health;}
	public void setActive(boolean isActivated) {this.isActivated = isActivated;}
	public void setActiveCollision(boolean isActivatedCollision) {this.isActivatedCollision = isActivatedCollision;}
	public void setCurrentAnim(int currentAnim)
	{
		assert(currentAnim > getSizeAnim() || currentAnim < 0) : "Dépassement taille nombre animation";
		anim.setCurrentFrame(currentAnim);
		super.setFrames(anim.getX(), anim.getY());
	}
	public void setAnim(Animation anim)
	{
		anim.setEntity(this);
		this.anim = anim;
		isActivatedCollision = true;
		isActivated = true;
		isInitialized = true;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	public void setGroup(String group) 
	{
		this.group = group;
	}
	public void setInverseMass(float inverseMass)
	{
		this.inverseMass = inverseMass;
	}
	public void setInverseInertia(float inverseInertia)
	{
		this.inverseInertia = inverseInertia;
	}
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	public void setForceAccum(Vector2D forceAccum)
	{
		this.forceAccum.set(forceAccum);
	}
	public void setTorqueAccum(float torqueAccum)
	{
		this.torqueAccum = torqueAccum;
	}
	
	
	public Entity(Graphics graphics, PointInt length, int cols, BufferedImage texture, String textureName, Animation anim)
	{
		super();//call parent constructor
		
		isInitialized = false;
		isActivated = false;
		isActivatedCollision = false;
		
		name = "NoName";
		group = "Default";
		
		init();
		
		this.initialize(graphics, length.x, length.y, cols, texture, textureName, anim);
	}
	
	public Entity(Graphics graphics, int width, int height, int cols, BufferedImage texture, String textureName, Animation anim)
	{
		super();//call parent constructor
		velocity = new Vector2D();
		acceleration = new Vector2D();
		inverseMass = 0;
		inverseInertia = 0;
		health = 0;
		isInitialized = false;
		isActivated = false;
		isActivatedCollision = false;
		
		name = "NoName";
		group = "Default";
		
		this.initialize(graphics, width, height, cols, texture, textureName, anim);
	}
	
	public void initialize(Graphics graphics, int width, int height, int cols, BufferedImage texture, String textureName, Animation anim)
	{
		super.initialize(graphics, width, height, cols, texture, textureName);
		this.anim = anim;
		isActivatedCollision = true;
		isActivated = true;
		isInitialized = true;
	}
	
	public void update(float dt)
	{
		//super.update(dt);
		this.integrate(dt);
	}
	
	public void computeBounds()
	{
	}
	
	public boolean collisionSat(Entity entity)
	{
		ArrayList<AdvForm> l_advFormCompute = this.getAdvFormsCompute();
		ArrayList<AdvForm> l_advFormCompute2 = entity.getAdvFormsCompute();
		
		for(int i=0; i<l_advFormCompute.size(); i++)
		{
			for(int j=0; j<l_advFormCompute2.size(); j++)
			{
				if(l_advFormCompute.get(i).collisionSat(l_advFormCompute2.get(j)))
					return true;
			}
		}
		return false;
	}



	public boolean getIsActivated()
	{
		return isActivated;
	}
	public boolean getIsActivatedCollision()
	{
		return isActivatedCollision;
	}
	public String toString()
	{
		return this.name;
	}

	
	//Physics
	public void wrapAngle()
	{
	}
	public void integrate(float dt)
	{
		if(inverseMass <= 0.0f)
			return;
		assert(dt > 0.0f);
		lastAcceleration.set(acceleration);
		acceleration.reset();
		
		//Integrate position
		setPos(getPos().addScaledVector(velocity, dt));
		this.rotateRadians(rotation*dt, this.getPos());
		//this.wrapAngle();
		
		//Integrate Velocity
		acceleration.selfAddScaledVector(forceAccum, inverseMass);
		velocity.selfAddScaledVector(acceleration, dt);
		velocity.selfMultiply(damping_pow);
		
		float rotationAcceleration = torqueAccum * inverseInertia;
		rotation += rotationAcceleration * dt;
		rotation *= dampingRotation_pow;
		
		clearAccumulator();
	}
	public void clearAccumulator()
	{
		forceAccum.set(0, 0);
		torqueAccum = 0;
	}
	public void addForce(Vector2D force)
	{
		forceAccum.selfAdd(force);
	}
	public void addForce(Vector2D force, Vector2D p)
	{
		forceAccum.selfAdd(force);
		torqueAccum += (p.sub(this.getPos())).crossProductZ(force);
	}
	
	
	
	public void updateInertia()
	{
		if(inverseMass <= 0f)
		{
			inverseInertia = 0;
			return;
		}
		ArrayList<AdvForm> advForms = this.getAdvFormsCompute();
		if(advForms.size() == 0)
		{
			inverseInertia = 0;
		}
		else if(advForms.size() == 1)
		{
			inverseInertia = advForms.get(0).getInverseInertia(this.getInverseMass());	
		}
		else
		{
			inverseInertia = 0;
			float totalSurface = 0;
			for(int i=0; i<advForms.size(); i++)
			{
				totalSurface += advForms.get(i).getSurface();
			}
			for(int i=0; i<advForms.size(); i++)
			{
				float surface = advForms.get(i).getSurface();
				float invMass = this.getInverseMass()*totalSurface/surface;
				inverseInertia += advForms.get(i).getInverseInertia(invMass);
			}
		}
	}
	public boolean isColliding(Entity B, Vector2D push, FloatA t)
	{
		ArrayList<AdvForm> advFormsA = this.getAdvFormsCompute();
		ArrayList<AdvForm> advFormsB = B.getAdvFormsCompute();
		if(advFormsA.size() == 0 || advFormsB.size() == 0)
		{
			return false;
		}
		else if(advFormsA.size() == 1 && advFormsB.size() == 1)
		{
			return advFormsA.get(0).collisionSat(advFormsB.get(0), this.getVelocity(), B.getVelocity(), push, t);
		}
		else
		{
			System.out.println("Error collision");
			return false;
		}
	}
	public ArrayList<Vector2D> findContacts(Vector2D push, Float t)
	{
		ArrayList<AdvForm> advFormsA = this.getAdvFormsCompute();
		ArrayList<Vector2D> contactsPointsA = new ArrayList<Vector2D>();
		if(advFormsA.size() == 0)
		{
		}
		else if(advFormsA.size() == 1)
		{	
			advFormsA.get(0).findSupportPoints(push, t, this.getVelocity(), contactsPointsA);
		}
		else
		{
			System.out.println("Error collision");
		}
		
		return contactsPointsA;
	}

	
	public static void updateDamping(float dt)
	{
		damping_pow = (float) Math.pow(damping, dt);
		dampingRotation_pow = (float) Math.pow(dampingRotation, dt);
	}
}