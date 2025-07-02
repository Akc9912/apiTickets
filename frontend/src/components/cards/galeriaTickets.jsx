import React from "react";
import TicketCard from "./ticketCard";
import Buscador from "../buscador/buscador";

const tickets = [
  {
    id: 1,
    titulo: "No funciona la impresora",
    descripcion: "La impresora HP no imprime desde ayer...",
    estado: "No atendido",
    trabajadorCreador: "Juan Pérez",
    tecnicoAsignado: null,
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-20 10:15",
  },
  {
    id: 2,
    titulo: "Problemas de conexión",
    descripcion: "Se corta el WiFi en mi oficina a cada rato...",
    estado: "Atendido",
    trabajadorCreador: "Lucía Torres",
    tecnicoAsignado: "Carlos Gómez",
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-19 14:42",
  },
  {
    id: 2,
    titulo: "Problemas de conexión",
    descripcion: "Se corta el WiFi en mi oficina a cada rato...",
    estado: "Atendido",
    trabajadorCreador: "Lucía Torres",
    tecnicoAsignado: "Carlos Gómez",
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-19 14:42",
  },
  {
    id: 2,
    titulo: "Problemas de conexión",
    descripcion: "Se corta el WiFi en mi oficina a cada rato...",
    estado: "Atendido",
    trabajadorCreador: "Lucía Torres",
    tecnicoAsignado: "Carlos Gómez",
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-19 14:42",
  },
  {
    id: 2,
    titulo: "Problemas de conexión",
    descripcion: "Se corta el WiFi en mi oficina a cada rato...",
    estado: "Atendido",
    trabajadorCreador: "Lucía Torres",
    tecnicoAsignado: "Carlos Gómez",
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-19 14:42",
  },
  {
    id: 2,
    titulo: "Problemas de conexión",
    descripcion: "Se corta el WiFi en mi oficina a cada rato...",
    estado: "Atendido",
    trabajadorCreador: "Lucía Torres",
    tecnicoAsignado: "Carlos Gómez",
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-19 14:42",
  },
  {
    id: 2,
    titulo: "Problemas de conexión",
    descripcion: "Se corta el WiFi en mi oficina a cada rato...",
    estado: "Atendido",
    trabajadorCreador: "Lucía Torres",
    tecnicoAsignado: "Carlos Gómez",
    tecnicoAnterior: null,
    fueReabierto: false,
    fechaCreacion: "2025-04-19 14:42",
  },
  // otros tickets...
];

export default function GaleriaDeTickets() {
  return (
    <>
        <div className="mt-10">
        <Buscador ></Buscador>
        </div>
        
        <div className="flex justify-center items-center mt-10">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {tickets.map((ticket) => (
            <TicketCard key={ticket.id} ticket={ticket} />
            ))}
        </div>
        </div>
    </>
  );
}
