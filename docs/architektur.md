# Architektur

React-SPA gegen eine zustandslose Spring-Boot-REST-API. Backend strikt 3-schichtig
(Controller → Service → Repository); Autorisierung zweistufig: grobe Rollen-Gates per
`@PreAuthorize` in den Controllern, feine Eigentümer-/Mitgliedschaftsprüfung in den Services.
Authentifizierung über JWT (Bearer-Token).

```mermaid
graph TD
  subgraph Frontend [React SPA - Vite/TypeScript]
    UI[Pages / Components] --> APIClient[API-Client + JWT]
  end
  APIClient -->|REST/JSON, Bearer-Token| C[Controller-Schicht]
  subgraph Backend [Spring Boot]
    C --> S[Service-Schicht<br/>Geschaeftslogik, Validierung, Autorisierung]
    S --> R[Repository-Schicht<br/>Spring Data JPA]
    R --> DB[(H2-Datenbank)]
    SEC[Spring Security<br/>JWT-Filter] -.pruft Token.-> C
  end
```
