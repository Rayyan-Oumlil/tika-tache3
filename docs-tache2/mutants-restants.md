# Notes brutes : mutants restants après les tests manuels (pit-final)

| Classe | Originaux | + générés | + manuels |
|---|---|---|---|
| EndianUtils | 18,4 % | 50,7 % | **88,4 %** (183/207) |
| FilenameUtils | 63,5 % | 64,3 % | **88,7 %** (102/115) |

## EndianUtils : 24 survivants, tous équivalents

- `getIntBE` L388 / `getIntLE` L362 « increment 1 → -1 » : c'est le `i++` du **dernier** `data[i++]`. `i` n'est plus lu après, donc changer l'incrément ne change rien.
- `readXxx` « bitwise OR → AND » dans `(ch1 | ch2 | ... ) < 0` (22 mutants) : d'après le contrat d'`InputStream`, une fois `read()` à -1 (fin de flux), tous les appels suivants renvoient aussi -1. Les -1 forment donc toujours un **suffixe**. Remplacer un `|` qui n'implique pas le dernier octet par `&` donne encore un résultat négatif dès qu'un octet manque. Seul le mutant sur le dernier `|` est observable ; il est tué par `streamThatEndsOneByteTooEarlyIsAnUnderrun`.

## FilenameUtils : 13 restants sous Windows (5 sous Linux), tous équivalents ou inatteignables

- `getPrefixLength` L320–324 (9 mutants, **sous Windows seulement**) : commons-io 2.22 renvoie déjà 2 pour `"C:"` sous Windows, donc la branche de repli n'est pas atteinte. Sous Linux, commons-io renvoie 0 pour un chemin de 2 caractères (`FileSystem.supportsDriveLetter()` faux) : le repli est exécuté et ces mutants sont tués dans la CI, les bornes `'A'`/`'Z'` par `driveLettersAtBothEndsOfTheAlphabetAreStrippedOnEverySystem`. Final CI (Linux) : FilenameUtils 109/114 = 95,6 %, total 292/321.
- `getSanitizedEmbeddedFileName` L156 (borne et négation) et `getSanitizedEmbeddedFilePath` L215 (borne) : `getPrefixLength` ne renvoie jamais de valeur négative, et `substring(0)` est l'identité. Ne pas retirer un préfixe `C:\` ne change rien, puisque les `:` et `\` sont ensuite remplacés par `/` et que `getName` ne garde que le dernier segment.
- `getSanitizedEmbeddedFileName` L185 et `getSanitizedEmbeddedFilePath` L259 (« return "" » non couverts) : vérifications « défense en profondeur » (commentaire du code). Un `namePart` vide est déjà intercepté plus haut, et les remplacements entre les deux ne peuvent pas rendre vide une chaîne non vide.
