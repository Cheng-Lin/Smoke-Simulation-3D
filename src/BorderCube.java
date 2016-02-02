
/**
 *
 */

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

/**
 * @author Kaito
 *
 */
public class BorderCube extends Shape3D
{
    public BorderCube(final float halfLength)
    {
        // create wire frame
        this.setGeometry(this.createBorder(halfLength));

        final ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.0f, 0.0f, 0.0f); // set the color of the wire frame to
                                       // black

        final LineAttributes la = new LineAttributes();
        la.setLineWidth(1.5f);

        final Appearance app = new Appearance();
        app.setColoringAttributes(ca);
        app.setLineAttributes(la);
        this.setAppearance(app);
    }

    private Geometry createBorder(final float halfLength)
    {
        final IndexedLineArray polyLine = new IndexedLineArray(8, GeometryArray.COORDINATES, 24);

        polyLine.setCoordinate(0, new Point3f(-halfLength, halfLength, halfLength));
        polyLine.setCoordinate(1, new Point3f(halfLength, halfLength, halfLength));
        polyLine.setCoordinate(2, new Point3f(halfLength, -halfLength, halfLength));
        polyLine.setCoordinate(3, new Point3f(-halfLength, -halfLength, halfLength));
        polyLine.setCoordinate(4, new Point3f(-halfLength, halfLength, -halfLength));
        polyLine.setCoordinate(5, new Point3f(halfLength, halfLength, -halfLength));
        polyLine.setCoordinate(6, new Point3f(halfLength, -halfLength, -halfLength));
        polyLine.setCoordinate(7, new Point3f(-halfLength, -halfLength, -halfLength));

        polyLine.setCoordinateIndex(0, 0);
        polyLine.setCoordinateIndex(1, 1);
        polyLine.setCoordinateIndex(2, 1);
        polyLine.setCoordinateIndex(3, 2);
        polyLine.setCoordinateIndex(4, 2);
        polyLine.setCoordinateIndex(5, 3);
        polyLine.setCoordinateIndex(6, 3);
        polyLine.setCoordinateIndex(7, 0);
        polyLine.setCoordinateIndex(8, 0);
        polyLine.setCoordinateIndex(9, 4);
        polyLine.setCoordinateIndex(10, 4);
        polyLine.setCoordinateIndex(11, 5);
        polyLine.setCoordinateIndex(12, 5);
        polyLine.setCoordinateIndex(13, 1);
        polyLine.setCoordinateIndex(14, 2);
        polyLine.setCoordinateIndex(15, 6);
        polyLine.setCoordinateIndex(16, 6);
        polyLine.setCoordinateIndex(17, 5);
        polyLine.setCoordinateIndex(18, 6);
        polyLine.setCoordinateIndex(19, 7);
        polyLine.setCoordinateIndex(20, 7);
        polyLine.setCoordinateIndex(21, 3);
        polyLine.setCoordinateIndex(22, 7);
        polyLine.setCoordinateIndex(23, 4);

        return polyLine;
    }
}
