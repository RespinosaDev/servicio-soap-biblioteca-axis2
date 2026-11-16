

/**
 * ETSIINFLibrary.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package es.upm.etsiinf.sos.client;

    /*
     *  ETSIINFLibrary java interface
     */

    public interface ETSIINFLibrary {
          

        /**
          * Auto generated method signature
          * 
                    * @param logout0
                
         */

         
                     public es.upm.etsiinf.sos.LogoutResponse logout(

                        es.upm.etsiinf.sos.Logout logout0)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param logout0
            
          */
        public void startlogout(

            es.upm.etsiinf.sos.Logout logout0,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param returnBook2
                
         */

         
                     public es.upm.etsiinf.sos.ReturnBookResponse returnBook(

                        es.upm.etsiinf.sos.ReturnBook returnBook2)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param returnBook2
            
          */
        public void startreturnBook(

            es.upm.etsiinf.sos.ReturnBook returnBook2,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param borrowBook4
                
         */

         
                     public es.upm.etsiinf.sos.BorrowBookResponse borrowBook(

                        es.upm.etsiinf.sos.BorrowBook borrowBook4)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param borrowBook4
            
          */
        public void startborrowBook(

            es.upm.etsiinf.sos.BorrowBook borrowBook4,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param removeBook6
                
         */

         
                     public es.upm.etsiinf.sos.RemoveBookResponse removeBook(

                        es.upm.etsiinf.sos.RemoveBook removeBook6)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param removeBook6
            
          */
        public void startremoveBook(

            es.upm.etsiinf.sos.RemoveBook removeBook6,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param deleteUser8
                
         */

         
                     public es.upm.etsiinf.sos.DeleteUserResponse deleteUser(

                        es.upm.etsiinf.sos.DeleteUser deleteUser8)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param deleteUser8
            
          */
        public void startdeleteUser(

            es.upm.etsiinf.sos.DeleteUser deleteUser8,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param addUser10
                
         */

         
                     public es.upm.etsiinf.sos.AddUserResponse addUser(

                        es.upm.etsiinf.sos.AddUser addUser10)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param addUser10
            
          */
        public void startaddUser(

            es.upm.etsiinf.sos.AddUser addUser10,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getBook12
                
         */

         
                     public es.upm.etsiinf.sos.GetBookResponse getBook(

                        es.upm.etsiinf.sos.GetBook getBook12)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getBook12
            
          */
        public void startgetBook(

            es.upm.etsiinf.sos.GetBook getBook12,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param listBooks14
                
         */

         
                     public es.upm.etsiinf.sos.ListBooksResponse listBooks(

                        es.upm.etsiinf.sos.ListBooks listBooks14)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param listBooks14
            
          */
        public void startlistBooks(

            es.upm.etsiinf.sos.ListBooks listBooks14,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param changePassword16
                
         */

         
                     public es.upm.etsiinf.sos.ChangePasswordResponse changePassword(

                        es.upm.etsiinf.sos.ChangePassword changePassword16)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param changePassword16
            
          */
        public void startchangePassword(

            es.upm.etsiinf.sos.ChangePassword changePassword16,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param login18
                
         */

         
                     public es.upm.etsiinf.sos.LoginResponse login(

                        es.upm.etsiinf.sos.Login login18)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param login18
            
          */
        public void startlogin(

            es.upm.etsiinf.sos.Login login18,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param addBook20
                
         */

         
                     public es.upm.etsiinf.sos.AddBookResponse addBook(

                        es.upm.etsiinf.sos.AddBook addBook20)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param addBook20
            
          */
        public void startaddBook(

            es.upm.etsiinf.sos.AddBook addBook20,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getBooksFromAuthor22
                
         */

         
                     public es.upm.etsiinf.sos.GetBooksFromAuthorResponse getBooksFromAuthor(

                        es.upm.etsiinf.sos.GetBooksFromAuthor getBooksFromAuthor22)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getBooksFromAuthor22
            
          */
        public void startgetBooksFromAuthor(

            es.upm.etsiinf.sos.GetBooksFromAuthor getBooksFromAuthor22,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param listBorrowedBooks24
                
         */

         
                     public es.upm.etsiinf.sos.ListBorrowedBooksResponse listBorrowedBooks(

                        es.upm.etsiinf.sos.ListBorrowedBooks listBorrowedBooks24)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param listBorrowedBooks24
            
          */
        public void startlistBorrowedBooks(

            es.upm.etsiinf.sos.ListBorrowedBooks listBorrowedBooks24,

            final es.upm.etsiinf.sos.client.ETSIINFLibraryCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    