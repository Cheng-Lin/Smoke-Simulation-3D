
/**
 *
 */

import java.text.DecimalFormat;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * @author Kaito
 *
 */
public class Grid
{
    private BranchGroup grid;
    private TransparencyAttributes[][][] ta;

    private final int xMax, yMax, zMax;

    // Constructor
    public Grid(final int xMax, final int yMax, final int zMax)
    {
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;

        this.createGrid();
    }

    // Create Grid
    private void createGrid()
    {
        // cube dimension
        final float halfLength = 0.6f / this.findMax(this.xMax, this.yMax, this.zMax);
        final float dimension = 2 * halfLength;

        // position adjust factor
        final float adjustX = (this.xMax + 1) / 2.0f * dimension;
        final float adjustY = (this.yMax + 1) / 2.0f * dimension;
        final float adjustZ = (this.zMax + 1) / 2.0f * dimension;

        final DecimalFormat formatter = new DecimalFormat("000");

        this.grid = new BranchGroup();
        final TransformGroup cube = new TransformGroup();
        this.ta = new TransparencyAttributes[this.xMax][this.yMax][this.zMax];

        // create cube
        for (int x = 0; x < this.xMax; x++)
        {
            final float xPos = (x + 1) * dimension - adjustX; // position in
                                                              // x-axis adjust
            for (int y = 0; y < this.yMax; y++)
            {
                final float yPos = (y + 1) * dimension - adjustY; // position in
                                                                  // y-axis
                                                                  // adjust
                for (int z = 0; z < this.zMax; z++)
                {
                    // cell
                    final ColoringAttributes ca = new ColoringAttributes();
                    ca.setColor(0.0f, 0.0f, 0.0f);

                    this.ta[x][y][z] = new TransparencyAttributes(TransparencyAttributes.NICEST, 1.0f);
                    this.ta[x][y][z].setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

                    final Appearance app = new Appearance();
                    app.setColoringAttributes(ca);
                    app.setTransparencyAttributes(this.ta[x][y][z]);

                    final Box box = new Box(halfLength, halfLength, halfLength, app);
                    box.setName(formatter.format(x + 1) + formatter.format(y + 1) + formatter.format(z + 1));
                    box.setCapability(Node.ENABLE_PICK_REPORTING);
                    box.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);

                    // translate the cell to it's right position
                    final Transform3D translate = new Transform3D();
                    translate.setTranslation(new Vector3f(xPos, yPos, (z + 1) * dimension - adjustZ));
                    final TransformGroup tg = new TransformGroup(translate);

                    // add cell to cube
                    tg.addChild(box);
                    cube.addChild(tg);
                }
            }
        }

        final Transform3D rotationX = new Transform3D();
        rotationX.rotX(Math.PI);
        cube.setTransform(rotationX);

        final BorderCube border = new BorderCube(0.6f);
        this.grid.addChild(cube);
        this.grid.addChild(border);
    }

    public BranchGroup getBG()
    {
        return this.grid;
    }

    public TransparencyAttributes[][][] getTA()
    {
        return this.ta;
    }

    // Find the Largest Number among xMax, yMax and zMax
    private float findMax(final int xMax, final int yMax, final int zMax)
    {
        final int temp = Math.max(xMax, yMax);
        return (Math.max(temp, zMax));
    }
}
