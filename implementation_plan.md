# Implémentation de l'Étape 1

Ce plan détaille comment nous allons implémenter l'étape 1 du sujet PDF : "Générer un environnement avec des gisements (formes), et une zone de livraison paramétrée avec une forme attendue."

## Proposed Changes

### Modèle (Logique de jeu)

#### [MODIFY] src/modele/plateau/Case.java
- Ajouter les getters/setters pour l'attribut `gisement` (qui est déjà présent mais non utilisé).
- Exemple : `public Item getGisement()`, `public void setGisement(Item gisement)`.

#### [MODIFY] src/modele/plateau/Plateau.java
- Dans la méthode [initPlateauVide()](file:///c:/Users/etulyon1/Desktop/ShapeCraft/src/modele/plateau/Plateau.java#39-50), après avoir créé les cases, on va ajouter une génération aléatoire de gisements (par exemple, 5 ou 6 zones de quelques cases contenant un `ItemShape` basique comme un Rond ou un Carré).

#### [NEW] src/modele/plateau/ZoneLivraison.java
- Créer une nouvelle classe `ZoneLivraison` héritant de [Machine](file:///c:/Users/etulyon1/Desktop/ShapeCraft/src/modele/plateau/Case.java#19-23).
- Elle aura comme propriétés une `ItemShape` attendue et un compteur de score `int quantiteLivree`.
- Lors de l'exécution (si on passe un item dessus), elle vérifiera s'il correspond à la forme attendue et incrémentera le score.

#### [MODIFY] src/modele/jeu/Jeu.java
- Au lieu de mettre une `Poubelle`, on placera une `ZoneLivraison` définie au centre ou au bord du plateau.

### Vue & Contrôleur (Affichage)

#### [MODIFY] src/vuecontroleur/VueControleur.java
- Ajouter les ressources graphiques (icônes) ou utiliser de la peinture simple (des cercles gris/carré gris) pour représenter les gisements quand une case n'a pas de machine mais possède un gisement.
- Dessiner la `ZoneLivraison` avec un texte indiquant le nombre de formes livrées et la forme attendue.

## Verification Plan
1. Lancer l'application ([Main.java](file:///c:/Users/etulyon1/Desktop/ShapeCraft/src/Main.java)).
2. S'assurer visuellement que des gisements (carrés/ronds gris sous le terrain) sont bien apparus.
3. S'assurer que le bloc "ZoneLivraison" est bien présent sur la grille avec un objectif.
