# IFT3913 — Tâche 3 : tests de sécurité automatisés (Semgrep) sur Apache Tika

| Nom complet | Identifiant GitHub |
|---|---|
| Oumlil Rayyan | Rayyan-Oumlil |
| Khettal Amine | aminekh04 |

Dépôt : https://github.com/Rayyan-Oumlil/tika-tache3 (Apache Tika, état du fork de la tâche 2 + modifications de la tâche 3).

| Livrable | Où le voir |
|---|---|
| Workflow Semgrep bloquant | [`.github/workflows/main-jdk17-build.yml` (job `semgrep`)](../.github/workflows/main-jdk17-build.yml) |
| Vert sans vulnérabilité | push sur `main`, job `semgrep` du run [42512945880](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42512945880) |
| Rouge avec 7 vulnérabilités | PR [#24](https://github.com/Rayyan-Oumlil/tika-tache3/pull/24), run [42515880138](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42515880138) |
| Rickroll | PR [#25](https://github.com/Rayyan-Oumlil/tika-tache3/pull/25), run [42515884832](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42515884832) |

Les PR #24 et #25 restent **ouvertes volontairement** : on ne les fusionne pas, sinon `main` contiendrait des vulnérabilités et un test en échec.

---

## 1. Le workflow GitHub Actions (Semgrep)

### Comportement

Le build échoue si un commit introduit une **nouvelle** vulnérabilité détectée par Semgrep. Les trouvailles qui existaient déjà dans Tika ne bloquent pas.

### Choix de conception

| Choix | Pourquoi |
|---|---|
| `semgrep scan --baseline-commit <sha>` | Ne rapporte que les trouvailles absentes du commit de référence. Sans ça, les 3 trouvailles déjà présentes dans Tika feraient échouer tous les builds, et le critère « nouvelle vulnérabilité » ne serait pas respecté. |
| Commit de référence = `base.sha` de la PR (ou `event.before` pour un push) | C'est exactement « avant le commit / la PR ». Cas particuliers : premier push d'une branche (sha nul) ou lancement manuel, on compare au parent `HEAD~1`. |
| `--error` | Fait sortir Semgrep avec le code 1 quand il y a une trouvaille, ce qui rend le job rouge. |
| `--severity ERROR --severity WARNING` | On ignore le niveau INFO, trop bruyant pour bloquer un build. |
| Règles `p/java` + `p/security-audit` | `p/java` couvre les erreurs courantes en Java ; `p/security-audit` ajoute les motifs de sécurité (crypto, XXE, injection, désérialisation) qui servent à la comparaison avant/après. |
| Cible `tika-core/src/main` | Module de la tâche 2, et exclut le code de test, qui contient volontairement des motifs « dangereux ». |
| `fetch-depth: 0` | `--baseline-commit` a besoin de l'historique git pour créer le worktree de référence. |
| `semgrep==1.183.0` figé, `--metrics=off` | Résultats reproductibles et aucun envoi de données. |
| Artefact `rapport-semgrep` (`if: always()`) | Le rapport JSON reste téléchargeable même quand le job échoue. |

### Validation

| Cas | Attendu | Observé |
|---|---|---|
| Code sans vulnérabilité (push sur `main`) | job vert | ✅ job `semgrep` du run [42512945880](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42512945880) |
| PR avec 7 vulnérabilités ([#24](https://github.com/Rayyan-Oumlil/tika-tache3/pull/24)) | job rouge, 7 trouvailles | ❌ [42515880138](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42515880138) : `Findings: 7 (7 blocking)`, comparées à la base `42b770b` |
| PR avec un test en échec mais sans vulnérabilité ([#25](https://github.com/Rayyan-Oumlil/tika-tache3/pull/25)) | Semgrep vert | ✅ [42515884837](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42515884837) |

Avant d'utiliser le CI, la même commande a été exécutée en local avec l'image Docker `semgrep/semgrep` : 4 trouvailles sur 7 au premier essai. Les 3 manquantes venaient de noms de classes écrits en entier (`java.sql.Statement`…) au lieu d'imports : les règles Semgrep reconnaissent les types importés. Corrigé, puis revalidé.

### Limites

- Le scan ne regarde que `tika-core/src/main`. Les autres modules ne sont pas protégés.
- Semgrep est une analyse statique par motifs : il ne prouve pas qu'une vulnérabilité est exploitable.
- Si on déplace du code existant déjà signalé, Semgrep peut le compter comme nouveau.
- Le job `semgrep` est indépendant du job `build (17)` du même workflow. Ce dernier (build Maven complet d'Apache Tika) échoue dans `tika-pipes-s3-integration-tests`, un module sans lien avec la tâche 3, et échouait déjà sur le fork avant nos modifications. Le verdict à lire est celui du job `semgrep`.

---

## 2. Comparaison des rapports Semgrep (avant / après)

Mêmes règles, même cible (`tika-core/src/main`), 331 fichiers.

| Rapport | Trouvailles |
|---|---|
| Avant (commit d'import, avant toute modification) : [`semgrep-avant.json`](semgrep-avant.json) | **3** |
| Nouvelles dans la branche `tache3-vulns` : [`semgrep-nouveaux.json`](https://github.com/Rayyan-Oumlil/tika-tache3/blob/tache3-vulns/docs-tache3/semgrep-nouveaux.json) | **7** |
| Total après modifications | 10 |

**Déjà présentes dans Tika (non bloquantes)**

| Fichier | Ligne | Règle | CWE |
|---|---|---|---|
| `mime/MimeTypesReader.java` | 223 | `saxparserfactory-disallow-doctype-decl-missing` | CWE-611 |
| `parser/NetworkParser.java` | 68 | `unencrypted-socket` | CWE-319 |
| `utils/XMLReaderUtils.java` | 307 | `xmlinputfactory-possible-xxe` | CWE-611 |

Aucune de ces 3 n'apparaît dans le rapport des nouvelles trouvailles.

---

## 3. Les 7 vulnérabilités introduites

Chaque vulnérabilité est une méthode ajoutée dans un fichier existant de `tika-core`, dans la branche [`tache3-vulns`](https://github.com/Rayyan-Oumlil/tika-tache3/tree/tache3-vulns) (PR #24). Les 7 types correspondent à **7 CWE différents**.

| # | Type (CWE) | Fichier : ligne | Règle Semgrep | Sévérité |
|---|---|---|---|---|
| 1 | Hash faible, MD5 (CWE-328) | `digest/InputStreamDigester.java:125` | `use-of-md5` | WARNING |
| 2 | Aléatoire non sûr (CWE-330) | `io/TemporaryResources.java:213` | `weak-random` | WARNING |
| 3 | Désérialisation non sûre (CWE-502) | `io/TikaInputStream.java:511` | `object-deserialization` | WARNING |
| 4 | Injection SQL (CWE-89) | `utils/ParserUtils.java:114` | `formatted-sql-string` | ERROR |
| 5 | Chiffrement faible, DES/ECB (CWE-326) | `utils/StringUtils.java:229` | `des-is-deprecated` | WARNING |
| 6 | Socket serveur non chiffré (CWE-319) | `utils/SystemUtils.java:80` | `unencrypted-socket` | WARNING |
| 7 | XXE (CWE-611) | `utils/XMLReaderUtils.java:1313` | `documentbuilderfactory-disallow-doctype-decl-missing` | ERROR |

### Justification de chaque vulnérabilité

**1. MD5 — `InputStreamDigester.quickFingerprint`.** Le fichier gère déjà les empreintes (digests) ; une méthode de « signature rapide » y est crédible. MD5 est cassé : on sait fabriquer deux contenus différents de même empreinte, donc on peut faire passer un document malveillant pour un doublon d'un document sûr.

**2. `Random` — `TemporaryResources.newTempToken`.** Ce fichier crée les fichiers temporaires, où l'on veut un nom imprévisible. `java.util.Random` est un générateur déterministe : à partir de quelques sorties, on prédit les suivantes, ce qui permet de deviner des noms de fichiers temporaires (attaque par lien symbolique ou lecture). Il faudrait `SecureRandom`.

**3. Désérialisation — `TikaInputStream.readCachedResult`.** `TikaInputStream` manipule déjà des flux. `ObjectInputStream.readObject()` sur un flux non fiable permet d'instancier des classes arbitraires, et d'exécuter du code via des chaînes de « gadgets ».

**4. Injection SQL — `ParserUtils.findFailures`.** `ParserUtils` s'occupe déjà des échecs de parseurs ; une recherche de l'historique des échecs y est plausible. Le nom du parseur est concaténé dans la requête : `x' OR '1'='1` renvoie toutes les lignes. Il faudrait un `PreparedStatement`.

**5. DES/ECB — `StringUtils.obfuscate`.** DES n'a que 56 bits de clé, donc on le casse par force brute. Le mode ECB chiffre les blocs identiques de façon identique et révèle la structure des données.

**6. Socket non chiffré — `SystemUtils.openStatusPort`.** `new ServerSocket(port)` ouvre un port en clair : tout ce qui y circule peut être lu ou modifié sur le réseau. Il faudrait TLS (`SSLServerSocket`).

**7. XXE — `XMLReaderUtils.parseConfigDocument`.** `DocumentBuilderFactory.newInstance()` sans désactiver les DOCTYPE : un XML contenant `<!ENTITY x SYSTEM "file:///etc/passwd">` fait lire des fichiers locaux ou déclenche des requêtes réseau (SSRF). Le reste du fichier configure justement ces protections ; cette méthode les contourne.

**Choix de ne pas relier ces méthodes au reste du code.** Elles ne sont appelées nulle part : cela évite de changer le comportement de Tika et de casser ses tests. Vérification : `mvn test -pl tika-core` donne 818 tests, 0 échec, 2 ignorés, avec les 7 méthodes en place (mesuré avant le remplacement de la #6, qui ne change aucun appel). Limite : ces vulnérabilités sont réelles dans le code, mais non atteignables à l'exécution.

**Correction en cours de route.** La première version utilisait un contexte SSL faible (CWE-326), de même CWE que DES. Remplacé par le socket non chiffré (CWE-319) pour que les 7 types soient réellement différents.

---

## 4. Rickroll

- **Action réutilisable** : [`.github/actions/rickroll/action.yml`](../.github/actions/rickroll/action.yml), écrite par nous (pas d'action tierce). Elle affiche une annotation d'erreur « Never gonna give you up », les paroles et le lien du clip dans le résumé du job.
- **Branchement** : [`tache3-tests-rickroll.yml`](../.github/workflows/tache3-tests-rickroll.yml) lance les tests de `tika-core` ; l'étape rickroll a `if: failure()`, donc elle ne s'exécute que si une étape précédente a échoué.
- **Preuve** : sur la PR [#25](https://github.com/Rayyan-Oumlil/tika-tache3/pull/25) un test est volontairement en échec ([`RickrollDemoTest`](https://github.com/Rayyan-Oumlil/tika-tache3/blob/tache3-rickroll-demo/tika-core/src/test/java/org/apache/tika/utils/RickrollDemoTest.java)). Dans le run [42515884832](https://github.com/Rayyan-Oumlil/tika-tache3/actions/runs/42515884832), « Tests de tika-core » est en échec et « Rickroll » est en succès. Sur les PR où les tests passent, l'étape est ignorée.
- **Note** : l'énoncé parle des tests de *graphhopper* ; notre cas d'étude est Tika, donc le rickroll est branché sur les tests de Tika.

---

## 5. Usage de l'intelligence artificielle

Claude Code (Anthropic) a servi à écrire le workflow, l'action rickroll, les 7 méthodes vulnérables et ce document. Les résultats ont été relus et chaque preuve vérifiée dans les runs GitHub Actions cités plus haut. Déclaration selon les [consignes de l'Université](https://boite-outils.bib.umontreal.ca/c.php?g=743753&p=5377614).
