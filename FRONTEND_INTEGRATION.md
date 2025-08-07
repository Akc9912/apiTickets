# 🎯 Frontend Integration Guide - API Tickets

## 📋 Información Esencial

### 🔗 URLs del Sistema

- **API Base**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`
- **OpenAPI Docs**: `http://localhost:8080/api-docs`

### 🛡️ Autenticación JWT

```javascript
// Headers requeridos para todas las requests (excepto login)
const headers = {
  Authorization: `Bearer ${token}`,
  "Content-Type": "application/json",
};
```

---

## 🚀 Quick Start para Desarrolladores Frontend

### 1. Setup Inicial

```javascript
// Configuración base del cliente HTTP
const API_BASE_URL = "http://localhost:8080/api";

// Axios setup (recomendado)
import axios from "axios";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para JWT automático
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 2. Servicio de Autenticación

```javascript
const authService = {
  async login(email, password) {
    const response = await apiClient.post("/auth/login", { email, password });

    // Guardar token y usuario
    localStorage.setItem("authToken", response.data.token);
    localStorage.setItem("user", JSON.stringify(response.data.usuario));

    return response.data;
  },

  logout() {
    localStorage.removeItem("authToken");
    localStorage.removeItem("user");
  },

  getCurrentUser() {
    return JSON.parse(localStorage.getItem("user") || "null");
  },

  isAuthenticated() {
    return !!localStorage.getItem("authToken");
  },
};
```

### 3. Servicio de Tickets

```javascript
const ticketService = {
  // Obtener todos los tickets
  async getTickets() {
    const response = await apiClient.get("/tickets");
    return response.data;
  },

  // Crear nuevo ticket
  async createTicket(ticketData) {
    const response = await apiClient.post("/tickets", ticketData);
    return response.data;
  },

  // Obtener ticket por ID
  async getTicketById(id) {
    const response = await apiClient.get(`/tickets/${id}`);
    return response.data;
  },

  // Actualizar ticket
  async updateTicket(id, updates) {
    const response = await apiClient.put(`/tickets/${id}`, updates);
    return response.data;
  },

  // Eliminar ticket
  async deleteTicket(id) {
    await apiClient.delete(`/tickets/${id}`);
  },
};
```

---

## 📱 Componentes UI Recomendados

### Login Component (React ejemplo)

```jsx
import React, { useState } from "react";
import { authService } from "../services/api";

const LoginComponent = ({ onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const result = await authService.login(
        credentials.email,
        credentials.password
      );
      onLoginSuccess(result.usuario);
    } catch (err) {
      setError(err.response?.data?.message || "Error de autenticación");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        placeholder="Email"
        value={credentials.email}
        onChange={(e) =>
          setCredentials({ ...credentials, email: e.target.value })
        }
        required
      />
      <input
        type="password"
        placeholder="Password"
        value={credentials.password}
        onChange={(e) =>
          setCredentials({ ...credentials, password: e.target.value })
        }
        required
      />
      <button type="submit" disabled={loading}>
        {loading ? "Iniciando sesión..." : "Iniciar Sesión"}
      </button>
      {error && <div className="error">{error}</div>}
    </form>
  );
};
```

### Ticket List Component

```jsx
import React, { useState, useEffect } from "react";
import { ticketService } from "../services/api";

const TicketList = () => {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    try {
      const data = await ticketService.getTickets();
      setTickets(data);
    } catch (error) {
      console.error("Error loading tickets:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Cargando tickets...</div>;

  return (
    <div className="ticket-list">
      <h2>Tickets</h2>
      {tickets.map((ticket) => (
        <div key={ticket.id} className="ticket-item">
          <h3>{ticket.titulo}</h3>
          <p>{ticket.descripcion}</p>
          <span className={`priority ${ticket.prioridad.toLowerCase()}`}>
            {ticket.prioridad}
          </span>
          <span className={`status ${ticket.estado.toLowerCase()}`}>
            {ticket.estado}
          </span>
        </div>
      ))}
    </div>
  );
};
```

---

## 🎨 Estados y Estilos CSS

### Estados de Tickets

```css
.status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
}

.status.abierto {
  background: #e3f2fd;
  color: #1976d2;
}
.status.en_progreso {
  background: #fff3e0;
  color: #f57c00;
}
.status.resuelto {
  background: #e8f5e8;
  color: #388e3c;
}
.status.cerrado {
  background: #f3e5f5;
  color: #7b1fa2;
}
```

### Prioridades

```css
.priority.baja {
  background: #f1f8e9;
  color: #689f38;
}
.priority.media {
  background: #fff8e1;
  color: #ffa000;
}
.priority.alta {
  background: #ffebee;
  color: #d32f2f;
}
.priority.critica {
  background: #ffebee;
  color: #c62828;
  font-weight: bold;
}
```

---

## 🔄 Manejo de Estados (Context/Redux)

### React Context Example

```jsx
import React, { createContext, useContext, useReducer } from "react";

const AppContext = createContext();

const initialState = {
  user: null,
  isAuthenticated: false,
  tickets: [],
  loading: false,
  error: null,
};

const appReducer = (state, action) => {
  switch (action.type) {
    case "LOGIN_SUCCESS":
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
        error: null,
      };
    case "LOGOUT":
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        tickets: [],
      };
    case "SET_TICKETS":
      return {
        ...state,
        tickets: action.payload,
      };
    case "SET_LOADING":
      return {
        ...state,
        loading: action.payload,
      };
    case "SET_ERROR":
      return {
        ...state,
        error: action.payload,
      };
    default:
      return state;
  }
};

export const AppProvider = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState);

  return (
    <AppContext.Provider value={{ state, dispatch }}>
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error("useApp must be used within AppProvider");
  }
  return context;
};
```

---

## 🛣️ Routing (React Router ejemplo)

```jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useApp } from "./context/AppContext";

const PrivateRoute = ({ children }) => {
  const { state } = useApp();
  return state.isAuthenticated ? children : <Navigate to="/login" />;
};

const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/tickets"
          element={
            <PrivateRoute>
              <TicketsPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/tickets/:id"
          element={
            <PrivateRoute>
              <TicketDetailPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/users"
          element={
            <PrivateRoute>
              <UsersPage />
            </PrivateRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
};
```

---

## 📊 Validación de Formularios

### Yup Schema Example

```javascript
import * as yup from "yup";

export const ticketSchema = yup.object({
  titulo: yup
    .string()
    .required("El título es requerido")
    .min(5, "El título debe tener al menos 5 caracteres")
    .max(100, "El título no puede exceder 100 caracteres"),

  descripcion: yup
    .string()
    .required("La descripción es requerida")
    .min(10, "La descripción debe tener al menos 10 caracteres"),

  prioridad: yup
    .string()
    .oneOf(["BAJA", "MEDIA", "ALTA", "CRITICA"], "Prioridad inválida")
    .required("La prioridad es requerida"),

  categoria: yup
    .string()
    .oneOf(["SOFTWARE", "HARDWARE", "NETWORK", "OTHER"], "Categoría inválida")
    .required("La categoría es requerida"),
});

export const userSchema = yup.object({
  nombre: yup
    .string()
    .required("El nombre es requerido")
    .min(2, "El nombre debe tener al menos 2 caracteres"),

  email: yup.string().email("Email inválido").required("El email es requerido"),

  password: yup
    .string()
    .min(6, "La contraseña debe tener al menos 6 caracteres")
    .required("La contraseña es requerida"),

  rol: yup
    .string()
    .oneOf(["TRABAJADOR", "TECNICO", "ADMIN"], "Rol inválido")
    .required("El rol es requerido"),
});
```

---

## 🔔 Notificaciones en Tiempo Real

### Setup con Socket.io (si se implementa)

```javascript
import io from "socket.io-client";

const socket = io("http://localhost:8080", {
  auth: {
    token: localStorage.getItem("authToken"),
  },
});

socket.on("ticket-updated", (ticket) => {
  // Actualizar estado del ticket en la UI
  updateTicketInState(ticket);
});

socket.on("notification", (notification) => {
  // Mostrar notificación al usuario
  showNotification(notification);
});
```

---

## 📱 Responsive Design Consideraciones

### Breakpoints Recomendados

```css
/* Mobile First */
.container {
  padding: 16px;
}

/* Tablet */
@media (min-width: 768px) {
  .container {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* Desktop */
@media (min-width: 1024px) {
  .ticket-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 16px;
  }
}
```

---

## 🧪 Testing del Frontend

### Test de Servicios

```javascript
import { describe, it, expect, beforeEach } from "vitest";
import { authService } from "../services/api";

describe("AuthService", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("should login successfully", async () => {
    const result = await authService.login("admin@example.com", "123456");

    expect(result).toHaveProperty("token");
    expect(result).toHaveProperty("usuario");
    expect(result.usuario.email).toBe("admin@example.com");
  });

  it("should store token in localStorage", async () => {
    await authService.login("admin@example.com", "123456");

    expect(localStorage.getItem("authToken")).toBeTruthy();
    expect(localStorage.getItem("user")).toBeTruthy();
  });
});
```

---

## 🔧 Variables de Entorno

### .env file

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_SOCKET_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
REACT_APP_VERSION=1.0.0
```

### Config file

```javascript
export const config = {
  apiBaseUrl: process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api",
  socketUrl: process.env.REACT_APP_SOCKET_URL || "http://localhost:8080",
  environment: process.env.REACT_APP_ENVIRONMENT || "development",
  version: process.env.REACT_APP_VERSION || "1.0.0",
};
```

---

## 📦 Dependencias Recomendadas

### React Project

```json
{
  "dependencies": {
    "axios": "^1.6.0",
    "react-router-dom": "^6.8.0",
    "react-hook-form": "^7.43.0",
    "yup": "^1.0.0",
    "@hookform/resolvers": "^3.0.0",
    "react-toastify": "^9.1.0",
    "date-fns": "^2.29.0"
  },
  "devDependencies": {
    "vitest": "^0.28.0",
    "@testing-library/react": "^13.4.0"
  }
}
```

### Vue Project

```json
{
  "dependencies": {
    "axios": "^1.6.0",
    "vue-router": "^4.1.0",
    "pinia": "^2.0.0",
    "vee-validate": "^4.7.0",
    "yup": "^1.0.0",
    "vue-toastification": "^2.0.0"
  }
}
```

---

## 🚀 Deployment Considerations

### Build para Producción

```javascript
// config/production.js
export const productionConfig = {
  apiBaseUrl: "https://api.tuempresa.com/api",
  socketUrl: "https://api.tuempresa.com",
  timeout: 30000,
  retryAttempts: 3,
};
```

### CORS en Producción

Asegurar que el backend tenga configurado CORS para el dominio de producción.

---

## 📧 Soporte y Recursos

### Enlaces Útiles

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`
- **Health Check**: `http://localhost:8080/actuator/health`

### Usuarios de Prueba

- **SuperAdmin**: `admin@example.com` / `123456`
- **Técnico**: `tecnico@example.com` / `123456`
- **Trabajador**: `trabajador@example.com` / `123456`

---

_Esta guía proporciona todo lo necesario para que el equipo de frontend comience a trabajar de forma independiente con la API._
