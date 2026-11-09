# Mapa REST API

Domyślnie `/api/**` wymaga zalogowania (dowolna rola). W kolumnie „Ograniczenia” są tylko dodatkowe wymagania.
Zakres danych (siebie / przypisani / grupa / wszyscy) liczy `UserUtils` — patrz DOMAIN.md §4.

| Resource | Ścieżka | Endpointy | Ograniczenia |
|---|---|---|---|
| `UserJWTController` | `/api/authenticate` | `POST` | publiczny |
| `AccountResource` | `/api` | `/register`, `/activate`, `/account`, `/account/change-password`, `/account/reset-password/init\|finish` | rejestracja, aktywacja i reset publiczne |
| `UserResource` | `/api/users` | `GET` (`?active`, `?related`, `?withPhone`), `GET /{login}`, `GET /all` | — |
| | | `POST`, `PUT`, `DELETE /{login}`, `GET /groups`, `/authorities`, `/relatedUsers/{id}` | ADMIN, SYSTEM |
| `IntegrationResource` | `/api` | `POST /updateUserDatabase`, `GET /integration/candidates`, `GET\|POST /activation/resend(/{userId})` | ADMIN, SYSTEM |
| `UserGroupResource` | `/api/userGroup` | CRUD (`CRUDResource`) | — |
| `VacationResource` | `/api/vacation` | `GET`, `GET /{id}`, `POST` (złożenie), `DELETE /{id}` (tylko WAITING), `GET /types` | — |
| `VacationAdminResource` | `/api/admin/vacation` | `GET`, `GET\|DELETE /{id}`, `POST\|PUT` (akceptacja/odrzucenie), `/user-approval…`, `/approve/definition` | ADMIN, SYSTEM, HR (na klasie) |
| `VacationHistoryResource` | `/api/vacation/history` | `GET`, `/details`, `/details/{externalId}` | `/details/{externalId}`: ADMIN, SYSTEM, HR |
| `DashboardResource` | `/api/dashboard` | `/calculate`, `/vacation`, `/vacation/{externalId}` | `/vacation/{externalId}`: ADMIN, SYSTEM, HR |
| `AbsencesResource` | `/api/absences` | `GET`, `/dictionary/relatedUsers` | widoczność w kodzie (HR — wszyscy) |
| `GraphicResource` | `/api/graphic` | `GET`, `?all`, `?groups` | `?all`, `?groups`: HR |
| `NotificationResource` | `/api/notifications` | CRUD, `?all`, `?groups`, `/users`, `/user(/{id})`, `/count` | `?all`, `?groups`: HR |
| `NotificationReadResource` | `/api/notification-reads` | CRUD, `/count`, `/notification/{id}` | — |
| `PayrollResource` | `/api/payroll` | `GET`, `/details`, `/additionalDescription`, `/ingredients`, `/breaks`, `/deductions` | — |
| `DocumentsPayrollResource` | `/api/payroll/documents` | `/{fileType}/exists/{year}`, `/{fileType}/{year}`, `POST /{id}/confirm` | — |
| `DocumentsInfoResource` | `/api/documents` | `GET`, `/types` | — |
| `PersonDataResource` | `/api` | `/medical-examination`, `/safety-training`, `/courses`, `/privileges` | — |
| `ManagementResource` | `/api/management` | `GET\|PUT /active-place-vacation-proposal`, `GET /can-place-vacation-proposal` | — |
| `QualificationsResource`, `InvoicesResource` | `/api/qualifications`, `/api/invoices` | CRUD | — |
| `AuditResource` | `/management/audits` | `GET` | ADMIN, SYSTEM (`/management/**`) |

Swagger UI jest dostępny w profilu `swagger` (dołączany przez `dev`): Administracja → API.
