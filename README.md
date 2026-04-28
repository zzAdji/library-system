# Système de Gestion de Bibliothèque (Java + JSON)

## Description

Ce projet est une application Java de gestion de bibliothèque.
La persistance des données repose exclusivement sur des **fichiers JSON**.

## Fonctionnalités

- Démarrage d'une application console
- Chargement et sauvegarde des données via fichiers JSON
- Séparation claire des responsabilités :
  - `model` : entités
  - `repository` : accès/persistance JSON
  - `service` : logique métier
  - `ui.console` : interaction utilisateur en console
  - `ui.gui` : future couche JavaFX

## Architecture

Le projet applique une architecture en couches stricte :

- **model**  
  Contient les classes entités (ex. `Book`, `User`, `Loan`).

- **repository**  
  Responsable de la lecture/écriture JSON (`books.json`, `users.json`, `loans.json`).

- **service**  
  Contient uniquement la logique métier et orchestre les repositories.

- **ui.console**  
  Gère les entrées/sorties console, appelle les services, sans logique métier interne.

- **ui.gui**  
  Réservé à la future interface JavaFX (Phase 2).

Règle de conception : **aucune logique métier dans la couche UI**.

## Stack technique

- Java 17+
- Maven 3.9+
- Jackson (JSON)
- Application console (GUI en prévision)

## Structure du projet

```text
library-system/
├─ pom.xml
├─ README.md
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/library/
│  │  │     ├─ Main.java
│  │  │     ├─ model/
│  │  │     ├─ repository/
│  │  │     ├─ service/
│  │  │     └─ ui/
│  │  │        ├─ console/
│  │  │        └─ gui/
│  │  └─ resources/
│  │     └─ data/
│  │        ├─ books.json
│  │        ├─ users.json
│  │        └─ loans.json
│  └─ test/
│     └─ java/
```

## Persistance JSON

Les données sont stockées dans trois fichiers :

- `books.json`
- `users.json`
- `loans.json`

Principe :

1. Au démarrage, la couche `repository` lit les fichiers JSON.
2. Les `service` manipulent les objets en mémoire.
3. Lors des modifications, `repository` sérialise et sauvegarde les données dans les mêmes fichiers.
4. Les fichiers restent la source de vérité persistante.

## Exécution du projet

### Prérequis

- Java 17 ou supérieur
- Maven 3.9 ou supérieur

### Lancer en mode développement

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.library.Main"
```

Ou créer un jar :

```bash
mvn clean package
java -jar target/library-system-1.0.0.jar
```

## Workflow de développement

1. Créer/mettre à jour les entités dans `model`.
2. Implémenter la persistance JSON dans `repository`.
3. Ajouter la logique métier dans `service`.
4. Connecter l’interface console dans `ui.console`.
5. Ajouter des tests unitaires sur `service` et `repository`.
6. Maintenir la séparation stricte des couches.

## Améliorations futures (JavaFX)

- Ajouter des écrans JavaFX dans `ui.gui`.
- Réutiliser `service` et `repository` existants sans les modifier en profondeur.
- Maintenir la même persistance JSON.
- Préparer une navigation GUI (liste des livres, utilisateurs, prêts, retours).
```