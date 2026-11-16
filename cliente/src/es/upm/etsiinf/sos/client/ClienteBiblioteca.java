package es.upm.etsiinf.sos.client;

import es.upm.etsiinf.sos.*;
import es.upm.etsiinf.sos.model.xsd.Author;
import es.upm.etsiinf.sos.model.xsd.Book;
import es.upm.etsiinf.sos.model.xsd.BookList;
import es.upm.etsiinf.sos.model.xsd.PasswordPair;
import es.upm.etsiinf.sos.model.xsd.User;
import es.upm.etsiinf.sos.model.xsd.Username;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Cliente de prueba del servicio ETSIINFLibrary.
 *
 * Abre varias sesiones a la vez (cada una es un stub con su propia sesión HTTP)
 * y recorre los casos del enunciado: permisos del admin, login/logout con varias
 * sesiones, préstamos, cambio de contraseña y baja de usuarios con sesiones abiertas.
 *
 * Uso: java es.upm.etsiinf.sos.client.ClienteBiblioteca [url-del-servicio]
 */
public class ClienteBiblioteca {

    private static final String URL_POR_DEFECTO = "http://localhost:8080/axis2/services/ETSIINFLibrary";
    private static String url;
    private static int correctas = 0;
    private static int fallidas = 0;

    // Sufijo único por ejecución: el estado del servicio persiste entre clientes,
    // así que cada ejecución usa sus propios usuarios e ISSN.
    private static final String R = "-" + Long.toString(System.nanoTime() % 1_000_000L, 36);
    private static final String ANA = "ana" + R;
    private static final String LUIS = "luis" + R;

    // ------------------------------------------------------------------
    // Una sesión de cliente = un stub con gestión de sesión activada
    // ------------------------------------------------------------------
    static class Sesion {
        private final ETSIINFLibraryStub stub;
        private final String nombre;

        Sesion(String nombre) throws Exception {
            this.nombre = nombre;
            this.stub = new ETSIINFLibraryStub(url);
            this.stub._getServiceClient().getOptions().setManageSession(true);
        }

        boolean login(String usuario, String pwd) throws Exception {
            User u = new User();
            u.setName(usuario);
            u.setPwd(pwd);
            Login op = new Login();
            op.setArgs0(u);
            return stub.login(op).get_return().getResponse();
        }

        boolean logout() throws Exception {
            return stub.logout(new Logout()).get_return().getResponse();
        }

        String addUser(String usuario) throws Exception {
            Username u = new Username();
            u.setUsername(usuario);
            AddUser op = new AddUser();
            op.setArgs0(u);
            es.upm.etsiinf.sos.model.xsd.AddUserResponse r = stub.addUser(op).get_return();
            return r.getResponse() ? r.getPwd() : null;
        }

        boolean deleteUser(String usuario) throws Exception {
            Username u = new Username();
            u.setUsername(usuario);
            DeleteUser op = new DeleteUser();
            op.setArgs0(u);
            return stub.deleteUser(op).get_return().getResponse();
        }

        boolean changePassword(String antigua, String nueva) throws Exception {
            PasswordPair p = new PasswordPair();
            p.setOldpwd(antigua);
            p.setNewpwd(nueva);
            ChangePassword op = new ChangePassword();
            op.setArgs0(p);
            return stub.changePassword(op).get_return().getResponse();
        }

        boolean addBook(String issn, String titulo, String... autores) throws Exception {
            Book b = new Book();
            b.setISSN(issn);
            b.setName(titulo);
            b.setAuthors(autores);
            AddBook op = new AddBook();
            op.setArgs0(b);
            return stub.addBook(op).get_return().getResponse();
        }

        boolean removeBook(String issn) throws Exception {
            RemoveBook op = new RemoveBook();
            op.setArgs0(issn);
            return stub.removeBook(op).get_return().getResponse();
        }

        Book getBook(String issn) throws Exception {
            GetBook op = new GetBook();
            op.setArgs0(issn);
            return stub.getBook(op).get_return();
        }

        BookList listBooks() throws Exception {
            return stub.listBooks(new ListBooks()).get_return();
        }

        BookList booksFromAuthor(String autor) throws Exception {
            Author a = new Author();
            a.setName(autor);
            GetBooksFromAuthor op = new GetBooksFromAuthor();
            op.setArgs0(a);
            return stub.getBooksFromAuthor(op).get_return();
        }

        boolean borrowBook(String issn) throws Exception {
            BorrowBook op = new BorrowBook();
            op.setArgs0(issn);
            return stub.borrowBook(op).get_return().getResponse();
        }

        boolean returnBook(String issn) throws Exception {
            ReturnBook op = new ReturnBook();
            op.setArgs0(issn);
            return stub.returnBook(op).get_return().getResponse();
        }

        BookList listBorrowedBooks() throws Exception {
            return stub.listBorrowedBooks(new ListBorrowedBooks()).get_return();
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    // ------------------------------------------------------------------
    // Utilidades de comprobación
    // ------------------------------------------------------------------
    private static void comprobar(String descripcion, boolean condicion) {
        if (condicion) {
            correctas++;
            System.out.println("  [OK]    " + descripcion);
        } else {
            fallidas++;
            System.out.println("  [FALLO] " + descripcion);
        }
    }

    private static List<String> issns(BookList lista) {
        if (lista == null || lista.getIssns() == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(lista.getIssns());
    }

    private static void seccion(String titulo) {
        System.out.println();
        System.out.println("== " + titulo);
    }

    // ------------------------------------------------------------------
    // Escenario de prueba
    // ------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        url = args.length > 0 ? args[0] : URL_POR_DEFECTO;
        System.out.println("Probando el servicio en " + url);

        Sesion admin = new Sesion("admin");
        Sesion ana1 = new Sesion("ana (sesión 1)");
        Sesion ana2 = new Sesion("ana (sesión 2)");
        Sesion luis = new Sesion(LUIS);
        Sesion anonimo = new Sesion("sin login");

        seccion("Administración de usuarios");
        comprobar("login de admin con contraseña incorrecta falla", !admin.login("admin", "mala"));
        comprobar("login de admin/admin", admin.login("admin", "admin"));
        comprobar("un cliente sin login no puede dar de alta usuarios", anonimo.addUser("pirata" + R) == null);
        String pwdAna = admin.addUser(ANA);
        String pwdLuis = admin.addUser(LUIS);
        comprobar("admin da de alta a ana y recibe su contraseña", pwdAna != null && !pwdAna.isEmpty());
        comprobar("admin da de alta a luis", pwdLuis != null);
        comprobar("no se puede dar de alta un usuario repetido", admin.addUser(ANA) == null);
        comprobar("no se puede dar de alta otro 'admin'", admin.addUser("admin") == null);

        seccion("Login y sesiones");
        comprobar("login de ana con contraseña incorrecta falla", !ana1.login(ANA, "mala"));
        comprobar("login de ana con su contraseña", ana1.login(ANA, pwdAna));
        comprobar("repetir login del mismo usuario en la sesión da true (aunque la contraseña no valga)",
                ana1.login(ANA, "cualquiera"));
        comprobar("otro usuario no puede hacer login en una sesión ya abierta", !ana1.login(LUIS, pwdLuis));
        comprobar("ana abre una segunda sesión concurrente", ana2.login(ANA, pwdAna));
        comprobar("luis hace login en su propio cliente", luis.login(LUIS, pwdLuis));
        comprobar("un usuario normal no puede dar de alta usuarios", ana1.addUser("eva" + R) == null);

        seccion("Catálogo");
        comprobar("admin añade 'El Quijote' (ejemplar 1)", admin.addBook("ISSN-1" + R, "El Quijote", "Cervantes"));
        comprobar("admin añade 'El Quijote' (ejemplar 2)", admin.addBook("ISSN-1" + R, "El Quijote", "Cervantes"));
        comprobar("admin añade 'Novelas ejemplares'", admin.addBook("ISSN-2" + R, "Novelas ejemplares", "Cervantes"));
        comprobar("admin añade 'Rayuela'", admin.addBook("ISSN-3" + R, "Rayuela", "Cortázar"));
        comprobar("un usuario normal no puede añadir libros", !ana1.addBook("ISSN-9" + R, "Pirata", "Nadie"));
        comprobar("no se puede añadir un libro sin ISSN", !admin.addBook("", "Sin ISSN", "Nadie"));

        BookList lista = ana1.listBooks();
        comprobar("listBooks devuelve result=true con sesión", lista.getResult());
        comprobar("listBooks muestra los últimos añadidos primero",
                issns(lista).size() >= 3
                        && issns(lista).subList(0, 3).equals(Arrays.asList("ISSN-3" + R, "ISSN-2" + R, "ISSN-1" + R)));
        comprobar("getBooksFromAuthor('Cervantes') devuelve sus dos libros",
                issns(ana1.booksFromAuthor("Cervantes")).containsAll(Arrays.asList("ISSN-1" + R, "ISSN-2" + R)));
        Book quijote = ana1.getBook("ISSN-1" + R);
        comprobar("getBook devuelve el libro", quijote != null && "El Quijote".equals(quijote.getName()));

        BookList sinSesion = anonimo.listBooks();
        comprobar("sin login, listBooks devuelve result=false", !sinSesion.getResult());
        comprobar("sin login no se puede pedir prestado", !anonimo.borrowBook("ISSN-1" + R));

        seccion("Préstamos");
        comprobar("ana pide prestado El Quijote", ana1.borrowBook("ISSN-1" + R));
        comprobar("ana no puede pedir dos ejemplares del mismo libro", !ana2.borrowBook("ISSN-1" + R));
        comprobar("luis pide el segundo ejemplar de El Quijote", luis.borrowBook("ISSN-1" + R));
        comprobar("ya no quedan ejemplares de El Quijote", ana1.getBook("ISSN-1" + R) == null);
        comprobar("no se puede prestar un libro que no existe", !luis.borrowBook("ISSN-404" + R));
        comprobar("ana pide prestada Rayuela", ana2.borrowBook("ISSN-3" + R));
        comprobar("los préstamos se ven desde las dos sesiones de ana (más recientes primero)",
                issns(ana1.listBorrowedBooks()).equals(Arrays.asList("ISSN-3" + R, "ISSN-1" + R)));
        comprobar("ana devuelve Rayuela", ana1.returnBook("ISSN-3" + R));
        comprobar("no se puede devolver un libro que no se tiene", !ana1.returnBook("ISSN-3" + R));
        comprobar("Rayuela vuelve a estar disponible", ana2.getBook("ISSN-3" + R) != null);
        comprobar("admin retira un ejemplar de Novelas ejemplares", admin.removeBook("ISSN-2" + R));
        comprobar("un usuario normal no puede retirar libros", !luis.removeBook("ISSN-3" + R));

        seccion("Logout con varias sesiones");
        comprobar("ana cierra la sesión 2", ana2.logout());
        comprobar("la sesión 2 ya no tiene acceso", !ana2.listBooks().getResult());
        comprobar("la sesión 1 de ana sigue activa", ana1.listBooks().getResult());
        comprobar("logout sin sesión devuelve false", !anonimo.logout());
        comprobar("tras el logout, otro usuario puede hacer login en ese cliente", ana2.login(LUIS, pwdLuis));
        comprobar("y vuelve a cerrar sesión", ana2.logout());

        seccion("Cambio de contraseña");
        comprobar("con la contraseña antigua incorrecta falla", !ana1.changePassword("mala", "nueva123"));
        comprobar("ana cambia su contraseña", ana1.changePassword(pwdAna, "nueva123"));
        comprobar("la contraseña antigua ya no sirve", !ana2.login(ANA, pwdAna));
        comprobar("la nueva contraseña sí", ana2.login(ANA, "nueva123"));
        comprobar("admin cambia su contraseña (gestionada en el propio servicio)",
                admin.changePassword("admin", "admin2"));
        Sesion admin2 = new Sesion("admin (2)");
        comprobar("admin entra con la contraseña nueva", admin2.login("admin", "admin2"));
        comprobar("admin restaura su contraseña", admin2.changePassword("admin2", "admin"));

        seccion("Baja de usuarios");
        comprobar("luis no puede dar de baja a ana", !luis.deleteUser(ANA));
        comprobar("el admin no se puede dar de baja", !admin.deleteUser("admin"));
        comprobar("admin da de baja a ana (con dos sesiones abiertas)", admin.deleteUser(ANA));
        comprobar("las sesiones de ana dejan de ser válidas", !ana1.listBooks().getResult() && !ana2.borrowBook("ISSN-3" + R));
        comprobar("el libro que tenía ana vuelve a la biblioteca", luis.getBook("ISSN-1" + R) != null);
        comprobar("ana ya no puede hacer login", !new Sesion("ana (nueva)").login(ANA, "nueva123"));
        comprobar("luis se da de baja a sí mismo", luis.deleteUser(LUIS));
        comprobar("y su ejemplar también vuelve", issns(admin.listBooks()).contains("ISSN-1" + R)
                && admin.getBook("ISSN-1" + R) != null);

        System.out.println();
        System.out.println("Resultado: " + correctas + " correctas, " + fallidas + " fallidas");
        System.exit(fallidas == 0 ? 0 : 1);
    }
}
