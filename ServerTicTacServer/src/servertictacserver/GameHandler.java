/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

import control.UserController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import model.Request;
import model.User;

/**
 *
 * @author kazafy
 */
    class GameHandler extends Thread {

        ObjectInputStream ois;
        ObjectOutputStream ous;
        User user = null;
        
        static Vector<GameHandler> clientsVector = new Vector<>();
        public GameHandler(Socket cs) {
            try {
                ous = new ObjectOutputStream(cs.getOutputStream());   
                ois = new ObjectInputStream(cs.getInputStream());                             
                clientsVector.add(this);
                start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            while (true) {

                Request request;
                try {
                    request = (Request) ois.readObject();
                    System.out.println("req"+request);
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                    switch(request.getType()){
//////////////////////////////////////////////////////////////////////////////////////////////////
                        case Setting.REG:
                            user = (User)request.getObject();
                            if(UserController.register(user)){ 
                                // if register ok send list off available players to client
                                user.setStatus(Setting.AVAILABLE);
                                request.setType(Setting.REG_OK);                                
                                List l = new ArrayList<User>();                                
//                                l.add(user);
//                                l.add(new User("kemo", "email", "123", "123"));
                                for (GameHandler gameHandler : clientsVector){
                                    if(gameHandler.user.getStatus()== Setting.AVAILABLE)
                                        l.add(gameHandler.user);
                                }
                                request.setObject(l);
                                this.ous.writeObject(request);
                            } 
                            else{
                                // error in registration  send to client error message
                                this.ous.writeObject(request);
                            }
                            break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                        default:
                            System.out.println("defualt");
                    }
/////////////////////////////////// end switch ////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
                    
                    if(request == null)
                    {
                        
                        clientsVector.remove(this);
                        System.out.println("null this :"+clientsVector.size());
                        ous.close();
                        ois.close();
                        break;
                    
                    }
//                    sendMessageToAll(request);      
                } catch (Exception ex) {
                    try {
                        clientsVector.remove(this);
                        System.out.println(" this :"+clientsVector.size());
                        ous.close();
                        ois.close();
                        ex.printStackTrace();
                        break;
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        void sendMessageToAll(Request msg) throws IOException {
            for (GameHandler ch : clientsVector) {
                ch.ous.writeObject(msg);         
            }
        }
    }
