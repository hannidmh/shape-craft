package vuecontroleur;

import modele.item.ItemShape;
import modele.item.SubShape;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image imgBackground;
    private Image imgFront;
    private ItemShape shape;
    private boolean hasGisement = false; // true si la case a un gisement

    public void setShape(ItemShape _shape) {
        shape = _shape;
    }

    public void setGisement(boolean _hasGisement) {
        hasGisement = _hasGisement;
    }

    public void setBackground(Image _imgBackground) {
        imgBackground = _imgBackground;
    }

    public void setFront(Image _imgFront) {
        imgFront = _imgFront;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int bordure = 1;
        final int xBack = bordure;
        final int yBack = bordure;
        final int widthBack = getWidth() - bordure * 2;
        final int heigthBack = getHeight() - bordure * 2;

        final int subPartWidth = widthBack / 4;
        final int subPartHeigth = heigthBack / 4;

        final int xFront = bordure + subPartWidth;
        final int yFront = bordure + subPartHeigth;
        final int widthFront = subPartWidth * 2;
        final int heigthFront = subPartHeigth * 2;

        // cadre
        g.drawRoundRect(bordure, bordure, widthBack, heigthBack, bordure, bordure);

        if (imgBackground != null) {
            g.drawImage(imgBackground, xBack, yBack, widthBack, heigthBack, this);
        }

        if (imgFront != null) {
            g.drawImage(imgFront, xFront, yFront, widthFront, heigthFront, this);
        }

        // Dessin du gisement : cercle gris en fond de case
        if (hasGisement) {
            g.setColor(new Color(180, 180, 180, 160));
            g.fillOval(xBack + widthBack / 4, yBack + heigthBack / 4, widthBack / 2, heigthBack / 2);
        }

        if (shape != null) {
            ItemShape.Layer[] layers = { ItemShape.Layer.one, ItemShape.Layer.two, ItemShape.Layer.three };
            for (int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
                SubShape[] tabS = shape.getSubShapes(layers[layerIndex]);
                modele.item.Color[] tabC = shape.getColors(layers[layerIndex]);

                int layerInset = layerIndex * Math.max(2, widthFront / 10);
                int layerX = xFront + layerInset;
                int layerY = yFront + layerInset;
                int layerWidth = widthFront - layerInset * 2;
                int layerHeight = heigthFront - layerInset * 2;

                if (layerWidth <= 0 || layerHeight <= 0) {
                    continue;
                }

                for (int i = 0; i < 4; i++) {
                    SubShape ss = tabS[i];

                    if (ss != SubShape.None) {
                        g.setColor(toAwtColor(tabC[i]));

                        switch (ss) {
                            case Carre:
                                g.fillRect(layerX + (layerWidth / 2) * ((i >> 1) ^ 1),
                                        layerY + (layerHeight / 2) * ((i & 1) ^ ((i >> 1) & 1)), layerWidth / 2,
                                        layerHeight / 2);
                                break;
                            case Circle:
                                g.fillOval(layerX + (layerWidth / 2) * ((i >> 1) ^ 1),
                                        layerY + (layerHeight / 2) * ((i & 1) ^ ((i >> 1) & 1)), layerWidth / 2,
                                        layerHeight / 2);
                                break;
                            case Fan:
                                g.fillArc(layerX + (layerWidth / 2) * ((i >> 1) ^ 1),
                                        layerY + (layerHeight / 2) * ((i & 1) ^ ((i >> 1) & 1)), layerWidth / 2,
                                        layerHeight / 2, getFanStartAngle(i), 90);
                                break;
                            case Star:
                                drawStar(g, layerX + (layerWidth / 2) * ((i >> 1) ^ 1),
                                        layerY + (layerHeight / 2) * ((i & 1) ^ ((i >> 1) & 1)), layerWidth / 2,
                                        layerHeight / 2);
                                break;
                            case None:
                                break;
                        }
                    }
                }
            }
        }
    }

    private Color toAwtColor(modele.item.Color color) {
        if (color == null) {
            return Color.GRAY;
        }

        return switch (color) {
            case Red -> Color.RED;
            case White -> Color.WHITE;
            case Green -> Color.GREEN;
            case Blue -> Color.BLUE;
            case Yellow -> Color.YELLOW;
            case Purple -> new Color(128, 0, 128);
            case Cyan -> Color.CYAN;
        };
    }

    private int getFanStartAngle(int index) {
        return switch (index) {
            case 0 -> 180;
            case 1 -> 90;
            case 2 -> 0;
            case 3 -> 270;
            default -> 0;
        };
    }

    private void drawStar(Graphics g, int x, int y, int width, int height) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int outerRadius = Math.max(2, Math.min(width, height) / 2);
        int innerRadius = Math.max(1, outerRadius / 2);

        int[] xs = new int[10];
        int[] ys = new int[10];
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xs[i] = centerX + (int) Math.round(Math.cos(angle) * radius);
            ys[i] = centerY + (int) Math.round(Math.sin(angle) * radius);
        }
        g.fillPolygon(xs, ys, 10);
    }

}
