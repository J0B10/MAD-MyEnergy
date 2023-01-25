package io.github.j0b10.mad.myenergy.model.pv.tripower;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadHoldingRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.github.j0b10.mad.myenergy.model.target.BaseProvider;
import io.github.j0b10.mad.myenergy.model.target.PVStatusProvider;

public class TripowerPVStatusAdapter extends BaseProvider implements PVStatusProvider {

    private static final int TIMEOUT = 1000;

    private final TripowerPVInverter settings;

    private final MutableLiveData<Double> pvProduction = new MutableLiveData<>(0.0);
    private ModbusMaster modbus;

    public TripowerPVStatusAdapter(TripowerPVInverter pvInverter) throws UnknownHostException {
        this.settings = pvInverter;
    }

    @Override
    public void start() {
        super.start();
        executor.execute(() -> {
            try {
                TcpParameters parameters = new TcpParameters();
                parameters.setHost(InetAddress.getByName(settings.getIP()));
                parameters.setPort(settings.getPort());
                parameters.setKeepAlive(true);
                this.modbus = ModbusMasterFactory.createModbusMasterTCP(parameters);
                modbus.setResponseTimeout(TIMEOUT);
                modbus.connect();
            } catch (ModbusIOException | UnknownHostException e) {
                modbus = null;
                Log.e("Tripower", e.getMessage(), e);
                error.postValue(e);
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        try {
            modbus.disconnect();
        } catch (ModbusIOException e) {
            Log.e("Tripower", e.getMessage(), e);
            error.postValue(e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Response requestHoldingRegisters(int register, int amount)
            throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest();
        request.setServerAddress(settings.getUnitId());
        request.setStartAddress(register);
        request.setQuantity(amount);
        modbus.processRequest(request);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) request.getResponse();
        return new Response(register, response.getHoldingRegisters().getBytes());
    }

    @Override
    protected void update() throws Exception {
        Response response = requestHoldingRegisters(30775, 100);
        int pvW = response.getSint32(30775); // GridMs.TotW
        pvProduction.postValue(pvW / 1000.0);
    }

    @Override
    public LiveData<Double> pvProduction() {
        return pvProduction;
    }
}
