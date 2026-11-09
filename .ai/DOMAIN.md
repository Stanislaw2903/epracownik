# Model domenowy e_Pracownik

Portal pracowniczy (self-service) nad systemem kadrowo-płacowym (Superpłace): urlopy i absencje,
grafik, listy płac, dokumenty płacowe (PIT-11, IMIR), dane kadrowe, powiadomienia, użytkownicy i grupy.

**Dwa źródła danych w jednej bazie PostgreSQL:**

1. **Tabele aplikacji** — zakładane przez Liquibase, aplikacja jest ich właścicielem.
2. **Tabele systemu kadrowo-płacowego** — NIE ma ich w Liquibase, muszą już istnieć w bazie.
   Aplikacja czyta je głównie przez encje `@Subselect` (często `@Immutable`), a do `osoba_prz` **zapisuje**.

Łącznik między światami: `jhi_user.external_id` = `osoba.oso_serial` (identyfikator osoby w kadrach).

## 1. Tabele aplikacji (Liquibase)

| Encja | Tabela | Sens biznesowy |
|---|---|---|
| `domain/User` | `jhi_user` | konto; `externalId` → osoba w kadrach; `manualInsert` (dodany ręcznie vs import); pola akceptacji urlopów (niżej) |
| — | `related_user` | przełożony ↔ przypisani pracownicy (`User.relatedUsers`) |
| — | `related_group`, `USER_GROUP` | przynależność do grup (`web/rest/group/UserGroupEntity`) |
| `domain/Authority` | `jhi_authority`, `jhi_user_authority` | role |
| `web/rest/vacation/Vacation` | `vacation` | wniosek urlopowy w aplikacji: daty, `fullDay`, `status`, `vacationType`, `used`, `erpId` (id rekordu w `osoba_prz` po akceptacji), `createdBy`/`processedBy` |
| `domain/Notification` | `notification` (+`_recepient`, `_group`) | powiadomienie: temat/treść, `category`, `canal`, wysyłka mail/SMS |
| `domain/NotificationRead` | `notification_read` (+`_user`) | potwierdzenia odczytu |
| `common/ftp/service/FileUploadInfoEntity` | `file_upload_info` | metadane dokumentów płacowych: `viewed`, `confirmed`, `year`, `type` |
| `web/rest/management/ManagementEntity` | `management` | ustawienia klucz–wartość (`ManagementType`) |
| `notification/birthday/NotificationInfoEntity` | `NOTIFICATION_INFO` | log wysłanych powiadomień cyklicznych (`sent` → ponowienia) |
| `notification/birthday/NotificationTemplateEntity` | `NOTIFICATION_TEMPLATE` | szablon treści per `NotificationType` |
| `domain/Invoices`, `domain/Qualifications` | `invoices`, `qualifications` | encje z generatora JHipster (`.jhipster/*.json`) |
| `domain/PersistentAuditEvent` | `jhi_persistent_audit_event` | audyt logowań |

## 2. Tabele kadrowo-płacowe (poza Liquibase)

`osoba` (pracownik, PESEL, data urodzenia, zwolnienie), `osoba_prz` (absencje/urlopy w kadrach),
`absencja` (słownik nieobecności), `okres` (kalendarz dni roboczych i świąt), `dane_hi_da`, `dane_stat`,
płace: `PLACE`, `LISTY`, `TYTUL`, `PLACE_SKL`, `PLACE_ZLEC`, `PLACE_PRZ`, `WYN_KOD_SKL`, `WYN_KOD_PRZ`, `WYN_POTRACENIA`.

| Encja | Źródło | Do czego |
|---|---|---|
| `domain/UserView` | `jhi_user JOIN osoba` | data urodzenia → job urodzinowy |
| `domain/RelatedUserView` | `jhi_user LEFT JOIN related_user` | użytkownik z przełożonym |
| `web/rest/absence/Absence` | `absencja JOIN osoba_prz JOIN jhi_user` | historia absencji (`/api/absences`) |
| `web/rest/vacation/VacationExternal` | `osoba_prz WHERE osp_abs_serial = 5` | urlopy wypoczynkowe zapisane w kadrach |
| `web/rest/dashboard/AbsencesView` | agregat `osoba_prz` | wykorzystany urlop per rok |
| `web/rest/dashboard/VestedVacationView` | `dane_stat` | urlop przysługujący per rok |
| `web/rest/graphic/GraphicAbsenceView` | `osoba_prz` (wybrane kody) | grafik absencji |
| `web/rest/dashboard/RangesEntity` | `@Table okres` | dni robocze/święta w grafiku |
| `web/rest/integration/PersonNotInSystem` | `osoba` bez konta w `jhi_user` | kandydaci do importu użytkowników |
| `web/rest/payroll/Payroll*`, `Breaks`, `Deductions`, `Ingredients` | `PLACE*`, `LISTY`, `WYN_*` | listy płac, składniki, potrącenia (natywne SQL w `@Subselect`) |
| `web/rest/persondata/DataHiDa` | `@Table dane_hi_da` | dane kadrowe (badania, szkolenia, kursy, uprawnienia) |
| `web/rest/persondata/PersonPrz` | `@Table osoba_prz`, sekwencja `osoba_prz_osp_serial_seq` | **zapis** urlopu do kadr |

SQL w `@Subselect` jest pisany pod PostgreSQL (`date_trunc`, `extract`).

### Zapis do kadr — najważniejszy punkt integracji

`web/rest/vacation/service/VacationOneLevelApproveErpServiceImpl`: po akceptacji wniosku
(`ANNUAL_LEAVE` / `LEAVE_ON_REQUEST`) wstawia rekord do `osoba_prz` i zapamiętuje go w `vacation.erpId`.
Przy wycofaniu wniosku kasuje ten rekord. Każda zmiana w tym miejscu zmienia dane systemu kadrowego.

## 3. Wnioski urlopowe i akceptacja

- `VacationStatus`: `WAITING → (ACCEPTED_INITIALLY) → ACCEPTED | REJECTED | CANCELLED`.
  Usunąć można tylko wniosek `WAITING`.
- `VacationType` — 27 rodzajów (`ANNUAL_LEAVE`, `LEAVE_ON_REQUEST`, `UNPAID_LEAVE`, `ILLNESS`, …).
- Tryb globalny `vacation.approve` (`ApproveLevelType` `ONE_LEVEL`/`TWO_LEVEL`) wybiera przez
  `@ConditionalOnProperty` implementację `VacationErpService`
  (`VacationTwoLevelApproveErpServiceImpl` dziedziczy po wersji jednopoziomowej).
- Na użytkowniku: `approval_verification` (`ApprovalVerificationType` — czy jego wnioski wymagają
  dwóch poziomów) i `approval_type` (`ApprovalType` `INITIAL_VERIFICATION` / `FINAL_VERIFICATION` —
  na którym poziomie akceptuje).
- W trybie dwupoziomowym akceptujący wstępnie widzi `WAITING`, a końcowy tylko `ACCEPTED_INITIALLY`
  (`VacationAdminResource.getApprovedUserIds`).
- Saldo urlopu: `web/rest/dashboard/DashboardResource` + `VacationBalance` (przysługujący z
  `VestedVacationView`, wykorzystany z `AbsencesView`).

## 4. Widoczność „kogo widzę” — `web/rest/UserUtils`

Sterowana polem `jhi_user.authorization_to_accept_vacation` (`AuthorizationToAcceptVacationType`):

| Wartość | Widzi |
|---|---|
| `ALL` | wszystkich użytkowników |
| `SPECIFIC_GROUP` | przypisanych ręcznie w `related_user` („moi przypisani”) |
| `USER_GROUP` | członków swoich grup (`USER_GROUP`) |
| `NO_ONE` | nikogo (sentinel `-1`) |

Do tego `ROLE_HR` odblokowuje tryby „wszyscy” i „wg grup” (`?all`, `?groups`) w grafiku,
powiadomieniach, historii i na dashboardzie. Z tej logiki korzystają: `VacationResource`,
`VacationAdminResource`, `AbsencesResource`, `GraphicResource`, `NotificationResource`.
Nowy ekran z danymi wielu osób **musi** filtrować przez `UserUtils`, a nie samodzielnie.

## 5. Pozostałe enumy

| Enum | Wartości | Sens |
|---|---|---|
| `NotificationCanal` | `ALL, GROUPS, USERS` | adresaci powiadomienia |
| `NotificationCategory` | `MESSAGE, SYSTEM, ABSENT, HR, VACATION` | kategoria (VACATION — automatyczne przy zmianie statusu wniosku) |
| `AbsenceType` (`web/rest/graphic`) | 25 kodów; `serial` = `osp_abs_serial`, kategoria A–D | skróty w grafiku; priorytet nakładania C > A > B > D (`GraphicResource.categoryPriority`) |
| `GraphicDayType` | `WORKING, HOLIDAY, WEEKEND, ABSENCE` | typ dnia w grafiku |
| `ManagementType` | `ACTIVE_PLACE_VACATION_PROPOSAL` | czy pracownik może składać wnioski |
| `FileType` | `PIT_11, IMIR` | dokumenty płacowe; wzorzec nazwy pliku `buildPrefix(year, lastName, firstName)` |
| `NotificationType` / `NotificationSource` | `BIRTH_SMS` / `SMS, EMAIL` | powiadomienia cykliczne |

## 6. Zadania cykliczne

| Klasa | Harmonogram | Włącznik | Co robi |
|---|---|---|---|
| `notification/absence/DailyAbsenceMailJob` | `app.job.absence-mail.cron` (pn–pt 8:30) | `app.job.absence-mail.enabled` (domyślnie false) | mail z listą dzisiejszych nieobecności do `recipients` |
| `notification/birthday/NotificationBirthDayJob` | `app.job.birth.value` (9:00) | `app.job.birth.enabled` | życzenia urodzinowe SMS/mail wg szablonu |
| `notification/birthday/NotificationBackupJob` | `app.job.birth.backup` (co 2 h, 10–20) | j.w. | ponowna wysyłka niewysłanych (`NOTIFICATION_INFO.sent=false`) |
| `service/AuditEventService` | 12:00 | zawsze | czyści stary audyt |
| `service/UserService` | 1:00 | zawsze | usuwa nieaktywowane konta starsze niż 3 dni |

## 7. Dokumenty płacowe (`common/ftp`)

`FTPClientConfiguration`: gdy `ftp.host` jest ustawiony, używa `FTPClientImpl` (FTP/FTPS/SFTP przez
dekoratory z `common/ftp/impl`, budowane w `FTPFactory`), a w przeciwnym razie `FTPLocalDiskImpl` (katalog `ftp.dir`).
`FTPService` szuka pliku po wzorcu nazwy, zapisuje `file_upload_info` i obsługuje viewed/confirmed.
REST: `DocumentsPayrollResource` (pracownik), `DocumentsInfoResource` (kontrola odczytu).

## 8. Liquibase

- `config/liquibase/master.xml` — chronologiczne `<include>`; nowe pliki `changelog/<yyyyMMddHHmmss>_<opis>.xml`.
- Nowsze changesety stosują `preConditions onFail="CONTINUE"` (`tableExists` / `columnExists`) —
  bazy klientów mogą być w różnym stanie. Trzymaj się tego wzorca.
- Kontekst `faker` (tylko dev) ładuje CSV z `config/liquibase/fake-data`. Bazowe konta JHipster
  (`data/user.csv`) ładują się zawsze.
- Nigdy nie dodawaj do Liquibase DDL tabel kadrowo-płacowych.
