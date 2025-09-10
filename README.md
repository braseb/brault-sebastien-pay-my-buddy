# 📦 PayMyBuddy — Modèle de Base de Données

Ce projet utilise une base de données PostgreSQL pour gérer les utilisateurs, leurs connexions et leurs transactions financières.

---

## 📐 Schéma SQL complet

```sql
-- =====================
-- Table : users
-- =====================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- =====================
-- Table : transaction
-- =====================
CREATE TABLE transaction (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    description TEXT,
    amount DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_transaction_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_sender_receiver_diff CHECK (sender_id <> receiver_id)
);

-- =====================
-- Table : user_connection
-- =====================
CREATE TABLE user_connection (
    user_id INTEGER NOT NULL,
    user_connection_id INTEGER NOT NULL,
    CONSTRAINT pk_user_connections PRIMARY KEY (user_id, user_connection_id),
    CONSTRAINT fk_connection_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_connection_partner FOREIGN KEY (user_connection_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_connection CHECK (user_id <> user_connection_id),
);
```

---

## ✅ Contraintes importantes

- **Unicité des utilisateurs** : les e-mails sont uniques.
- **Transactions valides** : un utilisateur ne peut pas s'envoyer de l'argent à lui-même (`sender_id <> receiver_id`).
- **Clés étrangères** : toutes les relations sont maintenues avec `ON DELETE CASCADE`.
- **Connexions directionnelles** : `(1, 2)` est différent de `(2, 1)`, ce qui permet d'avoir des relations asymétriques.
- **Vérification** : user_id est différent de user_connection_id, un utilisateur ne peut pas s'affecter lui même

---

## Cloner le dépôt :  
```bash
git clone https://github.com/ton-utilisateur/paymybuddy.git
cd paymybuddy
```

---

## 🧪 Exemple de création via terminal

```bash
createdb -U "utilisateur" paymybuddy
psql paymybuddy < schema.sql
```

---

## 🗂️ Schéma utilisé

Le schéma PostgreSQL utilisé est `public` (par défaut).

Pour forcer ce comportement à chaque connexion :

```sql
ALTER DATABASE paymybuddy SET search_path TO public;
```

---

## 🔗 Endpoints

## 🔗 Endpoints / Routes de l’application

| Méthode | URL                     | Description                                        | Vue Thymeleaf / Redirection           |
|---------|------------------------|---------------------------------------------------|--------------------------------------|
| GET     | /login                 | Formulaire de connexion                           | login.html                            |
| GET     | /register              | Formulaire d’inscription                          | register.html                         |
| POST    | /register              | Création d’un nouvel utilisateur                 | redirect:/login                       |
| GET     | /profile               | Affichage du profil utilisateur                   | profile.html                           |
| PUT     | /profile               | Mise à jour du profil utilisateur                 | redirect:/profile                      |
| GET     | /connection            | Page pour ajouter une connexion entre utilisateurs      | connection.html                        |
| POST    | /add_user_connection   | Ajouter un utilisateur au connexions de l'utilisateur          | redirect:/connection                   |
| GET     | /transaction           | Page listant les transactions de l’utilisateur  | transaction.html                        |
| POST    | /transaction           | Créer une nouvelle transaction                    | redirect:/transaction                  |


---

### Gestion des exceptions

| Exception                                  | Endpoint concerné                  | Action effectuée                                      |
|-------------------------------------------|-----------------------------------|------------------------------------------------------|
| `UserAppendConnectionError`                | /add_user_connection               | Redirige vers /connection avec message d’erreur     |
| `ConstraintViolationException`             | /add_user_connection               | Redirige vers /connection avec message d’erreur     |
| `UserNotFoundException`                    | /add_user_connection               | Redirige vers /connection avec message d’erreur     |
| `UserAlreadyExistException`                | /register ou /profile              | Redirige vers /register ou /profile avec erreur     |
| `IllegalArgumentException`                 | /profile                           | Redirige vers /profile avec message d’erreur        |


## 🔑 Gestion du mot de passe avec Keyring (Libsecret)

Pour sécuriser la connexion à la base de données, le projet utilise Keyring avec Libsecret afin d’éviter de stocker le mot de passe en clair. Dans `application.properties`, configurez le service et le compte comme suit :

```properties
keyring.datasource.service=PayMyBuddy
keyring.datasource.account=admin
```

-  --label : description affichée dans le trousseau.

- service : identifiant du service (doit correspondre à keyring.datasource.service).

- account : identifiant du compte (doit correspondre à keyring.datasource.account).
    
Ensuite, enregistrez le mot de passe dans le trousseau avec la commande :

```bash
secret-tool store --label="PayMyBuddy Database Password" service PayMyBuddy account admin
```
Le terminal vous demandera de saisir le mot de passe de la base de données. Pour vérifier que le mot de passe a bien été enregistré, utilisez :

```bash
secret-tool lookup service PayMyBuddy account admin
```
Si tout est correct, la commande renverra votre mot de passe.

---

## ⚙️ Configuration

- `application.properties` ou `application.yml` : configuration de la base de données, port, etc.
- Exemple : 
            spring.application.name=PayMyBuddy

            spring.datasource.url=jdbc:postgresql://localhost:5432/paymybuddy
            spring.jpa.show-sql=true
            server.error.whitelabel.enabled=false
            # Keyring pour les identifiants
            keyring.datasource.service=PayMyBuddy
            keyring.datasource.account=admin

---



## 🚀 Lancer le projet

```bash
# depuis le répertoire du projet
./mvnw spring-boot:run
# ou avec Maven installé
mvn spring-boot:run
# ou avec java
java -jar target/paymybuddy.jar
# avec options
java -jar target/paymybuddy-0.0.1-SNAPSHOT.jar \
     --keyring.datasource.service=NomService \
     --keyring.datasource.account=NomCompte


```

---

## ✍️ Auteur

Modélisation de base de données pour l'application **PayMyBuddy**.
