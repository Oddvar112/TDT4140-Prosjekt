# Commit-meldingsstandard

For å sikre konsistens i commit-meldinger, bruker vi en standardisert mal for commit-meldinger i dette prosjektet. Denne standarden er basert på [Conventional Commits](https://www.conventionalcommits.org/), som hjelper med å opprettholde en ren commit-historie og letter automatisk versjonering.

## Format for commit-meldinger

Commit-meldinger bør følge følgende format:

type(scope): kort beskrivelse

[valgfri kropp]

[valgfri footer(e)]

### Eksempler: Commit-meldinger

feat(auth): implementer brukerinnloggingsfunksjonalitet

fix(cart): korriger prisberegning for flere varer

docs(README): legg til seksjon om commit-meldingsstandarder

## Commit-typer

- **feat**: Introduksjon av en ny funksjon.
- **fix**: Fikse en feil eller et problem.
- **docs**: Endringer i dokumentasjon.
- **style**: Endringer relatert til stil/formattering (ikke-funksjonelle endringer).
- **refactor**: Kodeendringer som verken fikser en feil eller legger til en funksjon, men forbedrer kodens struktur.
- **perf**: Ytelsesforbedringer.
- **test**: Legge til eller forbedre tester.
- **build**: Endringer som påvirker byggesystemet eller avhengigheter.
- **ci**: Endringer i CI-konfigurasjon.
- **chore**: Andre endringer som ikke påvirker koden direkte.
- **revert**: Tilbakestille en tidligere commit.
- **security**: Implementering av sikkerhetsrelaterte forbedringer.

## Scope

`<scope>` er valgfritt, men nyttig for å spesifisere hvilken del av koden endringen påvirker (f.eks. `auth`, `api`, `ui`).

### Eksempler: Scope

feat(api): legg til støtte for nye spørringsparametere

fix(ui): løs renderingsproblem på mobile enheter

## Beskrivelse

Den korte beskrivelsen bør være en en-linjers oppsummering av endringen.

### Valgfri kropp

Bruk kroppen til å forklare **hvorfor** endringen ble gjort, ikke bare **hva** som ble gjort.

### Valgfrie footers

Footers brukes til tilleggsinformasjon, som referanser til problemer eller breaking changes.

#### Eksempler: Struktur

fix(auth): korriger tokenvalidering

Sørg for at tokenets utløpstid sjekkes riktig.

Fixes #1234

feat(api): legg til ny spørringsstøtte

Legg til støtte for includeInactive-spørringsparameteren.

BREAKING CHANGE: Standardresponsen inkluderer nå inaktive brukere som standard.

## Sjekkliste for commit-meldinger

1. **Type**: Er typen korrekt (f.eks. `feat`, `fix`)?
2. **Scope**: Har commit-en et passende scope?
3. **Beskrivelse**: Er beskrivelsen kort og informativ?
4. **Kropp**: Forklarer kroppen hvorfor endringen ble gjort hvis nødvendig?
5. **Footers**: Er footers lagt til hvis nødvendig (f.eks. `Fixes #1234` eller `BREAKING CHANGE`)?
