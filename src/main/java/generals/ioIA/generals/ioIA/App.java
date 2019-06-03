package generals.ioIA.generals.ioIA;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.border.Border;

import org.json.JSONArray;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) 
    {
        try {

        	
        	
        	/************************************************/
			final Bot bot1 = new Bot(1);
			//final Bot bot2 = new Bot(2);
			
			
			Random rg = new Random();
			String nobrePartida="partida"+rg.nextInt();
			
			
			
        	JFrame frame = new JFrame("Control");
        	frame.addWindowListener(new WindowAdapter() {
        		@Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("WindowClosing");
                    bot1.desconectar();
        			//bot2.desconectar();
                    System.exit(0);
                }
        	});

        	frame.setVisible(true);
        	frame.setLayout(new GridLayout(1,2));
        	JButton brendirse = new JButton("Rendirse");
        	brendirse.setAction(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					bot1.rendirse();
					//bot2.rendirse();
				}
        	});
        	brendirse.setText("Rendirse");
        	brendirse.setSize(100, 100);
        	frame.getContentPane().add(brendirse);
        	
        	JButton bPnull = new JButton("Proxima Partida null");
        	bPnull.setAction(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					bot1.setPartida(null,null);
					//bot2.setPartida(null,null);
				}
        	});
        	bPnull.setText("Proxima Partida null");
        	frame.getContentPane().add(bPnull);
        	
        	frame.setSize(500, 500);
        	frame.pack();
			
			

			//bot1.setPartida("Privada","111111");
			//bot2.setPartida("Privada","111111");
        	bot1.setPartida("FFA",null);
        	
			
			bot1.start();
			//bot2.start();
			
			System.out.println("\""+System.in.read()+"\"");
			System.in.skip(System.in.available());
			
			bot1.setPartida(null,null);
			//bot2.setPartida(null,null);
			
			System.out.println("\""+System.in.read()+"\"");
			System.in.skip(System.in.available());
			
			
			bot1.rendirse();
			//bot2.rendirse();

			
			System.out.println("\""+System.in.read()+"\"");
			System.in.skip(System.in.available());
			
			
			bot1.salirPartida();
			//bot2.salirPartida();
			bot1.desconectar();
			//bot2.desconectar();
			

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public void auxiliar() {
    	//System.out.println( "Hello World!" );
        final String user_id1 = "jfgury_bot_1";
        String username1 = "[Bot] jfgury Bot 1";
        String user_id2 = "jfgury_bot_2";
        String username2 = "[Bot] jfgury Bot 2";
        int i = 0;
    	
        final Semaphore s = new Semaphore(0);	
    	
        
        
        while(true){
        		
        	
	        try {
	        	final Socket socket1;
				socket1 = IO.socket("http://botws.generals.io");
	
				
		        
		        socket1.on(Socket.EVENT_CONNECT,  new Emitter.Listener() {
	
		        	public void call(Object... args) {
		        		System.out.println("Connected");
		        		s.release(1);
		        	}
	
		        });
		        
		        
		        socket1.on(Socket.EVENT_DISCONNECT,  new Emitter.Listener() {
	
		        	public void call(Object... args) {
		        		System.out.println("Disconnected");
		        	}
	
		        });
		        
		        socket1.connect();
		        
		        s.acquire(1);
		        
		        
		        
		        // Set the username for the bot.
		        // This should only ever be done once. See the API reference for more details.
		        /*
		        socket1.emit("set_username", user_id1, username1);
	
		        socket1.on("error_set_username",  new Emitter.Listener() {
	
		        	public void call(Object... args) {
		        		System.out.println(args[0]);
		        		s.release(1);
		        	}
	
		        });
		        
		        s.acquire(1);
		        */
		        
				/*
		        String game_id = "jfgurybnsdlsorfkjhre";
		        socket1.emit("join_private", game_id, user_id1);
	
		        System.out.println("Joined custom game at http://bot.generals.io/games/" 
		        		+ encodeURIComponent(custom_game_id));
		        while(System.in.read()!='A');
		        
		        socket1.emit("set_force_start", game_id, true);
		        */
		        
		        Thread.sleep(3000);
		        
		        socket1.emit("play",user_id1);
		        Thread.sleep(3000);
		        socket1.emit("set_force_start",null, true);
		        
	
		        System.out.println("Ready");
		        
		        socket1.on("game_start",  new Emitter.Listener() {
	
		        	public void call(Object... args) {
		        		System.out.println(args[0]);
		        		System.out.println("Comenzado");
		        		socket1.emit("surrender", user_id1);
		        		socket1.emit("leave_game", user_id1);
		        		//socket1.disconnect();
		        		s.release(1);
		        	}
	
		        });
		        
		        s.acquire(1);
		        
		        
		        //socket1.emit("cancel");
	
		        
		        //socket1.disconnect();
	
				
				Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
				System.out.println(threadSet.size());
				
		        
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        i++;
	        System.out.println("Van: "+i);
        
        }
        
        
    }
    
    
    
    
    
    /*********************************************************/
}
