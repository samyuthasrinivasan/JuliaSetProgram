import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JuliaSetProgram extends JPanel implements AdjustmentListener {
    JFrame frame;
    Color color;
    JScrollBar aBar, bBar, zoomBar, satBar, brightBar, hueBar, sizeBar, shapeBar, iterationBar;
    JPanel scrollPanel, labelPanel, bigPanel;
    JLabel ALabel, BLabel, zoomLabel, satLabel, brightLabel, hueLabel, sizeLabel, shapeLabel, iterationLabel;
    JButton reset, save;
    BufferedImage juliaImage;
    JScrollPane juliPane;
    double A, B;
    double zoom, saturation, brightness, hue, circleSize, shape, maxIterations;
    JFileChooser fileChooser;

    public JuliaSetProgram() {
        frame = new JFrame("Julia Set Program");
        frame.setSize(1000, 1000);

        aBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -2000, 2000);
        aBar.addAdjustmentListener(this);

        bBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -2000, 2000);
        bBar.addAdjustmentListener(this);

        zoomBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 1000);
        zoomBar.addAdjustmentListener(this);

        satBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 1000);
        satBar.addAdjustmentListener(this);

        brightBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 1000);
        brightBar.addAdjustmentListener(this);

        hueBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 1000);
        hueBar.addAdjustmentListener(this);

        sizeBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 2000);
        sizeBar.addAdjustmentListener(this);

        shapeBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 2000);
        shapeBar.addAdjustmentListener(this);

        iterationBar = new JScrollBar(JScrollBar.HORIZONTAL, 50, 0, 50, 300);
        iterationBar.addAdjustmentListener(this);

        reset = new JButton("RESET");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aBar.setValue(0);
                bBar.setValue(0);
                zoomBar.setValue(0);
                satBar.setValue(0);
                brightBar.setValue(0);
                hueBar.setValue(0);
                sizeBar.setValue(1000);
                shapeBar.setValue(1000);
                iterationBar.setValue(50);
            }
        });

        String currDir = System.getProperty("user.dir");
        fileChooser = new JFileChooser(currDir);

        save = new JButton("SAVE");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        scrollPanel = new JPanel();
        scrollPanel.setLayout(new GridLayout(11, 1));
        scrollPanel.add(aBar);
        scrollPanel.add(bBar);
        scrollPanel.add(zoomBar);
        scrollPanel.add(satBar);
        scrollPanel.add(brightBar);
        scrollPanel.add(hueBar);
        scrollPanel.add(sizeBar);
        scrollPanel.add(shapeBar);
        scrollPanel.add(iterationBar);
        scrollPanel.add(reset);
        scrollPanel.add(save);
        frame.add(scrollPanel, BorderLayout.SOUTH);

        A = aBar.getValue() / 1000.0;
        B = bBar.getValue() / 1000.0;
        zoom = zoomBar.getValue() / 1000.0;
        saturation = satBar.getValue() / 1000.0;
        brightness = brightBar.getValue() / 1000.0;
        hue = hueBar.getValue() / 1000.0;
        circleSize = sizeBar.getValue() / 1000.0;
        shape = shapeBar.getValue() / 1000.0;
        maxIterations = iterationBar.getValue();

        ALabel = new JLabel("A: " + A);
        ALabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        BLabel = new JLabel("B: " + B);
        BLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        zoomLabel = new JLabel("Zoom: " + zoom);
        zoomLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        satLabel = new JLabel("Saturation: " + saturation);
        satLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        brightLabel = new JLabel("Brightness: " + brightness);
        brightLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        hueLabel = new JLabel("Hue: " + hue);
        hueLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        sizeLabel = new JLabel("Circle Size: " + circleSize);
        sizeLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        shapeLabel = new JLabel("Shape Constant: " + shape);
        shapeLabel.setFont(new Font("Quicksand", Font.BOLD, 20));
        iterationLabel = new JLabel("Max Iterations: " + maxIterations);
        iterationLabel.setFont(new Font("Quicksand", Font.BOLD, 20));

        labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(11, 1));
        labelPanel.setPreferredSize(new Dimension(350, 0));
        labelPanel.add(ALabel);
        labelPanel.add(BLabel);
        labelPanel.add(zoomLabel);
        labelPanel.add(satLabel);
        labelPanel.add(brightLabel);
        labelPanel.add(hueLabel);
        labelPanel.add(sizeLabel);
        labelPanel.add(shapeLabel);
        labelPanel.add(iterationLabel);

        bigPanel = new JPanel();
        bigPanel.setLayout(new BorderLayout());
        bigPanel.add(labelPanel, BorderLayout.WEST);
        bigPanel.add(scrollPanel, BorderLayout.CENTER);

        frame.add(this);
        frame.add(bigPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(drawJulia(), 0, 0, null);
        /*
         * g.setColor(color);
         * g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
         */
    }

    public BufferedImage drawJulia() {
        int w = frame.getWidth();
        int h = frame.getHeight();
        juliaImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        double zx = 0f, zy = 0f;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                zx = 1.5 * ((j - (w / 2.0)) / (0.5 * zoom * w));
                zy = (i - (h / 2.0)) / (0.5 * zoom * h);

                float value = (float) maxIterations;

                while (zx * zx + zy * zy < circleSize && value > 0) {
                    double diff = zx * zx - zy * zy + A;
                    zy = (shape * zx * zy) + B;
                    zx = diff;
                    value--;
                }

                int c;
                if (value > 0) {
                    c = Color.HSBtoRGB((float) hue * value / (float) maxIterations, (float) saturation,
                            (float) brightness);
                } else {
                    c = Color.HSBtoRGB((float) hue * value / (float) maxIterations, (float) saturation,
                            (float) brightness);
                }

                juliaImage.setRGB(j, i, c);
            }
        }

        System.out.println(zx + zy);

        return juliaImage;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource() == aBar) {
            A = aBar.getValue() / 1000.0;
            ALabel.setText("A: " + A);
        }

        if (e.getSource() == bBar) {
            B = bBar.getValue() / 1000.0;
            BLabel.setText("B: " + B);
        }

        if (e.getSource() == zoomBar) {
            zoom = zoomBar.getValue() / 1000.0;
            zoomLabel.setText("Zoom: " + zoom);
        }

        if (e.getSource() == satBar) {
            saturation = satBar.getValue() / 1000.0;
            satLabel.setText("Saturation: " + saturation);
        }

        if (e.getSource() == brightBar) {
            brightness = brightBar.getValue() / 1000.0;
            brightLabel.setText("Brightness: " + brightness);
        }

        if (e.getSource() == hueBar) {
            hue = hueBar.getValue() / 1000.0;
            hueLabel.setText("Hue: " + hue);
        }

        if (e.getSource() == sizeBar) {
            circleSize = sizeBar.getValue() / 1000.0;
            sizeLabel.setText("Circle Size: " + circleSize);
        }

        if (e.getSource() == shapeBar) {
            shape = shapeBar.getValue() / 1000.0;
            shapeLabel.setText("Shape Constant: " + shape);
        }

        if (e.getSource() == iterationBar) {
            maxIterations = iterationBar.getValue();
            iterationLabel.setText("Max Iterations: " + maxIterations);
        }

        repaint();
    }

    public void saveImage() {
        if (juliaImage != null) {
            FileFilter filter = new FileNameExtensionFilter("*.png", "png");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    String st = file.getAbsolutePath();
                    if (st.indexOf(".png") >= 0)
                        st = st.substring(0, st.length() - 4);
                    ImageIO.write(juliaImage, "png", new File(st + ".png"));
                } catch (IOException e) {

                }

            }
        }
    }

    public static void main(String[] args) {
        JuliaSetProgram app = new JuliaSetProgram();
    }
}