# Bezpieczeństwo: JWT, role, uprawnienia

## Logowanie (backend)

1. `POST /api/authenticate` — `web/rest/UserJWTController.java`, body `LoginVM {username, password, rememberMe}`.
   Login jest normalizowany (usuwa polskie diakrytyki, lowercase — metoda `normalize`).
2. `DomainUserDetailsService` szuka użytkownika **po e-mailu** (gdy login jest poprawnym e-mailem)
   albo **po loginie**; nieaktywne konto → `UserNotActivatedException`. Hasła: BCrypt.
3. `TokenProvider.createToken` zwraca JWT w nagłówku `Authorization: Bearer …` i w body `{"id_token": "…"}`.

## Token (`security/jwt/TokenProvider.java`)

- Podpis HS512, sekret z `jhipster.security.authentication.jwt.base64-secret` (albo `…jwt.secret`).
- Claim `auth` — role rozdzielone przecinkami; `sub` = login.
- Ważność: 24 h; z „zapamiętaj mnie” 30 dni.
- `JWTFilter` czyta **tylko** nagłówek `Authorization: Bearer` (bez tokenu w query param).

## `SecurityConfiguration`

- Stateless, CSRF wyłączony (JWT), `httpBasic()` **również włączony** (odstępstwo od szablonu JHipster).
- `permitAll`: `/api/authenticate`, `/api/register`, `/api/activate`,
  `/api/account/reset-password/init|finish`, `/management/health|info|prometheus`.
- `/api/**` → zalogowany; `/management/**` → `ROLE_ADMIN` lub `ROLE_SYSTEM`.
- `@EnableGlobalMethodSecurity(prePostEnabled, securedEnabled)` — szczegółowe uprawnienia
  na metodach (`@PreAuthorize`) i w logice serwisów.
- CORS otwarty tylko w profilu dev.

## Role (`security/AuthoritiesConstants.java`)

| Rola | Znaczenie |
|---|---|
| `ROLE_USER` | pracownik |
| `ROLE_ADMIN` | administrator (moduł `/admin` we froncie) |
| `ROLE_HR` | kadry — widzi wszystkich pracowników (grafik, historia absencji, powiadomienia) |
| `ROLE_SYSTEM` | konto techniczne (`/management/**`) |

Front ma dodatkowo `ROLE_SUPER_ADMIN` (`shared/constants/authority.constants.ts`).
Biznesowe zasady widoczności (przełożeni, grupy, `authorizationToAcceptVacation`) — [DOMAIN.md](DOMAIN.md).

## Frontend

- `core/auth/auth-jwt.service.ts` — `POST api/authenticate`, token pod kluczem `authenticationToken`
  w `localStorage` (remember me) lub `sessionStorage`. Po zalogowaniu pobiera
  `GET api/admin/vacation/approve/definition` → `sessionStorage.vacationApproveLevel`.
- `blocks/interceptor/auth.interceptor.ts` — dokleja `Authorization: Bearer` do żądań na `SERVER_API_URL`.
- `blocks/interceptor/auth-expired.interceptor.ts` — 401 → zapamiętaj URL, wyloguj, modal logowania.
- `core/auth/user-route-access-service.ts` — guard tras wg `data.authorities`; brak roli → `/accessdenied`.
- `core/auth/csrf.service.ts` — nieużywany.

## Konto: rejestracja, aktywacja, reset (`web/rest/AccountResource.java`)

`POST /api/register`, `GET /api/activate?key=`, `POST /api/account/reset-password/init`
(nie zdradza, czy e-mail istnieje), `POST /api/account/reset-password/finish`.
Import użytkowników z systemu kadrowego: `web/rest/integration/` (`UserCreator`, `IntegrationResource`).

## Znane ryzyka — nie powielać, nie wklejać wartości

- Sekret JWT jest w repo (`.yo-rc.json`, `application-dev.yml`, `application-prod.yml`) i jest
  wspólny dla dev i prod. Na produkcji powinien przychodzić ze zmiennej
  `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET`.
- Hasła SMTP i bazy produkcyjnej są jawnie w `application-*.yml`.
- CSP dopuszcza `'unsafe-inline' 'unsafe-eval'`.

Nowy kod nie może dokładać sekretów do repo — używaj zmiennych środowiskowych.
