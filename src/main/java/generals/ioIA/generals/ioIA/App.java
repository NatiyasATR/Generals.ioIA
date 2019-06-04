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
   
   
    
    
    
    
    /*********************************************************/
}
