package client;

import models.Sharing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p> Progetto finale </p>
 * 
 * La classe <b> MulticastReader </b> rappresenta il thread del client che gestisce il socket multicast in ricezione.
 * 
 * @author Alessia Anile
 */
public class MulticastReader implements Runnable {
	
	/** Socket multicast per la ricezione */
	private MulticastSocket socket;
	/** Struttura dati contenente i risultati delle partite condivise */
	private ArrayList<Sharing> sharings;

	/**
	 * Costruttore della classe MulticastReader
	 * @param socket multicast socket per la ricezione dei messaggi
	 * @param sharings struttura dati per contenere i risultati delle partite condivise
	 * @throws IOException in caso di errori I/O durante la lettura
	 */
	public MulticastReader(MulticastSocket socket, ArrayList<Sharing> sharings) throws IOException {
		this.socket = socket;
		this.sharings = sharings;
	}

	/**
	 * Metodo del task da eseguire per la ricezione multicast
	 */
	public void run() {
		String receivedMsg = "";
		
		
		// Finchè il thread non viene interrotto
		while (!Thread.currentThread().isInterrupted()) {
						
			// Ricezione		
			byte[] buffer = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
			
			try {
				socket.receive(dp);
			} catch (IOException e) {
				if(!socket.isClosed())
					System.err.println("[MR] Errore lettura multicast.");
				continue;
			} 

			try {
				receivedMsg = new String(dp.getData(), 0, dp.getLength(), "US-ASCII");
			} catch (IOException e) {
				System.out.println("[MR] Errore multicast.");
			}
			
			// Memorizzo nella struttura dati apposita il contenuto appena ricevuto
			save(receivedMsg);

		}
	}

	/**
	 * Metodo per creare un oggetto di tipo Sharing a partire dalla stringa ricevuta
	 * e aggiungerlo alla struttura dati sharings
	 * @param receivedMsg contenuto da elaborare
	 */
	private void save(String receivedMsg) {		
		String[] splitted = receivedMsg.split(":");
		String[] splittedHead = splitted[0].split(";");
		String[] splittedBody = splitted[1].split(";");
		
		ArrayList<String> indizi = new ArrayList<String>();
		for(String s : splittedBody)
			indizi.add(s);
		
		Sharing sharing = new Sharing(Integer.parseInt(splittedHead[0]), Integer.parseInt(splittedHead[1]), indizi);
		sharings.add(sharing);
	}

}
