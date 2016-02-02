import java.text.DecimalFormat;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;

/**
 *
 */

/**
 * @author Kaito
 *
 */
public class Starter implements Runnable
{
    // solver variables
    int size;
    int multiple = 2;
    float dt = 0.2f;
    FluidSolver3D fs = new FluidSolver3D();

    // drawing
    Thread artist = null;
    TransformGroup objTransform;
    TransparencyAttributes[][][] ta;

    // Constructor
    public Starter(final int size, final TransparencyAttributes[][][] ta, final TransformGroup obj)
    {
        this.objTransform = obj;
        this.ta = ta;

        this.size = size * this.multiple;
        this.fs.setup(this.size, this.dt);
        this.createSmoke();
    }

    public void start()
    {
        this.artist = new Thread(this);
        this.artist.start();
    }

    public void stop()
    {
        this.artist = null;
        this.printStuff(this.fs.temp);
    }

    @Override
    public void run()
    {
        while (this.artist != null)
        {
            try
            {
                Thread.sleep(20);
            }
            catch (final InterruptedException e)
            {}

            this.createSmoke();
        }
        this.artist = null;
    }

    public void createSmoke()
    {
        this.fs.velocitySolver();
        this.fs.densitySolver();

        for (int x = 1; x <= this.size; x += this.multiple)
        {
            for (int y = 1; y <= this.size; y += this.multiple)
            {
                for (int z = 1; z <= this.size; z += this.multiple)
                {
                    float temp = 0.0f;

                    for (int i = x; i < this.multiple + x; i++)
                    {
                        for (int j = y; j < this.multiple + y; j++)
                        {
                            for (int k = z; k < this.multiple + z; k++)
                            {
                                // System.out.println(i + " " + j + " " + k);
                                temp += this.fs.d[i][j][k];
                            }
                        }
                    }

                    temp /= (this.multiple * this.multiple * this.multiple);
                    // draw density
                    if (temp > 0)
                    {
                        float tVal = (1.0f - temp);
                        if (tVal < 0) tVal = 0;
                        this.ta[(x + this.multiple - 1) / this.multiple - 1][(y + this.multiple - 1) / this.multiple
                                - 1][(z + this.multiple - 1) / this.multiple - 1].setTransparency(tVal);
                    }
                    else
                        this.ta[(x + this.multiple - 1) / this.multiple - 1][(y + this.multiple - 1) / this.multiple
                                - 1][(z + this.multiple - 1) / this.multiple - 1].setTransparency(1);
                }
            }
        }

        // for (int x = (7 - 1); x <= (7 + 1); x++)
        // {
        // for (int y = (7 - 1); y <= (7 + 1); y++)
        // {
        // for (int z = (7 - 1); z <= (7 + 1); z++)
        // {
        // //System.out.println(x + " " + y + " " + z + " " + fs.d[x][y][z]);
        // System.out.print(fs.d[x][y][z] + " ");
        // }
        // System.out.println();
        // }
        // System.out.println("--");
        // }
        // System.out.println("------------");
    }

    public void updateLocation(final boolean drag, int x, int y, int z, final int xOld, final int yOld, final int zOld)
    {
        // set boundries
        if (x > this.size) x = this.size;
        if (x < 1) x = 1;
        if (y > this.size) y = this.size;
        if (y < 1) y = 1;
        if (z > this.size) z = this.size;
        if (z < 1) z = 1;

        if (drag == true)
        {
            this.fs.uOld[x * this.multiple][y * this.multiple][z * this.multiple] = (x - xOld) * 50 * this.multiple;
            this.fs.vOld[x * this.multiple][y * this.multiple][z * this.multiple] = (y - yOld) * 50 * this.multiple;
            this.fs.wOld[x * this.multiple][y * this.multiple][z * this.multiple] = (z - zOld) * 50 * this.multiple;
        }
        else
            this.fs.dOld[x * this.multiple][y * this.multiple][z * this.multiple] = 100;
    }

    public void reset()
    {
        this.fs.reset();

        final Transform3D temp = new Transform3D();
        temp.rotX(0);
        this.objTransform.setTransform(temp);

        this.createSmoke();
    }

    public void nextStep()
    {
        this.stop();
        this.createSmoke();
        this.printStuff(this.fs.temp);
    }

    public void setDT(final float change)
    {
        this.dt += change;

        if (this.dt < 0.1f)
        {
            this.dt = 0.1f;
            return;
        }
        else if (this.dt > 1f)
        {
            this.dt = 1f;
            return;
        }

        // kill fp errors
        this.dt = Math.round(this.dt * 100);
        this.dt /= 100;
        System.out.println(this.dt);
    }

    public void printStuff(final float input[][][])
    {
        // DecimalFormat formatter = new DecimalFormat("0.000000000");
        final DecimalFormat formatter = new DecimalFormat("00");
        float scaled;

        for (int x = 1; x <= this.size; x++)
        {
            for (int y = 1; y <= this.size; y++)
            {
                scaled = input[x][y][this.size / 2] * 10000;
                if (scaled > 99) scaled = 99;
                if (scaled < -99) scaled = -99;

                if (scaled > -0.9 && scaled < 0.9)
                    System.out.print("... ");
                else if (scaled < 0)
                    System.out.print(formatter.format(scaled) + " ");
                else
                    System.out.print("+" + formatter.format(scaled) + " ");
            }
            System.out.println();
        }
    }
}
