# Uruchamianie e_Pracownik (ebok) lokalnie

## Wymagania

| Element | Wersja / wartość | Źródło |
|---|---|---|
| JDK | bytecode Java 8 (`java.version=1.8`); kompiluje się na JDK 11 | `pom.xml` |
| Spring Boot | 2.2.7.RELEASE (JHipster 6.10.3) | `pom.xml:47` |
| Node / npm | `>=12.16.1`; frontend-maven-plugin pobiera sam v12.16.1 / npm 6.14.5 | `package.json` `engines`, `pom.xml` |
| PostgreSQL | dev: `localhost:5433`, baza `firma`, user/hasło `ebok`/`ebok` | `application-dev.yml` |
| SMTP | `smtp.jk.com.pl:465` (maile aktywacyjne, reset hasła, powiadomienia) | `application-dev.yml` |

Na Node ≥ 17 stary webpack 4 wymaga `NODE_OPTIONS=--openssl-legacy-provider`.

## Zależność `com.iss:API:0.3` — blokuje build

`pom.xml:322-326` deklaruje prywatną bibliotekę `com.iss:API:0.3` (bramka SMS/mail). `pom.xml` nie ma
żadnego `<repositories>`, więc Maven szuka jej w repozytoriach z `~/.m2/settings.xml`. Bez niej
`mvn compile` nie przejdzie. Klasy z `com.iss` importują:
`EbokApp`, `DailyAbsenceMailJob`, `AbstractNotificationJob`, `NotificationBackupJob`, `UserNotificationSender`.

Jeśli artefaktu nie ma w żadnym dostępnym repozytorium, trzeba go zainstalować ręcznie
(`mvn install:install-file ...`) albo zaślepić — **nie commituj zaślepek**.

## Baza danych — Liquibase NIE tworzy wszystkiego

Liquibase (`src/main/resources/config/liquibase/master.xml`) zakłada tylko tabele aplikacji
(użytkownicy, role, powiadomienia, grupy, wnioski urlopowe itp.). Dane kadrowo-płacowe
pochodzą z zewnętrznego systemu (Superpłace) i **muszą już być w bazie**: m.in. `osoba_prz`,
`dane_stat` oraz tabele/widoki czytane przez encje `@Subselect` / `@Immutable`.
Szczegóły: [DOMAIN.md](DOMAIN.md).

Wniosek: `src/main/docker/postgresql.yml` (postgres 12.3, baza `ebok`, port 5432, trust) **nie
pasuje** do profilu dev (5433 / `firma` / `ebok:ebok`) i nie zawiera danych Superpłac.

W repo jest dump bazy z tabelami kadrowo-płacowymi: `src/main/resources/db/firma_pg.sql`
(format custom `pg_dump`, wersja archiwum 1.14). Odtwarzanie przez `pg_restore`, a nie `psql`:

```bash
createdb -p 5433 -U ebok firma
pg_restore -p 5433 -U ebok -d firma --no-owner src/main/resources/db/firma_pg.sql
```

Profil dev uruchamia Liquibase z kontekstami `dev, faker` (dane testowe z `config/liquibase/fake-data`).

## Porty — uwaga na konflikt

| Proces | Port |
|---|---|
| Backend, profil `dev` | **9000** (`application-dev.yml:76`, zakomentowane `8080`) |
| Backend, profil `prod` | 8089 |
| webpack-dev-server (`npm start`) | 9060, proxy `/api`, `/management`… → `localhost:8080` |
| BrowserSync (`npm start`) | **9000** → proxy do 9060 |

Backend dev i BrowserSync chcą tego samego portu 9000, a proxy frontendu celuje w 8080.
Przy pracy z `npm start` uruchom backend na 8080:

```bash
./mvnw -Dspring-boot.run.arguments=--server.port=8080
```

## Komendy

```bash
# backend (profile domyślne: dev + webpack; przy pierwszym buildzie zbuduje też frontend)
./mvnw                     # Windows: mvnw.cmd
./mvnw -P-webpack          # sam backend, bez budowania frontu

# frontend z live-reload (drugi terminal)
npm install
npm start                  # http://localhost:9000 (BrowserSync)

# testy
./mvnw verify              # backend: JUnit, *IT (integracyjne), ArchUnit (ArchTest)
npm test                   # frontend: eslint + jest (src/test/javascript/jest.conf.js)

# build produkcyjny
./mvnw -Pprod clean verify          # jar → java -jar target/*.jar
./mvnw -Pprod,war clean verify      # war na zewnętrzny Tomcat (ApplicationWebXml)
```

Inne profile Mavena: `no-liquibase`, `swagger`, `tls`, `IDE`.
Profil Spring `dev` dołącza `swagger` → Swagger UI w Administracja → API.

## Konfiguracja aplikacji (poza `jhipster.*`)

Czytana przez `@Value`, nie przez `ApplicationProperties` (ta klasa jest pusta).

| Property | Domyślnie | Używa |
|---|---|---|
| `vacation.approve` | `TWO_LEVEL` (`ONE_LEVEL` jako fallback w `@Value`) | wybór `VacationOneLevel…`/`VacationTwoLevelApproveErpServiceImpl` (`@ConditionalOnProperty`) |
| `app.job.birth.enabled/value/backup` | cron `0 0 9 * * *`, backup `0 0 10-20/2 * * *` | `NotificationBirthDayJob`, `NotificationBackupJob` |
| `app.job.absence-mail.enabled/cron/recipients/admin-contact` | `enabled: false`, `0 30 8 * * MON-FRI` | `DailyAbsenceMailJob` |
| `smsapi.url` | dev `127.0.0.1:8891`, prod `127.0.0.1:8893` | wysyłka SMS/mail przez `com.iss` |
| `ftp.*` | tylko `ftp.dir` (ścieżka lokalna) | `FTPParams` — przechowywanie plików (`common/ftp`) |

`spring.datasource-eurosoft` w `application-dev.yml` nie jest czytany przez żaden kod.

## Jakość kodu

- Checkstyle (`checkstyle.xml` + nohttp) w buildzie Mavena.
- ESLint (`extends: jhipster`) z mostem do `tslint.json`; Prettier; Husky `pre-commit` → lint-staged (prettier).
- Sonar: `sonar-project.properties`, lokalny serwer `docker-compose -f src/main/docker/sonar.yml up -d`.
