package guru.springframework.msscbeerorderservice.services.beer;

import java.net.URL;
import java.net.URLConnection;

/**
 * @author cevher
 */
public class BeerServiceController {
    public boolean beerServiceListening(String beerServiceHost) {

        try {
            URL url = new URL(beerServiceHost);
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
