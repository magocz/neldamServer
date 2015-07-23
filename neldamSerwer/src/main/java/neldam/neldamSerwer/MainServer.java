package neldam.neldamSerwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import neldam.Thread.ChannelThread;

/**
 * Hello world!
 *
 */
public class MainServer {

	private static ServerSocket server;
	private static BufferedReader is;

	/**
	 * Funkcja startowa do otwarcia polaczenia z inna aplikacja przez porty.
	 * Standardowa beda to porty 7001, 7002, 7004, 7005, 7006. Pierwsze cztery
	 * porty beda obslugiwac sygnaly PWM, a dwa ostanie beda pozwalac na
	 * ustawienie stanu wysokiego lub niskiego.
	 * 
	 * Laczenie z kazdym portem wyglada tak samo. Jezeli istnieje zadanie
	 * polaczenia, serwer nawiazuje polaczenie, po czym czeka na dane wejsciowe,
	 * ktore sa nr GPIO, ktory dany port ma sterowac. Jezeli wszystko przebieglo
	 * pomyslnie, to serwer odpowiada klijentowi "accepted", jezeli wystapil
	 * jakiklwiek blad, to serwer zwraca do klijenta "error", co powoduje reset
	 * serwera oraz reset aplikacji klijenta oraz wyrzuca mu informacje o
	 * bledzie.
	 * 
	 * Kazdy port obsluguje inny watek, ktory wlacza i oblsuguje koleny watek do
	 * ustawiania sygnalu.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Serwer zostal uruchominoy!");
		while (true) {
			if(createChannelAtPort(7001) &&
			createChannelAtPort(7002) &&
			createChannelAtPort(7003) &&
			createChannelAtPort(7004) &&
			createChannelAtPort(7005) &&
			createChannelAtPort(7006)){
				System.out.println("Konfiguracja zakonczona powodzeniem!");
				continue;
			}
			System.out.println("Blad podczas kinfiguracji, zostaje ona przerwana!");
		}
	}

	private static boolean createChannelAtPort(int port) {
		try {			
			server = new ServerSocket(port);
			Socket clientSocket = server.accept();
			ChannelThread cH = new ChannelThread(clientSocket);
			if(cH.validateConnection()){				
				Thread t = new Thread(cH);
				t.start();
				System.out.println("Polaczenie z portem :" + port + " zostalo nawiazane poprawnie!" ) ;
				server.close();
				return true;
			}
			System.out.println("Tworzenie polaczenia z portem " + port + " zakonczone nie powodzeniem!");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
