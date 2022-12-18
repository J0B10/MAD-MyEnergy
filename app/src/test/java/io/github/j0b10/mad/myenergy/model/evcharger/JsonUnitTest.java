package io.github.j0b10.mad.myenergy.model.evcharger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.junit.Test;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import io.github.j0b10.mad.myenergy.model.evcharger.results.Measurement;

public class JsonUnitTest {

    @Test
    public void test() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new JsonDeserializer<Instant>() {
            @Override
            public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return Instant.parse(json.getAsString());
                } catch (UnsupportedOperationException | DateTimeParseException e) {
                    throw new JsonParseException(e);
                }
            }
        }).create();
        Measurement[] list = gson.fromJson(payload, Measurement[].class);
    }

    String payload = """
            [
                {
                    "channelId": "Measurement.Metering.GridMs.TotWIn.ChaSta",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:53.798Z",
                            "value": 2188.3
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.GridMs.TotWhIn.ChaSta",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:53.798Z",
                            "value": 3896700
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntA.phsA",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.430Z",
                            "value": -2.586
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntA.phsB",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.430Z",
                            "value": -3.204
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntA.phsC",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.431Z",
                            "value": -2.283
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntCsmpW",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 1785.8000000000002
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntCsmpWh",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 5414343
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntHz",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 49.996
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntPF",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 0.962
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntPhV.phsA",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.430Z",
                            "value": 232.055
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntPhV.phsB",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.430Z",
                            "value": 234.174
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntPhV.phsC",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.431Z",
                            "value": 235.192
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntVAr",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 508.3
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntVAr.phsA",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 143.6
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntVAr.phsB",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 220.9
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntVAr.phsC",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 143.9
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntW",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 0
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntW.phsA",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": -576.2
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntW.phsB",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": -703
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntW.phsC",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": -506.5
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Metering.PCCMs.PlntWh",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-11-21T10:00:55.429Z",
                            "value": 1.0656777E7
                        }
                    ]
                },
                {
                    "channelId": "Measurement.Operation.Health",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-09-28T13:07:03.826Z",
                            "value": 307
                        }
                    ]
                },
                {
                    "channelId": "Setpoint.ExternalPlantControl.Inverter.VArModCfg.VArCtlVolCfg.VolRef.VolRefPu",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-04-29T14:09:43.733Z"
                        }
                    ]
                },
                {
                    "channelId": "Setpoint.PlantControl.Inverter.FstStop",
                    "componentId": "Plant:1",
                    "values": [
                        {
                            "time": "2022-04-29T14:09:57.661Z",
                            "value": 1467
                        }
                    ]
                }
            ]
            """;
}
