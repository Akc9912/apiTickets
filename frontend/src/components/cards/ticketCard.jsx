
import React, { useState } from 'react';
import Boton  from '../buttons/boton';

// Definimos el componente funcional 'TicketCard', que recibe un objeto 'ticket' como prop.
export default function TicketCard({ ticket }) {

    // Estado local para controlar si la tarjeta está expandida o no.
    const [expandido, setExpandido] = useState(false);
  
    // Función que alterna el estado de expansión del ticket.
    const toggleExpandir = () => {
      setExpandido(!expandido);
    };
  
    // JSX que representa la tarjeta visual del ticket
    return (
        <div className="bg-white shadow-md rounded-lg p-4 m-3 w-full max-w-xs min-w-xs border border-gray-200 
                hover:shadow-[0_4px_20px_rgba(59,130,246,0.4)] transition duration-200 ease-in-out cursor-pointer">


        {/* Sección de vista previa (información básica del ticket) */}
        <div>
          {/* Título del ticket */}
          <h3 className="text-lg font-semibold text-gray-800 mb-1">{ticket.titulo}</h3>
  
          {/* Estado del ticket */}
          <p className="text-sm text-gray-600">
            <span className="font-medium">Estado:</span> {ticket.estado}
          </p>
          
          {/* Nombre del trabajador que lo creó */}
          <p className="text-sm text-gray-600">
            <span className="font-medium">Trabajador:</span> {ticket.trabajadorCreador}
          </p>
  
          {/* Si el ticket ya tiene un técnico asignado, mostramos su nombre */}
          {ticket.tecnicoAsignado && (
            <p className="text-sm text-gray-600">
              <span className="font-medium">Técnico:</span> {ticket.tecnicoAsignado}
            </p>
          )}
  
          {/* Botón para expandir u ocultar los detalles del ticket */}
          
          <Boton
            onClick={toggleExpandir} // Llama a la función para alternar la expansión
            className="mt-3 text-blue-600 hover:underline text-sm font-medium cursor-pointer"
          >
            {expandido ? 'Ocultar detalles' : 'Ver más'}
          </Boton>
        </div>
  
        {/* Sección expandible con los detalles completos del ticket */}
        {expandido && (
          <div className="mt-3 border-t pt-2 text-sm text-gray-700">
            {/* Descripción larga del ticket */}
            <p>
              <span className="font-medium">Descripción:</span> {ticket.descripcion}
            </p>
  
            {/* Fecha de creación del ticket */}
            <p>
              <span className="font-medium">Fecha de creación:</span> {ticket.fechaCreacion}
            </p>
  
            {/* Si el ticket fue reabierto, mostramos más información */}
            {ticket.fueReabierto && (
              <>
                <p>
                  <span className="font-medium">Reabierto:</span> Sí
                </p>
                <p>
                  <span className="font-medium">Técnico anterior:</span> {ticket.tecnicoAnterior}
                </p>
              </>
            )}
          </div>
        )}
      </div>
    );
}