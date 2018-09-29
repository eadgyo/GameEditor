package TestEngine;

import Addons.AdvForm;
import Addons.Animation;
import Addons.Entity;
import Maths.PointInt;
import Maths.Vector2D;
import org.cora.graphics.graphics.Graphics;
import org.cora.graphics.graphics.myColor;
import org.cora.graphics.input.Input;


import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
/**
 * Created by ronan-h on 29/05/16.
 */
public class Main
{
    public static void main(String[] args)
    {
        int WINDOW_WIDTH = 800;
        int WINDOW_HEIGHT = 600;

        Graphics g = new Graphics();
        g.init("Test", WINDOW_WIDTH, WINDOW_HEIGHT);
        g.initGL(WINDOW_WIDTH, WINDOW_HEIGHT);

        Input input = new Input();
        input.initGL(g.getScreen());

        Entity A = new Entity();
        Entity B = new Entity();
        A.setMass(289.00000f);

        Animation animA = new Animation();
        Animation animB = new Animation();
        animA.add("test", new PointInt(-2,-2), 0);
        animB.add("test", new PointInt(-2,-2), 0);

        AdvForm formA = new AdvForm();
        AdvForm formB = new AdvForm();
        formA.addPoint(new Vector2D(-50.208333f, -9.208335f));
        formA.addPoint(new Vector2D(-90.208333f, 7.7916675f));
        formA.addPoint(new Vector2D(50.916666f, 9.916668f));
        formA.addPoint(new Vector2D(7.7916675f, -9.208334f));
        formA.setRadians(2.12f);
        A.setPos(new Vector2D(150,150));


        formB.addPoint(new Vector2D(-50, -30));
        formB.addPoint(new Vector2D(50, -30));
        formB.addPoint(new Vector2D(50, 30));
        formB.addPoint(new Vector2D(-40, 30));

        B.setPosition(new Vector2D(150,500));
        animA.addAForm(0,0, formA);
        animB.addAForm(0,0, formB);
        A.setAnim(animA);
        B.setAnim(animB);

        animA.setCurrentAnim(0);
        animA.setCurrentFrame(0);
        animB.setCurrentAnim(0);
        animB.setCurrentFrame(0);

        while (glfwWindowShouldClose(g.getScreen()) == GL_FALSE)
        {
            g.clear();

            g.setColor(myColor.RED());
            g.drawForm(formA);

            g.setColor(myColor.GREEN());
            g.drawForm(formB);

            engine.update(0.016f);

            input.update();

            g.swapGL();
        }
    }
}
