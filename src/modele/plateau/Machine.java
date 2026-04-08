package modele.plateau;

import modele.item.Item;

import java.util.LinkedList;

public abstract class Machine implements Runnable {
    LinkedList<Item> current;
    LinkedList<Item> incoming;

    Case c;
    Direction d = Direction.North ;

    public Machine()
    {
        current = new LinkedList<>();
        incoming = new LinkedList<>();
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

    public boolean isPrimaryCase(Case currentCase) {
        return c == currentCase;
    }

    public Case getCase() {
        return c;
    }

    public Item getCurrent() {
        if (!current.isEmpty()) {
            return current.getFirst();
        } else {
            return null;
        }
    }

    public Item getDisplayedItem(Case currentCase) {
        return getCurrent();
    }

    public Point[] getFootprint(Direction direction) {
        return new Point[] { new Point(0, 0) };
    }

    public void clearItems() {
        current.clear();
        incoming.clear();
    }

    public boolean receive(Item item, Case targetCase, Case sourceCase) {
        incoming.add(item);
        return true;
    }

    public void send()
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

    }

    @Override
    public void run() {
        work();
        send();


    }



}
