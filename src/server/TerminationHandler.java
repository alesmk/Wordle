package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> TerminationHandler </b> gestisce la terminazione del server.
 * 
 * @author Alessia Anile
 */
public class TerminationHandler extends Thread {
	
	/** Socket del server da terminare */
	private ServerSocket serverSocket;
	/** Struttura dati contenente i socket dei client connessi */
	private ArrayList<Socket> connectedClients;
	/* Client connessi */
	private ExecutorService clients;
	/* Gestore degli aggiornamenti della Secret Word */
	private Thread wordHandler;
	/** Indicatore del processo di shutdown del server */
	private boolean terminating;
	
	/**
	 * Costruttore della classe TerminationHandler
	 * @param serverSocket socket del server da terminare
	 * @param connectedClients struttura dati contenente i socket dei client connessi
	 * @param clients clients da terminare
	 * @param wordHandler riferimento al gestore dell'aggiornamento delle Secret Words
	 */
	public TerminationHandler(ServerSocket serverSocket, ArrayList<Socket> connectedClients, ExecutorService clients, Thread wordHandler) {
		this.serverSocket = serverSocket;
		this.connectedClients = connectedClients;
		this.clients = clients;
		this.wordHandler = wordHandler;
		this.terminating = false;
	}
	
	/**
	 * @return true se la terminazione è completata, false altrimenti
	 */
	public boolean isTerminated() {
		return terminating;
	}

	/**
	 * Metodo da eseguire per terminare il server, terminando anche i client a lui connessi e il gestore di aggiornamento delle Secret Words.
	 */
	public void run(){
		
		terminating=true;
		try {
			// Termino il server
			serverSocket.close();
			
			// Termino i client
			for(Socket client : connectedClients)
				client.close();
			
			// Richiedo la terminazione dei client
			clients.shutdown();

			int i = 0;
			while (!clients.isTerminated() && i < 1000) {
				try {
					clients.awaitTermination(3000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					System.out.println("[SM] Eccezione awaitTermination");
				}
				i++;
			}
			// Se ho effettuato 1000 tentativi, forzo lo shutdown
			if (i == 1000)
				clients.shutdownNow();

			// Terminazione del gestore dell'aggiornamento delle Secret Words
			wordHandler.interrupt();
			try {
				wordHandler.join();
				System.out.println("[SM] WordHandler terminato");
			} catch (InterruptedException e) {
				System.out.println("[SM] Eccezione WordHandler join");
			}
			
		} catch (IOException e) {
			System.err.println("[TH] Eccezione durante la terminazione.");
		}
		
		System.out.println(" *** SERVER CHIUSO *** ");
	}
}
