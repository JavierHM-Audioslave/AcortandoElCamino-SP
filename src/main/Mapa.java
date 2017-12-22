package main;

import java.io.*;
import java.util.Scanner;

public class Mapa {
	
	Grafo grafo;
	Dijkstra dijkstra;
	Integer nodoSalida;
	Integer cantGaleriasNoObstruidas;
	Integer cantGaleriasObstruidas;
	File archEntrada;
	
	public Mapa(File archIn)
	{
		Scanner sc;
		try
		{
			archEntrada = archIn;
			sc = new Scanner(archIn);
			nodoSalida = sc.nextInt();
			cantGaleriasNoObstruidas = sc.nextInt();
			cantGaleriasObstruidas = sc.nextInt();
			grafo = new Grafo(nodoSalida, (cantGaleriasNoObstruidas+cantGaleriasObstruidas)*2);
			grafo.cargarGrafoNodirigido(sc);
			dijkstra = new Dijkstra("1", grafo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	public void resolver()
	{
		
		String[] dev = dijkstra.resolver();
		String ultLinea = dev[dev.length-1];
		Integer posDeCamino = ultLinea.lastIndexOf(":")+2;
		ultLinea = ultLinea.substring(posDeCamino);
		String[] bifurcacionesTomadas = ultLinea.split(" ");
		String[] bifurcacionesObstruidas = new String[cantGaleriasObstruidas*4];
		Scanner sc;
		try
		{
			sc = new Scanner(archEntrada);
			for(int i = 0; i<cantGaleriasNoObstruidas+1; i++)
			{
				sc.nextLine();
			}
			for(int i = 0; i<cantGaleriasObstruidas*4; i+=4)
			{
				bifurcacionesObstruidas[i] = sc.next();
				bifurcacionesObstruidas[i+1] = sc.next();
				bifurcacionesObstruidas[i+2] = bifurcacionesObstruidas[i+1];
				bifurcacionesObstruidas[i+3] = bifurcacionesObstruidas[i];
				sc.nextInt();
			}
			
			try
			{
				sc.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		Integer contDeBloqueos = 0;
		for(int i=0; i<bifurcacionesTomadas.length-1; i++)
		{
			for(int j = 0; j<bifurcacionesObstruidas.length; j+=4)
			{
				if((bifurcacionesTomadas[i].equals(bifurcacionesObstruidas[j]) && bifurcacionesTomadas[i+1].equals(bifurcacionesObstruidas[j+1])) || (bifurcacionesTomadas[i].equals(bifurcacionesObstruidas[j+2]) && bifurcacionesTomadas[i+1].equals(bifurcacionesObstruidas[j+3])))
				{
					contDeBloqueos++;
					break;
				}
			}
			
			if(contDeBloqueos>2)
			{
				break;
			}
		}
		
		if(contDeBloqueos == 2)
		{
			System.out.println("Salida 3");
			return;
		}
		
		if(contDeBloqueos == 1)
		{
			System.out.println("Salida 2");
			return;
		}
		
		if(contDeBloqueos == 0)
		{
			System.out.println("Salida 1");
			return;
		}
		
		if(contDeBloqueos>2)
		{
			String caso="";
			String galerias="";
			Integer costo=100000;
			String[] devDeMetodo = new String[2];
			Scanner sc2; // Lo uso para apuntar (a cada iteracion) a los caminos obstruidos para que en el metodo "encontrarCamino" pueda eliminar ese camino del grafo y antes de terminarse el metodo, volver a ingresarlo. //
			Scanner sc3; // Lo uso para armar el array de String que va a tener (en cada elemento) los caminos obstruidos de todo el mapa. //
			String[] caminosObstruidos= new String[cantGaleriasObstruidas*4];

			try
			{
				sc3 = new Scanner(archEntrada);
				sc2 = new Scanner(archEntrada);
				for(int i = 0; i<cantGaleriasNoObstruidas+1; i++)
				{
					sc2.nextLine();
					sc3.nextLine();
				}
				
				for(int i = 0; i<cantGaleriasObstruidas*4; i+=4) // Este for arma en "caminosObstruidos" los caminos obstruidos en ida y vuelta. O sea, si esta obstruido 1 2, tambien va a estar 2 1. //
				{
					
					caminosObstruidos[i] = sc3.next();
					caminosObstruidos[i+1] = sc3.next();
					caminosObstruidos[i+2] = caminosObstruidos[i+1];
					caminosObstruidos[i+3] = caminosObstruidos[i];
					sc3.nextInt(); // Para que pase de largo el costo de ese camino. //
				}
				try // Cierro el Scanner sc3. //
				{
					sc3.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
				
				for(int i = 0; i<cantGaleriasObstruidas; i++) // Trozo principal del if contenedor ya que es el que llama al metodo "encontrarCamino" que es el cual aplica la logica para saber que caso es, que galerias obstruidas estan implicadas y el costo del trayecto. //
				{
					devDeMetodo = encontrarCamino(sc2, caminosObstruidos);
					if(Integer.parseInt(devDeMetodo[1]) < costo && Integer.parseInt(devDeMetodo[1])!=-1)
					{
						caso = devDeMetodo[0];
						galerias = devDeMetodo[2];
						costo = Integer.parseInt(devDeMetodo[1]);
												
					}
				}
				
				if(caso.equals("1")) // Muestra la salida de la forma que tiene que ser mostrada. //
				{
					System.out.println(caso+" "+costo);
				}
				else
				{
					System.out.println(caso+" "+galerias+" "+costo);
				}
				
				try // Cierro el Scanner sc2. //
				{
					sc2.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
					
		}
	}
	
	private String[] encontrarCamino(Scanner sc2, String[] caminosObstruidos)
	{
		String[] linea = sc2.nextLine().split(" ");
		Integer auxValor = grafo.obtenerValor(Integer.parseInt(linea[0])-1, Integer.parseInt(linea[1])-1);
		grafo.setValorCelda(Integer.parseInt(linea[0])-1, Integer.parseInt(linea[1])-1, 1000000);
		grafo.setValorCelda(Integer.parseInt(linea[1])-1, Integer.parseInt(linea[0])-1, 1000000);
		
		Dijkstra audxDisjktra = new Dijkstra("1", grafo);
		String[] devEnBruto = audxDisjktra.resolver();
		String devPulida = devEnBruto[devEnBruto.length-1];
		String[] devDeMetodo = new String[3];
		Integer posIndex1 = devPulida.indexOf(":")+2;
		Integer posIndex2 = devPulida.indexOf(".", posIndex1);
		devDeMetodo[1] = devPulida.substring(posIndex1, posIndex2); // Me da el costo. //
		posIndex1 = devPulida.lastIndexOf(":")+2;
		devDeMetodo[0] = devPulida.substring(posIndex1);	 // Pongo el camino de forma temporal porque en este elemento en realidad me interesa el numero de caso (de lo que pide el problema) y éso lo hago en la linea 212. //
		
		Integer cantDeGaleriasObstruidasAtravesadas = 0;
		String[] partirDevDeMetodo = devDeMetodo[0].split(" "); // Hago que cada nodo este dentro de un elemento de String. //
		for(int i = 0; i<partirDevDeMetodo.length-1; i++)
		{
			for(int j = 0; j<caminosObstruidos.length; j+=4)
			{
				if((partirDevDeMetodo[i].equals(caminosObstruidos[j]) && partirDevDeMetodo[i+1].equals(caminosObstruidos[j+1])) || (partirDevDeMetodo[i].equals(caminosObstruidos[j+2]) && partirDevDeMetodo[i+1].equals(caminosObstruidos[j+3])) )
				{
					cantDeGaleriasObstruidasAtravesadas++;
					devDeMetodo[2] = devDeMetodo[2]+" "+String.valueOf(j/4)+" "; // devDeMetodo[2] tiene siempre las galerias obstruidas por las que se pasa. //
					break;
				}
			}
			if(cantDeGaleriasObstruidasAtravesadas>2)
			{
				devDeMetodo[1] = "-1";
				break;
			}
		}
		
		devDeMetodo[0] = String.valueOf(cantDeGaleriasObstruidasAtravesadas+1);
		
		grafo.setValorCelda(Integer.parseInt(linea[0])-1, Integer.parseInt(linea[1])-1, auxValor);
		grafo.setValorCelda(Integer.parseInt(linea[1])-1, Integer.parseInt(linea[0])-1, auxValor);
		
		return devDeMetodo;
	}
}
