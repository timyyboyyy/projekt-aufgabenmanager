# CLAUDE.md – projekt-aufgabenmanager

Kontext für Claude Code. Bei Detailfragen zuerst `_planung/Entwicklungsplan.md` und
`_planung/Funktionsumfang.md` lesen (liegen lokal, sind bewusst nicht im Repo).

## Projekt
Webbasiertes Projekt- und Aufgabenmanagementsystem für ein IT-Dienstleistungsunternehmen.
Studentisches Prüfungsprojekt. Fokus: saubere, nachvollziehbare Umsetzung – „einfach"
im Sinne der Aufgabe, keine Feature-Fülle.

## Technologiestack (verbindlich, nicht abweichen)
- Backend: Java 21, Spring Boot 3, Spring Web, Spring Data JPA/Hibernate, Spring Security (JWT)
- Datenbank: H2 (Datei-Modus), PostgreSQL-tauglich konfigurieren
- Frontend: React (Vite, TypeScript), SPA gegen REST-API
- Tests: JUnit 5 + Spring Boot Test/MockMvc (Backend), Vitest + React Testing Library (Frontend)
- Build: Maven (Backend), npm/Vite (Frontend)

## Repo-Struktur (Monorepo)
- `backend/`  – Spring Boot (config, controller, service, repository, model, dto)
- `frontend/` – React (api, pages, components, context, routes)
- `docs/`     – ER-Diagramm, UML, Mockups, Screenshots (für die Doku)
- `_planung/` – lokale Spezifikation, GITIGNORED, nie committen

## Architektur & Konventionen
- Backend strikt 3-schichtig: Controller → Service → Repository. Keine Geschäftslogik in Controllern.
- Controller sprechen nur über DTOs (keine Entities nach außen).
- Geschäftslogik, Validierung und Autorisierung in der Service-Schicht.
- Autorisierung serverseitig erzwingen – UI-Ausblenden reicht nicht.
- Code/Bezeichner auf Englisch, fachliche Enum-Werte wie im Datenmodell.
- Aussagekräftige, kleine Commits (je Feature/Story einen), keine Riesen-Dumps.

## Rollen & Rechte
Drei Rollen: `ADMIN`, `PROJEKTLEITER`, `MITARBEITER`.
- ADMIN: Benutzer/Rollen verwalten
- PROJEKTLEITER: Projekte anlegen/bearbeiten/archivieren, Mitglieder zuordnen, Fortschritt sehen
- MITARBEITER: Aufgaben erstellen/bearbeiten, Status ändern
Benutzer sehen nur berechtigte Projekte.

## Datenmodell (Kern)
User, Role, Project (status AKTIV|ARCHIVIERT, leiter→User), Task (status
OFFEN|IN_BEARBEITUNG|ERLEDIGT, project→Project, bearbeiter→User), ProjectMember (User↔Project n:m).
Passwörter nur als BCrypt-Hash. Mandantenfähigkeit nur vorbereiten (optionales Feld, ein aktiver Mandant).
Vollständige ER-/API-/UML-Details in `_planung/Entwicklungsplan.md`.

## Security
- JWT-basiert: Login gibt Token zurück, Frontend sendet es als Bearer.
- JWT-Secret NICHT committen – über Umgebungsvariable / `application-local.properties` (gitignored).

## Seed-Daten
Beim Start via DataSeeder anlegen: 1 Admin, 1 Projektleiter, 2 Mitarbeitende, 2 Projekte,
Aufgaben in allen drei Status – damit Fortschrittsanzeige und Screenshots sofort Inhalt haben.
Testzugänge im README dokumentieren.

## Tests (Pflicht – Qualitätsnachweis für die Doku)
Mindestens abdecken: Fortschrittsberechnung, Status-Übergänge, Autorisierung (401/403),
Login-Flow. Lieber wenige aussagekräftige Tests als viele triviale.

## Umsetzungsreihenfolge
1. Monorepo-Gerüst (backend/ + frontend/), README, .gitignore
2. Backend: Entities + Repositories + H2-Config + DataSeeder
3. Security: JWT-Login, Rollen, geschützte Endpunkte
4. Backend: Services + Controller je Story
5. Backend-Tests
6. Frontend: AuthContext + Login + geschützte Routen
7. Frontend: Seiten (Dashboard, Projektliste, Projektdetail, Benutzerverwaltung)
8. Frontend-Tests
9. Screenshots + Diagramme nach docs/

## Nicht ins Repo
IU-Aufgabenstellung, schriftliche Fallstudie, `_planung/`, Secrets/Tokens, H2-DB-Dateien.
