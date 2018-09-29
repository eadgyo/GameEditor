import java.util.ArrayList;
import java.util.HashSet;

import Maths.Edge;
import Maths.Form;
import Maths.PointType;


public class b {

}
/*
public ArrayList<Form> transformEdges(ArrayList<Edge> edges, Edge first, Edge last)
{
	ArrayList<Form> monotonesForms = new ArrayList<Form>();
	PointType p0, p1, p2;
	HashSet<Edge> trash = new HashSet<Edge>();
	HashSet<Edge> toDo = new HashSet<Edge>();

	//On transforme les edges en forms
	if(edges.size() == points.size())
	{
		monotonesForms.add(this);
	}
	else
	{
		Edge next1, next2, prev1, prev2;
		boolean isNext = false;
		Edge next = null;
		Edge prev = null;
		for(int i=points.size(); i<edges.size(); i++)
		{
			Form form = new Form();

			Edge edge = edges.get(i);
			p0 = edge.p0;
			p1 = edge.p1;
			
			next2 = edges.get(p0.posEdge);
			next1 = next2.prev;
			
			assert(next1.p1 == p0 && next2.p0 == p0);
			
			
			Edge current;
			//On choisit le bon chemin
			//On a 2 possibilités next1 et next2
			//1er test
			current = next1;
			boolean valid1 = true, valid2 = true;
			while(current.p1 != p1)
			{
				if(bst.contains(current))
				{
					valid1 = false;
					break;
				}
				else
					bst.add(current);
				current = current.prev;
			}
			if(valid1)
			{
				valid1 = bst.contains(first);
				if(!valid1)
					valid1 = !bst.contains(last);	
			}
			
			
			//2eme test
			current = next2;
			while(current.p0 != p1)
			{
				if(bst.contains(current))
				{
					valid2 = false;
					break;
				}
				else
					bst.add(current);
				current = current.next;
			}
			if(valid2)
			{
				valid2 = bst.contains(first);
				if(!valid2)
					valid2 = !bst.contains(last);
			}
			
			assert((valid1 || valid2) && !(valid1 && valid2)): "Pas de chemins valides";

			
			//On parcourt le nouveau polygone
			form.addPoint(p0);
			if(valid1)
			{
				current = next1;
				isNext = true;
				int n = 0;
				while(current.p1 != p1 && n < 1000)
				{
					n++;
					current = current.prev;
					form.addPoint(current.p1);
				}
				assert(n != 1000);
				
				next = next2;
				prev = current;
		
				//On modifie les edges
				//current == final
				edge.next = next2;
				edge.prev = current;
				
				next2.prev = edge;
				current.next = edge;
			}
			else
			{
				current = next2;
				isNext = false;
				int n = 0;
				while(current.p0 != p1 && n < 1000)
				{
					n++;
					current = current.next;
					form.addPoint(current.p0);
				}
				assert(n != 1000);
			
				next = next1;
				prev = current;

				//On modifie les edges
				//current = final
				edge.prev = next1;
				edge.next = current;
				
				next1.next = edge;
				current.prev = edge;
			}
			monotonesForms.add(form);
		}
		//***************
		// Derniere form
		//***************
		
		Form form = new Form();
		//On finit par ajouter la dernier forme
		p0 = next.p0;
		p1 = next.p1;
		
		Edge current = next;
		int n = 0;
		if(isNext)
		{
			n = 0;
			while(current.p0 != p1 && n < 1000)
			{
				n++;
				current = current.next;
				form.addPoint(current.p0);
			}
			
		}
		else
		{
			n = 0;
			while(current.p0 != p1 && n < 1000)
			{
				n++;
				current = current.prev;
				form.addPoint(current.p0);
			}
			
		}
		assert(n != 1000);
		
		monotonesForms.add(form);
	}
	return monotonesForms;
}
*/