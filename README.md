# aboali-pos-backend



Run the APP
# Navigate to your backend folder
cd arabic-pos-backend

# Start PostgreSQL + pgAdmin in background
docker compose up -d

# Check they are running ✅
docker compose ps


Then
./mvnw spring-boot:run

# Quick health check
curl http://localhost:8080/api-docs

cd arabic-pos-system
npm run dev

✅ Open http://localhost:5173 in your browser

# ── Daily use ──────────────────────────────────────────────────
docker compose up -d          # start containers
docker compose down           # stop containers (data preserved)
docker compose restart        # restart all

# ── Logs ───────────────────────────────────────────────────────
docker compose logs postgres  # postgres logs
docker compose logs -f        # follow all logs live

# ── Database shell ─────────────────────────────────────────────
docker exec -it arabic_pos_db psql -U pos_user -d arabic_pos

# ── Backup database ────────────────────────────────────────────
docker exec arabic_pos_db pg_dump -U pos_user arabic_pos > backup.sql

# ── Restore database ───────────────────────────────────────────
cat backup.sql | docker exec -i arabic_pos_db psql -U pos_user -d arabic_pos

# ── Nuclear reset (wipe all data) ──────────────────────────────
docker compose down -v        # ⚠️ deletes volumes too!
docker compose up -d          # fresh start




