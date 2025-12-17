# Validierungsregeln - Auto API

## Übersicht

Die Auto-API implementiert umfassende Validierungsregeln, um die Datenintegrität und -qualität sicherzustellen. Alle Validierungen werden vor dem Speichern in der Datenbank durchgeführt und geben aussagekräftige Fehlermeldungen zurück.

---

## Auto-Entität Validierungen

### Pflichtfeld-Validierungen

| Feld | Regel | Fehlermeldung | HTTP Status |
|------|-------|---------------|-------------|
| **Modell** (String) | Darf nicht leer sein | "Fehler: Modell darf nicht leer sein" | 400 BAD REQUEST |
| **FS_Marken** (FK) | Marke muss existieren | "Fehler: Marke mit ID {id} existiert nicht" | 400 BAD REQUEST |

### Zusätzliche Validierungsregeln (nicht Pflichtfeld)

#### 1. Baujahr-Validierung (DATE)
- **Regel:** Baujahr darf nicht in der Zukunft liegen
- **Datentyp:** `LocalDate` (DATE)
- **Logik:** `Baujahr <= Heute`
- **Fehlermeldung:** "Fehler: Baujahr darf nicht in der Zukunft liegen"
- **HTTP Status:** 400 BAD REQUEST
- **Begründung:** Ein Auto kann nicht in der Zukunft gebaut worden sein

**Beispiel:**
```json
// Ungültig - wird abgelehnt
{
  "baujahr": "2099-12-31"  // Liegt in der Zukunft
}

// Gültig
{
  "baujahr": "2024-06-15"  // Liegt in der Vergangenheit oder ist heute
}
```

#### 2. Gewicht-Validierung (DECIMAL)
- **Regel:** Gewicht muss größer als 0 sein
- **Datentyp:** `double` (DECIMAL)
- **Logik:** `Gewicht > 0`
- **Fehlermeldung:** "Fehler: Gewicht muss größer als 0 sein"
- **HTTP Status:** 400 BAD REQUEST
- **Begründung:** Ein Auto kann kein negatives oder Null-Gewicht haben

**Beispiel:**
```json
// Ungültig - wird abgelehnt
{
  "gewicht": 0.0      // Null ist ungültig
}
{
  "gewicht": -1500.5  // Negativ ist ungültig
}

// Gültig
{
  "gewicht": 1500.5   // Positiver Wert in kg
}
```

#### 3. Leistung-Validierung (INTEGER)
- **Regel:** Leistung muss größer als 0 sein
- **Datentyp:** `int` (INTEGER)
- **Logik:** `Leistung > 0`
- **Fehlermeldung:** "Fehler: Leistung muss größer als 0 sein (PS)"
- **HTTP Status:** 400 BAD REQUEST
- **Begründung:** Ein Auto kann keine negative oder Null-Leistung haben

**Beispiel:**
```json
// Ungültig - wird abgelehnt
{
  "leistung": 0      // Null ist ungültig
}
{
  "leistung": -200   // Negativ ist ungültig
}

// Gültig
{
  "leistung": 200    // Positiver Wert in PS
}
```

---

## Marken-Entität Validierungen

### Pflichtfeld-Validierungen

| Feld | Regel | Fehlermeldung | HTTP Status |
|------|-------|---------------|-------------|
| **Name** (String) | Darf nicht leer sein | "Fehler: Markenname darf nicht leer sein" | 400 BAD REQUEST |

### Referentielle Integrität

| Operation | Regel | Fehlermeldung | HTTP Status |
|-----------|-------|---------------|-------------|
| **Marke löschen** | Darf nicht von Autos referenziert werden | "Fehler: Marke kann nicht gelöscht werden. Sie wird noch von X Auto(s) verwendet. Bitte löschen Sie zuerst die zugehörigen Autos." | 409 CONFLICT |

---

## Validierungs-Workflow

### Bei POST /autos (Neues Auto erstellen)

```
1. Prüfe ob Marke existiert
   └─ Nein → 400 BAD REQUEST
   └─ Ja → Weiter

2. Prüfe ob Modellname leer ist
   └─ Ja → 400 BAD REQUEST
   └─ Nein → Weiter

3. Prüfe ob Baujahr in Zukunft liegt
   └─ Ja → 400 BAD REQUEST
   └─ Nein → Weiter

4. Prüfe ob Gewicht > 0
   └─ Nein → 400 BAD REQUEST
   └─ Ja → Weiter

5. Prüfe ob Leistung > 0
   └─ Nein → 400 BAD REQUEST
   └─ Ja → Weiter

6. Auto in Datenbank einfügen
   └─ Erfolg → 201 CREATED
   └─ Fehler → 500 INTERNAL SERVER ERROR
```

### Bei PUT /autos (Auto aktualisieren)

```
1. Prüfe ob Auto existiert
   └─ Nein → 404 NOT FOUND
   └─ Ja → Weiter

2. Prüfe ob Marke existiert
   └─ Nein → 400 BAD REQUEST
   └─ Ja → Weiter

3. Prüfe ob Modellname leer ist
   └─ Ja → 400 BAD REQUEST
   └─ Nein → Weiter

4. Prüfe ob Baujahr in Zukunft liegt
   └─ Ja → 400 BAD REQUEST
   └─ Nein → Weiter

5. Prüfe ob Gewicht > 0
   └─ Nein → 400 BAD REQUEST
   └─ Ja → Weiter

6. Prüfe ob Leistung > 0
   └─ Nein → 400 BAD REQUEST
   └─ Ja → Weiter

7. Auto in Datenbank aktualisieren
   └─ Erfolg → 200 OK
   └─ Fehler → 500 INTERNAL SERVER ERROR
```

---

## Zusammenfassung der Datentyp-Validierungen

| Datentyp | Validierungsregel | Feld |
|----------|-------------------|------|
| **DATE** | Nicht in der Zukunft | Baujahr |
| **DECIMAL** | Muss größer als 0 sein | Gewicht |
| **INTEGER** | Muss größer als 0 sein | Leistung |
| **STRING** | Darf nicht leer sein | Modell, Name |
| **FOREIGN KEY** | Referenz muss existieren | FS_Marken |

---

## Logging bei Validierungsfehlern

Alle Validierungsfehler werden geloggt:

```java
LOGGER.log(Level.WARNING, "Baujahr liegt in der Zukunft: " + auto.getBaujahr());
LOGGER.log(Level.WARNING, "Ungültiges Gewicht: " + auto.getGewicht());
LOGGER.log(Level.WARNING, "Ungültige Leistung: " + auto.getLeistung());
LOGGER.log(Level.WARNING, "Marke mit ID " + id + " existiert nicht");
```

Dies ermöglicht eine vollständige Nachverfolgbarkeit von Validierungsfehlern in den Logs.

---

## Test-Szenarien

Alle Validierungsregeln werden durch JUnit-Tests abgedeckt:

- ✅ `testAddAuto_NegativeWeight()` - Negatives Gewicht
- ✅ `testAddAuto_FutureBaujahr()` - Zukünftiges Baujahr
- ✅ `testAddAuto_InvalidMarkeId()` - Nicht-existierende Marke
- ✅ `testAddMarke_EmptyName()` - Leerer Markenname
- ✅ `testDeleteMarke_WithReferences()` - Marke mit Referenzen löschen

Siehe `TestingServices.java` für vollständige Test-Implementierung.
