# e_Pracownik (ebok)

Portal pracowniczy nad systemem kadrowo-płacowym (Superpłace): wnioski urlopowe z akceptacją
jedno- lub dwupoziomową, grafik absencji, historia absencji, listy płac, dokumenty płacowe (PIT-11, IMIR),
dane kadrowe, powiadomienia (mail/SMS), import użytkowników z kadr.

Stos: monolit **JHipster 6.10.3** — Spring Boot 2.2.7 (Java 8), Angular 10 + PrimeNG,
PostgreSQL, Liquibase, JWT. Pakiet `com.eurosoftit.ebok`. UI w językach pl/en/de.

## Najważniejsze zasady

- **Baza ma dwa światy.** Tabele aplikacji są w Liquibase. Tabele kadrowo-płacowe (`osoba`, `osoba_prz`,
  `absencja`, `okres`, `dane_stat`, `PLACE*`, …) są zewnętrzne: czytamy je przez encje `@Subselect`
  i nigdy nie dodajemy ich do Liquibase. Akceptacja urlopu **zapisuje** do `osoba_prz` —
  ostrożnie z `VacationOneLevelApproveErpServiceImpl`.
- **Widoczność danych innych osób** liczy wyłącznie `web/rest/UserUtils`
  (`authorization_to_accept_vacation` + `ROLE_HR`). Nie pisz własnych filtrów.
- **Nowa funkcjonalność** = pakiet `web/rest/<feature>/` (encja, repo, DTO, `*Params`, converter, serwis, resource),
  z generycznym `common/controller` i wyszukiwaniem przez adnotacje `domain/specification`.
- **Nowe teksty UI** — zawsze w trzech plikach `i18n/{pl,en,de}`.
- **Liquibase** — nowy changeset w `config/liquibase/changelog/`, wpis w `master.xml`,
  z `preConditions onFail="CONTINUE"`.
- **Sekrety** — w repo są już jawne sekrety (JWT, SMTP, baza prod). Nie dokładaj nowych
  i nie przepisuj ich wartości do dokumentacji ani komentarzy.
- **Build** wymaga prywatnej zależności `com.iss:API:0.3` (bramka SMS/mail). Zaślepek nie commitujemy.

## Szybki start

```bash
./mvnw -Dspring-boot.run.arguments=--server.port=8080   # backend (profil dev; baza localhost:5433/firma)
npm install && npm start                                 # frontend: http://localhost:9000
./mvnw verify   /   npm test                             # testy
```

Bez bazy z danymi kadrowo-płacowymi aplikacja nie zadziała. Dump jest w
`src/main/resources/db/firma_pg.sql` (odtwarzanie przez `pg_restore`) — szczegóły w RUNNING.

## Dokumentacja szczegółowa

@.ai/RUNNING.md
@.ai/ARCHITECTURE.md
@.ai/DOMAIN.md
@.ai/SECURITY.md
@.ai/API.md
