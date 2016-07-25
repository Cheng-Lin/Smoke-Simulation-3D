public class FluidSolver3D
{
    int size, bSize;
    float dt;

    float visc = 0.0f;
    float diff = 0.0f;

    float[][][] tmp;

    float[][][] d, dOld;
    float[][][] u, uOld;
    float[][][] v, vOld;
    float[][][] w, wOld;
    float[][][] curl;
    float[][][] temp;

    float i, j, k;

    // Constructor
    public void setup(final int size, final float dt)
    {
        this.size = size;
        this.dt = dt;
        this.bSize = this.size + 2;

        this.reset();
    }

    /**
     * Reset the datastructures. We use 1d arrays for speed.
     **/
    public void reset()
    {
        this.d = new float[this.bSize][this.bSize][this.bSize];
        this.dOld = new float[this.bSize][this.bSize][this.bSize];
        this.u = new float[this.bSize][this.bSize][this.bSize];
        this.uOld = new float[this.bSize][this.bSize][this.bSize];
        this.v = new float[this.bSize][this.bSize][this.bSize];
        this.vOld = new float[this.bSize][this.bSize][this.bSize];
        this.w = new float[this.bSize][this.bSize][this.bSize];
        this.wOld = new float[this.bSize][this.bSize][this.bSize];
        this.curl = new float[this.bSize][this.bSize][this.bSize];
        this.temp = new float[this.bSize][this.bSize][this.bSize];

        for (int i = 0; i < this.bSize; i++)
        {
            for (int j = 0; j < this.bSize; j++)
            {
                for (int k = 0; k < this.bSize; k++)
                {
                    this.u[i][j][k] = this.uOld[i][j][k] = 0.0f;
                    this.v[i][j][k] = this.vOld[i][j][k] = 0.0f;
                    this.w[i][j][k] = this.wOld[i][j][k] = 0.0f;
                    this.d[i][j][k] = this.dOld[i][j][k] = this.curl[i][j][k] = 0.0f;
                    this.temp[i][j][k] = 0.0f;
                }
            }
        }

        this.d[this.bSize / 2][this.bSize - 2][this.bSize / 2] = 100;
        this.d[this.bSize / 2][1][this.bSize / 2] = 100;
    }

    /**
     * Calculate the buoyancy force as part of the velocity solver. Fbuoy =
     * -a*d*Y + b*(T-Tamb)*Y where Y = (0,1,0). The constants a and b are
     * positive with appropriate (physically meaningful) units. T is the
     * temperature at the current cell, Tamb is the average temperature of the
     * fluid grid. The density d provides a mass that counteracts the buoyancy
     * force.
     *
     * In this simplified implementation, we say that the tempterature is
     * synonymous with density (since smoke is *hot*) and because there are no
     * other heat sources we can just use the density field instead of a new,
     * seperate temperature field.
     *
     * @param Fbuoy
     *            Array to store buoyancy force for each cell.
     **/
    public void buoyancy(final float[][][] Fbuoy)
    {
        float Tamb = 0;
        final float a = 0.000625f;
        final float b = 0.025f;

        // sum all temperatures
        for (int i = 1; i <= this.size; i++)
        {
            for (int j = 1; j <= this.size; j++)
            {
                for (int k = 1; k <= this.size; k++)
                {
                    Tamb += this.d[i][j][k];
                }
            }
        }

        // get average temperature
        Tamb /= (this.size * this.size * this.size);

        // for each cell compute buoyancy force
        for (int i = 1; i <= this.size; i++)
        {
            for (int j = 1; j <= this.size; j++)
            {
                for (int k = 1; k <= this.size; k++)
                {
                    Fbuoy[i][j][k] = a * this.d[i][j][k] + -b * (this.d[i][j][k] - Tamb);
                }
            }
        }
    }

    /**
     * Calculate the curl at position (x, y, z) in the fluid grid. Physically
     * this represents the vortex strength at the cell. Computed as follows: w =
     * (del x U) where U is the velocity vector at (x, y, z).
     **/
    public float curl(final int x, final int y, final int z)
    {
        final float dw_dy = (this.w[x][y + 1][z] - this.w[x][y - 1][z]) * 0.5f;
        final float dv_dz = (this.v[x][y][z + 1] - this.v[x][y][z - 1]) * 0.5f;
        final float dw_dx = (this.w[x + 1][y][z] - this.w[x - 1][y][z]) * 0.5f;
        final float du_dz = (this.u[x][y][z + 1] - this.u[x][y][z - 1]) * 0.5f;
        final float du_dy = (this.u[x][y + 1][z] - this.u[x][y - 1][z]) * 0.5f;
        final float dv_dx = (this.v[x + 1][y][z] - this.v[x - 1][y][z]) * 0.5f;

        this.i = dw_dy - dv_dz;
        this.j = -(dw_dx - du_dz);
        this.k = dv_dx - du_dy;

        return (float)Math.sqrt(this.i * this.i + this.j * this.j + this.k * this.k);
    }

    /**
     * Calculate the vorticity confinement force for each cell in the fluid
     * grid. At a point (x,y,z), Fvc = N x w where w is the curl at (x,y,z) and
     * N = del |w| / |del |w||. N is the vector pointing to the vortex center,
     * hence we add force perpendicular to N.
     *
     * @param Fvc_x
     *            The array to store the x component of the vorticity
     *            confinement force for each cell.
     * @param Fvc_y
     *            The array to store the y component of the vorticity
     *            confinement force for each cell.
     * @param Fvc_z
     *            The array to store the z component of the vorticity
     *            confinement force for each cell.
     **/
    public void vorticityConfinement(final float[][][] Fvc_x, final float[][][] Fvc_y, final float[][][] Fvc_z)
    {
        float dw_dx, dw_dy, dw_dz;
        float length;
        final float v;
        final float epsilon = 1f;
        final float h = 1f;

        // Calculate magnitude of curl(u,v,w) for each cell. (|w|)
        for (int x = 1; x <= this.size; x++)
        {
            for (int y = 1; y <= this.size; y++)
            {
                for (int z = 1; z <= this.size; z++)
                {
                    this.curl[x][y][z] = this.curl(x, y, z);
                }
            }
        }

        for (int x = 2; x < this.size; x++)
        {
            for (int y = 2; y < this.size; y++)
            {
                for (int z = 2; z < this.size; z++)
                {
                    // Find derivative of the magnitude (n = del |w|)
                    dw_dx = (this.curl[x + 1][y][z] - this.curl[x - 1][y][z]) * 0.5f;
                    dw_dy = (this.curl[x][y + 1][z] - this.curl[x][y - 1][z]) * 0.5f;
                    dw_dz = (this.curl[x][y][z + 1] - this.curl[x][y][z - 1]) * 0.5f;

                    // Calculate vector length. (|n|)
                    // Add small factor to prevent divide by zeros.
                    length = (float)Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy + dw_dz * dw_dz) + 0.000001f;

                    // N = ( n/|n| )
                    dw_dx /= length;
                    dw_dy /= length;
                    dw_dz /= length;

                    // Will update the global variable i, j, k ans set them as w
                    this.curl(x, y, z);

                    // N x w
                    Fvc_x[x][y][z] = (dw_dy * this.k - dw_dz * this.j) * epsilon * h; // a2b3-a3b2
                    Fvc_y[x][y][z] = -(dw_dx * this.k - dw_dz * this.i) * epsilon * h; // a3b1-a1b3
                    Fvc_z[x][y][z] = (dw_dx * this.j - dw_dy * this.i) * epsilon * h; // a1b2-a2b1
                }
            }
        }
    }

    /**
     * The basic velocity solving routine as described by Stam.
     **/
    public void velocitySolver()
    {
        // add velocity that was input by mouse
        this.addSource(this.u, this.uOld);
        this.addSource(this.v, this.vOld);
        this.addSource(this.w, this.wOld);

        // add in vorticity confinement force
        this.vorticityConfinement(this.uOld, this.vOld, this.wOld);
        this.addSource(this.u, this.uOld);
        this.addSource(this.v, this.vOld);
        this.addSource(this.w, this.wOld);

        // add in buoyancy force
        this.buoyancy(this.vOld);
        this.addSource(this.v, this.vOld);

        // swapping arrays for economical mem use
        // and calculating diffusion in velocity.
        this.swapU();
        this.diffuse(1, this.u, this.uOld, this.visc);

        this.swapV();
        this.diffuse(2, this.v, this.vOld, this.visc);

        this.swapW();
        this.diffuse(3, this.w, this.wOld, this.visc);

        // we create an incompressible field
        // for more effective advection.
        this.project(this.u, this.v, this.w, this.temp, this.vOld);

        this.swapU();
        this.swapV();
        this.swapW();

        // self advect velocities
        this.advect(1, this.u, this.uOld, this.uOld, this.vOld, this.wOld);
        this.advect(2, this.v, this.vOld, this.uOld, this.vOld, this.wOld);
        this.advect(3, this.w, this.wOld, this.uOld, this.vOld, this.wOld);

        // make an incompressible field
        this.project(this.u, this.v, this.w, this.temp, this.vOld);

        // clear all input velocities for next frame
        for (int x = 0; x < this.bSize; x++)
        {
            for (int y = 0; y < this.bSize; y++)
            {
                for (int z = 0; z < this.bSize; z++)
                {
                    this.uOld[x][y][z] = 0;
                    this.vOld[x][y][z] = 0;
                    this.wOld[x][y][z] = 0;
                }
            }
        }
    }

    /**
     * The basic density solving routine.
     **/
    public void densitySolver()
    {
        // add density inputted by mouse
        this.addSource(this.d, this.dOld);
        this.swapD();

        this.diffuse(0, this.d, this.dOld, this.diff);
        this.swapD();

        this.advect(0, this.d, this.dOld, this.u, this.v, this.w);

        // clear input density array for next frame
        for (int x = 0; x < this.bSize; x++)
        {
            for (int y = 0; y < this.bSize; y++)
            {
                for (int z = 0; z < this.bSize; z++)
                {
                    this.dOld[x][y][z] = 0;
                }
            }
        }
    }

    private void addSource(final float[][][] x, final float[][][] x0)
    {
        for (int i = 0; i < this.bSize; i++)
        {
            for (int j = 0; j < this.bSize; j++)
            {
                for (int k = 0; k < this.bSize; k++)
                {
                    x[i][j][k] += this.dt * x0[i][j][k];
                }
            }
        }
    }

    /**
     * Calculate the input array after advection. We start with an input array
     * from the previous timestep and an and output array. For all grid cells we
     * need to calculate for the next timestep, we trace the cell's center
     * position backwards through the velocity field. Then we interpolate from
     * the grid of the previous timestep and assign this value to the current
     * grid cell.
     *
     * @param b
     *            Flag specifying how to handle boundries.
     * @param d
     *            Array to store the advected field.
     * @param d0
     *            The array to advect.
     * @param du
     *            The x component of the velocity field.
     * @param dv
     *            The y component of the velocity field.
     **/
    private void advect(final int b, final float[][][] d, final float[][][] d0, final float[][][] du,
            final float[][][] dv, final float[][][] dw)
    {
        int i0, j0, k0, i1, j1, k1;
        float x, y, z, r0, s0, t0, r1, s1, t1, dt0;

        dt0 = this.dt * this.size;

        for (int i = 1; i <= this.size; i++)
        {
            for (int j = 1; j <= this.size; j++)
            {
                for (int k = 1; k <= this.size; k++)
                {
                    // go backwards through velocity field
                    x = i - dt0 * du[i][j][k];
                    y = j - dt0 * dv[i][j][k];
                    z = k - dt0 * dw[i][j][k];

                    // interpolate results
                    if (x > this.size + 0.5)
                    {
                        x = this.size + 0.5f;
                    }
                    if (x < 0.5)
                    {
                        x = 0.5f;
                    }
                    i0 = (int)x;
                    i1 = i0 + 1;

                    if (y > this.size + 0.5)
                    {
                        y = this.size + 0.5f;
                    }
                    if (y < 0.5)
                    {
                        y = 0.5f;
                    }
                    j0 = (int)y;
                    j1 = j0 + 1;

                    if (z > this.size + 0.5)
                    {
                        z = this.size + 0.5f;
                    }
                    if (z < 0.5)
                    {
                        z = 0.5f;
                    }
                    k0 = (int)z;
                    k1 = k0 + 1;

                    r1 = x - i0;
                    r0 = 1 - r1;
                    s1 = y - j0;
                    s0 = 1 - s1;
                    t1 = z - k0;
                    t0 = 1 - t1;

                    d[i][j][k] = r0
                            * (s0 * (t0 * d0[i0][j0][k0] + t1 * d0[i0][j0][k1])
                                    + s1 * (t0 * d0[i0][j1][k0] + t1 * d0[i0][j1][k1]))
                            + r1 * (s0 * (t0 * d0[i1][j0][k0] + t1 * d0[i1][j0][k1])
                                    + s1 * (t0 * d0[i1][j1][k0] + t1 * d0[i1][j1][k1]));
                }
            }
        }
        this.setBoundry(b, d);
    }

    /**
     * Recalculate the input array with diffusion effects. Here we consider a
     * stable method of diffusion by finding the densities, which when diffused
     * backward in time yield the same densities we started with. This is
     * achieved through use of a linear solver to solve the sparse matrix built
     * from this linear system.
     *
     * @param b
     *            Flag to specify how boundries should be handled.
     * @param c
     *            The array to store the results of the diffusion computation.
     * @param c0
     *            The input array on which we should compute diffusion.
     * @param diff
     *            The factor of diffusion.
     **/
    private void diffuse(final int b, final float[][][] c, final float[][][] c0, final float diff)
    {
        final float a = this.dt * diff * this.size * this.size * this.size;
        this.linearSolver(b, c, c0, a, 1 + 6 * a);
    }

    /**
     * Use project() to make the velocity a mass conserving, incompressible
     * field. Achieved through a Hodge decomposition. First we calculate the
     * divergence field of our velocity using the mean finite differnce
     * approach, and apply the linear solver to compute the Poisson equation and
     * obtain a "height" field. Now we subtract the gradient of this field to
     * obtain our mass conserving velocity field.
     *
     * @param x
     *            The array in which the x component of our final velocity field
     *            is stored.
     * @param y
     *            The array in which the y component of our final velocity field
     *            is stored.
     * @param z
     *            The array in which the z component of our final velocity field
     *            is stored.
     * @param p
     *            A temporary array we can use in the computation.
     * @param div
     *            Another temporary array we use to hold the velocity divergence
     *            field.
     *
     **/
    void project(final float[][][] x, final float[][][] y, final float[][][] z, final float[][][] p,
            final float[][][] div)
    {
        for (int i = 1; i <= this.size; i++)
        {
            for (int j = 1; j <= this.size; j++)
            {
                for (int k = 1; k <= this.size; k++)
                {
                    div[i][j][k] = (x[i + 1][j][k] - x[i - 1][j][k] + y[i][j + 1][k]
                            - y[i][j - 1][k] + z[i][j][k + 1] - z[i][j][k - 1]) / -3.0f / this.size;
                    p[i][j][k] = 0;
                }
            }
        }

        this.setBoundry(0, div);
        this.setBoundry(0, p);

        this.linearSolver(0, p, div, 1, 6);

        for (int i = 1; i <= this.size; i++)
        {
            for (int j = 1; j <= this.size; j++)
            {
                for (int k = 1; k <= this.size; k++)
                {
                    x[i][j][k] -= 0.5f * this.size * (p[i + 1][j][k] - p[i - 1][j][k]);
                    y[i][j][k] -= 0.5f * this.size * (p[i][j + 1][k] - p[i][j - 1][k]);
                    z[i][j][k] -= 0.5f * this.size * (p[i][j][k + 1] - p[i][j][k - 1]);
                }
            }
        }

        this.setBoundry(1, x);
        this.setBoundry(2, y);
        this.setBoundry(3, z);
    }

    /**
     * Iterative linear system solver using the Gauss-sidel relaxation
     * technique. Room for much improvement here...
     *
     **/
    void linearSolver(final int b, final float[][][] x, final float[][][] x0, final float a, final float c)
    {
        for (int n = 0; n < 20; n++)
        {
            for (int i = 1; i <= this.size; i++)
            {
                for (int j = 1; j <= this.size; j++)
                {
                    for (int k = 1; k <= this.size; k++)
                    {
                        x[i][j][k] = (a * (x[i - 1][j][k] + x[i + 1][j][k] + x[i][j - 1][k] + x[i][j + 1][k]
                                + x[i][j][k - 1] + x[i][j][k + 1]) + x0[i][j][k]) / c;
                    }
                }
            }
            this.setBoundry(b, x);
        }
    }

    // specifies simple boundry conditions.
    private void setBoundry(final int b, final float[][][] x)
    {
        // check the border
        for (int i = 1; i <= this.size; i++)
        {
            for (int k = 1; k <= this.size; k++)
            {
                x[0][i][k] = b == 1 ? -x[1][i][k] : x[1][i][k];
                x[this.size + 1][i][k] = b == 1 ? -x[this.size][i][k] : x[this.size][i][k];
                x[i][0][k] = b == 2 ? -x[i][1][k] : x[i][1][k];
                x[i][this.size + 1][k] = b == 2 ? -x[i][this.size][k] : x[i][this.size][k];
                x[i][k][0] = b == 3 ? -x[i][k][1] : x[i][k][1];
                x[i][k][this.size + 1] = b == 3 ? -x[i][k][this.size] : x[i][k][this.size];
            }
        }

        // check each corner
        x[0][0][0] = (x[1][0][0] + x[0][1][0] + x[0][0][1]) / 3.0f;
        x[0][0][this.size + 1] = (x[1][0][this.size + 1] + x[0][1][this.size + 1] + x[0][0][this.size]) / 3.0f;
        x[0][this.size + 1][0] = (x[1][this.size + 1][0] + x[0][this.size][0] + x[0][this.size + 1][1]) / 3.0f;
        x[0][this.size + 1][this.size + 1] = (x[1][this.size + 1][this.size + 1]
                + x[0][this.size][this.size + 1] + x[0][this.size + 1][this.size]) / 3.0f;
        x[this.size + 1][0][0] = (x[this.size][0][0] + x[this.size + 1][1][0] + x[this.size + 1][0][1]) / 3.0f;
        x[this.size + 1][0][this.size + 1] = (x[this.size][0][this.size + 1] + x[this.size + 1][1][this.size + 1]
                + x[this.size + 1][0][this.size]) / 3.0f;
        x[this.size + 1][this.size + 1][0] = (x[this.size][this.size + 1][0] + x[this.size + 1][this.size][0]
                + x[this.size + 1][this.size + 1][1]) / 3.0f;
        x[this.size + 1][this.size + 1][this.size + 1] = (x[this.size][this.size + 1][this.size + 1]
                + x[this.size + 1][this.size][this.size + 1] + x[this.size + 1][this.size + 1][this.size]) / 3.0f;
    }

    // util array swapping methods
    public void swapU()
    {
        this.tmp = this.u;
        this.u = this.uOld;
        this.uOld = this.tmp;
    }

    public void swapV()
    {
        this.tmp = this.v;
        this.v = this.vOld;
        this.vOld = this.tmp;
    }

    public void swapW()
    {
        this.tmp = this.w;
        this.w = this.wOld;
        this.wOld = this.tmp;
    }

    public void swapD()
    {
        this.tmp = this.d;
        this.d = this.dOld;
        this.dOld = this.tmp;
    }
}
