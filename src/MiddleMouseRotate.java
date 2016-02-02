
/*
 * $RCSfile$
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistribution of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS
 * LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * $Revision$ $Date$ $State$
 */

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;

/**
 * MouseRotate is a Java3D behavior object that lets users control the rotation
 * of an object via a mouse.
 * <p>
 * To use this utility, first create a transform group that this rotate behavior
 * will operate on. Then, <blockquote>
 * 
 * <pre>
 *
 * MouseRotate behavior = new MouseRotate();
 * behavior.setTransformGroup(objTrans);
 * objTrans.addChild(behavior);
 * behavior.setSchedulingBounds(bounds);
 *
 * </pre>
 * 
 * </blockquote> The above code will add the rotate behavior to the transform
 * group. The user can rotate any object attached to the objTrans.
 */

public class MiddleMouseRotate extends MouseBehavior
{
    double x_angle, y_angle;
    double x_factor = .03;
    double y_factor = .03;

    private MouseBehaviorCallback callback = null;

    /**
     * Creates a rotate behavior given the transform group.
     * 
     * @param transformGroup
     *            The transformGroup to operate on.
     */
    public MiddleMouseRotate(final TransformGroup transformGroup)
    {
        super(transformGroup);
    }

    /**
     * Creates a default mouse rotate behavior.
     **/
    public MiddleMouseRotate()
    {
        super(0);
    }

    /**
     * Creates a rotate behavior. Note that this behavior still needs a
     * transform group to work on (use setTransformGroup(tg)) and the transform
     * group must add this behavior.
     * 
     * @param flags
     *            interesting flags (wakeup conditions).
     */
    public MiddleMouseRotate(final int flags)
    {
        super(flags);
    }

    /**
     * Creates a rotate behavior that uses AWT listeners and behavior posts
     * rather than WakeupOnAWTEvent. The behavior is added to the specified
     * Component. A null component can be passed to specify the behavior should
     * use listeners. Components can then be added to the behavior with the
     * addListener(Component c) method.
     * 
     * @param c
     *            The Component to add the MouseListener and MouseMotionListener
     *            to.
     * @since Java 3D 1.2.1
     */
    public MiddleMouseRotate(final Component c)
    {
        super(c, 0);
    }

    /**
     * Creates a rotate behavior that uses AWT listeners and behavior posts
     * rather than WakeupOnAWTEvent. The behaviors is added to the specified
     * Component and works on the given TransformGroup. A null component can be
     * passed to specify the behavior should use listeners. Components can then
     * be added to the behavior with the addListener(Component c) method.
     * 
     * @param c
     *            The Component to add the MouseListener and MouseMotionListener
     *            to.
     * @param transformGroup
     *            The TransformGroup to operate on.
     * @since Java 3D 1.2.1
     */
    public MiddleMouseRotate(final Component c, final TransformGroup transformGroup)
    {
        super(c, transformGroup);
    }

    /**
     * Creates a rotate behavior that uses AWT listeners and behavior posts
     * rather than WakeupOnAWTEvent. The behavior is added to the specified
     * Component. A null component can be passed to specify the behavior should
     * use listeners. Components can then be added to the behavior with the
     * addListener(Component c) method. Note that this behavior still needs a
     * transform group to work on (use setTransformGroup(tg)) and the transform
     * group must add this behavior.
     * 
     * @param flags
     *            interesting flags (wakeup conditions).
     * @since Java 3D 1.2.1
     */
    public MiddleMouseRotate(final Component c, final int flags)
    {
        super(c, flags);
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.x_angle = 0;
        this.y_angle = 0;
        if ((this.flags & MouseBehavior.INVERT_INPUT) == MouseBehavior.INVERT_INPUT)
        {
            this.invert = true;
            this.x_factor *= -1;
            this.y_factor *= -1;
        }
    }

    /**
     * Return the x-axis movement multipler.
     **/
    public double getXFactor()
    {
        return this.x_factor;
    }

    /**
     * Return the y-axis movement multipler.
     **/
    public double getYFactor()
    {
        return this.y_factor;
    }

    /**
     * Set the x-axis amd y-axis movement multipler with factor.
     **/
    public void setFactor(final double factor)
    {
        this.x_factor = this.y_factor = factor;
    }

    /**
     * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
     * respectively.
     **/
    public void setFactor(final double xFactor, final double yFactor)
    {
        this.x_factor = xFactor;
        this.y_factor = yFactor;
    }

    @Override
    public void processStimulus(final Enumeration criteria)
    {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        MouseEvent evt;
        // int id;
        // int dx, dy;

        while (criteria.hasMoreElements())
        {
            wakeup = (WakeupCriterion)criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent)
            {
                events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                if (events.length > 0)
                {
                    evt = (MouseEvent)events[events.length - 1];
                    this.doProcess(evt);
                }
            }

            else if (wakeup instanceof WakeupOnBehaviorPost)
            {
                while (true)
                {
                    // access to the queue must be synchronized
                    synchronized (this.mouseq)
                    {
                        if (this.mouseq.isEmpty()) break;
                        evt = (MouseEvent)this.mouseq.remove(0);
                        // consolidate MOUSE_DRAG events
                        while ((evt.getID() == MouseEvent.MOUSE_DRAGGED) && !this.mouseq.isEmpty()
                                && (((MouseEvent)this.mouseq.get(0)).getID() == MouseEvent.MOUSE_DRAGGED))
                        {
                            evt = (MouseEvent)this.mouseq.remove(0);
                        }
                    }
                    this.doProcess(evt);
                }
            }

        }
        this.wakeupOn(this.mouseCriterion);
    }

    void doProcess(final MouseEvent evt)
    {
        int id;
        int dx, dy;

        this.processMouseEvent(evt);
        if (((this.buttonPress) && ((this.flags & MouseBehavior.MANUAL_WAKEUP) == 0))
                || ((this.wakeUp) && ((this.flags & MouseBehavior.MANUAL_WAKEUP) != 0)))
        {
            id = evt.getID();
            if ((id == MouseEvent.MOUSE_DRAGGED) && evt.isAltDown() && !evt.isMetaDown())
            {
                this.x = evt.getX();
                this.y = evt.getY();

                dx = this.x - this.x_last;
                dy = this.y - this.y_last;

                if (!this.reset)
                {
                    this.x_angle = dy * this.y_factor;
                    this.y_angle = dx * this.x_factor;

                    this.transformX.rotX(this.x_angle);
                    this.transformY.rotY(this.y_angle);

                    this.transformGroup.getTransform(this.currXform);

                    final Matrix4d mat = new Matrix4d();
                    // Remember old matrix
                    this.currXform.get(mat);

                    // Translate to origin
                    this.currXform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
                    if (this.invert)
                    {
                        this.currXform.mul(this.currXform, this.transformX);
                        this.currXform.mul(this.currXform, this.transformY);
                    }
                    else
                    {
                        this.currXform.mul(this.transformX, this.currXform);
                        this.currXform.mul(this.transformY, this.currXform);
                    }

                    // Set old translation back
                    final Vector3d translation = new Vector3d(mat.m03, mat.m13, mat.m23);
                    this.currXform.setTranslation(translation);

                    // Update xform
                    this.transformGroup.setTransform(this.currXform);

                    this.transformChanged(this.currXform);

                    if (this.callback != null)
                        this.callback.transformChanged(MouseBehaviorCallback.ROTATE, this.currXform);
                }
                else
                {
                    this.reset = false;
                }

                this.x_last = this.x;
                this.y_last = this.y;
            }
            else if (id == MouseEvent.MOUSE_PRESSED)
            {
                this.x_last = evt.getX();
                this.y_last = evt.getY();
            }
        }
    }

    /**
     * Users can overload this method which is called every time the Behavior
     * updates the transform
     *
     * Default implementation does nothing
     */
    public void transformChanged(final Transform3D transform)
    {}

    /**
     * The transformChanged method in the callback class will be called every
     * time the transform is updated
     */
    public void setupCallback(final MouseBehaviorCallback callback)
    {
        this.callback = callback;
    }
}
