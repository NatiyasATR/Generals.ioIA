package generals.ioIA.generals.ioIA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ModuloNavegacion {
	
	private Bot bot;
	
	ModuloNavegacion(Bot bot) throws IOException{
		this.bot=bot;
		//BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/ALVARO/Desktop/ModuloNavegacionLog"+bot.getBotId()+".txt"));
	}
	
	public int[] calcularCaminoEntrePuntos(int origen,int destino) {//usando A*
		class Nodo {
			public ArrayList<Integer> camino;
			public int distanciaRecorrida;//g
			public int heuristica;//h
			Nodo(ArrayList<Integer> camino, int distanciaRecorrida, int heuristica) {
				this.camino = camino;
				this.distanciaRecorrida = distanciaRecorrida;
				this.heuristica = heuristica;
			}
		}
		ArrayList<Nodo> listaCerrada = new ArrayList<Nodo>();
		ArrayList<Nodo> listaAbierta = new ArrayList<Nodo>();
		ArrayList<Integer>caminoInicial = new ArrayList<Integer>();
		caminoInicial.add(origen);
		listaAbierta.add(new Nodo(caminoInicial,0,heuristica(origen,destino)));
		
		
		
		while(!listaAbierta.isEmpty()) {
			
			
			Nodo nodoActual = null;
			for(Nodo nodo : listaAbierta) {//En la lista abierta buscamos el nodo con menor g+h
				
				if(nodoActual==null||(nodo.distanciaRecorrida+nodo.heuristica<nodoActual.distanciaRecorrida+nodoActual.heuristica)) {
					nodoActual = nodo;
				}
			}
			listaAbierta.remove(nodoActual);
			
			
			//Control
			//System.out.println("Control"+nodoActual.camino);
				
			
			int casillaActual = nodoActual.camino.get(nodoActual.camino.size()-1);
			int ancho = bot.getModuloPercepcion().getAncho();
			int alto = bot.getModuloPercepcion().getAlto();
			
			ArrayList<Integer> sucesores = new ArrayList<Integer>();
			sucesores.add(casillaActual-ancho);
			sucesores.add(casillaActual-1);
			sucesores.add(casillaActual+ancho);
			sucesores.add(casillaActual+1);
			
			
			ArrayList<Integer> sucesoresParaBorrar = new ArrayList<Integer>();
			for(Integer sucesor:sucesores) {//eliminamos las casillas fuera del tablero y las que son montañas
				
				if(sucesor.intValue()<0||alto*ancho<=sucesor.intValue())
					sucesoresParaBorrar.add(sucesor);
				else {
					
					int terreno =bot.getModuloPercepcion().terrenoCasilla(sucesor.intValue()); 
					if(-2==terreno||terreno==-4)//montañas visibles e invisibles (-2 y -4)
						sucesoresParaBorrar.add(sucesor);
				}
			}
			for(Integer sucesor:sucesoresParaBorrar) {
				sucesores.remove(sucesor);
			}
			
			
			
			for(Integer sucesor : sucesores) {
				if(sucesor == destino) {//hemos encontrado el camino
					nodoActual.camino.add(sucesor);
					int resul[]=new int[nodoActual.camino.size()];
					for(int i=0;i<resul.length;i++)
						resul[i]=nodoActual.camino.get(i).intValue();
			    	return resul;  
				}else {// si no es el que buscamos miramos si esta en alguna de las listas con un g+h menor
					boolean mejorCaminoEncontrado = false;
					int g = nodoActual.distanciaRecorrida+1;
					int h = heuristica(sucesor,destino);
					for(Nodo nodo : listaAbierta) { // lo buscamos en la lista Abierta
						ArrayList <Integer> camino = nodo.camino;
						
						//Control
						//System.out.print("-"+camino.get(camino.size()-1));
						
						if(camino.get(camino.size()-1).intValue() == sucesor.intValue() &&nodo.heuristica+nodo.distanciaRecorrida<=g+h) {
							mejorCaminoEncontrado = true;
							break;
						}
							
					}
					if(!mejorCaminoEncontrado) {// si no lo hemos encontrado lo buscamos en la lista Cerrada
						for(Nodo nodo : listaCerrada) { 
							ArrayList <Integer> camino = nodo.camino;
							//Control
							//System.out.print("_"+camino.get(camino.size()-1));
							
							
							if(camino.get(camino.size()-1).intValue() == sucesor.intValue()/*&&nodo.heuristica+nodo.distanciaRecorrida<=g+h*/) {
								mejorCaminoEncontrado = true;
								break;
							}
								
						}
					}
					
					if(!mejorCaminoEncontrado) {//si no lo  hemos encontrado lo añadimos a la lista abierta
						ArrayList<Integer> camino = (ArrayList<Integer>) nodoActual.camino.clone();
						camino.add(sucesor);
						//Control
						//System.out.println(" Control "+origen+" "+destino+" "+camino);
						
						
						listaAbierta.add(new Nodo(camino,g,h));
					}
				}
			}
			
			listaCerrada.add(nodoActual);
			
		}
		return null; 
	}
	
	
	public int heuristica(int origen, int objetivo) {//usando Manhattan 
		int ancho = bot.getModuloPercepcion().getAncho();
		Coordenadas coordenadasOrigen = new Coordenadas();
		coordenadasOrigen.setCoordenadasCasilla(origen, ancho);
		Coordenadas coordenadasObjetivo = new Coordenadas();
		coordenadasObjetivo.setCoordenadasCasilla(objetivo, ancho);
		
		return Math.abs(coordenadasOrigen.getX()-coordenadasObjetivo.getX())+Math.abs(coordenadasOrigen.getY()-coordenadasObjetivo.getY());
	}
}
