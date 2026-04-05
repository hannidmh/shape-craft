package modele.plateau;

import modele.item.Item;

import java.util.LinkedList;

public abstract class Machine implements Runnable {
    LinkedList<Item> current;
    LinkedList<Item> incoming;

    Case c;
    Direction d = Direction.North ; // par défaut, pour commencer, tout est orienté au north

    public Machine()
    {
        current = new LinkedList<Item>();
        incoming = new LinkedList<Item>();
    }

    public Machine(Item _item) {
        this();
        current.add(_item);
    }

    public Direction getDirection() {
        return d;
    }

    public void setDirection(Direction d) {
        this.d = d;
    }

    public void rotateClockwise() {
        d = d.nextClockwise();
    }

    public void setCase(Case _c) {
        if (c == null) {
            c = _c;
        }
    }

    /**
     * Indique si la case donnée est la case principale de la machine (utile pour
     * les bâtiments multi-cases). Par défaut, la première case associée est la
     * principale.
     */
    public boolean isPrimaryCase(Case currentCase) {
        return c == currentCase;
    }

    public Case getCase() {
        return c;
    }

    public Item getCurrent() {
        if (current.size() > 0) {
            return current.get(0);
        } else {
            return null;
        }
    }

    /**
     * Permet aux machines multi-cases d'afficher un contenu different selon la
     * case sur laquelle on les dessine.
     */
    public Item getDisplayedItem(Case currentCase) {
        return getCurrent();
    }

    /**
     * Cases occupees par la machine relativement a sa case principale.
     */
    public Point[] getFootprint(Direction direction) {
        return new Point[] { new Point(0, 0) };
    }

    /**
     * Vide tout le contenu transporte par la machine lorsqu'elle est supprimee.
     */
    public void clearItems() {
        current.clear();
        incoming.clear();
    }

    /**
     * Point d'entree des items vers une machine. Les machines simples acceptent
     * tout, les machines multi-entrees peuvent filtrer selon la case cible.
     */
    public boolean receive(Item item, Case targetCase, Case sourceCase) {
        incoming.add(item);
        return true;
    }

    public void send() // la machine dépose un item sur sa ou ses sorties
    {
        Case up = c.plateau.getCase(c, d);
        if (up != null) {
            Machine m = up.getMachine();
            if (m != null && !current.isEmpty()) {
                Item item = current.getFirst();
                if (m.receive(item, up, c)) {
                    current.remove(item);
                }
            }
        }
    }

    public void endTick() {
        if (!incoming.isEmpty()) {
            current.addAll(incoming);
            incoming.clear();
        }
    }

    public void work() {

    }; // action de la machine, aucune par défaut

    @Override
    public void run() {
        work();
        send();


    }



}
