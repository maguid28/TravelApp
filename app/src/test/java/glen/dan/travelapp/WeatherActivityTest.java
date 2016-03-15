package glen.dan.travelapp;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import org.junit.Before;

public class WeatherActivityTest extends ActivityInstrumentationTestCase2<WeatherActivity> {


    WeatherActivity activity;

    public WeatherActivityTest() {
        super(WeatherActivity.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testTextViewNotNull(){
        TextView text = (TextView) activity.findViewById(R.id.city_field);
        assertNotNull(text);
    }


}
