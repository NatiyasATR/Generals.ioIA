package generals.ioIA.generals.ioIA;

public class Coordenadas {
	
	private int x;
	private int y;
	
	
	public Coordenadas() {
	}
	
	public Coordenadas(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	
	public void setCoordenadasCasilla(int casilla,int ancho) {
		this.x = casilla % ancho;
		this.y = casilla / ancho;
	}
	
	public int getCasilla(int ancho) {
		return x + ancho * y ;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
