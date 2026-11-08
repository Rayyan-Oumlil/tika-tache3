# Notes brutes : génération ChatUniTest sur `org.apache.tika.io.EndianUtils`

Même configuration que pour FilenameUtils. Log : `chatunitest-EndianUtils.log`.

## Ce que rapporte ChatUniTest

- 23 méthodes traitées (la classe en a 32 publiques ; les surcharges sans `offset` ne sont pas ciblées séparément).
- `getXxx(byte[], offset)` et `ubyteToInt` / `getUByte` : tests validés en ronde 0 dans la majorité des cas.
- `readXxx(InputStream)` : **11 méthodes sur 12 sans aucun test qui compile** après 3 tentatives × 3 rondes de réparation (readShortLE, readUShortLE, readUShortBE, readUIntLE, readUIntBE, readIntLE, readIntBE, readIntME, readLongLE, readLongBE, readUE7). Seul `readShortBE` a réussi.
- `getUIntBE` : 6 tentatives, 16 échecs de compilation, aucun test.

Fichiers produits : 17 classes de test + 1 suite.

## Exécution réelle dans le projet (avant correction)

- Suite : même problème qu'avec FilenameUtils (runner JUnit 4) → non copiée (C8).
- 19 tests, **17 réussis, 2 échoués**.

## Corrections manuelles

| # | Fichier | Problème | Correction |
|---|---|---|---|
| C8 | `EndianUtils_Suite.java` | Runner JUnit 4 | Non utilisé |
| C9 | `getLongLE_28_1` | Octets `12 34 56 78 9A BC DE F0` lus en little-endian donnent `0xF0DEBC9A78563412`. Le LLM attendait `0xFEDCBA9876543212` : il a inversé les **quartets** (nibbles) au lieu des octets | Oracle corrigé |
| C10 | `getUIntLE_24_2` | Les 3 cas sont faux : le LLM lit 8 octets au lieu de 4 (cas 1 : le `0x01` est à l'index 7, hors de la fenêtre lue) ; cas 2 : offset 6 + 4 octets dépasse un tableau de 8 ; cas 3 : valeur attendue sans rapport (`1296893664` au lieu de `0x04030201`) | Données et oracles corrigés en gardant l'intention de chaque cas (valeur 1, valeur max non signée, ordre des octets) |

Résultat après corrections : **19 tests, 19 réussis.**

## Observations

- Bons oracles quand la méthode est une pure fonction de calcul : valeur exacte attendue, pas de vérification triviale.
- Les erreurs restantes sont des **erreurs de calcul binaire** du modèle (ordre des octets / quartets), exactement le genre de détail qu'un humain vérifierait à la main.
- Les méthodes sur `InputStream` (exceptions vérifiées `IOException`, `BufferUnderrunException`) dépassent ce que le modèle 7B réussit à faire compiler.
