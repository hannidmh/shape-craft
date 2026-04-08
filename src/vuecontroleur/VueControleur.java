package vuecontroleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import modele.item.Item;
import modele.item.ItemColor;
import modele.item.ItemShape;
import modele.jeu.Jeu;
import modele.plateau.*;

public class VueControleur extends JFrame implements Observer {
    private final Plateau plateau;
    private final Jeu jeu;
    private final int sizeX;
    private final int sizeY;
    private static final int pxCase = 82;
    private Image icoTapisHaut;
    private Image icoTapisDroite;
    private Image icoTapisBas;
    private Image icoTapisGauche;
    private Image icoAngleDroite;
    private Image icoAngleGauche;
    private Image icoTunnelEntry;
    private Image icoTunnelExit;
    private Image icoPoubelle;
    private Image icoMine;
    private Image icoLivraison;
    private Image icoRotateur;
    private Image icoCoupeur;
    private Image icoEmpileur;
    private Image icoMixeur;
    private Image icoPeintre;

    private JComponent grilleIP;
    private JLabel modeLabel;

    private boolean mousePressed = false;
    private int pressedMouseButton = MouseEvent.NOBUTTON;

    private ImagePanel[][] tabIP;

    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = Plateau.SIZE_X;
        sizeY = Plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();
        installerActionsClavier();

        plateau.addObserver(this);

        mettreAJourAffichage();

    }

    private void chargerLesIcones() {

        icoTapisHaut = new ImageIcon("./data/sprites/buildings/belt_top.png").getImage();
        icoTapisDroite = rotateImage(icoTapisHaut, 90);
        icoTapisGauche = rotateImage(icoTapisHaut, 270);
        icoTapisBas = rotateImage(icoTapisHaut, 180);
        icoAngleDroite = new ImageIcon("./data/sprites/buildings/belt_right.png").getImage();
        icoAngleGauche = new ImageIcon("./data/sprites/buildings/belt_left.png").getImage();
        icoTunnelEntry = new ImageIcon("./data/sprites/buildings/underground_belt_entry.png").getImage();
        icoTunnelExit = new ImageIcon("./data/sprites/buildings/underground_belt_exit.png").getImage();
        icoPoubelle = new ImageIcon("./data/sprites/buildings/trash.png").getImage();
        icoMine = new ImageIcon("./data/sprites/buildings/miner.png").getImage();
        icoLivraison = new ImageIcon("./data/sprites/buildings/hub.png").getImage();
        icoRotateur = new ImageIcon("./data/sprites/buildings/rotater.png").getImage();
        icoCoupeur = loadImage("./data/sprites/buildings/cutter.png");
        icoEmpileur = new ImageIcon("./data/sprites/buildings/stacker.png").getImage();
        icoMixeur = new ImageIcon("./data/sprites/buildings/mixer.png").getImage();
        icoPeintre = new ImageIcon("./data/sprites/buildings/painter.png").getImage();

    }

    private void placerLesComposantsGraphiques() {
        setTitle("ShapeCraft");
        setResizable(true);
        setSize(sizeX * pxCase, sizeX * pxCase + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        grilleIP = new JPanel(new GridLayout(sizeY, sizeX));

        tabIP = new ImagePanel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                ImagePanel iP = new ImagePanel();

                tabIP[x][y] = iP;

                final int xx = x;
                final int yy = y;
                iP.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (mousePressed && pressedMouseButton == MouseEvent.BUTTON1) {
                            jeu.slide(xx, yy);
                            mettreAJourAffichage();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        mousePressed = true;
                        pressedMouseButton = e.getButton();
                        if (SwingUtilities.isRightMouseButton(e)) {
                            jeu.erase(xx, yy);
                            mettreAJourAffichage();
                            return;
                        }
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            jeu.press(xx, yy);
                            mettreAJourAffichage();
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        mousePressed = false;
                        pressedMouseButton = MouseEvent.NOBUTTON;
                    }
                });

                grilleIP.add(iP);
            }
        }
        add(grilleIP, BorderLayout.CENTER);
        add(createHelpBar(), BorderLayout.NORTH);
        add(createToolbar(), BorderLayout.SOUTH);
    }

    private void mettreAJourAffichage() {

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                tabIP[x][y].setBackground((Image) null);
                tabIP[x][y].setFront(null);
                tabIP[x][y].setShape(null);
                tabIP[x][y].setColorItem(null);
                tabIP[x][y].setGisementShape(null);
                tabIP[x][y].setGisementColor(null);
                tabIP[x][y].setOverlayShape(null);
                tabIP[x][y].setOverlayText(null);
                tabIP[x][y].setOverlayLevel(-1);
                tabIP[x][y].setSelected(false);

                Case c = plateau.getCases()[x][y];

                if (c.getGisement() instanceof ItemShape gisementShape) {
                    tabIP[x][y].setGisementShape(gisementShape);
                } else if (c.getGisement() instanceof ItemColor gisementColor) {
                    tabIP[x][y].setGisementColor(gisementColor);
                }

                Machine m = c.getMachine();

                if (m != null) {
                    boolean shouldDisplayCurrentItem = true;
                    tabIP[x][y].setSelected(jeu.isSelectedMachine(m));

                    if (m instanceof Tapis) {
                        tabIP[x][y].setBackground(getTapisImage((Tapis) m));
                    } else if (m instanceof Tunnel tunnel) {
                        Image tunnelImage = tunnel.usesEntryImage() ? icoTunnelEntry : icoTunnelExit;
                        tabIP[x][y].setBackground(getMachineImage(tunnelImage, m.getDirection()));
                    } else if (m instanceof Poubelle) {
                        tabIP[x][y].setBackground(getMachineImage(icoPoubelle, m.getDirection()));
                    } else if (m instanceof Mine) {
                        tabIP[x][y].setBackground(getMachineImage(icoMine, m.getDirection()));
                    } else if (m instanceof ZoneLivraison zoneLivraison) {
                        modele.plateau.Point origin = plateau.getPosition(zoneLivraison.getCase());
                        int offsetX = x - origin.x;
                        int offsetY = y - origin.y;
                        tabIP[x][y].setBackground(icoLivraison, offsetX, offsetY,
                                ZoneLivraison.HUB_SIZE, ZoneLivraison.HUB_SIZE);
                        tabIP[x][y].setOverlayShape(zoneLivraison.getTargetShape(), offsetX, offsetY,
                                ZoneLivraison.HUB_SIZE, ZoneLivraison.HUB_SIZE);
                        tabIP[x][y].setOverlayText(zoneLivraison.getProgressFractionLabel(), offsetX, offsetY,
                                ZoneLivraison.HUB_SIZE, ZoneLivraison.HUB_SIZE);
                        tabIP[x][y].setOverlayLevel(zoneLivraison.getLevelNumber());
                        shouldDisplayCurrentItem = false;
                    } else if (m instanceof Rotateur) {
                        tabIP[x][y].setBackground(getMachineImage(icoRotateur, m.getDirection()));
                    } else if (m instanceof Coupeur) {
                        setSpanningMachineBackground(tabIP[x][y], m, icoCoupeur, x, y);
                    } else if (m instanceof Empileur) {
                        setSpanningMachineBackground(tabIP[x][y], m, icoEmpileur, x, y);
                    } else if (m instanceof Mixeur) {
                        setSpanningMachineBackground(tabIP[x][y], m, icoMixeur, x, y);
                    } else if (m instanceof Peintre) {
                        setSpanningMachineBackground(tabIP[x][y], m, icoPeintre, x, y);
                    }

                    Item current = shouldDisplayCurrentItem ? m.getDisplayedItem(c) : null;

                    if (current instanceof ItemShape) {
                        tabIP[x][y].setShape((ItemShape) current);
                    }
                    if (current instanceof ItemColor) {
                        tabIP[x][y].setColorItem((ItemColor) current);
                    }

                }

            }
        }
        grilleIP.repaint();
        if (modeLabel != null) {
            modeLabel.setText("Mode: " + jeu.getBuildMode().name());
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(this::mettreAJourAffichage);
    }

    private Image getTapisImage(Tapis tapis) {
        Direction incoming = tapis.getIncomingDirection();
        Direction direction = tapis.getDirection();
        if (incoming == null || direction == null || incoming == opposite(direction)) {
            return getStraightTapisImage(direction);
        }
        return getCornerTapisImage(incoming, direction);
    }

    private Image getStraightTapisImage(Direction direction) {
        if (direction == null) {
            return icoTapisHaut;
        }

        return switch (direction) {
            case North -> icoTapisHaut;
            case South -> icoTapisBas;
            case East -> icoTapisDroite;
            case West -> icoTapisGauche;
        };
    }

    private Image getCornerTapisImage(Direction incoming, Direction outgoing) {
        if (incoming == Direction.South && outgoing == Direction.East) {
            return icoAngleDroite;
        }
        if (incoming == Direction.West && outgoing == Direction.South) {
            return rotateImage(icoAngleDroite, 90);
        }
        if (incoming == Direction.North && outgoing == Direction.West) {
            return rotateImage(icoAngleDroite, 180);
        }
        if (incoming == Direction.East && outgoing == Direction.North) {
            return rotateImage(icoAngleDroite, 270);
        }
        if (incoming == Direction.South && outgoing == Direction.West) {
            return icoAngleGauche;
        }
        if (incoming == Direction.East && outgoing == Direction.South) {
            return rotateImage(icoAngleGauche,270);
        }
        if (incoming == Direction.North && outgoing == Direction.East) {
            return rotateImage(icoAngleGauche, 180);
        }
        if (incoming == Direction.West && outgoing == Direction.North) {
            return rotateImage(icoAngleGauche, 90);
        }
        return getStraightTapisImage(outgoing);
    }

    private Direction opposite(Direction direction) {
        return switch (direction) {
            case North -> Direction.South;
            case South -> Direction.North;
            case East -> Direction.West;
            case West -> Direction.East;
        };
    }

    private Image rotateImage(Image source, double angleDegrees) {
        int width = source.getWidth(null);
        int height = source.getHeight(null);
        int normalizedAngle = ((int) Math.round(angleDegrees) % 360 + 360) % 360;
        int rotatedWidth = (normalizedAngle == 90 || normalizedAngle == 270) ? height : width;
        int rotatedHeight = (normalizedAngle == 90 || normalizedAngle == 270) ? width : height;
        BufferedImage rotated = new BufferedImage(rotatedWidth, rotatedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.translate(rotatedWidth / 2.0, rotatedHeight / 2.0);
        g2d.rotate(Math.toRadians(normalizedAngle));
        g2d.drawImage(source, -width / 2, -height / 2, null);
        g2d.dispose();
        return rotated;
    }

    private void installerActionsClavier() {
        String rotateSelectionAction = "rotate-selection";
        JRootPane rootPane = getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), rotateSelectionAction);
        rootPane.getActionMap().put(rotateSelectionAction, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                jeu.rotateSelection();
                mettreAJourAffichage();
            }
        });
    }

    private Image getMachineImage(Image baseImage, Direction direction) {
        if (direction == null || direction == Direction.North) {
            return baseImage;
        }
        if (direction == Direction.East) {
            return rotateImage(baseImage, 90);
        }
        if (direction == Direction.South) {
            return rotateImage(baseImage, 180);
        }
        return rotateImage(baseImage, 270);
    }

    private void setSpanningMachineBackground(ImagePanel panel, Machine machine, Image baseImage, int cellX, int cellY) {
        modele.plateau.Point primaryPosition = plateau.getPosition(machine.getCase());
        if (primaryPosition == null) {
            return;
        }

        modele.plateau.Point[] footprint = machine.getFootprint(machine.getDirection());
        int minOffsetX = 0;
        int maxOffsetX = 0;
        int minOffsetY = 0;
        int maxOffsetY = 0;

        for (modele.plateau.Point point : footprint) {
            minOffsetX = Math.min(minOffsetX, point.x);
            maxOffsetX = Math.max(maxOffsetX, point.x);
            minOffsetY = Math.min(minOffsetY, point.y);
            maxOffsetY = Math.max(maxOffsetY, point.y);
        }

        int originX = primaryPosition.x + minOffsetX;
        int originY = primaryPosition.y + minOffsetY;
        int offsetX = cellX - originX;
        int offsetY = cellY - originY;
        int spanX = maxOffsetX - minOffsetX + 1;
        int spanY = maxOffsetY - minOffsetY + 1;

        panel.setBackground(getMachineImage(baseImage, machine.getDirection()), offsetX, offsetY, spanX, spanY);
    }

    private JComponent createToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(createModeButton(icoTapisHaut, "Tapis", Jeu.BuildMode.TAPIS));
        panel.add(createModeButton(icoTunnelEntry, "Tunnel", Jeu.BuildMode.TUNNEL));
        panel.add(createModeButton(icoMine, "Mine", Jeu.BuildMode.MINE));
        panel.add(createModeButton(icoPoubelle, "Poubelle", Jeu.BuildMode.POUBELLE));
        panel.add(createModeButton(icoRotateur, "Rotateur", Jeu.BuildMode.ROTATEUR));
        panel.add(createModeButton(icoCoupeur, "Coupeur", Jeu.BuildMode.COUPEUR));
        panel.add(createModeButton(icoEmpileur, "Empileur", Jeu.BuildMode.EMPILEUR));
        panel.add(createModeButton(icoMixeur, "Mixeur", Jeu.BuildMode.MIXEUR));
        panel.add(createModeButton(icoPeintre, "Peintre", Jeu.BuildMode.PEINTRE));
        modeLabel = new JLabel("Mode: " + jeu.getBuildMode().name());
        modeLabel.setFont(modeLabel.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(modeLabel);
        return panel;
    }

    private JComponent createHelpBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 12));
        bar.setBackground(new Color(24, 38, 58));
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        bar.add(createHelpItem("./data/sprites/icones/click-gauche.png", "L", new Color(78, 174, 92), "Placer"));
        bar.add(createHelpItem("./data/sprites/icones/click-gauche.png", "L+", new Color(108, 125, 230), "Tracer (glisser)"));
        bar.add(createHelpItem("./data/sprites/icones/clic-droit(1).png", "R", new Color(205, 75, 75), "Supprimer"));
        bar.add(createHelpItem("./data/sprites/icones/R.png", "R", new Color(230, 165, 72), "Pivoter"));

        return bar;
    }

    private JComponent createHelpItem(String iconPath, String badgeText, Color badgeColor, String action) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);

        JLabel iconLabel = createHelpIconLabel(iconPath);

        JLabel badge = new JLabel(badgeText, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(badgeColor);
        badge.setForeground(Color.WHITE);
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 16f));
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JLabel actionLabel = new JLabel(action);
        actionLabel.setForeground(new Color(235, 238, 244));
        actionLabel.setFont(actionLabel.getFont().deriveFont(Font.PLAIN, 16f));

        item.add(iconLabel);
        item.add(badge);
        item.add(actionLabel);
        return item;
    }

    private JLabel createHelpIconLabel(String iconPath) {
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        try {
            Image icon = ImageIO.read(new File(iconPath));
            if (icon != null) {
                iconLabel.setIcon(new ImageIcon(icon.getScaledInstance(36, 36, Image.SCALE_SMOOTH)));
                return iconLabel;
            }
        } catch (IOException ignored) {
        }

        iconLabel.setText("?");
        iconLabel.setForeground(new Color(200, 206, 217));
        iconLabel.setFont(iconLabel.getFont().deriveFont(Font.BOLD, 18f));
        return iconLabel;
    }

    private JButton createModeButton(Image iconImage, String tooltip, Jeu.BuildMode mode) {
        Image scaled = iconImage.getScaledInstance(88, 88, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaled));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(120, 100));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            jeu.setBuildMode(mode);
            if (modeLabel != null) {
                modeLabel.setText("Mode: " + jeu.getBuildMode().name());
            }
        });
        return button;
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger l'image " + path, e);
        }
    }
}
