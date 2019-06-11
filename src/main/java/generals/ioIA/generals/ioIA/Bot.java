package generals.ioIA.generals.ioIA;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Bot extends Thread{
	private String botId;
	private String botName;
	private Socket socket;
	private String tipoPartida;
	private String partidaId;
	
	
	private boolean conectado;
	private boolean partidaFinalizada;
	private ModuloPercepcion moduloPercepcion;
	private ModuloDecision moduloDecision;
	private ModuloNavegacion moduloNavegacion;
	private Lock lock;
    private Condition condicionConectado;
    private Condition condicionFinalizada;
    
    private JFrame frame;
    private JTextArea ta;

	
	Bot(int numBot) throws URISyntaxException, InterruptedException, IOException{
		botId = "ATR_bot_"+numBot;
		botName = "[Bot] ATR Bot "+numBot;
		socket = IO.socket("http://botws.generals.io");

		conectado = false;
		moduloPercepcion = new ModuloPercepcion(this);
		moduloDecision = new MaquinaEstados(this);//MaquinaEstados o ArbolDecisiones
		moduloNavegacion = new ModuloNavegacion(this);
		lock = new ReentrantLock();
		condicionConectado =  lock.newCondition();
		condicionFinalizada =  lock.newCondition();	
		
		JFrame frame = new JFrame(botId);
		ta =new JTextArea();
		frame.add( ta);
		ta.setText("Bot: "+botId+"\n");
		frame.setVisible(true);
		frame.pack();
	}
	
	public void setPartida(String tipoPartida,String partidaId) {
		this.tipoPartida=tipoPartida;
		this.partidaId=partidaId;
	}
	
	public void run(){
		try {
			while(tipoPartida!=null) {
				conectarse();
				if(tipoPartida=="Privada")
					unirsePartidaPersonalizada(partidaId);
				else if(tipoPartida=="FFA")
					unirseFFA();
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void conectarse() throws InterruptedException {
		
		socket.on(Socket.EVENT_CONNECT,  new Emitter.Listener() {
        	public void call(Object... args) {
        		lock.lock();
        		try{
        			conectado = true;/*aqui*//////////////
        			System.out.println(botName+" conectado");
        			condicionConectado.signalAll();
        		}finally {
        			lock.unlock();
        		}	
        	}
        });
		socket.on(Socket.EVENT_DISCONNECT,  new Emitter.Listener() {
        	public void call(Object... args) {
        		System.out.println(botName+" desconectado");
        	}

        });
		
		socket.on("stars",  new Emitter.Listener() {
        	public void call(Object... args) {
        		System.out.println(args[0]);
        	}

        });
		
		socket.connect();
		
		
		lock.lock();
		try{
			while(!conectado) {
				System.out.println("Todavia no esta conectado");
				condicionConectado.await();
			}
		}finally {
			lock.unlock();
		}

		
		
		//socket.emit("set_username", botId, botName);
		socket.on("error_set_username",  new Emitter.Listener() {
        	public void call(Object... args) {
        		System.out.println(args[0]);
        	}

        });
	}
	
	
	public void unirsePartidaPersonalizada(final String gameId) throws InterruptedException {
		partidaFinalizada = false;
		socket.emit("join_private", gameId, botId);
		 System.out.println(botName + " joined custom game at http://bot.generals.io/games/" 
	        		+ Utilities.encodeURIComponent(gameId));
		Thread.sleep(3000);
		socket.emit("set_force_start",gameId, true);
		
		socket.on("game_start",  new Emitter.Listener() {
        	public void call(Object... args) {
        		gameStart(args);	
        	}
        });
		
		socket.on("game_update",  new Emitter.Listener() {
        	public void call(Object... args) {
        		gameUpdate(args);
        		
        			
        	}
        });
		
		socket.on("game_lost",  new Emitter.Listener() {
        	public void call(Object... args) {
        		lose(args);
        	}
        });
		
		socket.on("game_win",  new Emitter.Listener() {
        	public void call(Object... args) {
        		win(args);
        	}
        });
		
		socket.on("chat_message",new Emitter.Listener() {
        	public void call(Object... args) {
        		JSONObject argsjson = (JSONObject) args[1];
        		try {
					System.out.println(argsjson.get("text"));
					socket.emit("set_force_start",gameId, true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
		
		lock.lock();
		try{
			while(!partidaFinalizada) {
				System.out.println("La partida se esta jugando");
				condicionFinalizada.await();
			}
		}finally {
			lock.unlock();
		}
		
    }
	
	public void unirseFFA() throws InterruptedException {
		partidaFinalizada = false;
		socket.emit("play", botId);
		Thread.sleep(3000);
		socket.emit("set_force_start", true);
		
		socket.on("game_start",  new Emitter.Listener() {
        	public void call(Object... args) {
        		gameStart(args);	
        	}
        });
		
		socket.on("game_update",  new Emitter.Listener() {
        	public void call(Object... args) {
        		gameUpdate(args);
        		
        			
        	}
        });
		
		socket.on("game_lost",  new Emitter.Listener() {
        	public void call(Object... args) {
        		lose(args);
        	}
        });
		
		socket.on("game_win",  new Emitter.Listener() {
        	public void call(Object... args) {
        		win(args);
        	}
        });
		
		socket.on("chat_message",new Emitter.Listener() {
        	public void call(Object... args) {
        		JSONObject argsjson = (JSONObject) args[1];
        		try {
					System.out.println(argsjson.get("text"));
					socket.emit("set_force_start", true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
		
		
		
		lock.lock();
		try{
			while(!partidaFinalizada) {
				System.out.println("La partida se esta jugando");
				condicionFinalizada.await();
			}
		}finally {
			lock.unlock();
		}
	}
	
	
	private void gameStart(Object... args) {
		JSONObject argsjson = (JSONObject) args[0];
		moduloPercepcion.iniciarPartidaDatos(argsjson);
		String repeticionId = moduloPercepcion.getRepeticionId();
		
		String text = ta.getText();
		
		text+="\n\n"+"Comenzado partida. La repeticion estara disponible en: http://bot.generals.io/replays/"
	    		+ Utilities.encodeURIComponent(repeticionId)+"\n";
		ta.setText(text);
		frame.pack();
	}
	
	private void gameUpdate(Object... args) {
		JSONObject argsjson = (JSONObject) args[0];
		//System.out.println(argsjson);
		moduloPercepcion.actualizarPartidaDatos(argsjson);
		//Tomar decision y actuar
		if(moduloPercepcion.getAtaquesPendientes()==0) {
			Movimiento movimiento=moduloDecision.siguienteMovimiento();
			if(movimiento!=null) {
				System.out.println("Ataque desde "+movimiento.origen+" hacia "+movimiento.destino);
				socket.emit("attack",movimiento.origen,movimiento.destino,movimiento.is50);
			}else System.out.println("No se ataca");
		}else System.out.println("Ataques Pendientes");
	}
	
	private void win(Object... args) {
		String text = ta.getText();
		text+="Victorioso\n";
		ta.setText(text);
		salirPartida();
		try{
			socket.emit("stars_and_rank", this.botId);
			partidaFinalizada = true;
			condicionFinalizada.signalAll();
		
		}finally {
			lock.unlock();
		}
	}
	
	private void lose(Object... args) {
		JSONObject argsjson = (JSONObject) args[0];
		try {
			String text = ta.getText();
			text+="Derrotado, asesino: "+ argsjson.getInt("killer");
			ta.setText(text);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		salirPartida();
		
		lock.lock();
		try{
			socket.emit("stars_and_rank", this.botId);
			partidaFinalizada = true;
			condicionFinalizada.signalAll();
		
		}finally {
			lock.unlock();
		}
	}
	
	
	public void rendirse() {
		socket.emit("surrender");
	}
	public void salirPartida() {
		System.out.println("abandonando partida");
		socket.emit("leave_game");	
	}
	public void desconectar() {
		socket.disconnect();
	}
	
	
	
	
	public String getBotId() {
		return botId;
	}


	public String getBotName() {
		return botName;
	}


	public ModuloPercepcion getModuloPercepcion() {
		return moduloPercepcion;
	}


	public ModuloDecision getModuloDecision() {
		return moduloDecision;
	}

	public ModuloNavegacion getModuloNavegacion() {
		return moduloNavegacion;
	}

	
}
