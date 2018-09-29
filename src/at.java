
public class at {

}
//
//package MapEditor;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Container;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Insets;
//import java.awt.Rectangle;
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.StringSelection;
//import java.awt.datatransfer.Transferable;
//import java.awt.datatransfer.UnsupportedFlavorException;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.EventObject;
//import java.util.Stack;
//import java.util.Vector;
//
//import javax.swing.DefaultListModel;
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JMenuItem;
//import javax.swing.JOptionPane;
//import javax.swing.JPopupMenu;
//import javax.swing.JScrollPane;
//import javax.swing.JTree;
//import javax.swing.SpringLayout;
//import javax.swing.SwingUtilities;
//import javax.swing.TransferHandler;
//import javax.swing.UIManager;
//import javax.swing.event.TreeModelEvent;
//import javax.swing.event.TreeModelListener;
//import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.plaf.basic.BasicTreeUI;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeCellEditor;
//import javax.swing.tree.DefaultTreeCellRenderer;
//import javax.swing.tree.DefaultTreeModel;
//import javax.swing.tree.TreeModel;
//import javax.swing.tree.TreeNode;
//import javax.swing.tree.TreePath;
//import javax.swing.tree.TreeSelectionModel;
//
//import Addons.AdvEntity;
//import Addons.Entity;
//import Base.Image;
//import BaseWindows.EntitiesDisplay;
//import Map.Layer;
//import Maths.PointInt;
//
//public class LayersTree extends Container implements TreeSelectionListener, ActionListener, MouseListener, MouseMotionListener
//{
//	private static class MyTreeCellRenderer extends DefaultTreeCellRenderer
//	{
//        @Override
//        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
//        {
//        	JComponent c = (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//            // decide what icons you want by examining the node
//            if (value instanceof DefaultMutableTreeNode) 
//            {	
//            	
//                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
//                if (node.getUserObject() instanceof Layer) 
//                {
//                    // decide based on some property of your Contact obj
//                    Layer layer = (Layer) node.getUserObject();
//                    if(layer.getObject() == null) 
//                    {
//                        setIcon(UIManager.getIcon("FileView.directoryIcon"));
//                    }
//                    else
//                    {
//                    	setIcon(UIManager.getIcon("FileView.fileIcon"));
//                    }
//                }
//            }
//
//            return this;
//        }
//	}
//	class NonRootEditor extends DefaultTreeCellEditor
//	{
//	    private final DefaultTreeCellRenderer renderer;
//	 
//	    public NonRootEditor(JTree tree, DefaultTreeCellRenderer renderer)
//	    {
//	        super(tree, renderer);
//	        this.renderer = renderer;
//	    }
//	    
//	    @Override
//	    protected boolean canEditImmediately(EventObject event) 
//	    {
//	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//	    	if(node == null || node == root)
//	    		return false;
//	    	Layer layer = (Layer) node.getUserObject();
//	    	
//	        if((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event) && layer.getObject() == null) 
//	        {
//	            MouseEvent me = (MouseEvent)event;
//	            return ((me.getClickCount() >= 2) && inHitRegion(me.getX(), me.getY()));
//	        }
//	        return (event == null);
//	    }
//	    
//	}
//	class MyTreeModel extends DefaultTreeModel
//	{
//		public MyTreeModel(DefaultMutableTreeNode root) 
//		{
//			super(root);
//		}
//		@Override
//		public void valueForPathChanged(TreePath path, Object newValue) 
//	    {
//			DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) path.getLastPathComponent();
//			((Layer) aNode.getUserObject()).setName((String) newValue);
//			redrawTree();
//	    }
//	}
//	class MyTree extends JTree
//	{
//		public MyTree(MyTreeModel myTreeModel) 
//		{
//			super(myTreeModel);
//			setCellRenderer(new MyTreeCellRenderer());
//			setCellEditor(new NonRootEditor(this, (DefaultTreeCellRenderer) this.getCellRenderer()));
//			setUI(new NiceTreeUI());
//		}
//
//		/*@Override 
//		public void paintComponent(Graphics g) 
//		{
//			/*g.setColor(getBackground());
//			g.fillRect(0, 0, getWidth(), getHeight());
//			if (getSelectionCount() > 0) {
//				g.setColor(Color.darkGray);
//				for (int i : getSelectionRows()) {
//					Rectangle r = getRowBounds(i);
//					g.fillRect(r.x, r.y, getWidth() - r.x, r.height);
//				}
//			}*/
//			//this
//			//super.paintComponent(g);
//			/*if (getLeadSelectionPath() != null) 
//			{
//				Rectangle r = getRowBounds(getRowForPath(getLeadSelectionPath()));
//				g.setColor(Color.darkGray);
//				g.fillRect(0, 0, getWidth(), r.height);
//			}
//		}*/
//		  
//	}
//	public class NiceTreeUI extends BasicTreeUI
//	{
//	    @Override
//	    protected void paintRow( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
//	        TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
//	    {
//	        boolean isRowSelected = tree.isRowSelected( row );
//	        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
//	        /*if( isRowSelected && isLeaf )
//	        {
//	            Graphics g2 = g.create();
//	 
//	            g2.setColor( Color.RED );
//	            g2.fillRect( 0, bounds.y, tree.getWidth(), bounds.height );
//	 
//	            g2.dispose();
//	        }
//	 
//	        if( !isRowSelected && isLeaf )
//	        {
//	            Graphics g2 = g.create();
//	 
//	            if( row % 2 == 0 )
//	            {
//	                g2.setColor( Color.WHITE );
//	            }
//	            else
//	            {
//	                g2.setColor( new Color( 230, 230, 230 ) );
//	            }
//	            g2.fillRect( 0, bounds.y, tree.getWidth(), bounds.height );
//	 
//	            g2.dispose();
//	        }*/
//	        
//	    }
//	     
//	}
//	
//	
//	private JPopupMenu popupMenu;
//	//private JMenu arrondi ;
//	private JMenuItem renommer, supprimer;
//	
//	private Layer layer;
//	private EntitiesDisplay entitiesDisplay;
//
//	private JTree tree;
//	
//	private JScrollPane treeView;
//	private DefaultMutableTreeNode root;
//	
//	private JButton addFolder, addObject;
//	private boolean isDragging;
//	private DefaultMutableTreeNode selectedNode;
//	
//	public LayersTree(PointInt dim, EntitiesDisplay entitiesDisplay)
//	{
//		super();
//		popupMenu = new JPopupMenu();
//		
//		renommer = new JMenuItem("renommer");
//		supprimer = new JMenuItem("supprimer");
//		
//		popupMenu.add(renommer);
//		
//		renommer.addActionListener(this);
//		
//		isDragging = false;
//		this.entitiesDisplay = entitiesDisplay;
//		Layer layer = new Layer("root");
//		
//		root = new DefaultMutableTreeNode(layer);
//
//		tree = new MyTree(new MyTreeModel(root));
//		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		tree.setEditable(true);
//		tree.addTreeSelectionListener(this);
//		
//
//		tree.addMouseListener(this);
//		tree.addMouseMotionListener(this);
//		
//		treeView = new JScrollPane(tree);
//		treeView.setPreferredSize(new Dimension(dim.x, dim.y - 50));
//		
//		addFolder = new JButton("+ Calque");
//		addObject = new JButton("+ Objet");
//	
//		SpringLayout layout = new SpringLayout();
//		this.setLayout(layout);
//		
//		layout.putConstraint(SpringLayout.WEST, treeView, 0, SpringLayout.WEST, this);
//		layout.putConstraint(SpringLayout.NORTH, treeView, 0, SpringLayout.NORTH, this);
//		add(treeView);
//		
//		layout.putConstraint(SpringLayout.WEST, addFolder, 0, SpringLayout.WEST, this);
//		layout.putConstraint(SpringLayout.NORTH, addFolder, 0, SpringLayout.SOUTH, treeView);
//		add(addFolder);
//		
//		layout.putConstraint(SpringLayout.WEST, addObject, 5, SpringLayout.EAST, addFolder);
//		layout.putConstraint(SpringLayout.NORTH, addObject, 0, SpringLayout.SOUTH, treeView);
//		add(addObject);
//		
//		addFolder.addActionListener(this);
//		addObject.addActionListener(this);
//		
//		this.setPreferredSize(new Dimension(dim.x, dim.y));
//	}
//	
//	public void addObject(Entity object)
//	{
//		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//		if(selectedNode != null)
//		{
//			Layer actualLayer = (Layer) selectedNode.getUserObject();
//			Layer newLayer = new Layer(object);
//			
//			actualLayer.addLayer(newLayer);
//			
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newLayer);
//			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
//			
//			tree.expandPath(new TreePath(selectedNode.getPath()));
//		}
//		//treeModel.setRoot(root);
//		((DefaultTreeModel) tree.getModel()).nodeChanged(root);
//		
//	}
//	public void addFolder(String name)
//	{
//		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//		if(selectedNode != null)
//		{
//			Layer actualLayer = (Layer) selectedNode.getUserObject();
//			while(((Layer) selectedNode.getUserObject()).getObject() != null)
//			{
//				selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
//			}
//			
//			Layer newLayer = new Layer(name);
//			
//			actualLayer.addLayer(newLayer);
//			
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newLayer);
//			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
//			
//			tree.expandPath(new TreePath(selectedNode.getPath()));
//		}
//		//treeModel.setRoot(root);
//		//treeModel.nodeChanged(root);
//		
//		((DefaultTreeModel) tree.getModel()).nodeChanged(root);
//	}
//	public void recreateNodes(DefaultMutableTreeNode root, Layer layer)
//	{
//		ArrayList<Layer> layers = layer.getLayers();
//		for(int i=0; i<layers.size(); i++)
//		{
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(layers.get(i));
//			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, root, root.getChildCount());
//			recreateNodes(newNode, layers.get(i));
//		}
//	}
//	
//	@Override
//	public void valueChanged(TreeSelectionEvent e) 
//	{
//		/*DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//		if (node == null) //No selection  
//		    return;
//		
//		Object nodeInfo = node.getUserObject();
//	    if (node.isLeaf()) 
//	    {
//	        BookInfo book = (BookInfo)nodeInfo;
//	        displayURL(book.bookURL);
//	    } 
//	    else 
//	    {
//	        displayURL(helpURL); 
//	    }*/
//	}
//	@Override
//	public void actionPerformed(ActionEvent e) 
//	{
//		Object source = e.getSource();
//		if(source == renommer)
//		{
//			if(selectedNode != null && selectedNode != root)
//			{
//				
//				
//				/*
//				Layer layer = (Layer) selectedNode.getUserObject();
//				String name = JOptionPane.showInputDialog(null, "Nom du calque", layer.getName());
//			
//				if(name != null && name != "")
//					layer.setName(name);*/
//			}
//		}
//		else if(source == supprimer)
//		{
//			
//		}
//		else if(source == addFolder)
//		{
//			String name = JOptionPane.showInputDialog(null, "Nom du calque");
//			if(name != null && name != "")
//				addFolder(name);
//		}
//		else if(source == addObject)
//		{
//			Entity entity = entitiesDisplay.getObject();
//			if(entity != null)
//			{
//				addObject(entity);
//			}
//		}
//	}
//	@Override
//	public void mouseClicked(MouseEvent e) 
//	{
//		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//		
//		if(e.getClickCount() == 1 && e.getButton() == 3)
//		{
//			popupMenu.show(e.getComponent(), e.getX(), e.getY());
//		}
//		((DefaultTreeModel) tree.getModel()).nodeChanged(root);
//	}
//	@Override
//	public void mouseEntered(MouseEvent e) 
//	{
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void mouseExited(MouseEvent e) 
//	{
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void mousePressed(MouseEvent e) 
//	{
//		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//	}
//	@Override
//	public void mouseReleased(MouseEvent e) 
//	{	
//	    if(isDragging && selectedNode != null && selectedNode.getParent() != null && selectedNode != root)
//	    {
//	    	TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
//	    	if(pathFromPos != null)
//	    	{
//	    		DefaultMutableTreeNode selectedNodeFromPos = (DefaultMutableTreeNode) pathFromPos.getLastPathComponent();
//	    		if(selectedNodeFromPos != selectedNode && !selectedNodeFromPos.isNodeAncestor((selectedNode)))
//	    		{
//		    		Layer parent = (Layer) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
//		    		Layer draggingLayer = (Layer) selectedNode.getUserObject();
//		    		Layer newParent = (Layer) selectedNodeFromPos.getUserObject();
//		    		if(newParent.getObject() != null)
//		    			newParent = (Layer)  ((DefaultMutableTreeNode) selectedNodeFromPos.getParent()).getUserObject();
//		    		
//		    		assert(parent.getLayers().remove(draggingLayer));
//		    		
//		    		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);
//		    		
//		    		newParent.addLayer(draggingLayer);
//		    		
//					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(draggingLayer);
//					recreateNodes(newNode, draggingLayer);
//					((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNodeFromPos, selectedNodeFromPos.getChildCount());
//	    		}
//	    	}
//    		
//    		((DefaultTreeModel) tree.getModel()).nodeChanged(root);
//	    }
//	    
//	    isDragging = false;
//	    selectedNode = null;
//	    
//	    //Clique sur une surface blanche
//	    int row = tree.getRowForLocation(e.getX(),e.getY());
//	    if(row == -1)
//	        tree.clearSelection();
//	}
//
//	@Override
//	public void mouseDragged(MouseEvent e) 
//	{
//		isDragging = true;
//		TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
//		if(pathFromPos != null)
//		{
//			//DefaultMutableTreeNode selectedNodeFromPos = (DefaultMutableTreeNode) pathFromPos.getLastPathComponent();
//			tree.setSelectionPath(pathFromPos);
//		}
//		else
//		{
//			tree.clearSelection();
//		}
//		
//	}
//	@Override
//	public void mouseMoved(MouseEvent e) 
//	{
//		// TODO Auto-generated method stub
//		
//	}
//	
//	public void redrawTree() 
//	{
//		// First, capture the current expanded state of the JTree
//		Vector paths = new Vector();
//		for(Enumeration e = tree.getExpandedDescendants(new TreePath(root)); e.hasMoreElements();)
//		{
//			paths.addElement(e.nextElement());
//		}
//
//		// Force the JTree to rebuild itself
//		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
//		model.reload();
//
//		// Finally, recover the old expanded state of the JTree
//		for (int i=0; i<paths.size();i++) 
//		{
//			TreePath path = (TreePath)paths.elementAt(i);
//			tree.expandPath(path);
//		}
//	}
//
//}
