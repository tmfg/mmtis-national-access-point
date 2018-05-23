-- Rename Czech to easier typing form

UPDATE country
   SET namefin = 'Tsekki',
       nameeng = 'Czech Republic'
 WHERE code = 'CZ';
