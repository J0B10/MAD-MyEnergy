package io.github.j0b10.mad.myenergy;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import nl.joery.timerangepicker.TimeRangePicker;
import nl.joery.timerangepicker.TimeRangePicker.Time;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Context appContext;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testSetStartTime() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setStartTime(new Time(7, 20));
        assertEquals("7:20", picker.getStartTime().toString());
    }

    @Test
    public void testSetStartTimePM() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setStartTime(new Time(14, 30));
        assertEquals("14:30", picker.getStartTime().toString());
    }

    @Test
    public void testSetEndTime() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setEndTime(new Time(9, 30));
        assertEquals("9:30", picker.getEndTime().toString());
    }

    @Test
    public void testSetEndTimePM() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setEndTime(new Time(14, 30));
        assertEquals("14:30", picker.getEndTime().toString());
    }

    @Test
    public void testSetTime() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setStartTime(new Time(7, 20));
        picker.setEndTime(new Time(9,30));
        assertEquals("7:20", picker.getStartTime().toString());
        assertEquals("09:30", picker.getEndTime().toString());
    }

    @Test
    public void testSetTime12h() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setStartTime(new Time(7, 20));
        picker.setEndTime(new Time(14,30));
        assertEquals("7:20", picker.getStartTime().toString());
        assertEquals("14:30", picker.getEndTime().toString());
    }

    @Test
    public void testSetTimeNextDay() {
        TimeRangePicker picker = new TimeRangePicker(appContext, null, 0);
        picker.setStartTime(new Time(14, 30));
        picker.setEndTime(new Time(9,30));
        assertEquals("14:30", picker.getStartTime().toString());
        assertEquals("9:30", picker.getEndTime().toString());
    }
}