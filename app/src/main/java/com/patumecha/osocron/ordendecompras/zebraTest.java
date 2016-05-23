package com.patumecha.osocron.ordendecompras;

import android.os.Looper;
import android.widget.Toast;

import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.FormatUtil;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.internal.ZebraPrinterCpcl;

import java.util.HashMap;

/**
 * com.patumecha.osocron.ordendecompras Created by osocron on 14/10/14.
 */
public class zebraTest {

    public static void main(String[] args) {

        zebraTest example = new zebraTest();

        String theBtMacAddress = args[0];
        example.sendZplOverBluetooth(theBtMacAddress);
        example.sendCpclOverBluetooth(theBtMacAddress,"      ");
    }

    public static void sendZplOverBluetooth(final String theBtMacAddress) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate connection for given Bluetooth&reg; MAC Address.
                    Connection thePrinterConn = new BluetoothConnection(theBtMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    // This example prints "This is a ZPL test." near the top of the label.
                    String zplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL test.^FS^XZ";

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(zplData.getBytes());

                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);

                    // Close the connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void sendCpclOverBluetooth(final String theBtMacAddress, final String orderNum) {

        new Thread(new Runnable() {
            public void run() {
                try {

                    // Instantiate connection for given Bluetooth&reg; MAC Address.
                    Connection thePrinterConn = new BluetoothConnection(theBtMacAddress);

                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    // This example prints "This is a CPCL test." near the top of the label.
                    String cpclData ="! UF header.fmt\r\n"
                            +orderNum+"\r\n";
                            //+ "CENTER\r\n"
                            //+ "STF 4 0 30 10 PAPELERIA EL IRIS DE JALAPA\r\n"
                            //+ "STF PLL_LAT.CSF 40 10 0 20 MATRIZ\r\n"
                            //+ "Hello world!\r\n"
                            //+ "RIGHT 383\r\n"
                            //+ "It's been a great day\r\n"
                            //+ "As I print this thing...\r\n"
                            //+ "ENDML\r\n"
                            //+ "FORM\r\n"
                            //+ "PRINT\r\n";

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(cpclData.getBytes());

                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);

                    // Close the connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();

                } catch (Exception e) {

                    // Handle communications error here.
                    Toast myToast = Toast.makeText(searchActivity.getInstance(),"Error de conexión! \nVerificar el estado de la impresora",Toast.LENGTH_LONG);
                    myToast.show();
                    e.printStackTrace();

                }
            }
        }).start();
    }
}
