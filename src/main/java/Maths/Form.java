package Maths;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Form implements java.io.Serializable
{
	//update
	protected float omega, scale;
	protected boolean flipH, flipV;
	protected Matrix3 orientation;
	
	protected float inverseInertia;
	protected float surface;
	
	protected ArrayList<Vector2D> points;
	//premier point, les autres == vectors
	private class AxesSat
	{
		//On conserve les axis et la distance
		ArrayList<Vector2D> axes = new ArrayList<Vector2D>();
		ArrayList<Float> tAxes = new ArrayList<Float>();
		
		//Si la collision est future on conserve le temps avant la collision et l'axe
		ArrayList<Vector2D> axesT = new ArrayList<Vector2D>();
		ArrayList<Float> tAxesT = new ArrayList<Float>();
	}
	
	private ArrayList<Form> convexForms;

	public Form(int size)
	{
		convexForms = new ArrayList<Form>();
		
		points = new ArrayList<Vector2D>(size);
		for(int i=0; i<size; i++)
		{
			Vector2D p = new Vector2D();
			points.add(p);
		}
		
		omega = 0;
		scale = 1f;
		flipH = false;
		flipV = false;
		inverseInertia = 0;
		surface = 0;
		
		orientation = Matrix3.orientation(omega, scale, flipH, flipV, new Vector2D());
	}
	public Form()
	{
		convexForms = new ArrayList<Form>();
		
		points = new ArrayList<Vector2D>();
		
		omega = 0;
		scale = 1f;
		flipH = false;
		flipV = false;
		inverseInertia = 0;
		surface = 0;
		
		orientation = Matrix3.orientation(omega, scale, flipH, flipV, new Vector2D());
	}
	public Form(Form form)
	{
		convexForms = new ArrayList<Form>();
		for(int i=0; i<form.getConvexForms().size(); i++)
		{
			convexForms.add(form.getConvexForms().get(i).clone());
		}
		
		points = new ArrayList<Vector2D>(form.size());
		
		for(int i=0; i<form.size(); i++)
		{
			points.add(form.getLocal(i).clone());
		}
		
		this.omega = form.getOmega();
		this.scale = form.getScale();
		this.flipH = form.getFlipH();
		this.flipV = form.getFlipV();
		this.inverseInertia = form.getInverseInertia();
		this.surface = form.getSurface();
		
		orientation = Matrix3.orientation(omega, scale, flipH, flipV, form.getCenter());
	}
	public Form clone()
	{
		Form form = new Form(this);
		return form;
	}
	public void resetTransformations()
	{
		this.setRadians(0);
		this.setScale(1f);
		this.setFlipH(false);
		this.setFlipV(false);
		
		orientation.data[0] = 1f;
		orientation.data[1] = 0;

		orientation.data[3] = 0;
		orientation.data[4] = 1f;
	}
	public void clearTransformations()
	{
		omega = 0;
		scale = 1f;
		flipH = false;
		flipV = false;
		
		orientation.data[0] = 1f;
		orientation.data[1] = 0;

		orientation.data[3] = 0;
		orientation.data[4] = 1f;
	}
	public void setInit(Form form)
	{
		this.omega = form.getOmega();
		this.scale = form.getScale();
		this.flipH = form.getFlipH();
		this.flipV = form.getFlipV();
	}
	
	public Matrix3 getOrientation()
	{
		return orientation;
	}
	public Matrix3 getOrientationInverse()
	{
		return orientation.inverse();
	}
	public float getOmega()
	{
		return omega;
	}
	public float getScale()
	{
		return scale;
	}
	public boolean getFlipH()
	{
		return flipH;
	}
	public boolean getFlipV()
	{
		return flipV;
	}
	public Vector2D getCentroidWorld()
	{
		Vector2D center = this.getCentroidLocal();
		Vector2D centerW = orientation.multiply(center);
		return centerW;
	}
	public Vector2D getCentroidLocal()
	{
		if(points.size() == 0)
			return new Vector2D();
		else if(points.size() == 1)
			return this.getLocal();
		else if(points.size() == 2)
		{
			ArrayList<Vector2D> points = this.getPointsLocal();
			return new Vector2D((points.get(0).x + points.get(1).x)/2,
								(points.get(0).y + points.get(1).y)/2);
		}
		Vector2D center = new Vector2D();
		float x0 = 0, y0 = 0, x1 = 0, y1 = 0, signedArea = 0, a = 0;
		ArrayList<Vector2D> points = this.getPointsLocal();
		for(int j=points.size()-1, i=0; i<points.size(); j=i, i++)
		{
			x0 = points.get(i).x;
			y0 = points.get(i).y;
			x1 = points.get(j).x;
			y1 = points.get(j).y;
			a = x0*y1 - x1*y0;
			signedArea += a;
			center.x += (x0 + x1)*a;
			center.y += (y0 + y1)*a;
		}
		
		signedArea *= 0.5;
		center.x /= (6f*signedArea);
		center.y /= (6f*signedArea);
		
		return center;
	}
	public void updateCenter()
	{
		Vector2D newCenter = this.getCentroidWorld();
		if(Float.isNaN(newCenter.x) || Float.isNaN(newCenter.y))
		{
			this.calculateInertia();
			this.calculateSurface();
			return;
		}
		
		Matrix3 lastCoor = this.orientation; //this.center
		Matrix3 newCoor = this.orientation.clone();
		newCoor.setPos(newCenter);
		Matrix3 inverse = newCoor.inverse();
		Matrix3 result = inverse.multiply(lastCoor);
				
		
		for(int i=0; i<points.size(); i++)
		{
			/*
			result équivaut à ces opérations
			
			Vector2D p = points.get(i);
			Vector2D pW = orientation.multiply(p);
			Vector2D pN = inverse.multiply(pW);
			
			*/
			Vector2D vec = result.multiply(points.get(i));
			points.get(i).set(result.multiply(points.get(i)));
		}
		orientation.setPos(newCenter);
		
		//update inertia
		this.calculateInertia();
		this.calculateSurface();
	}
	public void updateOrientation()
	{
		orientation.setOrientation(omega, scale, flipH, flipV);
	}
	
	//Add/Remove/size
	public void setCenter(Vector2D center)
	{
		Vector2D vec = new Vector2D(this.getCenter(), center);
		this.translate(vec);
	}
	public void setPoint(int n, Vector2D p)
	{
		assert(n < points.size());
		points.set(n, orientation.inverse().multiply(p));
		convexForms.clear();
		updateCenter();
	}
	public void addPointFree(Vector2D p)
	{
		//Pas d'actualisation du centre
		if(orientation.getDeterminant() != 0f)
			points.add(orientation.inverse().multiply(p));
		else
			points.add(new Vector2D(this.getCenter(), p));
		convexForms.clear();
	}
	public void addPoint(Vector2D p)
	{
		//Pas d'actualisation du centre
		if(orientation.getDeterminant() != 0f)
			points.add(orientation.inverse().multiply(p));
		else
			points.add(new Vector2D(this.getCenter(), p));
		convexForms.clear();
		updateCenter();
	}
	public void removePoint(int i)
	{
		Vector2D p = points.remove(i);
		convexForms.clear();
		updateCenter();
	}
	public void removeLast()
	{
		this.removePoint(points.size() - 1);
	}
	public int size() {return points.size();}

	
	//getter
	public float getLocalX()
	{
		return points.get(0).x;
	}
	public float getLocalY()
	{
		return points.get(0).y;
	}
	public Vector2D getLocal()
	{
		return points.get(0);
	}
	public float getLocalX(int n)
	{
		return points.get(n).x;
	}
	public float getLocalY(int n)
	{
		return points.get(n).y;
	}
	public Vector2D getLocal(int n)
	{
		return points.get(n);
	}
	
	public float getX() 
	{
		return this.getX(0);
	}
	public float getY() 
	{
		return this.getY(0);
	}
	public float getX(int n) 
	{
		assert(n<points.size());
		Vector2D local = points.get(n);
		float x = orientation.multiplyX(local);
		return x;
	}
	public float getY(int n) 
	{
		assert(n<points.size());
		Vector2D local = points.get(n);
		float y = orientation.multiplyY(local);
		return y;
	}
	public Vector2D get() 
	{
		return get(0);
	}
	public Vector2D get(int n) 
	{
		assert(n<points.size());
		//change local to world
		Vector2D local = points.get(n);
		Vector2D world = orientation.multiply(local);
		return world;
	}
	public float getCenterX()
	{
		return orientation.getX();
	}
	public float getCenterY()
	{
		return orientation.getY();
	}
	public Vector2D getCenter() 
	{
		return orientation.getPos();
	}
	
	public int[] getXIntArray()
	{
		int[] l_X = new int[points.size()];
		for(int i=0; i<points.size(); i++)
		{
			l_X[i] = (int) this.getX(i);
		}
		return l_X;
	}
	public int[] getYIntArray()
	{
		int[] l_Y = new int[points.size()];
		for(int i=0; i<points.size(); i++)
		{
			l_Y[i] = (int) this.getY(i);
		}
		return l_Y;
	}
	
	public float getMinX()
	{
		float xMin = getX(0);
		for(int i=1; i<points.size(); i++)
		{
			float x = getX(i);
			if(x < xMin)
				xMin = x;
		}
		return xMin;
	}
	public float getMinY()
	{
		float yMin = getY(0);
		for(int i=1; i<points.size(); i++)
		{
			float y = getY(i);
			if(y < yMin)
				yMin = y;
		}
		return yMin;
	}
	public float getMaxX()
	{
		float xMax = getX(0);
		for(int i=1; i<points.size(); i++)
		{
			float x = getX(i);
			if(x > xMax)
				xMax = x;
		}
		return xMax;
	}
	public float getMaxY()
	{
		float yMax = getY(0);
		for(int i=1; i<points.size(); i++)
		{
			float y = getY(i);
			if(y > yMax)
				yMax = y;
		}
		return yMax;
	}
	
	public ArrayList<Vector2D> getVectorsSatLocal()
	{
		ArrayList<Vector2D> l_vectors = new ArrayList<Vector2D>(points.size());
		for(int j=points.size()-1, i=0; i<points.size(); j=i, i++)
		{
			Vector2D v = new Vector2D(points.get(j), points.get(i));
			l_vectors.add(v.getPerpendicular());
		}
		
		
		//On enleve les vectors colinéaires
		for(int i=0; i<l_vectors.size()-1; i++)
		{
			//Si on est en dessous de 2 vecteurs ca sert à rien de continuer,
			//On sait que ces 2 vecteurs (ou moins) ne sont pas colinéaires
			if(l_vectors.size() < 3)
				break;
			
			for(int j=i+1; j<l_vectors.size(); j++)
			{
				if(l_vectors.get(i).isColinear(l_vectors.get(j)))
				{
					l_vectors.remove(i);
					i--;
					break;
				}
			}
		}
		return l_vectors;
	}

	public ArrayList<Vector2D> getVectorsLocal()
	{
		ArrayList<Vector2D> l_vectors = new ArrayList<Vector2D>(points.size());
		for(int j=points.size()-1, i=0; i<points.size(); j=i, i++)
		{
			l_vectors.add(i, new Vector2D(points.get(j), points.get(i)));
		}
		return l_vectors;
	}
	public ArrayList<Vector2D> getVectorsWorld()
	{
		ArrayList<Vector2D> l_vectors = getVectorsLocal();
		for(int i=0; i<l_vectors.size(); i++)
		{
			l_vectors.get(i).set(orientation.multiply(l_vectors.get(i)));
		}
		return l_vectors;
	}
	
	public ArrayList<Vector2D> getPointsLocal()
	{
		return points;
	}
	public ArrayList<Vector2D> getPointsWorld()
	{
		if(orientation.getX() == 0 && orientation.getY() == 0 && omega == 0 && scale == 1f && !flipV && !flipH)
			return this.getPointsLocal();
		
		ArrayList<Vector2D> pointsW = new ArrayList<Vector2D>(points.size());
		for(int i=0; i<points.size(); i++)
		{
			pointsW.add(this.get(i));
		}
		return pointsW;
	}
	
	public Vector2D transformLocalToWorld(Vector2D point)
	{
		return orientation.multiply(point);
	}
	public Vector2D transformWorldToLocal(Vector2D point)
	{
		Matrix3 inverse = orientation.inverse();
		return inverse.multiply(point);
	}
	//Transformations
	public void translate(Vector2D v)
	{
		orientation.translate(v);
	}
	public void translateX(float x)
	{
		orientation.translateX(x);
	}
	public void translateY(float y)
	{
		orientation.translateY(y);
	}
	public void rotateDegrees(float omega, Vector2D center)
	{
		//omega = Extend.modulo(omega, 360f);
		rotateRadians((float) (omega*Math.PI)/180, center);
	}
	public void rotateRadians(float omega, Vector2D center)
	{
		this.omega += omega;
		orientation.rotateRadiansFree(omega, center);
		updateOrientation();
	}
	public void scale(float factor, Vector2D center)
	{
		scale *= factor;
		orientation.scale(factor, center);
	}
	public void flipH(Vector2D center)
	{
		this.flipH = !this.flipH;
		orientation.flipH(center);
	}
	public void flipV(Vector2D center)
	{
		this.flipV = !this.flipV;
		orientation.flipV(center);
	}
	
	public void setPos(Vector2D v)
	{
		orientation.setPos(v);
	}
	public void setRadians(float omega)
	{
		float d = omega - this.omega;
		this.omega = omega;
		orientation.rotateRadiansFree(d, new Vector2D());
		updateOrientation();
	}
	public void setScale(float scale)
	{
		float factor = scale/this.scale;
		float test = this.scale*factor;
		this.scale = scale;
		orientation.scale(factor);
	}
	public void setFlipH(boolean flipH)
	{
		if(this.flipH != flipH)
		{
			this.flipH = flipH;
			orientation.flipH();
		}
	}
	public void setFlipV(boolean flipV)
	{
		if(this.flipV != flipV)
		{
			this.flipV = flipV;
			orientation.flipV();
		}
	}
	
	//Mass Inertia
	public float calculateMass(float density)
	{
		if(points.size() == 0)
			return 0;
		else if(points.size() < 3)
			return 5.0f * density;
		
		float mass = 0f;
		
		for(int j=points.size()-1, i=0; i<points.size(); j=i, i++)
		{
			Vector2D P0 = points.get(j);
			Vector2D P1 = points.get(i);
			mass += (float) Math.abs(P0.crossProductZ(P1));
		}
		
		mass *= density*0.5f;
		
		return mass;
	}
	public void calculateInertia()
	{
		if(points.size() < 2)
			return;
		
		float denom = 0.0f;
		float numer = 0.0f;
		
		for(int j=points.size()-1, i=0; i<points.size(); j=i, i++)
		{
			Vector2D P0 = points.get(j);
			Vector2D P1 = points.get(i);
			
			float a = (float) Math.abs(P0.crossProductZ(P1));
			float b = (P0.getSqMagnitude() + P0.scalarProduct(P1) + P1.getSqMagnitude());
			
			denom += (a*b);
			numer += a;
		}
		
		this.inverseInertia = 1/((1/6f)*(denom/numer));
	}
	public float getInverseInertia()
	{
		return inverseInertia;
	}
	public float getInverseInertia(float inverseMass)
	{
		return inverseInertia*inverseMass;
	}
	public void calculateSurface()
	{
		surface = 0;
		if(points.size()<3)
			return;
		for(int i=points.size()-2, i1=points.size()-1, i2=0; i2<points.size(); i=i1, i1=i2, i2++)
			surface += points.get(i1).x*(points.get(i2).y-points.get(i).y) + points.get(i1).y*(points.get(i).x-points.get(i2).x);
		surface /= 2;
	}
	public float getSurface()
	{
		return surface;
	}
	
	//Collisions detection
	public boolean collisionSat(Form form)
	{
		if(convexForms.size() == 0)
			this.updateConvexForms();
		if(form.getConvexForms().size() == 0)
			form.updateConvexForms();
		
		for(int i = 0; i<convexForms.size(); i++)
		{
			Vector2D VA = new Vector2D();
			Vector2D VB = new Vector2D();
			if(convexForms.get(i).collisionSatFree(form, VA, VB))
				return true;
		}
		return false;
	}
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
	public boolean collisionSatFree(Form B, Vector2D VA, Vector2D VB)
	{
		Form A = this;
		//Les vecteurs VA et VB sont exprimés dans le repère world
		//Les points PA et PB sont exprimés dans le repères world
		
		Matrix3 OA = A.getOrientation();
		Matrix3 OB = B.getOrientation();
		Matrix3 OBi = OB.inverse();
		//La matrice orient permet le passage d'un point du repère local de A à B
		Matrix3 orient = OA.multiply(OBi);
		//La matrice orientI permet le passage d'un point du repère local de B à A
		Matrix3 orientI = orient.inverse();
		
		Vector2D PA = this.getCenter();
		Vector2D PB = B.getCenter();
		
		Vector2D relPos = OBi.multiply(PA.sub(PB));
		Vector2D relVel = OBi.multiply(VA.sub(VB));
		
		ArrayList<Vector2D> pointsA = this.getPointsLocal();
		ArrayList<Vector2D> pointsB = B.getPointsLocal();
		
		ArrayList<Vector2D> axisA = this.getVectorsSatLocal();
		ArrayList<Vector2D> axisB = B.getVectorsSatLocal();
		
		AxesSat axesSat = new AxesSat();
		
		float squaredVel = relVel.getSqMagnitude();
		if(squaredVel > 0.000001f)
		{
			if(!intervalIntersectionFree(relVel.getPerpendicular(), pointsA, pointsB, relPos, relVel, orientI))
				return false;
		}
		
		for(int i=0; i<axisA.size(); i++)
		{
			if(!intervalIntersectionFree(orient.multiply(axisA.get(i)), pointsA, pointsB, relPos, relVel, orientI))
				return false;
		}
		
		for(int i=0; i<axisB.size(); i++)
		{
			if(!intervalIntersectionFree(axisB.get(i), pointsA, pointsB, relPos, relVel, orientI))
				return false;
		}
		return true;
	}
	public boolean intervalIntersectionFree(Vector2D axis, ArrayList<Vector2D> pointsA, ArrayList<Vector2D> pointsB, Vector2D relPos, Vector2D relVel,
			Matrix3 orientI)
	{
		Vector2D minMaxA = getInterval(orientI.multiply(axis), pointsA);
		Vector2D minMaxB = getInterval(axis, pointsB);
		
		//On ajoute le décalage entre les deux repères
		float h = relPos.scalarProduct(axis);
		minMaxA.x += h;
		minMaxA.y += h;
		
		//On calcule les distances pour determiner le chevauchement
		float d0 = minMaxA.x - minMaxB.y;
		float d1 = minMaxB.x - minMaxA.y;
		
		if(d0 > 0 || d1 > 0)//Pas de chevauchement
			return false;
		return true;
	}
	
	public boolean collisionSatA(Form B, Vector2D VA, Vector2D VB, Vector2D push, FloatA t)
	{
		Form A = this;
		//Les vecteurs VA et VB sont exprimés dans le repère world
		//Les points PA et PB sont exprimés dans le repères world
		
		Matrix2 OA = A.getOrientation().convertMatrix2();
		Matrix2 OB = B.getOrientation().convertMatrix2();
		
		Matrix2 OBi = OB.inverse();
		
		//La matrice orient permet le passage d'un point du repère local de A à B
		Matrix2 orient = OA.multiply(OBi);
		//La matrice orientI permet le passage d'un point du repère local de B à A
		Matrix2 orientI = orient.inverse();
		
		Vector2D PA = this.getCenter();
		Vector2D PB = B.getCenter();
		
		Vector2D relPos = OBi.multiply(PA.sub(PB));
		Vector2D relVel = OBi.multiply(VA.sub(VB));
		
		ArrayList<Vector2D> pointsA = this.getPointsLocal();
		ArrayList<Vector2D> pointsB = B.getPointsLocal();
		
		ArrayList<Vector2D> axisA = this.getVectorsSatLocal();
		ArrayList<Vector2D> axisB = B.getVectorsSatLocal();
		
		AxesSat axesSat = new AxesSat();
		
		float squaredVel = relVel.getSqMagnitude();
		if(squaredVel > 0.000001f)
		{
			if(!intervalIntersection(relVel.getPerpendicular(), pointsA, pointsB, relPos, relVel, orientI, axesSat, t))
				return false;
		}
		
		for(int i=0; i<axisA.size(); i++)
		{
			if(!intervalIntersection(orient.multiply(axisA.get(i)), pointsA, pointsB, relPos, relVel, orientI, axesSat, t))
				return false;
		}
		
		for(int i=0; i<axisB.size(); i++)
		{
			if(!intervalIntersection(axisB.get(i), pointsA, pointsB, relPos, relVel, orientI, axesSat, t))
				return false;
		}
		
		getPushVector(axesSat, push, t);
		
		//On s'assurre que les objets s'éloignent l'un de l'autre
		if(relPos.scalarProduct(push) < 0)
			push.selfMultiply(-1);
		
		push.set(OB.multiply(push));
		
		//System.out.println("Collision");
		return true;
	}
	
	public boolean intervalIntersection(Vector2D axis, ArrayList<Vector2D> pointsA, ArrayList<Vector2D> pointsB, Vector2D relPos, Vector2D relVel,
			Matrix2 orientI, AxesSat axesSat, FloatA t)
	{
		Vector2D minMaxA = getInterval(orientI.multiply(axis), pointsA);
		Vector2D minMaxB = getInterval(axis, pointsB);
		
		//On ajoute le décalage entre les deux repères
		float h = relPos.scalarProduct(axis);
		minMaxA.x += h;
		minMaxA.y += h;
		
		//On calcule les distances pour determiner le chevauchement
		float d0 = minMaxA.x - minMaxB.y;
		float d1 = minMaxB.x - minMaxA.y;
		
		if(d0 > 0 || d1 > 0)//Pas de chevauchement
		{
			float fVel = relVel.scalarProduct(axis);
			if(Math.abs(fVel) > 0.00000001f)
			{
				float t0 =-d0/fVel;
				float t1 = d1/fVel;
				
				if(t0 > t1) {float temp = t0; t0 = t1; t1 = temp;}
				float l_tAxis = (t0 > 0)? t0:t1;
				
				if(l_tAxis < 0 || l_tAxis > t.v)
					return false;
				
				axesSat.axesT.add(axis);
				axesSat.tAxesT.add(l_tAxis);
				return true;
			}
			return false;
		}
		else
		{
			axesSat.axes.add(axis);
			axesSat.tAxes.add((d0 > d1)? d0:d1);
			return true;
		}
	}
	public Vector2D getInterval(Vector2D axis, ArrayList<Vector2D> points)
	{
		Vector2D minMax = new Vector2D();
		
		minMax.x = minMax.y = points.get(0).scalarProduct(axis);
		for(int i=1; i<points.size(); i++)
		{
			float scalar = points.get(i).scalarProduct(axis);
			if(scalar < minMax.x)
			{
				minMax.x = scalar;
			}
			else if(scalar > minMax.y)
			{
				minMax.y = scalar;
			}
		}
		return minMax;
	}
	public void getPushVector(AxesSat axesSat, Vector2D push, FloatA t)
	{
		t.v = 0;
		boolean found = false;
		for(int i=0; i<axesSat.axesT.size(); i++)
		{
			if(axesSat.tAxesT.get(i) > t.v)
			{
				t.v = axesSat.tAxesT.get(i);
				push.set(axesSat.axesT.get(i));
				found = true;
			}
		}
		push.normalize();
		
		if(found)
			return;
	
		float magnitude1 = axesSat.axes.get(0).normalize();
		axesSat.tAxes.set(0, axesSat.tAxes.get(0)/magnitude1);
		t.v = axesSat.tAxes.get(0);
		push.set(axesSat.axes.get(0));
		
		for(int i=1; i<axesSat.axes.size(); i++)
		{
			float magnitude = axesSat.axes.get(i).normalize();
			axesSat.tAxes.set(i, axesSat.tAxes.get(i)/magnitude);
			if(axesSat.tAxes.get(i) > t.v)
			{
				t.v = axesSat.tAxes.get(i);
				push.set(axesSat.axes.get(i));
				found = true;
			}
		}
		
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
	
	public ArrayList<Form> splitUnsercured(Vector2D p0, Vector2D p1, ArrayList<HashSet<Vector2D>> bst)
	{
		int n = 0;
		ArrayList<Form> forms = new ArrayList<Form>();
		
		//On cherche la position des 2 points
		int pos0 = -1;
		int pos1 = -1;
		int pos = 0;
		while(pos0 == -1 || pos1 == -1)
		{
			if(p0 == points.get(pos))
				pos0 = pos;
			else if(p1 == points.get(pos))
				pos1 = pos;
			pos++;
		}
		
		if(pos0 + 1 != pos1 && pos1 + 1 != pos0)
		{
			//Form1
			Form form1 = new Form();
			bst.add(new HashSet<Vector2D>());
			
			pos = pos0;
			while(pos != pos1)
			{
				form1.addPointFree(points.get(pos));
				bst.get(points.size()-1).add(points.get(pos));
				pos = (pos + 1)%points.size();
			}
			form1.addPointFree(points.get(pos1));
			bst.get(points.size()-1).add(points.get(pos1));
			
			//Form2
			Form form2 = new Form();
			bst.add(new HashSet<Vector2D>());
			
			pos = pos1;
			while(pos != pos0)
			{
				form2.addPointFree(points.get(pos));
				bst.get(points.size()-1).add(points.get(pos));
				pos = (pos + 1)%points.size();
			}
			form2.addPointFree(points.get(pos0));
			bst.get(points.size()-1).add(points.get(pos0));
			
			forms.add(form1);
			forms.add(form2);
		}
		else
		{
			forms.add(this);
		}
		return forms;
	}
	
	//******************
	//  	Draw
	//******************
	public Polygon getPolygon()
	{
		Polygon pol = new Polygon();
		for(int i=0; i<points.size(); i++)
		{
			pol.addPoint((int) this.getX(i), (int) this.getY(i));
		}
		return pol;
	}
	public void draw(Graphics g)
	{
		Polygon poly = getPolygon();
		if(convexForms.size() == 0)
			g.setColor(Color.magenta);
		else
			g.setColor(Color.red);
		g.drawPolygon(poly);
	}
	public void draw(Graphics g, Vector2D vec)
	{
		Polygon poly = getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.red);
		g.drawPolygon(poly);
	}
	public void draw(Graphics g, Vector2D vec, float scale)
	{
		Form form = this.clone();
		form.scale(scale, new Vector2D());
		
		Polygon poly = form.getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.red);
		g.drawPolygon(poly);
	}
	public void drawRW(Graphics g, Vector2D vec)
	{
		this.draw(g, vec);
		Form form = this.clone();
		form.scale(0.95f, this.getCenter());
		Polygon poly = form.getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.WHITE);
		g.drawPolygon(poly);
	}
	public void drawRW(Graphics g, Vector2D vec, float scale)
	{
		Form form = this.clone();
		form.scale(scale, new Vector2D());
		
		Polygon poly = form.getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.red);
		g.drawPolygon(poly);
		
		Vector2D center = this.getCenter().clone();
		center.scale(scale, new Vector2D());
		form.scale(0.95f, center);
		poly = form.getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.WHITE);
		g.drawPolygon(poly);
	}
	public void fillForm(Graphics g, Vector2D vec)
	{
		Polygon poly = getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.red);
		g.fillPolygon(poly);
	}
	public void fillForm(Graphics g, Vector2D vec, float scale)
	{
		Form form = this.clone();
		form.scale(scale, new Vector2D());
		
		Polygon poly = form.getPolygon();
		poly.translate((int) vec.x, (int) vec.y);
		g.setColor(Color.red);
		g.fillPolygon(poly);
	}

	//Convex
	public boolean isConvex()
	{
		if(points.size() < 3)
			return true;

		ArrayList<Vector2D> vectors = this.getVectorsLocal();
		int i=0;
		float crossProductZ = 0;
		
		while(crossProductZ == 0 && i<vectors.size())
		{
			crossProductZ = vectors.get(i).crossProductZ(vectors.get((i+1)%vectors.size()));
			i++;
		}
		
		for(; i<vectors.size(); i++)
		{
			float crossProductZ2 = vectors.get(i).crossProductZ(vectors.get((i+1)%vectors.size()));
		
			//Si les deux vectoriels z sont de sens différents, alors la forme n'est pas convexe
			if(crossProductZ * crossProductZ2 < 0)
				return false;
		}
		
		return true;
	}
	public int getClockwise()
	{
		if(points.size() < 3)
			return 0;
		double sum = 0;
		//float sumInt = 0;
		for(int i=0; i<points.size(); i++)
		{
			sum = sum + (Math.PI - points.get((i+1)%points.size()).getAngle(points.get(i), points.get((i+2)%points.size())));
			//sumInt = sumInt + (float) (points.get((i+1)%points.size()).getAngle(points.get(i), points.get((i+2)%points.size())));
		}
		//System.out.println("Sum: " + sum);
		//System.out.println("SumInt: " + sumInt);
		//System.out.println("");
		if(Math.PI*2 - 0.001 < Math.abs(sum) && Math.abs(sum) < 2*Math.PI + 0.001)
		{
			if(sum < 0)
				return -1;
			return 1;
		}
		else
		{
			//Si des cotés se croisent, ce n'est pas un polygone regulier
			System.out.println("bad: " + sum);
			return 0;
		}
	}
	public ArrayList<Edge> getEdgesLocal()
	{	
		int factor = this.getClockwise();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		if(points.size() < 2 || factor == 0)
			return edges;
		
		ArrayList<PointType> pointsType = new ArrayList<PointType>();
		
		if(factor == 1)
		{
			for(int i=points.size()-1; i>-1; i--)
			{
				PointType p = new PointType(points.get(i));
				p.posPoint = i;
				p.posEdge = pointsType.size();
				p.type = -1;
				pointsType.add(p);
			}
		}
		else
		{
			for(int i=0; i<points.size(); i++)
			{
				PointType p = new PointType(points.get(i));
				p.posPoint = i;
				p.posEdge = pointsType.size();
				p.type = -1;
				pointsType.add(p);
			}
		}
		
		for(int i=0; i<points.size(); i++)
		{
			Edge edge = new Edge();
		
			edge.p0 = pointsType.get(i);
			edge.p1 = pointsType.get((i+1)%pointsType.size());
			
			if(i != 0)
			{
				edge.prev = edges.get(edges.size() - 1);
				edges.get(edges.size() - 1).next = edge;
			}
			edges.add(edge);
		}
		edges.get(0).prev = edges.get(points.size() - 1);
		edges.get(points.size() - 1).next = edges.get(0);
		
		return edges;
	}
	
	//--------------------
	//	  Triangulate
	//--------------------
	public void triangulate()
	{
		convexForms.clear();
		
		ArrayList<Form> monotonesForms = makeMonotone();
		if(monotonesForms.size() != 0)
		{
			for(int i=0; i<monotonesForms.size(); i++)
			{
				convexForms.addAll(monotonesForms.get(i).triangulateMonotone());
				//convexForms.add(monotonesForms.get(i));
			}
		}
		else
			convexForms.add(this);
	}
	
	//******************
	//  Make Monotone
	//******************
	public ArrayList<Form> makeMonotone()
	{
		ArrayList<Edge> edges = getEdgesLocal();
		
		if(edges.size() == 0 || edges.size() == 2)
			return new ArrayList<Form>();
		
		//***************
		//Initialization
		//***************
		ArrayList<Integer> v = new ArrayList<Integer>(points.size());
		
		//Scanning line l store Edges colliding with l, sorted by x
		TreeMap<Float, ArrayList<Edge>> l = new TreeMap<Float, ArrayList<Edge>>();
		ArrayList<Edge> preBufferL = new ArrayList<Edge>();
		
		//<Edge, PointType>
		HashMap<Edge, PointType> helpers = new HashMap<Edge, PointType>();
		HashSet<Vector2D> trash = new HashSet<Vector2D>(); 
		
		//***************
		//   Sorting
		//***************
		//1) on range par ordre croissant suivant Y les points
		sortPointsY(edges, v);
		
		//***************
		//     Loop
		//***************
		PointType p0, p1, p2;
		//2) On applique une méthode pour chaque point
		while(v.size() != 0)
		{
			int pos = v.remove(0);
			Edge edge = edges.get(pos);
			
			//On determine le type de point
			p0 = edge.prev.p0;
			p1 = edge.p0;
			p2 = edge.p1;

			trash.add(p1);
			
			//On regarde si les 2 segments débutent à partir de ce point
			//ou finissent à partir de ce point
			if(trash.contains(p0))
			{
				float minX = edge.prev.getMinX();
				ArrayList<Edge> e = l.get(minX);
				if(e.size() == 1)
				{
					e.clear();
					l.remove(minX);
				}
				else
				{
					e.remove(edge.prev);
				}
			}
			else
				preBufferL.add(edge.prev);
			
			if(trash.contains(p2))
			{
				float minX = edge.getMinX();
				ArrayList<Edge> e = l.get(minX);
				if(e.size() == 1)
				{
					e.clear();
					l.remove(minX);
				}
				else
				{
					e.remove(edge);
				}
			}
			else
				preBufferL.add(edge);
			
			//***************
			//     Type
			//***************
			determineType(pos, edges, helpers, l);
			
			//On ajoute les nouveaux segments stockés dans le prébuffer
			for(int i=0; i<preBufferL.size(); i++)
			{
				float minX = preBufferL.get(i).getMinX();
				if(l.containsKey(minX))
				{
					l.get(minX).add(preBufferL.get(i));
				}
				else
				{
					ArrayList<Edge> e = new ArrayList<Edge>();
					e.add(preBufferL.get(i));
					l.put(minX, e);
				}
			}
			preBufferL.clear();
		}

		return transformEdges(edges);
	}
	public void sortPointsY(ArrayList<Edge> edges, ArrayList<Integer> v)
	{
		PointType p0, p1;
		for(int i=0; i<points.size(); i++)
		{
			v.add(i);
		}
		for(int t=1; t<v.size(); t++)
		{
			for(int i=0; i<v.size() - 1; i++)
			{
				p0 = edges.get(v.get(i)).p0;
				p1 = edges.get(v.get(i+1)).p0;
				if(p0.y > p1.y)
				{
					int pos = v.remove(i+1);
					v.add(i, pos);
				}
				else if(p0.y == p1.y)
				{
					if(p0.x < p1.x)
					{
						int pos = v.remove(i+1);
						v.add(i, pos);
					}
				}
			}
		}
	}
	public void determineType(int pos, ArrayList<Edge> edges, HashMap<Edge, PointType> helpers, TreeMap<Float, ArrayList<Edge>> l)
	{
		Edge edge = edges.get(pos);
		
		//On determine le type de point
		PointType p0 = edge.prev.p0;
		PointType p1 = edge.p0;
		PointType p2 = edge.p1;
		
		//On détermine le type de ce point
		float theta = (float) -(Math.PI - p1.getAngle(p0, p2)); //Angle exterieur
		if(p1.y < p0.y && p1.y < p2.y)
		{
			if(theta > 0 && theta < Math.PI)
			{
				//Start
				p1.type = PointType.START;
				handleStartVertex(pos, edges, helpers);
			}
			else
			{
				//Split
				p1.type = PointType.SPLIT;
				handleSplitVertex(pos, edges, helpers, l);
			}
		}
		else if(p1.y > p0.y && p1.y > p2.y)
		{
			if(theta > 0 && theta < Math.PI)
			{
				//End
				p1.type = PointType.END;
				handleEndVertex(pos, edges, helpers);
			}
			else
			{
				//Merge
				p1.type = PointType.MERGE;
				handleMergeVertex(pos, edges, helpers, l);
			}
		}
		else if(p1.y == p0.y || p1.y == p2.y)
		{
			if((p1.y < p0.y && p1.x > p2.x) || (p1.y < p2.y && p1.x > p0.x))
			{
				if(theta > 0 && theta < Math.PI)
				{
					//Start
					p1.type = PointType.START;
					handleStartVertex(pos, edges, helpers);
				}
				else
				{
					//Split
					p1.type = PointType.SPLIT;
					handleSplitVertex(pos, edges, helpers, l);
				}
			}
			else if((p1.y > p0.y && p1.x < p2.x) || (p1.y > p2.y && p1.x < p0.x))
			{
				if(theta > 0 && theta < Math.PI)
				{
					//End
					p1.type = PointType.END;
					handleEndVertex(pos, edges, helpers);
				}
				else
				{
					//Merge
					p1.type = PointType.MERGE;
					handleMergeVertex(pos, edges, helpers, l);
				}
			}
			else if(p1.y == p0.y && p1.y == p2.y)
			{
				//Nothing
			}
			else
			{
				//Regular
				p1.type = PointType.REGULAR;
				handleRegularVertex(pos, edges, helpers, l);
			}
		}
		else
		{
			//Regular
			p1.type = PointType.REGULAR;
			handleRegularVertex(pos, edges, helpers, l);
		}
	}
	public Edge getLeftEdge(int pos, ArrayList<Edge> edges, TreeMap<Float, ArrayList<Edge>> l)
	{
		PointType p = edges.get(pos).p0; 
		Entry<Float, ArrayList<Edge>> low = l.floorEntry(p.x);
		Entry<Float, ArrayList<Edge>> lastLow;
		Edge leftEdge = getLeftEdge(p, low.getValue());
		
		while(leftEdge == null)
		{
			lastLow = low;
			low = l.lowerEntry(low.getKey());
			if((low == null) || (low.getValue() == lastLow.getValue()))
				return null;
			leftEdge = getLeftEdge(p, low.getValue());
		}
		return leftEdge;
	}
	public Edge getLeftEdge(PointType p, ArrayList<Edge> lEdges)
	{
		for(int i=0; i<lEdges.size(); i++)
		{
			Vector2D vec = new Vector2D(lEdges.get(i).p0, lEdges.get(i).p1);
			Vector2D projection = p.getProjection(vec, lEdges.get(i).p0);
			projection = projection.sub(p);
			if(projection.x < 0)
				return lEdges.get(i);
		}
		return null;
	}
	
	public ArrayList<Form> transformEdges(ArrayList<Edge> edges)
	{
		ArrayList<Form> forms = new ArrayList<Form>();
		ArrayList<HashSet<Vector2D>> bst = new ArrayList<HashSet<Vector2D>>();
		
		forms.add(this);
		bst.add(new HashSet<Vector2D>());
		bst.get(0).addAll(points);
		
		
		for(int i=points.size(); i<edges.size(); i++)
		{
			Edge edge = edges.get(i);
			Vector2D p0 = points.get(edge.p0.posPoint);
			Vector2D p1 = points.get(edge.p1.posPoint);
			
			boolean did = false;
			int n = 0;
			int max = forms.size();
			for(int j=0; j<max; j++)
			{
				if(bst.get(j).contains(p0) && bst.get(j).contains(p1))
				{
					ArrayList<HashSet<Vector2D>> bst2 = new ArrayList<HashSet<Vector2D>>();
					ArrayList<Form> newForm = forms.get(j).splitUnsercured(p0, p1, bst2);
					if(newForm.size() == 2)
					{
						forms.addAll(newForm);
						forms.remove(j);
						bst.addAll(bst2);
						bst.remove(j);
						
						max--;
						j--;
						n++;
						did = true;
					}
				}
			}
		}
		
		return forms;
	}
	
	//Type fonction
	public void handleStartVertex(int pos, ArrayList<Edge> edges, HashMap<Edge, PointType> helpers)
	{
		helpers.put(edges.get(pos), edges.get(pos).p0);
	}
	public void handleEndVertex(int pos, ArrayList<Edge> edges, HashMap<Edge, PointType> helpers)
	{
		//Si c'est le helper du coté précédent est un point de type merge
		//il sera dans l'arbre
		PointType helper = helpers.get(edges.get(pos).prev);
		if(helper!= null && helper.type == PointType.MERGE)
		{
			//On crée une diagonale entre le point pos et le helper
			Edge diagonal = new Edge();
			diagonal.p0 = edges.get(pos).p0;
			diagonal.p1 = helper;
			diagonal.prev = null;
			diagonal.next = null;
			edges.add(diagonal);
		}
		helpers.remove(edges.get(pos).prev);
		
	}
	public void handleSplitVertex(int pos, ArrayList<Edge> edges, HashMap<Edge, PointType> helpers, TreeMap<Float, ArrayList<Edge>> l)
	{
		//On cherche le coté à gauche du point le plus proche
		Edge leftEdge = getLeftEdge(pos, edges, l);
		assert(leftEdge != null);
		
		//On récupère le helper de ce coté
		PointType helper = helpers.get(leftEdge);
		
		//On crée une diagonale entre le point pos et le helper
		Edge diagonal = new Edge();
		diagonal.p0 = edges.get(pos).p0;
		diagonal.p1 = helper;
		diagonal.prev = null;
		diagonal.next = null;
		edges.add(diagonal);
		
		assert(helper != null);
		
		//Le helper du coté à gauche devient vi
		helpers.remove(leftEdge);
		helpers.put(leftEdge, diagonal.p0);
		
		helpers.put(edges.get(pos), edges.get(pos).p0);
	}
	public void handleMergeVertex(int pos, ArrayList<Edge> edges, HashMap<Edge, PointType> helpers, TreeMap<Float, ArrayList<Edge>> l)
	{
		//On regarde le type du helper du dernier coté
		PointType helper = helpers.get(edges.get(pos).prev);
		if(helper != null && helper.type == PointType.MERGE)
		{
			//On crée une diagonale entre le point pos et le helper
			Edge diagonal = new Edge();
			diagonal.p0 = edges.get(pos).p0;
			diagonal.p1 = helper;
			diagonal.prev = null;
			diagonal.next = null;
			edges.add(diagonal);
		}
		helpers.remove(edges.get(pos).prev);
		
		//On cherche le coté à gauche de ce point
		Edge leftEdge = getLeftEdge(pos, edges, l);
		assert(leftEdge != null);
		
		//On récupère le helper de ce coté
		helper = helpers.get(leftEdge);
		if(helper != null && helper.type == PointType.MERGE)
		{
			//On crée une diagonale entre le point pos et le helper
			Edge diagonal = new Edge();
			diagonal.p0 = edges.get(pos).p0;
			diagonal.p1 = helper;
			diagonal.prev = null;
			diagonal.next = null;
			edges.add(diagonal);
		}
		//Le helper du coté à gauche devient vi
		helpers.remove(leftEdge);
		helpers.put(leftEdge, edges.get(pos).p0);
	}
	public void handleRegularVertex(int pos, ArrayList<Edge> edges, HashMap<Edge, PointType> helpers, TreeMap<Float, ArrayList<Edge>> l)
	{
		//On détermine si le polygone est à droite du coté
		//si le nombre de coté touché est pair = exterieur à droite
		//si le nombre de coté touché est impair = intérieur à droite
		
		int numberLines = numberLeftEdges(pos, edges, l);
		//System.out.println("size: " + numberLines);
		if(numberLines%2 == 0)
		{//impair
			PointType helper = helpers.get(edges.get(pos).prev);
			if(helper != null && helper.type == PointType.MERGE)
			{
				//On crée une diagonale entre le point pos et le helper
				Edge diagonal = new Edge();
				diagonal.p0 = edges.get(pos).p0;
				diagonal.p1 = helper;
				diagonal.prev = null;
				diagonal.next = null;
				edges.add(diagonal);
			}
			helpers.remove(edges.get(pos).prev);
			helpers.put(edges.get(pos), edges.get(pos).p0);
		}
		else
		{//pair
			//On cherche le coté à gauche de ce point
			Edge leftEdge = getLeftEdge(pos, edges, l);
			assert(leftEdge != null);
			
			//On récupère le helper de ce coté
			PointType helper = helpers.get(leftEdge);
			if(helper != null && helper.type == PointType.MERGE)
			{
				//On crée une diagonale entre le point pos et le helper
				Edge diagonal = new Edge();
				diagonal.p0 = edges.get(pos).p0;
				diagonal.p1 = helper;
				diagonal.prev = null;
				diagonal.next = null;
				edges.add(diagonal);
			}
			//Le helper du coté à gauche devient vi
			helpers.remove(leftEdge);
			helpers.put(leftEdge, edges.get(pos).p0);
		}
	}
	
	public int numberLeftEdges(int pos, ArrayList<Edge> edges, TreeMap<Float, ArrayList<Edge>> l)
	{
		int numberLines = 0;
		PointType p = edges.get(pos).p0;
		Entry<Float, ArrayList<Edge>> low = l.floorEntry(p.x);
		Entry<Float, ArrayList<Edge>> lastLow;
		if(low != null)
		{
			numberLines += this.numberLeftEdges(p, low.getValue());
		
			while(true)
			{
				lastLow = low;
				low = l.lowerEntry(low.getKey());
				if(low == null || (low.getValue() == lastLow.getValue()))
					break;
				numberLines += numberLeftEdges(p, low.getValue());
			}
		}
		return numberLines;
	}
	public int numberLeftEdges(PointType p, ArrayList<Edge> lEdges)
	{
		int numberLines = 0;
		for(int i=0; i<lEdges.size(); i++)
		{
			Vector2D vec = new Vector2D(lEdges.get(i).p0, lEdges.get(i).p1);
			Vector2D projection = p.getProjection(vec, lEdges.get(i).p0);
			projection = projection.sub(p);
			if(projection.x < 0)
				numberLines++;
		}
		return numberLines;
	}
	
	//******************
	//Triangulate Monot
	//******************
	public ArrayList<Form> triangulateMonotone()
	{
		ArrayList<Edge> edges = this.getEdgesLocal();

		ArrayList<Form> forms = new ArrayList<Form>();
		ArrayList<Integer> v = new ArrayList<Integer>();
		ArrayList<Integer> l = new ArrayList<Integer>();
		
		if(edges.size() < 3)
		{
			return new ArrayList();
		}
		
		HashSet<PointType> lChain = new HashSet<PointType>();
		//1) on range par ordre croissant suivant Y les points
		sortPointsY(edges, v);
		createChains(v, edges, lChain);
		
		//int factorChain = determineFactorChain(edges.get(v.get(v.size() - 1)), edges);
		
		//2) Initialisation de L, on ajout les 2 premiers points
		l.add(v.remove(0));
		l.add(v.remove(0));
		
		PointType p0, p1, p2, last;
		while(v.size() > 0)
		{
			int pos = v.remove(0);
			Edge edge = edges.get(pos);
			
			p0 = edge.prev.p0;
			p1 = edge.p0;
			p2 = edge.p1;
			
			last = edges.get(l.get(l.size() - 1)).p0;
			
			//Si le point appartient à une chaine différente
			if(lChain.contains(last) != lChain.contains(p1))
			{
				while(l.size() > 1)
				{
					Vector2D l_p0, l_p1, l_p2;
					l_p0 = points.get(edges.get(l.get(0)).p0.posPoint);
					l_p1 = points.get(edges.get(l.get(1)).p0.posPoint);
					l_p2 = points.get(p1.posPoint);
					
					Form form = new Form();
					form.addPointFree(l_p0);
					form.addPointFree(l_p1);
					form.addPointFree(l_p2);
					forms.add(form);
					l.remove(0);
				}
				l.add(pos);
			}
			else
			{
				if(l.size() > 1)
				{
					Vector2D p, q, r;
					q = points.get(edges.get(l.get(l.size() - 1)).p0.posPoint);
					r = points.get(edges.get(l.get(l.size() - 2)).p0.posPoint);
					p = points.get(p1.posPoint);
					
					//vec1.crossProductZ(vec2)
					boolean bLChain = lChain.contains(p1);
					float orientation = Utils.orientation(p, q, r);
					while(l.size() > 1 && ((bLChain && orientation > 0) || (!bLChain && orientation < 0)))
					{
						Form form = new Form();
						form.addPointFree(q);
						form.addPointFree(r);
						form.addPointFree(p);
						forms.add(form);
						
						l.remove(l.size() - 1);
						if(l.size() > 1)
						{
							q = points.get(edges.get(l.get(l.size() - 1)).p0.posPoint);
							r = points.get(edges.get(l.get(l.size() - 2)).p0.posPoint);
							
							orientation = Utils.orientation(p1, q, r);
						}
					}
				}
				l.add(pos);
			}
		}
		
		if(l.size() > 2)
		{
			//On ajoute les derniers triangles
			v.add(l.remove(l.size() - 1));
			for(int i=0; i<l.size()-1; i++)
			{
				Vector2D l_p0, l_p1, l_p2;
				l_p0 = points.get(edges.get(l.get(i)).p0.posPoint);
				l_p1 = points.get(edges.get(l.get(i + 1)).p0.posPoint);
				l_p2 = points.get(edges.get(v.get(v.size() - 1)).p0.posPoint);
				
				Form form = new Form();
				form.addPointFree(l_p0);
				form.addPointFree(l_p1);
				form.addPointFree(l_p2);
				forms.add(form);
			}
		}
		return forms;
	}
	private void createChains(ArrayList<Integer> v, ArrayList<Edge> edges, HashSet<PointType> lChain)
	{
		Edge start = edges.get(v.get(0));
		PointType last = edges.get(v.get(v.size() - 1)).p0;
		
		Vector2D collisionVec = new Vector2D(start.p0, start.next.p0);
		Vector2D vec = new Vector2D(1, 0);
		
		Vector2D collision = new Vector2D();
		boolean result = collision.computeIntersection(collisionVec, vec, start.p0, start.prev.p0);
		boolean left = false;
		if(result)
		{
			left = (collision.x < start.prev.p0.x);
		}
		else
		{
			assert(start.next.p0.y == start.p0.y);
			left = (start.next.p0.x < start.p0.x);
		}
		
		if(left)
		{
			Edge current = start.next;
			while(current.p0 != last)
			{
				lChain.add(current.p0);
				current = current.next;
			}
		}
		else
		{
			Edge current = start.prev;
			while(current.p0 != last)
			{
				lChain.add(current.p0);
				current = current.prev;
			}
		}
	}

	public ArrayList<Form> getTriangulation()
	{
		ArrayList<Form> forms = new ArrayList<Form>();
		if(this.isConvex())
		{
			forms.add(this);
			return forms;
		}
		float factor = getClockwise();
		ArrayList<Vector2D> pointsV = new ArrayList<Vector2D>(points);
		//ArrayList<Point2D[]> edgesL = new ArrayList<Point2D[]>();
		
		ArrayList<Integer> pointsVPos = new ArrayList<Integer>(points.size());
		ArrayList<Integer> pointsPreviousVPos = new ArrayList<Integer>(points.size());
		ArrayList<Integer> edgesLPos = new ArrayList<Integer>();
		for(int i=0; i<pointsV.size(); i++)
			pointsVPos.add(i);
		
		//1) on range par ordre croissant suivant X les points
		for(int t=1; t<pointsV.size(); t++)
		{
			for(int i=0; i<pointsV.size() - 1; i++)
			{
				if(pointsV.get(i).x > pointsV.get(i+1).x)
				{
					Vector2D point = pointsV.remove(i+1);
					pointsV.add(i, point);
					
					int pos = pointsVPos.remove(i+1);
					pointsVPos.add(i, pos);
				}
			}
		}
		
		Vector2D p[] = new Vector2D[3];
		Integer pos[] = new Integer[3];
		//2) Pour chaque point, on cherche les points d'interesections avec le polygon
		while(pointsV.size() != 0)
		{
			//a) insertion des segments
			p[1] = pointsV.remove(0);
			pos[1] = pointsVPos.remove(0);
			
			//On récupère les deux autres points reliés à ce point
			//point précédent
			pos[0] = pos[1] - 1;
			if(pos[0] < 0)
				pos[0] = points.size() - 1;
			p[0] = points.get(pos[0]);
			
			//point suivant
			pos[2] = (pos[1] + 1)%points.size();
			p[2] = points.get(pos[2]);
			
			//On insère un segment si le point est à droite, sinon on l'enleve
			//+= 2 signifie, on regarde le point précédent puis le point suivant
			for(int i=0; i<p.length; i += 2)
			{
				//Dans le cas ou le point a le même x, on prend seulement le point s'il est est au dessus (y inférieur)
				if(p[1].x < p[i].x)
				{
					boolean added = false;
					for(int j=0; j<edgesLPos.size(); j++)
					{
						if(p[1].y < points.get(edgesLPos.get(j)).y)
						{
							edgesLPos.add(j, pos[0] + i/2);
							added = true;
						}
					}
					if(!added)
						edgesLPos.add(pos[0] + i/2);
				}
				else if(p[1].x == p[i].x && p[1].y <= p[i].y)
				{
					assert(p[i].y != p[1].y):"2 points confondus";
					
					boolean added = false;
					for(int j=0; j<edgesLPos.size(); j++)
					{
						if(p[1].y < points.get(edgesLPos.get(j)).y)
						{
							edgesLPos.add(j, pos[0] + i/2);
							added = true;
						}
					}
					if(!added)
						edgesLPos.add(pos[0] + i/2);
				}
				else
				{
					//On enleve le segment
					boolean b = false;
					for(int j=0; j<edgesLPos.size(); j++)
					{
						if(pos[0] + i/2 == edgesLPos.get(j))
						{
							edgesLPos.remove(j);
							b = true;
							break;
						}
					}
					assert(b);
				}
			}
			
			//Si la ligne L touche plus de 2 segments,
			//On crée de nouvelles formes
		}
		
		
		return forms;
	}

	public void setConvexForms(ArrayList<Form> forms)
	{
		convexForms.clear();
		convexForms.addAll(forms);
	}
	public ArrayList<Form> getConvexForms()
	{
		return convexForms;
	}

	public void updateConvexForms()
	{
		convexForms.clear();
		if(this.isConvex())
		{
			convexForms.add(this);
		}
		else
		{
			this.triangulate();
		}
	}
}
