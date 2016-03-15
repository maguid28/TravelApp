package glen.dan.travelapp;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;




public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;


    public MainActivityTest(){
        super(MainActivity.class);
    }

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testTextViewNotNull(){
        TextView location_text = (TextView) activity.findViewById(R.id.location_text);
        assertNotNull(location_text);
    }

    @SmallTest
    public void testImageViewNotNull(){
        ImageView flag = (ImageView) activity.findViewById(R.id.flag);
        assertNotNull(flag);
    }


}
