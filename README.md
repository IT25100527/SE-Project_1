# Rx+ PHARMACORE — Pharmacy Management System

Spring Boot backend + the Supplier Management frontend, combined into a
single runnable project. Add / View / Edit / Delete on the webpage all
talk to a real REST API backed by your Aiven MySQL database.

## What's included

- `src/main/java/Pharmacy/Management/System/...` — the backend
  (entity, repository, service, controller, DTOs, exception handling)
- `src/main/resources/static/` — the frontend (`index.html`,
  `style.css`, `script.js`). Spring Boot serves these automatically,
  so opening `http://localhost:8080` shows the same page you designed,
  now wired to the database.
- `database.sql` — creates the `suppliers` table on your Aiven MySQL
  database, and seeds the 3 sample suppliers from your original mockup.
- `src/main/resources/application.properties` — already points at
  your Aiven MySQL instance (same credentials you used before).

## 1. Set up the database (one-time)

Connect to your Aiven MySQL database (MySQL Workbench, DBeaver, or
IntelliJ's built-in Database tool window — `View → Tool Windows →
Database → +  → Data Source → MySQL`, using the same host/port/user/
password that's in `application.properties`), open `database.sql`,
and run it. This creates the `suppliers` table and 3 sample rows
(only if the table doesn't already have data).

## 2. Run it in IntelliJ

1. Open the project folder in IntelliJ (`File → Open`, pick this
   folder — the one with `pom.xml`).
2. Let Maven finish downloading dependencies (bottom-right progress bar).
3. Open `PharmacyManagementSystemApplication.java`
   (`src/main/java/Pharmacy/Management/System/`) and click the green
   ▶ run button.
4. Once you see "Pharmacy Management System is running!" in the
   console, open **http://localhost:8080** in your browser.

That's it — no separate frontend server needed. The page, the API, and
the database are all one app.

## How the pages connect to the backend

| Action on the page                          | Calls                          |
|------------------------------------------------|--------------------------------|
| Page loads / table refreshes                    | `GET /api/suppliers`           |
| Click **+ Add Supplier** → Save                 | `POST /api/suppliers`          |
| Click **View**                                  | `GET /api/suppliers/{id}`      |
| Click **Edit** (or "Edit Details" in the View modal) → Save | `PUT /api/suppliers/{id}` |
| Click **Delete**                                | `DELETE /api/suppliers/{id}`   |

The **Add Supplier** and **Edit** buttons open the exact same form —
when editing, it's pre-filled with that supplier's data and does a
`PUT` instead of a `POST`.

## Troubleshooting

- **"Could not connect to the server" toast in the browser** — the
  backend isn't running yet, or it failed to start. Check the
  IntelliJ Run console for the actual error (scroll to the red text).
- **"Communications link failure" / connection timeout on startup** —
  usually means your internet connection can't reach Aiven right now,
  or the Aiven service is paused/sleeping (free Aiven services can
  auto-pause after inactivity — check the Aiven console).
- **"Access denied for user 'avnadmin'"** — the password in
  `application.properties` doesn't match anymore (Aiven passwords can
  be rotated from the Aiven console) — grab the current one from
  Aiven and update it there.
- **"Unknown database 'defaultdb'" or table not found** — make sure
  you ran `database.sql` against the same database
  (`defaultdb`) that's in the connection URL.

> **Note on committing this project to Git/GitHub:** the database
> password lives in `application.properties`. If you push this to a
> public repository, remove the real password first (or move it to an
> environment variable) so it isn't exposed.
