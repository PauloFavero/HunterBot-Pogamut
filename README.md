# Hunter Bot - Pogamut

Ce projet consiste à concevoir et à programmer un Personnage Non Joueur (Bot) pour le jeux Unreal Tournament 2004 en utilisant la bibliothèque Pogamut.
Le point de départ était le projet Maven *HunterBot* de la bibliothèque citée.
A l'origine, il n'existait qu'une seule classe *HunterBot* qui possède tous les attributs et méthodes faisant partie de la dynamique du bot.


## Pour Commencer

Pour lancer le bot dans les ordinateurs de la salle B005, il faut juste suivre les instructions presentes au moodle du module *IAS* de l'ENIB. Après ouvrir le projet au NetBeans, il faut ajouter une dependence chez maven pour pouvoir gerer des fichier *.csv*. Cet insertion doit être fait dans le fichier *pom.xlm*

```xml

<dependencies>
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>3.7</version>
        </dependency>
</dependencies>
```

Les instructions pour l'instalation dans une machine personnel se trouvent dans la subsection Prerequisites d'installations.

### Prerequisites d'installations

Tous les itens suivantes, sont necessaires pour l'installations dans une machine personnel. Un tutoriel de l'installation se trouve dans la documentation [Pogamut](http://pogamut.cuni.cz/main/tiki-view_blog_post.php?postId=47)

* Item [Java JDK 1.7](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Item [Maven](https://maven.apache.org/)
* Item NetBeans 7.4
* Item [Unreal Tournament 2004](https://store.steampowered.com/app/13230/Unreal_Tournament_2004_Editors_Choice_Edition/)
* Item [Pogamut](http://pogamut.cuni.cz/main/tiki-index.php) 

## Implementation

Dans cette section, nous expliquerons comment a été le développement de notre projet.

### Rempliment d'exigences minimales

L'automate à états qui nous avons developpé ce trouve ci-dessous.

<img src="https://github.com/PauloFavero/HunterBot-Pogamut/blob/master/Bot_States.png" width="400" height="400" />

Chaque état possède ses respectives transitions de sortie organisé par ordre de priorité.

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
 La structure ci-dessus nous permet à facilement d'ajouter de transitions et d'états au fur et à mesure. Il suffit juste d'ajouter des transitions si necessaire à un état correspondant.
 
 ### Application du Machine Learning
 
 Nous avons applique un algoritme d'apprentissage basé sur un article publié dans la revue ICANN. L'article c'est lle **Neural Networks Training for Weapon Selection in First-Person Shooter Games** écrit par **Stelios Petrakis and Anastasios Tefas**.
 
 Nous avons conçu un programme en python avec 9 réseaux de neuronnes que l'on pour but d'apprendre quelle est la meilleur arme à utiliser à une situation donné. Les données qu'iront alimenter nos réseaux sont obtenus dans le moment qui le bot inflinge dêgats à un ennemie. Chaque réseaux genère des fichiers avec les poids de la réseaux entrainé. 
 La réseux est structuré comme la liste ci-dessous:
 
 * **Couche d'Entre:** 3 Neuronnes (Distance, Rotation, Vitesse)
 * **Couche Caché:** 50 Neuronnes activés par la fonction tangent hyperbolique.
 * **Couche de Sortie** 1 Neuronne (Dêgats)

 Si nous pourrions avoir un mois de plus, nous aurions conçu une réseaux de neuronnes en java avec les poids obtenus par notre programme en python. La choix de l'arme était sense à être implementé dans la classe SwitchToBestWeaponState.

### Diagramme de classes du bot

Nous nous asservissons du patron de conception *[State](https://sourcemaking.com/design_patterns/state)* et en plus d'une interface pour gerer les transitions. 

La classe java *StateMachine* contient tous les états et transitions. Tous les états héritent de l'interface *HunterState*, qui possède une seule méthode *execute()*. Cette méthode correspond au comportement du bot pour une telle situation.

Chaque état a ses transitions de sortie possibles et chaque classe de transition vérifie si sa condition est satisfaite et si elle est vraie, elle renvoie à l'état suivant.

L'image ci-dessous exemplifique notre patron de conception que fait notre bot tourner.

<img src="https://github.com/PauloFavero/HunterBot-Pogamut/blob/master/Bot_UML.png" width="400" height="400" />

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Pogamut](http://pogamut.cuni.cz/main/tiki-index.php) - Java Middleware to control virtual agents

## Auteurs: 

* **Helon Moreira**

* **Paulo Henrique Favero Pereira**

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

