# LB M295 Backend 

## Name des Projekts 
Lucas Auto-Wunschliste 

## Beschreibung 
Wunschliste die zukünftig festhält welche Traumautos ich mal besitzen möchte :) 

## Visuals
### Datenbankdiagramm

<img width="677" height="295" alt="Screenshot 2025-12-16 at 10 47 54" src="https://github.com/user-attachments/assets/da4624f0-30df-4e60-83ce-c714deb293b7" />

### Klassendiagramm

<img width="658" height="264" alt="Screenshot 2025-12-17 at 11 03 11" src="https://github.com/user-attachments/assets/27ed8c7b-49a6-4b92-9882-d1e7b4c99462" />

### Testdurchführung

### Test AutoServices

<img width="1470" height="956" alt="Screenshot 2025-12-17 at 09 22 06" src="https://github.com/user-attachments/assets/10cc7596-e733-4f87-a913-0d9ac4e3bf02" />

### Test Roles

<img width="1468" height="877" alt="Screenshot 2025-12-17 at 14 36 45" src="https://github.com/user-attachments/assets/945fd6bf-4a55-475b-970d-9f8226c5afed" />

 
## Berechtigungsmatrix

### Rollen

  | Rolle      | Benutzer | Passwort | Beschreibung                                    |
  |------------|----------|----------|-------------------------------------------------|
  | @PermitAll | -        | -        | Öffentlich zugänglich (keine Authentifizierung) |
  | USER       | user     | 187      | Lesezugriff auf alle Ressourcen                 |
  | ADMIN      | admin    | 1234     | Vollzugriff (Lesen, Schreiben, Löschen)         |

  Auto-Ressource (/autos)

  | Endpoint                   | HTTP   | Beschreibung            | Öffentlich | USER | ADMIN |
  |----------------------------|--------|-------------------------|------------|------|-------|
  | /autos/ping                | GET    | API Status prüfen       | ✅         | ✅   | ✅    |
  | /autos?id={id}             | GET    | Auto nach ID abrufen    | ❌         | ✅   | ✅    |
  | /autos/all                 | GET    | Alle Autos abrufen      | ❌         | ✅   | ✅    |
  | /autos/baujahr?jahr={jahr} | GET    | Autos nach Jahr filtern | ❌         | ✅   | ✅    |
  | /autos/count               | GET    | Anzahl der Autos        | ❌         | ✅   | ✅    |
  | /autos                     | POST   | Neues Auto erstellen    | ❌         | ❌   | ✅    |
  | /autos                     | PUT    | Auto aktualisieren      | ❌         | ❌   | ✅    |
  | /autos?id={id}             | DELETE | Auto löschen            | ❌         | ❌   | ✅    |
  | /autos/all                 | DELETE | Alle Autos löschen      | ❌         | ❌   | ✅    |

### Legende

  - ✅ = Zugriff erlaubt
  - ❌ = Zugriff verweigert (401 UNAUTHORIZED oder 403 FORBIDDEN)

## OpenAPI Dokumentation

[https://github.com/Lucas187-glitch/ProjektM295#:~:text=yesterday-,OpenAPI.json,-Add%20files%20via](https://github.com/Lucas187-glitch/ProjektM295/blob/master/OpenAPI.json)

## Validirung 

https://github.com/Lucas187-glitch/ProjektM295/blob/master/Validierungsregeln.md


## Autor
Lucas Weisshaar

## Zusammenfassung







