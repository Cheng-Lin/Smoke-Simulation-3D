
/**
 *
 */

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;

/**
 * @author Kaito
 *
 */
public class MyMouseListener implements MouseListener, MouseMotionListener
{
    private Starter starter;
    private final PickCanvas picking;
    private int x, y, z;

    /**
     * 
     */
    public MyMouseListener(final PickCanvas picking)
    {
        this.picking = picking;
    }

    public MyMouseListener(final Starter starter, final PickCanvas picking)
    {
        this.starter = starter;
        this.picking = picking;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
     * MouseEvent)
     */
    @Override
    public void mouseDragged(final MouseEvent e)
    {
        // active when left mouse is used
        if (e.getModifiers() != InputEvent.BUTTON2_MASK)
        {
            this.picking.setShapeLocation(e);
            final PickResult result = this.picking.pickAny();

            if (result != null)
            {
                final Primitive p = (Primitive)result.getNode(PickResult.PRIMITIVE);

                if (p != null)
                {
                    final String s = p.getName();

                    final int x2 = this.x;
                    final int y2 = this.y;
                    final int z2 = this.z;
                    this.x = Integer.parseInt(s.substring(0, 3));
                    this.y = Integer.parseInt(s.substring(3, 6));
                    this.z = Integer.parseInt(s.substring(6));

                    if (e.getModifiers() == InputEvent.BUTTON3_MASK)
                        this.starter.updateLocation(true, this.x, this.y, this.z, x2, y2, z2);
                    else
                        this.starter.updateLocation(false, this.x, this.y, this.z, 0, 0, 0);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        // active when left mouse is used
        if (e.getModifiers() == InputEvent.BUTTON1_MASK)
        {
            this.picking.setShapeLocation(e);
            final PickResult result = this.picking.pickAny();

            if (result != null)
            {
                final Primitive p = (Primitive)result.getNode(PickResult.PRIMITIVE);

                if (p != null)
                {
                    final String s = p.getName();

                    this.x = Integer.parseInt(s.substring(0, 3));
                    this.y = Integer.parseInt(s.substring(3, 6));
                    this.z = Integer.parseInt(s.substring(6));

                    this.starter.updateLocation(false, this.x, this.y, this.z, 0, 0, 0);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {}

}
