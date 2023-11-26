/**
 MIT License

Copyright (c) 2023 bdsaen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.mqtoolbox.conn;

import java.io.FileInputStream;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.TranslateSSLCipherSuite;

public class Keystore {

	private boolean clickOk = true;
	private SSLContext sslContext = null;

	private KeyStore keystore = null;
	private KeyManagerFactory keyManagerFactory = null;
	private KeyStore truststore = null;
	private TrustManagerFactory trustManagerFactory = null;

	/**
	 * Create a new key store object
	 */
	public Keystore(String keyStore, String trustStore) {
		this.openKeyStore(keyStore, null, trustStore, null);
	}

	/**
	 * Create a new keystore object
	 * 
	 * @param keyStore     Name of the keystore
	 * @param keyStorePw   Keystore password
	 * @param trustStore   Name of the truststore
	 * @param trustStorePw Truststore password
	 */
	public Keystore(String keyStore, String keyStorePw, String trustStore, String trustStorePw) {
		this.openKeyStore(keyStore, keyStorePw, trustStore, trustStorePw);
	}

	/**
	 * Open a new key store
	 * 
	 * @param keyStore   Key store name
	 * @param trustStore Trust store name
	 */

	private final void openKeyStore(String keyStore, String keyStorePw, String trustStore, String trustStorePw) {
		if (keyStore == null || trustStore == null)
			return;

		String keyPasswordStr = keyStorePw;
		String trustPasswordStr = trustStorePw;

		if (keyStorePw == null) {
			List<String> passwords = this.promptPassword("Enter key store passwords");
			if (passwords == null)
				return;
			keyPasswordStr = passwords.get(0);
			trustPasswordStr = passwords.get(1);
		}

		try {
			keystore = KeyStore.getInstance("JKS");
			keystore.load(new FileInputStream(keyStore), keyPasswordStr.toCharArray());

			keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keystore, keyPasswordStr.toCharArray());

			truststore = KeyStore.getInstance("JKS");
			truststore.load(new FileInputStream(trustStore), trustPasswordStr.toCharArray());

			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(truststore);

			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

		} catch (KeyStoreException e) {
			System.out.println(String.format("Unable to set up SSL environment (KeyStoreException)\n\n%s", e.getMessage()));

		} catch (NoSuchAlgorithmException e) {
			System.out.println(String.format("Unable to set up SSL environment (NoSuchAlgorithmException)\n\n%s", e.getMessage()));

		} catch (CertificateException e) {
			System.out.println(String.format("Unable to set up SSL environment (CertificateException)\n\n%s", e.getMessage()));

		} catch (FileNotFoundException e) {
			System.out.println(String.format("Unable to set up SSL environment (FileNotFoundException)\n\n%s", e.getMessage()));

		} catch (IOException e) {
			System.out.println(String.format("Unable to set up SSL environment (IOException)\n\n%s", e.getMessage()));

		} catch (UnrecoverableKeyException e) {
			System.out.println(String.format("Unable to set up SSL environment (UnrecoverableKeyException)\n\n%s", e.getMessage()));

		} catch (KeyManagementException e) {
			System.out.println(String.format("Unable to set up SSL environment (KeyManagementException)\n\n%s", e.getMessage()));
		}
	}

	/**
	 * Prompt for a password
	 * 
	 * @param title
	 * @return
	 */
	private List<String> promptPassword(String title) {
		// 2022-03-26: Try to force the JOptionPane to the top always. Sometimes it disappears. Add settings to center the
		// frame.
		final JFrame jframe = new JFrame();
		jframe.setBounds(100, 100, 500, 400);
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setAlwaysOnTop(true);

		JLabel keystoreLabel = (JLabel) new JLabel("Enter keystore password");
		keystoreLabel.setFont(new Font(keystoreLabel.getFont().getName(), keystoreLabel.getFont().getStyle(), 20));
		JTextField keystorePw = (JTextField) new JPasswordField(20);
		keystorePw.setFont(new Font(keystorePw.getFont().getName(), keystorePw.getFont().getStyle(), 20));

		JLabel truststoreLabel = (JLabel) new JLabel("Enter truststore password (defaults to keystore)");
		truststoreLabel.setFont(new Font(truststoreLabel.getFont().getName(), truststoreLabel.getFont().getStyle(), 20));
		JTextField truststorePw = (JTextField) new JPasswordField(20);
		truststorePw.setFont(new Font(truststorePw.getFont().getName(), truststorePw.getFont().getStyle(), 20));

		Object[] fields = { keystoreLabel, keystorePw, truststoreLabel, truststorePw };

		JButton okButton = new JButton("OK");
		okButton.setFont(new Font(okButton.getFont().getName(), okButton.getFont().getStyle(), 20));
		AbstractAction okPressed = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			private JFrame jframe2 = jframe;

			@Override
			public void actionPerformed(ActionEvent e) {
				clickOk = true;
				jframe2.dispose();
			}
		};
		okButton.addActionListener(okPressed);
		okButton.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
				"Enter_pressed");
		okButton.getActionMap().put("Enter_pressed", okPressed);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font(cancelButton.getFont().getName(), cancelButton.getFont().getStyle(), 20));
		AbstractAction cancelPressed = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			private JFrame dummyFrame1 = jframe;

			@Override
			public void actionPerformed(ActionEvent e) {
				clickOk = false;
				dummyFrame1.dispose();
			}
		};
		cancelButton.addActionListener(cancelPressed);
		cancelButton.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
				"Enter_pressed");
		cancelButton.getActionMap().put("Enter_pressed", cancelPressed);

		Object[] options = { okButton, cancelButton };

		JOptionPane.showOptionDialog(jframe, fields, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, okButton);

		if (clickOk) {
			if (truststorePw.getText().length() == 0)
				truststorePw = keystorePw;
			return Arrays.asList(keystorePw.getText(), truststorePw.getText());
		} else {
			return null;
		}
	}

	/**
	 * Return the SSL connection details
	 * 
	 * @return
	 */
	public void getSSL(Hashtable<String, SSLSocketFactory> props) {
		if (this.sslContext == null)
			return;

		props.put(MQConstants.SSL_SOCKET_FACTORY_PROPERTY, sslContext.getSocketFactory());
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public KeyStore getKeystore() {
		return keystore;
	}

	public KeyManagerFactory getKeyManagerFactory() {
		return keyManagerFactory;
	}

	public KeyStore getTruststore() {
		return truststore;
	}

	public TrustManagerFactory getTrustManagerFactory() {
		return trustManagerFactory;
	}

	public static void main(String[] args) {
//		Keystore ks = new Keystore("a","a");
//		Keystore ks = new Keystore("D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "D:\\Dev\\#SSL\\mqtoolbox\\client.jks");
		Keystore ks = new Keystore("D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password", "D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password");
	}
}
