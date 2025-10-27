## Patrones utilizados por Spring Security

### Chain of responsability:

La Security Filter Chain es el núcleo del procesamiento de seguridad HTTP en Spring Security.

Cuando llega una petición a tu aplicación (por ejemplo, GET /admin), Spring Security intercepta esa solicitud antes de que llegue a tu controlador, y la hace pasar por una cadena de filtros (filters) especializados que se ejecutan uno tras otro.

Esta implementacion se basa en el patron "Chain of responsability", en donde los filtros se aplican en modo de cadena, siendo delegadas por FilterChainProxy. Luego la peticion sera pasada a diferentes filtros como: SecurityContextPersistenceFilter,  UsernamePasswordAuthenticationFilter, AnonymousAuthenticationFilter, etc. Esto ocurrira hasta que los filtros puedan procesar la peticion, en caso de no poder se devolvera un error.

> [Fuente: Security filter chain](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/security-filter-chain.html)

### Strategy

El patrón Strategy en Spring Security se utiliza para definir comportamientos intercambiables dentro del proceso de seguridad, permitiendo cambiar algoritmos o mecanismos sin modificar la lógica principal del sistema.

Una de sus aplicaciones más claras es la interfaz PasswordEncoder, que actúa como una estrategia de codificación de contraseñas. Esta interfaz define los métodos encode() y matches(), mientras que implementaciones como BCryptPasswordEncoder, Pbkdf2PasswordEncoder, Argon2PasswordEncoder o SCryptPasswordEncoder representan distintas estrategias concretas. De este modo, el sistema puede cambiar el algoritmo de encriptación sin afectar el flujo de autenticación.

Además, el patrón Strategy también se aplica en componentes como AuthenticationProvider y UserDetailsService. El AuthenticationManager delega el proceso de autenticación a diferentes estrategias de proveedores (DaoAuthenticationProvider, LdapAuthenticationProvider, JwtAuthenticationProvider, entre otros), cada uno con su propia forma de validar credenciales. De manera similar, UserDetailsService define la estrategia para obtener los datos de usuario, permitiendo implementar diferentes fuentes (base de datos, memoria, servicios externos, etc.).

Gracias a este patrón, Spring Security consigue una arquitectura flexible, extensible y desacoplada, donde los comportamientos clave pueden variar según la necesidad sin alterar el núcleo del framework.

> [Fuente: AuthenticationManager](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AuthenticationManager.html)
> [Fuente: PasswordEncoder](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/password/PasswordEncoder.html)

### Singleton

El patrón Singleton es un patrón de diseño creacional que garantiza que solo exista una única instancia de una clase en todo el sistema y que sea accesible globalmente. Su propósito es controlar el punto de acceso a un recurso compartido, asegurando que todas las partes de la aplicación utilicen la misma instancia.

En Spring Security, este patrón se aplica principalmente a través de la clase SecurityContextHolder, que administra el contexto de seguridad asociado a la ejecución actual (por ejemplo, la información del usuario autenticado).

SecurityContextHolder almacena un objeto SecurityContext, que contiene el Authentication actual. Para lograr que este contexto sea accesible desde cualquier parte del código (controladores, servicios, etc.), utiliza una instancia compartida que actúa como un singleton por hilo, implementada mediante un ThreadLocal.

Esto significa que, aunque el SecurityContextHolder es único a nivel global, el contexto almacenado en su interior es aislado por hilo de ejecución. Cada solicitud HTTP (que se maneja en un hilo diferente) tiene su propio SecurityContext, pero todos acceden al mismo punto central de gestión.

Gracias a este enfoque, Spring Security puede mantener la información del usuario autenticado de forma segura, consistente y desacoplada, sin necesidad de pasar explícitamente la información de autenticación entre componentes.

> [Fuente: SecurityContextHolder](https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-authentication-securitycontextholder)

### Facade

El patrón Fachada (Facade) es un patrón de diseño estructural que tiene como objetivo proporcionar una interfaz simplificada y de alto nivel para interactuar con un conjunto complejo de clases, ocultando los detalles internos del sistema. Su propósito es reducir la complejidad y el acoplamiento, ofreciendo un punto de entrada único y coherente hacia subsistemas más complicados.

En Spring Security, este patrón se aplica principalmente en la configuración de seguridad a través de la clase HttpSecurity (y anteriormente WebSecurityConfigurerAdapter). Esta clase actúa como una fachada que encapsula la configuración interna del framework — como filtros, manejadores de autenticación, proveedores, y reglas de autorización — detrás de una API fluida y legible.

Por ejemplo, al configurar seguridad con código como el siguiente:

<pre>
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    )
    .formLogin(withDefaults())
    .logout(withDefaults());
</pre>

El desarrollador no necesita interactuar directamente con las múltiples clases que implementan cada parte del proceso de autenticación y autorización (como FilterChainProxy, AuthenticationManager, ExceptionTranslationFilter, etc.).
En su lugar, HttpSecurity expone una interfaz unificada que delega internamente las tareas de configuración a los componentes adecuados.

> [Fuente: HttpSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)



En conclusión, Spring Security no se limita a un solo patrón de diseño; de hecho, su arquitectura hace un uso extensivo de múltiples patrones para lograr flexibilidad, modularidad y escalabilidad. Hemos visto cómo implementa Chain of Responsibility en la Security Filter Chain, Strategy en PasswordEncoder y proveedores de autenticación, Singleton en SecurityContextHolder, y Fachada en HttpSecurity.

Además, existen otros patrones presentes en el framework, como Proxy en la seguridad a nivel de métodos (@PreAuthorize y AOP), Observer en los eventos de autenticación y Adapter/Decorator en wrappers y filtros de integración. Esta combinación de patrones permite que Spring Security sea extensible, desacoplado y altamente configurable, ofreciendo un marco robusto para gestionar autenticación, autorización y seguridad en aplicaciones Java modernas.
