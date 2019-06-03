package generals.ioIA.generals.ioIA;

import java.util.ArrayList;

public class MaquinaEstados extends ModuloDecision{
	
	private String estado;
	private int[] datosEstado;
	
	MaquinaEstados(Bot bot) {
		super(bot);
		estado = "Expansion";
	}

	public void tomaDecision() {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		ModuloNavegacion moduloNavegacion = bot.getModuloNavegacion();
		
		actualizarEstado();
		
		if(estado ==  "Expansion") {
			System.out.println("Expansion");
			ArrayList<Integer>casillas = casillasNormalesNoPropias();
			int general = moduloPercepcion.posicionGeneral(moduloPercepcion.getEquipo());
			int casilla = buscarCasillaValidaMasCercana(casillas,general);
			int ejercito = ejercitoMasGrande();
			movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercito,casilla);
			System.out.println("origen destino: "+ejercito+" "+casilla);
			if(movimientoActual==null) {
				moduloPercepcion.getCasillasInacesibles().add(casilla);
			}
				
			posicionMovimientoActual = 0;
			
		}else if(estado ==  "Defensa") {
			System.out.println("Defensa");
			int general = datosEstado[0];
			int unidadesEnemigas = datosEstado[1];
			System.out.println(movimientoActual);
			movimientoActual = defenderContra(general,unidadesEnemigas);
			posicionMovimientoActual = 0;
			
		}else if(estado ==  "Ataque") {
			System.out.println("Ataque");
			int objetivo = datosEstado[0];
			int ejercito = datosEstado[1];
			System.out.println(movimientoActual);
			movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercito, objetivo);
			posicionMovimientoActual = 0;
		}else if(estado ==  "Conquista") {
			System.out.println("Conquista");
			int objetivo = datosEstado[0];
			int ejercito = datosEstado[1];
			System.out.println(movimientoActual);
			movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercito, objetivo);
			posicionMovimientoActual = 0;
		}else if(estado ==  "Reagrupar") {
			System.out.println("Reagrupar");
			ArrayList<Integer>ejercitos = ejercitosPropiosOrdenados();
			if(ejercitos.size()>1)
				movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitos.get(1), ejercitos.get(0));
			else movimientoActual = moduloNavegacion.calcularCaminoEntrePuntos(ejercitos.get(0), ejercitos.get(0));
			System.out.println(movimientoActual);
			posicionMovimientoActual = 0;
		}
		
	}
	
	
	
	
	
	private void actualizarEstado() {
		ModuloPercepcion moduloPercepcion = bot.getModuloPercepcion();
		int turno = moduloPercepcion.getTurno();
		int[] generalEnPeligro = generalEnPeligro(distanciaSeguridad);
		ArrayList<Integer> generalesEnemigos = generalesEnemigosConocidos();
		int generalElegido = -1;
		int ejercitoElegidoContraGeneral = -1;
		
		for(int i=0;i<generalesEnemigos.size();i++) {//buscamos combinaciones general enemigo - ejercito
			int general = generalesEnemigos.get(i);
			int unidadesGeneral = moduloPercepcion.unidadesCasilla(general);
			ArrayList<Integer> ejercitos = ejercitosPropiosMayoresQue(Math.round(unidadesGeneral*proporcionSuperioridad)+1);
			if(ejercitos.size()>0) {// existe al menos un ejercito que pueda atacar este general
				generalElegido = general;
				ejercitoElegidoContraGeneral = buscarCasillaValidaMasCercana(ejercitos,general);
			}
			
		}
		
		ArrayList<Integer> ciudadesEnemigas = ciudadesNoPropiasConocidas();	
		int ciudadElegida = -1;
		int ejercitoElegidoContraCiudad =-1;
			
		for(int i=0;i<ciudadesEnemigas.size();i++) {//buscamos combinaciones ciudad enemigo - ejercito
			int ciudad = ciudadesEnemigas.get(i);
			int unidadesCiudad = moduloPercepcion.unidadesCasilla(ciudad);
			//System.out.println(ciudad);
			//System.out.println(unidadesCiudad);
			//System.out.println(Math.round(unidadesCiudad*proporcionSuperioridad)+1);
			ArrayList<Integer> ejercitos = ejercitosPropiosMayoresQue(Math.round(unidadesCiudad*proporcionSuperioridad)+1);
			if(ejercitos.size()>0) {// existe al menos un ejercito que pueda atacar esta ciudad
				ciudadElegida = ciudad;
				ejercitoElegidoContraCiudad = buscarCasillaValidaMasCercana(ejercitos,ciudad);
			}
		}
		System.out.println(ciudadesEnemigas);
		System.out.println(ciudadElegida);
		System.out.println(ejercitoElegidoContraCiudad);
		
		
		
		
		if(estado ==  "Expansion") {	
			if(generalEnPeligro!=null)//Nuestro general esta en peligro
			{
				int general = generalEnPeligro[0];
				int unidadesEnemigas = moduloPercepcion.unidadesCasilla(generalEnPeligro[1]);
				estado = "Defensa";
				datosEstado = new int[] {general,unidadesEnemigas};//introducimos en los datos del estado la posicion del general y de las unidades enemigas
			}else if(generalesEnemigos.size()>0&&generalElegido!=-1) {//hay un general objetivo y tenemos ejercito
				estado = "Ataque";
				datosEstado = new int[]{generalElegido,ejercitoElegidoContraGeneral};
			
			}else if(ciudadesEnemigas.size()>0&&ciudadElegida!=-1&&turno>faseInicial) {//hay una ciudad objetivo, tenemos ejercito y ya no estamos en la fase inicial
				estado = "Conquista";
				datosEstado = new int[]{ciudadElegida,ejercitoElegidoContraCiudad};
			}else if(((generalesEnemigos.size()>0&&generalElegido==-1)||(ciudadesEnemigas.size()>0&&ciudadElegida==-1))&&turno>faseInicial) {//tenemos objetivos pero no ejercito  y ya no estamos en la fase inicial
				estado = "Reagrupar";
			}// si no hay nada mas que hacer seguimos expandiendo
			
			
		}else if(estado ==  "Defensa") {	
			if(generalEnPeligro==null) {//ya no estamos en peligro
				estado = "Expansion";
			}// si seguimos en peligro seguimos defendiendo
			
		}else if(estado ==  "Ataque") {
			int objetivo = datosEstado[0];
			int terrenoObjetivo = moduloPercepcion.terrenoCasilla(objetivo);
			int equipo = moduloPercepcion.getEquipo();
			if(terrenoObjetivo==equipo) {//hemos acabado con el general 
				estado = "Expandir";
			}else {//no hemos acabado con el general
				estado = "Reagrupar";
			}
			
		}else if(estado ==  "Conquista") {
			int objetivo = datosEstado[0];
			int terrenoObjetivo = moduloPercepcion.terrenoCasilla(objetivo);
			int equipo = moduloPercepcion.getEquipo();
			if(terrenoObjetivo==equipo) {//hemos conquistado la ciudad 
				estado = "Expansion";
				System.out.println("Ciudad conquistada");
			}else {//no hemos conquistado la ciudad 
				estado = "Reagrupar";
			}
			
		}else if(estado ==  "Reagrupar") {
			if(generalesEnemigos.size()>0&&generalElegido!=-1) {//hay un general objetivo y tenemos ejercito
				estado = "Ataque";
				datosEstado = new int[]{generalElegido,ejercitoElegidoContraGeneral};
			
			}else if(ciudadesEnemigas.size()>0&&ciudadElegida!=-1) {//hay una ciudad objetivo y tenemos ejercito
				estado = "Conquista";
				datosEstado = new int[]{ciudadElegida,ejercitoElegidoContraCiudad};
			}else if(ciudadesEnemigas.size()==0&&generalesEnemigos.size()==0) {//Hemos perdido de vista todos los posibles objetivos
				estado = "Expansion";
			}//si no se cumple nada seguimos reagrupando
			
		}
	}
}
