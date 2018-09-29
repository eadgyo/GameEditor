package BaseWindows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;

public class Grid 
{
	boolean isActivated;
	Vector2D move;
	int length;
	BufferedImage surface;
	Graphics buffG;
	
	Grid(BufferedImage surface)
	{
		this.surface = surface;
		this.buffG = surface.getGraphics();
		isActivated = false;
		move = new Vector2D();
		length = 64;
	}
	public void renderGrid()
	{
		if(isActivated)
		{
			buffG.setColor(Color.LIGHT_GRAY);
			PointInt n = new PointInt((int) ((float)(surface.getWidth())/length + 0.5f),
									(int) ((float)(surface.getHeight())/length + 0.5f));
			for(int i=-(int)(n.x*0.5f +0.5f); i<(int)(n.x*0.5f + 1.0f); i++)
			{
				buffG.drawLine((int)(surface.getWidth()*0.5f) + i*length + (int) (move.x)%length, 0,
						(int)(surface.getWidth()*0.5f) + i*length + (int) (move.x)%length, surface.getHeight());
			}
			for(int i=-(int)(n.y*0.5f + 0.5f); i<(int)(n.y*0.5f + 1.0f); i++)
			{
				buffG.drawLine(0, (int)(surface.getHeight()*0.5f) + i*length + (int) (move.y)%length,
						surface.getWidth(), (int)(surface.getHeight()*0.5f) + i*length + (int) (move.y)%length);
			}
		}
	}
	public void renderGrid(Vector2D vec)
	{
		if(isActivated)
		{
			buffG.setColor(Color.LIGHT_GRAY);
			PointInt n = new PointInt((int) ((float)(surface.getWidth())/length + 0.5f),
									(int) ((float)(surface.getHeight())/length + 0.5f));
			for(int i=-(int)(n.x*0.5f +0.5f); i<(int)(n.x*0.5f + 1.0f); i++)
			{
				buffG.drawLine((int)(surface.getWidth()*0.5f) + i*length + (int) (move.x + vec.x - surface.getWidth()*0.5f)%length, 0,
						(int)(surface.getWidth()*0.5f) + i*length +(int) (move.x + vec.x - surface.getWidth()*0.5f)%length, surface.getHeight());
			}
			for(int i=-(int)(n.y*0.5f + 0.5f); i<(int)(n.y*0.5f + 1.0f); i++)
			{
				buffG.drawLine(0, (int)(surface.getHeight()*0.5f) + i*length + (int) (move.y + vec.y - surface.getHeight()*0.5f)%length,
						surface.getWidth(), (int)(surface.getHeight()*0.5f) + i*length + (int) (move.y + vec.y - surface.getHeight()*0.5f)%length);
			}
		}
	}
	public Vector2D gridRound(Vector2D mousePos, Vector2D vec)
	{
		if(isActivated)
		{
			//calcul du décalage par translation
			//il suffit de calculer le modulo du décalage
			
			/*int x0 = (int)(vec.x - ((int)(vec.x/length))*length);
			int y0 = (int)(vec.y - ((int)(vec.y/length))*length);*/
			
			Vector2D l_mousePos = new Vector2D();
			Vector2D l_translate = new Vector2D((int)(vec.x)%length, (int)(vec.y)%length);
			PointInt l_moveInt = new PointInt((int) (move.x)%length, (int) (move.y)%length);
			l_mousePos.x = (int)(((mousePos.x - l_moveInt.x - l_translate.x)/length) + 0.5f)*length + l_translate.x + l_moveInt.x;
			l_mousePos.y = (int)((int) (((mousePos.y - l_moveInt.y - l_translate.y)/length) + 0.5f)*length) + l_translate.y +  l_moveInt.y;
			return l_mousePos.sub(vec);
		}
		else
			return mousePos.sub(vec);
	}
}
 