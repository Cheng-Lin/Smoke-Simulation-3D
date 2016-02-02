
/**
 *
 */

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;

import com.sun.j3d.utils.applet.MainFrame;

/**
 * @author Kaito
 *
 */
public class SmokeSimulation3D extends JApplet
{
    SmokePanel smoke;

    // Initiating
    @Override
    public void init()
    {
        this.smoke = new SmokePanel();

        final ControlPanel control = new ControlPanel(this.smoke);

        // add panels to the applet windows
        final Container c = this.getContentPane();
        c.add(control, BorderLayout.NORTH);
        c.add(this.smoke, BorderLayout.CENTER);
    }

    @Override
    public void stop()
    {
        this.smoke.stop();
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        // convert applet into application
        new MainFrame(new SmokeSimulation3D(), 400, 436);
    }
}
