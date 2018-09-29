package Base;
import javax.swing.*;

import Maths.PointInt;

import java.awt.*;
import java.awt.event.*;

public class Input extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final int KEYS_ARRAY_LEN = 256;
	
	//bit flags
	protected final int KEYS_DOWN = 1;//0001
	protected final int KEYS_PRESSED = 2;//0010
	protected final int MOUSE = 4;//0100
	protected final int TEXT_IN = 8;//1000
	protected final int KEY_MOUSE_TEXT = 15;//1111
	
	protected boolean[] isKeysDown;
	protected boolean[] isKeysPressed;
	protected String textIn;
	protected char charIn;
	protected boolean isNewLine;
	protected PointInt mouse;
	protected PointInt start;
	protected boolean isMouseCaptured;
	protected boolean isMouseLButton;
	protected boolean isMouseMButton;
	protected boolean isMouseRButton;
	protected boolean isMouseX1Button;
	protected boolean isMouseX2Button;
	protected boolean isMouseDragging;
	protected boolean isKeyCaptured;
	
	//add controller
	Input()
	{
		isKeysDown = new boolean[KEYS_ARRAY_LEN];
		isKeysPressed = new boolean[KEYS_ARRAY_LEN];
		for(int i=0; i<KEYS_ARRAY_LEN; i++)
		{
			isKeysDown[i] = false;
			isKeysPressed[i] = false;
		}
		textIn = new String("");
		charIn = ' ';
		isNewLine = false;
		mouse = new PointInt();
		start = new PointInt();
		isMouseCaptured = false;
		isMouseLButton = false;
		isMouseMButton = false;
		isMouseRButton = false;
		isMouseX1Button = false;
		isMouseX2Button = false;
		isMouseDragging = false;
		isKeyCaptured = false;
	}
	
	public void initialize(boolean isMouseCaptured)
	{
		this.isMouseCaptured = isMouseCaptured;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(!isMouseCaptured)
			return;
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		if(!isMouseCaptured)
			return;
		mouse.set(e.getX(), e.getY());
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		if(!isMouseCaptured)
			return;
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if(!isMouseCaptured)
			return;
		if((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
			isMouseLButton = true;
		if((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
			isMouseMButton = true;
		if((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			isMouseRButton = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(!isMouseCaptured)
			return;
		isMouseDragging = false;
		if((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
			isMouseLButton = false;
		if((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
			isMouseMButton = false;
		if((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			isMouseRButton = false;
	}

	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		int code = arg0.getKeyCode();
		if(code < KEYS_ARRAY_LEN)
		{
			isKeysPressed[code] = true;
			isKeysDown[code] = true;
		}
		if(isKeyCaptured && code == KeyEvent.VK_ENTER)
			isNewLine = true;
	}

	@Override
	public void keyReleased(KeyEvent arg0) 
	{
		int code = arg0.getKeyCode();
		if(code < KEYS_ARRAY_LEN)
			isKeysPressed[code] = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		if(!isKeyCaptured)
			return;
		if(isNewLine)
		{
			textIn = "";
			isNewLine = false;
		}
		if(arg0.getKeyChar() == '\b')
		{
			if(textIn.length()>0)
				textIn = textIn.substring(0, textIn.length()-1);
		}
		else
		{
			charIn = arg0.getKeyChar();
			textIn = textIn + charIn;
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		if(!isMouseCaptured)
			return;
		if(!isMouseDragging)
		{
			isMouseDragging = true;
			start.set(mouse.x, mouse.y);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		if(!isMouseCaptured)
			return;
		mouse.set(arg0.getX(), arg0.getY());
	}
	
	
	//Get
	public boolean wasKeyPressed(int code) {assert(code < this.KEYS_ARRAY_LEN); return isKeysPressed[code];}
	public boolean isKeyDown(int code) {assert(code < this.KEYS_ARRAY_LEN); return isKeysDown[code];}
	public boolean anyKeyPressed()
	{
		for(int i=0; i<isKeysPressed.length; i++)
		{
			if(isKeysPressed[i])
				return true;
		}
		return false;
	}
	public int getMouseX() {return mouse.x;}
	public int getMouseY() {return mouse.y;}
	public boolean getMouseLButton() {return isMouseLButton;}
	public boolean getMouseMButton() {return isMouseMButton;}
	public boolean getMouseRButton() {return isMouseRButton;}
	
	//set
	public void clearKeyPressed(int code) {assert(code < this.KEYS_ARRAY_LEN);  this.isKeysPressed[code] = false;}
	public void clear(int flag)
	{
		if((flag & KEYS_PRESSED) != 0)
			for(int i=0; i<isKeysPressed.length; i++)
				isKeysPressed[i] = false;
		
		if((flag & KEYS_DOWN) != 0)
			for(int i=0; i<isKeysDown.length; i++)
				isKeysDown[i] = false;
		
		if((flag & TEXT_IN) != 0)
			for(int i=0; i<isKeysDown.length; i++)
				isKeysDown[i] = false;
		
	}
	public void clearAll() {clear(KEY_MOUSE_TEXT);}
	public void setMouseLButton(boolean b) {isMouseLButton = b;}
	public void setMouseMButton(boolean b) {isMouseMButton = b;}
	public void setMouseRButton(boolean b) {isMouseRButton = b;}
	
}


