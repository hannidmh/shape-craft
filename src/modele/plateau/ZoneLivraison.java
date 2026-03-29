package modele.plateau;

import modele.item.ItemShape;

public class ZoneLivraison extends Machine {

    private int score = 0;

    public int getScore() {
        return score;
    }

    @Override
    public void work() {
        if (!current.isEmpty()) {
            current.remove(0); // on absorbe l'item reçu
            score = score + 1;
            System.out.println("Item livré ! Score : " + score);
        }
    }

    @Override
    public void send() {
        // La zone de livraison ne redirige rien
    }

}
