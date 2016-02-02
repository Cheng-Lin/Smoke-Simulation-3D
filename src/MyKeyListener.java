import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

public class MyKeyListener implements KeyListener
{
    Starter starter;

    TransformGroup objTransform;
    Transform3D rotationX = new Transform3D();
    Transform3D rotationY = new Transform3D();

    double xRotate = 0.0;
    double yRotate = 0.0;

    public MyKeyListener(final Starter s, final TransformGroup obj)
    {
        this.starter = s;
        this.objTransform = obj;
    }

    @Override
    public void keyPressed(final KeyEvent arg0)
    {
        if (arg0.getKeyCode() == KeyEvent.VK_UP)
        {
            this.xRotate -= 0.1;
            this.rotationX.rotX(this.xRotate);
            this.rotationY.rotY(this.yRotate);
            this.rotationX.mul(this.rotationY);
            this.objTransform.setTransform(this.rotationX);
        }
        else if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
        {
            this.xRotate += 0.1;
            this.rotationX.rotX(this.xRotate);
            this.rotationY.rotY(this.yRotate);
            this.rotationX.mul(this.rotationY);
            this.objTransform.setTransform(this.rotationX);
        }

        if (arg0.getKeyCode() == KeyEvent.VK_LEFT)
        {
            this.yRotate -= 0.1;
            this.rotationX.rotX(this.xRotate);
            this.rotationY.rotY(this.yRotate);
            this.rotationY.mul(this.rotationX);
            this.objTransform.setTransform(this.rotationY);
        }
        else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            this.yRotate += 0.1;
            this.rotationX.rotX(this.xRotate);
            this.rotationY.rotY(this.yRotate);
            this.rotationY.mul(this.rotationX);
            this.objTransform.setTransform(this.rotationY);
        }

        if (arg0.getKeyCode() == KeyEvent.VK_N)
        {
            this.starter.createSmoke();
        }
        else if (arg0.getKeyCode() == KeyEvent.VK_EQUALS)
        {
            this.starter.setDT(0.05f);
        }
        else if (arg0.getKeyCode() == KeyEvent.VK_MINUS)
        {
            this.starter.setDT(-0.05f);
        }

    }

    @Override
    public void keyReleased(final KeyEvent arg0)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(final KeyEvent arg0)
    {
        // TODO Auto-generated method stub
    }

    private double setBounds(final double angle)
    {
        if (angle > Math.PI)
            return angle - Math.PI;
        else if (angle < 0)
            return Math.PI + angle;
        else
            return 0;
    }

}
