package generals.ioIA.generals.ioIA;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModuloPercepcion {

	private Bot bot;
	private int ciudades[];
	private ArrayList<Integer> ciudadesConocidas;
	private ArrayList<Integer> casillasInacesibles;
	private int mapa[];
	private int generales[];
	private int ancho;
	private int alto;
	private int equipo;
	private String repeticionId;
	private int turno;
	private int ataquesPendientes;
	

	
	
	ModuloPercepcion(Bot bot){
		this.bot=bot;
		ciudades=new int[0];
		mapa=new int[0];
		equipo=-10;
		ciudadesConocidas = new ArrayList<Integer>();
		casillasInacesibles = new ArrayList<Integer>();
		ataquesPendientes = 0;
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
			//System.out.println(argsjson);
			ataquesPendientes = argsjson.getInt("attackIndex");
			JSONArray ciudades_parche_JSON = argsjson.getJSONArray("cities_diff");
			JSONArray generales_JSON = argsjson.getJSONArray("generals");
			turno = argsjson.getInt("turn");
			JSONArray mapa_parche_JSON = argsjson.getJSONArray("map_diff");

			generales=Utilities.JSONArraytoArray(generales_JSON);
			System.out.println("Generales ------------------ "+Arrays.toString(generales));
			ciudades=Utilities.parchear(ciudades,ciudades_parche_JSON);
			
			actualizarCiudadesConocidas();
			
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
	
	void actualizarCiudadesConocidas() {
		for(int i=0;i<ciudades.length;i++) {
			if(ciudadesConocidas.isEmpty())
				ciudadesConocidas.add(ciudades[i]);
			else {
				for(int j=0;j<ciudadesConocidas.size();j++) {
					if(ciudades[i]==ciudadesConocidas.get(j).intValue())
						break;
					if(ciudades[i]<ciudadesConocidas.get(j).intValue()) {
						ciudadesConocidas.add(j,ciudades[i]);
						break;
					}
				}
			}
		}
	}
	
	
	public int posicionGeneral(int general) {
		return generales[general];
	}
	
	public boolean esCiudad(int casilla){
		for(Integer ciudad : ciudadesConocidas)
		{
			if(casilla==ciudad.intValue())
				return true;
		}
		return false;
	}
	
	public boolean esNuestroGeneral(int casilla){
		
		if(casilla==generales[equipo])
			return true;	
		return false;
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
	
	public ArrayList<Integer> ciudadesPerdidasDeVista() {
		ArrayList<Integer> resul = new  ArrayList<Integer>();
		
		for(Integer ciudad : ciudadesConocidas) {
			boolean esVisible = false;
			for(int i=0;i<ciudades.length;i++){
				if(ciudades[i]==ciudad.intValue()) {
					esVisible = true;
					break;
				}	
			}
			if(!esVisible)
				resul.add(ciudad);
		}
		return resul;
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
	
	public ArrayList<Integer> getCiudadesConocidas() {
		return ciudadesConocidas;
	}

	public ArrayList<Integer> getCasillasInacesibles() {
		return casillasInacesibles;
	}
/*
	public void setCasillasInacesibles(ArrayList<Integer> casillasInacesibles) {
		this.casillasInacesibles = casillasInacesibles;
	}

	public void setCiudadesConocidas(ArrayList<Integer> ciudadesConocidas) {
		this.ciudadesConocidas = ciudadesConocidas;
	}
*/
	public int getTurno() {
		return turno;
	}

	public int getAtaquesPendientes() {
		return ataquesPendientes;
	}
	
	
	
}
