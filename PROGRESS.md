# État d'Avancement du Projet (Library System)

Ce document permet de visualiser la progression globale du développement de l'application de gestion de bibliothèque, conformément à l'architecture définie dans le `README.md` et aux responsabilités de la `documentation_technique.md`.

## 👥 Équipe & Rôles
- **Développeur 1** : Lead Backend - Architecture & Services
- **Développeur 2** : Lead Data - Repository & Persistance
- **Développeur 3** : Authentification & UI Utilisateurs
- **Développeur 4** : Module Livres
- **Développeur 5** : Module Emprunts
- **Développeur 6** : Statistiques & Support Tests

---

## 📦 1. Structure et Configuration (Phase Initiale)
**Rôle :** Met en place les fondations du projet, gère les dépendances (ex: Jackson pour JSON) et le point d'entrée de l'application.
- [x] Initialisation du projet (Maven) **[Assigné à : Développeur 1]**
- [x] Configuration du `pom.xml` (Dépendances telles que Jackson pour le JSON) **[Assigné à : Développeur 1]**
- [x] Création de la structure des packages (`com.library.*`) **[Assigné à : Développeur 1]**
- [x] Documentation initiale (`README.md`)
- [x] Script principal basique (`Main.java`)

## 🧱 2. Couche Modèle (Entités) - **Terminé**
**Rôle :** Définit la structure des données pures en mémoire. Ce sont des objets simples (POJOs) avec leurs attributs, getters et setters, sans aucune logique métier complexe ni lien avec la base de données ou l'interface.
- [x] `Book` (Livre) **[Assigné à : Développeur 2]**
- [x] `User` (Utilisateur) **[Assigné à : Développeur 2]**
- [x] `Loan` (Emprunt) **[Assigné à : Développeur 2]**
- [x] `HoldRequest` (Réservation) **[Assigné à : Développeur 2]**
- [x] Énumérations (`Role`, `HoldStatus`, `LoanStatus`, `UserStatus`) **[Assigné à : Développeur 2]**

## 💾 3. Couche Repository (Persistance JSON) - **Terminé**
**Rôle :** S'occupe *exclusivement* du chargement et de la sauvegarde des données depuis/vers les fichiers `.json` (`books.json`, `users.json`, etc.). Ne contient aucune règle de gestion (ex: vérifier si un livre est disponible), il ne fait que lire et écrire les données.
- [x] Interfaces de repository (`BookRepository`, `UserRepository`, `LoanRepository`, `HoldRequestRepository`) **[Assigné à : Développeur 2]**
- [x] Implémentation de la lecture/écriture JSON (`JsonBookRepository`, `JsonUserRepository`, `JsonLoanRepository`, `JsonHoldRequestRepository`) **[Assigné à : Développeur 2]**

## ⚙️ 4. Couche Service (Logique Métier) - **À faire / En cours**
**Rôle :** Contient le "cerveau" de l'application. Applique les règles de gestion (ex: "un utilisateur ne peut pas emprunter plus de 3 livres", "vérifier les mots de passe"), coordonne les actions et fait le lien entre l'UI et les Repositories.
- [x] Définition des classes / interfaces de services (`AuthService`, `BookService`, `UserService`, `LoanService`, `StatisticsService`) **[Assigné à : Développeur 1]**
- [x] `AuthService` : Gère la connexion, déconnexion et la session de l'utilisateur courant. **[Assigné à : Développeur 1]**
- [x] `BookService` : Gère l'ajout de livres, la recherche, et la vérification de disponibilité. **[Assigné à : Développeur 1]**
- [x] `UserService` : Gère l'inscription, la suspension et la liste des utilisateurs. **[Assigné à : Développeur 1]**
- [x] `LoanService` : Gère les emprunts, les retours, les réservations et le calcul des pénalités/retards. **[Assigné à : Développeur 1]**
- [x] `StatisticsService` : Agrège les données pour fournir des rapports (livres les plus empruntés, etc.). **[Assigné à : Développeur 1]**

## 🖥️ 5. Couche UI Console (Interface Utilisateur) - **À faire**
**Rôle :** Interagit avec l'utilisateur. Affiche les menus, récupère les saisies clavier, et appelle les méthodes de la couche `Service` correspondantes pour exécuter les actions. Elle ne *manipule jamais* directement la couche `Repository`.
- [ ] Menu principal de navigation **[Assigné à : Développeur 3]**
- [ ] Gestion des utilisateurs (`UserConsole`) **[Assigné à : Développeur 3]**
- [ ] Interface d'authentification (`AuthConsole`) **[Assigné à : Développeur 3]**
- [ ] Gestion des livres (`BookConsole`) **[Assigné à : Développeur 4]**
- [ ] Gestion des emprunts et retours (`LoanConsole`) **[Assigné à : Développeur 5]**
- [ ] Affichage des statistiques (`StatisticsConsole`) **[Assigné à : Développeur 6]**
- [ ] Connexion avec les `Service` depuis `Main.java` **[Assigné à : Équipe / Leads]**

## 🧪 6. Tests Unitaires - **À faire**
**Rôle :** S'assure que chaque composant fonctionne correctement de manière isolée pour éviter les bugs lors des évolutions futures.
- [ ] Tests pour les `Repository` (Vérification de la sérialisation/désérialisation JSON) **[Assigné à : Développeur 6]**
- [ ] Tests pour les `Service` (Validation de chaque règle métier) **[Assigné à : Développeur 6]**

## 🎨 7. Couche UI GUI (Phase 2 - JavaFX) - **À faire plus tard**
**Rôle :** Future interface graphique fenêtrée, qui remplacera ou complètera la Console UI, tout en réutilisant exactement les mêmes `Service`.
- [ ] Vues FXML (Fenêtres, boutons, tableaux) **[Assigné à : À définir (Phase 2)]**
- [ ] Contrôleurs JavaFX (Lien entre la vue et les services) **[Assigné à : À définir (Phase 2)]**
- [ ] Intégration globale **[Assigné à : À définir (Phase 2)]**
