package com.poo.miapi.model.enums;

public enum EstadoSolicitud {
        PENDIENTE("Pendiente"),
        APROBADO("Aprobado"),
        RECHAZADO("Rechazado");

        private final String displayName;

        EstadoSolicitud(String displayName) {
                this.displayName = displayName;
        }

        // Nombre estándar para el front (mayúsculas, igual al nombre del enum)
        public String getName() {
                return name();
        }

        // Nombre legible para mostrar en UI
        public String getDisplayName() {
                return displayName;
        }

        // Valor original del enum (para compatibilidad)
        public String getValue() {
                return name();
        }

        @Override
        public String toString() {
                return name();
        }
}
