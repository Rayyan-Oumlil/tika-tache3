# Notes brutes : scores de mutation (pitest 1.30.0, mutateurs par défaut)

| Classe | Tests originaux | Tests générés seuls | Originaux + générés |
|---|---|---|---|
| EndianUtils | 38/206 = **18,4 %** (154 sans couverture, 14 survivants) | 67/207 = 32,4 % | 105/207 = **50,7 %** (84 sans couverture, 18 survivants) |
| FilenameUtils | 73/115 = **63,5 %** (22 sans couverture, 20 survivants) | 36/115 = 31,3 % | 74/115 = **64,3 %** (22 sans couverture, 19 survivants) |

(206 vs 207 mutants pour EndianUtils : écart d'une exécution à l'autre de pitest, à mentionner.)

Constats :
- EndianUtils : les tests générés tuent surtout des mutants **MathMutator / IncrementsMutator** dans `getIntBE/LE`, `getUShortBE/LE`, `getLongLE` : ils vérifient la valeur exacte reconstruite à partir des octets, donc tout changement d'opérateur (`<<` vs `>>`, `+` vs `-`, `&`) donne une autre valeur.
- Les méthodes `readXxx(InputStream)` restent presque toutes **sans couverture** : ChatUniTest n'a jamais réussi à produire un test qui compile pour 11 d'entre elles.
- FilenameUtils : les tests générés tuent 36 mutants à eux seuls, mais **presque tous sont déjà tués par les tests originaux** : +1 mutant seulement. Ils sont redondants avec la suite existante.

Rapports XML : `pitest/pit-baseline`, `pitest/pit-generated`, `pitest/pit-generated-only`.
