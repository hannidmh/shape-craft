package modele.plateau;

import modele.item.Item;

public class Tunnel extends Machine {
    private boolean entryImage = true;

    public boolean usesEntryImage() {
        return entryImage;
    }

    public void setUsesEntryImage(boolean entryImage) {
        this.entryImage = entryImage;
    }

    @Override
    public boolean receive(Item item, Case targetCase, Case sourceCase) {
        if (c == null || targetCase != c || sourceCase == null) {
            return false;
        }

        Case expectedSourceCase = c.plateau.getCase(c, d.opposite());
        if (expectedSourceCase != sourceCase) {
            return false;
        }

        return super.receive(item, targetCase, sourceCase);
    }

    @Override
    public void send() {
        if (c == null || current.isEmpty()) {
            return;
        }

        Tunnel exitTunnel = findLinkedTunnelAhead();
        if (exitTunnel == null) {
            return;
        }

        Case outputCase = c.plateau.getCase(exitTunnel.getCase(), d);
        if (outputCase == null || outputCase.getMachine() == null) {
            return;
        }

        Item item = current.getFirst();
        if (outputCase.getMachine().receive(item, outputCase, exitTunnel.getCase())) {
            current.removeFirst();
        }
    }

    private Tunnel findLinkedTunnelAhead() {
        if (c == null) {
            return null;
        }

        Case scannedCase = c;
        while (true) {
            scannedCase = c.plateau.getCase(scannedCase, d);
            if (scannedCase == null) {
                return null;
            }

            Machine machine = scannedCase.getMachine();
            if (machine instanceof Tunnel tunnel && tunnel != this && isCompatibleWith(tunnel)) {
                return tunnel;
            }
        }
    }

    private boolean isCompatibleWith(Tunnel tunnel) {
        Direction candidateDirection = tunnel.getDirection();
        return candidateDirection == d || candidateDirection == d.opposite();
    }
}
