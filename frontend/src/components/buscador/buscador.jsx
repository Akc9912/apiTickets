


export default function Buscador() {
    return (
        <form className="max-w-md mx-auto">
            {/* Etiqueta oculta para accesibilidad */}
            <label htmlFor="default-search" className="mb-2 text-sm font-medium text-gray-900 sr-only">
                Buscar
            </label>

            <div className="relative">
                {/* Convertimos el contenedor del ícono en un botón */}
                <button
                    type="submit"
                    className="absolute inset-y-0 start-0 flex items-center ps-3 text-gray-500 hover:text-gray-700 cursor-pointer"
                    aria-label="Buscar"
                >
                    <svg
                        className="w-5 h-5"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 20 20"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                        />
                    </svg>
                </button>

                {/* Input de búsqueda */}
                <input
                    type="search"
                    id="default-search"
                    className="block w-full p-4 ps-10 text-sm text-gray-900 border-1 border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Buscar tickets..."
                    required
                />
            </div>
        </form>
    );
}