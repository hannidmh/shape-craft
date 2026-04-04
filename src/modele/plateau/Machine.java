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

    public void setCase(Case _c) {
        if (c == null) {
            c = _c;
        }
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

    public void send() // la machine dépose un item sur sa ou ses sorties
    {
        Case up = c.plateau.getCase(c, d);
        if (up != null) {
            Machine m = up.getMachine();
            ;
            if (m != null && !current.isEmpty()) {
                Item item = current.getFirst();
                m.incoming.add(item);
                current.remove(item);
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
