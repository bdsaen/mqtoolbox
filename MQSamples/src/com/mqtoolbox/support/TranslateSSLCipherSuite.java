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

package com.mqtoolbox.support;

import java.util.EnumSet;

/*
 * Translate an IBM MQ Explorer or RUNMQSC SSL CipherSpec to a Java IBM or Oracle SSL CipherSuite value. 
 */

public class TranslateSSLCipherSuite {

	public enum Protocol {
		SSLv3, TLSv1_0, TLSv1_2, TLSv1_3;
	}

	public enum SSLCipherSuite {

		// SSLv3
		NULL_MD5("SSL_RSA_WITH_NULL_MD5", "SSL_RSA_WITH_NULL_MD5", Protocol.SSLv3), //
		NULL_SHA("SSL_RSA_WITH_NULL_SHA", "SSL_RSA_WITH_NULL_SHA", Protocol.SSLv3), //
		RC4_MD5_EXPORT("SSL_RSA_EXPORT_WITH_RC4_40_MD5", "", Protocol.SSLv3), //
		RC4_MD5_US("SSL_RSA_WITH_RC4_128_MD5", "SSL_RSA_WITH_RC4_128_MD5", Protocol.SSLv3), //
		RC4_SHA_US("SSL_RSA_WITH_RC4_128_SHA", "", Protocol.SSLv3), //
		RC2_MD5_EXPORT("SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5", "SSL_RSA_EXPORT_WITH_RC4_40_MD5", Protocol.SSLv3), //
		DES_SHA_EXPORT("SSL_RSA_WITH_DES_CBC_SHA", "", Protocol.SSLv3), //
		RC4_56_SHA_EXPORT1024("SSL_RSA_EXPORT1024_WITH_RC4_56_SHA", "", Protocol.SSLv3), //
		DES_SHA_EXPORT1024("SSL_RSA_EXPORT1024_WITH_DES_CBC_SHA", "", Protocol.SSLv3), //
		TRIPLE_DES_SHA_US("SSL_RSA_WITH_3DES_EDE_CBC_SHA", "", Protocol.SSLv3), //
		AES_SHA_US("", "", Protocol.SSLv3), //
		TLS_RSA_WITH_3DES_EDE_CBC_SHA("SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA", Protocol.SSLv3), //
		FIPS_WITH_DES_CBC_SHA("SSL_RSA_FIPS_WITH_DES_CBC_SHA", "", Protocol.SSLv3), //
		FIPS_WITH_3DES_EDE_CBC_SHA("SSL_RSA_FIPS_WITH_3DES_EDE_CBC_SHA", "", Protocol.SSLv3), //

		// TLS 1.0
		TLS_RSA_WITH_AES_128_CBC_SHA("SSL_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", Protocol.TLSv1_0), //
		TLS_RSA_WITH_AES_256_CBC_SHA("SSL_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", Protocol.TLSv1_0), //
		TLS_RSA_WITH_DES_CBC_SHA("SSL_RSA_WITH_DES_CBC_SHA", "SSL_RSA_WITH_DES_CBC_SHA", Protocol.TLSv1_0), //

		// TLS 1.2
		ECDHE_ECDSA_3DES_EDE_CBC_SHA256("SSL_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", Protocol.TLSv1_2), //
		ECDHE_ECDSA_AES_128_CBC_SHA256("SSL_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", Protocol.TLSv1_2), //
		ECDHE_ECDSA_AES_128_GCM_SHA256("SSL_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", Protocol.TLSv1_2), //
		ECDHE_ECDSA_AES_256_CBC_SHA384("SSL_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", Protocol.TLSv1_2), //
		ECDHE_ECDSA_AES_256_GCM_SHA384("SSL_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", Protocol.TLSv1_2), //
		ECDHE_ECDSA_NULL_SHA256("SSL_ECDHE_ECDSA_WITH_NULL_SHA", "TLS_ECDHE_ECDSA_WITH_NULL_SHA", Protocol.TLSv1_2), //
		ECDHE_ECDSA_RC4_128_SHA256("SSL_ECDHE_ECDSA_WITH_RC4_128_SHA", "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", Protocol.TLSv1_2), //
		ECDHE_RSA_3DES_EDE_CBC_SHA256("SSL_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", Protocol.TLSv1_2), //
		ECDHE_RSA_AES_128_CBC_SHA256("SSL_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", Protocol.TLSv1_2), //
		ECDHE_RSA_AES_128_GCM_SHA256("SSL_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", Protocol.TLSv1_2), //
		ECDHE_RSA_AES_256_CBC_SHA384("SSL_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", Protocol.TLSv1_2), //
		ECDHE_RSA_AES_256_GCM_SHA384("SSL_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", Protocol.TLSv1_2), //
		ECDHE_RSA_NULL_SHA256("SSL_ECDHE_RSA_WITH_NULL_SHA", "TLS_ECDHE_RSA_WITH_NULL_SHA", Protocol.TLSv1_2), //
		ECDHE_RSA_RC4_128_SHA256("SSL_ECDHE_RSA_WITH_RC4_128_SHA", "TLS_ECDHE_RSA_WITH_RC4_128_SHA", Protocol.TLSv1_2), //
		TLS_RSA_WITH_AES_128_CBC_SHA256("SSL_RSA_WITH_AES_128_CBC_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA256", Protocol.TLSv1_2), //
		TLS_RSA_WITH_AES_128_GCM_SHA256("SSL_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_GCM_SHA256", Protocol.TLSv1_2), //
		TLS_RSA_WITH_AES_256_CBC_SHA256("SSL_RSA_WITH_AES_256_CBC_SHA256", "TLS_RSA_WITH_AES_256_CBC_SHA256", Protocol.TLSv1_2), //
		TLS_RSA_WITH_AES_256_GCM_SHA384("SSL_RSA_WITH_AES_256_GCM_SHA384", "TLS_RSA_WITH_AES_256_GCM_SHA384", Protocol.TLSv1_2), //
		TLS_RSA_WITH_NULL_SHA256("SSL_RSA_WITH_NULL_SHA256", "TLS_RSA_WITH_NULL_SHA256", Protocol.TLSv1_2), //
		TLS_RSA_WITH_RC4_128_SHA256("SSL_RSA_WITH_RC4_128_SHA", "SSL_RSA_WITH_RC4_128_SHA", Protocol.TLSv1_2), //
		ANY_TLS12("*TLS12", "*TLS12", Protocol.TLSv1_2), //

		// TLS 1.3
		TLS_AES_128_GCM_SHA256("TLS_AES_128_GCM_SHA256", "TLS_AES_128_GCM_SHA256", Protocol.TLSv1_3), //
		TLS_AES_256_GCM_SHA384("TLS_AES_256_GCM_SHA384", "TLS_AES_256_GCM_SHA384", Protocol.TLSv1_3), //
		TLS_CHACHA20_POLY1305_SHA256("TLS_CHACHA20_POLY1305_SHA256", "TLS_CHACHA20_POLY1305_SHA256", Protocol.TLSv1_3), //
		TLS_AES_128_CCM_SHA256("TLS_AES_128_CCM_SHA256", "TLS_AES_128_CCM_SHA256", Protocol.TLSv1_3), //
		TLS_AES_128_CCM_8_SHA256("TLS_AES_128_CCM_8_SHA256", "TLS_AES_128_CCM_8_SHA256", Protocol.TLSv1_3), //
		ANY("*ANY", "*ANY", Protocol.TLSv1_3), //
		ANY_TLS13("*TLS13", "*TLS13", Protocol.TLSv1_3), //
		ANY_TLS12_OR_HIGHER("*TLS12ORHIGHER", "*TLS12ORHIGHER", Protocol.TLSv1_3), //
		ANY_TLS13_OR_HIGHER("*TLS13ORHIGHER", "*TLS13ORHIGHER", Protocol.TLSv1_3) //

		;

		private String ibmValue;
		private String oracleValue;
		private Protocol protocol;

		SSLCipherSuite(String ibmValue, String oracleValue, Protocol protocol) {
			this.ibmValue = ibmValue;
			this.oracleValue = oracleValue;
			this.protocol = protocol;
		}

		/**
		 * @return the ibmValue
		 */
		public String getIbmValue() {
			return ibmValue;
		}

		/**
		 * @return the oracleValue
		 */
		public String getOracleValue() {
			return oracleValue;
		}

		/**
		 * @return the SSL CipherSuite value after determining the Java provider
		 */
		public String getValue() {
			setCipherMappings();

			if (System.getProperty("com.ibm.mq.cfg.useIBMCipherMappings") == "ibm")
				return this.ibmValue;
			else
				return this.oracleValue;
		}

		/**
		 * @return the protocol
		 */
		public String getProtocol() {
			return protocol.toString();
		}
	}

	/*
	 * Display all values in the enum
	 */
	public final void printEnumList() {
		EnumSet.allOf(SSLCipherSuite.class).forEach(val -> System.out.println(
				String.format("%-40s : %-10s : ibm = %-40s : ora =  %-40s", val, val.getProtocol(), val.getIbmValue(), val.getOracleValue())));
	}

	/*
	 * Set the SSL Cipher Mapping rule depending on whether IBM or Oracle java is being used. This sets the system property
	 * com.ibm.mq.cfg.useIBMCipherMappings to true or false. To override this method setting, pass the JVM arg
	 * -Dcom.ibm.mq.cfg.useIBMCipherMappings=true or false
	 */
	public static final void setCipherMappings() {
		if (System.getProperty("com.ibm.mq.cfg.useIBMCipherMappings") == null) {
			if (System.getProperty("java.vendor").toLowerCase().indexOf("ibm") != -1) {
				System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "true");
			} else {
				System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
			}
		}
	}

	public static void main(String[] args) {
		TranslateSSLCipherSuite ssl = new TranslateSSLCipherSuite();
		ssl.printEnumList();
	}
}
