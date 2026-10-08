# LipariBank Day 05 — Spring Security: Architettura di Base e Filtri

Benvenuto nel progetto **liparibank-day05-broken**.

Applicazione Spring Boot 3.3.4 (Java 21) con configurazione completa di Spring Security:
`SecurityFilterChain`, `UserDetailsService` custom, `BCryptPasswordEncoder`, `AccessDeniedHandler`
e `AuthenticationEntryPoint` con risposte JSON strutturate.

Il progetto contiene **3 bug intenzionali** da trovare e correggere.

---

## Prerequisiti

- Java 21
- Maven (o usa il wrapper incluso)

---

## Come avviare il progetto

```bash
./mvnw spring-boot:run
```

Su Windows:

```cmd
mvnw.cmd spring-boot:run
```

---

## Credenziali di test

| Username   | Password      | Ruolo          |
|------------|---------------|----------------|
| `admin`    | `admin123`    | `ROLE_ADMIN`   |
| `customer` | `customer123` | `ROLE_CUSTOMER`|

---

## Le 3 Missioni

### Missione 1 — Tutto è bloccato, anche gli endpoint pubblici

L'applicazione parte senza errori. Spring Security è configurato con una `SecurityFilterChain`
dichiarata correttamente. Tuttavia, qualsiasi richiesta — incluso `POST /api/v1/auth/login`,
`GET /swagger-ui.html` e `GET /actuator/health` — restituisce `401 Unauthorized`.
Nessun utente riesce ad autenticarsi perché nemmeno l'endpoint di login è raggiungibile.

**Sintomo:** tutti gli endpoint restituiscono `401 Unauthorized`. Gli endpoint dichiarati
come pubblici non sono effettivamente accessibili senza autenticazione.

---

### Missione 2 — CORS error dal frontend

Il frontend React su `localhost:3000` non riesce a chiamare nessun endpoint del backend
su `localhost:8080`. Il browser segnala un errore CORS su ogni richiesta, incluse le
preflight `OPTIONS`. Aprendo gli strumenti del browser si vede che le richieste `OPTIONS`
ricevono `401 Unauthorized` invece degli header `Access-Control-Allow-*` attesi.
Il bean `CorsConfigurationSource` è dichiarato nella configurazione.

**Sintomo:** `CORS error` nel browser su tutte le richieste cross-origin. Le preflight
`OPTIONS` non ricevono mai risposta con gli header CORS corretti.

---

### Missione 3 — L'admin non riesce a cancellare i conti

Un utente `admin` (credenziali: admin/admin123) effettua l'autenticazione con Basic Auth.
Il server riconosce l'utente — il `SecurityContext` viene popolato. Tenta quindi di chiamare
`DELETE /api/v1/accounts/1` — operazione protetta da `@PreAuthorize("hasRole('ADMIN')")`.
Riceve `403 Forbidden`. I log di Spring Security mostrano che il contesto di sicurezza
è popolato correttamente con un'`Authentication`, ma l'autorizzazione fallisce.

**Sintomo:** utente `admin` autenticato via Basic Auth riceve `403 Forbidden` sul metodo annotato
con `@PreAuthorize("hasRole('ADMIN')")`, nonostante il ruolo nel database sia `ROLE_ADMIN`.

**Come testare:** in Postman, apri la tab "Authorization", seleziona "Basic Auth",
inserisci `admin` / `admin123`. Oppure con curl: `curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/accounts/1`

---

## Come testare (dopo aver risolto le missioni)

```bash
# Missione 1 — testa l'endpoint di login (deve rispondere, non dare 401)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Crea conti di test (richiede autenticazione)
curl -X POST http://localhost:8080/api/v1/accounts/seed \
  -u admin:admin123

# Lista conti
curl http://localhost:8080/api/v1/accounts \
  -u admin:admin123

# Missione 3 — testa la cancellazione con utente admin (deve funzionare)
curl -X DELETE http://localhost:8080/api/v1/accounts/1 \
  -u admin:admin123

# Missione 3 — testa la cancellazione con utente customer (deve dare 403)
curl -X DELETE http://localhost:8080/api/v1/accounts/1 \
  -u customer:customer123

# Missione 2 — simula una preflight CORS
curl -X OPTIONS http://localhost:8080/api/v1/accounts \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v
```

H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
(JDBC URL: `jdbc:h2:mem:liparibankdb`)

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Struttura del progetto

```
src/main/java/com/lipari/bank/
├── LipariBankApplication.java
├── auth/
│   ├── model/BankUser.java
│   ├── model/Role.java
│   ├── BankUserRepository.java
│   ├── LipariBankUserDetailsService.java
│   └── AuthController.java
├── account/
│   ├── Account.java
│   ├── AccountRepository.java
│   ├── AccountService.java
│   └── AccountController.java
└── shared/
    ├── config/
    │   ├── SecurityConfig.java
    │   ├── OpenApiConfig.java
    │   └── DataInitializer.java
    └── security/
        ├── LipariBankAccessDeniedHandler.java
        └── LipariBankAuthenticationEntryPoint.java
```

---

## Bonus Mission — Feature da Implementare (opzionale, ~1 ora)

Una volta risolti i 3 bug, implementa la seguente feature per consolidare i concetti del giorno.

### Cambio password per l'utente autenticato

Il sistema permette il login ma non offre agli utenti la possibilità di cambiare la propria password una volta autenticati.

**Cosa implementare:**

Aggiungi un endpoint `POST /api/v1/auth/change-password` accessibile solo agli utenti autenticati. Il body della richiesta deve contenere due campi: `currentPassword` (la password attuale) e `newPassword` (la nuova password desiderata).

La logica deve:
1. Recuperare l'utente attualmente autenticato dal contesto di sicurezza.
2. Verificare che `currentPassword` corrisponda alla password salvata nel database (confronto corretto con BCrypt — non in chiaro).
3. Validare che `newPassword` abbia almeno 8 caratteri.
4. Codificare la nuova password con BCrypt e salvarla.

Se `currentPassword` non corrisponde, rispondere con `400 Bad Request`. Nessun utente — nemmeno `admin` — può cambiare la password di un altro utente tramite questo endpoint: l'operazione agisce sempre e solo sull'utente che ha effettuato la richiesta.

**Criteri di accettazione:**

- Un utente autenticato può cambiare la propria password con successo (risposta `200 OK`).
- Dopo il cambio, il login con la vecchia password restituisce `401`. Con la nuova password restituisce `200`.
- Se `currentPassword` è errata, la risposta è `400 Bad Request` e la password non cambia.
- Se `newPassword` ha meno di 8 caratteri, la risposta è `400 Bad Request`.
- L'endpoint non è accessibile senza autenticazione (deve rispondere `401` se chiamato senza credenziali).

---

*LipariBank Prompt Bootcamp — Spring Security: Architettura di Base e Filtri — Day 05*
