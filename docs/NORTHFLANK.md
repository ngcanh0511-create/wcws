# Deploy WCPL on Northflank

This project deploys as two Northflank services from the same GitHub repository:

- `wcpl-backend`: Spring Boot API, Dockerfile at `backend/Dockerfile`
- `wcpl-frontend`: nginx static frontend, Dockerfile at `frontend/Dockerfile`

## 1. Backend Service

Create a combined service:

- Repository: `ngcanh0511-create/wcws`
- Branch: `main`
- Build type: Dockerfile
- Dockerfile path: `backend/Dockerfile`
- Build context: `backend`
- Public port: `8080`, HTTP

Runtime variables:

```text
JWT_SECRET=<long-random-production-secret-at-least-32-chars>
CORS_ORIGINS=https://<frontend-public-domain>
FOOTBALL_API_PROVIDER=worldcup26
WCPL_DATA_DIR=/data/wcpl
```

Persistent volume:

- Access mode: Single Read/Write
- Container mount path: `/data/wcpl`
- Recommended size: start with 1 GB

The backend stores SQLite data at:

```text
/data/wcpl/wcpl.db
```

Uploaded avatars are stored at:

```text
/data/wcpl/avatars
```

Health check:

```text
GET /api/v1/health
```

## 2. Frontend Service

Create a second combined service:

- Repository: `ngcanh0511-create/wcws`
- Branch: `main`
- Build type: Dockerfile
- Dockerfile path: `frontend/Dockerfile`
- Build context: `frontend`
- Public port: `8080`, HTTP

Runtime variables:

```text
API_BASE_URL=https://<backend-public-domain>
```

The frontend container writes `config.js` at startup from `API_BASE_URL`, so the same image can be reused for staging or production.

## 3. Final Wiring

After both services have public domains:

1. Copy the backend public URL into frontend `API_BASE_URL`.
2. Copy the frontend public URL into backend `CORS_ORIGINS`.
3. Redeploy or restart both services.
4. Open the frontend URL and login with the seeded admin account if the SQLite database is new.

Default admin for a fresh database:

```text
username: admin
password: 123qwe!@#
```

## Notes

- Do not scale the backend above 1 instance while using SQLite on a Single Read/Write volume.
- For production with multiple backend instances, migrate from SQLite to a managed database such as PostgreSQL.
- Keep `JWT_SECRET` private and do not commit it to Git.
