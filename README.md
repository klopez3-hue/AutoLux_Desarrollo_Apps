# AutoLux – Concesionario Premium

Aplicación móvil desarrollada en Kotlin y Jetpack Compose para un concesionario de vehículos de lujo. AutoLux permite explorar un catálogo de vehículos, guardar favoritos, agendar pruebas de manejo y consultar solicitudes registradas.

## Objetivo del proyecto

Desarrollar una aplicación móvil que mejore la experiencia de los clientes de un concesionario premium, facilitando la consulta de vehículos y la gestión de solicitudes de prueba de manejo mediante una interfaz intuitiva y almacenamiento local persistente.

## Tecnologías utilizadas

- Kotlin.
- Android Studio.
- Jetpack Compose.
- Material 3.
- Navigation Compose.
- Room Database.
- ViewModel.
- Kotlin Flow / StateFlow.

## Pantallas principales

- **Inicio:** presenta el concesionario, vehículos destacados y categorías.
- **Catálogo:** permite explorar los vehículos disponibles y consultar sus características.
- **Favoritos:** muestra los vehículos guardados por el usuario.
- **Perfil:** permite consultar la próxima prueba de manejo, acceder a las solicitudes y visualizar los vehículos favoritos.
- **Prueba de manejo:** permite registrar una solicitud seleccionando vehículo, fecha y horario.
- **Mis solicitudes:** permite consultar, editar y eliminar solicitudes registradas.
- **Financiación:** permite realizar una simulación de financiación para los vehículos.

## Funcionalidades implementadas

### 1. Registro de solicitudes de prueba de manejo

El usuario puede seleccionar un vehículo y registrar una solicitud proporcionando sus datos personales y de contacto, además de una fecha y un horario.

**Pasos para probar:**
1. Ingresar al catálogo.
2. Seleccionar un vehículo.
3. Acceder a la opción de agendar prueba de manejo.
4. Completar los datos solicitados.
5. Seleccionar una fecha y un horario disponible.
6. Registrar la solicitud.

### 2. Consulta de solicitudes

Las solicitudes registradas se almacenan en Room y pueden consultarse desde el perfil del usuario.

**Pasos para probar:**
1. Ingresar a Perfil.
2. Seleccionar "Mis solicitudes de prueba de manejo".
3. Consultar las solicitudes registradas y sus respectivos datos.

### 3. Edición de solicitudes

El usuario puede modificar los datos de una solicitud existente, incluyendo nombre, teléfono, correo electrónico, fecha y horario.

**Pasos para probar:**
1. Ingresar a Perfil.
2. Abrir "Mis solicitudes de prueba de manejo".
3. Seleccionar la opción de editar una solicitud.
4. Modificar los datos necesarios.
5. Guardar los cambios.

### 4. Eliminación de solicitudes

La aplicación permite cancelar y eliminar solicitudes previamente registradas.

**Pasos para probar:**
1. Ingresar a "Mis solicitudes de prueba de manejo".
2. Seleccionar la opción de cancelar una solicitud.
3. Confirmar la eliminación.

### 5. Gestión de disponibilidad de horarios

La aplicación consulta las solicitudes almacenadas en Room para identificar los horarios que ya están reservados. Cuando un horario se encuentra ocupado, deja de aparecer entre las opciones disponibles para evitar reservas duplicadas.

También se validan los días y horarios de atención, así como los domingos y festivos colombianos.

**Pasos para probar:**
1. Registrar una prueba de manejo para una fecha y horario determinados.
2. Iniciar otra solicitud para esa misma fecha.
3. Consultar las opciones de horario.
4. Verificar que el horario previamente reservado ya no esté disponible.

## Almacenamiento local con Room

La aplicación utiliza Room Database para almacenar la información localmente.

Se implementan dos entidades:

- **FavoriteEntity:** almacena los vehículos guardados como favoritos.
- **TestDriveEntity:** almacena las solicitudes de pruebas de manejo.

La arquitectura de datos incluye:

- Entidades.
- DAO.
- Base de datos.
- Repository.
- ViewModel.
- Flow y StateFlow para observar los cambios en los datos.

La información se conserva al cerrar y volver a abrir la aplicación.

## Arquitectura del proyecto

El proyecto utiliza el patrón MVVM para separar las responsabilidades:

- **Model:** contiene los modelos y entidades de datos.
- **Data:** contiene la base de datos, DAO y repositorios.
- **ViewModel:** gestiona los datos y estados de la interfaz.
- **UI:** contiene las pantallas y componentes desarrollados con Jetpack Compose.

## Validaciones implementadas

- Validación de campos obligatorios.
- Validación del número de teléfono (10 dígitos).
- Validación del correo electrónico.
- Restricción de domingos y festivos colombianos.
- Validación de horarios de atención.
- Bloqueo de horarios previamente reservados.

## Ejecución del proyecto

1. Clonar o descargar el repositorio.
2. Abrir el proyecto desde Android Studio.
3. Esperar la sincronización de Gradle.
4. Conectar un dispositivo Android o iniciar un emulador.
5. Ejecutar la aplicación desde Android Studio.

## Generación del APK

El APK de depuración se genera desde Android Studio mediante:

**Build → Generate App Bundles or APKs → Generate APKs**

El archivo generado se encuentra en:

`app/build/outputs/apk/debug/app-debug.apk`

## Persistencia y pruebas

Para comprobar el funcionamiento de Room:

1. Registrar una solicitud de prueba de manejo.
2. Cerrar completamente la aplicación.
3. Abrir nuevamente AutoLux.
4. Ingresar a "Mis solicitudes de prueba de manejo".
5. Verificar que la solicitud continúa almacenada.

También se puede comprobar la persistencia de los vehículos favoritos al reiniciar la aplicación.

## Identidad visual

AutoLux cuenta con una interfaz de estilo premium, imágenes de vehículos, componentes reutilizables, navegación entre pantallas e ícono de lanzamiento personalizado.

---

**Proyecto académico – Aplicativos Móviles**

Desarrollado con Kotlin, Jetpack Compose y Room Database.