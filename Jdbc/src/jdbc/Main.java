/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc;

import java.sql.SQLException;

/**
 *
 * @author kosti
 */
public class Main {
    public static void main(String[] args) throws Exception {
       DbServer d = new DbServer();
     
 
//   Authors pushkin = new  Authors (6, "Pushkin", "poem and prose");
//   d.addAuthor(pushkin);
    Authors lermontov = new  Authors (5, "Лермонтов", " super new");
//    d.addAuthor(lermontov);
//
    Documents tom1Lermontov = new Documents (5, "Собрание сочинений. Том 1", "Поэзия 1830-1835 гг", 5 );  
    d.addDocument (tom1Lermontov, lermontov);
    Documents tom2Lermontov = new Documents (6, "Собрание сочинений. Том 2", "Поэзия 1836-1838 гг", 5 );  
    d.addDocument (tom2Lermontov, lermontov);


    //d.findDocumentByAuthor(pushkin); 
    //d.findDocumentByAuthor(lermontov); 
    // d.findDocumentByContent(null);
    // d.deleteAuthor (lermontov );
  // System.out.println(d.deleteAuthor (5));  
                  
    }
    }


