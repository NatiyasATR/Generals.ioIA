package generals.ioIA.generals.ioIA;

import java.io.IOException;
import java.util.ArrayList;

public abstract class ModuloDecision {
	
	protected Bot bot;
	protected int[] movimientoActual;
	protected int posicionMovimientoActual;
	private int avisos;
	//Para la busqueda de generales
	private int direccion;//1 izquierda, 2 derecha, 3 abajo, 4 arriba, -1 no masignada
	protected int posicion;
	
	protected static final int distanciaSeguridad = 5; //distancia a la que un ejercito enemigo se considera amenaza
	protected static final float proporcionSuperioridad = 1.1f; //1.1 significa que el ejercito que buscamos debe ser un 10% superior a la amenaza 
	protected static final float factorDeSeguridadTurno = 0.1f; //0.5 significa que en el turno 100 deben permanecer 50 unidades en una ciudad o el general
	protected static final int faseInicial = 100; //Turno en el que se considera acabada la fase inicial
	protected static final int minimoTamañoExploracion = 10; //minimo numero de unidades para explorar
	protected static final int turnosCadaCiudad  = 100; //50 significa que en el turno 200 querremos 4 ciudades
	
	ModuloDecision(Bot bot){
		this.bot=bot;
		movimientoActual = null;
		posicionMovimientoActual =-1;
		avisos=0;
		direccion = -1;
	}
	
	public int[] movimientoAleatorio() {
		
		int alto = bot.getModuloPercepcion().getAlto();
		int ancho = bot.getModuloPercepcion().getAncho();
		int[] movimiento;
		int equipo = bot.getModuloPercepcion().getEquipo();
		
		
		for(int i=0;i<100;i++) {//intentar 1000 veces encontrar un movimiento posible
		 	int origen = (int) (Math.random()*alto*ancho);
			if(bot.getModuloPercepcion().terrenoCasilla(origen)==equipo && bot.getModuloPercepcion().unidadesCasilla(origen)>3) {
				int random = (int) (Math.random()*alto*ancho);
				int destino;
				if(random<0.25) {
					destino = origen-ancho;
				}else	if(random<0.5) {
					destino = origen-1;
				}else	if(random<0.75) {
					destino = origen+ancho;
				}else destino = origen+1;
				
				if(0<=destino && destino<alto*ancho) {
					if(bot.getModuloPercepcion().terrenoCasilla(destino)!=-2||bot.getModuloPercepcion().terrenoCasilla(destino)!=-4) {//no es una montaña
						return new int[]{origen,destino};

					}
					
				}
			}
			
		}
		return null;
	}
	
	public int[] movimientoAleatorioConAEstrella() {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		
		
		int alto = moduloPercepcion.getAlto();
		int ancho = moduloPercepcion.getAncho();
		//int origen = moduloPercepcion.posicionGeneral(moduloPercepcion.getEquipo());
		int origen = ejercitoMasGrande();
		int destino;
		do{//buscamos un destino que no sea una montaña
			destino = (int) (Math.random()*alto*ancho);
		}while(moduloPercepcion.terrenoCasilla(destino)==-2||moduloPercepcion.terrenoCasilla(destino)==-4);
		
		int[] resul = bot.getModuloNavegacion().calcularCaminoEntrePuntos(origen, destino);
		//System.out.println("Nuevo camino seleccionado");
		//for(int i=0;i<resul.length;i++)
			//System.out.print(" "+resul[i]+" ("+moduloPercepcion.terrenoCasilla(resul[i])+") ");
	
		return resul;

			
	}
	
	
	
	
	
	public Movimiento siguienteMovimiento() {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ancho = moduloPercepcion.getAncho();
		
		
		//seleccionamos un nuevo objetivo si no tenemos uno
		if(movimientoActual ==null) {
			tomaDecision();//actualiza movimientoActual y posicionMovimientoActual
			avisos=0;
		}
		
		int origen = movimientoActual[posicionMovimientoActual];
		int destino = movimientoActual[posicionMovimientoActual+1];
		
		if(destino == -14)//significa que el bot debe buscar al general enemigo
		{
			Movimiento resul = siguienteMovimientoBusquedaGeneral();
			
			
			//si no hay mas casillas del enemigo o nos hemos quedado sin unidades, selecccionamos un nuevo objetivo
			if(resul == null||moduloPercepcion.terrenoCasilla(posicion)!=moduloPercepcion.getEquipo()||moduloPercepcion.unidadesCasilla(posicion)<=1) {
				avisos++;
				if(avisos>1) {// a los tres avisos selecccionamos un nuevo movimiento
					tomaDecision();//actualiza movimientoActual y posicionMovimientoActual
					avisos=0;
				}
				return null;
			}else {
				posicion = resul.destino;
				System.out.println("Realizamos movimiento buscando general "+resul.origen+" "+resul.destino);
				return resul;
			}
			
		}
		
		
		
		//Si el movimiento no se puede continuar por que nos hemos quedado sin unidades damos un aviso
		if(moduloPercepcion.terrenoCasilla(origen)!=moduloPercepcion.getEquipo()||moduloPercepcion.unidadesCasilla(origen)<=1)
			avisos++;
		if(avisos>1) {// a los tres avisos selecccionamos un nuevo movimiento
			tomaDecision();//actualiza movimientoActual y posicionMovimientoActual
			avisos=0;
		}
		
		
		Movimiento resul = new Movimiento();
		
		resul.origen=origen;
		resul.destino=destino;
		/*
		if(moduloPercepcion.esCiudad(origen)||moduloPercepcion.esNuestroGeneral(origen))
			resul.is50 = true;
		else resul.is50 = false;
		*/
		resul.is50 = false;
		
		posicionMovimientoActual++;
		
		if(posicionMovimientoActual>=movimientoActual.length-1) {// si hemos llegado al final del camino lo ponemos a null de nuevo
			movimientoActual = null;
			posicionMovimientoActual =-1;
		}
		
		return resul;
		
		
		
	}
	
	
	protected Movimiento siguienteMovimientoBusquedaGeneral() {
		System.out.println("siguienteMovimientoBusquedaGeneral");
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		boolean nuevaDireccion;
		int ancho = moduloPercepcion.getAncho();
		int alto = moduloPercepcion.getAlto();
		Coordenadas coordenadasPosicion = new Coordenadas();
		coordenadasPosicion.setCoordenadasCasilla(posicion, ancho);
		
		nuevaDireccion = false;
		
		//si todabia no hemos decidido la direccion, elegimos una nueva direccion
		if(direccion==-1){
			nuevaDireccion = true;
		}else {
			Coordenadas coordenadasDestino = AplicarMovimiento(coordenadasPosicion,direccion);
			if(coordenadasDestino == null)//si nos salimos del mapa, elegimos una nueva direccion
				nuevaDireccion = true;
			else {
				//si nos salimos del territorio del enemigo, elegimos una nueva direccion
				int terreno = moduloPercepcion.terrenoCasilla(coordenadasDestino.getCasilla(ancho));
				if(terreno<0||terreno==moduloPercepcion.getEquipo())
					nuevaDireccion = true;
			}
		}
		if(nuevaDireccion == true) {
			//elegimos una nueva direccion
			for(int i=1;i<=4;i++) {
				nuevaDireccion = false;
				Coordenadas coordenadasDestino = AplicarMovimiento(coordenadasPosicion,i);
				if(coordenadasDestino == null) {//si nos salimos del mapa, elegimos una nueva direccion
					nuevaDireccion = true;
					System.out.println("Direccion "+i+" nos salimos del mapa");
				}
				else {
					//si nos salimos del territorio del enemigo, elegimos una nueva direccion
					int casilla = coordenadasDestino.getCasilla(ancho);
					int terreno = moduloPercepcion.terrenoCasilla(casilla);
					if(terreno<0||terreno==moduloPercepcion.getEquipo()) {
						nuevaDireccion = true;
						System.out.println("Direccion "+i+" no es una casilla "+casilla+" valida "+terreno);
					}
				}
				if(nuevaDireccion == false) {
					direccion = i;
					break;
				}else if(i==4)
					return null;
			}
		}
		
		Coordenadas coordenadasDestino = AplicarMovimiento(coordenadasPosicion,direccion);
		
		Movimiento resul = new Movimiento();
		resul.origen = posicion;
		resul.destino = coordenadasDestino.getCasilla(ancho);
		if(moduloPercepcion.esCiudad(posicion)||moduloPercepcion.esNuestroGeneral(posicion))
			resul.is50 = true;
		else resul.is50 = false;
		return resul;
	}

	public abstract void  tomaDecision();//Debe actualizar movimientoActual y posicionMovimientoActual
	
	
	protected int[] generalEnPeligro(int distanciaSeguridad) {//retorna un vector con la pocicion del general y la posicion de la amenaza, si esta en peligro, si no retorna null 
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ancho  = moduloPercepcion.getAncho();
		int alto  = moduloPercepcion.getAlto();
		int equipo = moduloPercepcion.getEquipo();
		int general = moduloPercepcion.posicionGeneral(equipo);
		Coordenadas coordenadasGeneral = new Coordenadas();
		coordenadasGeneral.setCoordenadasCasilla(general, ancho);
				
		
		//creamos rectangulo alrededor del general y buscamos peligros
		int x1 = Math.max(coordenadasGeneral.getX()-distanciaSeguridad,0);
		int x2 = Math.min(coordenadasGeneral.getX()+distanciaSeguridad,ancho-1);
		int y1 = Math.max(coordenadasGeneral.getY()-distanciaSeguridad,0);
		int y2 = Math.min(coordenadasGeneral.getY()+distanciaSeguridad,alto-1);
		
		for(int i=x1;i<=x2;i++) {
			for(int j=y1;j<=y2;j++) {
				Coordenadas coordenadasCasilla = new Coordenadas(i,j);
				int casilla = coordenadasCasilla.getCasilla(ancho);
				int terreno = moduloPercepcion.terrenoCasilla(casilla);
				int unidades = moduloPercepcion.unidadesCasilla(casilla);
				
				if(terreno>=0&&terreno!=equipo) {// Es territorio de un rival
					int unidadesGeneral = moduloPercepcion.unidadesCasilla(general);
					if(unidadesGeneral<unidades) { // Tiene mas ejercito que nosotros 
						return new int[] {general,casilla};
					}
				}
			}
		}
		
		return null;
	}
 
	
	protected int[] ciudadEnPeligro(int distanciaSeguridad) {//TODO
		return null;
		
	}
	
	protected int peligroAlrededor(int casillaOrigen,int distanciaSeguridad){//TODO
		
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int peligro = 0;;
		int ancho  = moduloPercepcion.getAncho();
		int alto  = moduloPercepcion.getAlto();
		int equipo = moduloPercepcion.getEquipo();
		Coordenadas coordenadasCasillaOrigen = new Coordenadas();
		coordenadasCasillaOrigen.setCoordenadasCasilla(casillaOrigen, ancho);
				
		//creamos rectangulo alrededor de la casilla y buscamos peligros
		int x1 = Math.max(coordenadasCasillaOrigen.getX()-distanciaSeguridad,0);
		int x2 = Math.min(coordenadasCasillaOrigen.getX()+distanciaSeguridad,ancho-1);
		int y1 = Math.max(coordenadasCasillaOrigen.getY()-distanciaSeguridad,0);
		int y2 = Math.min(coordenadasCasillaOrigen.getY()+distanciaSeguridad,alto-1);
		
		for(int i=x1;i<=x2;i++) {
			for(int j=y1;j<=y2;j++) {
				Coordenadas coordenadasCasilla = new Coordenadas(i,j);
				int casilla = coordenadasCasilla.getCasilla(ancho);
				int terreno = moduloPercepcion.terrenoCasilla(casilla);
				int unidades = moduloPercepcion.unidadesCasilla(casilla);
				
				if(terreno>=0&&terreno!=equipo) {// Es territorio de un rival
					
					if(unidades>1) { // Tiene ejercito suficiente para moverlo 
						peligro+=unidades;
					}
				}
			}
		}
		return peligro;
	}
	
	
	protected int[] defenderContra(int casillaEnPeligro,int unidadesEnemigas) {//retorna un camino para mover un ejercito lo suficientemente grande para defender un punto
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		ModuloNavegacion moduloNavegacion = bot.getModuloNavegacion();
		int inidadesCasillaEnPeligro = moduloPercepcion.unidadesCasilla(casillaEnPeligro);
		
		//int ejercito = buscarEjercitoMasCercano(Math.round( proporcionSuperioridad*(unidadesEnemigas-inidadesCasillaEnPeligro))+1,casillaEnPeligro);
		int ejercito = buscarEjercitoMasCercano(Math.round( proporcionSuperioridad*unidadesEnemigas)+1-inidadesCasillaEnPeligro,casillaEnPeligro);
			
		if(ejercito!=-1) {
			return moduloNavegacion.calcularCaminoEntrePuntos(ejercito,casillaEnPeligro);
			
		}else{//no hay ningun ejercito suficientemente grande
			int mayorEjercito = ejercitoMasGrande();
			return moduloNavegacion.calcularCaminoEntrePuntos(mayorEjercito,casillaEnPeligro);
		}
	}
	
	
	protected int buscarEjercitoMasCercano(int tamañoMinimo, int posicionObjetivo) { //retorna la posicion del ejercito mas cercano al objetivo que tenga el tamaño minimo especificado, retorna -1 si no se ha encontrado ninguno
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int alto = moduloPercepcion.getAlto();
		int ancho = moduloPercepcion.getAncho();
		Coordenadas coordenadasObjetivo = new Coordenadas();
		coordenadasObjetivo.setCoordenadasCasilla(posicionObjetivo, ancho);
		
		for(int i = 1;i<alto||i<ancho;i++) {//i es el radio de busqueda, empezamos por 1 y cacabamos cuando sea imposible que haya mas casillas
			
			int x;
			int y;
			
			//arista superior
			y = coordenadasObjetivo.getY()-i;
			for(x=coordenadasObjetivo.getX()-i;x<=coordenadasObjetivo.getX()+i;x++) {
				if(0<=x&&x<ancho&&0<=y&&y<alto) {// no nos hemos salido del tablero
					Coordenadas coordenadas = new Coordenadas(x,y);
					int casilla = coordenadas.getCasilla(ancho);
					int unidadesDisponibles = ejercitoDisponible(casilla);
					int tereno = moduloPercepcion.terrenoCasilla(casilla);
					int equipo = moduloPercepcion.getEquipo();
					if(tereno == equipo && unidadesDisponibles >= tamañoMinimo)// si controlamos esa casilla y el ejercito es suficientemente grande
						return casilla;
				}
			}
			
			
			//arista inferior
			y = coordenadasObjetivo.getY()+i;
			for(x=coordenadasObjetivo.getX()-i;x<=coordenadasObjetivo.getX()+i;x++) {
				if(0<=x&&x<ancho&&0<=y&&y<alto) {// no nos hemos salido del tablero
					Coordenadas coordenadas = new Coordenadas(x,y);
					int casilla = coordenadas.getCasilla(ancho);
					int unidadesDisponibles = ejercitoDisponible(casilla);
					int tereno = moduloPercepcion.terrenoCasilla(casilla);
					int equipo = moduloPercepcion.getEquipo();
					if(tereno == equipo && unidadesDisponibles > tamañoMinimo)// si controlamos esa casilla y el ejercito es suficientemente grande
						return casilla;
				}
			}
			
			//arista izquierda
			x = coordenadasObjetivo.getX()-i;
			for(y=coordenadasObjetivo.getY()-i-1;y<coordenadasObjetivo.getY()+i;y++) {
				if(0<=x&&x<ancho&&0<=y&&y<alto) {// no nos hemos salido del tablero
					Coordenadas coordenadas = new Coordenadas(x,y);
					int casilla = coordenadas.getCasilla(ancho);
					int unidadesDisponibles = ejercitoDisponible(casilla);
					int tereno = moduloPercepcion.terrenoCasilla(casilla);
					int equipo = moduloPercepcion.getEquipo();
					if(tereno == equipo && unidadesDisponibles > tamañoMinimo)// si controlamos esa casilla y el ejercito es suficientemente grande
						return casilla;
				}
			}
			
			
			//arista derecha
			x = coordenadasObjetivo.getX()+i;
			for(y=coordenadasObjetivo.getY()-i-1;y<coordenadasObjetivo.getY()+i;y++) {
				if(0<=x&&x<ancho&&0<=y&&y<alto) {// no nos hemos salido del tablero
					Coordenadas coordenadas = new Coordenadas(x,y);
					int casilla = coordenadas.getCasilla(ancho);
					int unidadesDisponibles = ejercitoDisponible(casilla);
					int tereno = moduloPercepcion.terrenoCasilla(casilla);
					int equipo = moduloPercepcion.getEquipo();
					if(tereno == equipo && unidadesDisponibles > tamañoMinimo)// si controlamos esa casilla y el ejercito es suficientemente grande
						return casilla;
				}
			}
	
		}
		
		// si no encontramos ningun ejercito suficientemente granade
		return -1;
	}
	
	
	
	protected int ejercitoMasGrande() {// retorna la posicion del ejercito mas grande
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ancho  = moduloPercepcion.getAncho();
		int alto  = moduloPercepcion.getAlto();
		int maximo = -1;
		int unidadesMaximo = -1;
		for(int i=0;i<alto*ancho;i++) {
			int terreno = moduloPercepcion.terrenoCasilla(i);
			int equipo = moduloPercepcion.getEquipo();
			if(terreno==equipo) {//controlamos la casilla
				if(maximo==-1){// si no est asignado lo asignamos
					maximo = i;
					unidadesMaximo = ejercitoDisponible(i);
				}
				else {
					int unidadesDisponibles = ejercitoDisponible(i);
					//System.out.println("Ejercito Disponible "+unidadesDisponibles);
					if(unidadesDisponibles>unidadesMaximo) {
						unidadesMaximo = unidadesDisponibles;
						maximo = i;
					}
						
				}
			}
		}
		return maximo;
	}
	
	//Metodo no usado
	protected ArrayList<Integer> ejercitosPropiosOrdenados() {//retorna una lista con las posiciones de los ejerctos ordenados por tamaño
		ArrayList<Integer> ejercitos = new ArrayList<Integer>();
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ancho  = moduloPercepcion.getAncho();
		int alto  = moduloPercepcion.getAlto();
		//TODO
		for(int i=0;i<alto*ancho;i++) {
			int terreno = moduloPercepcion.terrenoCasilla(i);
			int equipo = moduloPercepcion.getEquipo();
			if(terreno==equipo) {//controlamos la casilla
				int unidadesDisponibles = ejercitoDisponible(i);
				//insertamos en orden
				int j=0;
				while(j<ejercitos.size()) {
					int ejercito = ejercitos.get(j);
					if(ejercitoDisponible(ejercito)<unidadesDisponibles)
						break;
					j++;
				}
				if(j==ejercitos.size())
					ejercitos.add(i);
				else ejercitos.add(j,i);
			}
		}
		return ejercitos; 	
	}
	
	
	protected ArrayList<Integer> ciudadesPropias() {// devuelve las posiciones de las ciudad neutrales que conocemos
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ciudades[] = moduloPercepcion.getCiudades();
		ArrayList<Integer> ciudadesNeutrales = new ArrayList<Integer>();
		int equipo = moduloPercepcion.getEquipo();
		
		for(int i=0;i<ciudades.length;i++) {
			if(moduloPercepcion.terrenoCasilla(ciudades[i])==equipo) {//es de nuestra propiedad
							ciudadesNeutrales.add(ciudades[i]) ;
			}
		}
		
    	return ciudadesNeutrales; 
	}
	
	
	protected ArrayList<Integer> ciudadesNeutralConocidas() {// devuelve las posiciones de las ciudad neutrales que conocemos 
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ciudades[] = moduloPercepcion.getCiudades();
		ArrayList<Integer> ciudadesNeutrales = new ArrayList<Integer>();
		
		for(int i=0;i<ciudades.length;i++) {
			if(moduloPercepcion.terrenoCasilla(ciudades[i])==-1) {//-1=neutral visible -3 neutral invisible

				ciudadesNeutrales.add(ciudades[i]) ;
			}
		}
		
    	return ciudadesNeutrales; 
	}
	
	
	protected ArrayList<Integer> ciudadesNoPropiasConocidas() {// devuelve la posicion de una ciudad neutral si conocemos la posicion de alguna
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		//ArrayList<Integer> ciudadesConocidas = moduloPercepcion.getCiudadesConocidas();
		int ciudades[] = moduloPercepcion.getCiudades();
		ArrayList<Integer> ciudadesNeutrales = new ArrayList<Integer>();
		int equipo = moduloPercepcion.getEquipo();
		
		for(int i=0;i<ciudades.length;i++) {
			if(moduloPercepcion.terrenoCasilla(ciudades[i])!=equipo) {//Es enemiga o neutral
				ciudadesNeutrales.add(ciudades[i]) ;
			}
		}
		
    	return ciudadesNeutrales; 
	}
	
	protected ArrayList<Integer> generalesEnemigosConocidos(){//retorna la posicion de los generales enemigos conocidos
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int generales[] = moduloPercepcion.getGenerales();
		ArrayList<Integer> resul = new ArrayList<Integer>();
		int equipo = moduloPercepcion.getEquipo();
		
		for(int i=0;i<generales.length;i++) {
			if(generales[i]!=-1 && moduloPercepcion.terrenoCasilla(generales[i])!=equipo)
				resul.add(generales[i]);
		}
		return resul;
	}
	
	


	protected int buscarCasillaValidaMasCercana(ArrayList<Integer> casillasValidas, int casillaObjetivo) {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		ModuloNavegacion moduloNavegacion = bot.getModuloNavegacion();
		
		int ancho = moduloPercepcion.getAncho();
		Coordenadas coordenadasObjetivo = new Coordenadas();
		coordenadasObjetivo.setCoordenadasCasilla(casillaObjetivo, ancho);
		
		int resul = -1;
		float distanciaMinima = Float.MAX_VALUE;
		
		for(Integer casillaValida : casillasValidas) {
			float distancia = moduloNavegacion.distanciaManhattan(casillaValida, casillaObjetivo);
			if(distancia<distanciaMinima) {
				resul = casillaValida;
				distanciaMinima = distancia;
			}
				
		}
		return resul;
	}

	protected ArrayList<Integer> ejercitosPropiosMayoresQue(int tamañoMinimo){
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int alto = moduloPercepcion.getAlto();
		int ancho = moduloPercepcion.getAncho();
		ArrayList<Integer> ejercitos = new ArrayList<Integer>();
		for(int i=0;i<alto*ancho;i++) {
			if(moduloPercepcion.terrenoCasilla(i)==moduloPercepcion.getEquipo()&&ejercitoDisponible(i)>=tamañoMinimo)
				ejercitos.add(i);
		}
		return ejercitos;
	}
	
	protected ArrayList<Integer> casillasNormalesNoPropias() {//devuelve las casillas no propia que no sean montañas ni ciudades ni generales 
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int alto = moduloPercepcion.getAlto();
		int ancho = moduloPercepcion.getAncho();
		ArrayList<Integer> casillas = new ArrayList<Integer>();
		for(int i=0;i<alto*ancho;i++) {
			int terrenoCasilla = moduloPercepcion.terrenoCasilla(i);
			int equipo = moduloPercepcion.getEquipo();
			if(terrenoCasilla!=equipo&&terrenoCasilla!=-2&&terrenoCasilla!=-4) {// no es propia y no es una montaña
				int ciudades[] = moduloPercepcion.getCiudades();
				int generales[] = moduloPercepcion.getGenerales();
				ArrayList<Integer> casillasInacesibles = moduloPercepcion.getCasillasInacesibles();
				boolean esCiudad = false;
				boolean esGeneral = false;
				boolean esInacesible = false;
				
				for(int j=0;j<ciudades.length;j++) {//comprobamos si es una ciudad
					if(i==ciudades[j]) {
						esCiudad=true;
						break;
					}
				}
				
				for(int j=0;j<generales.length;j++) {//comprobamos si es un general
					if(i==generales[j]) {
						esGeneral=true;
						break;
					}
				}
				
				for(Integer j :casillasInacesibles) {
					if(i==j.intValue()) {
						esInacesible=true;
						break;
					}
				}
				
				if(!esCiudad&&!esGeneral&&!esInacesible)//si no es ciudad ni general ni es inacesible
					casillas.add(i);
			}
		}
		return casillas;
	}
	
	protected ArrayList<Integer> casillasNormalesEnemigas() {//devuelve las casillas no propia, pertenecientes a un jugador que no sean montañas ni ciudades ni generales 
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int alto = moduloPercepcion.getAlto();
		int ancho = moduloPercepcion.getAncho();
		ArrayList<Integer> casillas = new ArrayList<Integer>();
		for(int i=0;i<alto*ancho;i++) {
			int terrenoCasilla = moduloPercepcion.terrenoCasilla(i);
			int equipo = moduloPercepcion.getEquipo();
			if(terrenoCasilla>=0&&terrenoCasilla!=equipo) {// no es propia y no es una montaña
				int ciudades[] = moduloPercepcion.getCiudades();
				int generales[] = moduloPercepcion.getGenerales();
				ArrayList<Integer> casillasInacesibles = moduloPercepcion.getCasillasInacesibles();
				boolean esCiudad = false;
				boolean esGeneral = false;
				boolean esInacesible = false;
				
				for(int j=0;j<ciudades.length;j++) {//comprobamos si es una ciudad
					if(i==ciudades[j]) {
						esCiudad=true;
						break;
					}
				}
				
				for(int j=0;j<generales.length;j++) {//comprobamos si es un general
					if(i==generales[j]) {
						esGeneral=true;
						break;
					}
				}
				
				for(Integer j :casillasInacesibles) {
					if(i==j.intValue()) {
						esInacesible=true;
						break;
					}
				}
				
				if(!esCiudad&&!esGeneral&&!esInacesible)//si no es ciudad ni general ni es inacesible
					casillas.add(i);
			}
		}
		return casillas;
	}
	
	protected boolean casillaEstaEnLista(int casilla, ArrayList<Integer> lista) {
		for(int i=0;i<lista.size();i++) {
			if(lista.get(i).intValue()==casilla) {
				return true;
			}
		}
		return false;
	}
	
	protected int defensaMinima(int casilla) {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int turno = moduloPercepcion.getTurno();
		int peligro = peligroAlrededor(casilla,distanciaSeguridad);
		int defensaPeligros = Math.round(peligro*proporcionSuperioridad)+1;
		int defensaMinimaTurno = Math.round(turno*factorDeSeguridadTurno);
		return Math.max(defensaPeligros, defensaMinimaTurno);
	}
	
	protected int ejercitoDisponible(int casilla) {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int ejercito = moduloPercepcion.unidadesCasilla(casilla);
		/*
		if(moduloPercepcion.esNuestroGeneral(casilla)||moduloPercepcion.esCiudad(casilla)) {
			int defensaMinima = defensaMinima(casilla);
			//System.out.println("defensa minima "+defensaMinima);
			if((ejercito/2)>defensaMinima)
				return ejercito/2;
			else return 0;
		}else return ejercito;
		*/
		return ejercito;
	}
	
	protected Coordenadas AplicarMovimiento(Coordenadas origen,int direc) {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		
		int ancho = moduloPercepcion.getAncho();
		int alto = moduloPercepcion.getAlto();
		int x = origen.getX();
		int y = origen.getY();
		Coordenadas destino = new Coordenadas(x,y);
		if(direc==1) {
			x--;
			if(x<0) {
				return null;
			}else {
				destino.setX(x);
			}	
		}else if(direc==2) {
			x++;
			if(x>= ancho) {
				return null;
			}else {
				destino.setX(x);
			}	
		}else if(direc==3) {
			y--;
			if(y<0) {
				return null;
			}else {
				destino.setY(y);
			}	
		}else if(direc==4) {
			y++;
			if(y>= alto) {
				return null;
			}else {
				destino.setY(y);
			}	
		}else return null;
		return destino;
	}
	
	
}
