package MapEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.plaf.synth.SynthTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.omg.CORBA.Bounds;

import Addons.AdvEntity;
import Addons.Entity;
import Addons.Path;
import Base.Image;
import BaseWindows.EntitiesDisplay;
import Map.Layer;
import Map.Map;
import Maths.Vector2D;
import Maths.PointInt;

public class LayersTree extends Container implements TreeSelectionListener, ActionListener, MouseListener, MouseMotionListener
{
	private static class MyTreeCellRenderer extends DefaultTreeCellRenderer
	{
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
        {
        	JLabel label = (JLabel) (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, false);
        	//label.setBorder(border);
            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) 
            {	
            	
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() instanceof Layer) 
                {
                    // decide based on some property of your Contact obj
                    Layer layer = (Layer) node.getUserObject();
                    if(layer.getObject() == null) 
                    {
                    	URL url = ClassLoader.getSystemClassLoader().getResource("icons/storage.png");
            			Icon icon = new ImageIcon(url);
            			setIcon(icon);
                    }
                    else
                    {
                    	if(layer.getObject().getClass() == Entity.class)
                    	{
                    		URL url = ClassLoader.getSystemClassLoader().getResource("icons/file.png");	
                			Icon icon = new ImageIcon(url);
                			setIcon(icon);
                    	}
                    	else
                    	{
                    		URL url = ClassLoader.getSystemClassLoader().getResource("icons/filePath.png");	
                			Icon icon = new ImageIcon(url);
                			setIcon(icon);
                    	}
                    }
                }
            }
            return this;
        }
	}
	class NonRootEditor extends DefaultTreeCellEditor
	{
	    private final DefaultTreeCellRenderer renderer;
	 
	    public NonRootEditor(JTree tree, DefaultTreeCellRenderer renderer)
	    {
	        super(tree, renderer);
	        this.renderer = renderer;
	    }
	    
	    @Override
	    protected boolean canEditImmediately(EventObject event) 
	    {
	    	if(tree.getSelectionCount() != 1)
	    		return false;
	    	
	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	    	if(node == null || node == root)
	    		return false;
	    	Layer layer = (Layer) node.getUserObject();
	    	
	        if((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event) && layer.getObject() == null) 
	        {	
	            MouseEvent me = (MouseEvent)event;
	            
	            if(me.isShiftDown())
	            	return false;
	            return ((me.getClickCount() >= 2) && inHitRegion(me.getX(), me.getY()));
	        }
	        return (false);
	    }
	    
	}
	class MyTreeModel extends DefaultTreeModel
	{
		public MyTreeModel(DefaultMutableTreeNode root) 
		{
			super(root);
			
		}
		@Override
		public void valueForPathChanged(TreePath path, Object newValue) 
	    {
			DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			((Layer) aNode.getUserObject()).setName((String) newValue);
			redrawTree();
	    }
	}
	class MyTree extends JTree
	{
		public MyTree(MyTreeModel myTreeModel) 
		{
			super(myTreeModel);
			setCellRenderer(new MyTreeCellRenderer());
			setCellEditor(new NonRootEditor(this, (DefaultTreeCellRenderer) this.getCellRenderer()));
			setEditable(true);	
			setRowHeight(0);
		}
		@Override
		public boolean hasBeenExpanded(TreePath path) { return true; }
	}
	public class NiceTreeUI extends SynthTreeUI
	{
		public NiceTreeUI()
		{
			super();
		}
		/*
		Change Icon
		@Override
	    public Icon getCollapsedIcon() 
		{
	        return collapseIcon;
	    }

	    @Override
	    public Icon getExpandedIcon() 
	    {
	        return expandIcon;
	    }*/
		@Override
		protected boolean shouldPaintExpandControl(TreePath tree, int row, boolean isExpanded, boolean wasExpanded, boolean lead)
		{
			return true;
		}
	    @Override
	    protected void paintRow( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
	        TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
	    {
	        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
            g.setColor( Color.GREEN );
            g.fillRect( 0, 0, 100, 100 );
            
	            
	        
	        
	    }
	     
	}
	
	Layer copyLayer;
	
	private MapView mapView;
	private JPopupMenu popupMenu;
	
	private JLabel opacityLabel, scaleLabel, rotationLabel, posXLabel, posYLabel, flipHLabel, flipVLabel;
	private JTextField opacityField, scaleField, rotationField, posXField, posYField;
	private JCheckBox flipHCheck, flipVCheck;
	
	//private JMenu arrondi ;
	private JMenuItem renommer, supprimer;
	
	private Layer layer;
	private EntitiesDisplay entitiesDisplay;

	private JTree tree;
	
	private JScrollPane treeView;
	private DefaultMutableTreeNode root;
	
	private JButton addFolder, addObject, addPath;
	private boolean isDragging;
	private DefaultMutableTreeNode selectedNode, insertNode;
	
	public LayersTree(PointInt dim, EntitiesDisplay entitiesDisplay, MapView mapView)
	{
		super();
		
		copyLayer = null;
		
		//Label and field
		opacityLabel = new JLabel("Opacity");
		scaleLabel = new JLabel("Scale");
		rotationLabel = new JLabel("Rotation");
		posXLabel = new JLabel("Pos X");
		posYLabel = new JLabel("Pos Y");
		flipHLabel = new JLabel("Flip H");
		flipVLabel = new JLabel("Flip V");
		
		scaleField = new JTextField("1.00", 3);
		opacityField = new JTextField("1.00", 3);
		rotationField = new JTextField("0.00", 3);
		posXField = new JTextField("0.00", 3);
		posYField = new JTextField("0.00", 3);
		flipHCheck = new JCheckBox();
		flipVCheck = new JCheckBox();
		
		scaleField.addActionListener(this);
		opacityField.addActionListener(this);
		rotationField.addActionListener(this);
		posXField.addActionListener(this);
		posYField.addActionListener(this);
		flipHCheck.addActionListener(this);
		flipVCheck.addActionListener(this);
		
		this.mapView = mapView;
		selectedNode = null;
		insertNode = null;
		
		popupMenu = new JPopupMenu();
		
		renommer = new JMenuItem("renommer");
		supprimer = new JMenuItem("supprimer");
		
		popupMenu.add(renommer);
		popupMenu.add(supprimer);
		
		renommer.addActionListener(this);
		supprimer.addActionListener(this);
		
		isDragging = false;
		this.entitiesDisplay = entitiesDisplay;
		Layer layer = new Layer("root");
		
		root = new DefaultMutableTreeNode(layer);
		tree = new MyTree(new MyTreeModel(root));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		tree.expandRow(0);
		tree.setRootVisible(false);
		
		tree.addMouseListener(this);
		tree.addMouseMotionListener(this);
		
		treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(dim.x, dim.y - 150));
		
		addFolder = new JButton("+ Calque");
		addObject = new JButton("+ Objet");
		addPath = new JButton("+ Path ");
	
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		
		//opacity
		layout.putConstraint(SpringLayout.WEST, opacityLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, opacityLabel, 14, SpringLayout.NORTH, this);
		add(opacityLabel);
		
		layout.putConstraint(SpringLayout.WEST, opacityField, 12, SpringLayout.EAST, opacityLabel);
		layout.putConstraint(SpringLayout.NORTH, opacityField, 7, SpringLayout.NORTH, this);
		add(opacityField);
		
		//scale
		layout.putConstraint(SpringLayout.WEST, scaleLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, scaleLabel, 14, SpringLayout.SOUTH, opacityLabel);
		add(scaleLabel);
		
		layout.putConstraint(SpringLayout.WEST, scaleField, 12, SpringLayout.EAST, opacityLabel);
		layout.putConstraint(SpringLayout.NORTH, scaleField, 7, SpringLayout.SOUTH, opacityLabel);
		add(scaleField);
		
		//rotation
		layout.putConstraint(SpringLayout.WEST, rotationLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, rotationLabel, 14, SpringLayout.SOUTH, scaleLabel);
		add(rotationLabel);
		
		layout.putConstraint(SpringLayout.WEST, rotationField, 12, SpringLayout.EAST, opacityLabel);
		layout.putConstraint(SpringLayout.NORTH, rotationField, 7, SpringLayout.SOUTH, scaleLabel);
		add(rotationField);
		
		//PosX
		layout.putConstraint(SpringLayout.WEST, posXLabel, 10, SpringLayout.EAST, opacityField);
		layout.putConstraint(SpringLayout.NORTH, posXLabel, 14, SpringLayout.NORTH, this);
		add(posXLabel);
		
		layout.putConstraint(SpringLayout.WEST, posXField, 12, SpringLayout.EAST, posXLabel);
		layout.putConstraint(SpringLayout.NORTH, posXField, 7, SpringLayout.NORTH, this);
		add(posXField);
		
		//PosY
		layout.putConstraint(SpringLayout.WEST, posYLabel, 10, SpringLayout.EAST, opacityField);
		layout.putConstraint(SpringLayout.NORTH, posYLabel, 14, SpringLayout.SOUTH, posXLabel);
		add(posYLabel);
		
		layout.putConstraint(SpringLayout.WEST, posYField, 12, SpringLayout.EAST, posXLabel);
		layout.putConstraint(SpringLayout.NORTH, posYField, 7, SpringLayout.SOUTH, posXLabel);
		add(posYField);
		
		//FlipH
		layout.putConstraint(SpringLayout.WEST, flipHLabel, 10, SpringLayout.EAST, opacityField);
		layout.putConstraint(SpringLayout.NORTH, flipHLabel, 14, SpringLayout.SOUTH, posYLabel);
		add(flipHLabel);
		
		layout.putConstraint(SpringLayout.WEST, flipHCheck, 12, SpringLayout.EAST, posXLabel);
		layout.putConstraint(SpringLayout.NORTH, flipHCheck, 11, SpringLayout.SOUTH, posYLabel);
		add(flipHCheck);
		
		//FlipV
		layout.putConstraint(SpringLayout.WEST, flipVLabel, 15, SpringLayout.EAST, flipHCheck);
		layout.putConstraint(SpringLayout.NORTH, flipVLabel, 14, SpringLayout.SOUTH, posYLabel);
		add(flipVLabel);
		
		layout.putConstraint(SpringLayout.WEST, flipVCheck, 12, SpringLayout.EAST, flipVLabel);
		layout.putConstraint(SpringLayout.NORTH, flipVCheck, 11, SpringLayout.SOUTH, posYLabel);
		add(flipVCheck);
		
		
		//Tree
		layout.putConstraint(SpringLayout.WEST, treeView, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, treeView, 5, SpringLayout.SOUTH, rotationField);
		add(treeView);
		
		layout.putConstraint(SpringLayout.WEST, addFolder, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, addFolder, 0, SpringLayout.SOUTH, treeView);
		add(addFolder);
		
		layout.putConstraint(SpringLayout.WEST, addObject, 5, SpringLayout.EAST, addFolder);
		layout.putConstraint(SpringLayout.NORTH, addObject, 0, SpringLayout.SOUTH, treeView);
		add(addObject);
		
		layout.putConstraint(SpringLayout.WEST, addPath, 5, SpringLayout.EAST, addObject);
		layout.putConstraint(SpringLayout.NORTH, addPath, 0, SpringLayout.SOUTH, treeView);
		add(addPath);
		
		addFolder.addActionListener(this);
		addObject.addActionListener(this);
		addPath.addActionListener(this);
		
		this.setPreferredSize(new Dimension(dim.x, dim.y));
	}
	
	public void addObject(Entity object)
	{
		//((Layer) root.getUserObject()).reset();
		object.reset();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
		{
			selectedNode = root;
		}
		
		Layer actualLayer = (Layer) selectedNode.getUserObject();
		while(actualLayer.getObject() != null)
		{
			selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
			actualLayer = (Layer) selectedNode.getUserObject();
		}
		Layer newLayer = new Layer(object);
		
		actualLayer.addLayer(newLayer);
		
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newLayer);
		((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
		
		tree.expandPath(new TreePath(selectedNode.getPath()));
		
		//((Layer) root.getUserObject()).build();
		
		redrawTree();
		
		
	}
	public void addFolder(String name)
	{
		//((Layer) root.getUserObject()).reset();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
		{
			selectedNode = root;
		}
		Layer actualLayer = (Layer) selectedNode.getUserObject();
		while(actualLayer.getObject() != null)
		{
			selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
			actualLayer = (Layer) selectedNode.getUserObject();
		}
		
		Layer newLayer = new Layer(name);
		
		actualLayer.addLayer(newLayer);
		
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newLayer);
		((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
		
		tree.expandPath(new TreePath(selectedNode.getPath()));
		
		//((Layer) root.getUserObject()).build();
		redrawTree();
	}
	public void recreateNodes(DefaultMutableTreeNode root, Layer layer)
	{
		ArrayList<Layer> layers = layer.getLayers();
		for(int i=0; i<layers.size(); i++)
		{
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(layers.get(i));
			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, root, root.getChildCount());
			recreateNodes(newNode, layers.get(i));
		}
	}
	
	public void updateSelectedEntities(ArrayList<Entity> entities)
	{
		selectedNode = null;
		tree.clearSelection();
		TreePath[] treePaths = new TreePath[entities.size()];
		for(int i=0; i<entities.size(); i++)
		{
			treePaths[i] = getPathEntity(root, entities.get(i));
		}
		tree.setSelectionPaths(treePaths);
		
		if(treePaths.length == 1)
		{
			selectedNode = (DefaultMutableTreeNode) treePaths[0].getLastPathComponent();
		}
	}
	public TreePath getPathEntity(DefaultMutableTreeNode node, Entity entity)
	{
		for(int i=0; i<node.getChildCount(); i++)
		{
			Layer layer = (Layer) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
			if(layer.getBounds().isInsideBorder(entity.getPos()))
			{
				if(layer.getObject() == null)
				{
					return getPathEntity(((DefaultMutableTreeNode) node.getChildAt(i)), entity);
				}
				else if(entity == layer.getObject())
				{
					TreeNode[] treeNodes = ((DefaultMutableTreeNode) node.getChildAt(i)).getPath();
					return new TreePath(treeNodes);
				}
			}
		}
		return null;
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) 
	{
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		
		if(source == scaleField)
		{
			String text = scaleField.getText();
			if(text != null && text != "")
			{
				if(selectedNode != null && selectedNode != root)
				{
					ArrayList<TreePath> selectionsPath = getSelectedPaths();
					for(int i=0; i<selectionsPath.size(); i++)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
						
						Layer layer = (Layer) node.getUserObject();
						float scale = Float.parseFloat(text);
						layer.setScale(scale, layer.getCenter());
					}
					mapView.render();
				}
			}
		}
		else if(source == rotationField)
		{
			String text = rotationField.getText();
			if(text != null && text != "")
			{
				if(selectedNode != null && selectedNode != root)
				{
					ArrayList<TreePath> selectionsPath = getSelectedPaths();
					for(int i=0; i<selectionsPath.size(); i++)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
						
						Layer layer = (Layer) node.getUserObject();
						float rot = Float.parseFloat(text);
						layer.setRotationDegree(rot, layer.getCenter());
					}
					mapView.render();
				}
			}
		}
		else if(source == opacityField)
		{
			String text = opacityField.getText();
			if(text != null && text != "")
			{
				if(selectedNode != null && selectedNode != root)
				{
					ArrayList<TreePath> selectionsPath = getSelectedPaths();
					for(int i=0; i<selectionsPath.size(); i++)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
						
						Layer layer = (Layer) node.getUserObject();
						float opacity = Float.parseFloat(text);	
						layer.setVisible(opacity);
					}
					mapView.render();
				}
			}
		}
		else if(source == posXField)
		{
			String text = posXField.getText();
			if(text != null && text != "")
			{
				if(selectedNode != null && selectedNode != root)
				{
					ArrayList<TreePath> selectionsPath = getSelectedPaths();
					for(int i=0; i<selectionsPath.size(); i++)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
						
						Layer layer = (Layer) node.getUserObject();
						float posX = Float.parseFloat(text);
						float vecX = posX - layer.getPos().x;
						mapView.translateArrowX(vecX);
						layer.setPosX(posX);
					}
					mapView.render();
				}
			}
		}
		else if(source == posYField)
		{
			String text = posYField.getText();
			if(text != null && text != "")
			{
				if(selectedNode != null && selectedNode != root)
				{
					ArrayList<TreePath> selectionsPath = getSelectedPaths();
					for(int i=0; i<selectionsPath.size(); i++)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
						
						Layer layer = (Layer) node.getUserObject();
						float posY = Float.parseFloat(text);
						float vecY = posY - layer.getPos().y;
						mapView.translateArrowY(vecY);
						layer.setPosY(posY);
					}
					mapView.render();
				}
			}
		}
		else if(source == flipHCheck)
		{
			boolean b = flipHCheck.isSelected();
			if(selectedNode != null && selectedNode != root)
			{
				ArrayList<TreePath> selectionsPath = getSelectedPaths();
				for(int i=0; i<selectionsPath.size(); i++)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
					
					Layer layer = (Layer) node.getUserObject();
					layer.setFlipH(b, layer.getCenter());
				}
				mapView.render();
			}
		}
		else if(source == flipVCheck)
		{
			boolean b = flipVCheck.isSelected();
			if(selectedNode != null && selectedNode != root)
			{
				ArrayList<TreePath> selectionsPath = getSelectedPaths();
				for(int i=0; i<selectionsPath.size(); i++)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
					
					Layer layer = (Layer) node.getUserObject();
					layer.setFlipV(b, layer.getCenter());		
				}
				mapView.render();
			}
		}
		else if(source == renommer)
		{
			if(selectedNode != null && selectedNode != root)
			{
				Layer layer = (Layer) selectedNode.getUserObject();
				if(layer.getObject() == null)
				{
					String name = JOptionPane.showInputDialog(null, "Nom du calque", layer.getName());
					
					if(name != null && name != "")
						layer.setName(name);
					
					redrawTree();
				}
			}
			
		}
		else if(source == supprimer)
		{
			if(selectedNode != null && selectedNode != root)
			{
				ArrayList<TreePath> selectionsPath = getSelectedPaths();
				int answer = JOptionPane.showConfirmDialog(null, "voulez-vous supprimer cet élément", "Suppression", JOptionPane.YES_NO_OPTION);
				if(answer == 0)
				{
					for(int i=0; i<selectionsPath.size(); i++)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
						Layer layer = (Layer) node.getUserObject();
					
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
						Layer parentLayer = (Layer) parent.getUserObject();
						parentLayer.getLayers().remove(layer);
						
						node.removeFromParent();
					}
					tree.clearSelection();
					redrawTree();
					selectedNode = null;
				}
			}
			
		}
		else if(source == addFolder)
		{
			String name = JOptionPane.showInputDialog(null, "Nom du calque");
			if(name != null && name != "")
				addFolder(name);
		}
		else if(source == addObject)
		{
			Entity entity = entitiesDisplay.getObject();			
			if(entity != null)
			{
				Entity newEntity = entity.clone();
				addObject(newEntity);
			}
		}
		else if(source == addPath)
		{
			Path path = new Path(new Vector2D());
			String name = JOptionPane.showInputDialog(null, "Nom du chemin");
			if(name != null && name != "")
			{
				path.setName(name);
				addObject(path);
			}
		}
	}
	
	public ArrayList<TreePath> getSelectedPaths()
	{
		TreePath[] selectionPaths = tree.getSelectionPaths();
		
		ArrayList<TreePath> treePaths = new ArrayList<TreePath>();
		if(selectionPaths != null)
		{
			for(int i=0; i<selectionPaths.length; i++)
			{
				treePaths.add(selectionPaths[i]);
			}
			
			//On enleve les fils des parents qui sont déjà dedans
			for(int i=0; i<treePaths.size(); i++)
			{
				for(int j=0; j<treePaths.size(); j++)
				{
					if(i != j)
					{
						if(treePaths.get(j).isDescendant(treePaths.get(i)))
						{
							treePaths.remove(i);
							i--;
							break;
						}
					}
				}
			}
		}
		return treePaths;
	}
	public ArrayList<Layer> getSelectedLayers()
	{
		ArrayList<Layer> layers = new ArrayList<Layer>();
		ArrayList<TreePath> treePaths = getSelectedPaths();
		for(int i=0; i<treePaths.size(); i++)
		{
			layers.add((Layer) ((DefaultMutableTreeNode) treePaths.get(i).getLastPathComponent()).getUserObject());
		}
		return layers;
	}
	public void setSelectionPaths(ArrayList<TreePath> treePaths)
	{
		TreePath[] newTreePaths = new TreePath[treePaths.size()];
		for(int i=0; i<treePaths.size(); i++)
		{
			newTreePaths[i] = treePaths.get(i);
		}
		tree.setSelectionPaths(newTreePaths);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			selectedNode = root;
		if(mapView.getOption() == 3)
		{
			mapView.setOption(0);
		}
		
		if(e.getClickCount() == 1 && e.getButton() == 3)
		{
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
		redrawTree();
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
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			selectedNode = root;
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{	
		if(isDragging && insertNode != null) //insertion d'une node avant une node
		{
			if(insertNode != selectedNode && !insertNode.isNodeAncestor((selectedNode)))
    		{
				//((Layer) root.getUserObject()).reset();
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) insertNode.getParent();
				int index = parentNode.getIndex(insertNode);
				
				Layer parent = (Layer) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
	    		Layer draggingLayer = (Layer) selectedNode.getUserObject();
	    		parent.getLayers().remove(draggingLayer);
	    		Layer newParent = (Layer) parentNode.getUserObject();
	    		
	    		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);		
	    		newParent.addLayer(draggingLayer, index);
	    		
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(draggingLayer);
				recreateNodes(newNode, draggingLayer);
				((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, parentNode, index);
			
				//((Layer) root.getUserObject()).build();
				redrawTree();
				insertNode = null;
    		}
		}
		else if(isDragging && selectedNode != null && selectedNode.getParent() != null && selectedNode != root)
	    {
	    	TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
	    	DefaultMutableTreeNode selectedNodeFromPos = null; 
	    	if(pathFromPos == null)
	    		selectedNodeFromPos = root;
	    	else
	    		selectedNodeFromPos = (DefaultMutableTreeNode) pathFromPos.getLastPathComponent();
    		if(selectedNodeFromPos != selectedNode && !selectedNodeFromPos.isNodeAncestor((selectedNode)))
    		{
    			//((Layer) root.getUserObject()).reset();
    			
	    		Layer parent = (Layer) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
	    		Layer draggingLayer = (Layer) selectedNode.getUserObject();
	    		parent.getLayers().remove(draggingLayer);
	    		Layer newParent = (Layer) selectedNodeFromPos.getUserObject();
	    		
	    		if(newParent.getObject() != null)
	    		{
	    			selectedNodeFromPos = (DefaultMutableTreeNode) selectedNodeFromPos.getParent();
	    			newParent = (Layer)  selectedNodeFromPos.getUserObject();
	    		}
	    		
	    		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);
	    		
	    		newParent.addLayer(draggingLayer);
	    		
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(draggingLayer);
				recreateNodes(newNode, draggingLayer);
				((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNodeFromPos, selectedNodeFromPos.getChildCount());
				
				//((Layer) root.getUserObject()).build();
				redrawTree();
    		}
	    }
		//Clique sur une surface blanche
		int row = tree.getRowForLocation(e.getX(),e.getY());
		    if(row == -1)
		        tree.clearSelection();
		updateSelected();
		
	    isDragging = false;
	    selectedNode = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		isDragging = true;
		TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
		if(pathFromPos != null)
		{
			Rectangle bound = tree.getPathBounds(pathFromPos);
			
			if(Math.abs(e.getY() - bound.getMinY()) < 4)
			{
				tree.clearSelection();
				insertNode = (DefaultMutableTreeNode) pathFromPos.getLastPathComponent();
			}
			else
			{
				tree.setSelectionPath(pathFromPos);
				insertNode = null;
			}
		}
		else
		{
			tree.clearSelection();
		}
		
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public void redrawTree() 
	{
		if(selectedNode == null || selectedNode == root)
		{
			mapView.clearSelection();
		}
		else
		{
			ArrayList<TreePath> selectionsPath = getSelectedPaths();
			mapView.clearSelection();
			for(int i=0; i<selectionsPath.size(); i++)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionsPath.get(i).getLastPathComponent();
				Layer layer = (Layer) node.getUserObject();
				
				mapView.addSelectedEntities(layer);
			}
		}
		TreePath[] selectionPaths = tree.getSelectionPaths();
		//ArrayList<TreePath> selectionsPath = getSelectedPaths();
		
		// First, capture the current expanded state of the JTree
		StringBuilder sb = new StringBuilder();

	    for(int i =0 ; i < tree.getRowCount(); i++){
	        TreePath tp = tree.getPathForRow(i);
	        if(tree.isExpanded(i)){
	            sb.append(tp.toString());
	            sb.append(",");
	        }
	    }
	    String s = sb.toString();

		// Force the JTree to rebuild itself
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload();

		// Finally, recover the old expanded state of the JTree
		for(int i = 0 ; i<tree.getRowCount(); i++){
	        TreePath tp = tree.getPathForRow(i);
	        if(s.contains(tp.toString() )){
	            tree.expandRow(i);
	        }   
	    }
		tree.setSelectionPaths(selectionPaths);
		//setSelectionPaths(selectionsPath);
		
		mapView.render();
	}

	public void loadMap(Map map)
	{
		root.removeAllChildren();
		root.setUserObject(map.getLayer());
		recreateNodes(root, map.getLayer());
		this.redrawTree();
	}
	public void updateSelected()
	{
		
		ArrayList<TreePath> selectionsPath = getSelectedPaths();
		
		if(selectionsPath != null && selectionsPath.size() == 1 && selectedNode != root && selectedNode != null)
		{
			Layer layer = (Layer) selectedNode.getUserObject();
			scaleField.setText((float) (Math.round(layer.getScale()*10))/10 + "");
			rotationField.setText((float) (Math.round((layer.getTheta()*180/Math.PI)*10))/10 + "");//Conversion rad en degree + "");
			opacityField.setText((float) (Math.round(layer.getVisible()*10))/10 + "");
			posXField.setText((float) (Math.round(layer.getPos().x*10))/10 + "");
			posYField.setText((float) (Math.round(layer.getPos().y*10))/10 + "");
			flipHCheck.setSelected(layer.getFlipH());
			flipVCheck.setSelected(layer.getFlipV());
		}
		else
		{
			scaleField.setText("");
			rotationField.setText("");//Conversion rad en degree + "");
			opacityField.setText("");
			posXField.setText("");
			posYField.setText("");
			flipHCheck.setSelected(false);
			flipVCheck.setSelected(false);
		}
		
	}

	public void copy()
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
		{
			copyLayer = null;
			return;
		}
		copyLayer = ((Layer) selectedNode.getUserObject()).clone();
	}
	public void paste()
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(copyLayer != null)
		{
			Layer layer = (Layer) selectedNode.getUserObject();
			layer.addLayer(copyLayer);
			selectedNode.add(new DefaultMutableTreeNode(copyLayer));
			copyLayer = copyLayer.clone();
			redrawTree();
		}
	}
}
