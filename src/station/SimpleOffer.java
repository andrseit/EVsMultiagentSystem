package station;

import main.ChargingSettings;

/**
 * Created by Thesis on 2/5/2018.
 */
public class SimpleOffer extends Offer {

    public SimpleOffer(ChargingSettings settings, int price) {
        super(settings, price);
    }

    @Override
    protected void computeRating() {
        setRating(1);
    }
}
