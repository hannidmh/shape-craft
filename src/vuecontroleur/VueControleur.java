package vuecontroleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

/**
 * Cette classe a deux fonctions :
 * (1) Vue : proposer une représentation graphique de l'application (cases
 * graphiques, etc.)
 * (2) Controleur : écouter les évènements clavier et déclencher le traitement
 * adapté sur le modèle
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle
                             // pour le rafraichissement, permet de communiquer les actions clavier (ou
                             // souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 82; // nombre de pixel par case
    // icones affichées dans la grille
    private Image icoRouge;
    private Image icoTapisHaut;
    private Image icoTapisDroite;
    private Image icoTapisBas;
    private Image icoTapisGauche;
    private Image icoAngleDroite;
    private Image icoAngleGauche;
    private Image icoPoubelle;
    private Image icoMine;
    private Image icoLivraison;
    private Image icoRotateur;
    private Image icoCoupeurLeft;
    private Image icoCoupeurRight;
    private Image icoEmpileur;
    private Image icoPeintre;

    private JComponent grilleIP;
    private JLabel modeLabel;

    private boolean mousePressed = false; // permet de mémoriser l'état de la souris

    private ImagePanel[][] tabIP; // cases graphique (au moment du rafraichissement, chaque case va être associée
                                  // à une icône background et front, suivant ce qui est présent dans le modèle)

    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addObserver(this);

        mettreAJourAffichage();

    }

    private void chargerLesIcones() {

        icoRouge = new ImageIcon("./data/sprites/colors/blue.png").getImage();
        icoTapisHaut = new ImageIcon("./data/sprites/buildings/belt_top.png").getImage();
        icoTapisDroite = rotateImage(icoTapisHaut, 90);
        icoTapisGauche = rotateImage(icoTapisHaut, 270);
        icoTapisBas = rotateImage(icoTapisHaut, 180);
        icoAngleDroite = new ImageIcon("./data/sprites/buildings/belt_right.png").getImage();
        icoAngleGauche = new ImageIcon("./data/sprites/buildings/belt_left.png").getImage();
        icoPoubelle = new ImageIcon("./data/sprites/buildings/trash.png").getImage();
        icoMine = new ImageIcon("./data/sprites/buildings/miner.png").getImage();
        icoLivraison = new ImageIcon("./data/sprites/buildings/hub.png").getImage();
        icoRotateur = new ImageIcon("./data/sprites/buildings/rotater.png").getImage();
        BufferedImage cutterQuad = loadImage("./data/sprites/buildings/cutter-quad.png");
        icoCoupeurLeft = cutterQuad.getSubimage(0, 0, cutterQuad.getWidth() / 2, cutterQuad.getHeight());
        icoCoupeurRight = cutterQuad.getSubimage(cutterQuad.getWidth() / 2, 0,
                cutterQuad.getWidth() - cutterQuad.getWidth() / 2, cutterQuad.getHeight());
        icoEmpileur = new ImageIcon("./data/sprites/buildings/stacker.png").getImage();
        icoPeintre = new ImageIcon("./data/sprites/buildings/painter.png").getImage();

    }

    private void placerLesComposantsGraphiques() {
        setTitle("ShapeCraft");
        setResizable(true);
        setSize(sizeX * pxCase, sizeX * pxCase + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre
        setLayout(new BorderLayout());

        grilleIP = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les
                                                             // positionner sous la forme d'une grille

        tabIP = new ImagePanel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                ImagePanel iP = new ImagePanel();

                tabIP[x][y] = iP; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique
                                  // à celles-ci (voir mettreAJourAffichage() )

                final int xx = x; // permet de compiler la classe anonyme ci-dessous
                final int yy = y;
                // écouteur de clics
                iP.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        mousePressed = false;
                        jeu.press(xx, yy);
                        System.out.println(xx + "-" + yy);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (mousePressed) {
                            jeu.slide(xx, yy);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        mousePressed = true;
                        jeu.press(xx, yy);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        mousePressed = false;

                    }
                });

                grilleIP.add(iP);
            }
        }
        add(grilleIP, BorderLayout.CENTER);
        add(createToolbar(), BorderLayout.SOUTH);
    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté
     * de la vue (tabIP)
     */
    private void mettreAJourAffichage() {

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                tabIP[x][y].setBackground((Image) null);
                tabIP[x][y].setFront(null);
                tabIP[x][y].setGisement(false); // reset

                Case c = plateau.getCases()[x][y];

                // Affichage du gisement si la case en a un
                if (c.getGisement() != null) {
                    tabIP[x][y].setGisement(true);
                }

                Machine m = c.getMachine();

                if (m != null) {

                    if (m instanceof Tapis) {
                        tabIP[x][y].setBackground(getTapisImage((Tapis) m));
                    } else if (m instanceof Poubelle) {
                        tabIP[x][y].setBackground(icoPoubelle);
                    } else if (m instanceof Mine) {
                        tabIP[x][y].setBackground(icoMine);
                    } else if (m instanceof ZoneLivraison) {
                        tabIP[x][y].setBackground(icoLivraison);
                    } else if (m instanceof Rotateur) {
                        tabIP[x][y].setBackground(icoRotateur);
                    } else if (m instanceof Coupeur coupeur) {
                        tabIP[x][y].setBackground(coupeur.isPrimaryCase(c) ? icoCoupeurLeft : icoCoupeurRight);
                    } else if (m instanceof Empileur) {
                        tabIP[x][y].setBackground(icoEmpileur);
                    } else if (m instanceof Peintre) {
                        tabIP[x][y].setBackground(icoPeintre);
                    }

                    Item current = m.getCurrent();

                    if (current instanceof ItemShape) {
                        tabIP[x][y].setShape((ItemShape) current);
                    }
                    if (current instanceof ItemColor) {
                        // tabIP[x][y].setFront(); TODO : placer l'icone des couleurs approprié
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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mettreAJourAffichage();
            }
        });

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
        BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(Math.toRadians(angleDegrees), width / 2.0, height / 2.0);
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return rotated;
    }

    private JComponent createToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(createModeButton("Tapis", Jeu.BuildMode.TAPIS));
        panel.add(createModeButton("Mine", Jeu.BuildMode.MINE));
        panel.add(createModeButton("Livraison", Jeu.BuildMode.LIVRAISON));
        panel.add(createModeButton("Poubelle", Jeu.BuildMode.POUBELLE));
        panel.add(createModeButton("Rotate", Jeu.BuildMode.ROTATEUR));
        panel.add(createModeButton("Cut", Jeu.BuildMode.COUPEUR));
        panel.add(createModeButton("Stack", Jeu.BuildMode.EMPILEUR));
        panel.add(createModeButton("Paint", Jeu.BuildMode.PEINTRE));
        modeLabel = new JLabel("Mode: " + jeu.getBuildMode().name());
        panel.add(modeLabel);
        return panel;
    }

    private JButton createModeButton(String label, Jeu.BuildMode mode) {
        JButton button = new JButton(label);
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
