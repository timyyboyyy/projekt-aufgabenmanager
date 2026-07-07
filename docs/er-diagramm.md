# ER-Diagramm

Datenmodell des Aufgabenmanagers (wie im Backend umgesetzt). Tabellenname `app_user`,
da `user` in vielen Datenbanken reserviert ist; die n:m-Zuordnung Mitglied liegt in der
Join-Tabelle `project_member`. `organisation_id` ist zur Mandantenvorbereitung vorhanden
(ein aktiver Mandant, Default 1).

```mermaid
erDiagram
  ROLE ||--o{ APP_USER : "hat"
  APP_USER ||--o{ PROJECT : "leitet (leiter)"
  PROJECT ||--o{ TASK : "enthaelt"
  APP_USER ||--o{ TASK : "bearbeitet"
  APP_USER }o--o{ PROJECT : "Mitglied (project_member)"

  APP_USER {
    long id PK
    string username
    string passwordHash
    boolean aktiv
    long role_id FK
    long organisation_id
  }
  ROLE {
    long id PK
    string name "ADMIN|PROJEKTLEITER|MITARBEITER"
  }
  PROJECT {
    long id PK
    string name
    string beschreibung
    string status "AKTIV|ARCHIVIERT"
    long leiter_id FK
    long organisation_id
  }
  TASK {
    long id PK
    string titel
    string beschreibung
    string status "OFFEN|IN_BEARBEITUNG|ERLEDIGT"
    long project_id FK
    long bearbeiter_id FK
  }
  PROJECT_MEMBER {
    long project_id FK
    long user_id FK
  }
```
