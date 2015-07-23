package neldam.Thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class ChannelThread implements Runnable {

	private Socket clientSocket;
	private PrintWriter outStream;
	private BufferedReader inStream;
	private String gpioNumber;

	final static GpioController gpio = GpioFactory.getInstance();

	public ChannelThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public boolean validateConnection() {
		try {
			this.outStream = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
			this.inStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			this.gpioNumber = this.inStream.readLine();
			this.outStream.println("accepted");
			this.outStream.flush();

			return true;

		} catch (IOException e) {
			this.outStream.println("error");
			e.printStackTrace();
			return false;
		}
	}

	public void run() {
		PinThread pH = new PinThread(this.gpioNumber);
		Thread thread = new Thread(pH);
		thread.start();
		while (true) {
			try {
				String comand = inStream.readLine();
				pH.setSignalType(comand);				
				System.out.println(comand);
			} catch (IOException e) {
				try {
					pH.setSignalType("STOP_THREAD");
					outStream.close();
					inStream.close();
					clientSocket.close();
					break;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				e.printStackTrace();
			}
		}
	}

}
