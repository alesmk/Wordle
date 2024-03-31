package models;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p> Progetto finale </p>
 * 
 * La classe <b> ClientConfig </b> rappresenta la configurazione del client.
 * 
 * @author Alessia Anile
 */
public class ClientConfig {
	
	/** Indirizzo del server */
	private String serverAddress;
	/** Numero di porta del server */
	private int port;
	/** Indirizzo del gruppo multicast */
	private String multicastAddress;
	/** Numero di porta multicast */
	private int multicastPort;
	

	/**
	 * Costruttore della classe ClientConfig
	 * @param serverAddress indirizzo del server
	 * @param port numero di porta del server
	 * @param multicastAddress indirizzo del gruppo multicast
	 * @param multicastPort numero di porta multicast
	 */
	public ClientConfig(String serverAddress, int port, String multicastAddress, int multicastPort) {
		this.serverAddress = serverAddress;
		this.port = port;
		this.multicastAddress = multicastAddress;
		this.multicastPort = multicastPort;
	}


	/**
	 * @return indirizzo del server
	 */
	public String getServerAddress() {
		return serverAddress;
	}


	/**
	 * @param serverAddress indirizzo del server da settare
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}


	/**
	 * @return numero di porta del server
	 */
	public int getPort() {
		return port;
	}


	/**
	 * @param port numero di porta del server da settare
	 */
	public void setPort(int port) {
		this.port = port;
	}


	/**
	 * @return indirizzo del gruppo multicast
	 */
	public String getMulticastAddress() {
		return multicastAddress;
	}


	/**
	 * @param multicastAddress indirizzo del gruppo multicast da settare
	 */
	public void setMulticastAddress(String multicastAddress) {
		this.multicastAddress = multicastAddress;
	}


	/**
	 * @return numero di porta multicast
	 */
	public int getMulticastPort() {
		return multicastPort;
	}


	/**
	 * @param multicastPort numero di porta multicast da settare
	 */
	public void setMulticastPort(int multicastPort) {
		this.multicastPort = multicastPort;
	}

}
