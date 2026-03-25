package modele.plateau;

import modele.item.ItemShape;

import java.util.Random;

public class Mine extends Machine {


    @Override
    public void work() { // TODO : modifier, suivant le gisement
        if (new Random().nextInt(4) == 0) {
            current.add(new ItemShape("CrCb--Cb"));
        }

    }

    @Override
    public void send() {
        super.send();
    }
}
