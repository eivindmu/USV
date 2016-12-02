package USVProsjekt;

import java.util.HashMap;
import java.util.Map;


public class NMEAparser {
	
	
	interface SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position);
	}
	
	// utils
	static double Latitude2Decimal(String lat, String NS) {
		double med = Double.parseDouble(lat.substring(2))/60.0;
		med +=  Double.parseDouble(lat.substring(0, 2));
		if(NS.startsWith("S")) {
			med = -med;
		}
		return med;
	}

	static double Longitude2Decimal(String lon, String WE) {
		double med = Double.parseDouble(lon.substring(3))/60.0;
		med +=  Double.parseDouble(lon.substring(0, 3));
		if(WE.startsWith("W")) {
			med = -med;
		}
		return med;
	}

	// parsers 
	class GPGGA implements SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position) {
			position.time = Double.parseDouble(tokens[1]);
			position.lat = Latitude2Decimal(tokens[2], tokens[3]);
			position.lon = Longitude2Decimal(tokens[4], tokens[5]);
			position.quality = Integer.parseInt(tokens[6]);
			position.satNum = Integer.parseInt(tokens[7]);
			position.altitude = Double.parseDouble(tokens[9]);
			return true;
		}
	}
	
	class GPGGL implements SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position) {
			position.lat = Latitude2Decimal(tokens[1], tokens[2]);
			position.lon = Longitude2Decimal(tokens[3], tokens[4]);
			position.time = Double.parseDouble(tokens[5]);
			return true;
		}
	}
	
	class GPRMC implements SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position) {
			position.time = Double.parseDouble(tokens[1]);
			position.lat = Latitude2Decimal(tokens[3], tokens[4]);
			position.lon = Longitude2Decimal(tokens[5], tokens[6]);
			position.velocity = (
                                0.51444*Double.parseDouble(tokens[7]));
			position.dir = Double.parseDouble(tokens[8]);
			return true;
		}
	}
	
	class GPVTG implements SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position) {
			position.dir = Double.parseDouble(tokens[3]);
			return true;
		}
	}
	
	class GPRMZ implements SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position) {
			position.altitude = Double.parseDouble(tokens[1]);
			return true;
		}
	}
        
        class PGTOP implements SentenceParser {
		public boolean parse(String [] tokens, GPSPosition position) {
                        char number = tokens[2].charAt(0);
			position.antenna = Integer.parseInt(""+number);
			return true;
		}
	}
	
	public class GPSPosition {
		public double time = 0.0f;
		public double lat = 0.0f;
		public double lon = 0.0f;
		public boolean fixed = false;
		public int quality = 0;
		public double dir = 0.0f;
		public double altitude = 0.0f;
		public double velocity = 0.0f;
                public int antenna = 0;
                public int satNum = 0;
		
		public void updatefix() {
			fixed = quality > 0;
		}
		
                @Override
		public String toString() {
			return String.format("POSITION: lat: %d, lon: "
                                + "%d, time: %d, Q: %d, dir: %d, alt(m):"
                                + " %d, vel(m/s): %d, ant: %d, satNum: "
                                + "%d", lat, lon, time, quality, dir,
                                altitude, velocity, antenna,satNum);
		}
	}
	
	GPSPosition position = new GPSPosition();
	
	private static final Map<String, SentenceParser> sentenceParsers 
                = new HashMap<String, SentenceParser>();
	/**
         * Konverterer NMEA setninger til brukende variabler
         */
    public NMEAparser() {
    	sentenceParsers.put("GPGGA", new GPGGA());
    	sentenceParsers.put("GPGGL", new GPGGL());
    	sentenceParsers.put("GPRMC", new GPRMC());
    	sentenceParsers.put("GPRMZ", new GPRMZ());
    	sentenceParsers.put("PGTOP", new PGTOP());
    	//only really good GPS devices have this sentence but ...
    	sentenceParsers.put("GPVTG", new GPVTG());
    }
    
	public synchronized GPSPosition parse(String line) {
		if(line.startsWith("$")) {
			String nmea = line.substring(1);
			String[] tokens = nmea.split(",");
			String type = tokens[0];
			//sjekker hvilken setning som er mottatt og parser 
			if(sentenceParsers.containsKey(type)) {
                            sentenceParsers.get(type).parse(tokens, position);
			}
			position.updatefix();
		}
		
		return position;
	}
}