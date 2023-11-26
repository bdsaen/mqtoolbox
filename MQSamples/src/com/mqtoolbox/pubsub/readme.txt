Complete the following to create the test environment for the pub-sub programs.

There programs have been tested using MQ 9.3.

5 March 2024

crtmqm QMGR1

echo echo define topic(STOCK.PRICE.IBM.STATE.RETAINED) topicstr(STOCK/PRICE/IBM) | runmqsc QMGR1
echo define topic(STOCK.PRICE.ORACLE.STATE.RETAINED) topicstr(STOCK/PRICE/ORACLE) | runmqsc QMGR1
echo define topic(STOCK.TRADE) topicstr(STOCK/TRADE) | runmqsc QMGR1
echo define topic(MARKET_OPEN) topicstr(MARKET_OPEN) | runmqsc QMGR1

echo DEFINE CHANNEL('TEST.SVRCONN') CHLTYPE(SVRCONN) MCAUSER('noaccess') | runmqsc QMGR1

echo SET CHLAUTH ('TEST.SVRCONN') TYPE(USERMAP) DESCR('Allow') CLNTUSER('your_logged_in_name') MCAUSER('MUSR_MQADMIN') USERSRC(MAP) | runmqsc QMGR1
echo SET CHLAUTH ('TEST.SVRCONN') TYPE(BLOCKUSER) DESCR('Default block') USERLIST(noaccess) | runmqsc QMGR1