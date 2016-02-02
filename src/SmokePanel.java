
/**
 *
 */

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author Kaito
 *
 */
public class SmokePanel extends JPanel
{
    Starter s;

    // Constructor
    public SmokePanel()
    {
        // panel layout
        this.setLayout(new BorderLayout());
        this.setFocusable(true);

        final GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        final Canvas3D canvas3D = new Canvas3D(config);
        this.add("Center", canvas3D);

        final BranchGroup scene = this.createSceneGraph(canvas3D);

        final SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().setNominalViewingTransform();
        simpleU.addBranchGraph(scene);
    }

    public BranchGroup createSceneGraph(final Canvas3D canvas)
    {
        final BranchGroup objRoot = new BranchGroup();

        final TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        // create the cube
        final Grid grid = new Grid(15, 15, 15);
        objTransform.addChild(grid.getBG());
        objRoot.addChild(objTransform);

        // add mouse listener
        this.s = new Starter(15, grid.getTA(), objTransform);
        final PickCanvas picking = new PickCanvas(canvas, objRoot);
        picking.setMode(PickTool.GEOMETRY);
        final MyMouseListener mouse = new MyMouseListener(this.s, picking);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);

        // right mouse drag rotate
        final MiddleMouseRotate myMouseRotate = new MiddleMouseRotate();
        myMouseRotate.setTransformGroup(objTransform);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myMouseRotate);

        // key rotation
        final MyKeyListener keys = new MyKeyListener(this.s, objTransform);
        canvas.addKeyListener(keys);

        // set the color of the background to white
        final Background backg = new Background(1.0f, 1.0f, 1.0f);
        backg.setApplicationBounds(new BoundingSphere(new Point3d(), 1000.0));
        objRoot.addChild(backg);

        objRoot.compile();
        return objRoot;
    }

    public void start()
    {
        this.s.start();
    }

    public void nextStep()
    {
        this.s.nextStep();
    }

    public void reset()
    {
        this.s.reset();
    }

    public void stop()
    {
        this.s.stop();
    }
}
