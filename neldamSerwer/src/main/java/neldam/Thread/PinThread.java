package neldam.Thread;

import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class PinThread implements Runnable {
	private String signalType;
	private boolean isRunning;
	protected GpioPinDigitalOutput pin;

	public PinThread(String gpioNumber) {
		this.pin = ChannelThread.gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(gpioNumber), "MyLED",
				PinState.HIGH);
		pin.low();
	}

	public void run() {
		while (!"STOP_THREAD".equals(signalType)) {
			setLow();
			setHigh();
			oneMilisecondSignal();
			oneAndHalfMilisecondSignal();
			twoMilisecondsSignal();
			setReferenceSpeed();
			try {
				TimeUnit.MICROSECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void setLow() {
		if ("STOP".equals(signalType)) {
			System.out.println("STOP!");
			signalType = "NON";
			pin.low();
		}
	}
	
	protected void setHigh() {
		if ("START".equals(signalType)) {
			System.out.println("START!");
			signalType = "NON";
			pin.high();
		}
	}

	protected void oneMilisecondSignal() {
		try {
			while ("SET_RIGHT".equals(signalType)) {
				System.out.println("MIN_SPEED!");
				pin.high();
				TimeUnit.MICROSECONDS.sleep(1000);
				pin.low();
				TimeUnit.MICROSECONDS.sleep(19000);
			}
		} catch (InterruptedException e) {
			System.out.println("Blad podczas tworzenia sygnalu o dlugosci jednej milisekundy!");
		}

	}

	protected void oneAndHalfMilisecondSignal() {
		try {
			while ("SET_MIDLE".equals(signalType)) {
				System.out.println("MEDIUM_SPEED!");
				pin.high();
				TimeUnit.MICROSECONDS.sleep(15000);
				pin.low();
				TimeUnit.MICROSECONDS.sleep(18500);
			}
		} catch (InterruptedException e) {
			System.out.println("Blad podczas tworzenia sygnalu o dlugosci poltorej milisekundy!");
		}
	}

	protected void twoMilisecondsSignal() {
		try {
			while ("SET_LEFT".equals(signalType)) {
				System.out.println("MAX_SPEED!");
				pin.high();
				TimeUnit.MICROSECONDS.sleep(2000);
				pin.low();
				TimeUnit.MICROSECONDS.sleep(18000);
			}
		} catch (InterruptedException e) {
			System.out.println("Blad podczas tworzenia sygnalu o dlugosci dwoch milisekund!");
		}
	}

	protected void setReferenceSpeed() {
		try {
			signalTypeToInt();
			while (isRunning) {
			System.out.println("SET_SPEED -> " + signalTypeToInt());
				pin.high();
				TimeUnit.MICROSECONDS.sleep(1000 + signalTypeToInt());
				pin.low();
				TimeUnit.MICROSECONDS.sleep(19000 - signalTypeToInt());
			}
		} catch (InterruptedException e) {
			System.out.println("Blad podczas tworzenia sygnalu o wybranej dlugosci!");
		}
	}

	private int signalTypeToInt() {
		try{
			isRunning = true;
			return Integer.parseInt(signalType);
		}
		catch(NumberFormatException e){
			isRunning = false;
			return 0;
		}		
	}

	public String getSignalType() {
		return signalType;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}

}
