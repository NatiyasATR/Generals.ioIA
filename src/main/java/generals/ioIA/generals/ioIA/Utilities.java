package generals.ioIA.generals.ioIA;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public final class Utilities {
	//https://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-outpu
    /**************************************************************/
    
    public static String decodeURIComponent(String s)
    {
      if (s == null)
      {
        return null;
      }

      String result = null;

      try
      {
        result = URLDecoder.decode(s, "UTF-8");
      }

      // This exception should never occur.
      catch (UnsupportedEncodingException e)
      {
        result = s;  
      }

      return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     * 
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURIComponent(String s)
    {
      String result = null;

      try
      {
        result = URLEncoder.encode(s, "UTF-8")
                           .replaceAll("\\+", "%20")
                           .replaceAll("\\%21", "!")
                           .replaceAll("\\%27", "'")
                           .replaceAll("\\%28", "(")
                           .replaceAll("\\%29", ")")
                           .replaceAll("\\%7E", "~");
      }

      // This exception should never occur.
      catch (UnsupportedEncodingException e)
      {
        result = s;
      }

      return result;
    } 
    
    
    /*********************************************************************************/
    public static int[] JSONArraytoArray(JSONArray jarray) {
		int resul[] = new int[jarray.length()];
		try {
			for(int i=0;i<jarray.length();i++) {
				resul[i]=jarray.getInt(i);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return resul;
    }
    
    /*******************************************/
    public static int[] parchear(int antiguo[], JSONArray parche) {
		ArrayList<Integer> nuevo = new ArrayList<Integer>(0);
		
		
		try {
			int punteroParche=0;
			while (punteroParche < parche.length()) {
				if (parche.getInt(punteroParche)>0) {// iguales
					int datosIguales = parche.getInt(punteroParche);
					int punteroAntiguo = nuevo.size();
					for(int i=punteroAntiguo;i<punteroAntiguo+datosIguales;i++) {
						nuevo.add(new Integer(antiguo[i]));
					}
				}
				punteroParche++;
				
				if (punteroParche<parche.length() && parche.getInt(punteroParche)>0) {// diferentes
					int datosDiferentes = parche.getInt(punteroParche);
					
					while(datosDiferentes>0) {
						punteroParche++;
						nuevo.add(new Integer(parche.getInt(punteroParche)));
						datosDiferentes--;
					}
				}
				punteroParche++;
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		int resul[]=new int[nuevo.size()];
		for(int i=0;i<resul.length;i++)
			resul[i]=nuevo.get(i).intValue();
    	return resul;  
    }
    /*
    public static int[] casillaACoordenadas(int casilla,int ancho) {
    	int[] resul = new int[2];
    	resul[0] = casilla % ancho;
    	resul[1] = casilla / ancho;
    	
    	return resul;
    }
    
    public static int coordenadasACasilla(int[] coordenadas,int ancho) {
    	return coordenadas[0] + ancho * coordenadas[1] ;
    }
    */
}
