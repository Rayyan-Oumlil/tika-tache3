# Notes brutes : génération ChatUniTest sur `org.apache.tika.io.FilenameUtils`

Modèle : `qwen2.5-coder:7b` via Ollama (alias `codeqwen:v1.5-chat`), `testNumber=3`, `maxRounds=3`, `temperature=0.2`.
Log complet : `tika-core/target/chatunitest-FilenameUtils.log`. Les fichiers tels que générés sont dans `originaux-chatunitest/`.

## Ce que rapporte ChatUniTest (par méthode)

| Méthode | Tentatives | Échecs de compilation (rondes de réparation) | Statut ChatUniTest |
|---|---|---|---|
| normalize | 1 | 0 | « compile and execute successfully », ronde 0 |
| getName | 1 | 0 | succès, ronde 0 |
| getSuffixFromPath | 1 | 0 | « succès », ronde 0, alors que **0 test réussi / 1 échoué** dans son propre rapport |
| getSanitizedEmbeddedFileName | 1 | 2 | « succès », ronde 2 (4 réussis / 5 échoués) |
| getSanitizedEmbeddedFilePath | 3 | 6 | aucune version validée ; la dernière est gardée (« Processed test ... generated successfully ») |
| resolveWithin | 2 | 3 | idem |
| calculateExtension | 2 | 5 | « succès », ronde 2 (1 réussi / 2 échoués) |

Constat : pour ChatUniTest, « succès » veut dire *compile et s'exécute*, **pas** *les assertions passent*.

## Exécution réelle dans le projet (avant toute correction)

- `FilenameUtils_Suite.java` : **ne compile pas** (runner JUnit 4 `org.junit.platform.runner` / `org.junit.runner`, absents de JUnit 6 utilisé par Tika).
- Les 7 classes de test : 27 tests, **18 réussis, 9 échoués**. Les 9 échecs sont tous des oracles faux, pas des plantages.

## Corrections manuelles nécessaires (7 au total, sur 4 fichiers + 1 supprimé)

| # | Fichier | Problème | Correction |
|---|---|---|---|
| C1 | `FilenameUtils_Suite.java` | Runner JUnit 4, ne compile pas avec JUnit 6 | Supprimé (Surefire découvre les classes sans suite) |
| C2 | `normalize_0_0` | Appel d'une méthode **publique** par réflexion : l'`IllegalArgumentException` attendue arrive enveloppée dans une `InvocationTargetException` | Appel direct `FilenameUtils.normalize(null)` |
| C3 | `getSuffixFromPath_2_0` | Le LLM croit que `.docx` est une extension invalide ; le code accepte jusqu'à 5 caractères point compris (`n.length() - i < 6`) | Entrées `*.docx` remplacées par `*.backup` (7 car.) pour garder l'intention « extension invalide » (3 assertions) |
| C4 | `getSuffixFromPath_2_0` | Oracles contradictoires : `"example.txt."` → `".txt"` mais `"example.docx."` → `""` | Oracle corrigé à `""` (point final = pas d'extension) |
| C5 | `getSanitizedEmbeddedFileName_3_0` | Mauvaise compréhension de l'API : le nom de fichier est lu dans les **métadonnées** (`RESOURCE_NAME_KEY`), le 2e paramètre est l'extension par défaut. Le LLM passait le chemin en 2e paramètre → `null` partout | Helper réécrit : chemin placé dans `RESOURCE_NAME_KEY`, extension par défaut `.bin` |
| C6 | `getSanitizedEmbeddedFileName_3_0` | 4 oracles faux une fois l'API bien appelée : `name.jpg` n'est pas tronqué (4 car. < maxLength 10) ; nom sans extension → extension déduite du Content-Type (`path.pdf`, `file.pdf`) ; chemin finissant par `\` → `null` | Oracles alignés sur le comportement réel, vérifié dans le code source |
| C7 | `calculateExtension_10_1` | `Metadata` **mockée** + réflexion : le stub `get("Content-Type")` ne suffit pas, on obtient `.bin` au lieu de `.png` | Vrai objet `Metadata` + appel direct |

Résultat après corrections : **27 tests, 27 réussis.**

## Observations pour la critique des oracles

- **Réflexion inutile** sur des méthodes publiques (3 fichiers) : tests fragiles et moins lisibles.
- **Mockito importé partout** mais utilisé une seule fois, et à tort (C7).
- **Oracles triviaux qui passent « par accident »** : dans `getSanitizedEmbeddedFileName`, `withEmptyPath` et `withNullPath` attendaient `null`. Avant C5 ils passaient parce que *rien* n'était lu ; ils ne vérifiaient donc pas ce que leur nom annonce.
- **Assertion roulette** : `getSuffixFromPath` regroupe 11 cas dans un seul test ; le premier échec masque les suivants.
- Les oracles reflètent souvent une **supposition plausible** (tronquer un nom « long », rejeter `.docx`) plutôt que la spécification réelle (javadoc et code).

## Correction de style commune (C11, les deux classes)

Les 24 fichiers générés utilisaient des imports `*` (`org.junit.jupiter.api.*`, `org.mockito.*`, ...), interdits par le checkstyle de Tika (96 erreurs, le build échoue). Imports remplacés par des imports explicites, et imports Mockito inutilisés retirés. Aucun changement de logique.

## Test sans oracle

`FilenameUtils_getSanitizedEmbeddedFilePath_4_2_Test.testGetSanitizedEmbeddedFilePathWithNullMaxLength` prépare des données (`metadata`, `defaultExtension`, `maxLength = null`) puis **n'appelle même pas la méthode testée** et ne contient aucune assertion. Il passe toujours. Il est gardé tel quel pour illustrer le problème : un test « vert » généré automatiquement peut ne rien vérifier du tout.
