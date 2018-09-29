package EntityEditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import Addons.AdvForm;
import Addons.Animation;
import Maths.Vector2D;
import Maths.Vector2D;

public class AFormDef extends Container implements ActionListener, FocusListener
{
	private JCheckBox weaponCheck, lifeCheck, blockingCheck, actionCheck, loopingCheck;
	private JLabel weaponLabel, lifeLabel, blockingLabel, actionLabel, loopingLabel;
	private JTextField lifeField;
	private JButton weaponVecB;
	protected SpringLayout layout;
	
	private FramesList framesList;
	private CollisionsList collisionsList;
	private Animation anim;
	private Modifying modif;
	
	private BufferedImage surfEntity;
	private Graphics buffG;
	private boolean isEnabled;
	
	public AFormDef(Modifying modif)
	{
		layout = new SpringLayout();
		this.setSize(300, 300);
		this.setLayout(layout);
		
		this.modif = modif;
		isEnabled = true;
		weaponCheck = new JCheckBox();
		lifeCheck = new JCheckBox();
		blockingCheck = new JCheckBox();
		actionCheck = new JCheckBox();
		loopingCheck = new JCheckBox();
		
		weaponCheck.addActionListener(this);
		lifeCheck.addActionListener(this);
		blockingCheck.addActionListener(this);
		actionCheck.addActionListener(this);
		loopingCheck.addActionListener(this);
		
		weaponLabel = new JLabel("Weapon");
		lifeLabel = new JLabel("Life");
		blockingLabel = new JLabel("Blocking");
		actionLabel = new JLabel("Action");
		loopingLabel = new JLabel("Looping");
		
		lifeField = new JTextField("100", 5);
		lifeField.setEnabled(false);
		lifeField.addFocusListener(this);
		
		weaponVecB = new JButton(" Vector ");
		weaponVecB.addActionListener(this);
		weaponVecB.setEnabled(false);
		
		//on ajoute
		//Weapon
		layout.putConstraint(SpringLayout.WEST, weaponCheck, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, weaponCheck, 5, SpringLayout.NORTH, this);
		this.add(weaponCheck);
		
		layout.putConstraint(SpringLayout.WEST, weaponLabel, 5, SpringLayout.EAST, weaponCheck);
		layout.putConstraint(SpringLayout.NORTH, weaponLabel, 5, SpringLayout.NORTH, this);
		this.add(weaponLabel);
		
		layout.putConstraint(SpringLayout.WEST, weaponVecB, 5, SpringLayout.EAST, weaponLabel);
		layout.putConstraint(SpringLayout.NORTH, weaponVecB, 2, SpringLayout.NORTH, this);
		this.add(weaponVecB);
		
		//life
		layout.putConstraint(SpringLayout.WEST, lifeCheck, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, lifeCheck, 14, SpringLayout.SOUTH, weaponCheck);
		this.add(lifeCheck);
		
		layout.putConstraint(SpringLayout.WEST, lifeLabel, 5, SpringLayout.EAST, lifeCheck);
		layout.putConstraint(SpringLayout.NORTH, lifeLabel, 14, SpringLayout.SOUTH, weaponCheck);
		this.add(lifeLabel);
		
		layout.putConstraint(SpringLayout.WEST, lifeField, 5, SpringLayout.EAST, weaponLabel);
		layout.putConstraint(SpringLayout.NORTH, lifeField, 7, SpringLayout.SOUTH, weaponCheck);
		this.add(lifeField);
		
		//blocking
		layout.putConstraint(SpringLayout.WEST, blockingCheck, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, blockingCheck, 10, SpringLayout.SOUTH, lifeCheck);
		this.add(blockingCheck);
		
		layout.putConstraint(SpringLayout.WEST, blockingLabel, 5, SpringLayout.EAST, weaponCheck);
		layout.putConstraint(SpringLayout.NORTH, blockingLabel, 10, SpringLayout.SOUTH, lifeCheck);
		this.add(blockingLabel);
		
		//action
		layout.putConstraint(SpringLayout.WEST, actionCheck, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, actionCheck, 10, SpringLayout.SOUTH, blockingCheck);
		this.add(actionCheck);
		
		layout.putConstraint(SpringLayout.WEST, actionLabel, 5, SpringLayout.EAST, weaponCheck);
		layout.putConstraint(SpringLayout.NORTH, actionLabel, 10, SpringLayout.SOUTH, blockingCheck);
		this.add(actionLabel);
		
		//looping
		layout.putConstraint(SpringLayout.WEST, loopingCheck, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, loopingCheck, 10, SpringLayout.SOUTH, actionCheck);
		this.add(loopingCheck);
		
		layout.putConstraint(SpringLayout.WEST, loopingLabel, 5, SpringLayout.EAST, weaponCheck);
		layout.putConstraint(SpringLayout.NORTH, loopingLabel, 10, SpringLayout.SOUTH, actionCheck);
		this.add(loopingLabel);
		
		framesList = null;
		enable(false);
	}
	public void initialize(Animation anim, FramesList framesList, CollisionsList collisionsList,
			BufferedImage surfEntity)
	{
		this.surfEntity = surfEntity;
		this.buffG = surfEntity.getGraphics();
		this.anim = anim;
		this.framesList = framesList;
		this.collisionsList = collisionsList;
	}
	public void enable(boolean b)
	{
		if(isEnabled != b)
		{
			isEnabled = b;
			weaponCheck.setEnabled(b);
			lifeCheck.setEnabled(b);
			blockingCheck.setEnabled(b);
			actionCheck.setEnabled(b);
			loopingCheck.setEnabled(b);
		}
	}
	public void reset()
	{
		weaponCheck.setSelected(false);
		lifeCheck.setSelected(false);
		blockingCheck.setSelected(false);
		actionCheck.setSelected(false);
		loopingCheck.setSelected(false);
		lifeField.setText("100");
		lifeField.setEnabled(false);
		weaponVecB.setEnabled(false);
	}
	public void update(ArrayList<AdvForm> forms)
	{
		modif.weaponPos = false;
		modif.weaponVec = false;
		int selected = collisionsList.getSelected();
		if(anim.getCurrentAnim() == -1 || anim.getCurrentFrame() == -1 || selected == -1)
		{
			enable(false);
			reset();
			return;
		}
		enable(true);
		AdvForm l_aForm = forms.get(selected);
		weaponCheck.setSelected(l_aForm.isWeapon());
		lifeCheck.setSelected(l_aForm.isHavingLife());
		blockingCheck.setSelected(l_aForm.isBlocking());
		actionCheck.setSelected(l_aForm.isAction());
		loopingCheck.setSelected(l_aForm.isLooping());
		lifeField.setText(l_aForm.getLife() + "");
		
		if(l_aForm.isWeapon())
			weaponVecB.setEnabled(true);
		else
			weaponVecB.setEnabled(false);
		
		if(l_aForm.isHavingLife())
			lifeField.setEnabled(true);
		else
			lifeField.setEnabled(false);
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
		
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		int selected = collisionsList.getSelected();
		if(anim.getSizeAForms() == 0 || selected == -1)
			return;
		AdvForm l_aForm = anim.getAForm(selected);
		l_aForm.setLife(Integer.parseInt(lifeField.getText()));
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int selected = collisionsList.getSelected();
		if(anim.getSizeAForms() == 0 || selected == -1)
			return;
		
		Object source = e.getSource();
		AdvForm l_aForm = anim.getAForm(selected);
		if(source == weaponCheck)
		{
			l_aForm.setWeapon(weaponCheck.isSelected());
			if(l_aForm.isWeapon())
				weaponVecB.setEnabled(true);
			else
				weaponVecB.setEnabled(false);
		}
		else if(source == lifeCheck)
		{
			l_aForm.setHavingLife(lifeCheck.isSelected());
			if(l_aForm.isHavingLife())
				lifeField.setEnabled(true);
			else
				lifeField.setEnabled(false);
		}
		else if(source == blockingCheck)
		{
			l_aForm.setBlocking(blockingCheck.isSelected());
		}
		else if(source == actionCheck)
		{
			l_aForm.setAction(actionCheck.isSelected());
		}
		else if(source == loopingCheck)
		{
			l_aForm.setLooping(loopingCheck.isSelected());
		}
		else if(source == weaponVecB)
		{
			modif.weaponPos = true;
		}
	}
	public void setWeaponPos(Vector2D mousePos)
	{
		int selected = collisionsList.getSelected();
		if(selected == -1)
			return;
		AdvForm l_aForm = anim.getAForm(selected);
		l_aForm.setWeaponPos(mousePos);
	}
	public void setWeaponVec(Vector2D mousePos)
	{
		int selected = collisionsList.getSelected();
		if(selected == -1)
			return;
		AdvForm l_aForm = anim.getAForm(selected);
		l_aForm.getWeaponVec().set(l_aForm.getWeaponPos(), mousePos);
	}
	public int getSelectedPoint(Vector2D mousePos, AdvForm advForm)
	{
		int selected = collisionsList.getSelected();
		if(!(anim.size() != 0 && anim.getSizeFrames() != 0 && selected != -1))
			return -1;
		int selectedPoint = -1;
		
		if(!advForm.isWeapon())
			return selectedPoint;
		
		Vector2D l_vec = new Vector2D();
		
		l_vec.set(mousePos, advForm.getWeaponPos());
		
		if(l_vec.getMagnitude() < 15.0f)
			selectedPoint = -2;
		else
		{
			l_vec.set(mousePos, advForm.getWeaponPos().add(advForm.getWeaponVec()));
			if(l_vec.getMagnitude() < 15.0f)
				selectedPoint = -3;
		}
		
		return selectedPoint;
	}
	public void updateWeapon(Vector2D mousePos, int selectedPoint)
	{
		if(selectedPoint == -2)
			setWeaponPos(mousePos);

		else if(selectedPoint == -3)
			setWeaponVec(mousePos);
	}
	public void render()
	{
		int selected = collisionsList.getSelected();
		if(anim.getCurrentAnim() == -1 || anim.getCurrentFrame() == -1 || anim.getSizeAForms() == 0 || selected == -1)
			return;
		AdvForm l_aForm = anim.getAForm(selected);
		if(l_aForm.isWeapon())
		{
			Vector2D vec = new Vector2D(surfEntity.getWidth()*0.5f, surfEntity.getHeight()*0.5f);
			buffG.setColor(Color.BLUE);
			buffG.drawLine((int) (l_aForm.getWeaponPos().x + vec.x), (int) (l_aForm.getWeaponPos().y + vec.y),
					(int) (l_aForm.getWeaponPos().x + l_aForm.getWeaponVec().x + vec.x), (int) (l_aForm.getWeaponPos().y + l_aForm.getWeaponVec().y + vec.y));
			buffG.fillOval((int) (l_aForm.getWeaponPos().x + 0.5f + vec.x) - 5, (int) (l_aForm.getWeaponPos().y + 0.5f + vec.y) - 5, 10, 10);
		}
		
	}
}
