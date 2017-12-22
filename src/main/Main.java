package main;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		Mapa mapa = new Mapa(new File("caso1.in"));
		//String dev = mapa.resolver();
		mapa.resolver();
		//System.out.println(dev);

	}

}
