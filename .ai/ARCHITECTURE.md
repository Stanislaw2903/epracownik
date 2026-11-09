# Architektura i konwencje kodu

Monolit JHipster 6.10.3: Spring Boot 2.2.7 (Java 8) + Angular 10, jeden artefakt (jar/war).
Pakiet bazowy `com.eurosoftit.ebok`, nazwa techniczna `ebok`, nazwa produktu „e_Pracownik”.

## Backend — układ pakietów

| Pakiet | Zawartość |
|---|---|
| `config/` | konfiguracja Spring/JHipster (`SecurityConfiguration`, Jackson, cache Ehcache, Liquibase) |
| `security/` | JWT, `DomainUserDetailsService`, `AuthoritiesConstants`, `SecurityUtils` |
| `domain/` | encje z generatora (User, Notification, …), enumy, `specification/` (adnotacje wyszukiwania) |
| `repository/`, `service/` | warstwy z generatora JHipster (User, Notification, Qualifications, Mail, Audit) |
| **`web/rest/<feature>/`** | **funkcjonalności biznesowe — encja, repozytorium, DTO, params, serwis i resource razem** |
| `common/controller/` | generyczny CRUD/wyszukiwanie (niżej) |
| `common/ftp/` | dokumenty płacowe z FTP lub dysku |
| `notification/` | zadania cykliczne (urodziny, dzienny mail o absencjach) |

Funkcjonalności w `web/rest/`: `absence`, `dashboard`, `files`, `graphic`, `group`, `integration`,
`management`, `payroll`, `persondata`, `vacation` + `UserUtils` (widoczność, patrz DOMAIN.md §4).
Nowa funkcjonalność biznesowa trafia jako nowy pakiet `web/rest/<feature>/`, a nie do `service/`.

## Generyczny CRUD (`common/controller`)

- `IEntity<ID>`, `DTO<T>` — kontrakty encji i DTO.
- `ContentConverter<E,ID,D>` — **ręczny** mapper `<Feature>Converter` (MapStruct tylko punktowo:
  `AbsencesMapper`, `NotificationMapper`, `UserMapper`).
- `Service<K,E,D>` (CRUD) + `SearchService<K,E,P>` (wyszukiwanie z paginacją).
- `SearchResource` / `SaveResource` / `UpdateResource` / `DeleteResource` — interfejsy z metodami
  domyślnymi; razem `CRUDResource<K,E,D,P>`.
- `service/AbstractSearchService` — bazowa klasa serwisu z `JpaSpecificationExecutor`.

Wzorzec referencyjny: `web/rest/group/` (`UserGroupEntity`, `UserGroupRepository`,
`UserGroupDTO`, `UserGroupParams`, `UserGroupConverter`, `UserGroupService`, `UserGroupResource`).

## Wyszukiwanie przez adnotacje (`domain/specification`)

Klasa parametrów (`*Params` / `*Properties` / `*Criteria`) to POJO z adnotacjami, a
`SpecificationReflectionBuilder.process(params)` buduje z niej `Specification<T>`:

- `@Equal` (kolekcja → `IN`), `@Like` (lower-case), `@DateFrom`, `@DateTo`, `@DateEquals`
- `@DateToAndFromInRange` (na klasie) — nakładanie się przedziałów `dateFrom`/`dateTo`
- `field = "createdBy.login"` — ścieżka z automatycznym JOIN

Przykład: `web/rest/vacation/VacationProperties`. Resource'y z regułami widoczności
(`AbsencesResource`, `GraphicResource`, `NotificationResource`) łączą specyfikację z filtrem z `UserUtils`.

## Frontend (`src/main/webapp/app`)

Angular 10, ng-bootstrap + Bootstrap 4, **PrimeNG 11** (nowsze ekrany: tabele, kalendarz, dialogi),
moment.js, ngx-translate, pdfmake, apexcharts/chart.js.

| Trasa | Moduł | Ekran |
|---|---|---|
| `/dashboard` | `home/` | pulpit pracownika (saldo urlopu, wykresy, wnioski); `user-vacation` — moje wnioski |
| `/vacations` | `entities/vacations` | akceptacja wniosków urlopowych |
| `/absences` | `entities/absences` | historia absencji |
| `/payroll` | `entities/employee-income-cards` | listy płac / karty wynagrodzeń |
| `/control-read-files` | `entities/control-read-files` | kontrola odczytu dokumentów płacowych |
| `/personal-data/{medical,courses,qualifications,safetyTraining}` | `entities/personal` | dane kadrowe (tylko odczyt) |
| `/notification`, `/notification-read` | `entities/notification*` | powiadomienia |
| `/invoices`, `/qualifications` | `entities/…` | encje z generatora |
| `/admin/graphic` | `admin/graphic` | grafik absencji |
| `/admin/user-management`, `/admin/user-groups` | `admin/…` | użytkownicy, grupy |
| `/admin/{audits,configuration,docs,health,logs,metrics}` | `admin/…` | standard JHipster |

- Encje ładowane są pod pustym prefiksem (`/vacations`, nie `/entity/vacations`) — `entities/entity.module.ts`.
- `/admin/**` wymaga `ROLE_ADMIN` (`app-routing.module.ts`); trasy deklarują `data.authorities`,
  a guard to `UserRouteAccessService`.
- Dwa różne „qualifications”: `entities/qualifications` (CRUD, `api/qualifications`) oraz
  `entities/personal/qualifications` (uprawnienia pracownika, `api/privileges`).
- Menu: `layouts/sidebar/sidebar.component.html`; widoczność przez `*jhiHasAnyAuthority`.
- Słowniki etykiet: `shared/dictionaries/*`; modele: `shared/model/*`; komunikaty: `shared/toast` (własny) i `shared/alert`.

### Nowy ekran — checklista

1. Model w `shared/model/<feature>.model.ts`.
2. Serwis z `resourceUrl = SERVER_API_URL + 'api/<endpoint>'` i `createRequestOption()`.
3. Komponent(y), `<feature>.route.ts` (`authorities`, `pageTitle`, `canActivate: [UserRouteAccessService]`).
4. Moduł z `RouterModule.forChild`, importem `EbokSharedModule`, lazy w `entity.module.ts` lub `admin-routing.module.ts`.
5. **i18n w trzech językach**: `src/main/webapp/i18n/{pl,en,de}/<feature>.json` (klucze `ebokApp.<feature>.*`)
   + pozycja menu `global.menu.*` w `global.json`. Uwaga: `incomes.json` istnieje tylko w `pl` — to dług, nie wzorzec.
6. Pozycja w sidebarze.

## Testy

- Backend `src/test/java`: JUnit, `*IT` (Spring context), `ArchTest` (ArchUnit — reguły warstw).
- Frontend `src/test/javascript/spec` (Jest). Wiele nowszych modułów nie ma testów
  (vacations, absences, graphic, personal, employee-income-cards).
