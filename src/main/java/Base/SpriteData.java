package Base;
import java.awt.image.BufferedImage;

import Maths.Vector2D;
import Maths.sRectangle;

public class SpriteData implements java.io.Serializable
{
	public final static long serialVersionUID = 6787545941574381914L;
	
	public BufferedImage texture;
	public String textureName;
	public sRectangle rect;

	public boolean flipH, flipV;
	
	public SpriteData()
	{
		this.flipH = false;
		this.flipV = false;
		this.rect = new sRectangle(0, 0, 0, 0);
		this.texture = null;
		this.textureName = "";
	}
}
