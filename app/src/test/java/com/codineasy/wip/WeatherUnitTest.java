package com.codineasy.wip;

import com.google.common.truth.Truth;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;

public class WeatherUnitTest {

    static String convertInputStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is);
        return s.hasNextLine() ? s.nextLine() : "";
    }

    @Test
    public void getNextHourWeatherWorks() {
        DarkSkyJSONHandler h = Mockito.mock(DarkSkyJSONHandler.class);

        String json = convertInputStreamToString(this.getClass().getClassLoader().getResourceAsStream("darksky.json"));

        try {
            Mockito.when(h.getJson()).thenReturn(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail();
        }

        WipGlobals.startTime =  1557590551;
        LocationDetail ld = Mockito.mock(LocationDetail.class);
        Mockito.when(ld.getTimeToArrive()).thenReturn(3600);
        Weather w = new Weather(h, ld);

        try {
            Weather w2 = w.getNextHourWeather();
            Truth.assertThat(w2.time()).isGreaterThan(w.time());

            Weather w3 = w2.getNextHourWeather();
            Truth.assertThat(w3.time()).isGreaterThan(w2.time());
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
