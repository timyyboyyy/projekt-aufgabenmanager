# Navigation (Frontend)

Seitenfluss der SPA. Alle Seiten außer Login sind geschützt (gültiges JWT nötig);
die Benutzerverwaltung ist zusätzlich auf die Rolle ADMIN beschränkt.

```mermaid
graph LR
  Login --> Dashboard
  Dashboard --> Projektliste
  Dashboard --> Benutzerverwaltung[Benutzerverwaltung - nur ADMIN]
  Projektliste --> Projektdetail
  Projektdetail --> AufgabenDialog[Aufgaben-Dialog]
  Projektdetail --> Fortschritt[Fortschrittsanzeige]
```
