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
