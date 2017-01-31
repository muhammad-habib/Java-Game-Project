/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author kazafy
 */
public class UserController {
    
    public static int register( User user){
        try {
            Connection con = DBConnection.openConnection();
            PreparedStatement stmt = con.prepareStatement("INSERT INTO user (name,email,password,score) VALUES(?,?,?,0);");

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();


            con.close();
            return 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            int error = ex.getErrorCode();
            if (error == 1062){
                //email already exist
                return 1;
            }
            else{
                return 10;
            }
        }
        
        
    }
    public static boolean login(User user){
        try {
            Connection con = DBConnection.openConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM user WHERE email=? AND password=?");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static boolean logout(User user){
        return true;
    }
}