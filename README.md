# Prosjektbeskrivelse: AppEvent

**AppEvent** er en app som fokuserer på å tilby lavterskel arrangementer og fremme sosial interaksjon. Det er en plattform som skal spre informasjon om offentlige og private arrangementer. Det skal være en dynamisk og brukervennlig app slik at det er lett å opprette, planlegge og delta på arrangementer.

## Hovedfunksjoner (Iterasjon 1)

Noen av hovedfunksjonene til appen utvikles i iterasjon 1. Funksjonene som utvikles her inkluderer:

- Opprettelse av brukerkonto for tilgang til nettstedet.
- Arrangører kan opprette et arrangement med informasjon om sted og tid, slik at brukere kan få oversikt over tilgjengelige arrangementer på en ryddig måte.
- Funksjoner for å sikre et trygt miljø, inkludert muligheten til å fjerne og manipulere data.
- Påmelding og avmelding av arrangementer, slik at brukere enkelt kan engasjere seg i interessante arrangementer eller melde seg av ved behov (f.eks. sykdom).

## Målgruppe

Vår app er designet for personer som ønsker å delta på arrangementer. Det er derfor ingen spesifikk aldersgruppe som er særlig egnet for appen. Brukerbasen avhenger av de tilgjengelige arrangementene til enhver tid.

## Verktøy og Rammer

For å utvikle AppEvent benytter vi følgende teknologier og rammeverk:

- **Java 17**: Brukes til server-siden av appen for databehandling og brukerforespørsler.
- **Spring Boot 3.2.0**: Forenkler kjøringen av applikasjonen og kobler sammen serverkomponentene.
- **Maven**: Hjelper utviklere med å bygge og administrere prosjektet, og sørger for håndtering av avhengigheter og biblioteker.
- **PostgreSQL**: Database-systemet for lagring og henting av informasjon.
- **React**: Brukes til å utvikle brukergrensesnittet (frontend) av appen.

## Oppsett og Installasjon

### Oppstart av Server

1. Åpne terminalen på din pc.
2. Naviger til rotmappen med kommandoen `cd appevent`.
3. Kjør `mvn clean install` for å installere avhengigheter og bygge prosjektet.
4. Gå til servermappen med `cd server`.
5. Start serveren med `mvn spring-boot:run`.

### Oppstart av Frontend

1. Åpne en ny terminal.
2. Naviger til frontend-mappen.
3. Sikre at alle avhengigheter er installert med `npm install --force`.
4. Start React-serveren fra npn run dev i folderen "appevent-frontend"


### Features

- Kan opprette et event
- Kan melde seg på arrangementer 
- Kan søke på arrangementer
- Kan opprette en profil
- Kan legge til venner og invitere dem til arrangementer
- Som admin kan du slette arragementer
- Kan legge inn kommentarer på arragementer
- Har oversikt over arrangementer man er påmeldt og arrangementer man har opprettet på profilsiden
