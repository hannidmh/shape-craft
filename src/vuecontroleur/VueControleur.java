package vuecontroleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;


import modele.item.Item;
import modele.item.ItemColor;
import modele.item.ItemShape;
import modele.jeu.Jeu;
import modele.plateau.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 82; // nombre de pixel par case
    // icones affichées dans la grille
    private Image icoRouge;
    private Image icoTapisDroite;
    private Image icoPoubelle;
    private Image icoMine;

    private JComponent grilleIP;

    private boolean mousePressed = false; // permet de mémoriser l'état de la souris

    private ImagePanel[][] tabIP; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône background et front, suivant ce qui est présent dans le modèle)


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
        icoTapisDroite = new ImageIcon("./data/sprites/buildings/belt_top.png").getImage();

        icoPoubelle = new ImageIcon("./data/sprites/buildings/trash.png").getImage();
        icoMine = new ImageIcon("./data/sprites/buildings/miner.png").getImage();

    }



    private void placerLesComposantsGraphiques() {
        setTitle("ShapeCraft");
        setResizable(true);
        setSize(sizeX * pxCase, sizeX * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        grilleIP = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille


        tabIP = new ImagePanel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                ImagePanel iP = new ImagePanel();

                tabIP[x][y] = iP; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

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
        add(grilleIP);
    }

    
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabIP)
     */
    private void mettreAJourAffichage() {


        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                tabIP[x][y].setBackground((Image) null);

                tabIP[x][y].setFront(null);

                Case c = plateau.getCases()[x][y];

                Machine m = c.getMachine();

                if (m != null) {

                    if (m instanceof Tapis) {
                        tabIP[x][y].setBackground(icoTapisDroite);
                    } else if (m instanceof Poubelle) {
                        tabIP[x][y].setBackground(icoPoubelle);
                    } else if (m instanceof Mine) {
                        tabIP[x][y].setBackground(icoMine);
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
}
