
/**
 * UPMAuthenticationAuthorizationWSSkeletonSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
    package es.upm.fi.sos.t3.backend;

import java.util.HashMap;
import java.util.Map;

/**
     *  UPMAuthenticationAuthorizationWSSkeletonSkeleton java skeleton for the axisService
     */
    public class UPMAuthenticationAuthorizationWSSkeletonSkeleton{
    	// Simulacion local del servicio UPMAuthenticationAuthorization de la asignatura
    	// (en la practica era un servicio remoto que ya no esta disponible).
        
    	// Mapa donde se van guardando los usuarios
    	private static Map<String, String> users = new HashMap<>();

    	private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    	private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    	// Genera una contrasena aleatoria de 8 caracteres para los usuarios nuevos
    	private static String generarContrasena() {
    	    StringBuilder sb = new StringBuilder(8);
    	    for (int i = 0; i < 8; i++) {
    	        sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
    	    }
    	    return sb.toString();
    	}
        
    	
    	// LOGIN RESPONSEBACKEND
    	// Comprueba que existe el usuario y tiene la contrasena suministrada
        // Devuelve boolean que indica si tiene exito
    	public es.upm.fi.sos.t3.backend.LoginResponse login(es.upm.fi.sos.t3.backend.Login login){
    		// respuesta del login
    		es.upm.fi.sos.t3.backend.LoginResponse respuesta = new es.upm.fi.sos.t3.backend.LoginResponse();
    		// resultado del login
            es.upm.fi.sos.t3.backend.xsd.LoginResponseBackEnd resultado = new es.upm.fi.sos.t3.backend.xsd.LoginResponseBackEnd();
            // datos del loguin
            es.upm.fi.sos.t3.backend.xsd.LoginBackEnd datosLogin = login.getLogin();
            
            //nombre
            String usuario = datosLogin.getName();
            //contrasena
            String contrasena = datosLogin.getPassword();
            
            //compruebo si son ese nombre y esa contraseña
            if (users.containsKey(usuario) && users.get(usuario).equals(contrasena)) {
                resultado.setResult(true);
            } 
            else {
                resultado.setResult(false);
            }

            respuesta.set_return(resultado);
            
            //devuelve el boolean
            return respuesta;
    	}
    	
    	
    	// ADDUSER RESPONSEBACKEND
    	
    	// Anade el usuario al sistema, la respuesta es true s tiene exito y un string que es la contrasena generada
    	// Develve false si quiere anadir un usuario existente
    	public es.upm.fi.sos.t3.backend.AddUserResponse addUser(es.upm.fi.sos.t3.backend.AddUser addUser){
    		// creamos la respuesta a devolver
    		es.upm.fi.sos.t3.backend.AddUserResponse respuesta = new es.upm.fi.sos.t3.backend.AddUserResponse();
    		// creamos el resultado del login
    	    es.upm.fi.sos.t3.backend.xsd.AddUserResponseBackEnd resultado = new es.upm.fi.sos.t3.backend.xsd.AddUserResponseBackEnd();
    	    
    	    // creamos nuevo usuario
    	    es.upm.fi.sos.t3.backend.xsd.UserBackEnd nuevoUsuario = addUser.getUser();
    	    // le ponemos el nombre dekl que queremos añadir
    	    String username = nuevoUsuario.getName();

    	    // Ve si existe ya el usuario
    	    if (username == null || username.trim().isEmpty() || users.containsKey(username)) {
    	    	// si ya existe (o no tiene nombre) devolvemos falso y la contrasena vacia
    	        resultado.setResult(false);
    	        resultado.setPassword("");
    	    } 
    	    else {
    	    	// si no, le anadimos con una contrasena autogenerada
    	        String contrasena = generarContrasena();
    	        users.put(username, contrasena);
    	        resultado.setResult(true);
    	        resultado.setPassword(contrasena);
    	    }
    	    
    	    respuesta.set_return(resultado);
    	    return respuesta;
    	}
    	
    	 
    	// REMOVEUSER RESPONSE
    	 
    	// Borra usuario del sistema, true si el usuario a borrar existe y falso si no
    	public es.upm.fi.sos.t3.backend.RemoveUserResponse removeUser(es.upm.fi.sos.t3.backend.RemoveUser removeUser){
    		// creamos respuesta 
    		es.upm.fi.sos.t3.backend.RemoveUserResponse respuesta = new es.upm.fi.sos.t3.backend.RemoveUserResponse();
    		// creamos resultado
    		es.upm.fi.sos.t3.backend.xsd.RemoveUserResponse resultado = new es.upm.fi.sos.t3.backend.xsd.RemoveUserResponse();

    		// usuario que vamos a borrar 
    		es.upm.fi.sos.t3.backend.xsd.RemoveUser usuarioaBorrar = removeUser.getRemoveUser();
    		// nombre del usuario a borrar
    		String nombreaBorrar = usuarioaBorrar.getName();

    		// si existe devolvelmos true y le borramos
    		if (users.containsKey(nombreaBorrar)){
    			// lo quitamos
    		    users.remove(nombreaBorrar);
    		    // devolvemos true
    		    resultado.setResult(true);
    		}
    		else {
    			// devolvemos false
    		    resultado.setResult(false);
    		}

    		respuesta.set_return(resultado);
    		    return respuesta;
    	}
    
    
    	// CHANGEPASSWORD RESPONSEBACKEND
    	 
    	// Permite a un usuario cambiar contrasena
    	// El parametro changePassword incluye el nombre, la contrasena y la nueva contrasena
    	// La respuesta es true si tiene exito y false en caso contrario
    	public es.upm.fi.sos.t3.backend.ChangePasswordResponse changePassword(es.upm.fi.sos.t3.backend.ChangePassword changePassword) {
            // creamos la respuesta a devolver
    		es.upm.fi.sos.t3.backend.ChangePasswordResponse respuesta = new es.upm.fi.sos.t3.backend.ChangePasswordResponse();
    	    // creamos el resultado
    		es.upm.fi.sos.t3.backend.xsd.ChangePasswordResponse resultado = new es.upm.fi.sos.t3.backend.xsd.ChangePasswordResponse();
    		
    		// extraemos los datos
    	    es.upm.fi.sos.t3.backend.xsd.ChangePasswordBackEnd cambioContrasena = changePassword.getChangePassword();
    	    String usuario = cambioContrasena.getName();
    	    String antContrasena = cambioContrasena.getOldpwd();
    	    String nueContrasena = cambioContrasena.getNewpwd();

    	    // vamos a ver si se puede realizar la operacion
    	    // si existe el usuario y la contrasena es la del parametro
    	    if (users.containsKey(usuario) && users.get(usuario).equals(antContrasena)) {
    	    	// establecemos la nueva contrasena
    	        users.put(usuario, nueContrasena);
    	        // el resultado es true
    	        resultado.setResult(true);
    	    } 
    	    else {
    	    	// si no el resultado es false
    	        resultado.setResult(false);
    	    }

    	    // ponermos el resultado y devolvemos la respuesta
    	    respuesta.set_return(resultado);
    	    return respuesta;
    	}
    
    
    	// EXISTUSER RESPONSE
    	 
    	// Permite averiguar si existe un usuario
    	// Devuelve false si no existe
    	public es.upm.fi.sos.t3.backend.ExistUserResponse existUser(es.upm.fi.sos.t3.backend.ExistUser existUser){
            // creamos la respuesta
    		es.upm.fi.sos.t3.backend.ExistUserResponse respuesta = new es.upm.fi.sos.t3.backend.ExistUserResponse();
    		// creamos el resultado
    	    es.upm.fi.sos.t3.backend.xsd.ExistUserResponse resultado = new es.upm.fi.sos.t3.backend.xsd.ExistUserResponse();
    	    
    	    // creamos un string con el nombre del parametro de entrada
    	    String nombre = existUser.getUsername().getName();
    	    // el resultado si existe sera true y false en caso contrario
    	    resultado.setResult(users.containsKey(nombre));

    	    // ponemos el resultado y devolvemos la respuesta
    	    respuesta.set_return(resultado);
    	    return respuesta;
    	}
         
      
    }
    