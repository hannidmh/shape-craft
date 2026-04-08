package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class Case {

    protected Plateau plateau;
    protected Machine machine;
    protected Item gisement;

    public void setMachine(Machine m) {
        machine = m;
        m.setCase(this);
    }

    public void clearMachine() {
        machine = null;
    }

    public Machine getMachine() {
        return machine;
    }

    public Case(Plateau _plateau) {

        plateau = _plateau;
    }

    public Item getGisement() {
        return gisement;
    }

    public void setGisement(Item gisement) {
        this.gisement = gisement;
    }
}
