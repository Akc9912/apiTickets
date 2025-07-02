

export default function Boton({ children, className = "", ...props }) {
    return (
      <button
        className={`bg-blue-600 text-white rounded px-4 py-2 ${className} hover:bg-blue-500 `}
        {...props}
      >
        {children}
      </button>
    );
  }