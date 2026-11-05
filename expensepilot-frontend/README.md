# ExpensePilot frontend

React + Vite frontend for the existing Spring Boot REST API.

## Run
1. Install Node.js LTS.
2. Open a terminal in this folder.
3. Run `npm install`
4. Copy `.env.example` to `.env` and set `VITE_API_BASE_URL` to your backend URL.
5. Run `npm run dev` and open http://localhost:5173.

## Backend integration — important
The frontend is wired through `src/api/`. Authentication paths are set to `/auth/login` and `/auth/register` based on the existing project discussion. Verify these against your controller mappings.
All other resource paths are centralized in `src/api/endpoints.js`. Confirm their exact mappings and DTO property names against your Java controllers/DTOs before expecting those screens to load.

The app assumes the login response contains a JWT string or an object with `token`, `accessToken`, or `jwt`. It sends `Authorization: Bearer <token>` on protected requests.
Do not put secrets in `.env`; the API base URL is not a secret.

## Recommended production setup
Build with `npm run build`; deploy `dist/` to a static host or serve it via Spring Boot static resources. Configure Spring Security/CORS appropriately if frontend and backend use different origins.
