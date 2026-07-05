# PoC Tchat WebSocket - Your Car Your Way

Cette application est une preuve de concept simple d’un tchat en temps réel.

Elle est composée de :

- un **backend Spring Boot** ;
- un **frontend Angular** ;
- une communication en temps réel avec **WebSocket** ;
- une base de données **H2 en mémoire** pour conserver l’historique pendant l’exécution de l’application.

## Versions utilisées

- Java 21
- Spring Boot 3.3.2
- Angular 19.2
- Node.js 20.11.1

## Lancer le backend

Depuis le dossier `back` :

```bash
mvn spring-boot:run
```

Le backend démarre sur :

```text
http://localhost:8080
```

## Lancer le frontend

Dans un deuxième terminal, depuis le dossier `front` :

```bash
npm install
npm start
```

Le frontend est accessible sur :

```text
http://localhost:4200
```

## Tester le tchat

1. Ouvrir `http://localhost:4200` dans deux fenêtres de navigateur.
2. Saisir un pseudo différent dans chaque fenêtre.
3. Envoyer des messages.
4. Les messages apparaissent en temps réel dans les deux fenêtres.

Les messages de l’utilisateur courant apparaissent avec un fond bleu.

## Remarque

La base H2 est en mémoire. L’historique des messages est donc réinitialisé à chaque redémarrage du backend.
