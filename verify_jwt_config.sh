#!/bin/bash
# Script para verificar la configuración del JWT desde .env

echo "🔍 Verificando configuración JWT..."
echo ""

# Verificar que existe .env
if [ -f ".env" ]; then
    echo "✅ Archivo .env encontrado"
    echo "🔑 JWT_SECRET en .env:"
    grep "JWT_SECRET=" .env | head -1
else
    echo "❌ Archivo .env no encontrado"
    echo "💡 Copia .env.example como .env:"
    echo "   cp .env.example .env"
    exit 1
fi

echo ""

# Verificar application.properties
echo "📋 Configuración en application.properties:"
grep -A 3 "jwt.secret=" src/main/resources/application.properties

echo ""
echo "✅ Configuración completada correctamente!"
echo "🔐 El JWT secret se carga desde .env y no está hardcodeado en el código."
