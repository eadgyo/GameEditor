package Base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.imageio.ImageIO;

import Addons.AdvAnimation;
import Addons.AdvEntity;
import Addons.AdvForm;
import Addons.Animation;
import Addons.Entity;
import Addons.Key;
import Addons.KeyBool;
import Addons.KeyFloat;
import Addons.KeyInt;
import MapEditor.Main;
import Maths.Form;
import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;
import Maths.sRectangle;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Attribute;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//Singleton
public class FileManager
{
	private DocumentBuilderFactory factory;
	
	//parseur
	public final String binFolder = "bin_";
	public final String objectsFolder = binFolder + "/" + "Objects";;
	public final String textureFolder = "pic";
	
	ArrayList<BufferedImage> textures;
	HashMap<String, Integer> names;
	
	//Singleton object
	private static FileManager INSTANCE = new FileManager();
	
	private FileManager()
	{
		textures = new ArrayList<BufferedImage>();
		names = new HashMap<String, Integer>();
		
		initialize();
	}
	public void initialize()
	{
		boolean creation = createFolder(binFolder);
		boolean creation1 = createFolder(objectsFolder);
		loadDefTextures();
	}
	
	//Load
	public BufferedImage getDefTexture(String textureName)
	{
		if(textureName.contentEquals(""))
			return null;
		assert(names.containsKey(textureName));
		
		int i = names.get(textureName);
		return this.textures.get(i);
	}
	private void loadDefTextures()
	{
		ArrayList<String> names = getAllFilesName(textureFolder, true);
		for(int i=0; i<names.size(); i++)
		{
			this.names.put(names.get(i), i);
			textures.add(loadTexture(textureFolder + "/" + names.get(i) , false));
		}
	}
	public BufferedImage loadTexture(String file, boolean isInternal)
	{
		BufferedImage l_texture;
		try 
		{
			if(isInternal)
			{
				URL url = Main.class.getResource(file);
				l_texture = ImageIO.read(url);
			}
			else
				l_texture = ImageIO.read(new File(file));
		} 
		catch (IOException e) 
		{
			l_texture = null;
		}
		return l_texture;
	}
	public ArrayList<Image> loadImages(String file, Graphics g, int size, boolean isInternal)
	{
		//Get images from texture file
		ArrayList<Image> l_images = new ArrayList<Image>();
		BufferedImage l_texture = loadTexture(file, isInternal);
		int cols = (int)(l_texture.getWidth())/size;
		int l_maxImages = (int) ((l_texture.getWidth()*l_texture.getHeight())/(size*size));
		
		//Get the name of the file
		File f = new File(file);  
		String fileName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/")+1);

		for(int i=0; i<l_maxImages; i++)
		{
			Image l_image = new Image();
			l_image.initialize(g, size, size, cols, l_texture, fileName);
			l_image.setCurrentFrame(i);
			l_images.add(l_image);
		}
		return l_images;
	}
	
	//external only
	public ArrayList<String> getAllFilesName(String Directory, boolean canCreate)
	{
		ArrayList<String> files = new ArrayList<String>();
		File folder = new File(Directory);
		if (folder.exists()) 
		{
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) 
			{
				if (listOfFiles[i].isFile()) 
				{
					files.add(listOfFiles[i].getName());
				} 
		    }
		}
		else if(canCreate)
		{
			try
			{
				folder.mkdir();
			}
			catch(SecurityException se)
			{
		        
		    }        
		}
		return files;
	}
	public boolean isFolderExisting(String Directory)
	{
		File folder = new File(Directory);
		return folder.exists();
	}
	public boolean createFolder(String Directory)
	{
		//Si le dossier existe on ne fait rien
		//Si il n'existe pas on le crée
		File folder = new File(Directory);
		if (!folder.exists())
		{
			try
			{
				folder.mkdir();
				return true;
			}
			catch(SecurityException se)
			{
		        
		    }
		}
		return false;
	}
	public boolean isFileExisting(String Directory)
	{
		File file = new File(Directory);
		return file.exists() && file.isFile();
	}
	
	public void saveObject(Object object, String directory)
	{
		try 
		{
			FileOutputStream f_out = new FileOutputStream(directory);
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(object);
			obj_out.close();
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	public void saveObject(Object object)
	{
		String name = object.getClass().getName();
		saveObject(object, objectsFolder + "/" + name + ".data");
	}
	
	public Object getObject(String directory)
	{
		Object obj = null;
		
		if(this.isFileExisting(directory))
		{
			try 
			{
				FileInputStream f_in = new FileInputStream(directory);
				ObjectInputStream obj_in = new ObjectInputStream(f_in);
				obj = obj_in.readObject();
				obj_in.close();
			} 
			catch (FileNotFoundException e1) 
			{
				e1.printStackTrace();
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		return obj;
	}
	public Object getObjectInside(String directory)
	{
		return getObject(objectsFolder + "/" + directory);
	}
	
	public static FileManager getInstance()
	{	
		return INSTANCE;
	}
}
