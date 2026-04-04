# Plan de fin de projet ShapeCraft

Ce document te guide **pas à pas jusqu’à la fin du projet**.  
Objectif : passer d’une base partielle à une version propre, démontrable et soutenable.

---

## 1) Résultat final attendu (vision)

À la fin, tu dois pouvoir :

1. Lancer l’application.
2. Placer des machines sur la grille.
3. Voir des items se déplacer correctement selon l’orientation.
4. Appliquer des transformations (rotation, coupe, empilage, coloration).
5. Livrer des items dans la zone de livraison avec score mis à jour.
6. Montrer une démo stable (sans bug bloquant) + code lisible.

---

## 2) Ordre de travail recommandé (important)

Travaille dans cet ordre pour éviter les blocages :

1. **Modèle métier (ItemShape, ItemColor)**
2. **Machines & flux d’items (Machine, Tapis, Mine, directions)**
3. **Rendu graphique (ImagePanel, VueControleur)**
4. **Gameplay & ergonomie (interactions, score, polish)**
5. **Tests, stabilisation, démonstration**

> Règle d’or : ne commence pas les finitions visuelles tant que les règles métier ne sont pas correctes.

---

## 3) Étape A — Finaliser la logique des formes (priorité absolue)

### A.1 Compléter les layers dans `ItemShape`

Dans `ItemShape`, seules les données du layer `one` sont réellement exploitées.  
Tu dois prendre en charge `two` et `three` dans :

- `getSubShapes(Layer l)`
- `getColors(Layer l)`

#### Action concrète
- Organise les tableaux `tabSubShapes` et `tabColors` par blocs de 4 cases par layer :
    - index 0..3 = layer one
    - index 4..7 = layer two
    - index 8..11 = layer three
- Retourne la bonne tranche selon le layer demandé.

### A.2 Rendre le constructeur cohérent avec 3 layers

Le commentaire décrit un encodage sur 3 couches, mais le code parse seulement la première couche.  
Tu dois parser l’ensemble de la chaîne.

#### Action concrète
- Valider la longueur : multiple de 2, et cohérence avec 1 à 3 layers.
- Boucler sur `str.length()/2` et mapper chaque paire (forme, couleur).
- Gérer proprement les erreurs de codage (caractère inconnu).

### A.3 Implémenter les transformations manquantes

Méthodes à finir :

- `stack(ItemShape shapeSup)`
- `Cut()`
- `Color(Color c)`

#### Proposition de comportement (simple et défendable)
- **Cut** : sépare la forme en 2 moitiés (gauche/droite ou haut/bas, choisis une convention fixe).
- **stack** : empile les couches de `shapeSup` au-dessus de `this` tant qu’il reste de la place (max 3 layers).
- **Color** : recolore toutes les sous-formes présentes (sauf `None`).

### A.4 Vérification minimale de A

Crée des tests unitaires (ou une classe de test temporaire) pour vérifier :

- rotation d’un layer
- coupe d’une forme
- empilage à 2 puis 3 couches
- recoloration

---

## 4) Étape B — Corriger le flux des machines

### B.1 Orientation réelle dans `Machine.send()`

Actuellement l’envoi est forcé vers le Nord.  
Tu dois utiliser la direction courante `d` de la machine.

#### Action concrète
- Remplacer la cible `Direction.North` par `d`.
- Ajouter des accesseurs (`setDirection`, `getDirection`) pour piloter l’orientation depuis l’UI.

### B.2 Implémenter `Tapis`

`Tapis` ne fait rien pour l’instant.

#### Action concrète
- Hériter du comportement de transfert de `Machine`.
- Selon ton design, `work()` peut être vide et `send()` suffit.
- Vérifier qu’un tapis ne duplique pas les items.

### B.3 Rendre `Mine` dépendante du gisement de sa case

La mine produit actuellement un item codé en dur.

#### Action concrète
- Lire `c.getGisement()` dans `Mine.work()`.
- Cloner/instancier l’item du gisement pour le mettre dans `current`.
- Garder (ou enlever) l’aléatoire, mais justifier ton choix.

### B.4 Vérification minimale de B

Faire un scénario simple :

`Mine -> Tapis -> ZoneLivraison`

et vérifier que :

- un item sort de la mine,
- passe sur le tapis,
- est absorbé par la zone,
- le score augmente.

---

## 5) Étape C — Compléter l’affichage

### C.1 Dessiner toutes les couleurs

Dans `ImagePanel`, seuls `Red` et `White` sont gérés.

#### Action concrète
- Ajouter les cas `Green, Blue, Yellow, Purple, Cyan`.
- Mapper vers `java.awt.Color` cohérent.

### C.2 Dessiner toutes les formes utiles

Seul `Carre` est dessiné.

#### Action concrète
- Ajouter `Circle` (ovale), `Fan`, `Star` (même approximations graphiques simples).
- Préférer un rendu lisible plutôt que parfait.

### C.3 Afficher plusieurs layers

Actuellement seul `Layer.one` est rendu.

#### Action concrète
- Dessiner couches 2 et 3 en réduisant un peu la taille/alpha à chaque couche.
- Conserver un ordre stable : layer 1 en bas, puis 2, puis 3.

### C.4 `ItemColor` côté vue

Dans `VueControleur`, le rendu `ItemColor` est TODO.

#### Action concrète
- Associer des icônes couleur (ou un rendu simple) pour visualiser les items de peinture.

---

## 6) Étape D — Interactions et ergonomie

### D.1 Contrôles utilisateurs

Objectif : rendre la démo manipulable rapidement.

#### Suggestions
- clic gauche : poser un tapis,
- clic droit : rotation de la machine sélectionnée,
- touche clavier (optionnel) : changer type de machine à poser.

### D.2 Protection contre erreurs de placement

- Empêcher d’écraser certaines machines critiques (mine/livraison) sans confirmation logique.
- Vérifier les bornes et états nuls.

### D.3 Score visible dans la fenêtre

Actuellement le score est surtout console.  
Ajoute un `JLabel` de score dans la fenêtre pour la démo.

---

## 7) Étape E — Qualité, tests, et finitions

### E.1 Nettoyage code

- Supprimer TODO résolus.
- Renommer les méthodes avec convention Java (`cut`, `color` au lieu de `Cut`, `Color`) si possible.
- Ajouter commentaires courts sur les choix métier.

### E.2 Plan de tests final (checklist)

- Lancement appli sans crash.
- Placement de machines.
- Rotation orientation correcte.
- Flux continu mine → tapis → livraison.
- Transformations shape (rotate/cut/stack/color).
- Rendu des couleurs et formes.
- Score qui monte.

### E.3 Démo finale

Prépare un scénario de 2–3 minutes :

1. Présenter la grille et les machines.
2. Montrer circulation des items.
3. Montrer au moins 2 transformations.
4. Montrer livraison + score.
5. Expliquer 2 limites restantes (honnêteté technique).

---

## 8) Planning conseillé sur 10 jours (adaptable)

### J1–J2
- Finaliser `ItemShape` (layers + parsing)
- Définir règles de `cut/stack/color`

### J3–J4
- Implémenter `cut/stack/color`
- Ajouter tests unitaires associés

### J5
- Corriger orientation `Machine.send`
- Implémenter `Tapis`

### J6
- Corriger `Mine` pour utiliser le gisement
- Valider chaîne mine→tapis→livraison

### J7–J8
- Compléter `ImagePanel` (couleurs, formes, layers)
- Compléter rendu `ItemColor`

### J9
- UI/ergonomie : rotation, score visible, petits correctifs

### J10
- Relecture complète, tests finaux, répétition démo

---

## 9) Definition of Done (projet terminé)

Tu peux considérer le projet terminé si :

- [ ] Plus de TODO critique dans les classes principales.
- [ ] Pipeline de jeu fonctionnel de bout en bout.
- [ ] Transformations shape principales implémentées.
- [ ] Affichage cohérent des formes/couleurs/layers.
- [ ] Démo reproductible en moins de 3 minutes.
- [ ] Code relu et stable.

---

## 10) Conseils pour ne pas perdre du temps

1. Fais des petits commits fréquents (un sous-bloc fonctionnel par commit).
2. Teste après chaque changement métier important.
3. Ne mélange pas grosse refacto + nouvelle feature dans le même commit.
4. Si un bug est difficile : reproduire, isoler, corriger, retester.
5. Garde un document de décisions (2 lignes par choix technique).

Bon courage — tu as déjà une base solide, le plus dur est de **structurer l’ordre d’exécution** et de **tenir la qualité jusqu’à la fin**.
