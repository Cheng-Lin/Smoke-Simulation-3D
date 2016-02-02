import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel implements ActionListener
{
    private final SmokePanel smoke;
    private final JButton btnReset, btnNext, btnAnimate;

    private boolean isAnimate = false;

    public ControlPanel(final SmokePanel smoke)
    {
        this.smoke = smoke;

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        this.btnReset = new JButton("Reset");
        this.btnReset.addActionListener(this);
        this.add(this.btnReset);

        this.btnNext = new JButton("Next Step");
        this.btnNext.addActionListener(this);
        this.add(this.btnNext);

        this.btnAnimate = new JButton("Animate ON");
        this.btnAnimate.addActionListener(this);
        this.add(this.btnAnimate);
    }

    @Override
    public void actionPerformed(final ActionEvent arg0)
    {
        if (arg0.getSource() == this.btnAnimate)
        {
            if (!this.isAnimate)
            {
                this.btnAnimate.setLabel("Animate OFF");
                this.isAnimate = true;
                this.smoke.start();
            }
            else
            {
                this.btnAnimate.setLabel("Animate ON");
                this.isAnimate = false;
                this.smoke.stop();
            }
        }
        else if (arg0.getSource() == this.btnNext)
        {
            this.smoke.nextStep();
        }
        else if (arg0.getSource() == this.btnReset)
        {
            this.smoke.reset();
        }
    }
}
