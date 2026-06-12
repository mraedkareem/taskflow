# Deploying TaskFlow to Railway — step by step

Total time: ~15 minutes. Everything happens in the browser.

## 1. Create the Railway project

1. Go to [railway.app](https://railway.app) and log in (use **"Login with GitHub"**).
2. Click **New Project** → **Deploy from GitHub repo**.
3. Authorize Railway to access your GitHub, then pick **`taskflow`**.
4. Railway detects a Maven project and starts building automatically.
   The first build will **fail to start** — that's expected, the database
   and environment variables don't exist yet. Keep going.

## 2. Add PostgreSQL

1. In your project view, click **+ New** → **Database** → **Add PostgreSQL**.
2. Wait until the Postgres service shows a green checkmark.

## 3. Set the environment variables

1. Click your **taskflow service** (not the database) → **Variables** tab.
2. Add these five variables. The `${{Postgres...}}` syntax is a *reference* —
   Railway fills in the value from the database service automatically:

| Name | Value |
|---|---|
| `DB_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `DB_USERNAME` | `${{Postgres.PGUSER}}` |
| `DB_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `JWT_SECRET` | a long random string — generate one at the command line with:<br>`node -e "console.log(require('crypto').randomBytes(48).toString('hex'))"` |
| `ADMIN_PASSWORD` | a strong password for the seeded `admin` account |

3. Railway redeploys automatically after saving variables.

## 4. Get your public URL

1. taskflow service → **Settings** tab → **Networking** → **Generate Domain**.
2. You get a URL like `taskflow-production-xxxx.up.railway.app`.
3. To use `taskflow.railway.app`-style custom names, edit the generated
   domain name in the same place (if available).

## 5. Verify it works

- Open `https://<your-domain>/` → the React login page should load.
- Open `https://<your-domain>/swagger-ui/index.html` → API docs should load.
- Register a user in the UI and create a task.

## 6. CI/CD is now active

Railway watches your GitHub repo: **every push to `main` deploys
automatically**. Combined with GitHub Actions running tests on every
push, that is the full CI/CD pipeline.

## Note about the frontend

The React app is pre-built into `src/main/resources/static/`, so Spring Boot
serves it — one service runs the full application. If you change frontend
code, rebuild before pushing:

```bash
cd frontend
npm install
npm run build   # outputs to ../src/main/resources/static
```
