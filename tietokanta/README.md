# Sisältää NAPOTE-hankkeen tietokantamääritykset

Tietokannan perustamisen ja päivittämisen määritykset ovat Flyway työkalulla
tehtyjä skriptejä.


# Kehitysympäristö

Dockerfile sisältää määritykset paikallisen kehitystietokannan perustamiselle.

# Docker testitietokannan päivitys viimeisimpään migraatioon

Kun docker image halutaan päivittää uusimpaan migraatioon (nopeamman testi/kehitys
buildin vuoksi), aja komennot:

> docker build -t solita/napotedb:latest .
> docker push solita/napotedb:latest


Muiden täytyy sitten ottaa uusin image:

> docker pull solita/napotedb:latest
