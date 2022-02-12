/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author kosti
 */

public class DbServer implements IDbService {
    private static String url = "jdbc:derby://localhost:1527/test";
    private static String user = "topchilo";
    private static String psw = "zyf201";
    private static Connection con;
    private Statement st;
    private PreparedStatement ps;
    public static List<Authors> list = new ArrayList<Authors>();
    public static List<Documents> listDoc = new ArrayList<Documents>();
    public DbServer() {

    };

    public DbServer(String url, String user, String psw) {
        this.url = url;
        this.user = user;
        this.psw = psw;
    }
    
    

   public void init() throws SQLException {
        con = DriverManager.getConnection(url, user, psw);
        if (con == null) {
            throw new SQLException("Connection is null");
        }
        st = con.createStatement();
    }
   
   public  List<Authors> getAuthors (){
   
         try {
             init();
           Statement stm = con.createStatement();
           ResultSet rs = stm.executeQuery("SELECT * FROM AUTHORS");
           Authors authorFromTable;
            while(rs.next()){
               authorFromTable = new Authors(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3));
    
                list.add(authorFromTable);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
        }
            return list;
    }
   
   public  List<Documents> getDocs (){
   
         try {
           init();
           Statement stm = con.createStatement();
           ResultSet rs = stm.executeQuery("SELECT * FROM documents");
           Documents docFromTable;
            while(rs.next()){
               docFromTable = new Documents(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDate(4),
                        rs.getInt(5));
                listDoc.add(docFromTable);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
        }
            return listDoc;
    }
     
    @Override
   public  boolean addAuthor(Authors author) throws DocumentException {
   boolean result = true;
      getAuthors ();
      List<Integer> listIDAuth = new ArrayList<>() ;     
      for (int i=0; i<list.size(); i++ ){
          int id = list.get(i).getAuthor_id();
          listIDAuth.add(id);
      }
       List<String> listNameAuth = new ArrayList<>() ; 
       for (int i=0; i<list.size(); i++ ){
          String name = list.get(i).getAuthor();
          listNameAuth.add(name);
      }
   
   if (author.getAuthor() != null) {
      if (!listNameAuth.contains(author.getAuthor()) & listIDAuth.contains(author.getAuthor_id())) 
         throw new DocumentException("ID is not not unique");   

      if (!listNameAuth.contains(author.getAuthor()) & !listIDAuth.contains(author.getAuthor_id())) {
          try {
            init();
            String sql = "INSERT INTO authors VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, author.getAuthor_id());
            preparedStatement.setString(2, author.getAuthor());
            preparedStatement.setString(3, author.getNotes());
            preparedStatement.executeUpdate();
            list.clear();
            getAuthors ();}
         catch (SQLException ex) {Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);} 
         result = true;
          }}
   else if  (author.getAuthor() == null & author.getNotes() != null)  { 
           try {
            init();
            String sql = "UPDATE authors SET NOTES = (?) WHERE idAuth = (?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, author.getNotes());
            preparedStatement.setInt(2, author.getAuthor_id());
            preparedStatement.executeUpdate();
            list.clear();
            getAuthors ();
              }
           catch (SQLException ex) {
           Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);}

      result = false;    
      }    
      else  throw new DocumentException("Author is not added"); 
 return result; 
  }

    @Override
    public boolean addDocument(Documents doc, Authors author) throws DocumentException {
       getAuthors(); 
       getDocs();
        boolean result = false;
        List<Integer> listIDDoc = new ArrayList<>() ;     
        
      for (int i=0; i<listDoc.size(); i++){
          int id = listDoc.get(i).getDocument_id();
         listIDDoc.add(id);
      }
       List<String> listNameDoc = new ArrayList<>() ; 
       for (int i=0; i<listDoc.size(); i++){
          String name = listDoc.get(i).getTitle();
          listNameDoc.add(name);
      }

       if (!list.contains(author) || author == null ){      
       throw new DocumentException("The author is not found");}

      else if (doc.getTitle() == null & !listIDDoc.contains(doc.getDocument_id())  ){      
          throw new DocumentException("Title can not be empty");} 
         
        else if (doc.getTitle() != null) {
              if (!listNameDoc.contains(doc.getTitle()) & listIDDoc.contains(doc.getDocument_id()))    
                throw new DocumentException("ID is not not unique");
             
              else if (!listIDDoc.contains(doc.getDocument_id())){
                  try {
                    init();
                    String sql = "INSERT INTO documents VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setInt(1, doc.getDocument_id());
                    preparedStatement.setString(2, doc.getTitle());
                    preparedStatement.setString(3, doc.getText());
                    preparedStatement.setDate(4, new java.sql.Date(doc.getDate().getTime()));
                    preparedStatement.setInt(5, doc.getAuthor_id());
                    preparedStatement.executeUpdate();
                    listDoc.clear();
                    getDocs();}
                 catch (SQLException ex) {Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
                 result = true;
                 } 
             }      
              else if (listNameDoc.contains(doc.getTitle()) & listIDDoc.contains(doc.getDocument_id()))  { 
                   if (doc.getText() != null){
                   try {
                   init();
                    String sql = "UPDATE documents SET TEXT = (?), DATE = (?) WHERE idDoc = (?)";
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, doc.getText());
                    preparedStatement.setDate(2, new java.sql.Date(doc.getDate().getTime()));
                    preparedStatement.setInt(3, doc.getDocument_id());
                    preparedStatement.executeUpdate();
                    listDoc.clear();
                    getDocs();}

                   catch (SQLException ex) {
                   Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
               }        

           result = false;
       }
                   if (doc.getAuthor_id() != 0){
                   try {
                   init();
                   String sql = "UPDATE documents SET author = (?), DATE = (?) WHERE idDoc = (?)";
                   PreparedStatement preparedStatement = con.prepareStatement(sql);
                   preparedStatement.setInt(1, doc.getAuthor_id());
                   preparedStatement.setDate(2, new java.sql.Date(doc.getDate().getTime()));
                   preparedStatement.setInt(3, doc.getDocument_id());
                   preparedStatement.executeUpdate();
                   listDoc.clear();
                   getDocs();}

               catch (SQLException ex) {
               Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
           }        

               result = false;
               
           }}
                 else  throw new DocumentException("Document is not added");
        }
              
        if (doc.getTitle() == null) {
               if (listIDDoc.contains(doc.getDocument_id()) & doc.getText() != null){
               try {
               init();
                String sql = "UPDATE documents SET TEXT = (?), DATE = (?) WHERE idDoc = (?)";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, doc.getText());
                preparedStatement.setDate(2, new java.sql.Date(doc.getDate().getTime()));
                preparedStatement.setInt(3, doc.getDocument_id());
                preparedStatement.executeUpdate();
                listDoc.clear();
                getDocs();}

               catch (SQLException ex) {
               Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
           }        

       result = false;
    }
                   if (listIDDoc.contains(doc.getDocument_id()) & doc.getAuthor_id() != 0){
                   try {
                   init();
                   String sql = "UPDATE documents SET author = (?), DATE = (?) WHERE idDoc = (?)";
                   PreparedStatement preparedStatement = con.prepareStatement(sql);
                   preparedStatement.setInt(1, doc.getAuthor_id());
                   preparedStatement.setDate(2, new java.sql.Date(doc.getDate().getTime()));
                   preparedStatement.setInt(3, doc.getDocument_id());
                   preparedStatement.executeUpdate();
                   listDoc.clear();
                   getDocs();}

               catch (SQLException ex) {
               Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);
           }        
               result = false;
               
           }
        else  throw new DocumentException("Document is not added");
        }
       
    return result;
     }
  

    @Override
    public Documents[] findDocumentByAuthor(Authors author) throws DocumentException {
        getDocs();
        getAuthors ();
        Documents[] docArray = new Documents[listDoc.size()];
        int ID = 0;
        if (author.getAuthor_id() == 0 & author.getAuthor() == null )
           throw new DocumentException("Search is not started");
        
        else if (author.getAuthor_id() == 0){
            for (int i=0; i<list.size(); i++){
            if (list.get(i).getAuthor().equals(author.getAuthor()) ){
                 ID = list.get(i).getAuthor_id();}
          }}
        else if   (author.getAuthor_id() != 0){
            ID = author.getAuthor_id();
        }
         
    int j = 0;
          for (int k=0; k<listDoc.size(); k++)
          {
              if (listDoc.get(k).getAuthor_id() == ID)
            {
             docArray[j] = new Documents(listDoc.get(k).getDocument_id(), 
             listDoc.get(k).getTitle(), listDoc.get(k).getText(), listDoc.get(k).getDate(),
              listDoc.get(k).getAuthor_id()  );
            j++;
            }   
          }
        for (Documents docArray1 : docArray) {
            {if (docArray1 != null)
                System.out.println(docArray1);
            }
        }
        
        if ( docArray[0] == null){
            System.out.println("null");
            return null;         
        }
        
        else return docArray; 
    }
    
    
    @Override
    public Documents[] findDocumentByContent(String content) throws DocumentException {
        getDocs();
        getAuthors ();
        Documents[] docArray = new Documents[listDoc.size()];
        if ( content == null )
           throw new DocumentException("Search is not started. No content");
          
          int j = 0;
          for (int k=0; k<listDoc.size(); k++)
          {
              if (listDoc.get(k).getText().contains(content))
            {
             docArray[j] = new Documents(listDoc.get(k).getDocument_id(), 
             listDoc.get(k).getTitle(), listDoc.get(k).getText(), listDoc.get(k).getDate(),
              listDoc.get(k).getAuthor_id()  );
            j++;
            }   
          }
        for (Documents docArray1 : docArray) {
            {if (docArray1 != null)
                System.out.println(docArray1);
            }
        }
        
        if ( docArray[0] == null){
            System.out.println("null");
            return null;         
        }
        
        else return docArray; 
    }
     

    @Override
    public boolean deleteAuthor(Authors author) throws DocumentException {
        boolean result = true;
        int ID = 0;
        getAuthors ();
        List<Integer> listIDAuth = new ArrayList<>() ;     
        for (int i=0; i<list.size(); i++ ){
          int id = list.get(i).getAuthor_id();
          listIDAuth.add(id);
        }

         if (author.getAuthor_id() == 0 & author.getAuthor() == null )
           throw new DocumentException("Author is not be null");

         if (author.getAuthor_id() == 0){
            for (int i=0; i<list.size(); i++){
            if (list.get(i).getAuthor().equals(author.getAuthor()) ){
                 ID = list.get(i).getAuthor_id();}
          }}
          else if   (author.getAuthor_id() != 0){
            ID = author.getAuthor_id();
         }

        if (listIDAuth.contains(ID)){    
          try {
            init();
            String sq1 = "DELETE FROM documents WHERE author = (?)";
            PreparedStatement preparedStatement = con.prepareStatement(sq1);
            preparedStatement.setInt(1, ID);
            preparedStatement.executeUpdate();
            String sq2 = "DELETE FROM authors WHERE idAuth = (?)";
            preparedStatement = con.prepareStatement(sq2);
            preparedStatement.setInt(1, ID);
            preparedStatement.executeUpdate();
            listDoc.clear();
            list.clear();
            getAuthors ();
            getDocs();    
           result = true; 
           }
         catch (SQLException ex) {Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);} 
       } 
     else  throw new DocumentException("Author is not delete"); 
      return result; 
 
}


    @Override
    public boolean deleteAuthor(int id) throws DocumentException {
        boolean result = true;
        getAuthors ();
         if (id == 0)
           throw new DocumentException("Author is not be null");
        
          List<Integer> listIDAuth = new ArrayList<>() ;     
        for (int i=0; i<list.size(); i++ ){
          int ID = list.get(i).getAuthor_id();
          listIDAuth.add(ID);
        }     
        
        if (listIDAuth.contains(id)){    
          try {
            init();
            String sq1 = "DELETE FROM documents WHERE author = (?)";
            PreparedStatement preparedStatement = con.prepareStatement(sq1);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            String sq2 = "DELETE FROM authors WHERE idAuth = (?)";
            preparedStatement = con.prepareStatement(sq2);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            listDoc.clear();
            list.clear();
            getAuthors ();
            getDocs();    
           result = true; 
           }
         catch (SQLException ex) {Logger.getLogger(Authors.class.getName()).log(Level.SEVERE, null, ex);} 
         } 
     else  throw new DocumentException("ID is not not correct"); 
           return result; 
        }
    
    @Override
    public void close() throws SQLException {
        if (con != null && con.isValid(10)) {
            con.close();
        }
    }
    
}
