package EntityEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Addons.Animation;
import Base.Image;
import BaseWindows.ListPerso;
import BaseWindows.Rendering;
import Maths.Form;
import Maths.Vector2D;


@SuppressWarnings("serial")
public class FramesList extends ListPerso implements ActionListener, FocusListener, ListSelectionListener, Rendering
{
	private Animation anim;
	private Graphics buffG;
	private BufferedImage surfEntity;
	private Select select;
	private ArrayList<Image> images;
	private int file;
	private JScrollPane buffScrollPane;
	private CollisionsList collisionsList;
	private JButton playButton;
	private FramesDialog framesDial;
	private boolean isInitialized;
	
	public FramesList(JDialog frame, String labelName, int width, int height) 
	{
		super(frame, labelName, width, height);
		isInitialized = false;
		framesDial = new FramesDialog(frame);
		images = new ArrayList<Image>();
		anim = null;
		file = 0;
		
		playButton = new JButton(" Play ");
		layout.putConstraint(SpringLayout.WEST, playButton, 3, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, playButton, 5, SpringLayout.SOUTH, listScroller);
		this.add(playButton);
		playButton.setEnabled(false);
		
		buttonAdd.setEnabled(false);
		buttonSup.setEnabled(false);
		
		//Listener
		jlist.addListSelectionListener(this);
		
		buttonAdd.addActionListener(this);
		buttonSup.addActionListener(this);
		playButton.addActionListener(this);
	}
	public boolean isInitialized()
	{
		return isInitialized;
	}
	public void initialize(Animation anim, BufferedImage surfEntity, JScrollPane buffScrollPane, Select select, CollisionsList collisionsList)
	{
		this.buffScrollPane = buffScrollPane;
		this.anim = anim;
		this.surfEntity = surfEntity;
		this.buffG = surfEntity.getGraphics();
		this.select = select;
		this.collisionsList = collisionsList;
		
		if(select.isInitialized())
			isInitialized = true;
	}
	public void clear()
	{
		anim.setCurrentFrame(-1);
		buttonAdd.setEnabled(false);
		buttonSup.setEnabled(false);
		images.clear();
		super.clear();
		deselect();
	}
	public void setFile(int file)
	{
		this.file = file;
	}

	public void deselect()
	{
		super.deselect();
		anim.setCurrentFrame(-1);
		collisionsList.deselect();
		collisionsList.update();
		render();
	}
	public void reloadImages()
	{
		//when combo is changed, need to reload images from new texture
		if(anim.getCurrentAnim() == -1 || !isInitialized)
			return;
		images.clear();
		if(anim.size() > 0)
		{
			for(int i=0; i<anim.getY() - anim.getX() + 1; i++)
			{
				images.add(select.getImage(file, anim.getX() + i));
				images.get(i).setGraphics(buffG);
				images.get(i).setSize(512);
				images.get(i).setDegrees(0);
				images.get(i).setPos(new Vector2D(surfEntity.getWidth()/2, surfEntity.getHeight()/2));
			}
		}
		collisionsList.update();
	}
	public void reload()
	{
		if(!isInitialized)
			return;
		clear();
		if(anim.getCurrentAnim() != -1)
		{
			reloadImages();
			addAll(anim.getX(), anim.getY());
		}
		deselect();
	}
	@Override
	public void render()
	{
		if(!isInitialized)
			return;
		//buffG.clearRect(0,0,buffG.getClipBounds().width, buffG.getClipBounds().height);
		buffG.setColor(Color.WHITE);
		buffG.fillRect(0, 0, surfEntity.getWidth(), surfEntity.getHeight());
		
		int selected = jlist.getSelectedIndex();
		if(selected != -1)
			images.get(selected).draw();
		else
			if(images.size() >= 1)
				images.get(0).draw();
			
		
		collisionsList.render();
	}
	public void copyCollisions()
	{
		if(!isInitialized)
			return;
		collisionsList.copy();
	}
	public void pasteCollisions()
	{
		if(!isInitialized)
			return;
		collisionsList.paste();
	}
	public ArrayList<Image> getImages()
	{
		if(!isInitialized)
			assert(false);
		return images;
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(!isInitialized)
			return;
		Object source = e.getSource();
		if(source == buttonAdd)
		{
			if(jlist.isSelectedIndex(listModel.getSize() - 1))
			{
				anim.addFrameTop();
				this.clear();
				this.addAll(anim.getX(), anim.getY());
				jlist.setSelectedIndex(listModel.getSize() - 1);
				listScroller.getVerticalScrollBar().setValue(listScroller.getVerticalScrollBar().getMaximum() - 2);
			}
			else if(jlist.isSelectedIndex(0))
			{
				if(anim.getX() - 1 >= 0)
					anim.addFrameBack();
				this.clear();
				this.addAll(anim.getX(), anim.getY());
				jlist.setSelectedIndex(0);
			}
		}
		else if(source == buttonSup)
		{
			
			if(jlist.isSelectedIndex(0))
			{
				anim.removeFrameBack();
				this.clear();
				this.addAll(anim.getX(), anim.getY());
				jlist.setSelectedIndex(0);
				
			}
			else if(jlist.isSelectedIndex(listModel.getSize() - 1))
			{
				if(anim.getY() - 1 >= 0)
					anim.removeFrameTop();
				this.clear();
				this.addAll(anim.getX(), anim.getY());
				jlist.setSelectedIndex(listModel.getSize() - 1);
				listScroller.getVerticalScrollBar().setValue(listScroller.getVerticalScrollBar().getMaximum() - 2);
			}
		}
		else if(source == playButton)
		{
			try 
			{
				framesDial.launchDialog(images, anim);
			} 
			catch (InterruptedException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		int selected = jlist.getSelectedIndex();
		if(selected == -1 || listModel.isEmpty())
		{
			buttonAdd.setEnabled(false);
			buttonSup.setEnabled(false);
			playButton.setEnabled(false);
		}
		else
			playButton.setEnabled(true);
		
		anim.setCurrentFrame(selected);
		
		if(!jlist.isSelectedIndex(0) && !jlist.isSelectedIndex(listModel.getSize() - 1))
		{
			buttonAdd.setEnabled(false);
			buttonSup.setEnabled(false);
		}
		else
		{
			if(jlist.isSelectedIndex(0))
			{
				if(anim.getX() - 1 >= 0)
					buttonAdd.setEnabled(true);
				else
					if(anim.getSizeFrames() == 1)
						buttonAdd.setEnabled(true);
			}
			else
				buttonAdd.setEnabled(true);
			
			if(anim.getX() == anim.getY())
				buttonSup.setEnabled(false);
			else
				buttonSup.setEnabled(true);
			
		}
		if(anim.isChangedAnim())
			reloadImages();
		if(anim.isChangedAnim() || anim.isChangedFrame())
			render();
		
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
		// TODO Auto-generated method stub
	}	@Override
	public void focusLost(FocusEvent e) 
	{
		// TODO Auto-generated method stub
	}

	public float getScale()
	{
		int selected = jlist.getSelectedIndex();
		if(selected != -1)
			return images.get(selected).getScale();
		else
			if(images.size() >= 1)
				return images.get(0).getScale();
		return 1;
	}
}
