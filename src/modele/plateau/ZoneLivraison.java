package modele.plateau;

import modele.item.Color;
import modele.item.ItemShape;

public class ZoneLivraison extends Machine {
    public static final int HUB_SIZE = 4;

    private int score = 0;
    private ItemShape targetShape;
    private int requiredDeliveries = 10;
    private int levelNumber = 1;

    public int getScore() {
        return score;
    }

    public int getRequiredDeliveries() {
        return requiredDeliveries;
    }

    public String getProgressLabel() {
        return score + "/" + requiredDeliveries + " livre";
    }

    public String getProgressFractionLabel() {
        return score + "/" + requiredDeliveries;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public ItemShape getTargetShape() {
        return targetShape == null ? null : targetShape.copy();
    }

    @Override
    public void work() {
        if (current.isEmpty()) {
            return;
        }

        if (!(current.getFirst() instanceof ItemShape received)) {
            current.removeFirst(); // on ignore les items non supportés
            return;
        }

        if (targetShape == null || isMatching(received)) {
            current.removeFirst();
            if (score < requiredDeliveries) {
                score = score + 1;
            }
            System.out.println("Item livre ! " + getProgressLabel());
        } else {
            current.removeFirst(); // item non conforme
        }
    }

    @Override
    public void send() {
        // La zone de livraison ne redirige rien
    }

    public void setTargetShape(ItemShape targetShape) {
        this.targetShape = targetShape;
    }

    public void configureHalfCircleGoal(int requiredDeliveries) {
        this.targetShape = new ItemShape(ItemShape.ShapeType.CIRCLE, Color.Gray, ItemShape.Part.LEFT);
        this.requiredDeliveries = Math.max(1, requiredDeliveries);
    }

    private boolean isMatching(ItemShape received) {
        if (received == null || targetShape == null) {
            return false;
        }
        return received.getType() == targetShape.getType()
                && received.getColor() == targetShape.getColor()
                && received.getPart() == targetShape.getPart();
    }
}
