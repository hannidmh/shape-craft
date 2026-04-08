package vuecontroleur;

import modele.item.ItemColor;
import modele.item.ItemShape;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ImagePanel extends JPanel {
    private Image imgBackground;
    private Image imgFront;
    private ItemShape shape;
    private ItemShape gisementShape;
    private ItemColor colorItem;
    private ItemColor gisementColor;
    private ItemShape overlayShape;
    private String overlayText;
    private int overlayLevel = -1;
    private boolean selected;

    private int backgroundOffsetX;
    private int backgroundOffsetY;
    private int backgroundSpanX = 1;
    private int backgroundSpanY = 1;

    private int overlayShapeOffsetX;
    private int overlayShapeOffsetY;
    private int overlayShapeSpanX = 1;
    private int overlayShapeSpanY = 1;

    private int overlayTextOffsetX;
    private int overlayTextOffsetY;
    private int overlayTextSpanX = 1;
    private int overlayTextSpanY = 1;
    private final Map<modele.item.Color, Image> depositColorImages = new EnumMap<modele.item.Color, Image>(modele.item.Color.class);

    public ImagePanel() {
        loadDepositColorImages();
    }

    public void setShape(ItemShape _shape) {
        shape = _shape;
    }

    public void setGisementShape(ItemShape gisementShape) {
        this.gisementShape = gisementShape;
    }

    public void setColorItem(ItemColor colorItem) {
        this.colorItem = colorItem;
    }

    public void setGisementColor(ItemColor gisementColor) {
        this.gisementColor = gisementColor;
    }

    public void setOverlayText(String text) {
        setOverlayText(text, 0, 0, 1, 1);
    }

    public void setOverlayText(String text, int offsetX, int offsetY, int spanX, int spanY) {
        overlayText = text;
        overlayTextOffsetX = offsetX;
        overlayTextOffsetY = offsetY;
        overlayTextSpanX = Math.max(1, spanX);
        overlayTextSpanY = Math.max(1, spanY);
    }

    public void setOverlayShape(ItemShape overlayShape) {
        setOverlayShape(overlayShape, 0, 0, 1, 1);
    }

    public void setOverlayShape(ItemShape overlayShape, int offsetX, int offsetY, int spanX, int spanY) {
        this.overlayShape = overlayShape;
        overlayShapeOffsetX = offsetX;
        overlayShapeOffsetY = offsetY;
        overlayShapeSpanX = Math.max(1, spanX);
        overlayShapeSpanY = Math.max(1, spanY);
    }

    public void setOverlayLevel(int overlayLevel) {
        this.overlayLevel = overlayLevel;
    }

    public void setBackground(Image _imgBackground) {
        setBackground(_imgBackground, 0, 0, 1, 1);
    }

    public void setBackground(Image _imgBackground, int offsetX, int offsetY, int spanX, int spanY) {
        imgBackground = _imgBackground;
        backgroundOffsetX = offsetX;
        backgroundOffsetY = offsetY;
        backgroundSpanX = Math.max(1, spanX);
        backgroundSpanY = Math.max(1, spanY);
    }

    public void setFront(Image _imgFront) {
        imgFront = _imgFront;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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

        Graphics2D borderGraphics = (Graphics2D) g.create();
        borderGraphics.setColor(selected ? new Color(255, 140, 0) : Color.DARK_GRAY);
        borderGraphics.setStroke(new BasicStroke(selected ? 3f : 1f));
        borderGraphics.drawRoundRect(bordure, bordure, widthBack, heigthBack, bordure, bordure);
        borderGraphics.dispose();

        if (imgBackground != null) {
            drawSpanningImage(g, imgBackground, xBack, yBack, widthBack, heigthBack,
                    backgroundOffsetX, backgroundOffsetY, backgroundSpanX, backgroundSpanY);
        }

        if (imgFront != null) {
            g.drawImage(imgFront, xFront, yFront, widthFront, heigthFront, this);
        }

        if (gisementShape != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
            drawShape(g2d, gisementShape, xBack + widthBack / 5, yBack + heigthBack / 5,
                    widthBack * 3 / 5, heigthBack * 3 / 5);
            g2d.dispose();
        }
        if (gisementColor != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            drawColorDeposit(g2d, gisementColor, xBack + widthBack / 5, yBack + heigthBack / 5,
                    widthBack * 3 / 5, heigthBack * 3 / 5);
            g2d.dispose();
        }

        if (shape != null) {
            drawShape(g, shape, xFront, yFront, widthFront, heigthFront);
        }
        if (colorItem != null) {
            drawColorItem(g, colorItem, xFront, yFront, widthFront, heigthFront);
        }

        if (overlayShape != null && !isHubOverlayStyle()) {
            drawSpanningShape(g, overlayShape, xBack, yBack, widthBack, heigthBack,
                    overlayShapeOffsetX, overlayShapeOffsetY, overlayShapeSpanX, overlayShapeSpanY);
        }

        if (overlayText != null && !overlayText.isEmpty()) {
            drawSpanningOverlayText(g, overlayText, xBack, yBack, widthBack, heigthBack,
                    overlayTextOffsetX, overlayTextOffsetY, overlayTextSpanX, overlayTextSpanY);
        }
    }

    private void drawSpanningImage(Graphics g, Image image, int x, int y, int width, int height,
            int offsetX, int offsetY, int spanX, int spanY) {
        int drawX = x - offsetX * width;
        int drawY = y - offsetY * height;
        g.drawImage(image, drawX, drawY, width * spanX, height * spanY, this);
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
            case Gray -> Color.GRAY;
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

    private void drawFan(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int outerRadius = Math.max(6, Math.min(width, height) / 2);
        int innerRadius = Math.max(3, outerRadius / 3);
        double bladeSpread = Math.toRadians(24);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < 3; i++) {
            double angle = -Math.PI / 2 + i * (2 * Math.PI / 3);
            Path2D.Double blade = new Path2D.Double();
            blade.moveTo(centerX + Math.cos(angle) * outerRadius, centerY + Math.sin(angle) * outerRadius);
            blade.lineTo(centerX + Math.cos(angle + bladeSpread) * innerRadius,
                    centerY + Math.sin(angle + bladeSpread) * innerRadius);
            blade.lineTo(centerX + Math.cos(angle - bladeSpread) * innerRadius,
                    centerY + Math.sin(angle - bladeSpread) * innerRadius);
            blade.closePath();
            g2d.fill(blade);
        }

        int hubRadius = Math.max(4, outerRadius / 3);
        g2d.fillOval(centerX - hubRadius, centerY - hubRadius, hubRadius * 2, hubRadius * 2);
        g2d.dispose();
    }

    private void drawShape(Graphics g, ItemShape shape, int x, int y, int width, int height) {
        if (shape == null || width <= 0 || height <= 0) {
            return;
        }

        List<ItemShape> layers = new ArrayList<ItemShape>();
        collectShapeLayers(shape, layers);

        int layerOffset = layers.size() <= 1 ? 0 : Math.max(3, Math.min(width, height) / 8);
        int maxShift = layerOffset * Math.max(0, layers.size() - 1);
        int layerWidth = Math.max(4, width);
        int layerHeight = Math.max(4, height - maxShift);
        int layerX = x;
        int centeredStartY = y - (maxShift / 2);

        for (int i = 0; i < layers.size(); i++) {
            int layerY = centeredStartY + (layers.size() - 1 - i) * layerOffset;
            drawSingleShape(g, layers.get(i), layerX, layerY, layerWidth, layerHeight);
        }
    }

    private void drawSingleShape(Graphics g, ItemShape shape, int x, int y, int width, int height) {
        g.setColor(toAwtColor(shape.getColor()));

        Graphics2D g2d = (Graphics2D) g.create();
        if (shape.getPart() == ItemShape.Part.LEFT) {
            g2d.setClip(x, y, width / 2, height);
        } else if (shape.getPart() == ItemShape.Part.RIGHT) {
            g2d.setClip(x + width / 2, y, width / 2, height);
        } else if (shape.getPart() == ItemShape.Part.TOP) {
            g2d.setClip(x, y, width, height / 2);
        } else if (shape.getPart() == ItemShape.Part.BOTTOM) {
            g2d.setClip(x, y + height / 2, width, height / 2);
        } else if (shape.getPart() == ItemShape.Part.TOP_LEFT) {
            g2d.setClip(x, y, width / 2, height / 2);
        } else if (shape.getPart() == ItemShape.Part.TOP_RIGHT) {
            g2d.setClip(x + width / 2, y, width / 2, height / 2);
        } else if (shape.getPart() == ItemShape.Part.BOTTOM_LEFT) {
            g2d.setClip(x, y + height / 2, width / 2, height / 2);
        } else if (shape.getPart() == ItemShape.Part.BOTTOM_RIGHT) {
            g2d.setClip(x + width / 2, y + height / 2, width / 2, height / 2);
        }

        switch (shape.getType()) {
            case CIRCLE -> g2d.fillOval(x, y, width, height);
            case SQUARE -> g2d.fillRect(x, y, width, height);
            case STAR -> drawStar(g2d, x, y, width, height);
            case FAN -> drawFan(g2d, x, y, width, height);
        }

        g2d.setColor(new Color(55, 55, 55, 170));
        switch (shape.getType()) {
            case CIRCLE -> g2d.drawOval(x, y, width, height);
            case SQUARE -> g2d.drawRect(x, y, width, height);
            case STAR, FAN -> {
            }
        }
        g2d.dispose();
    }

    private void collectShapeLayers(ItemShape shape, List<ItemShape> layers) {
        if (shape == null) {
            return;
        }
        layers.add(shape);
        ItemShape stackedTop = shape.getStackedTop();
        if (stackedTop != null) {
            collectShapeLayers(stackedTop, layers);
        }
    }

    private void drawColorItem(Graphics g, ItemColor colorItem, int x, int y, int width, int height) {
        if (colorItem == null) {
            return;
        }
        Image colorImage = depositColorImages.get(colorItem.getColor());
        if (colorImage != null) {
            g.drawImage(colorImage, x, y, width, height, this);
            return;
        }
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(toAwtColor(colorItem.getColor()));
        g2d.fillRoundRect(x + width / 6, y + height / 6, width * 2 / 3, height * 2 / 3, 12, 12);
        g2d.setColor(new Color(40, 40, 40, 180));
        g2d.drawRoundRect(x + width / 6, y + height / 6, width * 2 / 3, height * 2 / 3, 12, 12);
        g2d.dispose();
    }

    private void drawColorDeposit(Graphics g, ItemColor colorItem, int x, int y, int width, int height) {
        if (colorItem == null) {
            return;
        }
        Image depositImage = depositColorImages.get(colorItem.getColor());
        if (depositImage != null) {
            g.drawImage(depositImage, x, y, width, height, this);
            return;
        }
        drawColorItem(g, colorItem, x, y, width, height);
    }

    private void drawSpanningShape(Graphics g, ItemShape shape, int x, int y, int width, int height,
            int offsetX, int offsetY, int spanX, int spanY) {
        int fullX = x - offsetX * width;
        int fullY = y - offsetY * height;
        int fullWidth = width * spanX;
        int fullHeight = height * spanY;

        int insetX = Math.max(8, fullWidth / 4);
        int insetY = Math.max(8, fullHeight / 4);
        drawShape(g, shape, fullX + insetX, fullY + insetY, fullWidth - insetX * 2, fullHeight - insetY * 2);
    }

    private void drawSpanningOverlayText(Graphics g, String text, int x, int y, int width, int height,
            int offsetX, int offsetY, int spanX, int spanY) {
        if (isHubOverlayStyle()) {
            drawHubOverlayCard(g, x, y, width, height, offsetX, offsetY, spanX, spanY);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        int fullX = x - offsetX * width;
        int fullY = y - offsetY * height;
        int fullWidth = width * spanX;
        int fullHeight = height * spanY;

        g2d.setFont(getFont().deriveFont(Font.BOLD, Math.max(11f, fullWidth / 18f)));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int boxWidth = textWidth + 20;
        int boxHeight = metrics.getHeight() + 10;
        int boxX = fullX + (fullWidth - boxWidth) / 2;
        int boxY = fullY + fullHeight - boxHeight - Math.max(8, fullHeight / 12);

        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);
        g2d.drawString(text, boxX + 10, boxY + metrics.getAscent() + 5);
        g2d.dispose();
    }

    private boolean isHubOverlayStyle() {
        return overlayLevel >= 0 && overlayText != null && overlayText.contains("/");
    }

    private void drawHubOverlayCard(Graphics g, int x, int y, int width, int height,
            int offsetX, int offsetY, int spanX, int spanY) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int fullX = x - offsetX * width;
        int fullY = y - offsetY * height;
        int fullWidth = width * spanX;
        int fullHeight = height * spanY;

        int cardX = fullX + Math.max(12, fullWidth / 10);
        int cardY = fullY + Math.max(10, fullHeight / 12);
        int cardWidth = fullWidth - Math.max(24, fullWidth / 5);
        int cardHeight = fullHeight - Math.max(20, fullHeight / 5);

        g2d.setColor(new Color(214, 217, 226, 236));
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, 14, 14);
        g2d.setColor(new Color(145, 148, 157, 210));
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, 14, 14);

        int badgeWidth = Math.max(26, cardWidth / 6);
        int badgeHeight = Math.max(34, cardHeight / 4);
        int badgeX = cardX + Math.max(6, cardWidth / 18);
        int badgeY = cardY + Math.max(6, cardHeight / 20);
        g2d.setColor(new Color(241, 25, 92));
        g2d.fillRoundRect(badgeX, badgeY, badgeWidth, badgeHeight, 7, 7);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont().deriveFont(Font.BOLD, Math.max(8f, cardWidth / 22f)));
        FontMetrics badgeSmall = g2d.getFontMetrics();
        g2d.drawString("LVL", badgeX + (badgeWidth - badgeSmall.stringWidth("LVL")) / 2, badgeY + badgeHeight / 3);
        g2d.setFont(getFont().deriveFont(Font.BOLD, Math.max(14f, cardWidth / 13f)));
        FontMetrics badgeBig = g2d.getFontMetrics();
        String levelText = Integer.toString(overlayLevel);
        g2d.drawString(levelText, badgeX + (badgeWidth - badgeBig.stringWidth(levelText)) / 2,
                badgeY + badgeHeight - Math.max(4, badgeHeight / 8));

        g2d.setColor(new Color(100, 104, 114));
        g2d.setFont(getFont().deriveFont(Font.BOLD, Math.max(17f, cardWidth / 9f)));
        g2d.drawString("DELIVER", cardX + cardWidth / 2 - Math.max(8, cardWidth / 5), cardY + cardHeight / 5);

        int iconSize = Math.max(36, Math.min(cardWidth, cardHeight) / 3);
        int iconX = cardX + Math.max(14, cardWidth / 10);
        int iconY = cardY + cardHeight / 3;
        if (overlayShape != null) {
            drawShape(g2d, overlayShape, iconX, iconY, iconSize, iconSize);
        }

        String[] parts = overlayText.split("/");
        String currentPart = parts.length > 0 ? parts[0] : "0";
        String requiredPart = parts.length > 1 ? parts[1] : "0";

        int scoreX = iconX + iconSize + Math.max(10, cardWidth / 14);
        int scoreY = iconY + iconSize / 2;
        g2d.setColor(new Color(90, 92, 101));
        g2d.setFont(getFont().deriveFont(Font.BOLD, Math.max(42f, cardWidth / 5f)));
        g2d.drawString(currentPart.trim(), scoreX, scoreY);

        g2d.setFont(getFont().deriveFont(Font.BOLD, Math.max(26f, cardWidth / 8f)));
        g2d.setColor(new Color(144, 148, 156));
        g2d.drawString("/" + requiredPart.trim(), scoreX - Math.max(2, cardWidth / 40), scoreY + Math.max(34, cardHeight / 5));

        g2d.dispose();
    }

    private void loadDepositColorImages() {
        depositColorImages.put(modele.item.Color.Red, new ImageIcon("./data/sprites/colors/red.png").getImage());
        depositColorImages.put(modele.item.Color.Green, new ImageIcon("./data/sprites/colors/green.png").getImage());
        depositColorImages.put(modele.item.Color.Blue, new ImageIcon("./data/sprites/colors/blue.png").getImage());
        depositColorImages.put(modele.item.Color.Yellow, new ImageIcon("./data/sprites/colors/yellow.png").getImage());
        depositColorImages.put(modele.item.Color.Purple, new ImageIcon("./data/sprites/colors/purple.png").getImage());
        depositColorImages.put(modele.item.Color.Cyan, new ImageIcon("./data/sprites/colors/cyan.png").getImage());
        depositColorImages.put(modele.item.Color.White, new ImageIcon("./data/sprites/colors/white.png").getImage());
        depositColorImages.put(modele.item.Color.Gray, new ImageIcon("./data/sprites/colors/uncolored.png").getImage());
    }
}
