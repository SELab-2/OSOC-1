# 100% Completionist Speedrun Milestone 1

## Code

### General
- architecture choices aanpassen om overige eigenschappen te verklaren (password encryption scheme - Bcrypt, Spring Security)

### Frontend
- CORS fixen
- Registreren van een nieuwe user
- Login werkende krijgen
- Finale routing
- Logout fixen

### Backend
- CORS fixen
- Documentatie bij code schrijven (auth)
- Refresh token fixen
- Project / Skills endpoints implementeren / extenden

## Presentatie

### Overzicht
- Alle endpoints uitleggen / bespreken
- Design choices
- Architectural choices
- Uitleg van testen / documentatie
- Volgende stappen
- Demo
- Etc.

### Stappenplan demo
TODO

## Difficulties & Challenges

### General
- Self-Hosted Runner was veel ingewikkelder dan voorzien (aanpassen van de machine state -> alles in een docker container moeten plaatsen)
- Permissions / files bij Self-Hosted runner. Github Actions gebruiken standaard github workspace. (Production files moeten gekopieerd worden naar andere file location)
- Schrijven van duidelijke documentatie is ingewikkelder dan het lijkt, sommige dingen lijken triviaal terwijl anderen hier toch onduidelijkheden zien.
- Zorgen dat Actions **NIET** falen, veel rode kruisjes :(
- Actions niet lokaal kunnen testen zorgt voor vertraging bij het maken ervan.

### Frontend
- CORS BEING A PAIN IN MY FUCKING ASS
- Styling is heel ingewikkeld om met van start te gaan, maar het heeft uiteindelijk toch zijn charmes.
- Styling neemt veel meer tijd in beslag dan het effectief programmeren van de functionaliteit *Shocker*.

### Backend
- Docker. (Oplossing -> Rund file van Lars). Problemen op WSL omdat WSL geen systemd heeft, dus moet docker opgestart worden met `sudo dockerd`.
- Bij Kotlin testen geen nuttige feedback over waarom een test faalt (vooral een issue bij authenticatie).
- Geen deftige feedback van JPA over reserved terms (name, desc).

## Vragen
- Hoe zouden we best data van vorige edities opslaan?
  1. Alle logica/data in de huidige database steken waardoor deze volledig gebloat wordt met informatie die eigenlijk niet langer van toepassing is.
  2. Verschillende databases gebruiken per editie, waardoor er een soort plug&play is met elke database.
- Horen we aan de backend filters te gebruiken (filteren op naam etc.) of mag dit ook in de frontend gebeuren.
- Is het de bedoeling dat we voorbereiden op een grote hoeveelheid data en hierdoor pagination implementeren of blijft de hoeveelheid data beperkt genoeg om dit niet te moeten doen?