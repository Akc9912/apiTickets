export default function ListaUsuarios() {
  return (
    <div class="overflow-x-auto shadow-md border border-gray-200 rounded-lg mt-10 mx-6 lg:mx-20 p-4">
      <div class="pb-4 bg-white">
        <div class="relative mt-1">
          <div class="absolute inset-y-0 rtl:inset-r-0 start-0 flex items-center ps-3 pointer-events-none">
            <svg
              class="w-4 h-4 text-gray-500"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 20 20"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
              />
            </svg>
          </div>
          <input
            type="text"
            id="table-search"
            class="block ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg w-80 bg-gray-50 focus:ring-blue-500 focus:border-blue-500 h-8"
            placeholder="Buscar Usuario"
          />
        </div>
      </div>
      <table class="w-full text-sm text-left rtl:text-right text-gray-500 shadow-md border border-gray-200">
        <thead class="text-xs text-gray-800 uppercase  bg-zinc-300">
          <tr className="border-b border-gray-200">
            <th scope="col" class="px-6 py-3">
              Nombre
            </th>
            <th scope="col" class="px-6 py-3">
              Apellido
            </th>
            <th scope="col" class="px-6 py-3">
              Correo electronico
            </th>
            <th scope="col" class="px-6 py-3">
              Id
            </th>
            <th scope="col" class="px-6 py-3">
              Estado
            </th>
            <th scope="col" class="px-6 py-3">
                Acciones
            </th>
          </tr>
        </thead>
        <tbody className="">
          <tr class="bg-white border-b border-gray-200 rounded-lg">
            <th
              scope="row"
              class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap "
            >
              Lucia
            </th>
            <td class="px-6 py-4">Fernendez</td>
            <td class="px-6 py-4">lucia.fernandez@example.com</td>
            <td class="px-6 py-4">1001</td>
            <td class="px-6 py-4">Bloqueado</td>
            <td class="px-6 py-4">
              <a href="#" class="font-medium text-indigo-600 ">
              <button className="bg-blue-600 rounded-md text-white py-1 px-2 cursor-pointer hover:bg-blue-500">Editar</button>
              </a>
            </td>
          </tr>
          <tr class="bg-white border-b border-gray-200 rounded-lg">
            <th
              scope="row"
              class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap "
            >
              	Tomás
            </th>
            <td class="px-6 py-4">Aguirre</td>
            <td class="px-6 py-4">tomas.aguirre@example.com</td>
            <td class="px-6 py-4">1002</td>
            <td class="px-6 py-4">Bloqueado</td>
            <td class="px-6 py-4">
              <a href="#" class="font-medium text-indigo-600 ">
                <button className="bg-blue-600 rounded-md text-white py-1 px-2 cursor-pointer hover:bg-blue-500">Editar</button>
              </a>
            </td>
          </tr>
          <tr class="bg-white border-b border-gray-200 rounded-lg">
            <th
              scope="row"
              class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap "
            >
              	Mateo
            </th>
            <td class="px-6 py-4">Ramírez</td>
            <td class="px-6 py-4">mateo.ramirez@example.com</td>
            <td class="px-6 py-4">1003</td>
            <td class="px-6 py-4">Bloqueado</td>
            <td class="px-6 py-4">
              <a href="#" class="font-medium text-indigo-600 ">
              <button className="bg-blue-600 rounded-md text-white py-1 px-2 cursor-pointer hover:bg-blue-500 ">Editar</button>
              </a>
            </td>
          </tr>
        </tbody>
      </table>
      
    </div>
  );
}
