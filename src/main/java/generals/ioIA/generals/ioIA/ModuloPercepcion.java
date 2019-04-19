package generals.ioIA.generals.ioIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModuloPercepcion {

	private Bot bot;
	private int ciudades[];
	private int mapa[];
	private int generales[];
	private int ancho;
	private int alto;
	private int equipo;
	private String repeticionId;
	

	
	
	ModuloPercepcion(Bot bot){
		this.bot=bot;
		ciudades=new int[0];
		mapa=new int[0];
		equipo=-10;
	}
	
	public void iniciarPartidaDatos(JSONObject argsjson) {
		try {
			repeticionId = argsjson.getString("replay_id");
			equipo=argsjson.getInt("playerIndex");
			System.out.println(argsjson);
    		
    		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void actualizarPartidaDatos(JSONObject argsjson){
		try {
			
			int ataquesPendientes = argsjson.getInt("attackIndex");
			JSONArray ciudades_parche_JSON = argsjson.getJSONArray("cities_diff");
			JSONArray generales_JSON = argsjson.getJSONArray("generals");
			int turno = argsjson.getInt("turn");
			JSONArray mapa_parche_JSON = argsjson.getJSONArray("map_diff");

			generales=Utilities.JSONArraytoArray(generales_JSON);
			ciudades=Utilities.parchear(ciudades,ciudades_parche_JSON);
			mapa=Utilities.parchear(mapa,mapa_parche_JSON);
			
			ancho=mapa[0];
			alto=mapa[1];
			
			//Terreno(Unidades)
			System.out.println(bot.getBotId()+": Turno "+turno);
			System.out.println(" estado de las unidades en el mapa");
			/*
			int k=2;
			for(int i=0;i<alto;i++){
				for(int j=0;j<ancho;j++) {
					System.out.print(" | "+mapa[k+ancho*alto]+"("+mapa[k]+")");	
					k++;
				}
				System.out.println();
			}
			*/
			
			
					
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int posicionGeneral(int general) {
		return generales[general];
	}
	
	public int[] getGenerales() {
		return generales;
	}
	
	
	public int unidadesCasilla(int casilla) {
		return(mapa[2+casilla]);
	}
	
	public int terrenoCasilla(int casilla) {
		return(mapa[2+casilla+ancho*alto]);
	}
	
	public String getRepeticionId() {
		return repeticionId;
	}

	public int[] getCiudades() {
		return ciudades.clone();
	}
	
	public int getAncho() {
		return ancho;
	}
	
	public int getAlto() {
		return alto;
	}

	public int getEquipo() {
		return equipo;
	}
	
	
}
