# Pharmacy Management System – Sales & Billing module

Spring Boot 3 (Java 17) + Spring Data JPA + Aiven MySQL, with a plain HTML/CSS/JS front end served from `src/main/resources/static`.

## Run
1. In Aiven console create a MySQL service and copy Host, Port, User, Password (a database named `defaultdb` exists by default).
2. Set environment variables (do NOT commit them to GitHub):
   - `DB_URL=jdbc:mysql://<host>:<port>/defaultdb?sslMode=REQUIRED`
   - `DB_USERNAME=avnadmin`
   - `DB_PASSWORD=<your password>`
3. `mvn spring-boot:run`  then open http://localhost:8080
4. Tables are created automatically (`ddl-auto=update`).
   Install the Lombok plugin in your IDE and enable annotation processing.

## REST API
| Method | URL | Purpose |
|---|---|---|
| GET | /api/medicines?search= | list / search medicines |
| POST, PUT, DELETE | /api/medicines[/{id}] | add, edit, delete medicine |
| POST | /api/invoices | process a sale (deducts stock, creates invoice, records payment) |
| GET | /api/invoices, /api/invoices/{id} | invoice history / one invoice |
| POST | /api/invoices/{id}/payments | record a further payment |

Sale request body:
```json
{ "customerName": "Nimal", "customerPhone": "0771234567",
  "items": [{ "medicineId": 1, "quantity": 2 }],
  "discount": 0, "amountPaid": 500, "paymentMethod": "CASH" }
```
