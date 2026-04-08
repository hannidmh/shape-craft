package modele.plateau;

import modele.item.Color;
import modele.item.ItemShape;

public class ZoneLivraison extends Machine {
    public static final int HUB_SIZE = 4;

    private static final class LevelDef {
        final ItemShape template;
        final int required;

        LevelDef(ItemShape template, int required) {
            this.template = template;
            this.required = Math.max(1, required);
        }

        ItemShape newTarget() {
            return template == null ? null : template.copy();
        }
    }

    private final LevelDef[] levels = new LevelDef[] {

            // Niveau 1 : demi-cercle gris (hérité du projet initial)
            new LevelDef(new ItemShape(ItemShape.ShapeType.CIRCLE, Color.Red, ItemShape.Part.FULL), 10),
            // Niveau 2 : carré plein rouge
            new LevelDef(new ItemShape(ItemShape.ShapeType.SQUARE, Color.Red, ItemShape.Part.FULL), 10),
            // Niveau 3 : étoile pleine blanche
            new LevelDef(new ItemShape(ItemShape.ShapeType.STAR, Color.White, ItemShape.Part.FULL), 10),
            // Niveau 4 : étoile jaune superposée sur carré rouge
            new LevelDef(createStackedShape(
                    new ItemShape(ItemShape.ShapeType.SQUARE, Color.Red, ItemShape.Part.TOP_LEFT),
                    new ItemShape(ItemShape.ShapeType.STAR, Color.Yellow, ItemShape.Part.FULL)), 10)
    };

    private static int defaultStartLevelIndex = 0; // 0 -> niveau 1

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
            advanceLevelIfNeeded();
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
        this.levelNumber = 1;
        this.score = 0;
    }

    private boolean isMatching(ItemShape received) {
        if (received == null || targetShape == null) {
            return false;
        }
        return received.getType() == targetShape.getType()
                && received.getColor() == targetShape.getColor()
                && received.getPart() == targetShape.getPart();
    }

    private void advanceLevelIfNeeded() {
        if (score < requiredDeliveries) {
            return;
        }

        if (levelNumber >= levels.length) {
            // Dernier niveau atteint : on borne le score et on reste sur la cible finale
            score = requiredDeliveries;
            return;
        }

        levelNumber += 1;
        LevelDef def = levels[levelNumber - 1];
        targetShape = def.newTarget();
        requiredDeliveries = def.required;
        score = 0;
    }

    public ZoneLivraison() {
        int startIndex = Math.max(0, Math.min(defaultStartLevelIndex, levels.length - 1));
        LevelDef def = levels[startIndex];
        levelNumber = startIndex + 1;
        targetShape = def.newTarget();
        requiredDeliveries = def.required;
    }



    private static ItemShape createStackedShape(ItemShape bottom, ItemShape top) {
        if (bottom == null) {
            return top == null ? null : top.copy();
        }
        ItemShape base = bottom.copy();
        if (top != null) {
            base.stack(top);
        }
        return base;
    }
}
