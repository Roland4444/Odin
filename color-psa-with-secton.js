
'sms'=>::login{'avs'}, ::pass{'7BBAP7nkTCA4L3r'},::sendto{'89050604353','89029556848'},::enabled{'true'}.  в sendto должно быть минимум два отправителя '', ''
'psa'=>::psa{'login':'unused','pass':'unused'},::db{jdbc:mysql://127.0.0.1:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}.
'psaconnector'=>::psalogin{'root'},::psapass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}.
'wprocessor'=>::usedepsmap{'true','1':'ACKK','2':'Kutum','24':'Babaevskogo'},::pathtoimgs{/home/coder/spark/sparktest/photo/},::addresstoresend{https://db.avs.com.ru/storage/purchase/import},::enabled{'true'},:$
'dbconnector'=>::dblogin{'root'},::dbpass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}.
'dbhelper'=>::hockDeleting{false},::ProductionMode{true},::enabled{'true'}.
