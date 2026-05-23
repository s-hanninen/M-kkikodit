Mökkikodit-varausjärjestelmä:


HUOM!!!

Ohjelmaa ei ole tarkoitus käynnistää 
koodieditorin 'Run' -painiketta painamalla, vaan ohjelma 
pitää käynnistää komennolla "mvn javafx:run" (ilman lainausmerkkejä) 
suoraan terminaalista.

HUOM!!!


OHJEET KÄYNNISTYKSEEN:


Ohjelma käynnistetään koodieditorin terminaalista komennolla:


mvn javafx:run


Siis kirjoita tämä komento koodieditorin terminaaliin ja paina enter. Ohjelman pitäisi käynnistyä.
Ohjelma on koodattu ja testattu Visual Studio Codella. Ohjelma on testattu myös IntelliJ Idealla, ja käynnistyi molemmilla terminaalista.


JOS OHJELMA EI KÄYNNISTY:

Varmista, että olet sisemmässä "M-kkikodit-main"-kansiossa (jos latasit ohjelman .zip-tiedostona. Sisempi "M-kkikodit-main"-kansio on siis se, jossa on src, lib, target, ja muut kansiot ja tiedostot).

OHJELMA EI KÄYNNISTY, JOS OLET ULOMMASSA "M-kkikodit-main"-KANSIOSSA.

Ohjelman voi käynnistää myös suoraan komentokehotteesta. Tällöin navigoidaan polkuun jonne .zip-kansio purettiin ja sisempään "M-kkikodit-main"-kansioon. Esim. "C:\Users\stell\Downloads\M-kkikodit-main\M-kkikodit-main". Tämän jälkeen ohjelman voi käynnistää komentokehotteesta "mvn javafx:run"-komennolla.

JOS LATAAT OHJELMAN .ZIP-TIEDOSTONA:

1. Lataa ohjelma .zip-tiedostona
2. Pura .zip-tiedosto
3. Navigoi sisempään "M-kkikodit-main"-tiedostoon
4. Avaa komentokehote klikkaamalla polkua ja kirjoittamalla "cmd", tämän jälkeen paina enter
5. Komentokehotteessa kirjoita "mvn javafx:run" ja paina enter



Järjestelmässä voi lisätä, muokata ja poistaa:

- Asiakkaita
- Mökkejä
- Työntekijöitä
- Varauksia

Lisäksi järjestelmä näyttää laskut ja yhteenvedon varatuista mökeistä sekä mökeistä saatavista tuloista. Järjestelmässä on mahdollista suodattaa varaukset päivämäärän perusteella.

Järjestelmä tekee oletuksen, että mökit varataan ensisijaisesti yöksi.

Täten, ei ole mahdollista varata mökkiä esimerkiksi 1.7.2026.-1.7.2026, vaan lyhyin mahdollinen varausaika on 1.7.2026.-2.7.2026.

Mökin hinnat on merkitty yksittäisissä öissä. Hinnat lasketaan öissä, eli varaus ajalle 
1.7.2026.-7.7.2026. tulee maksamaan 6 x mökin hinta.


Muita huomioita:

- Date Picker näyttää ensimmäisellä rivillä viikon ja vasta sitten 
kuukauden päivän. Viikkoa ei voi valita, vain päiviä voi.

- Ajat on merkitty muodossa YYYY-MM-DD.

- Laskun eräpäiväksi on merkitty päivä 14 päivää varauksen päättymisestä.

- Lasku luodaan automaattisesti varauksen myötä, ja laskuja voi tarkastella 'Laskut'-näkymässä
