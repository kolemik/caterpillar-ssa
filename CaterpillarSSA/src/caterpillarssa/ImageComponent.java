package caterpillarssa;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Васькин Александр
 */
public class ImageComponent extends JComponent {

    private Image image;

    public ImageComponent() {
        try {
            URL imgURL = getClass().getResource("/image/mai.png");
            image = ImageIO.read(imgURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            return;
        }
        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);
        //отображение логотипа в левом вехнем углу
        g.drawImage(image, 0, 0, null);

        g.setFont(new Font("Helvetica", Font.PLAIN, 14));

        g.drawString("Метод \"Гусеница\"", 360, 50);
        g.drawString("(Singular Spectrum Analysis)", 330, 70);
        g.drawString("Автор: Васькин Александр, гр. 03 - 423", 260, 90);
        g.drawString("Контакты:", 260, 110);
        g.drawString("  электропочта - aleksandr.vaskin@gmail.com", 260, 130);
        g.drawString("  аська - 376560286", 260, 150);
        g.drawString("copyright (c) 2012", 360, 170);
    }
}
