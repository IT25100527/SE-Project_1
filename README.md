# Pharmacy Management System — User Management Module

Spring Boot MVC module covering **User Management**: customer registration/login,
staff (admin-created) login, role-based dashboards, email verification, and
admin CRUD over users — matching the `users / roles / permissions / role_permissions /
login_logs / email_verification_tokens` tables from the shared schema. Other tables
(medicine, inventory, prescriptions, purchase orders, suppliers, deliveries, stock
receipts) belong to your teammates' modules and are not touched here.

## Roles
ADMIN, INVENTORY_MANAGER, DELIVERY_STAFF, PHARMACIST, CASHIER, PURCHASE_MANAGER, CUSTOMER
— seeded automatically on first run (see `config/DataSeeder.java`).

## Run it

1. Create a MySQL database (or let `createDatabaseIfNotExist=true` do it) and set
   credentials in `src/main/resources/application.properties`.
2. Set real Gmail SMTP credentials as env vars (or edit the properties file directly):
   ```
   export MAIL_USERNAME=you@gmail.com
   export MAIL_APP_PASSWORD=your16charapppassword   # Gmail App Password, not your normal password
   ```
3. `mvn spring-boot:run`
4. App runs at http://localhost:8080

## URLs

| Page | URL |
|---|---|
| Customer login | http://localhost:8080/customer/login |
| Customer register | http://localhost:8080/customer/register.html |
| Staff/Admin login | http://localhost:8080/manager/login |
| Dashboard (role-aware) | http://localhost:8080/dashboard |

Default seeded admin: `admin@pharmacy.com` / `Admin@123` — change the password after
first login (an update endpoint is provided; a change-password UI is a good next step).

## API summary

- `POST /customer/register` — create customer account, sends email verification link
- `GET  /customer/verify-email?token=...` — verifies email
- `POST /customer/resend-verification?email=...`
- `POST /customer/login` / `POST /customer/logout`
- `POST /manager/login` / `POST /manager/logout` — any non-customer role
- `GET  /api/session` — who's logged in + role (drives the dashboard UI)
- `POST /api/admin/users/staff` — admin creates a staff account (any role except CUSTOMER)
- `GET  /api/admin/users/staff` / `GET /api/admin/users/customers`
- `GET  /api/admin/users/{id}` / `PUT /api/admin/users/{id}`
- `PATCH /api/admin/users/{id}/status?status=ACTIVE|SUSPENDED|INACTIVE`
- `DELETE /api/admin/users/{id}`

All `/api/admin/**` routes require an active session with role `ADMIN` (enforced by
`AuthInterceptor`).

## Notes / what still needs a decision from your team

- **Phone verification**: only *format* validation is implemented (Sri Lankan mobile
  regex). Real OTP-based phone verification needs an SMS gateway (e.g. Twilio, a local
  SL SMS API) which isn't in the shared schema/tables — add a `phone_verification_tokens`
  table + an SMS provider when you're ready and I can wire it the same way email
  verification works.
- Auth here is deliberately **not** Spring Security's form login — it's a manual
  session (`HttpSession`) + `AuthInterceptor`, because the frontend talks to plain JSON
  endpoints under `/customer/**` and `/manager/**`. Spring Security is only used for
  `BCryptPasswordEncoder`.
- Non-admin dashboards (Inventory Manager, Delivery Staff, Pharmacist, Cashier,
  Purchase Manager, Customer) render their nav + stat-card layout using the shared CSS,
  with placeholder content where each teammate's module plugs in later.
