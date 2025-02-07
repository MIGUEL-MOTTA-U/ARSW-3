# Resolución Laboratorio 1

## Tabla de Contenido
1. [Integrantes](#integrantes)
2. [Introducción](#introducción)
3. [Procedimiento](#procedimiento)
    1. [Parte I](#Parte-i)
    2. [Parte II](#parte-ii)
    3. [Parte III](#parte-iii)
5. [Conclusiones](#conclusiones)

---

## Integrantes
- Santiago Avellaneda
- Miguel Motta

---

## Introducción
Este informe detalla cómo abordamos un ejercicio práctico en el curso de 
Arquitecturas de Software, en el que nos adentramos en los conceptos clave 
de la programación concurrente, el manejo de condiciones de carrera y la 
sincronización de hilos en Java. 

El ejercicio se divide en tres partes: 

* Comenzamos con la implementación básica de un modelo productor-consumidor, 
utilizando técnicas como wait y notify para controlar los hilos.
* Luego, implementamos las clases de cola con manejo de hilos, para aprovechar
sus funcionalidades y el manejo que tienen de recursos de hilos.
* Finalmente, avanzamos hacia la resolución de problemas de sincronización más complejos 
en un simulador de inmortales que opera con múltiples hilos. 

A lo largo de estas actividades, nuestro objetivo ha sido optimizar el rendimiento de 
los programas, gestionando eficientemente el uso de la CPU, abordando 
adecuadamente las condiciones de carrera y aplicando estrategias de 
bloqueo para prevenir posibles deadlocks. Por último, analizaremos cómo 
se comporta el sistema en escenarios más grandes, evaluando cómo nuestras 
soluciones impactan en el rendimiento y la consistencia del programa.

---

## Procedimiento

---

### Parte I
> 
> 1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute
     jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este
     consumo?, cuál es la clase responsable?

> 2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU,
   teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido.
   Verifique con JVisualVM que el consumo de CPU se reduzca.
 
> 3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento.
   Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos
   debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise
   el API de la colección usada como cola para ver cómo garantizar que dicho límite
   no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya
   consumo alto de CPU ni errores.


### Solución
> #### Punto 1
> 
> Las clases responsables del consumo de CPU es debido a la continua ejecución de las clases 
> [`Producer`](src/main/java/edu/eci/arst/concprg/prodcons/Producer.java),
> [`Consumer`](src/main/java/edu/eci/arst/concprg/prodcons/Consumer.java)
> que están haciendo uso de un recurso compartido de producción y consumo.
> 
> La clase principal que mantiene el control de esta ejecución es la clase
> [`Start Production`](src/main/java/edu/eci/arst/concprg/prodcons/StartProduction.java)
> Permitiendo la producción de recursos por un periodo de 5 segundos.
> 
> Las clases
> [`Producer`](src/main/java/edu/eci/arst/concprg/prodcons/Producer.java),
> [`Consumer`](src/main/java/edu/eci/arst/concprg/prodcons/Consumer.java)
> comparten la responsabilidad de ejecución, sin embargo, no se está haciendo un
> uso óptimo de los recursos como podremos apreciar en su ejecución:
> 
> ![](img/first-execution.png)
> ![](img/img.png)

> #### Punto 2
> Para reducir el tiempo de uso innecesario de CPU, nuestra estrategia fue 
> bloquear los hilos dependiendo de su actividad evaluando su capacidad de ejecución,
> es decir, dependiendo de su actividad y los recursos disponibles, los hilos se bloquean
> hasta que puedan seguirse ejecutando. ¿Cómo podemos hacer esto?
> 
> La solución es implementar una clase [_Thread-safety_](https://www.baeldung.com/java-thread-safety)
> que esté enfocada a una estructura de pila o cola de datos, como lo es la clase [LinkedBlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedBlockingQueue.html).
> 
> Esta clase nos permite manejar un comportamiento similar a la solución que buscamos, 
> con los métodos `take()` y `put()` que pausan sus hilos en caso de no haber elementos en la pila, o que 
> se halla excedido el tamaño de la cola y no se puedan agregar más elementos.
> 
> Primero usamos el método `take()` en lugar de `poll()`, dado que la actividad de consumo se presenta con mayor frecuencia y, en consecuencia
> el hilo está ocioso un mayor periodo de tiempo, lo que es ineficiente. 
> De esta manera, pudimos reducir considerablemente el uso de CPU, de aproximadamente un 6% a un 0.1%:
> 
> ![](img/ultra-efficient.png) 

> #### Punto 3
> Para resolver este punto, mantuvimos los cambios del anterior punto, y adicionalmente, implementamos 
> el método `put()` en lugar de `add()` para que se ejecute siempre que haya espacio en la cola de elementos, de lo
> contrario bloquea el hilo hasta que haya espacio en la cola.
> 
> Esta implementación permitió reducir el procesamiento considerablemente:
> 
> ![](img/partIII.png)

#### Parte II
> Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva 
> versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión 
> actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores 
> que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. 
> Teniendo esto en cuenta, haga que:
> 
> * La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne 
> la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias 
> requerido que determina si un host es confiable o no (BLACK_LIST_ALARM_COUNT).
> 
> * Lo anterior, garantizando que no se den condiciones de carrera.
## Solución

#### Parte III

## Solución


## Conclusiones
Durante el desarrollo de este informe, aprendimos a aplicar los principios 
clave de la programación concurrente y la sincronización de hilos en entornos 
multi-hilo. En la primera parte, nos enfocamos en optimizar el uso de la CPU, 
lo que resultó fundamental para mejorar la eficiencia del sistema al hacer 
que los hilos productores y consumidores trabajaran de manera más equilibrada 
sin sobrecargar la cola compartida. En la segunda parte, implementar 
correctamente la sincronización nos ayudó a evitar problemas de condiciones 
de carrera y a asegurar que los hilos se detuvieran adecuadamente al cumplir 
con el número establecido de resultados en nuestra búsqueda distribuida. 

Finalmente, al simular inmortales, logramos identificar y solucionar 
varios problemas relacionados con los bloqueos y la actualización 
de la lista de inmortales activos. A pesar de los retos de manejar múltiples 
hilos en simulaciones a gran escala, conseguimos mantener la consistencia del 
programa y aumentar su velocidad sin incurrir en sincronizaciones pesadas. 
En resumen, esta experiencia nos brindó una comprensible visión de cómo 
gestionar la concurrencia en aplicaciones complejas y aplicar técnicas 
de sincronización que garanticen un funcionamiento eficiente y correcto.