package MapEditor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import Addons.Entity;
import Addons.Path;
import Map.Layer;
import Map.Map;
import Maths.Circle;
import Maths.Form;
import Maths.Vector2D;
import Maths.Rectangle;
import Maths.Vector2D;
import Maths.sRectangle;

public class MapView extends Container implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener
{
	private Map map;
	private BufferedImage mapBuff, circleBuff;
	private JLabel surfLabel;
	private Graphics g, gCircle;
	
	private JPopupMenu popupMenu;
	private JMenuItem edit, delete;
	
	private Vector2D translate, translateScale;
	private Vector2D savedPoint;
	private float scale;
	private ArrayList<Entity> selectedEntities;
	private sRectangle vision;
	private LayersTree layersTree;
	
	//Move and scale
	private ArrayList<Form> up;
	private ArrayList<Form> right;
	
	//Rot
	private Circle circle;
	private Circle circle2;
	private Form triangleCircle;
		
	private int point;
	private Vector2D center;
	private int option;
	private boolean isMovingUp;
	private boolean isMovingRight;
	
	private boolean isDragging;
	
	public MapView(int i, int j, int typeIntRgb) 
	{
		super();
		//Creation fleche haut
		
		popupMenu = new JPopupMenu();
		edit = new JMenuItem("edit Path");
		delete = new JMenuItem("delete point");
		popupMenu.add(edit);
		popupMenu.add(delete);
		delete.setVisible(false);
		edit.addActionListener(this);
		delete.addActionListener(this);
		
		option = 0; //arrow
		point = -1;
		
		up = new ArrayList<Form>();
		Form triangle = new Form();
		triangle.addPoint(new Vector2D(0, -27));
		triangle.addPoint(new Vector2D(-5, -17));
		triangle.addPoint(new Vector2D(5, -17));
		Rectangle rectangle = new Rectangle(new Vector2D(0, -10), new Vector2D(3, 16), 0);
		Rectangle rectangleScale = new Rectangle(new Vector2D(0, -22), new Vector2D(10, 10), 0);
		
		
		up.add(rectangle);
		up.add(triangle);
		up.add(rectangleScale);
		
		//Creation fleche droite
		right = new ArrayList<Form>();
		Form triangle2 = triangle.clone();
		triangle2.rotateRadians((float) -Math.PI/2, new Vector2D(0, 0));
		Rectangle rectangle2 = rectangle.clone();
		rectangle2.rotateRadians((float) -Math.PI/2, new Vector2D(0, 0));
		Rectangle rectangleScale2 = rectangleScale.clone();
		rectangleScale2.rotateRadians((float) -Math.PI/2, new Vector2D(0, 0));
		
		right.add(rectangle2);
		right.add(triangle2);
		right.add(rectangleScale2);
		
		//Circle
		float radius = 18;
		float scaleCircle = 0.5f;
		circle = new Circle(new Vector2D(0, 0), radius);
		circle2 = new Circle(new Vector2D(0, 0), radius - 4);
		triangleCircle = new Form();
		triangleCircle.addPoint(new Vector2D(radius + 6, 6));
		triangleCircle.addPoint(new Vector2D(radius - 10, 6));
		triangleCircle.addPoint(new Vector2D(radius - 2, -5));
		
		circle.scale(scaleCircle, new Vector2D());
		circle2.scale(scaleCircle, new Vector2D());
		triangleCircle.scale(scaleCircle, new Vector2D());
		
		circleBuff = new BufferedImage((int) 80, (int) 80, typeIntRgb);
		gCircle = circleBuff.getGraphics();
		((Graphics2D) gCircle).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		circle.fillForm(gCircle, new Vector2D(radius*scaleCircle, radius*scaleCircle));
		((Graphics2D) gCircle).setComposite(AlphaComposite.SrcOut);
		circle2.fillForm(gCircle, new Vector2D(radius*scaleCircle, radius*scaleCircle));
		((Graphics2D) gCircle).setComposite(AlphaComposite.Src);
		triangleCircle.fillForm(gCircle, new Vector2D(radius*scaleCircle, radius*scaleCircle));
		center = new Vector2D();
		
		selectedEntities = new ArrayList<Entity>();
		
		isDragging = true;
		isMovingUp = false;
		isMovingRight = false;
		
		translate = new Vector2D();
		translateScale = new Vector2D();
		
		savedPoint = new Vector2D();
		scale = 1;
		
		mapBuff = new BufferedImage(i, j, typeIntRgb);
		surfLabel = new JLabel(new ImageIcon(mapBuff));
		this.setLayout(new FlowLayout());
		this.add(surfLabel);
		
		g = mapBuff.getGraphics();
		
		
		surfLabel.addMouseListener(this);
		surfLabel.addMouseMotionListener(this);
		surfLabel.addMouseWheelListener(this);
		
		vision = new sRectangle(0,0,i-1,j-1);
	}
	public void initialize(LayersTree layersTree)
	{
		this.layersTree = layersTree;
	}
	
	public boolean isCollidingUp(Form form, Circle circle)
	{
		if(option == 0)
		{
			if(up.get(0).collisionSat(form) || up.get(1).collisionSat(form))
				return true;
		}
		else if(option == 1)
		{
			if(this.circle.isColliding(circle))
				return true;
		}
		else if(option == 2)
		{
			if(up.get(0).collisionSat(form) || up.get(2).collisionSat(form))
				return true;
		}
		
		return false;
	}
	public boolean isCollidingRight(Form form)
	{
		if(option == 0)
		{
			if(right.get(0).collisionSat(form) || right.get(1).collisionSat(form))
				return true;
		}
		else if(option == 2)
		{
			if(right.get(0).collisionSat(form) || right.get(2).collisionSat(form))
				return true;
		}
		return false;
	}
	public void moveObjectsUp(ArrayList<Entity> objects, Vector2D vec)
	{
		Vector2D upVec = new Vector2D(up.get(0).get(1), up.get(0).get(0));
		upVec.normalize();
		float value = upVec.scalarProduct(vec);
		upVec.scalarProduct(value);
		
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).translate(upVec);
		}
		translateArrow(upVec);
	}
	public void moveObjectsRight(ArrayList<Entity> objects, Vector2D vec)
	{
		Vector2D rightVec = new Vector2D(right.get(0).get(1), right.get(0).get(0));
		rightVec.normalize();
		float value = rightVec.scalarProduct(vec);
		rightVec.scalarProduct(value);
		
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).translate(rightVec);
		}
		translateArrow(rightVec);
	}
	public void rotateObjects(ArrayList<Entity> objects, Vector2D vec1, Vector2D vec2)
	{
		float theta = vec1.getAngle(vec2);
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		for(int i=0; i<layers.size(); i++)
		{
			layers.get(i).rotateRadians(theta, this.center);
		}
	}
	public void scaleObjectsUp(ArrayList<Entity> objects, Vector2D vec)
	{
		Vector2D upVec = new Vector2D(up.get(0).get(1), up.get(0).get(0));
		upVec.normalize();
		float value = upVec.scalarProduct(vec);
		upVec.scalarProduct(value);
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		if(value/15f > -1)
		{
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).scale(1 + value/15f, this.center);
			}
		}
	}
	public void scaleObjectsRight(ArrayList<Entity> objects, Vector2D vec)
	{
		Vector2D rightVec = new Vector2D(right.get(0).get(1), right.get(0).get(0));
		rightVec.normalize();
		float value = rightVec.scalarProduct(vec);
		rightVec.scalarProduct(value);
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		if(value/15f > -1)
		{
			for(int i=0; i<layers.size(); i++)
			{
				layers.get(i).scale(1 + value/15f, this.center);
			}
		}
	}
	
	public void setArrowPos(Vector2D pos)
	{
		Vector2D vec = new Vector2D(center, pos);
		center.translate(vec);
		for(int i=0; i<up.size(); i++)
		{
			up.get(i).translate(vec);
		}
		for(int i=0; i<right.size(); i++)
		{
			right.get(i).translate(vec);
		}
		circle.translate(vec);
	}
	public void translateArrowX(float vecX)
	{
		center.translateX(vecX);
		for(int i=0; i<up.size(); i++)
		{
			up.get(i).translateX(vecX);
		}
		for(int i=0; i<right.size(); i++)
		{
			right.get(i).translateX(vecX);
		}
		circle.translateX(vecX);
	}
	public void translateArrowY(float vecY)
	{
		center.translateY(vecY);
		for(int i=0; i<up.size(); i++)
		{
			up.get(i).translateY(vecY);
		}
		for(int i=0; i<right.size(); i++)
		{
			right.get(i).translateY(vecY);
		}
		circle.translateY(vecY);
	}
	public void translateArrow(Vector2D vec)
	{
		center.translate(vec);
		for(int i=0; i<up.size(); i++)
		{
			up.get(i).translate(vec);
		}
		for(int i=0; i<right.size(); i++)
		{
			right.get(i).translate(vec);
		}
		circle.translate(vec);
	}
	public void scaleArrow(float f)
	{
		for(int i=0; i<up.size(); i++)
		{
			up.get(i).scale(f, this.center);
		}
		for(int i=0; i<right.size(); i++)
		{
			right.get(i).scale(f, this.center);
		}
		circle.scale(f, this.center);
	}
	public void updateCenter(ArrayList<Entity> objects)
	{
		if(objects.size() != 0)
		{
			Vector2D center = new Vector2D();
			for(int i=0; i<objects.size(); i++)
			{
				center.x += objects.get(i).getX();
				center.y += objects.get(i).getY();
			}
			center.x = center.x/objects.size();
			center.y = center.y/objects.size();
			this.setArrowPos(center);
		}
	}
	
	public void setMap(Map map)
	{
		this.map = map;
		this.render();
	}
	public void render()
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if(map != null)
		{
			map.computeEntitiesBounds();
			update();
			map.draw(g, translate.add(translateScale), vision, this.scale);
			map.drawBounds(g, translate.add(translateScale), scale);
			map.loadQuadTree();
			map.drawQuadTree(g, translate.add(translateScale), scale);
			
			for(int i=0; i<selectedEntities.size(); i++)
			{
				selectedEntities.get(i).getRectangle().drawRW(g, translate.add(translateScale), scale);
			}
			
			if(selectedEntities.size() != 0)
			{
				if(option == 0)
				{
					up.get(0).fillForm(g, translate.add(translateScale), scale);
					up.get(1).fillForm(g, translate.add(translateScale), scale);
					
					right.get(0).fillForm(g, translate.add(translateScale), scale);
					right.get(1).fillForm(g, translate.add(translateScale), scale);
				}
				else if(option == 1)
				{
					Vector2D vec = translate.add(translateScale);
					Circle circle = this.circle.clone();
					circle.scale(scale, new Vector2D());
					g.drawImage(circleBuff, (int) (circle.getCenter().x + vec.x - circle.getRadius()), (int) (circle.getCenter().y + vec.y - circle.getRadius()), null);
				}
				else if(option == 2)
				{
					up.get(0).fillForm(g, translate.add(translateScale), scale);
					up.get(2).fillForm(g, translate.add(translateScale), scale);
					
					right.get(0).fillForm(g, translate.add(translateScale), scale);
					right.get(2).fillForm(g, translate.add(translateScale), scale);
				}
			}
		}
		
		vision.draw(g, translate.add(translateScale));
		surfLabel.repaint();
	}
	public void update()
	{
		map.updateLayers();
	}
	public void setScale(float scale, Vector2D center)
	{
		float lastScale = this.scale;
		if(scale > 1.0)
			this.scale = scale;
		else
			this.scale = 1.0f;
		
		float lastX = translateScale.x;
		float lastY = translateScale.y;
		translateScale.scale(this.scale/lastScale, center);
		vision.translate(new Vector2D(lastX - translateScale.x, lastY - translateScale.y));
		
		
		scaleArrow(lastScale/this.scale);
	}
	public float getScale()
	{
		return this.scale;
	}
	public Vector2D getTranslateScale()
	{
		return this.translateScale;
	}
	
	public int getOption()
	{
		return option;
	}
	public void setOption(int option)
	{
		this.option = option;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		if(e.getButton() == 1)
		{
			if(e.getClickCount()%2 == 0)
			{
				option = (option + 1)%3;
			}
			
			if(option == 3)
			{
				Vector2D mouse = new Vector2D(e.getX() - translate.x - translateScale.x, e.getY() - translate.y - translateScale.y);
				Path path = (Path) layers.get(0).getObject();

				if(point == -1)
				{
					path.addPoint(mouse);
				}
			}
			else
			{
				sRectangle rec = sRectangle.createSRectangleCenter(new Vector2D(e.getX() - translate.x - translateScale.x, e.getY() - translate.y - translateScale.y), new Vector2D(6*scale, 6*scale));
				rec.scale(1/scale, new Vector2D());
				
				ArrayList<Entity> entities = map.getObject(rec);
				
				if(!e.isShiftDown())
				{
					selectedEntities.clear();
					start:
						for(int i=0; i<entities.size(); i++)
						{
							ArrayList<Rectangle> colli = entities.get(i).getSelectionRectangle();
							for(int j=0; j<colli.size(); j++)
							{
								if(rec.collisionSat(colli.get(j)))
								{
									selectedEntities.remove(entities.get(i));
									selectedEntities.add(entities.get(i));
									break start;
								}	
							}
						}
				}
				else
				{
					start:
						for(int i=0; i<entities.size(); i++)
						{
							ArrayList<Rectangle> colli = entities.get(i).getSelectionRectangle();
							for(int j=0; j<colli.size(); j++)
							{
								if(rec.collisionSat(colli.get(j)))
								{
									if(!selectedEntities.remove(entities.get(i)))
										selectedEntities.add(entities.get(i));
									break start;
								}
							}
						}
				}
				layersTree.updateSelectedEntities(selectedEntities);
				layersTree.updateSelected();
				updateCenter(selectedEntities);
			}
		}
		else if(e.getButton() == 3)
		{
			if(option == 3)
			{
				Vector2D mouse = new Vector2D(e.getX() - translate.x - translateScale.x, e.getY() - translate.y - translateScale.y);
				point = -1;
				Path path = (Path) layers.get(0).getObject();
				Vector2D dist = new Vector2D();
				for(int i=0; i<path.size(); i++)
				{
					dist.set(mouse, path.getPoint(i));
					if(dist.getMagnitude() < scale*4)
					{
						point = i;
						path.remove(i);
						this.setArrowPos(path.getRectangle().getCenter());
						break;
					}
				}
				if(point == -1)
				{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			else
			{
				edit.setText("edit path");
				delete.setVisible(false);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		this.render();
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
		isDragging = true;
		ArrayList<Layer> layers = layersTree.getSelectedLayers();
		savedPoint.set(e.getX(), e.getY());
		if(option == 3 && (layers.size() != 1 || layers.get(0).getObject().getClass() != Path.class))
		{
			option = 0;
			point = -1;
		}
		if(e.getButton() == 1)
		{	
			if(option == 3)
			{
				Vector2D mouse = new Vector2D(e.getX() - translate.x - translateScale.x, e.getY() - translate.y - translateScale.y);
				point = -1;
				Path path = (Path) layers.get(0).getObject();
				Vector2D dist = new Vector2D();
				for(int i=0; i<path.size(); i++)
				{
					dist.set(mouse, path.getPoint(i));
					if(dist.getMagnitude() < scale*4)
					{
						point = i;
						isDragging = false;
						break;
					}
				}
			}
			else
			{
				Vector2D center = new Vector2D(e.getX() - translate.x - translateScale.x, e.getY() - translate.y - translateScale.y);
				sRectangle rec = sRectangle.createSRectangleCenter(center, new Vector2D(6*scale, 6*scale));
				rec.scale(1/scale, new Vector2D());
			
				Circle mouseCircle = new Circle(center, 6*scale);
				mouseCircle.scale(1/this.scale, new Vector2D());
				
				if(this.isCollidingUp(rec, mouseCircle))
				{
					isDragging = false;
					isMovingUp = true;
				}
				else if(this.isCollidingRight(rec))
				{
					isDragging = false;
					isMovingRight = true;
				}
				else
					isDragging = true;
			}
		}
		this.render();		
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		isMovingUp = false;
		isMovingRight = false;
	}
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(!isDragging)
		{
			if(option == 0)
			{
				if(isMovingUp)
				{
					Vector2D mouse = new Vector2D(e.getX(), e.getY());
					Vector2D translate = new Vector2D(savedPoint, mouse);
					translate.scale(1/this.scale, new Vector2D());
					
					moveObjectsUp(selectedEntities, translate);
					
					savedPoint.set(mouse);
					layersTree.updateSelected();
					render();
				}
				else if(isMovingRight)
				{
					Vector2D mouse = new Vector2D(e.getX(), e.getY());
					Vector2D translate = new Vector2D(savedPoint, mouse);
					translate.scale(1/this.scale, new Vector2D());
					
					moveObjectsRight(selectedEntities, translate);
					
					savedPoint.set(mouse);
					layersTree.updateSelected();
					render();
				}
			}
			else if(option == 1)
			{
				if(isMovingUp)
				{
					Vector2D mouse = new Vector2D(e.getX(), e.getY());
					
					Vector2D newCenter = center.clone();
					newCenter.scale(this.scale, new Vector2D());
					newCenter.translate(translate.add(translateScale));
					
					Vector2D vec1 = new Vector2D(newCenter, mouse);
					Vector2D vec2 = new Vector2D(newCenter, savedPoint);
					
					vec1.normalize();
					vec2.normalize();
					rotateObjects(selectedEntities, vec1, vec2);
					layersTree.updateSelected();
					savedPoint.set(mouse);
					render();
				}
			}
			else if(option == 2)
			{
				if(isMovingUp)
				{
					Vector2D mouse = new Vector2D(e.getX(), e.getY());
					Vector2D translate = new Vector2D(savedPoint, mouse);
					translate.scale(1/this.scale, new Vector2D());
					
					scaleObjectsUp(selectedEntities, translate);
					
					savedPoint.set(mouse);
					layersTree.updateSelected();
					render();
				}
				else if(isMovingRight)
				{
					Vector2D mouse = new Vector2D(e.getX(), e.getY());
					Vector2D translate = new Vector2D(savedPoint, mouse);
					translate.scale(1/this.scale, new Vector2D());
					
					scaleObjectsRight(selectedEntities, translate);
					
					savedPoint.set(mouse);
					layersTree.updateSelected();
					render();
				}
			}
			else if(option == 3)
			{
				Vector2D mouse = new Vector2D(e.getX(), e.getY());
				Vector2D translate = new Vector2D(savedPoint, mouse);
				ArrayList<Layer> layers = layersTree.getSelectedLayers();
				((Path) layers.get(0).getObject()).getPoint(point).translate(translate);
				
				savedPoint.set(mouse);
				layersTree.updateSelected();
				render();
			}
		}
		else
		{
			//On bouge la vision
			Vector2D mouse = new Vector2D(e.getX(), e.getY());
			Vector2D translate = new Vector2D(savedPoint, mouse);
			
			vision.translate(translate.multiply(-1));
			this.translate.translate(translate);
			savedPoint.set(mouse);
			render();
		}
		
	}
	@Override
	public void mouseMoved(MouseEvent e)
	{
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		float scale = e.getScrollAmount()/3f;
		if(e.getWheelRotation() > 0)
			scale = - scale;
		Vector2D center = new Vector2D(e.getX(), e.getY());
		this.setScale(this.scale + scale, center.add(translate.multiply(-1)));
		render();
	}
	
	public void addSelectedEntities(Layer selectedLayer)
	{
		ArrayList<Entity> selected = selectedLayer.getObjects();
		//On supprime tout au cas ou l'élément est déjà séléctionné
		selectedEntities.removeAll(selected);
		selectedEntities.addAll(selected);
		
		updateCenter(selectedEntities);
	}
	public void setSelectedEntities(ArrayList<Entity> selected)
	{
		selectedEntities.clear();
		selectedEntities.addAll(selected);
		
		updateCenter(selectedEntities);
	}
	public void setSelectedEntities(Layer selectedLayer)
	{
		selectedEntities.clear();
		ArrayList<Entity> selected = selectedLayer.getObjects();
		selectedEntities.addAll(selected);
		
		setArrowPos(selectedLayer.getCenter());
	}
	
	public void clearSelection()
	{
		selectedEntities.clear();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == edit)
		{
			if(option != 3)
			{
				option = 3;
				edit.setText("stop");
				delete.setVisible(true);
				this.render();
			}
			else
			{
				option = 0;
				this.render();
			}
		}
		else if(source == delete)
		{
			ArrayList<Layer> layers = layersTree.getSelectedLayers();
			Path path = ((Path) layers.get(0).getObject());
			String text = JOptionPane.showInputDialog(null, "Point à supprimer: (" + path.size() + ")");
			if(text != null && text != "")
			{
				point = Integer.parseInt(text);
				if(point > -1 && point < path.size())
				{
					path.remove(point);
					this.setArrowPos(path.getRectangle().getCenter());
					this.render();
				}
			}
		}
	}
}
