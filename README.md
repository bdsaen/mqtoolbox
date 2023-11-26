# MQ Toolbox

## Overview

This repository contains sample Java code to perform various MQ activities.

For simplicity sake, the majority of this code has been written in main().

I have tried to cover many scenarios. Should you have a different scenario you need help with, feel free to contact me and I'll see whether I can assist.

All commands and code have been tested. Should you encounter problems, please let me know.

## Pre-requisites

The sample code has been tested for IBM MQ 9.3 using Oracle Java 8 on Windows.

The IBM MQ redistributable "9.3.0.6-IBM-MQC-Redist-Java" package was used. To locate these files, do a search of "Redistributable IBM MQ clients".

## Create test queue manager

The samples were tested with a test queue manager named QMGR1.

To create this queue manager, issue the following command on a machine where MQ server has been installed.

QMGR2 is used when testing queue manager groups using the JSON file.

````
crtmqm QMGR1
crtmqm QMGR2
````

## Create basic client channel

For the MQ client tests, issue the following runmqsc commands. Substitute your_logged_in_name with your logged in user name.

````
runmqsc QMGR1

DEFINE CHANNEL('TEST.SVRCONN') CHLTYPE(SVRCONN) MCAUSER('noaccess')

SET CHLAUTH ('TEST.SVRCONN') TYPE(USERMAP) DESCR('Allow') CLNTUSER('your_logged_in_name') MCAUSER('MUSR_MQADMIN') USERSRC(MAP)

SET CHLAUTH ('TEST.SVRCONN') TYPE(BLOCKUSER) DESCR('Default block') USERLIST(noaccess)
````

Optionally, repeat the above steps for QMGR2.

## Create 'binary' client channel definition table (CCDT)

Issue the following command to create a CLNTCONN channel (sending side). This must be named the same as the SVRCONN channel (receiving end). Set the CONNAME to a value that matches your environment.

````
runmqsc QMGR1

DEFINE CHANNEL('TEST.SVRCONN') CHLTYPE(CLNTCONN) CONNAME('localhost(1414)') QMNAME('QMGR1')
````

Optionally, repeat the above steps for QMGR2.

The CCDT file is located in the directory where the queue manager is created. For example,

On Windows check

````
C:/ProgramData/qmgrs/QMGR1/@ipcc/AMQCLCHL.TAB
````

On UNIX check,

````
/var/mqm/qmgrs/QMGR1/@ipcc/AMQCLCHL.TAB
````

A stop watch has been added so you can test the overhead of each action in your environment. Of course, this may be useful while testing MQ but is not required for any programs you may write.

## Create 'JSON' client channel definition table (CCDT) files

Sample JSON files have been provided to test the client connections.

## SSL testing

To complete the SSL testing, SSL needs to be enabled on the queue manager. Additionally, you'll need your own keystore.

For this exercise, we'll create our own CA root certificate, queue manager certificate and client certificate. In reality, this would be handled by a dedicated security team.

### Create CA certificates

Use keytool to create our test CA root and intermediate certificates. You can also complete this exercise with a root certificate only, but you'll usually deal with both root and intermediate certificates so we'll create both now.

Disclaimer: The settings used are for this exercise only and may not be ideal values to be used for real queue managers.

For this exercise, I am working out of the following Windows folder.

cd /d D:\Dev\#SSL\mqtoolbox

**Create the CA root certificate**

First, create the private keys for the root and intermediate certificates.

````
keytool -genkeypair -alias root4k -dname "cn=MQ ROOT CA V1 4K,O=MQTOOLBOX,C=US,OU=MQTOOLBOX,ST=MQTOOLBOX" -validity 10000 -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -ext bc:c=ca:true -ext KeyUsage:critical=keyCertSign,cRLSign -keystore ca_root4k.jks -keypass password -storepass password
````

The output of the command should look like:

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 10,000 days
        for: CN=MQ ROOT CA V1 4K, O=MQTOOLBOX, C=US, OU=MQTOOLBOX, ST=MQTOOLBOX

**Create the CA intermediate certificate**

````
keytool -genkeypair -alias int2k -dname "cn=MQ INTER CA V1 2K,O=MQTOOLBOX,C=US,OU=MQTOOLBOX,ST=MQTOOLBOX" -validity 10000 -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -ext bc:c=ca:true -ext KeyUsage:critical=keyCertSign,cRLSign -keystore ca_int2k.jks -keypass password -storepass password
````

The output of the command should look like:

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 5,000 days
        for: CN=MQ INTER CA V1 2K, O=MQTOOLBOX, C=US, OU=MQTOOLBOX, ST=MQTOOLBOX

**Extract the root CA public certificate**

````
keytool -exportcert -keystore ca_root4k.jks -alias root4k -storepass password -rfc > mq.root.v1.4k.der
````

**Sign the intermediate by the root CA**

Refer note (A) below

````
keytool -keystore ca_int2k.jks -storepass password -certreq -alias int2k | keytool -keystore ca_root4k.jks -validity 5000 -storepass password -gencert -alias root4k -ext BasicConstraints:critical=ca:true -ext KeyUsage:critical=keyCertSign,cRLSign -rfc >mq.inter.v1.2k.der
````

**Add the signed intermediate plus the root certificate into the intermediate keystore**

````
keytool -keystore ca_int2k.jks -storepass password -importcert -trustcacerts -noprompt -alias root -file mq.root.v1.4k.der
keytool -keystore ca_int2k.jks -storepass password -importcert -alias int2k -file mq.inter.v1.2k.der
````

The output of the commands should be:

Certificate was added to keystore
Certificate reply was installed in keystore

**View certificates**

To view the certificates you can issue the following commands:

````
keytool -keystore ca_root4k.jks -list
keytool -keystore ca_int2k.jks -list

keytool -keystore ca_root4k.jks -list -v -alias root4k -storepass password
keytool -keystore ca_int2k.jks -list -v -alias int2k -storepass password
````

### Create queue manager certificates

There are different ways to create a keystore for a queue managers - this is out-of-scope of this repository. The below shows one way for this exercise.

**Create the keystore**

````
runmqakm -keydb -create -pw password -stash -db C:\IBM\MQ\ProgramData\qmgrs\QMGR1\ssl\key.kdb
````

**Create the CSR**

````
runmqakm -certreq -create -db C:\IBM\MQ\ProgramData\qmgrs\QMGR1\ssl\key.kdb -label ibmwebspheremqqmgr1 -dn "CN=MQ QMGR QMGR1,O=MQTOOLBOX,C=US,OU=TEST,L=STAGING" -size 2048 -file D:\Dev\#SSL\mqtoolbox\qmgr1.arm -pw password
````

**View the CSR**

````
runmqakm -certreq -details -db C:\IBM\MQ\ProgramData\qmgrs\QMGR1\ssl\key.kdb -label ibmwebspheremqqmgr1 -pw password
````

**Sign the CSR**

````
keytool -keystore ca_int2k.jks -storepass password -gencert -alias int2k -ext ku:c=dig,keyEnc -validity 365 -ext "san=dns:localhost,ip:127.0.0.1" -ext eku=sa,ca -infile D:\Dev\#SSL\mqtoolbox\qmgr1.arm > D:\Dev\#SSL\mqtoolbox\qmgr1.der
````

**Add the signed certificate to the keystore**

Add the root, intermediate and signed queue manager certificates to the queue manager keystore.

````
runmqakm -cert -add -db C:\IBM\MQ\ProgramData\qmgrs\QMGR1\ssl\key.kdb -label root4k -file mq.root.v1.4k.der -format binary -pw password
runmqakm -cert -add -db C:\IBM\MQ\ProgramData\qmgrs\QMGR1\ssl\key.kdb -label int2k -file mq.inter.v1.2k.der -format binary -pw password
runmqakm -cert -receive -db C:\IBM\MQ\ProgramData\qmgrs\QMGR1\ssl\key.kdb -file qmgr1.der -format binary -pw password
````

**Refresh the queue manager SSL security cache**

Before SSL can be used, the queue manager SSL cache needs to be refreshed.

````
runmqsc QMGR1

refresh security type(ssl)
````

**NOTES**

(A) GSKVAL_ERR_CA_MISSING_CRITICAL_BASIC_CONSTRAINT (575051)

I have found many examples use a command similar to the below when signing the intermediate certificate:

````
keytool -keystore ca_int2k.jks -storepass password -certreq -alias int2k | keytool -keystore ca_root4k.jks -validity 5000 -storepass password -gencert -alias root4k **-ext bc=0 -ext bc=ca:true** -rfc >mq.inter.v1.2k.der
````

Later when issuing the command to add the signed queue manager certificate the following error will appear.

````
5724-H72 (C) Copyright IBM Corp. 1994, 2022.
CTGSK2052W An invalid basic constraint extension was found.
Additional untranslated info:
GSKKM_LAST_VALIDATION_ERROR: GSKVAL_ERR_CA_MISSING_CRITICAL_BASIC_CONSTRAINT (575051)
GSKKM_VALIDATIONFAIL_SUBJECT: GSKNativeValidator:: [IssuerName=]CN=MQ ROOT CA V1 4K[Serial#=]0de9eb55f7173354[SubjectName=]CN=MQ ROOT CA V1 4K[Class=]GSKVALMethod::PKIX[Issuer=]CN=MQ ROOT CA V1 4K[#=]0de9eb55f7173354[Subject=]CN=MQ ROOT CA V1 4K
CTGSK2043W Key entry validation failed.
````

To solve this issue, use the following command when signing the intermediate certificate:

````
keytool -keystore ca_int2k.jks -storepass password -certreq -alias int2k | keytool -keystore ca_root4k.jks -validity 5000 -storepass password -gencert -alias root4k -ext BasicConstraints:critical=ca:true -ext KeyUsage:critical=keyCertSign,cRLSign -rfc >mq.inter.v1.2k.der
````

### Create client certificates

For testing an MQ client connection, a client keystore needs to be created. For this example, I am using a JKS file.

As the 'runmqakm' command cannot create a JKS file, I am using runmqckm for the below.

**Create the client JKS file**

````
runmqckm -keydb -create -pw password -db client.jks -type jks
````

**Create the client CSR**

````
runmqckm -certreq -create -db client.jks -label ibmwebspheremqsean -dn "CN=A client,O=MQTOOLBOX,C=US,OU=ACLIENT,L=STAGING" -size 2048 -sig_alg SHA256WithRSA -file client.arm -pw password
````

**View the CSR**

````
runmqckm -certreq -details -db client.jks -label ibmwebspheremqsean -pw password
````

**Sign the CSR**

````
keytool -keystore ca_int2k.jks -storepass password -gencert -alias int2k -ext ku:c=dig,keyEnc -validity 365 -ext "san=dns:localhost,ip:127.0.0.1" -ext eku=sa,ca -infile client.arm > client.der
````

**Add the certificates**

````
runmqckm -cert -add -db client.jks -label root4k -file mq.root.v1.4k.der -format binary  -pw password
runmqckm -cert -add -db client.jks -label int2k -file mq.inter.v1.2k.der -format binary -pw password
runmqckm -cert -receive -db client.jks -file client.der -format binary -pw password
````

###Configure an SSL channel

````
runmqsc QMGR1

DEFINE CHANNEL('TEST.SVRCONN.SSL') CHLTYPE(SVRCONN) MCAUSER('noaccess') SSLCIPH(ECDHE_RSA_AES_256_GCM_SHA384)

SET CHLAUTH ('TEST.SVRCONN.SSL') TYPE(USERMAP) DESCR('Allow') CLNTUSER('your_logged_in_name') MCAUSER('MUSR_MQADMIN') USERSRC(MAP)

SET CHLAUTH ('TEST.SVRCONN.SSL') TYPE(BLOCKUSER) DESCR('Default block') USERLIST(noaccess)
````

For the JSON testing, sample files have been provided. For the SSL testing, you need to use the keystores created above.

Before testing with SSL, if your queue manager is on Windows, ensure the "mqm" group has access to the QMGR1 key.sth file.

If you get the following error when testing the MQ SSL client program "ClientConnection_Basic_WithSSL.java"

````
com.ibm.mq.jmqi.JmqiException: CC=2;RC=2397;AMQ9204: Connection to host 'localhost(1414)' rejected. [1=com.ibm.mq.jmqi.JmqiException[CC=2;RC=2397;AMQ9771: SSL handshake failed. [1=javax.net.ssl.SSLHandshakeException[Remote host terminated the handshake],3=localhost/127.0.0.1:1414 (localhost),4=SSLSocket.startHandshake,5=default]],3=localhost(1414),4=,5=RemoteTCPConnection.protocolConnect]
````

And you see the following error in the AMQERR01.LOG file

````
AMQ9660E: SSL key repository: password incorrect or, stash file absent or unusable.
````

Use Windows File Manager to locate the QMGR1/ssl/key.sth file. Right click on the file, select the Security tab, and if required click Edit to give the "mqm" group "Read & Execute" and "Read" access.

##Sample programs

The following sample programs are provide.

I'd suggest you work with them in the following order for learning, or if you know what you are looking for, just select the one that looks best.

**BindConnection**

This is the most basic 'bind' connection when the queue manager runs on the same server as your program.

**ClientConnection_Basic**

This is the most basic 'client' connection when the queue manager runs on a different server as your program.

I find this to be the example that pops up most frequently. While simple, I personally don't recomment using this style, especially when writing framework style programs.

**ClientConnection_Hashtable**

Create a client connection to a queue manager using a Hashtable. This is my recommended way as it allows for the most flexibility, especially when connecting to multiple queue managers from the same program.

**ClientConnection_Basic_WithSSL**

This shows the most basic way to connect to a queue manager using an SSL enabled client connection.

**ClientConnection_Hashtable_WithSSL**

A better, yet more complicated way, to connect to a queue manager using an SSL enabled client connection.

**ClientConnection_HashtableUsingTAB_Binary**

Connect to a remote queue manager using an MQ client channel definition table (CCDT). This example uses the traditional binary file you'll find in the qmgrs/QMGR/@ipcc directory

**ClientConnection_HashtableUsingTAB_JSON**

Similar to the CCDT example but this one uses a JSON file available in later versions of MQ.

**ClientConnection_HashtableUsingTAB_JSON_WithSSL**

Connect to a remote queue manager, with an SSL enabled client channel, using a JSON file.

**JMS**

One example to send JMS messages to a queue manager, including an example to send a native MQ message without an MQRFH2 header.

**Helper**

Contains some helper methods.

**StopWatch**

A stop watch.

**TranslateSSLCipherSuite**

Shows the difference between the SSL CipherSuite values used by Oracle Java or IBM Java.













