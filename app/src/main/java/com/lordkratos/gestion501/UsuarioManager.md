# Implementación de UsuarioManager: Estructura y Conceptos

### 1. Definición de Paquete e Importaciones

```java
package com.lordkratos.gestion501;
import java.util.HashMap;
```

* **`package`**: Define el espacio de nombres (namespace) para organizar lógicamente las clases y evitar colisiones de nombres.
* **`import`**: Incluye clases de la API estándar de Java.
* **`HashMap`**: Estructura de datos basada en tablas hash que almacena pares clave-valor. Permite acceso y búsqueda en tiempo constante .

### 2. Declaración de la Clase

```java
public class UsuarioManager {
```

* **`public`**: Modificador de acceso. La clase es visible y utilizable desde cualquier otro paquete del proyecto.
* **`class`**: Palabra reservada que define la plantilla o plano para la creación de objetos.

### 3. Variables de Estado y Estructura de Datos

```java
private static UsuarioManager instancia;
private HashMap<String, String[]> usuarios = new HashMap<>();
```

* **`private`**: Restringe el acceso de las variables únicamente al interior de la clase (Encapsulamiento).
* **`static`**: La variable `instancia` pertenece a la clase en sí, no a los objetos individuales creados a partir de ella. Comparte el mismo espacio de memoria en toda la ejecución.
* **`HashMap<String, String[]>`**: Define un mapa donde la clave es un objeto `String` (email) y el valor es un arreglo unidimensional de tipo `String[]` (almacena nombre en índice 0 y contraseña en índice 1).

### 4. Constructor Privado

```java
private UsuarioManager() {}
```

* **Constructor `private**`: Bloquea la capacidad de otras clases para crear instancias utilizando el operador `new UsuarioManager()`. Es el mecanismo fundamental para forzar el patrón Singleton.

### 5. Instanciación Controlada (Patrón Singleton)

```java
public static UsuarioManager getInstance() {
    if (instancia == null) {
        instancia = new UsuarioManager();
    }
    return instancia;
}
```

* **`public static`**: Método accesible globalmente sin necesidad de instanciar la clase.
* **Instanciación Perezosa (Lazy Initialization)**: Evalúa si `instancia` es `null`. Si lo es, invoca al constructor privado. Si no, retorna la referencia existente. Garantiza un único estado global para el mapa de `usuarios`.

### 6. Método de Registro

```java
public boolean registrar(String email, String nombre, String contrasena) {
    if (usuarios.containsKey(email)) {
        return false;
    }
    usuarios.put(email, new String[]{nombre, contrasena});
    return true;
}
```

* **`boolean`**: Tipo de retorno primitivo que indica el éxito (`true`) o fracaso (`false`) de la operación.
* **`containsKey(Object key)`**: Método de `HashMap` que verifica la existencia de la clave (email) para evitar sobrescritura de cuentas (colisión).
* **`new String[]{nombre, contrasena}`**: Creación e inicialización anónima en línea de un arreglo de tamaño fijo (2) para inyectarlo como valor asociado a la clave mediante el método `put()`.

### 7. Método de Autenticación

```java
public String login(String email, String contrasena) {
    if (usuarios.containsKey(email)) {
        String[] datos = usuarios.get(email);
        if (datos[1].equals(contrasena)) {
            return datos[0];
        }
    }
    return null;
}
```

* **`get(Object key)`**: Recupera el valor (`String[]`) asociado al email ingresado.
* **`equals(Object anObject)`**: Método de la clase `String` utilizado para evaluar la equivalencia estructural (contenido) de la cadena `contrasena` contra el índice `1` del arreglo almacenado. Evita el uso del operador `==`, el cual en Java evalúa igualdad de referencias en memoria para objetos.
* **`return null`**: Ausencia de valor. Indica fallo en la autenticación por inexistencia de la clave o discrepancia en la contraseña.