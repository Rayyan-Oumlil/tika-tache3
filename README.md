# IFT3913 — Tâche 2 : génération de tests par LLM et analyse de mutation sur Apache Tika

| Nom complet | Identifiant GitHub |
|---|---|
| Oumlil Rayyan | Rayyan-Oumlil |
| Khettal Amine | aminekh04 |

Le README original d'Apache Tika a été déplacé dans [`README-tika.md`](README-tika.md).

**Résumé**

| Classe (`tika-core`) | Score de mutation, tests originaux | + tests générés (ChatUniTest) | + tests écrits à la main | Final dans la CI (Linux) |
|---|---|---|---|---|
| `org.apache.tika.io.EndianUtils` | 18,4 % | 50,7 % | **88,4 %** | **88,4 %** (183/207) |
| `org.apache.tika.io.FilenameUtils` | 63,5 % | 64,3 % | **88,7 %** | **95,6 %** (109/114) |

Les quatre premières colonnes viennent d'exécutions sous Windows. `FilenameUtils` contient une branche qui ne s'exécute que sous Linux, d'où la dernière colonne (section 4). Tous les mutants encore vivants à la fin sont **équivalents ou dans du code inatteignable** (section 6).

---

## 1. Classes choisies et justification

Les deux classes ont déjà une classe de test dans Tika (`EndianUtilsTest`, `FilenameUtilsTest`), mais sont loin d'être entièrement couvertes. Les mesures viennent de `mvn verify` (JaCoCo) puis de pitest 1.30.0 (mutateurs par défaut), sur le dépôt avant nos ajouts.

| Classe | Lignes couvertes | Branches couvertes | Mutants | Tués | Survivants | Sans couverture |
|---|---|---|---|---|---|---|
| `EndianUtils` | 26 % | 36 % | 206 | 38 (18,4 %) | 14 | 154 |
| `FilenameUtils` | 87 % | 75 % | 115 | 73 (63,5 %) | 20 | 22 |

- **`EndianUtils`** lit des entiers dans des tableaux d'octets ou des flux, en little/big-endian. Ce sont des fonctions pures pleines d'opérateurs (`<<`, `+`, `&`, `|`), donc beaucoup de mutants. Les tests originaux couvrent à peine les méthodes `readXxx(InputStream)`, qui laissent **154 mutants sans couverture**.
- **`FilenameUtils`** nettoie les noms et chemins de fichiers embarqués : c'est une protection contre le *zip slip*. La couverture de lignes est bonne, mais il reste **20 mutants survivants**. Ils sont concentrés dans l'ordre de priorité des clés de métadonnées (`getEmbeddedName`, `getEmbeddedPath`) et dans les bornes de troncature : du code exécuté, mais dont le résultat n'est pas vérifié.
- `MediaType` a aussi été mesurée (81 %, 2 survivants seulement) puis écartée : il y avait trop peu à apprendre.

Pour reproduire :

```bash
mvn install -pl tika-core -am -DskipTests
cd tika-core
mvn verify                                   # couverture : target/site/jacoco/
mvn org.pitest:pitest-maven:mutationCoverage \
    -DtargetClasses=org.apache.tika.io.EndianUtils,org.apache.tika.io.FilenameUtils
```

## 2. Installation de ChatUniTest avec un modèle local

Tout est dans [`tika-core/pom.xml`](tika-core/pom.xml) (blocs commentés « IFT3913 tâche 2 »).

- **Plugin :** `io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1`.
- **Modèle :** `qwen2.5-coder:7b` exécuté localement par **Ollama**, à travers son API compatible OpenAI (`http://localhost:11434/v1/chat/completions`). Machine : RTX 5060 8 Go, 32 Go de RAM.
- **Configuration :** `testNumber=3`, `maxRounds=3`, `temperature=0.2`, `thread=false` (un seul GPU).

Deux choix techniques à justifier :

1. **Nom du modèle.** Le plugin refuse tout nom hors de sa liste fixe (`No Model with name qwen2.5-coder:7b`). Comme `codeqwen:v1.5-chat` est dans la liste, on a créé un alias Ollama vers le même modèle : `ollama cp qwen2.5-coder:7b codeqwen:v1.5-chat`. Les deux noms ont le même identifiant (`dae161e27b0e`) : c'est bien `qwen2.5-coder:7b` qui génère les tests.
2. **Pas de `chatunitest-starter`.** La dépendance recommandée impose, en scope `compile`, JUnit 5.9.2, Mockito 3.8 et Byte Buddy 1.10.20. Cette dernière ne supporte pas Java 21 et Tika utilise JUnit 6. On a donc ajouté seulement ce dont les tests générés ont besoin (`junit-jupiter-params`, `junit-platform-launcher`, `mockito-core`, `mockito-junit-jupiter`), en scope `test`, aux versions déjà gérées par Tika.

Génération :

```bash
ollama cp qwen2.5-coder:7b codeqwen:v1.5-chat
cd tika-core
mvn chatunitest:class -DselectClass=org.apache.tika.io.FilenameUtils
mvn chatunitest:class -DselectClass=org.apache.tika.io.EndianUtils
```

## 3. Tests générés

### Où sont-ils ?

- **Sortie brute de ChatUniTest** (non modifiée) : [`tika-core/chatunitest-tests/`](tika-core/chatunitest-tests/tika-parent/tika-core/org/apache/tika/io/), soit 24 classes de test et 2 suites.
- **Version intégrée au projet** (après corrections) : `tika-core/src/test/java/org/apache/tika/io/EndianUtils_*_Test.java` et `FilenameUtils_*_Test.java`.
- **Logs de génération :** [`docs-tache2/chatunitest-*.log`](docs-tache2/).

### Compilent-ils et s'exécutent-ils sans intervention ?

**Non.** Le statut « compile and execute successfully » de ChatUniTest veut dire *compile et s'exécute*, **pas** *les assertions passent*. Par exemple, `getSuffixFromPath` est marqué réussi alors que son propre rapport indique 0 test réussi et 1 échoué.

| | `FilenameUtils` | `EndianUtils` |
|---|---|---|
| Méthodes ciblées par ChatUniTest | 7 | 23 |
| Méthodes sans aucun test qui compile | 0 (2 gardés sans validation) | **12** (11 des 12 `readXxx(InputStream)` et `getUIntBE`) |
| Échecs de compilation pendant la génération | 16 | 130 |
| Tests produits | 27 | 19 |
| Suite JUnit produite | ne compile pas | ne compile pas |
| Tests qui passent tels quels | **18 / 27** | **17 / 19** |
| Après corrections manuelles | 27 / 27 | 19 / 19 |

### Corrections nécessaires : 11

| # | Fichier(s) | Problème | Correction |
|---|---|---|---|
| C1, C8 | `*_Suite.java` | Utilisent le runner JUnit 4 (`org.junit.platform.runner`), absent de JUnit 6 | Non intégrées : Surefire trouve les classes sans suite |
| C2 | `FilenameUtils_normalize_0_0` | Méthode **publique** appelée par réflexion, donc l'`IllegalArgumentException` attendue arrive enveloppée dans une `InvocationTargetException` | Appel direct |
| C3 | `FilenameUtils_getSuffixFromPath_2_0` | Le modèle croit `.docx` invalide ; le code accepte jusqu'à 5 caractères, point compris | `*.docx` → `*.backup`, ce qui garde l'intention « extension trop longue » |
| C4 | idem | Oracles contradictoires : `"example.txt."` → `".txt"` mais `"example.docx."` → `""` | Oracle `""` (un point final veut dire pas d'extension) |
| C5 | `FilenameUtils_getSanitizedEmbeddedFileName_3_0` | API mal comprise : le nom est lu dans les **métadonnées** (`RESOURCE_NAME_KEY`), et le 2e paramètre est l'extension par défaut. Résultat : `null` partout | Le helper place le chemin dans les métadonnées |
| C6 | idem | 4 oracles faux une fois l'API bien appelée : il n'y a pas de troncature sous `maxLength`, une extension absente est déduite du `Content-Type`, un chemin finissant par `\` donne `null` | Oracles alignés sur le code source |
| C7 | `FilenameUtils_calculateExtension_10_1` | `Metadata` **mockée** : le stub `get("Content-Type")` ne suffit pas, d'où `.bin` au lieu de `.png` | Vrai objet `Metadata` et appel direct |
| C9 | `EndianUtils_getLongLE_28_1` | `12 34 … F0` en little-endian donne `0xF0DEBC9A78563412`, mais le modèle a inversé les **quartets** (`0xFEDCBA9876543212`) | Oracle corrigé |
| C10 | `EndianUtils_getUIntLE_24_2` | Lit 8 octets au lieu de 4 ; offset hors tableau ; valeur sans rapport | Données et oracles corrigés, en gardant l'intention de chaque cas |
| C11 | les 24 fichiers | Imports `*` interdits par le checkstyle de Tika (96 erreurs, le build échoue) | Imports explicites, imports Mockito inutiles retirés |

Le détail est dans [`docs-tache2/corrections-FilenameUtils.md`](docs-tache2/corrections-FilenameUtils.md) et [`docs-tache2/corrections-EndianUtils.md`](docs-tache2/corrections-EndianUtils.md). Les fichiers d'origine sont gardés dans [`docs-tache2/originaux-chatunitest/`](docs-tache2/originaux-chatunitest/).

### Comparaison qualitative des oracles : IA contre tests écrits à la main

| Critère | Tests générés | Tests originaux de Tika (`EndianUtilsTest`, `FilenameUtilsTest`) |
|---|---|---|
| **Pertinence** | Bonne sur les fonctions de calcul pur (`getIntBE`, `getUShortLE`…) : valeur exacte attendue. Faible sur les méthodes à contexte (`Metadata`) : le modèle devine l'API au lieu de la lire. | Ciblent les cas réels de Tika : noms d'attachements Outlook, chemins Windows/UNC, protocoles, *zip slip*. |
| **Spécificité** | Une méthode par fichier, sans partager de cas entre méthodes. `getSuffixFromPath` met 11 cas dans un seul test (*assertion roulette* : le premier échec cache les autres). | Tests groupés par scénario, avec des messages clairs. |
| **Vérifications triviales** | Plusieurs : `withEmptyPath` / `withNullPath` attendaient `null` et passaient « par accident » (avant C5, *rien* n'était lu). `getSanitizedEmbeddedFilePath_4_2` **n'appelle même pas la méthode et n'a aucune assertion** : il passe toujours. | Aucune vérification vide. |
| **Robustesse** | Réflexion inutile sur des méthodes publiques (3 fichiers), Mockito importé partout et mal utilisé une fois. | Appels directs, vrais objets. |
| **Justesse** | 11 tests sur 46 échouaient tels quels : erreurs de calcul binaire (quartets contre octets), suppositions « plausibles » (tronquer un nom « long », rejeter `.docx`) qui contredisent la javadoc. | Oracles exacts, souvent tirés de bugs réels. |

En résumé, l'IA écrit vite des tests **qui ressemblent** à de bons tests. Mais un humain doit vérifier chaque oracle contre la spécification : un test vert n'est pas forcément un test utile.

## 4. Analyse de mutation

pitest 1.30.0 et `pitest-junit5-plugin` 1.2.3 ont été ajoutés à `tika-core/pom.xml`. Les rapports XML des quatre exécutions sont dans [`docs-tache2/pitest/`](docs-tache2/pitest/).

| Classe | Originaux | Générés **seuls** | Originaux + générés | + manuels |
|---|---|---|---|---|
| `EndianUtils` | 38/206 = 18,4 % | 67/207 = 32,4 % | 105/207 = 50,7 % | **183/207 = 88,4 %** |
| `FilenameUtils` | 73/115 = 63,5 % | 36/115 = 31,3 % | 74/115 = 64,3 % | **102/115 = 88,7 %** |

(206 contre 207 mutants pour `EndianUtils` : pitest a généré un mutant de plus lors des exécutions suivantes. L'écart n'affecte pas les conclusions.)

### Les tests générés détectent-ils tous les mutants ? Non.

**`EndianUtils` : +67 mutants tués par les tests générés.**

| Type de mutant tué | Nombre | Pourquoi il est détecté |
|---|---|---|
| `Math` (`+`→`-`, `<<`→`>>`, `&`→`\|`…) | 40 | Les tests comparent la **valeur exacte** reconstruite à partir d'octets choisis (ex. `{0x12, 0x34, 0x56, 0x78}`). Changer un seul opérateur déplace ou altère au moins un octet, et la valeur ne correspond plus. |
| `PrimitiveReturns` (retourne 0) | 17 | Les valeurs attendues sont non nulles. |
| `Increments` (`i++`→`i--`) | 7 | Lire l'octet précédent au lieu du suivant change la valeur, sauf pour le dernier `i++` (équivalent, section 6). |
| `NegateConditionals`, `ConditionalsBoundary` | 3 | Boucle de `getLongLE` et test de fin de flux de `readUShortBE`. |

Non détectés : tous les mutants des méthodes `readXxx(InputStream)` (le modèle n'a pas produit de test qui compile, à cause des exceptions vérifiées `IOException` / `BufferUnderrunException`) et ceux des surcharges sans offset.

**`FilenameUtils` : +1 mutant seulement.**
Seuls, les tests générés tuent 36 mutants (21 `NegateConditionals`, 10 `EmptyObjectReturnVals`…), mais **35 d'entre eux sont déjà tués par les tests originaux**. Les tests générés suivent les chemins évidents (nom simple, `null`, chaîne vide) que Tika testait déjà. Le seul nouveau mutant tué est dans `resolveWithin` (retour remplacé par `null`). Les mutants vivants (ordre des clés de métadonnées, bornes de troncature) demandent des données *construites pour* les distinguer, ce que le modèle ne fait pas.

### Windows contre Linux : une branche qui dépend du système

Les quatre exécutions ci-dessus ont été faites sous Windows. Dans la CI (Linux), `FilenameUtils` n'a pas le même résultat, à cause de `getPrefixLength` (lignes 318-326). Cette méthode appelle d'abord `commons-io`, puis a un repli qui renvoie 2 pour un chemin de deux caractères comme `"C:"`.

- **Sous Windows**, `commons-io` 2.22 renvoie déjà 2 pour `"C:"` : le repli n'est jamais exécuté et ses mutants sont `NO_COVERAGE`.
- **Sous Linux**, `commons-io` renvoie **0** pour un chemin de deux caractères, parce que `"C:"` est un nom de fichier valide sous Unix (`FileSystem.getCurrent().supportsDriveLetter()` vaut `false`, vérifié dans le bytecode). Le repli de Tika sert justement à garder le même résultat sur tous les systèmes, et le test original `testEmbeddedFilePaths` l'exécute.

Résultat final dans la CI, avec tous les tests : **292 mutants tués sur 321 (91 %)**, dont 183/207 pour `EndianUtils` et 109/114 pour `FilenameUtils`. Deux mutants de borne du repli (`>= 'A'` et `<= 'Z'`) survivaient sous Linux. Ils sont tués par le test manuel `driveLettersAtBothEndsOfTheAlphabetAreStrippedOnEverySystem` (section 5).

## 5. Tests ajoutés à la main

Deux classes : [`EndianUtilsTache2Test`](tika-core/src/test/java/org/apache/tika/io/EndianUtilsTache2Test.java) (9 tests) et [`FilenameUtilsTache2Test`](tika-core/src/test/java/org/apache/tika/io/FilenameUtilsTache2Test.java) (14 tests). Sous Windows, elles tuent **78 mutants** dans `EndianUtils` et **28** dans `FilenameUtils` que ni les tests originaux ni les tests générés ne tuaient. Sous Linux, le test des lettres de lecteur en tue 2 de plus dans `FilenameUtils`.

### `EndianUtilsTache2Test`

| Test | Intention | Données | Oracle |
|---|---|---|---|
| `readLongLittleAndBigEndianPlaceEachByteAtItsOwnPosition` | `readLongLE` / `readLongBE` mettent chaque octet à la bonne position | `01 02 03 04 05 06 07 88` : 8 octets **distincts**, donc un octet mal placé change la valeur. Le dernier a son bit de poids fort à 1 pour vérifier le passage en `long` sans propagation de signe. | Calculé à la main : LE = octets lus à l'envers (`0x8807060504030201`), BE = dans l'ordre (`0x0102030405060788`) |
| `readIntFamilyUsesTheRightByteOrder` | Ordre des octets de `readIntLE/BE/ME` et `readUIntLE/BE` | `01 02 03 84` : octets distincts, dernier octet signé négatif | LE `0x84030201`, BE `0x01020384`, ME (« middle-endian » : ordre 2-1-4-3 selon le code) `0x02018403`. Pour les versions `UInt`, mêmes valeurs mais positives dans un `long`. |
| `readShortFamilyDistinguishesSignedFromUnsigned` | `readUShort*` reste non signé, `readShortLE` est signé | `01 80` : bit de signe à 1 sur l'octet de poids fort en LE | `0x8001` non signé, `(short) 0x8001` négatif signé, `0x0180` en BE |
| `readsOfOnlyZeroBytesAreValidAndDoNotThrow` | Un octet nul est une donnée valide, pas une fin de flux | Flux de zéros de la bonne longueur | Résultat 0 et aucune exception. Ce test tue les mutants `< 0` → `<= 0` du test de fin de flux. |
| `streamThatEndsOneByteTooEarlyIsAnUnderrun` | Il manque un seul octet, en dernière position | `0x41` répété (longueur requise − 1) | `BufferUnderrunException` : seul le **dernier** `read()` renvoie -1, donc remplacer le dernier `\|` par `&` le masquerait |
| `readUE7AccumulatesSevenBitGroupsAndAcceptsAZeroTerminator` | Décodage d'un entier à longueur variable par groupes de 7 bits | `81 00` : un groupe « continue » de valeur 1, puis un terminateur **nul** | `(1 << 7) + 0 = 128`. L'octet nul tue les mutants `>= 0` → `> 0` et `i < 0` → `i <= 0`. |
| `readUE7StopsAfterSixContinuationBytes` | La lecture est bornée à 6 octets | 7 octets `0x81` (tous « continue ») | 6 groupes de valeur 1 (le 7e octet est lu mais ignoré). Ce test tue `read++ < max` → `<=` et l'incrément inversé. |
| `readUE7ThrowsWhenTheStreamEndsBeforeTheLastGroup` | Flux tronqué au milieu d'un nombre | `81` puis fin de flux | `IOException`, comme dans le code (`Buffer underun`) |
| `overloadsWithoutOffsetReadFromTheStartOfTheArray` | Les surcharges sans offset lisent à partir de 0, et `getUIntBE` n'avait aucun test | `FF FE FD FC` : octets distincts, tous avec le bit de signe | Valeurs calculées à la main. Le masque `& 0xFFFFFFFF` garantit un résultat positif (le mutant `&`→`\|` donnerait -1). |

### `FilenameUtilsTache2Test`

Les méthodes `getSanitizedEmbeddedFileName` / `getSanitizedEmbeddedFilePath` sont publiques. Elles lisent le nom dans les métadonnées, selon un **ordre de priorité** codé dans `getEmbeddedName` / `getEmbeddedPath` (privées). Ces deux méthodes sont testées à travers l'API publique.

| Test | Intention | Données | Oracle |
|---|---|---|---|
| `fileNameFallsBackOnEachMetadataKeyWhenItIsTheOnlyOneSet` | Chaque clé de repli est bien lue | Une seule clé renseignée à la fois : `INTERNAL_PATH`, `EMBEDDED_RELATIONSHIP_ID`, `EMBEDDED_RESOURCE_PATH`, `ORIGINAL_RESOURCE_NAME` | Le dernier segment du chemin. Pour `rId7`, qui n'a pas d'extension, l'extension par défaut s'ajoute (`rId7.bin`). |
| `fileNamePrefersTheResourceNameOverTheOtherKeys` | Priorité de `RESOURCE_NAME_KEY` | Trois clés renseignées avec trois noms différents | `first.txt` : un mutant qui inverse une condition choisirait une autre clé |
| `filePathFallsBackOnEachMetadataKeyWhenItIsTheOnlyOneSet` | Idem pour les chemins | Une clé à la fois | Le chemin relatif nettoyé (`a/b.txt`, `c.txt`, `rId9.bin`, `o.docx`) |
| `filePathPrefersTheEmbeddedResourcePathOverTheOtherKeys` | Priorité de `EMBEDDED_RESOURCE_PATH` pour les chemins (ordre différent de celui des noms) | Trois clés renseignées | `dir/sub/file.pdf`, sans le `/` initial (le code le retire) |
| `fileNameIsTruncatedOnlyWhenTheNamePartIsStrictlyLongerThanMaxLength` | Borne de troncature : `>` et non `>=` | Nom de **exactement** 10 caractères, puis de 11, avec `maxLength = 10` | 10 : inchangé. 11 : `substring(0, 10 − 4 − 3) + "..." + ".txt"` = `abc....txt` |
| `filePathKeepsTheDirectoryOnlyWhileTheWholePathFitsInMaxLength` | Borne sur la longueur du chemin complet | `dir/name.txt` (12 car.) avec `maxLength` 12 puis 11 | 12 : chemin complet. 11 : le nom seul (`name.txt`) |
| `filePathTruncatesTheNameOnlyWhenItIsStrictlyLongerThanMaxLength` | Deuxième borne : la longueur du nom seul | Nom de 8 puis 9 caractères, `maxLength` 8 | `abcdefgh.txt` puis `a....txt` (même formule) |
| `blankNamesGiveNullRatherThanAnEmptyName` | Un nom vide est refusé (`null`, pas `""`) | `"   .txt"`, `dir/..`, `dir/.txt` | `null` : la javadoc et le code renvoient `null` plutôt qu'un nom inutilisable |
| `driveLettersAtBothEndsOfTheAlphabetAreStrippedOnEverySystem` | Le repli de `getPrefixLength` reconnaît toutes les lettres de lecteur, bornes comprises (utile sous Linux, section 4) | Chemins `A:` et `Z:` : la première et la dernière lettre, pour tuer les mutants `>= 'A'` → `> 'A'` et `<= 'Z'` → `< 'Z'`. On passe par le **chemin** : pour un nom, le mutant est équivalent (`A:` devient `A/`, dont le dernier segment est vide). | `null`, comme pour `C:` dans les tests originaux. Avec le mutant, le préfixe n'est pas retiré et le chemin devient `A.bin`. |
| `suffixOfSixCharactersIncludingTheDotIsRejected` | Borne de longueur d'extension (`< 6`) | `.abcd` (5 car.) puis `.abcde` (6 car.) | Acceptée puis rejetée, selon la javadoc (« 5 or less ») |
| `unknownContentTypeFallsBackToBin` | Type MIME inconnu | `application/x-ift3913-inconnu` | `.bin`, la valeur codée en dur (et non l'extension par défaut passée en paramètre) |
| `resolveWithinAcceptsAFileThatExistsInsideTheDirectory` | Branche « le fichier existe » (vérification des liens symboliques) | Fichier créé dans un `@TempDir` | Le chemin normalisé du fichier |
| `resolveWithinAcceptsAFileThatDoesNotExistYet` | Un fichier à créer est accepté (cas normal d'extraction) | Nom absent du répertoire | Le chemin résolu, sans exception (`toRealPath` ne doit pas être appelé) |
| `resolveWithinRejectsASymlinkThatEscapesTheDirectory` | Défense contre une sortie du répertoire par lien symbolique | Lien `dir/link.txt` → `../secret.txt` | `IOException`. Le test est ignoré (`assumeTrue`) si le système ne permet pas de créer des liens. |

## 6. Mutants restants : tous équivalents ou inatteignables

**`EndianUtils` (24) :**
- `getIntBE` / `getIntLE`, « incrément 1 → -1 » : c'est le `i++` du **dernier** `data[i++]`. `i` n'est plus lu ensuite, donc le mutant est équivalent.
- `readXxx`, « `|` → `&` » dans `(ch1 | ch2 | …) < 0` (22) : d'après le contrat d'`InputStream`, une fois `read()` à -1, il renvoie toujours -1. Les -1 forment donc un **suffixe**, et un `&` entre deux octets valides ne peut pas masquer le -1 final. Seul le mutant sur le dernier `|` est observable ; il est tué.

**`FilenameUtils` (13 sous Windows, 5 sous Linux) :**
- `getPrefixLength` lignes 320–324 (9, **sous Windows seulement**) : `commons-io` y renvoie déjà 2 pour `"C:"`, donc la branche de repli n'est pas atteinte (section 4). Sous Linux, cette branche est exécutée et tous ses mutants sont tués.
- `getSanitizedEmbeddedFileName` l.156 et `getSanitizedEmbeddedFilePath` l.215 (3) : le préfixe n'est jamais négatif et `substring(0)` est l'identité. Ne pas retirer `C:\` ne change rien non plus, puisque `:` et `\` deviennent ensuite des `/` et que seul le dernier segment est gardé.
- `getSanitizedEmbeddedFileName` l.185 et `getSanitizedEmbeddedFilePath` l.259 (2) : vérifications « défense en profondeur » (commentaire du code). Un nom vide est déjà intercepté plus haut, et les remplacements intermédiaires ne peuvent pas rendre vide une chaîne non vide.

## 7. Exécution dans GitHub Actions

Le workflow [`.github/workflows/tache2.yml`](.github/workflows/tache2.yml) s'exécute à chaque push. Il fait les étapes suivantes :
1. il construit `tika-core` (Java 21) ;
2. il lance **toute** la suite de `tika-core`, soit 818 tests : 749 originaux, 46 générés et 23 manuels. Checkstyle, forbiddenapis et RAT sont activés, comme dans le build officiel de Tika ;
3. il lance pitest sur les deux classes et publie le rapport HTML en artefact (`rapport-pitest`).

## 8. Utilisation de l'IA générative

Conformément aux consignes de l'Université de Montréal (*Citer, signaler, déclarer et documenter*) :

- **ChatUniTest 2.1.1 avec `qwen2.5-coder:7b` (Ollama, local)** : génération des tests de la section 3, comme demandé par l'énoncé.
- Chaque correction, oracle et mutant équivalent a été vérifié en exécutant les tests et en relisant le code source de Tika. Nous restons responsables du contenu.
