/*
 * Copyright 2014 individual contributors as indicated by the @author 
 * tags
 * 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.sector67.nsaaway;

import org.sector67.nsaaway.android.AlertUtils;
import org.sector67.nsaaway.key.KeyUtils;
import org.sector67.otp.envelope.EnvelopeUtils;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//SerialSendCode Start
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
//SerialSendCode End



/**
 * 
 * @author scott.hasse@gmail.com
 * 
 */
public class DisplayCiphertextActivity extends Activity {
	// Class variables
	private String keyName;
	private String ciphertext = null;
	private String envelope = null;
	
	private int offset;
	private int length;
	
	//SerialSendCode Start
    private final static String TAG = "FPGA_FIFO Activity";

    private static D2xxManager ftD2xx = null;
    private FT_Device ftDev;

    static final int READBUF_SIZE  = 256;
    byte[] rbuf  = new byte[READBUF_SIZE];
    char[] rchar = new char[READBUF_SIZE];
    int mReadSize=0;

    TextView tvRead;

    boolean mThreadIsStopped = true;
    Handler mHandler = new Handler();
    Thread mThread;
	//SerialSendCode End


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_display_ciphertext);
		
		// Grab all the buttons
		Button sendAsKeystrokes = (Button) findViewById(R.id.sendAsKeystrokesButton);
		Button copyToKeyboardButton = (Button) findViewById(R.id.copyToKeyboardButton);
		Button eraseKeyAndContinueButton = (Button) findViewById(R.id.eraseKeyAndContinueButton);

		if (ciphertext == null) {
			Intent i = getIntent();
			ciphertext = i.getStringExtra(MainActivity.CIPHERTEXT_KEY);
			length = i.getIntExtra(MainActivity.LENGTH_KEY, -1);
			offset = i.getIntExtra(MainActivity.OFFSET_KEY, -1);
			keyName = i.getStringExtra(MainActivity.KEYNAME_KEY);
			//TODO: move the new lines to a configuration or the EnvelopeUtils
			TextView ciphertextView = (TextView) findViewById(R.id.ciphertext);
			String result = EnvelopeUtils.getEnvelopeHeader();
			result = result + EnvelopeUtils.formatHeader("Offset", Integer.toString(offset));
			result = result + EnvelopeUtils.getBodySeparator();
			result = result + "\n";
			result = result + ciphertext;
			result = result + "\n";
			result = result + EnvelopeUtils.getEnvelopeFooter();
			envelope = result;
			ciphertextView.setText(result);
			
			
			//SerialSendCode Start
			updateView(false);

	        try {
	            ftD2xx = D2xxManager.getInstance(this);
	        } catch (D2xxManager.D2xxException ex) {
	            Log.e(TAG,ex.toString());
	        }

	        IntentFilter filter = new IntentFilter();
	        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
	        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
	        registerReceiver(mUsbReceiver, filter);
	        //SerialSendCode End
		}
		

	

		// Listen for a button event
		sendAsKeystrokes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				
				//Toast.makeText(getApplicationContext(), "Sending Data", Toast.LENGTH_SHORT).show();
				
				
				openDevice();
				
				for (int i = 0; i < envelope.length(); i++){       
				    //Process char
				    WriteData(envelope.substring(i,i+1));
				    //sleep command
				    try
				    {
				        Thread.sleep(30);
				    }
				    catch (Exception e){}
				    
				}
				
				closeDevice();
				Toast.makeText(getApplicationContext(), "Serial Send Complete", Toast.LENGTH_SHORT).show();
				
				//original code below
				//Intent nextScreen = new Intent(getApplicationContext(),
						//MainActivity.class);
				//startActivity(nextScreen);
			}
		});

		// Listen for a button event
		copyToKeyboardButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Gets a handle to the clipboard service.
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData
						.newPlainText("simple text", envelope);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(getApplicationContext(),
						getString(R.string.message_copied_to_clipboard),
						Toast.LENGTH_SHORT).show();

			}
		});



	    
		// Listen for a button event
		eraseKeyAndContinueButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						try {
							KeyStore ks = KeyUtils.getKeyStore(getApplicationContext());
							ks.eraseKeyBytes(keyName, offset, length);
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.message_key_bytes_erased),
									Toast.LENGTH_SHORT).show();
							Intent nextScreen = new Intent(
									getApplicationContext(), MainActivity.class);
							startActivity(nextScreen);
						} catch (KeyException e) {
							AlertUtils
									.createAlert(
											getString(R.string.error_erasing_key_bytes),
											e.getMessage(),
											DisplayCiphertextActivity.this).show();
						}

					}
				});
		
		

	}
	
	
	
	
	

	//SerialSendCode Start
    public void WriteData(String datastring) {
        if(ftDev == null) {
            return;
        }

        synchronized (ftDev) {
            if(ftDev.isOpen() == false) {
                Log.e(TAG, "onClickWrite : Device is not open");
                return;
            }

            ftDev.setLatencyTimer((byte)16);

            byte[] writeByte = datastring.getBytes();
            ftDev.write(writeByte, datastring.length());
        }
    }

  

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadIsStopped = true;
        unregisterReceiver(mUsbReceiver);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/

    private void openDevice() {
        if(ftDev != null) {
            if(ftDev.isOpen()) {
                if(mThreadIsStopped) {
                    updateView(true);
                    SetConfig(19200, (byte)8, (byte)1, (byte)0, (byte)0);
                    ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                    ftDev.restartInTask();
                    new Thread(mLoop).start();
                }
                return;
            }
        }

        int devCount = 0;
        devCount = ftD2xx.createDeviceInfoList(this);

        Log.d(TAG, "Device number : "+ Integer.toString(devCount));

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);

        if(devCount <= 0) {
            return;
        }

        if(ftDev == null) {
            ftDev = ftD2xx.openByIndex(this, 0);
        } else {
            synchronized (ftDev) {
                ftDev = ftD2xx.openByIndex(this, 0);
            }
        }

        if(ftDev.isOpen()) {
            if(mThreadIsStopped) {
                updateView(true);
                SetConfig(19200, (byte)8, (byte)1, (byte)0, (byte)0);
                ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();
                new Thread(mLoop).start();
            }
        }
    }

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int i;
            int readSize;
            mThreadIsStopped = false;
            while(true) {
                if(mThreadIsStopped) {
                    break;
                }

/*                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
*/
                synchronized (ftDev) {
                    readSize = ftDev.getQueueStatus();
                    if(readSize>0) {
                        mReadSize = readSize;
                        if(mReadSize > READBUF_SIZE) {
                            mReadSize = READBUF_SIZE;
                        }
                        ftDev.read(rbuf,mReadSize);

                        // cannot use System.arraycopy
                        for(i=0; i<mReadSize; i++) {
                            rchar[i] = (char)rbuf[i];
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvRead.append(String.copyValueOf(rchar,0,mReadSize));
                  
                            }
                        });

                    } // end of if(readSize>0)
                } // end of synchronized
            }
        }
    };

    private void closeDevice() {
        mThreadIsStopped = true;
        updateView(false);
        if(ftDev != null) {
            ftDev.close();
        }
    }

    private void updateView(boolean on) {
        
    }

    public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (ftDev.isOpen() == false) {
            Log.e(TAG, "SetConfig: device not open");
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
        case 7:
            dataBits = D2xxManager.FT_DATA_BITS_7;
            break;
        case 8:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        default:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        }

        switch (stopBits) {
        case 1:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        case 2:
            stopBits = D2xxManager.FT_STOP_BITS_2;
            break;
        default:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        }

        switch (parity) {
        case 0:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        case 1:
            parity = D2xxManager.FT_PARITY_ODD;
            break;
        case 2:
            parity = D2xxManager.FT_PARITY_EVEN;
            break;
        case 3:
            parity = D2xxManager.FT_PARITY_MARK;
            break;
        case 4:
            parity = D2xxManager.FT_PARITY_SPACE;
            break;
        default:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
        case 0:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        case 1:
            flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
            break;
        case 2:
            flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
            break;
        case 3:
            flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
            break;
        default:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        }

        // TODO : flow ctrl: XOFF/XOM
        // TODO : flow ctrl: XOFF/XOM
        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }

    // done when ACTION_USB_DEVICE_ATTACHED
    @Override
    protected void onNewIntent(Intent intent) {
        openDevice();
    };

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // never come here(when attached, go to onNewIntent)
                openDevice();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                closeDevice();
            }
        }
    };
    
  //SerialSendCode Start



}
