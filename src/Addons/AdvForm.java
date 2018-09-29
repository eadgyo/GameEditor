package Addons;

import java.util.ArrayList;

import Maths.Form;
import Maths.Vector2D;
import Maths.Vector2D;

public class AdvForm extends Form
{
	public final static long serialVersionUID = 5685600427694131355L;
	
	private CollisionListener caller;
	private boolean isWeapon, isHavingLife, isBlocking, isAction, isLooping;
	private int life;
	private Vector2D weaponPos;
	private Vector2D weaponVec;
	
	public AdvForm()
	{
		super();
		
		setWeapon(false);
		setHavingLife(false);
		setBlocking(false);
		setAction(false);
		setLooping(false);
		
		weaponVec = new Vector2D();
		weaponPos = new Vector2D();
		
		this.life = 100;
		caller = null;
	}
	public AdvForm(Form form)
	{
		super(form);
		
		setWeapon(false);
		setHavingLife(false);
		setBlocking(false);
		setAction(false);
		setLooping(false);
		
		weaponVec = new Vector2D();
		weaponPos = new Vector2D();
		
		this.life = 100;
		caller = null;
	}
	public AdvForm clone()
	{
		AdvForm advForm = new AdvForm(this);

		advForm.setWeapon(this.isWeapon);
		advForm.setHavingLife(this.isHavingLife);
		advForm.setBlocking(this.isBlocking);
		advForm.setAction(this.isAction);
		advForm.setLooping(this.isLooping);
		
		advForm.setLife(this.life);
	
		advForm.setWeaponPos(this.weaponPos);
		advForm.setWeaponVec(this.weaponVec);
		advForm.setCaller(this.caller);
		
		return advForm;
	}
	
	public boolean isLooping()
	{
		return isLooping;
	}
	public void setLooping(boolean isLooping)
	{
		this.isLooping = isLooping;
	}
	public boolean isWeapon() 
	{
		return isWeapon;
	}
	public void setWeapon(boolean isWeapon) 
	{
		this.isWeapon = isWeapon;
	}
	public boolean isBlocking() 
	{
		return isBlocking;
	}
	public void setBlocking(boolean isBlocking) 
	{
		this.isBlocking = isBlocking;
	}
	public boolean isHavingLife() 
	{
		return isHavingLife;
	}
	public void setHavingLife(boolean isHavingLife) 
	{
		this.isHavingLife = isHavingLife;
	}
	public boolean isAction() 
	{
		return isAction;
	}
	public void setAction(boolean isAction) 
	{
		this.isAction = isAction;
	}
	public int getLife() 
	{
		
		return life;
	}
	public void setLife(int life) 
	{
		
		this.life = life;
	}
	public Vector2D getWeaponPos() 
	{
		
		return weaponPos;
	}
	public void setWeaponPos(Vector2D weaponPos) 
	{
		this.weaponPos.set(weaponPos.x, weaponPos.y);
	}
	public Vector2D getWeaponVec() 
	{
		
		return weaponVec;
	}
	public void setWeaponVec(Vector2D weaponVec) 
	{
		this.weaponVec.set(weaponVec.x, weaponVec.y);
	}

	private CollisionListener getCaller()
	{
		return caller;
	}
	private void setCaller(CollisionListener caller)
	{
		this.caller = caller;
	}
	private boolean testCollision(Form form)
	{
		if(this.collisionSat(form))
		{
			if(this.caller != null)
			{
				caller.collisionPerformed(this);
			}
			return true;
		}
		return false;
	}
}
