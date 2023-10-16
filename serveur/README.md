# Serveur Java d'Extinction Mini Jeux

## Licence

GNU GPL v3

## Propriétés du projet

Encodage : UTF-8

## Bibliothèques

### MySQL

lib/mysql-connector-java-5.0.5-bin.jar

### Websockets

lib/java-websocket.jar

lib/slf4j-nop-1.7.25.jar

[Github de la lib](https://github.com/TooTallNate/Java-WebSocket)

## Aperçu général du fonctionnement du code

* *serveur/Local.java* : spécifier si le serveur est en mode local et/ou bêta
* *serveur/Serveur.java* : chargement du jeu (cartes Aaaah! & Bouboum, timers, version du jeu, lance l'initialisation du réseau, etc), liste des parties/joueurs, etc
* *serveur/Boite.java* : thread gérant les interactions avec la BDD sur base de requêtes (.Requete()) ajouter par des classes externes
* *joueur/Joueur.java* : toutes les données du joueur et gestion des messages réseau
* *serveur/Partie.java* : créer et rejoindre une partie
* *serveur/BDD.java* : contient toutes les requêtes de BDD
* *serveur/Nio.java* : gestion des interactions réseau
* *serveurWS/...* : gestion des interactions réseau (WebSockets)
