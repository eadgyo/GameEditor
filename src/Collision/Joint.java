package Collision;

import java.awt.Graphics;
import java.text.Normalizer.Form;

import Addons.AdvForm;
import Addons.Entity;
import Maths.Vector2D;

public class Joint
{
	private Entity entities[];
	private Vector2D points[];
	private float sep;
	private float restitution;
	private float error;
	private float length;
	
	public Joint()
	{
		entities = new Entity[2];
		points = new Vector2D[2];
		sep = 1f;
		restitution = 0f;
		length = 0f;
		error = 0.001f;
	}
	
	public void setSep(float sep)
	{
		this.sep = sep;
	}
	public void setRestitution(float restitution)
	{
		this.restitution = restitution;
	}
	public void setError(float error)
	{
		this.error = error;
	}
	public void setLength(float length)
	{
		this.length = length;
	}
	
	
	public void createJointLocal(Entity entities[], Vector2D points[])
	{
		this.entities[0] = entities[0];
		this.entities[1] = entities[1];
		this.setPointsLocal(points);
	}
	public void createJointWorld(Entity entities[], Vector2D points[])
	{
		this.entities[0] = entities[0];
		this.entities[1] = entities[1];
		this.setPointsWorld(points);
	}
	public void setPointsLocal(Vector2D points[])
	{
		this.points[0] = points[0].clone();
		this.points[1] = points[1].clone();
	}
	public void setPointsWorld(Vector2D points[])
	{
		AdvForm a = entities[0].getAdvFormsCompute().get(0);
		AdvForm b = entities[1].getAdvFormsCompute().get(0);
		this.points[0] = a.transformWorldToLocal(points[0]);
		this.points[1] = b.transformWorldToLocal(points[1]);		
	}
	public Vector2D[] getPointsWorld()
	{
		Vector2D pointsWorld[] = new Vector2D[2];
		AdvForm a = entities[0].getAdvFormsCompute().get(0);
		AdvForm b = entities[1].getAdvFormsCompute().get(0);
		pointsWorld[0] = a.transformLocalToWorld(points[0]);
		pointsWorld[1] = b.transformLocalToWorld(points[1]);
		return pointsWorld;
	}
	public void check()
	{
		
		Vector2D pointsWorld[] = new Vector2D[2];
		AdvForm a = entities[0].getAdvFormsCompute().get(0);
		AdvForm b = entities[1].getAdvFormsCompute().get(0);
		pointsWorld[0] = a.transformLocalToWorld(points[0]);
		pointsWorld[1] = b.transformLocalToWorld(points[1]);
		
		Vector2D vec = new Vector2D(pointsWorld[0], pointsWorld[1]);
		float dist = vec.normalize();
		
		if(dist + error > length)
		{
			resolveJoint(entities[0], entities[1], vec, dist - length, sep);
			/*entities[0].addForce(vec.multiply(10*(dist - length)), pointsWorld[0]);
			entities[1].addForce(vec.multiply(10*(length - dist)), pointsWorld[1]);*/
			resolveJointResponse(entities[0], entities[1], vec, 0f, restitution, pointsWorld[0], pointsWorld[1]);
		}
		else if(dist - error < length)
		{
			resolveJoint(entities[0], entities[1], vec.multiply(-1), length - dist, sep);
			/*entities[0].addForce(vec.multiply(10*(dist - length)), pointsWorld[0]);
			entities[1].addForce(vec.multiply(10*(length - dist)), pointsWorld[1]);*/
			resolveJointResponse(entities[0], entities[1], vec.multiply(-1f), 0f, restitution, pointsWorld[0], pointsWorld[1]);
		}
	}
	public static void resolveJoint(Entity A, Entity B, Vector2D normalColl, float dist, float sep)
	{
		float iMassA = A.getInverseMass();
		float iMassB = B.getInverseMass();
		float total = (iMassA + iMassB);
		
		Vector2D D = normalColl.multiply(dist*sep);
		
		if(iMassA == 0 && iMassB == 0)
			return;
		if(iMassA > 0)
		{
			Vector2D D0 =  D.multiply(iMassA / total);
			A.translate(D0);
		}
		if(iMassB > 0)
		{
			Vector2D D0 =  D.multiply(-iMassB / total);
			B.translate(D0);
		}
	}
	public static void resolveJointResponse(Entity A, Entity B, Vector2D normalColl, float coefFric, float coefRest, Vector2D CA, Vector2D CB)
	{
		/*
		 * Calcul de j
		 */
		 
		normalColl.normalize();
			
		Vector2D PA = A.getPos();
		Vector2D PB = B.getPos();
		Vector2D VA = A.getVelocity();
		Vector2D VB = B.getVelocity();
		float rotA = A.getRotation();
		float rotB = B.getRotation();
		
		//Calcul en fonction du temps
		Vector2D QA = PA;
		Vector2D QB = PB;
		Vector2D rAP = (CA.sub(QA));  
		Vector2D rBP = (CB.sub(QB));
		Vector2D TA = rAP.getPerpendicular();
		Vector2D TB = rBP.getPerpendicular();
		Vector2D VPA = VA.sub(TA.multiply(-rotA));
		Vector2D VPB = VB.sub(TB.multiply(-rotB));
		
		Vector2D relVel = VPB.sub(VPA);
		float vn = relVel.scalarProduct(normalColl);
		Vector2D Vn = normalColl.multiply(vn);
		Vector2D Vt = relVel.sub(Vn);
		
		if(vn > 0.0f)
		{
			return;
		}
		float vt = Vt.normalize();
		
		Vector2D J;
		float t0 = (rAP.crossProductZ(normalColl))*(rAP.crossProductZ(normalColl))*A.getInverseInertia();
		float t1 = (rBP.crossProductZ(normalColl))*(rBP.crossProductZ(normalColl))*B.getInverseInertia();
		float m = A.getInverseMass() + B.getInverseMass();
		
		float denom = m + t0 + t1;
		float jn = vn/denom;
		J = normalColl.multiply(-(1f + coefRest) * jn);
		
		Vector2D VA1 = VA.add(J.multiply(-A.getInverseMass()));
		Vector2D VB1 = VB.add(J.multiply(B.getInverseMass()));
		A.setVelocity(VA1);
		B.setVelocity(VB1);
		
		//Angular Responce
		float rotA1 = rotA + -A.getInverseInertia()*rAP.crossProductZ(J);
		float rotB1 = rotB + B.getInverseInertia()*rBP.crossProductZ(J);
		A.setRotation(rotA1);
		B.setRotation(rotB1);
	}
	
	public void draw(Graphics g)
	{
		Vector2D pointsWorld[] = this.getPointsWorld();
		g.drawLine((int) (pointsWorld[0].x), (int) (pointsWorld[0].y), (int) (pointsWorld[1].x), (int) (pointsWorld[1].y));
	}
	public void draw(Graphics g, Vector2D vec)
	{
		Vector2D pointsWorld[] = this.getPointsWorld();
		g.drawLine((int) (pointsWorld[0].x + vec.x), (int) (pointsWorld[0].y + vec.y), (int) (pointsWorld[1].x + vec.x), (int) (pointsWorld[1].y + vec.y));
	}
	public void draw(Graphics g, Vector2D vec, float scale)
	{
		Vector2D pointsWorld[] = this.getPointsWorld();
		pointsWorld[0].scale(scale, new Vector2D());
		pointsWorld[1].scale(scale, new Vector2D());
		g.drawLine((int) (pointsWorld[0].x + vec.x), (int) (pointsWorld[0].y + vec.y), (int) (pointsWorld[1].x + vec.x), (int) (pointsWorld[1].y + vec.y));
	}
}
