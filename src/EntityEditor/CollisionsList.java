package EntityEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Addons.AdvForm;
import Addons.Animation;
import Addons.Entity;
import BaseWindows.Grid;
import BaseWindows.ListPerso;
import Maths.Edge;
import Maths.Form;
import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;

@SuppressWarnings("serial")
public class CollisionsList extends ListPerso implements ActionListener, FocusListener, ListSelectionListener, MouseListener, MouseMotionListener
{
	private Animation anim;
	private JScrollPane buffScrollPane;
	private Graphics buffG;
	private BufferedImage surfEntity;
	private ArrayList<AdvForm> forms;
	private ArrayList<AdvForm> copyForms;
	
	private FramesList framesList;
	private AnimList animList;
	private Grid grid;
	private int selectedPoint;
	private AFormDef aFormDef;
	private Modifying modif;
	private boolean isInitialized;
	
	public CollisionsList(JDialog frame, String labelName, int width, int height) 
	{
		super(frame, labelName, width, height);
		isInitialized = true;
		selectedPoint = -1;
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.addListSelectionListener(this);
		
		buttonAdd.setEnabled(false);
		buttonSup.setEnabled(false);
		
		buttonAdd.addActionListener(this);
		buttonSup.addActionListener(this);
		
		copyForms = new ArrayList<AdvForm>();
		this.forms = null;
	}
	public void initialize(Animation anim, BufferedImage surfEntity,
			JScrollPane buffScrollPane, AnimList animList, FramesList framesList,
			Grid grid, AFormDef aFormDef, Modifying modif)
	{
		this.modif = modif;
		this.grid = grid;
		this.buffScrollPane = buffScrollPane;
		this.anim = anim;
		this.surfEntity = surfEntity;
		this.buffScrollPane.addMouseListener(this);
		this.buffScrollPane.addMouseMotionListener(this);
		this.buffG = surfEntity.getGraphics();
		this.framesList = framesList;
		this.animList = animList;
		this.aFormDef = aFormDef;
	}
	public void clear()
	{
		copyForms.clear();
		super.clear();
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) 
	{
		isUsed();
		int selected = jlist.getSelectedIndex();
		if(selected == -1 || animList.getSelected() == -1 || framesList.getSelected() == -1)
		{
			endUsed();
			return;
		}
		selectedPoint = -1;
		if(selected != -1 && isInitialized)
		{
			Vector2D l_mousePos = new Vector2D(e.getX(), e.getY());
			Vector2D l_mousePosReal = new Vector2D(e.getX() - surfEntity.getWidth()*0.5f, e.getY() -surfEntity.getHeight()*0.5f);
			Vector2D l_vec = new Vector2D();
			AdvForm l_aForm = forms.get(selected);
			float distMax = Float.MAX_VALUE;
			for(int i=0; i < l_aForm.size(); i++)
			{
				l_vec.set(l_mousePosReal, l_aForm.get(i));
				float magnitude = l_vec.getMagnitude();
				if(magnitude < 15.0f && magnitude < distMax)
				{
					distMax = magnitude;
					selectedPoint = i;
				}
			}
			
			int selectedPoint2 = aFormDef.getSelectedPoint(l_mousePos, l_aForm);
			if(selectedPoint2 != -1)
				selectedPoint = selectedPoint2;
			
			if(selectedPoint == -1)
			{
				Vector2D vec = new Vector2D(surfEntity.getWidth()*0.5f, surfEntity.getHeight()*0.5f);
				if(e.getButton() == 1)
				{
					if(modif.weaponPos)
					{
						aFormDef.setWeaponPos(grid.gridRound(l_mousePos, vec));
						modif.weaponPos = false;
						modif.weaponVec = true;
					}
					else if(modif.weaponVec)
					{
						aFormDef.setWeaponVec(grid.gridRound(l_mousePos, vec));
						modif.weaponVec = false;
					}
					else
						forms.get(selected).addPoint(grid.gridRound(l_mousePos, vec));
				}
			}
			else if(selectedPoint > -1)
			{
				if(e.getButton() == 3)
				{
					forms.get(selected).removePoint(selectedPoint);
					selectedPoint = -1;
				}
			}
			endUsed();
			framesList.render();
		}
		
		endUsed();
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		
	}
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		isUsed();
		
		int selected = jlist.getSelectedIndex();
		if(selectedPoint != -1 && selected != -1)//-2, -3
		{
			Vector2D l_mousePos = new Vector2D(e.getX() - 8, e.getY() - 8);//8 taille point
			Vector2D vec = new Vector2D(surfEntity.getWidth()*0.5f, surfEntity.getHeight()*0.5f);
			if(selectedPoint < -1)
			{
				aFormDef.updateWeapon(grid.gridRound(l_mousePos, vec), selectedPoint);
			}
			else
			{
				forms.get(selected).setPoint(selectedPoint, grid.gridRound(l_mousePos, vec));
			}
			endUsed();
			framesList.render();
		}
		
		endUsed();
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		isUsed();
		
		Object source = e.getSource();
		int selected = jlist.getSelectedIndex();
		if(source == buttonAdd)
		{
			AdvForm l_form = new AdvForm();
			l_form.setScale(framesList.getScale());
			forms.add(l_form);
			addElements(listModel.getSize() + "");
			endUsed();
			jlist.setSelectedIndex(listModel.size() - 1);
		}
		else if(source == buttonSup)
		{
			int newSelected = selected - 1;
			forms.remove(selected);
			removeSelected();
			endUsed();
			if(!listModel.isEmpty())
				setSelected(newSelected);
		}
		endUsed();
	}
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		selectedPoint = -1;
		int selected = jlist.getSelectedIndex();
		if(jlist.isSelectionEmpty())
		{
			buttonSup.setEnabled(false);
		}
		else
		{
			buttonSup.setEnabled(true);
			framesList.render();
		}
		aFormDef.update(forms);
	}
	
	@Override
	public void focusGained(FocusEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		
		
	}

	public ArrayList<Form> getCopy()
	{
		isUsed();
		
		if(forms == null)
			return new ArrayList();
		
		ArrayList<Form> l_forms = new ArrayList<Form>(forms.size());
		int[] l_array = jlist.getSelectedIndices();
		if(jlist.isSelectionEmpty())
		{
			for(int i=0; i<forms.size(); i++)
			{
				copyForms.add(forms.get(i).clone());
			}
		}
		else
		{
			for(int i=0; i<l_array.length; i++)
			{
				System.out.println(l_array[i]);
				copyForms.add(forms.get(l_array[i]).clone());
			}
		}
		
		endUsed();
		return l_forms;
	}
	
	public void update()
	{
		isUsed();
		
		listModel.clear();
		if(anim.getCurrentFrame() != -1)
		{
			forms = anim.getAForms();
			for(int i=0; i<forms.size(); i++)
			{
				listModel.addElement(i + "");
			}
			buttonAdd.setEnabled(true);
		}
		aFormDef.update(forms);
		
		endUsed();
	}
	public void deselect()
	{
		super.deselect();
	}
	public void render()
	{
		isUsed();
		
		grid.renderGrid();
		
		if(forms != null && forms.size() != 0 ) 
		{
			Vector2D vec = new Vector2D(surfEntity.getWidth()*0.5f, surfEntity.getHeight()*0.5f);
			int selected = jlist.getSelectedIndex();
			if(!jlist.isSelectionEmpty())
			{
				forms.get(selected).setScale(framesList.getScale());
				ArrayList<Vector2D> l_points = forms.get(selected).getPointsWorld();
				buffG.setColor(Color.red);
				for(int i=0; i<l_points.size(); i++)
				{
					//buffG.drawLine((int) (l_points.get(i).x), (int) (l_points.get(i).y), (int) (l_points.get((i+1)%l_points.size()).x), (int) (l_points.get((i+1)%l_points.size()).y));
					buffG.fillOval((int) (l_points.get(i).x + vec.x + 0.5f) - 5, (int) (l_points.get(i).y + vec.y + 0.5f) - 5, 10, 10);
				}
				if(selectedPoint > -1)// &&  selectedPoint < l_points.size())
					buffG.fillOval((int) (l_points.get(selectedPoint).x + vec.x + 0.5f) - 8, (int) (l_points.get(selectedPoint).y + vec.y + 0.5f) - 8, 16, 16);
				
				
				//forms.get(selected).triangulate();
				ArrayList<Form> l_forms = new ArrayList();//
				l_forms = forms.get(selected).getConvexForms();
				
				if(l_forms.size() != 0)
				{
					for(int i=0; i<l_forms.size(); i++)
					{
						l_forms.get(i).draw(buffG, vec);
					}
				}
				else
				{
					forms.get(selected).draw(buffG, vec);
				}
			}
			else
			{
				buffG.setColor(Color.cyan);
				for(int j=0; j<listModel.size(); j++)
				{
					forms.get(j).setScale(framesList.getScale());
					ArrayList<Vector2D> l_points = forms.get(j).getPointsWorld();
					for(int i=0; i<l_points.size(); i++)
					{
						buffG.drawLine((int) (l_points.get(i).x + vec.x),
										(int) (l_points.get(i).y + vec.y),
										(int) (l_points.get((i+1)%l_points.size()).x + vec.x),
										(int) (l_points.get((i+1)%l_points.size()).y + vec.y));
						buffG.fillOval((int) (l_points.get(i).x + 0.5f + vec.x) - 5,
										(int) (l_points.get(i).y + 0.5f + vec.y) - 5,
										10,
										10);
					}
				}
			}
		}
		aFormDef.render();
		buffScrollPane.repaint();
		
		endUsed();
	}
	
	public void copy()
	{
		isUsed();
		
		if(forms == null)
			return;
		copyForms.clear();
		int[] l_array = jlist.getSelectedIndices();
		if(jlist.isSelectionEmpty())
		{
			for(int i=0; i<forms.size(); i++)
			{
				copyForms.add(forms.get(i).clone());
			}
		}
		else
		{
			for(int i=0; i<l_array.length; i++)
			{
				copyForms.add(forms.get(l_array[i]).clone());
			}
		}
		
		endUsed();
	}
	public void paste()
	{
		isUsed();
		
		if(forms == null)
			return;
		for(int i=0; i<copyForms.size(); i++)
		{
			anim.getAForms().add(copyForms.get(i).clone());
			listModel.addElement(anim.getAForms().size() - 1 + "");
		}
		endUsed();
	}
	public void paste(ArrayList<AdvForm> aforms)
	{
		isUsed();
		
		if(aforms == null)
			return;
		
		for(int i=0; i<aforms.size(); i++)
		{
			anim.getAForms().add(aforms.get(i).clone());
			listModel.addElement(anim.getAForms().size() - 1 + "");
		}
		
		endUsed();
	}
}
