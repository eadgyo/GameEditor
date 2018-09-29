import java.util.ArrayList;

import Maths.Matrix2;
import Maths.Vector2D;

/*import java.util.ArrayList;

import Maths.FloatA;
import Maths.Form;
import Maths.Matrix2;
import Maths.Vector2D;


	public boolean collisionSat(Form form, Vector2D VA, Vector2D VB, Vector2D push, FloatA t)
	{
		if(convexForms.size() == 0)
			this.updateConvexForms();
		if(form.getConvexForms().size() == 0)
			form.updateConvexForms();
		
		for(int i = 0; i<convexForms.size(); i++)
		{
			if(convexForms.get(i).collisionSatA(form, VA, VB, push, t))
				return true;
		}
		return false;
	}
	public boolean collisionSatA(Form B, Vector2D VA, Vector2D VB, Vector2D push, FloatA t)
	{
		Form A = this;
		Matrix2 OA = A.getOrientation().convertMatrix2();
		Matrix2 OB = B.getOrientation().convertMatrix2();
		Vector2D PA = A.getCenter();
		Vector2D PB = B.getCenter();
		
		Matrix2 OBi = OB.inverse();
		
		//On convertit tout dans un repère local (ici B)
		Matrix2 orient = OA.multiply(OBi);
		Matrix2 orientI = orient.inverse();
		Vector2D offset = OBi.multiply(PA.sub(PB));
		Vector2D relV = OBi.multiply(VA.sub(VB));
		
		ArrayList<Vector2D> axisA = A.getVectorsSatLocal();
		ArrayList<Vector2D> axisB = B.getVectorsSatLocal();
		ArrayList<Vector2D> vecA = A.getPointsLocal();
		ArrayList<Vector2D> vecB = B.getPointsLocal();
		
		ArrayList<Vector2D> axis = new ArrayList<Vector2D>();
		ArrayList<Float> tAxis = new ArrayList<Float>();
		
		float fVel = relV.scalarProduct(relV);
		if(fVel > 0.00001f)
		{
			axis.add(relV.getPerpendicular());
			if(!intervalIntersect(vecA, vecB, axis.get(axis.size() - 1),
								  offset, relV, orientI, tAxis, t))
				return false;
		}
		
		for(int i=0; i<axisA.size(); i++)
		{
			axis.add(orient.multiply(axisA.get(i)));
			if(!intervalIntersect(vecA, vecB, axis.get(axis.size() - 1),
					  offset, relV, orientI, tAxis, t))
				return false;
		}
		
		for(int i=0; i<axisB.size(); i++)
		{
			axis.add(axisB.get(i));
			if(!intervalIntersect(vecA, vecB, axis.get(axis.size() - 1),
					  offset, relV, orientI, tAxis, t))
				return false;
		}
		
		if(!findPushVector(axis, tAxis, push, t))
			return false;
		
		//On s'assurre que les objets s'éloignent l'un de l'autre
		if(offset.scalarProduct(push) < 0)
			push.selfMultiply(-1);
		
		push.set(OB.multiply(push));
		
		return true;
	}
	public boolean intervalIntersect(ArrayList<Vector2D> vecA, ArrayList<Vector2D> vecB,
			Vector2D axis, Vector2D offset, Vector2D vel, Matrix2 orientI,
			ArrayList<Float> tAxis, FloatA t)
	{
		Vector2D minMax0 = getInterval(vecA, orientI.multiply(axis));
		Vector2D minMax1 = getInterval(vecB, axis);
		float min0 = minMax0.x;
		float max0 = minMax0.y;
		float min1 = minMax1.x;
		float max1 = minMax1.y;
		
		//On ajoute le décalage pour les deux repères
		float h = offset.scalarProduct(axis);
		min0 += h;
		max0 += h;
		//On a deux cas: intersection ou non intersection
		
		//pas d'intersection si maxa < minb ou maxb < mina
		float d0 = min0 - max1;
		float d1 = min1 - max0;
		if(d0 > 0 || d1 > 0)
		{
			float fVel = vel.scalarProduct(axis);
			if(Math.abs(fVel) < 0.00000001)
				return false;
			
			//Calcul du temps pour la prochaine intersection avec la vitesse
			float t0 =-d0/fVel;
			float t1 = d1/fVel;
			
			if(t0 > t1) {float temp = t0; t0 = t1; t1 = temp;}
			float l_tAxis = (t0 > 0)? t0:t1;
			
			if(l_tAxis < 0 || l_tAxis > t.v)
				return false;
			
			tAxis.add(l_tAxis);
			
			return true;
			//return false;
		}
		else //intersection
		{
			//On récupère le plus petit écart (valeur absolue)
			tAxis.add((d0 > d1)? d0:d1);
			return true;
		}
		
	}
	public Vector2D getInterval(ArrayList<Vector2D> vectors, Vector2D axis)
	{
		float min, max;
		min = max = (vectors.get(0).scalarProduct(axis));
		
		for(int i=1; i<vectors.size(); i++)
		{
			float f = vectors.get(i).scalarProduct(axis);
			if(f < min)
			{
				min = f;
			}
			else if(f > max)
			{
				max = f;
			}
		}
		return new Vector2D(min, max);
	}
	public boolean findPushVector(ArrayList<Vector2D> axis, ArrayList<Float> tAxis, Vector2D push, FloatA t)
	{
		//On cherche le plus petit vecteur de poussé
		//Soit il y aura une collision tAxis > 0
		//Sinon il y a une collision tAxis < 0
		int mini = -1;
		t.v = 0f;
		for(int i=0; i<axis.size(); i++)
		{
			if(tAxis.get(i) > 0)
			{
				if(tAxis.get(i) > t.v)
				{
					mini = i;
					t.v = tAxis.get(i);
					push.set(axis.get(i));
					push.normalize();
				}
			}
		}
		
		//On a trouvé une intersection future
		if(mini != -1)
			return true;
		
		for(int i=0; i<axis.size(); i++)
		{
			Float magnitude = axis.get(i).normalize();
			tAxis.set(i, tAxis.get(i).floatValue()/magnitude);
			if(tAxis.get(i) > t.v || mini == -1)
			{
				mini = i;
				t.v = tAxis.get(i);
				push.set(axis.get(i));
			}
		}
		
		assert(mini != -1);
		return true;
	}
		public void findSupportPoints(Vector2D push, float t, Vector2D VA, ArrayList<Vector2D> S)
	{
		assert(points.size() != 0);
		
		//Conversion
		Matrix2 orientation = this.orientation.convertMatrix2();
		Vector2D pushOA = orientation.inverse().multiply(push);
		ArrayList<Float> scalar = new ArrayList<Float>(points.size());
		float dmin;
		
		//On cherche le point minimum par rapport au vecteur
		scalar.add(points.get(0).scalarProduct(pushOA));
		dmin = scalar.get(0);
		
		for(int i=1; i<points.size(); i++)
		{
			scalar.add(points.get(i).scalarProduct(pushOA));
			
			if(scalar.get(i) < dmin)
			{
				dmin = scalar.get(i);
			}
		}
		
		float threshold = 0.001f;
		ArrayList<Float> s = new ArrayList<Float>(2);
		Vector2D perp = pushOA.getPerpendicular();
		
		//On regarde s'il y a deux points a peu près au meme niveau
		for(int i=0; i<points.size(); i++)
		{
			if(scalar.get(i) < dmin + threshold)
			{
				Vector2D contact = transform(points.get(i), this.getCenter(), VA, orientation, t);
				float fScalar = contact.scalarProduct(perp);
				
				//On prend les deux points les plus éloignés
				if(s.size() < 2)
				{
					s.add(fScalar);
					S.add(contact);
					
					if(s.size() > 1)
					{
						if(s.get(0) > s.get(1))
						{
							float temp = s.get(0);
							s.set(0, s.get(1));
							s.set(1, temp);
							
							Vector2D tempV = S.get(0);
							S.set(0, S.get(1));
							S.set(1, tempV);
						}
					}
				}
				else
				{
					if(fScalar < s.get(0)) //< min
					{
						s.set(0, fScalar);
						S.set(0, contact);
					}
					else if(fScalar > s.get(1)) //> max
					{
						s.set(1, fScalar);
						S.set(1, contact);
					}
				}
			}
		}
	}
	public Vector2D transform(Vector2D vertex, Vector2D p, Vector2D v, Matrix2 orientation, float t)
	{
		Vector2D T = p.add(orientation.multiply(vertex));
		
		//Si la collision est future
		if(t > 0)
			T.add(v.multiply(t));
		
		return T;
	}
	public Vector2D handleEdgePoint(Vector2D PA, Vector2D PB1, Vector2D PB2)
	{
		Vector2D edgeB = new Vector2D(PB1, PB2);
		Vector2D projection = new Vector2D(PB1, PB2);
		float fProjection = edgeB.scalarProduct(projection);
		
		return edgeB.multiply(fProjection);
	}
	*/
public class save
{

}
