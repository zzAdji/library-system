package com.library.ui.console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.attribute.UserDefinedFileAttributeView;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.zip.InflaterInputStream;

import com.library.model.User;

public class UserConsole {
    public  static ArrayList <User> users = new ArrayList<User>();

     
    public static  void save(User user){
               users.add(user);

    }
    public static void update (User user){
        
        
    }
    public static void deleteById(String id){
           for(User user : users){
                if(user.getId() == id){
                    users.remove(user);
                }
           }
    }
    public static Optional<User> findBYEmail(String email){
             Optional<User> U  = Optional.empty();
             for(User user : users){
                if(user.getEmail() == email){
                    U = Optional.of(user);
                }
           }
            return U ;
              
    } 
     public  static Optional<User> findById(String id){
         Optional<User> U  = Optional.empty();
        
         for(User user : users){
                if(user.getEmail() == id){
                    U = Optional.of(user);
                }
           }
            return U ;
    } 
     public static  Optional<User> findByCardNumber(String cardNumber){
        Optional<User> U  = Optional.empty();
        
         for(User user : users){
                if(user.getEmail() == cardNumber){
                    U = Optional.of(user);
                }
           }
            return U ;
    } 
    public static List<User> findAll() {
             List<User> Us =  users.subList(0,users.size()) ; 
             return Us ;         
    }
     public static void main (String [] args ){
         int choise = 0 ;

    
        User mmm = new User( "id",  "cardNumber",  "password",  "firstName",  "lastName",  "email",  "phone",  null,  null,  null);
        UserConsole.save(mmm);

     }
}
