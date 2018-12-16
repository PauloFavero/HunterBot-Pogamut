# Hunter Bot - Pogamut

Ce projet consiste à concevoir et à programmer un Personnage Non Joueur (PNJ/BOT) dans le jeux Unreal Tournament 2004 en utilisant la bibliothèque Pogamut.
Le point de départ était le projet Maven *HunterBot* de la bibliothèque citée.
A l'origine, il n'existait qu'une seule classe de *HunterBot* qui possède tous les attributs et méthodes faisant partie de la dynamique du bot.


## Pour Commencer

Pour lancer le bot dans les ordinateurs de la salle B005, il sufit suivre les instructions présentes sur moodle dans le module *IAS* de l'ENIB. Après avoir ouvert le projet dans NetBeans, il faut ajouter une dépendence chez maven pour pouvoir gerer des fichier *.csv*. Cette insertion doit être fait dans le fichier *pom.xlm* 

```xml

<dependencies>
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>3.7</version>
        </dependency>
</dependencies>
```

Les instructions pour l'instalation dans une machine personnel se trouvent dans la sous section Prérequis d'installations.

### Prérequis d'installations

Toutes les indications mensionnées ci-dessous sont necessaires pour l'installation dans une machine personnel. Un tutoriel de l'installation se trouve dans le document [Pogamut](http://pogamut.cuni.cz/main/tiki-view_blog_post.php?postId=47)

* [Java JDK 1.7](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](https://maven.apache.org/)
*  NetBeans 7.4
*  [Unreal Tournament 2004](https://store.steampowered.com/app/13230/Unreal_Tournament_2004_Editors_Choice_Edition/)
*  [Pogamut](http://pogamut.cuni.cz/main/tiki-index.php) 

## Implémentation

Dans cette section, nous expliquerons comment nous avons développé de notre projet.

### Remplissement des éxigences minimales

L'automate à états que nous avons developpé ce trouve ci-dessous.

<img src="https://github.com/PauloFavero/HunterBot-Pogamut/blob/master/Bot_States.png" width="400" height="400" />

Chaque état possède ses propres transitions de sortie organisé par ordre de priorité.

L'exemple ci-dessous, nous montre les transitions associé à l'état *Attack*. 

```java
    public AttackState() {
        transitions = new Transition[4];
        transitions[0] = new TransitionToAttack();
        transitions[1] = new TransitionToHurted();
        transitions[2] = new TransitionToPursue();
        transitions[3] = new TransitionToSearch();
    }
```

La machine à états a pour rôle de verifier le transitions qui appartient à l'état actuel et changer d'état si la condition pour la transition est vrai. Cette verification est effectué par la methode *execute* de la machine à état.

```java
public void execute(HunterBot Bot) {
        for(int i = 0; i < this.currentState.getTransitions().length; i++){
            HunterState hs = this.currentState.getTransitions()[i].transition(Bot);
            if(hs != null){
                this.currentState = hs;
                break;
            }
        }
    }
 ```
 La structure ci-dessus nous permet à facilement d'ajouter de transitions et d'états au fur et à mesure. Il suffit d'ajouter des transitions si nécessaire à un état correspondant.
 
 ### Application du Machine Learning
 
 Nous avons appliqué un algoritme d'apprentissage basé sur un article publié dans la revue de l'ICANN. L'article est : **Neural Networks Training for Weapon Selection in First-Person Shooter Games** écrit par **Stelios Petrakis and Anastasios Tefas**.
 
 Nous avons conçu un programme en python avec 9 réseaux de neuronnes qui ont pour but d'apprendre à choisir la meilleur arme à utiliser dans une situation donné. Les données qu'iront alimenter nos réseaux sont obtenus au moment où le bot inflinge  des dêgats à un ennemi. Chaque réseaux genère des fichiers avec le poids de la réseaux entrainé. 
 La réseux est structuré comme la liste ci-dessous:
 
 * **Couche d'Entre:** 3 Neuronnes (Distance, Rotation, Vitesse)
 * **Couche Caché:** 50 Neuronnes activés par la fonction tangent hyperbolique.
 * **Couche de Sortie** 1 Neuronne (Dêgats)

 Avec un mois de plus, nous aurions pu conçevoir un réseaux de neuronnes en java avec le poids obtenus par notre programme en python.
 Le choix de l'arme était sensé être implementé dans la classe SwitchToBestWeaponState.

### Diagramme de classes du bot

Nous nous servons du patron de conception *[State](https://sourcemaking.com/design_patterns/state)* et d'une interface pour gerer les transitions. 

La classe java *StateMachine* contient tous les états et transitions. Tous les états héritent de l'interface *HunterState*, qui possède une méthode *execute()*. Cette méthode correspond au comportement du bot pour une telle situation.

Chaque état a ses transitions de sortie possibles et chaque classe de transition vérifie si sa condition est satisfaisante et si elle est vraie, elle renvoie à l'état suivant.

L'image ci-dessous exemplifique notre patron de conception que fait notre bot tourner.

<img src="https://github.com/PauloFavero/HunterBot-Pogamut/blob/master/Bot_UML.png" width="400" height="400" />

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Pogamut](http://pogamut.cuni.cz/main/tiki-index.php) - Java Middleware to control virtual agents

## Auteurs: 

* **Helon Moreira**

* **Paulo Henrique Favero Pereira**


