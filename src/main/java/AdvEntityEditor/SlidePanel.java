package AdvEntityEditor;
import java.awt.* ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import Addons.AdvAnimation;
import Addons.AdvEntity;
import Addons.Animation;
import Addons.Key;
import Addons.KeyBool;
import Addons.KeyFloat;
import Addons.KeyInt;
import BaseWindows.Grid;
import Maths.Form;
import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;

public class SlidePanel extends JScrollPane implements ActionListener, MouseListener, MouseMotionListener, FocusListener, Runnable, KeyListener
{
	private KeyContainer[] keyContainer;
	
	private BufferedImage surfKeys;
	private JLabel surfLabel;
	private Graphics surfaceKG;
	protected SpringLayout layout;
	protected Container container;
	private boolean isInitialized;
	private PointInt SIZE = new PointInt(190, 320);
	private PointInt LENGTH = new PointInt(190, 35);

	private JTextField timeField, maxTimeField;
	private JButton playB;
	private Form triangle;
	
	private Boolean isPlaying;
	private Thread runner;
	
	private Vector2D translate;
	private float scale;
	
	private AdvEntity treeAdvEntity;
	
	//private ArrayList<AdvEntity> actualTree;
	private ObjectTree objectsTree;
	private AdvAnimList advAnimList;
	
	private int selectedKey;
	private int selectedLine;
	private boolean isDraggingKey;

	private BufferedImage surfEntity;
	private Graphics surfG;
	
	private JDialog parent;
	private Grid grid;
	
	public SlidePanel(int width, int height, JDialog parent)
	{
		isInitialized = false;
		this.parent = parent;
		
		translate = new Vector2D();
		scale = 1.0f;
		
		runner = null;
		isPlaying = false;
		
		selectedKey = -1;
		selectedLine = -1;
		isDraggingKey = false;
				
		layout = new SpringLayout();
		container = new Container();
		container.setLayout(layout);
		setPreferredSize(new Dimension(width, height));
		
		timeField = new JTextField("0.0", 5);
		maxTimeField = new JTextField("150.0", 5);
		playB = new JButton(" Play ");
		
		keyContainer = new KeyContainer[8];
		keyContainer[0] = new KeyFloatContainer("Pos X", LENGTH);
		keyContainer[1] = new KeyFloatContainer("Pos Y", LENGTH);
		keyContainer[2] = new KeyFloatContainer("Scale", LENGTH);
		((KeyFloatContainer)keyContainer[2]).setText("1.00");
		keyContainer[3] = new KeyFloatContainer("Rot", LENGTH);
		
		keyContainer[4] = new KeyIntContainer("Anim", LENGTH);
		
		keyContainer[5] = new KeyBoolContainer("SymV  ", LENGTH);
		keyContainer[6] = new KeyBoolContainer("SymH  ", LENGTH);
		keyContainer[7] = new KeyFloatContainer("Visible", LENGTH);
		((KeyFloatContainer)keyContainer[7]).setText("100.00");
		
		surfKeys = new BufferedImage(width - SIZE.x - 20, SIZE.y - 5, BufferedImage.TYPE_INT_RGB);
		surfLabel = new JLabel(new ImageIcon(surfKeys));
		surfLabel.addMouseMotionListener(this);
		surfLabel.addMouseListener(this);
		surfLabel.setPreferredSize(new Dimension(width - SIZE.x - 20, SIZE.y - 5));
		surfaceKG = surfKeys.getGraphics();
		
		layout.putConstraint(SpringLayout.WEST, maxTimeField, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, maxTimeField, 5, SpringLayout.NORTH, this);
		container.add(maxTimeField);
		
		layout.putConstraint(SpringLayout.WEST, timeField, LENGTH.x - timeField.getPreferredSize().width, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, timeField, 5, SpringLayout.NORTH, this);
		container.add(timeField);
		
		layout.putConstraint(SpringLayout.WEST, playB, 5, SpringLayout.EAST, maxTimeField);
		layout.putConstraint(SpringLayout.NORTH, playB, 5, SpringLayout.NORTH, this);
		container.add(playB);
		
		timeField.addFocusListener(this);
		maxTimeField.addFocusListener(this);
		
		layout.putConstraint(SpringLayout.WEST, keyContainer[0], 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, keyContainer[0], LENGTH.y, SpringLayout.NORTH, this);
		container.add(keyContainer[0]);

		keyContainer[0].buttonAdd.addActionListener(this);
		keyContainer[0].buttonSupp.addActionListener(this);
		keyContainer[0].buttonApply.addActionListener(this);
		
		for(int i=1; i<keyContainer.length; i++)
		{
			layout.putConstraint(SpringLayout.WEST, keyContainer[i], 5, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, keyContainer[i], 0, SpringLayout.SOUTH, keyContainer[i-1]);
			
			container.add(keyContainer[i]);
			
			keyContainer[i].buttonAdd.addActionListener(this);
			keyContainer[i].buttonSupp.addActionListener(this);
			keyContainer[i].buttonApply.addActionListener(this);
		}
		
		layout.putConstraint(SpringLayout.WEST, surfLabel, 5, SpringLayout.EAST, keyContainer[0]);
		layout.putConstraint(SpringLayout.NORTH, surfLabel, 0, SpringLayout.NORTH, this);
		container.add(surfLabel);
		container.setPreferredSize(new Dimension(width - 20, SIZE.y));
		
		triangle = new Form(3);
		triangle.setPoint(0, new Vector2D(-10,15));
		triangle.setPoint(1, new Vector2D(10,15));
		triangle.setPoint(2, new Vector2D(0,30));
		
		objectsTree = null;
		advAnimList = null;
		
		timeField.addKeyListener(this);
		maxTimeField.addKeyListener(this);
		playB.addActionListener(this);
		
		render();
		this.getViewport().add(container);
		this.getVerticalScrollBar().setUnitIncrement(16);
	}
	public void initialize(AdvEntity treeAdvEntity, ObjectTree objectsTree, AdvAnimList advAnimList,
			BufferedImage surfEntity, Grid grid)
	{
		this.treeAdvEntity = treeAdvEntity;
		this.objectsTree = objectsTree;
		this.advAnimList = advAnimList;
		this.surfEntity = surfEntity;
		this.surfG = surfEntity.getGraphics();
		this.grid = grid;
		isInitialized = true;
		
		translate.set(surfEntity.getWidth()*0.5f, surfEntity.getHeight()*0.5f);
	}
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int clicked = e.getClickCount();
		
		if(clicked == 2 && selectedLine != -1 && selectedKey != -1)
		{
			AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
			int selectedAnim = advAnimList.getSelected();
			ArrayList<? extends Key> keys = advAnim.getKeys(selectedLine, selectedAnim);
			if(selectedAnim != -1)
			{
				keys.get(selectedKey).isLinear = !keys.get(selectedKey).isLinear;
			}
		}
		update();
		render();
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		
	}
	@Override
	public void mouseExited(MouseEvent e) 
	{
		
	}
	@Override
	public void mousePressed(MouseEvent e) 
	{	
		if(runner != null)
			return;
		if(advAnimList.getSelected() == -1)
			return;

		int y = e.getY();
		if(y < LENGTH.y)
		{
			isDraggingKey = false;
			selectedKey = -1;
			moveTime(e);
		}
		else
		{
			moveTime(e);
			int x = e.getX();
			float l_maxTime =  Float.parseFloat(maxTimeField.getText()) ;
			float time = (x * l_maxTime)/((float)surfKeys.getWidth()) ;
			if(time > l_maxTime)
				time = l_maxTime;
			
			//on récupère la série de clé sur la ligne de clique
			ArrayList<? extends Key> keys = getKeysSelectedLine(e);
			if(keys == null)
				return;
			
			isDraggingKey(keys, l_maxTime, x);
			if(isDraggingKey) //une clé est sélectionnée
			{
				if(e.getButton() == 3) //On supprime la clé
				{
					keys.remove(selectedKey);
					selectedKey = -1;
					selectedLine = -1;
					isDraggingKey = false;
				}
			}		
		}
		update();
		render();
		repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if(runner != null)
			return;
		if(advAnimList.getSelected() == -1)
			return;
		int y = e.getY();
		if(y < LENGTH.y)
		{
			moveTime(e);
			selectedKey = -1;
			selectedLine = -1;
			isDraggingKey = false;
		}
		else
		{
			moveTime(e);
			if(isDraggingKey) //on déplace une clé
			{
				int x = e.getX();
				float l_maxTime =  Float.parseFloat(maxTimeField.getText()) ;
				float time = (float)((int)(100*(x * l_maxTime)/((float)surfKeys.getWidth())))/100;
				if(time > l_maxTime)
					time = l_maxTime;
				
				//on récupère la série de clé sur la ligne de clique
				//ArrayList<? extends Key> keys = getKeysSelectedLine(e);
				AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
				int selectedAnim = advAnimList.getSelected();
				ArrayList<? extends Key> keys = advAnim.getKeys(selectedLine, selectedAnim);
				if(keys != null && keys.size() != 0)
				{
					boolean isLinear = keys.get(selectedKey).isLinear;
					switch(AdvAnimation.getOption(selectedLine))
					{
						case 0:
							float valueF = ((ArrayList<KeyFloat>) keys).get(selectedKey).value;
							keys.remove(selectedKey);
							addKey(time, keys, valueF, isLinear);
							break;
						case 1:
							int valueI = ((ArrayList<KeyInt>) keys).get(selectedKey).value;
							keys.remove(selectedKey);
							addKey(time, keys, valueI, isLinear);
							break;
						case 2:
							boolean valueB = ((ArrayList<KeyBool>) keys).get(selectedKey).value;
							keys.remove(selectedKey);
							addKey(time, keys, valueB, isLinear);
							break;
					}
				}
			}
		}
		update();
		render();
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
		int selectedAnim = advAnimList.getSelected();
		if(selectedAnim != -1)
		{
			if(source == playB)
			{
				managedPlay();
				return;
			}
			if(runner != null)
				return;
			for(int i=0; i<keyContainer.length; i++)
			{
				if(source == keyContainer[i].buttonAdd)
				{
					selectedLine = i;
					ArrayList<? extends Key> keys = advAnim.getKeys(selectedLine, selectedAnim);
					float time = Float.parseFloat(timeField.getText()) ;
					addKey(time, keys, null, true);
					update();
					render();
					repaint();
					return;
				}
				else if(source == keyContainer[i].buttonSupp)
				{
					selectedLine = i;
					ArrayList<? extends Key> keys = advAnim.getKeys(selectedLine, selectedAnim);
					if(selectedKey != -1)
					{
						keys.remove(selectedKey);
						selectedKey = -1;
						selectedLine = -1;
						isDraggingKey = false;
					}
					update();
					render();
					repaint();
					return;
				}
				else if(source == keyContainer[i].buttonApply)
				{
					if(selectedLine == -1 || selectedKey == -1)
						break;
					
					ArrayList<? extends Key> keys = advAnim.getKeys(selectedLine, selectedAnim);
					boolean isLinear = keys.get(selectedKey).isLinear;
					if(selectedKey != -1)
					{
						float time = Float.parseFloat(timeField.getText()) ;
						switch(AdvAnimation.getOption(selectedLine))
						{
							case 0:
								keys.remove(selectedKey);
								addKey(time, keys, null, isLinear);
								break;
							case 1:
								keys.remove(selectedKey);
								addKey(time, keys, null, isLinear);
								break;
							case 2:
								keys.remove(selectedKey);
								addKey(time, keys, null, isLinear);
								break;
						}
						update();
						render();
						repaint();
						return;
					}
				}

			}
		}
		
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
		
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		update();
		render();
	}
	
	public void moveTime(MouseEvent e)
	{
		int x = e.getX(); 
		float l_maxTime =  Float.parseFloat(maxTimeField.getText()) ;
		float time = ((x) * l_maxTime)/((float)surfKeys.getWidth()) ;
		if(x > surfKeys.getWidth())
		{
			timeField.setText(l_maxTime + "");
		}
		else if(x < 0)
			timeField.setText(0 + "");
		else
		{
			timeField.setText(String.format("%.2f", time, 1));
		}
	}
	public void isDraggingKey(ArrayList<? extends Key> keys, float l_maxTime, int x)
	{
		selectedKey = -1;
		isDraggingKey = false;
		for(int i=0; i<keys.size(); i++)
		{
			int posKey = (int) ((keys.get(i).time * surfKeys.getWidth())/l_maxTime);
			if(x > posKey - 15 && x < posKey + 15)
			{
				selectedKey = i;
				isDraggingKey = true;
				timeField.setText(keys.get(i).time + "");
				return;
			}
			else if(x < posKey)
				break;
		}
		return;
	}
	
	public void addKey(float time, ArrayList<? extends Key> keys, Object value, boolean isLinear)
	{
		int between = 0;
		int size = keys.size();
		//determine la position de la clé a ajouter
		if(size > 1)
		{
			for(between = 0; between < size; between++)
			{
				if(time < keys.get(between).time)
					break;
			}
		}
		else if(size == 1)
		{
			
			if(time > keys.get(between).time)
				between = 1;
		}
		if(value != null)
			selectedKey = between;
		switch(AdvAnimation.getOption(selectedLine))
		{
			//3 possibilités: -float, int, bool
			case 0:
				KeyFloatContainer keyValueFloat = (KeyFloatContainer)keyContainer[selectedLine];
				ArrayList<KeyFloat> keysFloat = (ArrayList<KeyFloat>) keys;
				KeyFloat keyF = new KeyFloat();
				keyF.time = time;
				keyF.isLinear = isLinear;
				if(value == null)
					keyF.value = Float.parseFloat(keyValueFloat.getText());
				else
					keyF.value = (float)value;
				keysFloat.add(between, keyF);
				break;
			case 1:
				KeyIntContainer keyValueInt = (KeyIntContainer)keyContainer[selectedLine];
				ArrayList<KeyInt> keysInt = (ArrayList<KeyInt>) keys;
				KeyInt keyI = new KeyInt();
				keyI.time = time;
				keyI.isLinear = isLinear;
				if(value == null)
					keyI.value = keyValueInt.getSelected();
				else
					keyI.value = (int)value;
				keysInt.add(between, keyI);
				break;
			case 2:
				KeyBoolContainer keyValueBool = (KeyBoolContainer)keyContainer[selectedLine];
				ArrayList<KeyBool> keysBool = (ArrayList<KeyBool>) keys;
				KeyBool keyB = new KeyBool();
				keyB.time = time;
				keyB.isLinear = isLinear;
				if(value == null)
					keyB.value = keyValueBool.isSelected();
				else
					keyB.value = (boolean)value;
				keysBool.add(between, keyB);
				break;
			default:
				System.out.println("Error");
		}
	}
	public ArrayList<? extends Key> getKeysSelectedLine(MouseEvent e)
	{
		selectedLine = -1;
		int x = e.getX();
		int y = e.getY();
		selectedLine = (y - LENGTH.y)/(LENGTH.y);
		if(selectedLine >= AdvAnimation.OPTIONS)
		{
			selectedLine = -1;
			return null;
		}
		
		AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
		int selectedAnim = advAnimList.getSelected();
		ArrayList<? extends Key> keys = advAnim.getKeys(selectedLine, selectedAnim);
		return keys;
	}
	
	public void load()
	{
		if(isInitialized)
		{
			((KeyIntContainer) keyContainer[4]).clearCombo();
			int selectedAnim = advAnimList.getSelected();
			AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
			
			if(selectedAnim != -1)
			{
				maxTimeField.setText(String.format("%.2f", advAnim.getDuration(selectedAnim)));
				
				Animation anim = advAnim.getAdvEntity().getAnim();
				for(int i=0; i<anim.size(); i++)
				{
					((KeyIntContainer) keyContainer[4]).addItem(anim.getName(i));
				}
			}
		}
	}
	public void update(float dt)
	{
		if(advAnimList.getSelected() == -1)
			return;
		if(isInitialized)
		{
			int selectedAnim = advAnimList.getSelected();
			AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
			
			if(advAnim != null)
			{
				treeAdvEntity.update(dt, new Vector2D(1,0), new Vector2D(), 1);
				this.treeAdvEntity.setScale(scale * this.treeAdvEntity.getScale());
				timeField.setText(String.format("%.2f", advAnim.getCurrentTime(), 1));
				
				AdvEntity advEntity = advAnim.getAdvEntity();
				
				((KeyFloatContainer) keyContainer[0]).setText( String.format("%.2f", advAnim.getPosX()));
				((KeyFloatContainer) keyContainer[1]).setText( String.format("%.2f", advAnim.getPosY()));
				((KeyFloatContainer) keyContainer[2]).setText( String.format("%.2f",advAnim.getScale()));
				((KeyFloatContainer) keyContainer[3]).setText( String.format("%.2f",advAnim.getRot()));
				((KeyIntContainer) keyContainer[4]).setSelected(advEntity.getAnim().getCurrentAnim());
				((KeyBoolContainer) keyContainer[5]).setSelected(advAnim.getSymV());
				((KeyBoolContainer) keyContainer[6]).setSelected(advAnim.getSymH());
				((KeyFloatContainer) keyContainer[7]).setText( String.format("%.2f", advAnim.getVisible()));
				
			}
		}
	}
	public void update()
	{
		if(runner != null)
			return;
		
		float l_maxTime =  Float.parseFloat(maxTimeField.getText()) ;
		float l_time = Float.parseFloat(timeField.getText()) ;
		if(l_time > l_maxTime)
		{
			l_time = l_maxTime;
			timeField.setText(l_maxTime + "");
		}
		if(isInitialized)
		{
			AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
			int selected = advAnimList.getSelected();
			
			if(advAnim != null && selected != -1)
			{
				advAnim.setCurrentAdvAnim(selected);
				
				treeAdvEntity.reset();
				treeAdvEntity.resetAdvAnim();
				advAnim.get().duration = l_maxTime;
				treeAdvEntity.setTime(l_time);
				treeAdvEntity.update(0, new Vector2D(1,0), new Vector2D(), 1);
				this.treeAdvEntity.setScale(scale * this.treeAdvEntity.getScale());
				
				AdvEntity advEntity = advAnim.getAdvEntity();
				
				((KeyFloatContainer) keyContainer[0]).setText( String.format("%.2f", advAnim.getPosX()));
				((KeyFloatContainer) keyContainer[1]).setText( String.format("%.2f", advAnim.getPosY()));
				((KeyFloatContainer) keyContainer[2]).setText( String.format("%.2f",advAnim.getScale()));
				((KeyFloatContainer) keyContainer[3]).setText( String.format("%.2f",advAnim.getRot()));
				((KeyIntContainer) keyContainer[4]).setSelected(advEntity.getAnim().getCurrentAnim());
				((KeyBoolContainer) keyContainer[5]).setSelected(advAnim.getSymV());
				((KeyBoolContainer) keyContainer[6]).setSelected(advAnim.getSymH());
				((KeyFloatContainer) keyContainer[7]).setText( String.format("%.2f", advAnim.getVisible()));
			}
		}
	}
	public void render()
	{
		renderBack(true);
		
		if(isInitialized)
		{
			clearScreen();
			if(treeAdvEntity.getAdvEntities().size() != 0)
			{
				float l_maxTime =  Float.parseFloat(maxTimeField.getText());
				AdvAnimation advAnim = advAnimList.getCurrentAdvAnim();
				int selectedAnim = advAnimList.getSelected();
				if(selectedAnim != -1)
				{
					for(int i=0; i<AdvAnimation.OPTIONS; i++)
					{
						ArrayList<? extends Key> keys = advAnim.getKeys(i, selectedAnim);
						if(keys.size() > 0 && AdvAnimation.getOption(i) == 0)
						{
							surfaceKG.setColor(new Color(150,0,0,125));
							surfaceKG.fillRect(0 ,(int) (LENGTH.y*(i+1)), (int)(((float)(surfKeys.getWidth() * keys.get(0).time))/(l_maxTime)),(int) LENGTH.y);
						}
						
						for(int j=0; j<keys.size(); j++)
						{
							if(keys.get(j).time < l_maxTime)
							{
								if(i == selectedLine && j == selectedKey)
								{
									surfaceKG.setColor(Color.CYAN);
								}
								else
								{
									surfaceKG.setColor(Color.orange);
								}
								PointInt pos = new PointInt();
								pos.x = (int)(((float)(surfKeys.getWidth() * keys.get(j).time))/(l_maxTime));
								pos.y = (int) (LENGTH.y*(i+1.5f));
								
								if(keys.get(j).isLinear)
								{
									surfaceKG.fillOval(pos.x - 5, pos.y - 5, 10, 10);
								}
								else
								{
									surfaceKG.fillRect(pos.x - 5, pos.y - 5, 10, 10);
								}
							}
						}
					}
					this.treeAdvEntity.draw(translate);
				}
				grid.renderGrid(translate);
			}
		}
		parent.repaint();
	}
	public void clearScreen()
	{
		surfG.setColor(Color.WHITE);
		surfG.fillRect(0,0,surfEntity.getWidth(), surfEntity.getHeight());
	}
	public void renderBack()
	{
		renderBack(false);
	}
	public void renderBack(boolean only)
	{
		surfaceKG.setColor(Color.darkGray);
		surfaceKG.fillRect(0, LENGTH.y, surfKeys.getWidth(), surfKeys.getHeight());
		
		surfaceKG.setColor(Color.BLACK);
		surfaceKG.fillRect(0, 0, surfKeys.getWidth(), LENGTH.y);
		
		surfaceKG.setColor(Color.ORANGE);
		String text = maxTimeField.getText();
		float l_maxTime = 0;
		if(!text.isEmpty())
			l_maxTime = Float.parseFloat(text);
		else 
			return;
		
		text = timeField.getText();
		float l_time = 0;
		if(!text.isEmpty())
			l_time = Float.parseFloat(timeField.getText());
		else
			return;
		
		surfaceKG.drawString("0", 1, 30);
		String l_sMax = l_maxTime + "";
		surfaceKG.drawString(l_sMax, surfKeys.getWidth() - 8*(l_sMax.length() + 2), 30);
		
		//draw Triangle
		float l_vecX = (l_time*surfKeys.getWidth())/l_maxTime;
		triangle.translate(new Vector2D(l_vecX,0));
		surfaceKG.fillPolygon(triangle.getXIntArray(), triangle.getYIntArray() ,3);
		triangle.translate(new Vector2D(-l_vecX,0));
		
		surfaceKG.drawLine((int) l_vecX, LENGTH.y, (int) l_vecX, surfKeys.getHeight());
		//draw lines
		for(int i=0; i<keyContainer.length + 1; i++)
		{
			surfaceKG.drawLine(0, LENGTH.y*(i+1), surfKeys.getWidth(), LENGTH.y*(i+1));
		}
		if(!only)
			this.repaint();
	}
	
	public Vector2D getTranslate()
	{
		return translate;
	}
	public void setScale(float scale)
	{
		if(scale > 1.0)
			this.scale = scale;
		else
			this.scale = 1.0f;
	}
	public float getScale()
	{
		return scale;
	}
	
	public boolean getIsPlaying()
	{
		return isPlaying;
	}
	@Override
	public void run()
	{
		while(isPlaying)
		{
			update(0.030f);
			render();
			try 
			{
				Thread.sleep(30);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void managedPlay()
	{
		if(runner == null)
		{
			playB.setText("Pause");
			isPlaying = true;
			runner = new Thread(this);
			runner.start();
		}
		else
		{
			isPlaying = false;
			while(runner.isAlive())
			{
				try 
				{
					Thread.sleep(10);
				} catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
			}
			runner = null;
			playB.setText("Play");
		}
	}
	public void closeRunner()
	{
		if(runner != null)
			managedPlay();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{

	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			update();
			render();
		}
	}
	@Override
	public void keyTyped(KeyEvent e)
	{
	}
}
