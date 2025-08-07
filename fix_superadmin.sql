-- Script para solucionar el problema del discriminador del SuperAdmin
USE ticket_system;

-- Mostrar los usuarios actuales
SELECT id, nombre, apellido, email, tipo_usuario, rol FROM usuario;

-- Actualizar el discriminador del SuperAdmin existente
UPDATE usuario 
SET tipo_usuario = 'SUPERADMIN' 
WHERE tipo_usuario = 'SUPER_ADMIN' AND email = 'superadmin@sistema.com';

-- Verificar que se actualizó correctamente
SELECT id, nombre, apellido, email, tipo_usuario, rol FROM usuario WHERE email = 'superadmin@sistema.com';

-- Si no hay ningún usuario, esto mostrará que está vacía la tabla
SELECT COUNT(*) as total_usuarios FROM usuario;
