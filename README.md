# Système de Gestion de Bibliothèque

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

### Lancer en mode développement

```bash
mvn clean compile
mvn exec:java
```

## Améliorations futures (JavaFX)

- Ajouter des écrans JavaFX dans `ui.gui`.
- Réutiliser `service` et `repository` existants sans les modifier en profondeur.
- Maintenir la même persistance JSON.
- Préparer une navigation GUI (liste des livres, utilisateurs, prêts, retours).
