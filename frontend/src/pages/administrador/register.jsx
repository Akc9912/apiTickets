import Header from "../../layouts/header";
import HeaderAdm from "./headerAdm";


export default function RegisterPage() {
  return (
  <>
    <HeaderAdm />
    <div className=" flex flex-col items-center mt-8 min-h-230">
      
      <form className="border border-gray-200 rounded-lg shadow-md">
      
        <div className="flex flex-col items-center px-10">
          <div className="flex items-start justify-start w-full ">
            <h2 className="text-base/7 font-semibold mt-7 text-xl">Registrar usuario</h2>
          </div> 
          <div className="">
            <div className="mt-10 grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6">
              <div className="sm:col-span-3">
                <label htmlFor="nombre" className="block text-sm/6 font-medium text-gray-900">
                  Nombre completo
                </label>
                <div className="mt-2">
                  <input
                    id="nombre"
                    name="nombre"
                    type="text"
                    autoComplete="given-name"
                    className="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
                  />
                </div>
              </div>

              <div className="sm:col-span-3">
                <label htmlFor="apellido" className="block text-sm/6 font-medium text-gray-900">
                  Apellido
                </label>
                <div className="mt-2">
                  <input
                    id="apellido"
                    name="apellido"
                    type="text"
                    autoComplete="family-name"
                    className="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
                  />
                </div>
              </div>

              <div className="sm:col-span-4">
                <label htmlFor="mail" className="block text-sm/6 font-medium text-gray-900">
                  Correo electronico
                </label>
                <div className="mt-2">
                  <input
                    id="mail"
                    name="mail"
                    type="email"
                    autoComplete="email"
                    className="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
                  />
                </div>
              </div>

              <div className="sm:col-span-3">
                <label htmlFor="id" className="block text-sm/6 font-medium text-gray-900">
                  ID
                </label>
                <div className="mt-2">
                  <input
                    id="idUsuario"
                    name="idUsuario"
                    type="text"
                    className="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
                  />
                </div>
              </div>

            </div>
          </div>

          
        </div>
        <div className="rolUsuarioChecks px-10 pb-10 border-b border-gray-900/10 ">
            <div className="mt-10 space-y-10">
              <fieldset>
                <legend className="text-sm/6 font-semibold text-gray-900">Rol del Usuario</legend>
                <p className="mt-1 text-sm/6 text-gray-600">Seleccione el tipo</p>
                <div className="mt-6 space-y-6">
                  <div className="flex items-center gap-x-3">
                    <input
                      defaultChecked
                      id="trabajador"
                      name="rolUsuario"
                      type="radio"
                      className="relative size-4 appearance-none rounded-full border border-gray-300 bg-white before:absolute before:inset-1 before:rounded-full before:bg-white not-checked:before:hidden checked:border-blue-600 checked:bg-blue-600 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:border-gray-300 disabled:bg-gray-100 disabled:before:bg-gray-400 forced-colors:appearance-auto forced-colors:before:hidden"
                    />
                    <label htmlFor="trabajador" className="block text-sm/6 font-medium text-gray-900">
                      trabajador
                    </label>
                  </div>
                  <div className="flex items-center gap-x-3">
                    <input
                      id="tecnico"
                      name="rolUsuario"
                      type="radio"
                      className="relative size-4 appearance-none rounded-full border border-gray-300 bg-white before:absolute before:inset-1 before:rounded-full before:bg-white not-checked:before:hidden checked:border-blue-600 checked:bg-blue-600 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:border-gray-300 disabled:bg-gray-100 disabled:before:bg-gray-400 forced-colors:appearance-auto forced-colors:before:hidden"
                    />
                    <label htmlFor="tecnico" className="block text-sm/6 font-medium text-gray-900">
                      Tecnico
                    </label>
                  </div>
                  
                </div>
              </fieldset>
            </div>
          </div>
        <div className="m-6 flex items-center justify-end gap-x-6">
          <button type="button" className="text-sm/6 font-semibold text-gray-900 hover:text-gray-400 cursor-pointer">
            Cancel
          </button>
          <button
            type="submit"
            className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold text-white shadow-xs hover:bg-blue-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 cursor-pointer"
          >
            Save
          </button>
        </div>
      </form>
    </div>
  </>
  )
}
