# AppEvent API Dokumentasjon

## Innholdsfortegnelse

- [AppEvent API Dokumentasjon](#appevent-api-dokumentasjon)
  - [Innholdsfortegnelse](#innholdsfortegnelse)
  - [Introduksjon](#introduksjon)
  - [Autentisering API](#autentisering-api)
    - [Innlogging](#innlogging)
    - [Registrering](#registrering)
  - [Aktivitets API](#aktivitets-api)
    - [Hente kommende aktiviteter](#hente-kommende-aktiviteter)
    - [Registrere deltakelse](#registrere-deltakelse)
    - [Legge til ny aktivitet](#legge-til-ny-aktivitet)
    - [Hente mine aktiviteter](#hente-mine-aktiviteter)
  - [Datamodeller](#datamodeller)
    - [AuthDTO](#authdto)
    - [AuthDTORegistration](#authdtoregistration)
    - [ActivityDTO](#activitydto)
    - [UserDTO](#userdto)
  - [Feilhåndtering](#feilhåndtering)

## Introduksjon

Dette dokumentet beskriver REST API-et for AppEvent-applikasjonen. API-et tilbyr endepunkter for brukerautentisering og aktivitetshåndtering. Alle endepunkter returnerer feilmeldinger som ren tekst for enkel visning i klientapplikasjonen.

## Autentisering API

### Innlogging

Autentiserer en bruker ved å verifisere brukernavn og passord.

**Endepunkt:** `/api/auth/login`

**Metode:** POST

**Headers:**

- Content-Type: application/json

**Request Body:**

```json
{
  "username": "bruker123",
  "password": "sikkertpassord"
}
```

**Respons:**

- **200 OK**: Innlogging vellykket. Returnerer JWT token.

"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

- **401 UNAUTHORIZED**: Ugyldig brukernavn eller passord

"Ugyldig brukernavn eller passord"

- **500 INTERNAL SERVER ERROR**: Serverfeil

"En feil oppstod under innlogging"

### Registrering

Registrerer en ny brukerkonto.

**Endepunkt:** `/api/auth/register`

**Metode:** POST

**Headers:**

- Content-Type: application/json

**Request Body:**

```json
{
  "username": "nybruker123",
  "password": "sikkertpassord",
  "confirmPassword": "sikkertpassord"
}
```

**Respons:**

- **201 CREATED**: Registrering vellykket. Returnerer JWT token.

"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

- **400 BAD REQUEST**: Ugyldig forespørsel

"Passordene matcher ikke"

- **500 INTERNAL SERVER ERROR**: Serverfeil
  
"En feil oppstod under registrering"

## Aktivitets API

### Hente kommende aktiviteter

Henter en liste over alle kommende aktiviteter.

**Endepunkt:** `/api/activity/upcoming`

**Metode:** GET

**Headers:**

- Authorization: Bearer JWT-token

**Respons:**

- **200 OK**: Aktiviteter hentet

```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "title": "Gruppetrening",
    "dateTime": "2024-02-15T18:00:00",
    "location": "Treningssenter",
    "description": "Ukentlig gruppetrening",
    "participants": [
      {
        "id": "987fcdeb-51d2-4af1-a2ec-87592f6f0000",
        "brukernavn": "bruker123"
      }
    ]
  }
]
```

### Registrere deltakelse

Bytter påmeldingsstatus for en bruker på en aktivitet.

**Endepunkt:** `/api/activity/toggleParticipation`

**Metode:** POST

**Headers:**

- Content-Type: application/json
- Authorization: Bearer JWT-token

**Request Body:**

"123e4567-e89b-12d3-a456-426614174000"

**Respons:**

- **200 OK**: Deltakelse oppdatert
- **404 NOT FOUND**: Aktivitet ikke funnet
- **500 INTERNAL SERVER ERROR**: Serverfeil

"Det oppstod en feil ved registrering av deltakelse"

### Legge til ny aktivitet

Oppretter en ny aktivitet.

**Endepunkt:** `/api/activity/add`

**Metode:** POST

**Headers:**

- Content-Type: application/json
- Authorization: Bearer JWT-token

**Request Body:**

```json
{
  "title": "Morgenyoga",
  "dateTime": "2024-02-20T08:00:00",
  "location": "Yogastudio",
  "description": "Nybegynnervennlig morgenyoga",
  "participants": []
}
```

**Respons:**

- **201 CREATED**: Aktivitet opprettet
- **400 BAD REQUEST**: Ugyldig aktivitetsdata
- **401 UNAUTHORIZED**: Ugyldig autentisering

### Hente mine aktiviteter

Henter en liste over aktiviteter brukeren deltar på.

**Endepunkt:** `/api/activity/myactivities`

**Metode:** GET

**Headers:**

- Authorization: Bearer JWT-token

**Respons:**

- **200 OK**: Aktiviteter hentet

```json

[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "title": "Gruppetrening",
    "dateTime": "2024-02-15T18:00:00",
    "location": "Treningssenter",
    "description": "Ukentlig gruppetrening",
    "participants": [
      {
        "id": "987fcdeb-51d2-4af1-a2ec-87592f6f0000",
        "brukernavn": "bruker123"
      }
    ]
  }
]
```

## Datamodeller

### AuthDTO

```json
{
  "username": "string",
  "password": "string"
}
```

### AuthDTORegistration

```json
{
  "username": "string",
  "password": "string",
  "confirmPassword": "string"
}
```

### ActivityDTO

```json
{
  "id": "UUID",
  "title": "string",
  "dateTime": "string (ISO-8601)",
  "location": "string",
  "description": "string",
  "participants": [
    {
      "id": "UUID",
      "brukernavn": "string"
    }
  ]
}
```

### UserDTO

```json
{
  "id": "UUID",
  "brukernavn": "string"
}
```

## Feilhåndtering

API-et bruker følgende HTTP-statuskoder:

- **400 BAD REQUEST**: Ugyldig formatering eller validering feilet
- **401 UNAUTHORIZED**: Autentisering påkrevd eller ugyldig token
- **404 NOT FOUND**: Ressurs ikke funnet
- **500 INTERNAL SERVER ERROR**: Serverfeil

Feilmeldinger returneres som ren tekst for enkel visning i klientapplikasjonen. Vanlige feil inkluderer:

- Innloggingsfeil ved ugyldige legitimasjoner
- Registreringsfeil ved ikke-matchende passord
- Manglende eller ugyldig JWT-token
- Aktivitet ikke funnet ved påmelding/avmelding
