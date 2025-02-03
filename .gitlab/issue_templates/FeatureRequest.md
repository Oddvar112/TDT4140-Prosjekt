# **Sammendrag**

<!-- Gi en kort beskrivelse av funksjonen som skal implementeres. -->
Implementer en ny funksjon som lar brukere tilbakestille passordene sine gjennom brukergrensesnittet.

## **Bakgrunn**

<!-- Beskriv hvorfor denne funksjonen er nødvendig og hvordan den vil forbedre systemet. -->
Brukere trenger en måte å tilbakestille passordene sine på hvis de glemmer dem. Denne funksjonen vil tillate brukere å be om en lenke for tilbakestilling av passord, som vil bli sendt til deres e-post, slik at de kan opprette et nytt passord. Denne funksjonen vil forbedre brukeropplevelsen og kontosikkerheten.

## **Implementeringstrinn**

<!-- Beskriv trinnene som kreves for å implementere funksjonen. -->
1. Opprett en "Glemt passord?"-knapp på innloggingssiden.
2. Når den klikkes, be brukeren om å oppgi e-postadressen sin.
3. Send en e-post med en lenke for tilbakestilling av passord.
4. Opprett et skjema for å la brukere skrive inn et nytt passord.
5. Oppdater brukerdatabasen med det nye passordet (hashed).

## **Akseptansekriterier**

<!-- Definer hva som skal sjekkes eller valideres for å anse funksjonen som fullført. -->
- [ ] Brukere kan be om tilbakestilling av passord.
- [ ] Systemet sender en e-post for tilbakestilling av passord.
- [ ] Brukere kan oppdatere passordet sitt vellykket.
- [ ] Passord lagres sikkert og hashed.

## **Prioritet**

<!-- Angi prioriteten til denne funksjonsforespørselen (f.eks. Høy, Middels, Lav). -->
 Høy

## **Tilleggsinformasjon**
<!-- Legg til andre detaljer, som relevante lenker eller vedlegg. -->
- Lenken for tilbakestilling av passord bør utløpe etter 24 timer.
- Sørg for at tilbakestillingsprosessen følger OWASP sikkerhetsretningslinjer.
