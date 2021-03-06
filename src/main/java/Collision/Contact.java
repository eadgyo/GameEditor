package Collision;

import Addons.Entity;
import Maths.FloatA;
import Maths.Vector2D;

import java.util.ArrayList;

public class Contact
{
	public static boolean testCollision(Entity A, Entity B, float dt)
	{
		Vector2D push = new Vector2D();

		Vector2D posA = A.getPos();
		Vector2D posB = B.getPos();

		FloatA t = new FloatA();
		t.v = dt;
		if (posA.x == 150 && posA.y == 413.37354f)
		{
			int ad = 10;

		}

		if(A.isColliding(B, push, t))
		{
			ArrayList<Vector2D> CA = new ArrayList<Vector2D>();
			ArrayList<Vector2D> CB = new ArrayList<Vector2D>();
			
			//Find contacts
			ArrayList<Vector2D> contactsA = A.findContacts(push, t.v);
			ArrayList<Vector2D> contactsB = B.findContacts(push.multiply(-1), t.v);
			
			if(!analyseContacts(A, B, contactsA, contactsB, CA, CB))
				return false;

			resolveCollisionResponse(A, B, push.multiply(-1), 0.5f, 0.3f, CA, CB, t.v, dt);

			if(t.v < 0)
				resolveOverlap(A, B, CA, CB);

			return true;
		}
		
		return false;
	}
	public static void resolveOverlap(Entity A, Entity B, ArrayList<Vector2D> CA, ArrayList<Vector2D> CB)
	{
		for(int i=0; i<CA.size(); i++)
		{
			resolveOverlap(A, B, CA.get(i), CB.get(i), 1f);
		}
	}
	public static void resolveOverlap(Entity A, Entity B, Vector2D CA, Vector2D CB, float sep)
	{
		float iMassA = A.getInverseMass();
		float iMassB = B.getInverseMass();
		float total = (iMassA + iMassB);
		
		Vector2D D = new Vector2D(CA, CB);
		D.selfMultiply(sep);
		
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
	
	public static void resolveCollisionResponse(Entity A, Entity B, Vector2D normalColl, float coefFric, float coefRest, ArrayList<Vector2D> CA, ArrayList<Vector2D> CB, float t, float dt)
	{
		assert(CA.size() == CB.size());
		for(int i=0; i<CA.size(); i++)
		{
			resolveCollisionResponse(A, B, normalColl, coefFric, coefRest, CA.get(i), CB.get(i), t, dt);
		}
	}
	
	 /* Tout est exprimé dans le repère général
	 * 
	 * 2 entités: A et B
	 * normalColl == la normale au coté en collision
	 * coefFric == coefficient de friction
	 * coefRest == coefficient de restitution
	 * CA == point de collision de l'objet A
	 * CB == point de collision de l'objet B
	 */
	 
	public static void resolveCollisionResponse(Entity A, Entity B, Vector2D normalColl, float coefFric, float coefRest, Vector2D CA, Vector2D CB, float t, float dt)
	{
		/*
		 * Calcul de j
		 */
		 
		normalColl.normalize();
		float tColl = (t>0f)? t:0f;
		
		Vector2D PA = A.getPos();
		Vector2D PB = B.getPos();
		Vector2D VA = A.getVelocity();
		Vector2D VB = B.getVelocity();
		float rotA = A.getRotation();
		float rotB = B.getRotation();
		
		//Calcul en fonction du temps
		Vector2D QA = PA.add(VA.multiply(tColl));
		Vector2D QB = PB.add(VB.multiply(tColl));
		Vector2D rAP = (CA.sub(QA));  
		Vector2D rBP = (CB.sub(QB));
		Vector2D TA = rAP.getPerpendicular();
		Vector2D TB = rBP.getPerpendicular();
		Vector2D VPA = VA.sub(TA.multiply(-rotA));
		Vector2D VPB = VB.sub(TB.multiply(-rotB));
		
		Vector2D relVel = VPB.sub(VPA);
		FloatA rest = new FloatA();
		FloatA vAcc = new FloatA();
		rest.v = coefRest;
		
		float vn = desiredVel(relVel, normalColl, vAcc, A, B, rest, dt);
		Vector2D Vn = normalColl.multiply(vn);
		
		Vector2D N = normalColl.getPerpendicular();
		float vt = N.scalarProduct(relVel);
		Vector2D Vt = N.multiply((vt< 0)? -1:1);
		
		/*Vector2D Vt = relVel.sub(Vn);
		float vt = Vt.normalize();*/
		
		if(vn > 0.0f)
		{
			//Separating collision
			//Les deux entités s'éloignent
			return;
		}

		
		Vector2D J;
		float t0 = (rAP.crossProductZ(normalColl))*(rAP.crossProductZ(normalColl))*A.getInverseInertia();
		float t1 = (rBP.crossProductZ(normalColl))*(rBP.crossProductZ(normalColl))*B.getInverseInertia();
		float m = A.getInverseMass() + B.getInverseMass();
		
		float denom = m + t0 + t1;
		float normalJn = (vn)/denom;
		float jn = (vn - vAcc.v)/denom;
		Vector2D normalJ = normalColl.multiply(-(1f + rest.v) * normalJn);
		J = normalColl.multiply(vAcc.v+((1f + rest.v)*-jn));

		//Dynamic friction
		Vector2D Vta = Vt.multiply((coefFric*jn));
		J.selfAdd(Vt.multiply((coefFric*jn)));
	

		Vector2D VA1 = VA.add(J.multiply(-A.getInverseMass()));
		Vector2D VB1 = VB.add(J.multiply(B.getInverseMass()));
		A.setVelocity(VA1);
		B.setVelocity(VB1);
		
		//Angular Responce
		float rotA1 = rotA + -A.getInverseInertia()*rAP.crossProductZ(J);
		float rotB1 = rotB + B.getInverseInertia()*rBP.crossProductZ(J);
		A.setRotation(rotA1);
		B.setRotation(rotB1);
		
		//Static friction
		if(coefFric > 0.0f && vt > vn*coefFric)
		{
			Vector2D Nfriction = Vt.multiply(-1);
			float fCoS = 0.3f;
			resolveCollisionResponse(A, B, normalColl, 0.0f, fCoS, CA, CB, 0, dt);
		}
	
		
	}
	public static float desiredVel(Vector2D relVel, Vector2D normalColl, FloatA vAcc, Entity A, Entity B, FloatA restitution, float dt)
	{
		vAcc.v = -A.getLastAcceleration().scalarProduct(normalColl)*dt;
		vAcc.v += B.getLastAcceleration().scalarProduct(normalColl)*dt;
		
		float vn = relVel.scalarProduct(normalColl);
		
		if(Math.abs(vn) < 0.25f)
		{
			restitution.v = 0;
		}
		
		return vn;
	}
	
	public static boolean analyseContacts(Entity A, Entity B, ArrayList<Vector2D> contactsA, ArrayList<Vector2D> contactsB, ArrayList<Vector2D> CA, ArrayList<Vector2D> CB)
	{
		if(contactsA.size() == 0 || contactsB.size() == 0)
			return false;
		if(contactsA.size() == 1 && contactsB.size() == 2)
		{
			Vector2D edge = new Vector2D(contactsB.get(0), contactsB.get(1));
			
			Vector2D PA = contactsA.get(0);
			Vector2D PB1 = contactsB.get(0);
			Vector2D PB2 = contactsB.get(1);
			
			CA.add(contactsA.get(0));
			
			Vector2D projection = new Vector2D();
			if(!projectOnSegment(PA, PB1, PB2, projection))
				return false;
			CB.add(projection);
		}
		else if(contactsA.size() == 2 && contactsB.size() == 1)
		{
			Vector2D edge = new Vector2D(contactsA.get(0), contactsA.get(1));
			
			Vector2D PB = contactsB.get(0);
			Vector2D PA1 = contactsA.get(0);
			Vector2D PA2 = contactsA.get(1);
			
			CB.add(contactsB.get(0));
			
			Vector2D projection = new Vector2D();
			if(!projectOnSegment(PB, PA1, PA2, projection))
				return false;
			CA.add(projection);
		}
		else if(contactsA.size() == 2 && contactsB.size() == 2)
		{
			Vector2D edgeA = new Vector2D(contactsA.get(0), contactsA.get(1));
			Vector2D edgeB = new Vector2D(contactsB.get(0), contactsB.get(1));
			
			if(!handleEdgeToEdge(edgeA, contactsA, contactsB, CA, CB))
				return false;
		}
		return true;
	}
	
	public static boolean projectOnSegment(Vector2D PA, Vector2D PB1, Vector2D PB2, Vector2D project)
	{
		Vector2D edgeB = new Vector2D(PB1, PB2);
		Vector2D projection = new Vector2D(PB1, PA);
		float magnitude = edgeB.normalize();
		float fProjection = edgeB.scalarProduct(projection);
		
		if(fProjection < 0)
			fProjection = 0;
		if(fProjection > magnitude)
			fProjection = magnitude;
		project.set(edgeB.multiply(fProjection).add(PB1));
		return true;
		
	}
	public static boolean handleEdgeToEdge(Vector2D edge, ArrayList<Vector2D> contactsA, ArrayList<Vector2D> contactsB, ArrayList<Vector2D> CA, ArrayList<Vector2D> CB)
	{
		float min0 = 0;
		float max0 = edge.getSqMagnitude();
		
		if(min0 > max0)
		{
			float temp = max0;
			max0 = min0;
			min0 = temp;
			
			Vector2D tempA = contactsA.get(0);
			contactsA.set(0, contactsA.get(1));
			contactsA.set(1, tempA);
		}
		
		float min1 = (contactsB.get(0).sub(contactsA.get(0))).scalarProduct(edge);
		float max1 = (contactsB.get(1).sub(contactsA.get(0))).scalarProduct(edge);
		
		if(min1 > max1)
		{
			float temp = max0;
			max0 = min0;
			min0 = temp;
			
			Vector2D tempB = contactsB.get(0);
			contactsB.set(0, contactsB.get(1));
			contactsB.set(1, tempB);
		}
		
		
		if(min0 > max1 || min1 > max0)
			return false;
		
		//On est sûr qu'il y a une collision
		if(min0 > min1)
		{
			//Le point en collision est le point contactsA_0
			//Projection du point sur le coté opposé
			Vector2D projection = new Vector2D();
			if(projectOnSegment(contactsA.get(0), contactsB.get(0), contactsB.get(1), projection))
			{
				//On ajoute le point projeter et sa projection
				CA.add(contactsA.get(0));
				CB.add(projection);
			}
		}
		else
		{
			//Le point en collision est le point contactsB_0
			//Projection du point sur le coté opposé
			Vector2D projection = new Vector2D();
			if(projectOnSegment(contactsB.get(0), contactsA.get(0), contactsA.get(1), projection))
			{
				//On ajoute le point projeter et sa projection
				CB.add(contactsB.get(0));
				CA.add(projection);
			}
		}
		
		if(max0 != min0 && max1 != min1)
		{
			if(max0 < max1)
			{
				//Le point en collision est le point contactsA_1
				//Projection du point sur le coté opposé
				Vector2D projection = new Vector2D();
				if(projectOnSegment(contactsA.get(1), contactsB.get(0), contactsB.get(1), projection))
				{
					//On ajoute le point projeter et sa projection
					CA.add(contactsA.get(1));
					CB.add(projection);
				}
			}
			else
			{
				//Le point en collision est le point contactsB_1
				//Projection du point sur le coté opposé
				Vector2D projection = new Vector2D();
				if(projectOnSegment(contactsB.get(1), contactsA.get(0), contactsA.get(1), projection))
				{
					//On ajoute le point projeter et sa projection
					CB.add(contactsB.get(1));
					CA.add(projection);
				}
			}
		}
		return true;
	}
}