# Voxel-Game
Ceci est un jeu de type Voxel.

![Image du jeu](Images%20du%20jeu/screen27.png)

# Utilisation
Ce jeu utilise les bibliothèques qui se trouvent dans [lib](lib), à savoir [lwjgl](lwjgl.jar) et [lwjgl_utils](lwjgl_utils.jar).
Un [Makefile](Makefile) permet de compiler le jeu sur Linux en utilisant la commande :
```
make
```
Pour jouer vous pouvez lancer le serveur avec : `make server`  
Puis vous pouvez lancer le jeu avec : `make game`

## BUG
Si vous n'arrivez pas à exécuter le serveur il se peut que cela vienne d'une récente mise à jour de Java. Dans ce cas, installez
Java8 (`sudo apt install openjdk-8-jdk` sur Debian) puis changez les versions de votre compilateur et de votre interpréteur comme ceci :
```
sudo update-alternatives --config javac
sudo update-alternatives --config java
```
Vous aurez à renseigner un chiffre indiquant la version voulu pour ces deux commandes.

# Editeur de Voxel
Vous trouverez un éditeur de voxel pour créer de nouvelles armes dans le package [editor](src/fr/coudert/editor) :

![Editeur](Images%20du%20jeu/screen09.png)

Vous pouvez alors lancer l'éditeur de voxel via : `make editor`
