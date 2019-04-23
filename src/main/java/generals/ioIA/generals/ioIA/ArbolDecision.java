package generals.ioIA.generals.ioIA;

import java.util.ArrayList;

public class ArbolDecision extends ModuloDecision{
	
	
	
	ArbolDecision(Bot bot) {
		super(bot);
		
	}

	public void tomaDecision() {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		ModuloNavegacion moduloNavegacion = bot.getModuloNavegacion();
		
		int[] generalEnPeligro = generalEnPeligro(distanciaSeguridad);
		if(generalEnPeligro!=null)//Nuestro general esta en peligro
		{
			System.out.println("General En Peligro!!!!!!!");
			int general = generalEnPeligro[0];
			int unidadesEnemigas = moduloPercepcion.unidadesCasilla(generalEnPeligro[1]);
			
			movimientoActual = defenderContra(general,unidadesEnemigas);
			posicionMovimientoActual = 0;
			
		}else//Nuestro general NO esta en peligro
		{
			ArrayList<Integer> generalesEnemigos = generalesEnemigosConocidos();
			
			if(generalesEnemigos.size()>0) {//Nuestro general NO esta en peligro, conocemos la posicion de al menos un general enemigo
				int generalElegido = -1;
				int ejercitoElegido = -1;
				
				for(int i=0;i<generalesEnemigos.size();i++) {
					int general = generalesEnemigos.get(i);
					int unidadesGeneral = moduloPercepcion.unidadesCasilla(general);
					ArrayList<Integer> ejercitos = ejercitosPropiosMayoresQue(Math.round(unidadesGeneral*proporcionSuperioridad)+1);
					if(ejercitos.size()>0) {// existe al menos un ejercito que pueda atacar este general
						generalElegido = general;
						ejercitoElegido = buscarCasillaValidaMasCercana(ejercitos,general);
					}
					
				}
				
				if(generalElegido!=-1) {//Alguno de los ejercitos puede atacar alguno de los generales
					System.out.println("Atacando General  "+generalElegido);
					movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitoElegido, generalElegido);
					posicionMovimientoActual = 0;
				}else {//No hay ningun ejercito suficientemente grande
					System.out.println("No podemos atacar ningun general "+generalesEnemigos);
					ArrayList<Integer>ejercitos = ejercitosPropiosOrdenados();
					if(ejercitos.size()>1) {
						movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitos.get(1), ejercitos.get(0));
						System.out.println("Reuniendo ejercitos"+ejercitos.get(1)+"->"+ ejercitos.get(0));
					}
					else {
						System.out.println("Reuniendo ejercitos"+ejercitos.get(0)+"->"+ ejercitos.get(0));
						movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitos.get(0), ejercitos.get(0));
					} 
					posicionMovimientoActual = 0;
				}
				
				
			}else {//Nuestro general NO esta en peligro, no conocemos la posicion de al menos un general
				ArrayList<Integer> ciudades = ciudadesNoPropiasConocidas();
				if(ciudades.size()>0) {//Nuestro general NO esta en peligro, no conocemos la posicion de ningun general pero conocemos la posicion de al menos una ciudad 
					
					int ciudadElegida = -1;
					int ejercitoElegido =-1;
					
					for(int i=0;i<ciudades.size();i++) {
						int ciudad = ciudades.get(i);
						int unidadesCiudad = moduloPercepcion.unidadesCasilla(ciudad);
						ArrayList<Integer> ejercitos = ejercitosPropiosMayoresQue(Math.round(unidadesCiudad*proporcionSuperioridad)+1);
						if(ejercitos.size()>0) {// existe al menos un ejercito que pueda atacar esta ciudad
							ciudadElegida = ciudad;
							ejercitoElegido = buscarCasillaValidaMasCercana(ejercitos,ciudad);
						}
					}
					
					if(ciudadElegida!=-1) {//Alguno de los ejercitos puede tomar alguna de las ciudades
						System.out.println("Atacando Ciudad  "+ciudadElegida);
						movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitoElegido, ciudadElegida);
						posicionMovimientoActual = 0;
					}else {//Ningun ejercito es suficientemente grande
						System.out.println("No podemos atacar ninguna ciudad "+ciudades);
						ArrayList<Integer>ejercitos = ejercitosPropiosOrdenados();
						if(ejercitos.size()>1) {
							movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitos.get(1), ejercitos.get(0));
							System.out.println("Reuniendo ejercitos"+ejercitos.get(1)+"->"+ ejercitos.get(0));
						}
						else {
							System.out.println("Reuniendo ejercitos"+ejercitos.get(0)+"->"+ ejercitos.get(0));
							movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitos.get(0), ejercitos.get(0));
						} 
						posicionMovimientoActual = 0;
					}
				}else {///Nuestro general NO esta en peligro, no conocemos la posicion de ningun general ni ninguna ciudad
					//atacamos la casilla normal mas cercana a nuestro general que no sea nuestra, con el ejercito mas grande que tengamos
					ArrayList<Integer>casillas = casillasNormalesNoPropias();
					int general = moduloPercepcion.posicionGeneral(moduloPercepcion.getEquipo());
					int casilla = buscarCasillaValidaMasCercana(casillas,general);
					int ejercito = ejercitoMasGrande();
					
					
					movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercito,casilla);
					posicionMovimientoActual = 0;
				}
			}
			
			
			
		}
		
					
		System.out.println("Nuevo camino seleccionado");
		for(int i=0;i<movimientoActual.length;i++)
			System.out.print(" "+movimientoActual[i]+" ("+moduloPercepcion.terrenoCasilla(movimientoActual[i])+") ");
	}
		
		
		
}
	
