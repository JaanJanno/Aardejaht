Georivals
=========

Server: [![Build Status](https://travis-ci.org/JaanJanno/Georivals.svg)](https://travis-ci.org/JaanJanno/Georivals)

Latest mobile app apk: http://pacific-plains-8541.herokuapp.com/download

Server accessible from: http://pacific-plains-8541.herokuapp.com/

## Introduction

Georivals is a massively multiplayer online real-time strategy mobile game. The whole world is divided into provinces and the goal is to conquer as many as possible. 

#### Core mechanics:
* Conquer provinces from both AI and other players
* Gather units by physically visiting owned provinces
* New units are generated every 24 hours
* Manage attacks and unit movements
* Procedurally generated AI enemies
* Compate scores with other players

## Eesti keeles
'Georivals' on mobiilirakendus, mis ühendab endas meelelahutuse, sportlikkuse, taktikalise mõtlemise ja nutikuse. See on taktikaline sõjamäng, kus sa juhid vägesid, saadad neid sõtta ning kaitsed oma kodu. Aga selleks, et alad oleksid alluvad ja et sul jätkuks sõjamehi, tuleb neid pidevalt külastada.

### Serveri käivitamise juhised
Server on võimeline kasutama nii Postgres andmebaasi kui ka mälus olevat andmebaasi. 

#### Vajalikud asjad
* Maven. Hea seadistamise juhis siin http://www.mkyong.com/maven/how-to-install-maven-in-windows/. Linuxi peal järgida seda http://iambusychangingtheworld.blogspot.com/2014/04/install-and-configure-java-and-maven-in.html

#### Soovitatud asjad
* Eclipse (soovitavalt Spring Tool Suite)
* Eclipse m2e plugin (ilma selleta arvab Eclipse, et projekt on katki). Eclipse'ile lisamiseks "Install new software", lehekülg http://download.eclipse.org/technology/m2e/releases
* Postgres andmebaas

#### Projekti seadistamine
1. Tõmba alla giti repo
2. Mine käsurealt webapp kausta
3.  Mälus oleva andmebaasi kasutamiseks peab minema faili src/main/resources/application.properties ja muutma rida `spring.profiles.active=default`. `default` asemele kirjuta `test`. Kohaliku andmebaasi kasutamiseks kirjuta `postgres`. 
4. webapp kaustas käsk `mvn spring-boot:run`
5. Nüüd peaks server olema ligipääsetav aadressil `localhost:8080`. Andmete nägemiseks võid proovida `localhost:8080/highscore`
6. Käsk `mvn eclipse:eclipse` genereerib Eclipse jaoks vajalikud failid
7. Nüüd saab teha `import existing projects`.

Serveri käivitamiseks on vaja ainult käsku `mvn spring-boot:run`


#### Andmebaasi setup
`test` ehk mälus olev andmebaas kasutab andmeid, mis on kirjas `import.sql` failis. Postgres pead ise tõmbama.  Nende paroolid ja muu info peab ühtima  `application-postgres.properties` kirjeldatuga. Andmebaasi tabelid tekitab server ise. 

Päris andmebaasi näidisandmeid hetkel ei impordita. TÄHTIS: import.sql andmed ei suurenda päris andmebaasi sequence'eid. Seega, kui sisestad testandmed ja siis tahad sinna otsa veel serveri kaudu andmeid lisada, siis kõik läheb katki. Lahendus on käsitsi suurendada sequence'ide algusarvu. Kui selliseks jamaks huvi puudub, siis kasuta mälus olevat andmebaasi, mis toimib kohe vaikimis seadistamata. 

Herokus töötab andmebaas `DATABASE_URL` kaudu, nii et soovi kõrval võib Heroku stiilis Postgres ühendamise stringi süsteemi keskonnamuutujatesse panna. Sellisel juhul ei pea `application.properties` failis midagi muutma. 

Selline string on näiteks `postgres://root:root@localhost:3306/test`.

#### Testide jooksutamine
Maveni abil testide jooksutamiseks on käsk `mvn test`.

Kui projekt on seadistatud ja andmebaas töötab, siis saab jooksutada teste, minnes eclips-is java/tests kausta peale vajutades paremat klõpsu ja valides rippmenüüst `Run As > JUnit test`. Pärast seda jooksutatakse kõik testid mis on olemas. Kõik testid jooksutatakse ka nagunii iga `commiti` järel, ja testide õnnestumine on näidatud selle README faili alguses oleva rohelise/punase tulukese poolt, tänu pideva integratsiooni tööriistale Travis.

### Androidi seadistus
#### Vajalikud asjad
* Eclipse, Eclipse ADT, Android SDK: http://developer.android.com/sdk/index.html

Lisaks läheb vaja Android SDK Managerist:
* Android 4.4.2 (API 19)
* Google Play services

#### Soovitatud asjad
* Nutitelefon Androidiga 4.0 või kõrgem.

#### Projekti seadistamine
1. Lae alla Georivals giti repositoorium.
2. Lisa mobileapp kausta project.properties fail, kirjuta sellesse "target=android-21" ja salvesta see.
3. Impordi mobileapp alamkausta projektina toimetades Eclipse'i keskkonnas järgnevalt: Git Repositories > Add an existing local Git Repository to this view > Directory: ...\Georivals > Finish > Parem klõps mobileapp kaustal > Import projects... > Import existing project > Next > Finish.
4. Impordi Google Play services library projektina. (http://developer.android.com/google/play-services/setup.html, http://developer.android.com/tools/projects/projects-eclipse.html#SettingUpLibraryProject)
5. Lisa Google Play services Georivals projekti Android library'ks. (http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject)
6. Kui Sa oled siiani kõik õigesti teinud ja Eclipse viskab ikka punaseid veateateid, tee Eclipse'ile restart.
7. Kui soovid oma telefoni kasutada:
	1. Ühenda telefon arvutiga.
	2. Lülita USB debugging telefonis sisse. (http://www.phonearena.com/news/How-to-enable-USB-debugging-on-Android_id53909)
8. Kui soovid emulaatorit kasutada, siis on soovitav kasutada Genymotioni emulaatorit, mis on stabiilsem (vt. allpool "Genymotioni emulaatori installimine...". 
Võimalik on ka kasutada ADT emulaatorit, mis pole nii stabiilne:
	1. Vali Android Virtual Device Manager.
	2. Loo uus seade, valides Target'iks 'Google APIs - API Level 19', RAM'iks 256 ja märgi "Use Host GPU". Muu võid vabal valikul valida.
9. Vajuta Run > Run as Android application.
10. Vali omale sobiv seade (oma telefon või emulaator) ja vajuta OK.
11. Kui oled kõiki samme õigesti järginud, peaks programm nüüd käivituma.
12. Kui soovida androidi emulaatoris kasutada kohalikku serverit, määrata ee.bmagrupp.georivals.mobile.core.communications.Constants klassis muutuja isLocalMobile = true; See töötab ainult google'i emulaatoriga, mitte genymotion'iga.

#### Testide jooksutamine
Testide jooksutamiseks on vaja kasutada Genymotioni emulaatorit või füüsilist seadet. Android Development Tools'iga kaasasolevat emulaatorit ei saa selleks kasutada, sest selle emulaatori Google Play Service'is on bug, mis seda takistab.

Siinkohal on soovitatud kasutada füüsilist seadet, sest Genymotioni emulaatori installimine ja seadistamine võib võtta kusagil ~30 minutit vähemalt. Samuti kasutab Genymotion emulaatorina VirtualBoxi virtuaalmasinat, seega kui Te kompileerite koodi ise juba VirtualBoxi virtuaalmasinas, ei saa Te Genymotioni emulaatorit kasutada.

##### Füüsilise seadmega testide jooksutamine
1. Ühenda telefon arvutiga.
2. Lülita USB debugging telefonis sisse, kui see pole juba sisse lülitatud. (http://www.phonearena.com/news/How-to-enable-USB-debugging-on-Android_id53909)
3. Ava mobileapp-test alamprojektis asuva MainTest faili ja vajuta Run > Run as.. > Android JUnit Test

##### Genymotioni emulaatori installimine, seadistamine ja selles testide jooksutamine
1. Tõmmake ja installige sobiv Genymotioni versioon siit lehelt: https://cloud.genymotion.com/page/launchpad/download/ <br>
Kui Teil pole VirtualBox juba installitud, on soovitatud valida see versioon, mis on koos VirtualBoxiga.
2. Tõmmake ja installige sobiva kompilaatori plugin eelnevalt antud lehelt.
3. Käivitage Genymotioni programm ja looge uus seade. Seadme API versioon peab olema vähemalt 14.
4. Jälgige vastava StackOverflow teema vastuse juhiseid, et installida loodud emulaatorisse Google Play Services: http://stackoverflow.com/questions/17831990/how-do-you-install-google-frameworks-play-accounts-etc-on-a-genymotion-virt <br>
Kui vastavad juhised jäävad kusagil arusaamatuks, võite lugeda ka järgneva lehekülje juhiseid: http://www.techrepublic.com/article/pro-tip-install-google-play-services-on-android-emulator-genymotion/ <br>
NB! Juhise seitsmendast sammust alates on normaalne, et Google Play Services emulaatoris kokku jookseb, kui Te pole veel jõudnud sellele uuenduse teha Google Play-s.
5. Kui emulaator ja Google Play Services on installitud ja seadistatud, siis käivita see emulaator. (Eclipse'is saab seadmete nimekirja avada Ctrl+6 vajutades.)
6. Testide jooksutamiseks ava mobileapp-test alamprojektis asuva MainTest faili ja vajuta Run > Run as.. > Android JUnit Test
