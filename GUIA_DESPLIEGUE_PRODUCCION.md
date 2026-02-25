# Guía Completa de Despliegue en Producción - Tutodo Marketplace

## 📋 Tabla de Contenidos
1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Arquitectura de Despliegue](#arquitectura-de-despliegue)
3. [Preparación del Código](#preparación-del-código)
4. [Despliegue del Backend (Render)](#despliegue-del-backend-render)
5. [Despliegue del Frontend (Vercel)](#despliegue-del-frontend-vercel)
6. [Configuración de CORS](#configuración-de-cors)
7. [Variables de Entorno](#variables-de-entorno)
8. [Verificación del Despliegue](#verificación-del-despliegue)
9. [Consideraciones de Rendimiento](#consideraciones-de-rendimiento)
10. [Troubleshooting](#troubleshooting)

---

## 1. Resumen Ejecutivo

Este documento describe el proceso completo de despliegue de la aplicación Tutodo Marketplace en producción, utilizando servicios gratuitos de hosting en la nube.

### URLs de Producción
- **Backend**: https://tutodo-backend-1.onrender.com
- **Frontend**: https://tutodo-frontend.vercel.app
- **Base de Datos**: Supabase (PostgreSQL)
- **Storage**: Supabase Storage

### Tecnologías de Despliegue
- **Backend**: Render.com (Docker)
- **Frontend**: Vercel
- **Base de Datos**: Supabase PostgreSQL
- **Storage**: Supabase Storage
- **Control de Versiones**: GitHub

---

## 2. Arquitectura de Despliegue

```
┌─────────────────────────────────────────────────────────────┐
│                         USUARIOS                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    VERCEL (Frontend)                         │
│              https://tutodo-frontend.vercel.app              │
│                                                              │
│  - Angular 20                                                │
│  - Bootstrap 5                                               │
│  - Leaflet Maps                                              │
│  - CDN Global                                                │
└────────────────────┬────────────────────────────────────────┘
                     │ HTTPS
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  RENDER (Backend)                            │
│           https://tutodo-backend-1.onrender.com              │
│                                                              │
│  - Spring Boot 3.2.2                                         │
│  - Java 17                                                   │
│  - Docker Container                                          │
│  - API REST                                                  │
└────────────┬───────────────────────────┬────────────────────┘
             │                           │
             ▼                           ▼
┌────────────────────────┐  ┌──────────────────────────────┐
│  SUPABASE (Database)   │  │  SUPABASE (Storage)          │
│  PostgreSQL            │  │  Bucket: imagenes-productos  │
│  aws-0-us-west-1       │  │  Imágenes comprimidas        │
└────────────────────────┘  └──────────────────────────────┘
```

---

## 3. Preparación del Código

### 3.1. Backend - Configuración de Maven Wrapper

El archivo `mvnw` debe tener permisos de ejecución:

```bash
cd tutodo-backend
git update-index --chmod=+x mvnw
git add mvnw
git commit -m "Fix: Agregar permisos de ejecución a mvnw"
git push origin main
```

### 3.2. Backend - Dockerfile

Crear `Dockerfile` en la raíz del proyecto backend:

```dockerfile
# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.3. Backend - .dockerignore

Crear `.dockerignore`:

```
target/
.git/
.idea/
*.iml
*.log
.DS_Store
node_modules/
.env
.vscode/
*.md
!README.md
```

### 3.4. Frontend - Configuración de Environment

**Archivo: `src/environments/environment.ts`**
```typescript
export const environment = {
  production: false,
  apiUrl: 'https://tutodo-backend-1.onrender.com'
};
```

**Archivo: `src/environments/environment.prod.ts`**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tutodo-backend-1.onrender.com'
};
```

### 3.5. Frontend - Actualizar Servicios

Todos los servicios deben usar `environment.apiUrl`:

```typescript
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MiServicio {
  private apiUrl = `${environment.apiUrl}/api/endpoint`;
  // ...
}
```

### 3.6. Frontend - Configuración de Vercel

**Archivo: `vercel.json`**
```json
{
  "buildCommand": "npm run build:prod",
  "outputDirectory": "dist/tutodo-frontend/browser",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

### 3.7. Frontend - Angular.json

Actualizar budgets y file replacements:

```json
{
  "configurations": {
    "production": {
      "budgets": [
        {
          "type": "initial",
          "maximumWarning": "2MB",
          "maximumError": "5MB"
        },
        {
          "type": "anyComponentStyle",
          "maximumWarning": "20kB",
          "maximumError": "50kB"
        }
      ],
      "outputHashing": "all",
      "fileReplacements": [
        {
          "replace": "src/environments/environment.ts",
          "with": "src/environments/environment.prod.ts"
        }
      ]
    }
  }
}
```

---

## 4. Despliegue del Backend (Render)

### 4.1. Crear Cuenta en Render

1. Ve a https://render.com
2. Regístrate con tu cuenta de GitHub
3. Autoriza a Render para acceder a tus repositorios

### 4.2. Crear Web Service

1. Click en **"New +"** → **"Web Service"**
2. Conecta el repositorio: `Gladbat/tutodo-backend`
3. Configuración:
   - **Name**: `tutodo-backend`
   - **Region**: Oregon (US West)
   - **Branch**: `main`
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `Dockerfile`
   - **Docker Build Context Directory**: `.`
   - **Instance Type**: `Free`

### 4.3. Variables de Entorno

Agregar en Render → Environment:

```
DATABASE_URL=jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres
DATABASE_USERNAME=postgres.ktfhlxqqxfnotxvtewmd
DATABASE_PASSWORD=[tu-password-de-supabase]
SUPABASE_URL=https://ktfhlxqqxfnotxvtewmd.supabase.co
SUPABASE_SERVICE_ROLE_KEY=[tu-service-role-key]
PORT=8080
```

### 4.4. Deploy

1. Click en **"Create Web Service"**
2. Render comenzará a compilar (5-10 minutos)
3. Espera a que el status cambie a **"Live"**

### 4.5. Verificar Backend

```bash
# Health Check
curl https://tutodo-backend-1.onrender.com/actuator/health

# Debe responder:
{"status":"UP"}

# Probar API
curl https://tutodo-backend-1.onrender.com/api/categorias
```

---

## 5. Despliegue del Frontend (Vercel)

### 5.1. Crear Cuenta en Vercel

1. Ve a https://vercel.com
2. Regístrate con tu cuenta de GitHub
3. Autoriza a Vercel para acceder a tus repositorios

### 5.2. Crear Proyecto

1. Click en **"Add New..."** → **"Project"**
2. Selecciona el repositorio: `Gladbat/tutodo-frontend`
3. Click en **"Import"**

### 5.3. Configuración del Proyecto

Vercel detectará automáticamente Angular:

- **Framework Preset**: Angular
- **Build Command**: `npm run build:prod`
- **Output Directory**: `dist/tutodo-frontend/browser`
- **Install Command**: `npm install`

### 5.4. Deploy

1. Click en **"Deploy"**
2. Vercel compilará la aplicación (3-5 minutos)
3. Una vez completado, obtendrás una URL como:
   - `https://tutodo-frontend.vercel.app`

### 5.5. Verificar Frontend

1. Abre la URL en el navegador
2. Verifica que:
   - ✅ La página carga correctamente
   - ✅ Los productos se muestran
   - ✅ Puedes registrarte/iniciar sesión
   - ✅ Las imágenes cargan desde Supabase

---

## 6. Configuración de CORS

El backend debe permitir peticiones desde el frontend en Vercel.

**Archivo: `src/main/java/com/tutodo/backend/config/CorsConfig.java`**

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200", 
            "http://localhost:*",
            "https://tutodo-frontend.vercel.app",
            "https://*.vercel.app",
            "https://*.up.railway.app",
            "https://*.netlify.app"
        ));
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

---

## 7. Variables de Entorno

### 7.1. Backend (Render)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATABASE_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://...` |
| `DATABASE_USERNAME` | Usuario de la base de datos | `postgres.xxxxx` |
| `DATABASE_PASSWORD` | Contraseña de la base de datos | `tu-password` |
| `SUPABASE_URL` | URL del proyecto Supabase | `https://xxx.supabase.co` |
| `SUPABASE_SERVICE_ROLE_KEY` | Service Role Key de Supabase | `eyJhbGci...` |
| `PORT` | Puerto del servidor | `8080` |

### 7.2. Frontend (Vercel)

No requiere variables de entorno adicionales. La URL del backend está configurada en `environment.prod.ts`.

---

## 8. Verificación del Despliegue

### 8.1. Checklist de Verificación

- [ ] Backend responde en `/actuator/health`
- [ ] Backend responde en `/api/categorias`
- [ ] Frontend carga correctamente
- [ ] Productos se muestran en el home
- [ ] Registro de usuarios funciona
- [ ] Login funciona
- [ ] Crear producto funciona
- [ ] Subir imágenes funciona
- [ ] Búsqueda funciona
- [ ] Filtros por categoría funcionan
- [ ] Favoritos funcionan
- [ ] Panel de administración funciona (si eres admin)

### 8.2. Pruebas de Integración

```bash
# 1. Verificar backend
curl https://tutodo-backend-1.onrender.com/actuator/health

# 2. Obtener categorías
curl https://tutodo-backend-1.onrender.com/api/categorias

# 3. Obtener productos
curl https://tutodo-backend-1.onrender.com/api/productos

# 4. Verificar frontend
# Abrir en navegador: https://tutodo-frontend.vercel.app
```

---

## 9. Consideraciones de Rendimiento

### 9.1. Plan Gratuito de Render

**Limitaciones:**
- El servicio se "duerme" después de 15 minutos de inactividad
- La primera petición después de dormir tarda 30-60 segundos
- 750 horas de uso gratuito por mes

**Soluciones:**
1. **Warming Service**: Configurar un servicio externo (como UptimeRobot) para hacer ping cada 10 minutos
2. **Mensaje al Usuario**: Mostrar un mensaje indicando que la primera carga puede tardar
3. **Upgrade a Plan Pago**: $7/mes para servicio siempre activo

### 9.2. Optimización de Imágenes

El backend comprime automáticamente las imágenes antes de subirlas a Supabase:

```java
@Service
public class ImageCompressionService {
    public byte[] compressImage(MultipartFile file) throws IOException {
        // Compresión a 800x800px, calidad 0.85
        // Reduce tamaño promedio en 70-80%
    }
}
```

### 9.3. Caché del Frontend

Vercel proporciona CDN global automático:
- Assets estáticos cacheados en edge locations
- Tiempo de carga < 1 segundo en todo el mundo
- Invalidación automática en cada deploy

---

## 10. Troubleshooting

### 10.1. Backend no responde (Error 500)

**Síntomas:**
- `/actuator/health` responde con error 500
- Logs muestran errores de conexión a base de datos

**Soluciones:**
1. Verificar credenciales de Supabase en variables de entorno
2. Verificar que la IP de Render esté permitida en Supabase
3. Revisar logs en Render para errores específicos

```bash
# Ver logs en Render
# Dashboard → Tu servicio → Logs
```

### 10.2. Frontend muestra "localhost:8080"

**Síntomas:**
- Errores de CORS en consola
- Peticiones a `http://localhost:8080`

**Soluciones:**
1. Verificar que `environment.prod.ts` tenga la URL correcta
2. Verificar que `angular.json` tenga `fileReplacements` configurado
3. Hacer redeploy en Vercel
4. Limpiar caché del navegador (Ctrl + Shift + R)

### 10.3. Backend se queda "dormido"

**Síntomas:**
- Primera petición tarda 30-60 segundos
- Después funciona normal

**Soluciones:**
1. **Warming Service** (Recomendado):
   - Usar UptimeRobot (gratuito)
   - Configurar ping cada 10 minutos a `/actuator/health`
   
2. **Mensaje al Usuario**:
   ```typescript
   // Mostrar loading con mensaje
   "Iniciando servidor... Esto puede tardar hasta 1 minuto"
   ```

3. **Upgrade a Plan Pago**: $7/mes en Render

### 10.4. Imágenes no cargan

**Síntomas:**
- Productos sin imágenes
- Error 403 o 404 en URLs de Supabase

**Soluciones:**
1. Verificar que el bucket `imagenes-productos` sea público
2. Verificar `SUPABASE_SERVICE_ROLE_KEY` en variables de entorno
3. Verificar permisos del bucket en Supabase

### 10.5. Error de CORS

**Síntomas:**
- Error en consola: "CORS policy: No 'Access-Control-Allow-Origin'"

**Soluciones:**
1. Verificar `CorsConfig.java` incluye la URL de Vercel
2. Hacer redeploy del backend en Render
3. Verificar que el frontend use HTTPS (no HTTP)

---

## 11. Mantenimiento y Actualizaciones

### 11.1. Actualizar Backend

```bash
cd tutodo-backend
# Hacer cambios en el código
git add .
git commit -m "Update: Descripción del cambio"
git push origin main
# Render detectará el cambio y hará redeploy automático
```

### 11.2. Actualizar Frontend

```bash
cd tutodo-frontend
# Hacer cambios en el código
git add .
git commit -m "Update: Descripción del cambio"
git push origin main
# Vercel detectará el cambio y hará redeploy automático
```

### 11.3. Rollback

**En Render:**
1. Dashboard → Tu servicio → Deployments
2. Click en un deployment anterior
3. Click en "Redeploy"

**En Vercel:**
1. Dashboard → Tu proyecto → Deployments
2. Click en un deployment anterior
3. Click en "Promote to Production"

---

## 12. Costos y Escalabilidad

### 12.1. Plan Actual (Gratuito)

| Servicio | Plan | Costo | Limitaciones |
|----------|------|-------|--------------|
| Render | Free | $0/mes | 750 horas, se duerme |
| Vercel | Hobby | $0/mes | 100 GB bandwidth |
| Supabase | Free | $0/mes | 500 MB database, 1 GB storage |

**Total: $0/mes**

### 12.2. Plan Recomendado para Producción

| Servicio | Plan | Costo | Beneficios |
|----------|------|-------|------------|
| Render | Starter | $7/mes | Siempre activo, 512 MB RAM |
| Vercel | Pro | $20/mes | 1 TB bandwidth, analytics |
| Supabase | Pro | $25/mes | 8 GB database, 100 GB storage |

**Total: $52/mes**

---

## 13. URLs y Recursos

### 13.1. URLs de Producción

- **Frontend**: https://tutodo-frontend.vercel.app
- **Backend**: https://tutodo-backend-1.onrender.com
- **API Health**: https://tutodo-backend-1.onrender.com/actuator/health
- **API Categorías**: https://tutodo-backend-1.onrender.com/api/categorias

### 13.2. Dashboards

- **Render**: https://dashboard.render.com
- **Vercel**: https://vercel.com/dashboard
- **Supabase**: https://supabase.com/dashboard

### 13.3. Repositorios

- **Backend**: https://github.com/Gladbat/tutodo-backend
- **Frontend**: https://github.com/Gladbat/tutodo-frontend

---

## 14. Contacto y Soporte

**Email de Soporte Técnico**: soportetecnicotutodo@gmail.com

**Documentación Adicional**:
- `DOCUMENTACION_TECNICA_FINAL.md` - Documentación técnica completa
- `RENDER_DEPLOYMENT_GUIDE.md` - Guía específica de Render
- `VERCEL_DEPLOYMENT_GUIDE.md` - Guía específica de Vercel

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Autor**: Equipo Tutodo
