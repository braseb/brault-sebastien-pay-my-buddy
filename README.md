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
    CONSTRAINT fk_connection_partner FOREIGN KEY (user_connection_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

## ✅ Contraintes importantes

- **Unicité des utilisateurs** : les e-mails sont uniques.
- **Transactions valides** : un utilisateur ne peut pas s'envoyer de l'argent à lui-même (`sender_id <> receiver_id`).
- **Clés étrangères** : toutes les relations sont maintenues avec `ON DELETE CASCADE`.
- **Connexions directionnelles** : `(1, 2)` est différent de `(2, 1)`, ce qui permet d'avoir des relations asymétriques.

---

## 🧪 Exemple de création via terminal

```bash
createdb paymybuddy
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

## ✍️ Auteur

Modélisation de base de données pour l'application **PayMyBuddy**.
