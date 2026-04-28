# Documentation Technique - Systeme de Gestion de Bibliotheque

**Projet :** Library Management System  
**Langage :** Java  
**Persistance :** Fichiers JSON  
**Version :** 1.0 - Phase 1 (Console)  
**Date :** Avril 2026

---

## 1. Presentation du Projet

### 1.1 Description

Le Systeme de Gestion de Bibliotheque est une application Java modulaire permettant de gerer l'ensemble des operations d'une bibliotheque : catalogue de livres, comptes utilisateurs, emprunts et statistiques. L'application est concue selon une architecture en couches strictes, garantissant la maintenabilite et l'evolutivite du systeme.

### 1.2 Objectifs

- Fournir un outil complet de gestion de bibliotheque utilisable en ligne de commande.
- Appliquer une architecture logicielle propre et extensible.
- Assurer une separation nette entre la logique metier, la persistance et l'interface utilisateur.
- Preparer la migration vers une interface graphique (JavaFX) sans refactorisation du coeur applicatif.

### 1.3 Approche de Developpement par Phases

| Phase | Description | Interface | Persistance |
|-------|-------------|-----------|-------------|
| Phase 1 | Application console fonctionnelle | Console (CLI) | Fichiers JSON |
| Phase 2 | Migration vers interface graphique | JavaFX (GUI) | Fichiers JSON (inchange) |

La Phase 2 ne modifie ni la couche service ni la couche repository. Seule la couche `ui` est remplacee.

---

## 2. Architecture du Systeme

### 2.1 Principe Architectural

Le systeme repose sur une architecture en couches (Layered Architecture). Chaque couche a une responsabilite unique et ne communique qu'avec la couche adjacente. Cette contrainte est non negociable et garantit l'independance de chaque composant.

```
+--------------------------------------------------+
|               ui.console / ui.gui                |  <-- Interface utilisateur
|         (affichage, saisie, navigation)          |
+--------------------------------------------------+
                        |
                        v
+--------------------------------------------------+
|                    service                       |  <-- Logique metier
|    (regles, validations, orchestration)          |
+--------------------------------------------------+
                        |
                        v
+--------------------------------------------------+
|                  repository                      |  <-- Acces aux donnees
|         (lecture/ecriture JSON, CRUD)            |
+--------------------------------------------------+
                        |
                        v
+--------------------------------------------------+
|                    model                         |  <-- Entites metier
|          (Book, User, Loan, etc.)                |
+--------------------------------------------------+
                        |
                        v
+--------------------------------------------------+
|              Fichiers JSON (data/)               |  <-- Persistance
+--------------------------------------------------+
```

### 2.2 Role de Chaque Couche

**Couche model**
- Contient les entites metier (classes POJO).
- Aucune logique, aucune dependance vers d'autres couches.
- Exemples : `Book`, `User`, `Loan`, `Role`.

**Couche repository**
- Responsable de la lecture et de l'ecriture des donnees dans les fichiers JSON.
- Expose des interfaces CRUD (Create, Read, Update, Delete).
- Ne contient aucune regle metier.
- Encapsule toute dependance a la librairie Jackson.

**Couche service**
- Contient toute la logique metier : validations, regles d'emprunt, controles d'acces.
- Utilise les repositories via injection ou instanciation directe.
- Ne communique jamais directement avec l'interface utilisateur.

**Couche ui**
- Affiche les menus, recoit les entrees utilisateur et appelle les services.
- Ne contient aucune logique metier.
- En Phase 1 : sous-package `ui.console`. En Phase 2 : sous-package `ui.gui`.

### 2.3 Flux de Donnees

```
Utilisateur
    --> ui.console (saisie et affichage)
        --> service (validation et traitement)
            --> repository (lecture/ecriture JSON)
                --> data/books.json, users.json, loans.json
```

### 2.4 Justification pour la Migration GUI

Parce que la couche `ui` est completement isolee de la logique metier, remplacer `ui.console` par `ui.gui` (JavaFX) ne necessite aucune modification des couches `service` et `repository`. C'est l'avantage fondamental de cette architecture.

---

## 3. Phases de Developpement

### Phase 1 : Application Console

**Fonctionnalites implementees :**
- Authentification par identifiant et mot de passe.
- Gestion complete du catalogue de livres (ajout, modification, suppression, recherche).
- Gestion des comptes utilisateurs (creation, modification, desactivation).
- Systeme d'emprunts et de retours avec gestion des dates.
- Tableau de bord avec statistiques de base.

**Strategie de persistance JSON :**
- Un fichier JSON par entite : `books.json`, `users.json`, `loans.json`.
- La serialisation et deserialisation sont gerees via la librairie Jackson.
- Les fichiers sont lus integralement en memoire au demarrage et reecrits apres chaque modification.
- Le dossier `data/` contient tous les fichiers de persistance.

**Logique metier couverte :**
- Verification des doublons (ISBN unique, login unique).
- Verification de disponibilite avant emprunt.
- Calcul et verification des dates de retour.
- Gestion des roles (ADMIN, LIBRARIAN, MEMBER).

### Phase 2 : Migration JavaFX

| Element | Statut |
|---------|--------|
| Couche `model` | Inchange |
| Couche `repository` | Inchange |
| Couche `service` | Inchange |
| Couche `ui.console` | Remplacee par `ui.gui` |
| Fichiers JSON | Inchanges |

La migration consiste uniquement a creer le package `ui.gui` avec les vues JavaFX (FXML), les controleurs et la configuration de l'application graphique. Aucune regle metier n'est deplacee ou modifiee.

---

## 4. Modules Fonctionnels

### 4.1 Authentification

Permet aux utilisateurs de se connecter avec un identifiant et un mot de passe. Les mots de passe sont stockes sous forme hachee. Le systeme attribue un role a chaque utilisateur (ADMIN, LIBRARIAN, MEMBER) et controle l'acces aux fonctionnalites selon ce role.

### 4.2 Gestion des Livres

Permet la gestion complete du catalogue : ajout, modification, suppression et recherche de livres. Chaque livre est identifie par un ISBN unique. La recherche peut porter sur le titre, l'auteur ou la categorie. La disponibilite d'un exemplaire est mise a jour automatiquement lors des emprunts et retours.

### 4.3 Gestion des Utilisateurs

Permet la creation et la gestion des comptes membres et bibliothecaires. Un administrateur peut creer, modifier ou desactiver des comptes. Chaque utilisateur possede un identifiant unique, un role, et un historique d'emprunts.

### 4.4 Systeme d'Emprunts

Gere le cycle de vie d'un emprunt : creation, prolongation et cloture par retour. Le systeme verifie la disponibilite du livre avant tout emprunt, applique une duree d'emprunt definie, et detecte les retards. Un utilisateur ne peut pas emprunter un livre deja en sa possession.

### 4.5 Statistiques

Fournit des indicateurs de suivi : nombre de livres disponibles, livres les plus empruntes, utilisateurs les plus actifs, taux d'occupation du catalogue, et liste des emprunts en retard. Ces donnees sont calculees a la volee a partir des fichiers JSON.

---

## 5. Organisation de l'Equipe

### 5.1 Structure

| Membre | Role | Charge |
|--------|------|--------|
| Developpeur 1 | Lead Backend - Architecture & Services | Elevee |
| Developpeur 2 | Lead Data - Repository & Persistance | Elevee |
| Developpeur 3 | Authentification & UI Utilisateurs | Standard |
| Developpeur 4 | Module Livres | Standard |
| Developpeur 5 | Module Emprunts | Standard |
| Developpeur 6 | Statistiques & Support Tests | Standard |

### 5.2 Responsabilites Detaillees

Cette section definit precisement "qui cree quoi".  
Convention : chaque membre travaille sur sa branche `feature/*`, puis ouvre une PR vers `develop`.

#### Developpeur 1 - Lead Backend Architecture (branche `feature/service-layer`)

**Objectif :** definir les contrats de service et garantir les regles d'architecture.

**Fichiers a creer/modifier :**
- `src/main/java/com/library/service/AuthService.java`
- `src/main/java/com/library/service/BookService.java`
- `src/main/java/com/library/service/UserService.java`
- `src/main/java/com/library/service/LoanService.java`
- `src/main/java/com/library/service/StatisticsService.java`
- `src/main/java/com/library/config/AppConfig.java` (si necessaire pour le wiring)

**Methodes minimales attendues (signatures cibles) :**
- `AuthService` : `login(String login, String password)`, `logout()`, `getCurrentUser()`
- `BookService` : `addBook(Book book)`, `updateBook(Book book)`, `deleteBook(String isbn)`, `findByIsbn(String isbn)`, `search(String keyword)`, `getAllBooks()`
- `UserService` : `createUser(User user)`, `updateUser(User user)`, `deactivateUser(String userId)`, `findById(String userId)`, `getAllUsers()`
- `LoanService` : `borrowBook(String userId, String isbn)`, `returnBook(String loanId)`, `extendLoan(String loanId, int extraDays)`, `getLoansByUser(String userId)`, `getActiveLoans()`
- `StatisticsService` : `countAvailableBooks()`, `countBorrowedBooks()`, `topBorrowedBooks(int limit)`, `overdueLoans()`

**Livrables de validation :**
- Interfaces/services compilent sans erreurs.
- Aucune classe `ui.*` n'est importee dans `service`.
- Contrats valides avec Dev 2 avant implementation complete.

#### Developpeur 2 - Lead Data & Persistance (branche `feature/repository`)

**Objectif :** fournir la couche `model` et la persistance JSON via `repository`.

**Fichiers a creer/modifier :**
- `src/main/java/com/library/model/Book.java`
- `src/main/java/com/library/model/User.java`
- `src/main/java/com/library/model/Loan.java`
- `src/main/java/com/library/model/Role.java`
- `src/main/java/com/library/repository/BookRepository.java`
- `src/main/java/com/library/repository/UserRepository.java`
- `src/main/java/com/library/repository/LoanRepository.java`
- `src/main/java/com/library/repository/impl/JsonBookRepository.java`
- `src/main/java/com/library/repository/impl/JsonUserRepository.java`
- `src/main/java/com/library/repository/impl/JsonLoanRepository.java`
- `src/main/java/com/library/config/JsonConfig.java`
- `data/books.json`, `data/users.json`, `data/loans.json` (schema stable)

**Methodes minimales attendues (signatures cibles) :**
- `BookRepository` : `findAll()`, `findByIsbn(String isbn)`, `save(Book book)`, `update(Book book)`, `deleteByIsbn(String isbn)`
- `UserRepository` : `findAll()`, `findById(String id)`, `findByLogin(String login)`, `save(User user)`, `update(User user)`, `deleteById(String id)`
- `LoanRepository` : `findAll()`, `findById(String id)`, `findActiveByUser(String userId)`, `save(Loan loan)`, `update(Loan loan)`, `deleteById(String id)`
- Repositories JSON : `loadData()`, `writeData(List<T> data)` (privates internes)

**Livrables de validation :**
- Lecture/ecriture JSON stable (fichiers non corrompus).
- Gestion propre des erreurs I/O.
- Tests repository verts avant merge.

#### Developpeur 3 - Authentification & Gestion Utilisateurs (branche `feature/auth`)

**Objectif :** construire le flux console d'authentification et le menu utilisateur.

**Fichiers a creer/modifier :**
- `src/main/java/com/library/ui/console/AuthConsole.java`
- `src/main/java/com/library/ui/console/UserConsole.java`
- `src/main/java/com/library/util/PasswordUtils.java`
- `src/main/java/com/library/util/ConsoleUtils.java` (si necessaire)

**Methodes minimales attendues (signatures cibles) :**
- `AuthConsole` : `showLoginScreen()`, `handleLogin()`, `handleLogout()`
- `UserConsole` : `showUserMenu()`, `handleCreateUser()`, `handleUpdateUser()`, `handleDeactivateUser()`, `listUsers()`
- `PasswordUtils` : `hash(String rawPassword)`, `verify(String rawPassword, String hash)`

**Livrables de validation :**
- Aucun acces direct a `repository` depuis `ui.console`.
- Toutes les actions passent par `AuthService`/`UserService`.
- Cas d'erreur console traites (entree vide, utilisateur introuvable, etc.).

#### Developpeur 4 - Module Livres (branche `feature/books`)

**Objectif :** construire le flux console du catalogue de livres.

**Fichiers a creer/modifier :**
- `src/main/java/com/library/ui/console/BookConsole.java`
- `src/main/java/com/library/model/Book.java` (ajustements mineurs via PR coordonnee avec Dev 2)

**Methodes minimales attendues (signatures cibles) :**
- `BookConsole` : `showBookMenu()`, `handleAddBook()`, `handleUpdateBook()`, `handleDeleteBook()`, `handleSearchBooks()`, `listBooks()`

**Livrables de validation :**
- Appels uniquement vers `BookService`.
- Affichage lisible du catalogue en console.
- Gestion des erreurs de saisie ISBN/titre/auteur.

#### Developpeur 5 - Module Emprunts (branche `feature/loans`)

**Objectif :** construire le flux console complet emprunt/retour.

**Fichiers a creer/modifier :**
- `src/main/java/com/library/ui/console/LoanConsole.java`
- `src/main/java/com/library/util/DateUtils.java`

**Methodes minimales attendues (signatures cibles) :**
- `LoanConsole` : `showLoanMenu()`, `handleBorrowBook()`, `handleReturnBook()`, `handleExtendLoan()`, `listActiveLoans()`, `listUserLoans()`
- `DateUtils` : `today()`, `addDays(LocalDate date, int days)`, `isOverdue(LocalDate dueDate)`

**Livrables de validation :**
- Appels uniquement vers `LoanService`.
- Flux nominal valide : emprunt -> prolongation -> retour.
- Messages clairs pour indisponibilite, retard, emprunt introuvable.

#### Developpeur 6 - Statistiques & Support Tests (branche `feature/statistics`)

**Objectif :** construire l'affichage console des indicateurs et industrialiser les tests.

**Fichiers a creer/modifier :**
- `src/main/java/com/library/ui/console/StatisticsConsole.java`
- `src/test/java/com/library/service/` (tests des services)
- `src/test/java/com/library/repository/` (tests des repositories JSON)
- `src/test/resources/data/` (jeux JSON de test dedies)
- `docs/tests_plan.md` (plan de tests manuel + scenarios)

**Methodes minimales attendues (signatures cibles) :**
- `StatisticsConsole` : `showStatisticsMenu()`, `showGlobalStats()`, `showTopBooks()`, `showOverdueLoans()`
- Tests : `should...()` par cas fonctionnel (nommage explicite)

**Livrables de validation :**
- Tableau de bord lisible en console.
- Couverture de tests des flux critiques.
- Rapport de test partage avant release de phase.

#### Regle de coordination inter-membres (obligatoire)

- Dev 1 publie d'abord les contrats `service` (stubs) pour debloquer Dev 3/4/5/6.
- Dev 2 publie ensuite les contrats `repository` + modeles.
- Dev 3/4/5/6 implementent leurs `Console` en parallele sur ces contrats.
- Toute methode ajoutee/modifiee dans un contrat doit etre annoncee sur le canal equipe avant merge.
- Une PR qui viole la regle "pas de logique metier dans UI" est refusee.

### 5.3 Regles de Collaboration

- Aucun membre ne pousse directement sur la branche `main`.
- Toute fonctionnalite est developpee sur une branche dediee.
- Toute pull request doit etre reviewee par au moins un lead developpeur avant fusion.
- Les conflits de fusion sont resolus conjointement par les leads.
- Les reunions de synchronisation ont lieu en debut et fin de sprint.

### 5.4 Workflow Git

```
main
 └── develop
      ├── feature/auth           (Dev 3)
      ├── feature/books          (Dev 4)
      ├── feature/loans          (Dev 5)
      ├── feature/statistics     (Dev 6)
      ├── feature/service-layer  (Dev 1)
      └── feature/repository     (Dev 2)
```

- `main` : version stable et deployable uniquement.
- `develop` : branche d'integration commune.
- `feature/*` : branche individuelle par module.
- Les merges vers `develop` se font uniquement par pull request approuvee.
- Les merges vers `main` sont effectues par les leads en fin de phase.

---

## 6. Strategie de Distribution des Taches

### 6.1 Decoupage du Travail

Le travail est decoupage selon les couches architecturales et les modules fonctionnels. Les leads ont la charge des couches transversales (service, repository, model) tandis que les developpeurs standard prennent en charge les modules fonctionnels verticalement (du service a l'UI).

### 6.2 Ordre de Dependance

L'ordre ci-dessous est obligatoire pour garantir la coherence technique et le management d'equipe.

#### Etape 0 - Bootstrap commun (Jour 1, demi-jour)

**Responsable :** Dev 1  
**Participants :** tous

**A creer/valider avant de commencer :**
- Arborescence des packages
- `pom.xml` valide
- Fichiers JSON initiaux (`books.json`, `users.json`, `loans.json`)
- Regles de nommage de classes/methodes

**Sortie attendue :**
- Projet compilable par tous sur la branche `develop`.

#### Etape 1 - Contrats de service (Jour 1)

**Responsable :** Dev 1

**Creation prioritaire :**
- `AuthService.java`
- `BookService.java`
- `UserService.java`
- `LoanService.java`
- `StatisticsService.java`

**Contenu attendu :**
- Signatures de methodes uniquement (ou stubs minimaux)
- JavaDoc courte par methode pour clarifier les comportements attendus

**Debloque :**
- Dev 3/4/5/6 peuvent coder les interfaces console en parallele.

#### Etape 2 - Modeles + contrats repository (Jour 1 -> Jour 2)

**Responsable :** Dev 2

**Creation prioritaire :**
- `model/Book.java`, `model/User.java`, `model/Loan.java`, `model/Role.java`
- `repository/BookRepository.java`, `UserRepository.java`, `LoanRepository.java`

**Contenu attendu :**
- Attributs modeles stabilises
- Contrats CRUD valides avec Dev 1

**Debloque :**
- Dev 1 peut implementer la logique service sans hypothese floue sur les donnees.

#### Etape 3 - Implementations JSON repository (Jour 2)

**Responsable :** Dev 2

**Creation prioritaire :**
- `repository/impl/JsonBookRepository.java`
- `repository/impl/JsonUserRepository.java`
- `repository/impl/JsonLoanRepository.java`
- `config/JsonConfig.java`

**Contenu attendu :**
- Lecture/ecriture JSON fiable
- Gestion des erreurs I/O
- Tests unitaires repository de base

**Debloque :**
- Dev 1 peut brancher les services sur une persistance reelle.

#### Etape 4 - Implementation service (Jour 2 -> Jour 3)

**Responsable :** Dev 1

**Creation prioritaire :**
- Logique des classes `service/*` deja contractees en Etape 1

**Contenu attendu :**
- Validations metier
- Orchestration repository
- Aucun code UI dans `service`

**Debloque :**
- Dev 3/4/5/6 peuvent finaliser les ecrans console avec comportement complet.

#### Etape 5 - UI Console par module (Jour 3 -> Jour 4)

**Responsables :** Dev 3, Dev 4, Dev 5, Dev 6 (en parallele)

**Repartition :**
- Dev 3 : `AuthConsole.java`, `UserConsole.java`, `PasswordUtils.java`
- Dev 4 : `BookConsole.java`
- Dev 5 : `LoanConsole.java`, `DateUtils.java`
- Dev 6 : `StatisticsConsole.java`

**Contenu attendu :**
- Menus, saisies, affichage
- Appels exclusifs aux services
- Gestion des erreurs de saisie

**Debloque :**
- Integration fonctionnelle complete pour les tests croises.

#### Etape 6 - Tests croises et integration (Jour 4 -> Jour 5)

**Responsables :** Dev 6 + Dev 1 + Dev 2

**Creation prioritaire :**
- Tests dans `src/test/java/com/library/service/`
- Tests dans `src/test/java/com/library/repository/`
- Jeux de donnees `src/test/resources/data/`
- Rapport `docs/tests_plan.md`

**Sortie attendue :**
- Flux critiques verifies : login, CRUD livres, CRUD utilisateurs, emprunt/retour, statistiques.
- Stabilisation finale sur `develop`.

#### Regles de passage entre etapes (gates)

- Une etape n'est pas consideree terminee sans PR mergee sur `develop`.
- Toute PR de contrat (`service` ou `repository`) doit etre revue par Dev 1 et Dev 2.
- Les Dev 3/4/5/6 ne finalisent pas leurs PR tant que les services reels ne sont pas merges.
- Tout changement de signature apres Etape 2 impose :
  - annonce immediate a toute l'equipe
  - mise a jour des consommateurs impactes dans la meme fenetre d'integration.

### 6.3 Strategie d'Integration

- Sequence d'integration recommandee : `feature/service-layer` -> `feature/repository` -> `feature/auth|books|loans|statistics` -> tests.
- Maximum 1 merge majeur a la fois vers `develop` pour limiter les conflits.
- Chaque merge vers `develop` declenche une verification compile + tests unitaires associes.
- L'integration finale est coordonnee par Dev 1 et Dev 2, avec validation fonctionnelle pilotee par Dev 6.

---

## 7. Structure du Projet

```
library-system/
|
+-- src/
|   +-- main/
|   |   +-- java/
|   |   |   +-- com/library/
|   |   |       |
|   |   |       +-- model/
|   |   |       |   +-- Book.java
|   |   |       |   +-- User.java
|   |   |       |   +-- Loan.java
|   |   |       |   +-- Role.java (enum)
|   |   |       |
|   |   |       +-- repository/
|   |   |       |   +-- BookRepository.java
|   |   |       |   +-- UserRepository.java
|   |   |       |   +-- LoanRepository.java
|   |   |       |
|   |   |       +-- service/
|   |   |       |   +-- AuthService.java
|   |   |       |   +-- BookService.java
|   |   |       |   +-- UserService.java
|   |   |       |   +-- LoanService.java
|   |   |       |   +-- StatisticsService.java
|   |   |       |
|   |   |       +-- ui/
|   |   |       |   +-- console/
|   |   |       |   |   +-- MainMenu.java
|   |   |       |   |   +-- AuthConsole.java
|   |   |       |   |   +-- BookConsole.java
|   |   |       |   |   +-- UserConsole.java
|   |   |       |   |   +-- LoanConsole.java
|   |   |       |   |   +-- StatisticsConsole.java
|   |   |       |   +-- gui/
|   |   |       |       (reserve pour Phase 2 - JavaFX)
|   |   |       |
|   |   |       +-- config/
|   |   |       |   +-- AppConfig.java
|   |   |       |   +-- JsonConfig.java
|   |   |       |
|   |   |       +-- util/
|   |   |           +-- PasswordUtils.java
|   |   |           +-- DateUtils.java
|   |   |           +-- ConsoleUtils.java
|   |   |
|   |   +-- resources/
|   |       (reserve pour Phase 2 - fichiers FXML)
|   |
|   +-- test/
|       +-- java/
|           +-- com/library/
|               +-- service/
|               +-- repository/
|
+-- data/
|   +-- books.json
|   +-- users.json
|   +-- loans.json
|
+-- pom.xml
+-- README.md
```

---

## 8. Contraintes Techniques

### 8.1 Separation Stricte des Couches

- La couche `ui` ne peut appeler que la couche `service`. Tout appel direct a `repository` depuis `ui` est interdit.
- La couche `service` ne peut appeler que la couche `repository`. Tout appel direct a `ui` depuis `service` est interdit.
- La couche `repository` ne contient aucune logique conditionnelle metier.

### 8.2 Aucune Logique Metier dans l'Interface

Toute validation, toute regle d'affaire (exemple : verifier qu'un livre est disponible avant emprunt) doit etre implementee dans la couche `service`, jamais dans la couche `ui`. L'UI se contente d'afficher et de collecter.

### 8.3 Abstraction des Repositories

Les repositories exposent une interface stable. Si la persistance venait a changer (base de donnees, fichiers XML, etc.), seule l'implementation du repository serait modifiee, sans impact sur la couche service.

### 8.4 JSON comme Couche de Persistance Temporaire

Les fichiers JSON constituent la persistance de Phase 1. Ils sont adequats pour un volume de donnees modere. Cette approche est volontairement temporaire et ne doit pas influencer la conception des services ou des modeles.

---

## 9. Processus de Developpement

### 9.1 Strategie de Branches Git

Chaque fonctionnalite est developpee dans une branche `feature/nom-du-module` issue de `develop`. Une fois la fonctionnalite terminee et testee localement, une pull request est ouverte vers `develop`. Deux membres au minimum doivent approuver la pull request avant la fusion.

### 9.2 Cycle de Developpement d'une Fonctionnalite

```
1. Creation de la branche feature depuis develop
2. Implementation de la fonctionnalite
3. Tests unitaires et validation locale
4. Ouverture d'une pull request vers develop
5. Code review par au moins un lead
6. Corrections eventuelless
7. Fusion dans develop
8. Suppression de la branche feature
```

### 9.3 Processus d'Integration

- Une fois tous les modules fusionnes dans `develop`, une session d'integration est organisee.
- Les leads executent les tests d'integration croise (exemple : creer un emprunt impliquant les modules auth, livres et emprunts).
- Les anomalies detectees sont traquees et corrigees avant la fusion vers `main`.

### 9.4 Approche de Tests

| Type de Test | Responsable | Cible |
|--------------|-------------|-------|
| Tests unitaires service | Dev 1 | Logique metier |
| Tests unitaires repository | Dev 2 | Lecture/ecriture JSON |
| Tests de validation module | Dev de chaque module | Comportement attendu |
| Tests d'integration | Dev 6 + Leads | Flux complets |

Les tests sont places dans `src/test/java/` en miroir de la structure `main`. Aucun test ne doit acceder directement aux fichiers de donnees de production. Des fichiers JSON de test dedies sont utilises.

---

## 10. Glossaire

| Terme | Definition |
|-------|------------|
| POJO | Plain Old Java Object - classe Java simple sans logique |
| CRUD | Create, Read, Update, Delete - operations de base sur les donnees |
| ISBN | International Standard Book Number - identifiant unique d'un livre |
| Persistance | Mecanisme de sauvegarde des donnees entre deux executions |
| Jackson | Librairie Java de serialisation/deserialisation JSON |
| Pull Request | Demande de fusion de code soumise a review avant integration |
| Lead Developer | Developpeur senior responsable d'un domaine technique et de la qualite globale |

---

*Document genere dans le cadre du projet Library Management System - Equipe 6 membres - Avril 2026*
