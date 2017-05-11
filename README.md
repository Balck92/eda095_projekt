# EDA095 projekt - Chat

## Resultat
I vårt projekt har vi utvecklat ett chattforum, där det går att skicka både textmeddelanden i en gruppchatt till samtliga användare (broadcast) och privata meddelanden till en sprecifik användare. Förutom att skicka textmeddelanden kan användare även skicka bilder. Chattforumet innehåller en användarlista där alla inloggade användare syns, det vill säga användare läggs till i användarlistan när de loggar in och tas bort från användarlistan när de loggar ut. När Användare loggar in väljer de ett användarnamn och när de vill logga ut klickar de ner chatten.  

BILDER!!

## Tekniker och design
I projektet används en multitrådar TCP server och ett program som kör mutipla trådar som läser och visar meddelanden. 
## Utvärdering

## Förslag på förbättringar
 - Använda HTTP istället för TCP (byta Socket mot URL)
 - Användarlista (uppdateras när någon går med eller lämnar)
 - Chattrum
 - Privata meddelanden
 - Gränssnitt som går att ändra storlek på (fixa Layouts)
 - Skicka bilder (ny knapp)
 - Nya användare ser de senaste 10 meddelandena.
 
### Nästa möte är Ons 10/5, 12:00

## Vad den klarar av
Servern kan skicka meddelande till alla användare, skicka tillbaka meddelande, acceptera nya användare och ta bort användare.
Klienten består av ett enkelt gränssnitt som visar de senaste meddelanden, kan skicka meddelanden och stänga ner.

## Gruppen
Lisa Silfversten, Amanda Eliasson, Carl Johan Balck, Niklas Ovnell

