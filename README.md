# Voxel-Game
Ceci est un jeu de type Voxel.

![Image du jeu](Images%20du%20jeu/screen27.png)

# Utilisation
Il vous faudra utiliser les bibliothèques qui se trouvent dans [lib](lib), à savoir [lwjgl](lwjgl.jar) et
[lwjgl_utils](lwjgl_utils.jar). Les natives pour lwjgl se trouvent dans le dossier [natives](lib/natives). Vous pouvez copier
ces fichiers natives dans le dossier `/usr/lib`. Voici maintenant comment compiler le jeu :
```
mkdir build
find -name "*.java" > sources.txt
javac -d "bin2" -cp "src:lib/*" @sources.txt
```
Pour jouer vous pouvez lancer le serveur avec : `java -cp "bin/:lib/*" fr.coudert.game.ServerMain`  
Puis vous pouvez lancer le jeu avec : `java -cp "bin/:lib/*" fr.coudert.game.GameMain`

## BUG
Si vous n'arrivez pas à exécuter le serveur il se peut que cela vienne d'une récente mise à jour de Java. Dans ce cas, installer
Java8 puis changer les versions de votre compilateur et de votre interpréteur comme ceci :
```
sudo update-alternatives --config javac
sudo update-alternatives --config java
```
Vous aurez à renseigner un chiffre indiquant la version voulu pour ces deux commandes.

# Editeur de Voxel
Vous trouverez un éditeur de voxel pour créer de nouvelles armes dans le package [editor](src/fr/coudert/editor) :

![Editeur](Images%20du%20jeu/screen09.png)

Vous pouvez alors lancer l'éditeur de voxel via : `java -cp "bin/:lib/*" fr.coudert.editor.EditorMain`
