# Comment contribuer à ce projet
2 cas sont possibles pour contribuer à ce projet:
- Vous ête un étudiant et l'utilisez pour créer votre projet, dans ce cas reportez vous à [la section étudiant](#etudiant)
- Vous êtes un enseignant améliorant ce modèle, dans ce cas reportez vous à [la section enseignant](#enseignant)

## Etudiant

Vous ne devez pas contribuer directement à ce projet mais devez en effectuer un fork. Une fois cela effectué vous devez:
- [ ] Ajouter votre identifiant de groupe au champs `groupId` du fichier [pom.xml](pom.xml) sous la forme de `fr.u-paris.gla.votreequipe`
- [ ] Modifier le package principal afin de refleter le nouveau nom de groupe.
- [ ] Adapter le fichier [README](README.md) au contenu de votre projet specifique
- [ ] Adapter ce fichier (CONTRIBUTING.md) à vos propres instructions de contribution, notamment:
  - [ ] Convention de style de codage
  - [ ] Convention d'utilisation de git
  - [ ] Lien avec d'autres projets et d'autres dépôts.
- [ ] Modifier le fichier `application.properties` au besoin.


## Enseignant

Ce dépôt suit la convention de gitflow. Les modifications doivent être effectuées dans des branches séparées,
intégrées dans la branche dev une fois terminée.
La branche main ne doit contenir que des versions stables de ce modèle.

Le code est écrit en Java, manipulé par l'outils de construction maven et doit suivre les conventions usuelles du langage et de l'outils.

Le package principal du code Java est `fr.u_paris.gla.project`
Le fichier de properties `application.properties` permet d'accéder depuis le code Java aux diverses informations inscrite dans maven.
