/**
 * ETSIINFLibrarySkeleton.java
 *
 * Implementación del servicio web ETSIINFLibrary (Axis2 1.6.2).
 * El esqueleto se generó desde el WSDL; la lógica de cada operación es propia.
 *
 * Modelo de sesiones:
 *  - El servicio se despliega con scope="transportsession" (services.xml): Axis2 crea
 *    una instancia de esta clase por cada sesión HTTP de cliente. Por eso el usuario
 *    autenticado de la sesión es un campo de instancia.
 *  - El estado de la biblioteca (libros, préstamos, contraseña del admin) es estático,
 *    compartido por todas las sesiones y persiste mientras el servicio esté desplegado.
 *  - La gestión de usuarios (alta, baja, login, cambio de contraseña) se delega en el
 *    servicio de autenticación UPMAuthenticationAuthorization, salvo para el admin,
 *    que se gestiona aquí mismo.
 */
package es.upm.etsiinf.sos;

import es.upm.etsiinf.sos.model.xsd.Book;
import es.upm.etsiinf.sos.model.xsd.BookList;
import es.upm.etsiinf.sos.model.xsd.Response;
import es.upm.fi.sos.t3.backend.UPMAuthenticationAuthorizationWSSkeletonSkeleton;
import es.upm.fi.sos.t3.backend.xsd.ChangePasswordBackEnd;
import es.upm.fi.sos.t3.backend.xsd.LoginBackEnd;
import es.upm.fi.sos.t3.backend.xsd.UserBackEnd;

import java.util.*;

public class ETSIINFLibrarySkeleton {

    private static final String ADMIN = "admin";

    // ---------------- Estado compartido por todas las sesiones ----------------
    private static final Object LOCK = new Object();
    private static String contrasenaAdmin = "admin";
    /** ISSN -> ejemplares disponibles. LinkedHashMap para conservar el orden de alta. */
    private static final Map<String, List<Book>> biblioteca = new LinkedHashMap<>();
    /** usuario -> ejemplares que tiene prestados (en orden de préstamo). */
    private static final Map<String, List<Book>> prestamos = new HashMap<>();

    private static final UPMAuthenticationAuthorizationWSSkeletonSkeleton auth =
            new UPMAuthenticationAuthorizationWSSkeletonSkeleton();

    // ---------------- Estado de esta sesión ----------------
    private String usuarioActual = null;

    // ======================================================================
    // Utilidades
    // ======================================================================

    private static Response respuesta(boolean ok) {
        Response r = new Response();
        r.setResponse(ok);
        return r;
    }

    private static BookList listaLibros(boolean ok, List<Book> libros) {
        BookList lista = new BookList();
        lista.setResult(ok);
        String[] issns = new String[libros.size()];
        String[] nombres = new String[libros.size()];
        for (int i = 0; i < libros.size(); i++) {
            issns[i] = libros.get(i).getISSN();
            nombres[i] = libros.get(i).getName();
        }
        lista.setIssns(issns);
        lista.setBookNames(nombres);
        return lista;
    }

    private static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean existeEnBackend(String usuario) {
        es.upm.fi.sos.t3.backend.ExistUser peticion = new es.upm.fi.sos.t3.backend.ExistUser();
        es.upm.fi.sos.t3.backend.xsd.Username nombre = new es.upm.fi.sos.t3.backend.xsd.Username();
        nombre.setName(usuario);
        peticion.setUsername(nombre);
        return auth.existUser(peticion).get_return().getResult();
    }

    /**
     * Comprueba que la sesión tiene un usuario autenticado que sigue existiendo.
     * Si el usuario fue dado de baja mientras tenía la sesión abierta, la sesión
     * se invalida y se comporta como si el usuario no existiera.
     */
    private boolean sesionValida() {
        if (usuarioActual == null) {
            return false;
        }
        if (ADMIN.equals(usuarioActual) || existeEnBackend(usuarioActual)) {
            return true;
        }
        usuarioActual = null;
        return false;
    }

    private boolean esAdmin() {
        return sesionValida() && ADMIN.equals(usuarioActual);
    }

    // ======================================================================
    // Gestión de usuarios
    // ======================================================================

    /** Alta de usuario. Solo el admin; la contraseña la genera el servicio de autenticación. */
    public AddUserResponse addUser(AddUser addUser) {
        synchronized (LOCK) {
            es.upm.etsiinf.sos.model.xsd.AddUserResponse resultado =
                    new es.upm.etsiinf.sos.model.xsd.AddUserResponse();
            resultado.setResponse(false);
            resultado.setPwd("");

            String nombre = addUser.getArgs0() == null ? null : addUser.getArgs0().getUsername();
            if (esAdmin() && !vacio(nombre) && !ADMIN.equals(nombre)) {
                es.upm.fi.sos.t3.backend.AddUser peticion = new es.upm.fi.sos.t3.backend.AddUser();
                UserBackEnd usuario = new UserBackEnd();
                usuario.setName(nombre);
                peticion.setUser(usuario);

                es.upm.fi.sos.t3.backend.xsd.AddUserResponseBackEnd r = auth.addUser(peticion).get_return();
                resultado.setResponse(r.getResult());
                resultado.setPwd(r.getResult() ? r.getPassword() : "");
            }

            AddUserResponse respuesta = new AddUserResponse();
            respuesta.set_return(resultado);
            return respuesta;
        }
    }

    /**
     * Inicia sesión. Si la sesión ya tiene un usuario autenticado, devuelve true solo si
     * es el mismo usuario (sea cual sea la contraseña) y false si es otro.
     * El admin se valida aquí, no en el servicio de autenticación.
     */
    public LoginResponse login(Login login) {
        synchronized (LOCK) {
            String nombre = login.getArgs0() == null ? null : login.getArgs0().getName();
            String pwd = login.getArgs0() == null ? null : login.getArgs0().getPwd();
            boolean ok;

            if (sesionValida()) {
                ok = usuarioActual.equals(nombre);
            } else if (vacio(nombre) || pwd == null) {
                ok = false;
            } else if (ADMIN.equals(nombre)) {
                ok = contrasenaAdmin.equals(pwd);
            } else {
                es.upm.fi.sos.t3.backend.Login peticion = new es.upm.fi.sos.t3.backend.Login();
                LoginBackEnd datos = new LoginBackEnd();
                datos.setName(nombre);
                datos.setPassword(pwd);
                peticion.setLogin(datos);
                ok = auth.login(peticion).get_return().getResult();
            }

            if (ok) {
                usuarioActual = nombre;
            }
            LoginResponse respuesta = new LoginResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    /** Cierra la sesión de este cliente. Las demás sesiones del mismo usuario siguen abiertas. */
    public LogoutResponse logout(Logout logout) {
        synchronized (LOCK) {
            boolean ok = usuarioActual != null;
            usuarioActual = null;
            LogoutResponse respuesta = new LogoutResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    /**
     * Baja de usuario. La puede pedir el admin o el propio usuario; el admin no se puede borrar.
     * Los libros que tuviera prestados vuelven a la biblioteca.
     */
    public DeleteUserResponse deleteUser(DeleteUser deleteUser) {
        synchronized (LOCK) {
            String nombre = deleteUser.getArgs0() == null ? null : deleteUser.getArgs0().getUsername();
            boolean ok = false;

            if (sesionValida() && !vacio(nombre) && !ADMIN.equals(nombre)
                    && (ADMIN.equals(usuarioActual) || usuarioActual.equals(nombre))) {
                es.upm.fi.sos.t3.backend.RemoveUser peticion = new es.upm.fi.sos.t3.backend.RemoveUser();
                es.upm.fi.sos.t3.backend.xsd.RemoveUser usuario = new es.upm.fi.sos.t3.backend.xsd.RemoveUser();
                usuario.setName(nombre);
                peticion.setRemoveUser(usuario);
                ok = auth.removeUser(peticion).get_return().getResult();

                if (ok) {
                    List<Book> prestados = prestamos.remove(nombre);
                    if (prestados != null) {
                        for (Book libro : prestados) {
                            devolverABiblioteca(libro);
                        }
                    }
                    if (nombre.equals(usuarioActual)) {
                        usuarioActual = null;
                    }
                }
            }

            DeleteUserResponse respuesta = new DeleteUserResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    /** Cambia la contraseña del usuario de la sesión (la del admin se gestiona aquí). */
    public ChangePasswordResponse changePassword(ChangePassword changePassword) {
        synchronized (LOCK) {
            String antigua = changePassword.getArgs0() == null ? null : changePassword.getArgs0().getOldpwd();
            String nueva = changePassword.getArgs0() == null ? null : changePassword.getArgs0().getNewpwd();
            boolean ok = false;

            if (sesionValida() && antigua != null && !vacio(nueva)) {
                if (ADMIN.equals(usuarioActual)) {
                    if (contrasenaAdmin.equals(antigua)) {
                        contrasenaAdmin = nueva;
                        ok = true;
                    }
                } else {
                    es.upm.fi.sos.t3.backend.ChangePassword peticion = new es.upm.fi.sos.t3.backend.ChangePassword();
                    ChangePasswordBackEnd datos = new ChangePasswordBackEnd();
                    datos.setName(usuarioActual);
                    datos.setOldpwd(antigua);
                    datos.setNewpwd(nueva);
                    peticion.setChangePassword(datos);
                    ok = auth.changePassword(peticion).get_return().getResult();
                }
            }

            ChangePasswordResponse respuesta = new ChangePasswordResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    // ======================================================================
    // Catálogo (solo admin)
    // ======================================================================

    /** Añade un ejemplar de un libro. Varios ejemplares pueden compartir ISSN. */
    public AddBookResponse addBook(AddBook addBook) {
        synchronized (LOCK) {
            Book libro = addBook.getArgs0();
            boolean ok = esAdmin() && libro != null && !vacio(libro.getISSN());
            if (ok) {
                List<Book> ejemplares = biblioteca.get(libro.getISSN());
                if (ejemplares == null) {
                    ejemplares = new ArrayList<>();
                    biblioteca.put(libro.getISSN(), ejemplares);
                }
                ejemplares.add(libro);
            }
            AddBookResponse respuesta = new AddBookResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    /** Retira un ejemplar disponible de un libro. */
    public RemoveBookResponse removeBook(RemoveBook removeBook) {
        synchronized (LOCK) {
            String issn = removeBook.getArgs0();
            boolean ok = esAdmin() && sacarDeBiblioteca(issn) != null;
            RemoveBookResponse respuesta = new RemoveBookResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    // ======================================================================
    // Consultas y préstamos (usuarios con sesión)
    // ======================================================================

    /** Datos de un libro con ejemplares disponibles, o null. */
    public GetBookResponse getBook(GetBook getBook) {
        synchronized (LOCK) {
            String issn = getBook.getArgs0();
            Book libro = null;
            if (sesionValida() && issn != null) {
                List<Book> ejemplares = biblioteca.get(issn);
                if (ejemplares != null && !ejemplares.isEmpty()) {
                    libro = ejemplares.get(0);
                }
            }
            GetBookResponse respuesta = new GetBookResponse();
            respuesta.set_return(libro);
            return respuesta;
        }
    }

    /** Libros con ejemplares disponibles, los últimos añadidos primero. */
    public ListBooksResponse listBooks(ListBooks listBooks) {
        synchronized (LOCK) {
            List<Book> libros = new ArrayList<>();
            boolean ok = sesionValida();
            if (ok) {
                for (List<Book> ejemplares : biblioteca.values()) {
                    if (!ejemplares.isEmpty()) {
                        libros.add(ejemplares.get(0));
                    }
                }
                Collections.reverse(libros);
            }
            ListBooksResponse respuesta = new ListBooksResponse();
            respuesta.set_return(listaLibros(ok, libros));
            return respuesta;
        }
    }

    /** Libros disponibles de un autor. */
    public GetBooksFromAuthorResponse getBooksFromAuthor(GetBooksFromAuthor getBooksFromAuthor) {
        synchronized (LOCK) {
            String autor = getBooksFromAuthor.getArgs0() == null ? null : getBooksFromAuthor.getArgs0().getName();
            List<Book> libros = new ArrayList<>();
            boolean ok = sesionValida() && autor != null;
            if (ok) {
                for (List<Book> ejemplares : biblioteca.values()) {
                    if (!ejemplares.isEmpty() && tieneAutor(ejemplares.get(0), autor)) {
                        libros.add(ejemplares.get(0));
                    }
                }
            }
            GetBooksFromAuthorResponse respuesta = new GetBooksFromAuthorResponse();
            respuesta.set_return(listaLibros(ok, libros));
            return respuesta;
        }
    }

    /** Presta un ejemplar al usuario de la sesión (como mucho uno de cada ISSN). */
    public BorrowBookResponse borrowBook(BorrowBook borrowBook) {
        synchronized (LOCK) {
            String issn = borrowBook.getArgs0();
            boolean ok = false;
            if (sesionValida() && !ADMIN.equals(usuarioActual) && issn != null
                    && buscarPrestamo(usuarioActual, issn) == null) {
                Book ejemplar = sacarDeBiblioteca(issn);
                if (ejemplar != null) {
                    List<Book> suyos = prestamos.get(usuarioActual);
                    if (suyos == null) {
                        suyos = new ArrayList<>();
                        prestamos.put(usuarioActual, suyos);
                    }
                    suyos.add(ejemplar);
                    ok = true;
                }
            }
            BorrowBookResponse respuesta = new BorrowBookResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    /** Devuelve a la biblioteca un ejemplar que el usuario tiene prestado. */
    public ReturnBookResponse returnBook(ReturnBook returnBook) {
        synchronized (LOCK) {
            String issn = returnBook.getArgs0();
            boolean ok = false;
            if (sesionValida() && issn != null) {
                Book ejemplar = buscarPrestamo(usuarioActual, issn);
                if (ejemplar != null) {
                    prestamos.get(usuarioActual).remove(ejemplar);
                    devolverABiblioteca(ejemplar);
                    ok = true;
                }
            }
            ReturnBookResponse respuesta = new ReturnBookResponse();
            respuesta.set_return(respuesta(ok));
            return respuesta;
        }
    }

    /** Libros prestados al usuario de la sesión, los más recientes primero. */
    public ListBorrowedBooksResponse listBorrowedBooks(ListBorrowedBooks listBorrowedBooks) {
        synchronized (LOCK) {
            List<Book> libros = new ArrayList<>();
            boolean ok = sesionValida();
            if (ok && prestamos.containsKey(usuarioActual)) {
                libros.addAll(prestamos.get(usuarioActual));
                Collections.reverse(libros);
            }
            ListBorrowedBooksResponse respuesta = new ListBorrowedBooksResponse();
            respuesta.set_return(listaLibros(ok, libros));
            return respuesta;
        }
    }

    // ======================================================================
    // Auxiliares del catálogo (llamar con LOCK tomado)
    // ======================================================================

    private static Book sacarDeBiblioteca(String issn) {
        List<Book> ejemplares = issn == null ? null : biblioteca.get(issn);
        if (ejemplares == null || ejemplares.isEmpty()) {
            return null;
        }
        Book ejemplar = ejemplares.remove(0);
        if (ejemplares.isEmpty()) {
            biblioteca.remove(issn);
        }
        return ejemplar;
    }

    private static void devolverABiblioteca(Book libro) {
        List<Book> ejemplares = biblioteca.get(libro.getISSN());
        if (ejemplares == null) {
            ejemplares = new ArrayList<>();
            biblioteca.put(libro.getISSN(), ejemplares);
        }
        ejemplares.add(libro);
    }

    private static Book buscarPrestamo(String usuario, String issn) {
        List<Book> suyos = prestamos.get(usuario);
        if (suyos != null) {
            for (Book libro : suyos) {
                if (issn.equals(libro.getISSN())) {
                    return libro;
                }
            }
        }
        return null;
    }

    private static boolean tieneAutor(Book libro, String autor) {
        if (libro.getAuthors() != null) {
            for (String a : libro.getAuthors()) {
                if (autor.equals(a)) {
                    return true;
                }
            }
        }
        return false;
    }
}
