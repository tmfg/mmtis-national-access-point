#
# -*- coding: iso-8859-1 -*-
###########################################################################
# 
# File:            coordinates.py
#
# Author:          Olli Lammi
#
# Version:         1.0c
#
# Date:            14.02.2014
#
# License:         MIT License (http://opensource.org/licenses/MIT)
#
#                  Copyright (c) 2012-2014 Olli Lammi (olammi@iki.fi)
#
#                  Permission is hereby granted, free of charge, to any 
#                  person obtaining a copy of this software and associated 
#                  documentation files (the "Software"), to deal in the 
#                  Software without restriction, including without  
#                  limitation the rights to use, copy, modify, merge,  
#                  publish, distribute, sublicense, and/or sell copies of  
#                  the Software, and to permit persons to whom the Software  
#                  is furnished to do so, subject to the following
#                  conditions: 
#
#                  The above copyright notice and this permission notice  
#                  shall be included in all copies or substantial portions 
#                  of the Software.
#
#                  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF 
#                  ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
#                  TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
#                  PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT 
#                  SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
#                  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
#                  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
#                  IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
#                  DEALINGS IN THE SOFTWARE.
#
# Functions:       Translate
#                  KKJxy_to_WGS84lalo
#                  WGS84lalo_to_KKJxy
#                  KKJxy_to_KKJlalo
#                  KKJlalo_to_KKJxy
#                  KKJlalo_to_WGS84lalo
#                  WGS84lalo_to_KKJlalo
#                  KKJ_Zone_I
#                  KKJ_Zone_Lo
#                  KKJxy_ZoneShift
#                  WGS84lalo_to_GoogleMapsXY
#                  Str_to_CoordinateValue
#                  KKJxy_in_Finland
#                  ETRSTM35FINxy_to_WGS84lalo
#                  WGS84lalo_to_ETRSTM35FINxy
#                  KKJxy_to_ETRSTM35FINxy
#                  ETRSTM35FINxy_to_KKJxy
#                  WGS84distance
#                  WGS84bearing
#                  WGS84travel
#                  WGS84lalo_to_MRGS
#                  MRGS_to_WGS84lalo
#                  WGS84lalo_to_UTM_MGRS
#                   
# Description:     Coordinate system functions. 
#
#                  KKJ and ETRS-TM35FIN algorithms implemented by 
#                  Olli Lammi according to JHS 154.                  
#                  (http://docs.jhs-suositukset.fi/jhs-suositukset/JHS154/JHS154.pdf)
#
#                  Google Maps functions developed based on knowledge on
#                  multiple sites in the Internet, simplifying the 
#                  algorithms and traditional trial error method.
# 
#                  NOTE!: The KKJ and ETRS-TM35FIN coordinate functions  
#                  are developed to work only with coordinates that are in 
#                  the area of Finland.
#
#                  NOTE!: MGRS conversion functions do not support the
#                  polar regions (Antarctic and North pole).
#
# Version history: ** 25.10.2012 v1.0a (Olli Lammi) **
#                  Published the rewritten version under MIT License. 
#
#                  ** 05.12.2012 v1.0b (Olli Lammi) **
#                  Added MGRS conversion functions. Fixed WGS84 bearing
#                  and distance bug to support coincident points. 
#
#                  ** 14.02.2014 v1.0c (Olli Lammi) **
#                  Added WGS84travel function. Fixed a bug in WGS84bearing
#                  function (returned initial bearing was wrong). 
#
###########################################################################

# Imports

import sys, os, string
import math
import re


###########################################################################

# Constants

# Longitude0 and Center meridian of KKJ bands
KKJ_ZONE_INFO = { 0: (18.0,  500000.0), \
                  1: (21.0, 1500000.0), \
                  2: (24.0, 2500000.0), \
                  3: (27.0, 3500000.0), \
                  4: (30.0, 4500000.0), \
                  5: (33.0, 5500000.0), \
                }

# Coordinate system type identifiers
COORD_TYPE_YKJ = 'YKJxy'
COORD_TYPE_KKJ = 'KKJxy'
COORD_TYPE_WGS84 = 'WGS84lalo'
COORD_TYPE_ETRSTM35FIN = 'ETRSTM35FINxy'
COORD_TYPE_MGRS = 'MGRS'

MGRS_CHARS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', \
              'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

# Ellipsoids
ELLIPSOID = {'WGS84': {'a': 6378137.0, 'b': 6356752.314245, 'f': 1.0 / 298.257223563, 'k0': 0.9996}, \
             'KKJ': {'a': 6378388.0, 'b': 6356911.946128, 'f': 1.0 / 297.0, 'k0': 1.0} \
}

# init precalculated ellipsoid parameters
for key in ELLIPSOID.keys():
    a = ELLIPSOID[key]['a']
    f = ELLIPSOID[key]['f']
    
    n = f / (2.0 - f)
    ELLIPSOID[key]['n'] = n
    ELLIPSOID[key]['A1'] = a / (1.0 + n) * (1.0 + math.pow(n, 2.0) / 4.0 + math.pow(n, 4.0) / 64.0)
    ELLIPSOID[key]['e'] = math.sqrt(2.0 * f - math.pow(f, 2.0))
    ELLIPSOID[key]['h1'] = 1.0/2.0 * n - 2.0/3.0 * math.pow(n, 2.0) + 37.0/96.0 * math.pow(n, 3.0) - 1.0/360.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h2'] = 1.0/48.0 * math.pow(n, 2.0) + 1.0/15.0 * math.pow(n, 3.0) - 437.0/1440.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h3'] = 17.0/480.0 * math.pow(n, 3.0) - 37.0/840.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h4'] = 4397.0/161280.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h1p'] = 1.0/2.0 * n - 2.0/3.0 * math.pow(n, 2.0) + 5.0/16.0 * math.pow(n, 3.0) + 41.0/180.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h2p'] = 13.0/48.0 * math.pow(n, 2.0) - 3.0/5.0 * math.pow(n, 3.0) + 557.0/1440.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h3p'] = 61.0/240.0 * math.pow(n, 3.0) - 103.0/140.0 * math.pow(n, 4.0)
    ELLIPSOID[key]['h4p'] = 49561.0/161280.0 * math.pow(n, 4.0)

# Bursa-Wolf transform parameters for latlon coordinate system transfers
# in 3D (JHS153)
BW_TRANSFORM = {'KKJ_WGS84': {'dX': -96.0617, 'dY': -82.4278, 'dZ': -121.7535, 'ex': math.radians(-4.80107 / 3600.0), \
                           'ey': math.radians(-0.34543 / 3600.0), 'ez': math.radians(1.37646 / 3600.0), 'm': 1.49640 / 1000000.0}, \
                'WGS84_KKJ': {'dX': 96.0610, 'dY': 82.4298, 'dZ': 121.7485, 'ex': math.radians(4.80109 / 3600.0), \
                           'ey': math.radians(0.34546 / 3600.0), 'ez': math.radians(-1.37645 / 3600.0), 'm': -1.49651 / 1000000.0} \
}

    
# Functions

###########################################################################
# Function:  Translate
###########################################################################
# Input:     dictionary with ['type'] is coordinate system type identifier
#                            ['N'] is coordinate Northing / Lat
#                            ['E'] is coordinate Easting / Lon
#                            ['STR'] is coordinate string (MGRS)
#            type identifier of the coordinate system to transform the input
#                            coordinates to
# Output:    dictionary with ['type'] is coordinate system type identifier
#                            ['N'] is coordinate Northing / Lat
#                            ['E'] is coordinate Easting / Lon
#                            ['STR'] is coordinate string (MGRS) 
###########################################################################

def Translate(coordIn, outType):
    if (coordIn['type'] == outType):
        return coordIn
    
    # first convert all formats to WGS84
    WGS = {}
    if (coordIn['type'] == COORD_TYPE_KKJ):
        WGS = KKJxy_to_WGS84lalo({'P': coordIn['N'], 'I': coordIn['E']})
    elif (coordIn['type'] == COORD_TYPE_YKJ):
        WGS = KKJxy_to_WGS84lalo({'P': coordIn['N'], 'I': coordIn['E']}, 3)
    elif (coordIn['type'] == COORD_TYPE_ETRSTM35FIN):
        WGS = ETRSTM35FINxy_to_WGS84lalo(coordIn)
    elif (coordIn['type'] == COORD_TYPE_MGRS):
        WGS = MGRS_to_WGS84lalo(coordIn['STR'])     
    elif (coordIn['type'] == COORD_TYPE_WGS84):
        WGS['La'] = coordIn['N'] 
        WGS['Lo'] = coordIn['E']
    else:
        return None
       
    # then convert from WGS84 to wanted coordinate type 
    if (outType == COORD_TYPE_WGS84):
        return {'type': COORD_TYPE_WGS84, 'N': WGS['La'], 'E': WGS['Lo']}
    elif (outType == COORD_TYPE_KKJ or outType == COORD_TYPE_YKJ):
        KKJ = WGS84lalo_to_KKJxy(WGS)
        if (outType == COORD_TYPE_YKJ):
            KKJ = KKJxy_ZoneShift(KKJ, 3)
        return {'type': outType, 'N': KKJ['P'], 'E': KKJ['I']}
    elif (outType == COORD_TYPE_ETRSTM35FIN):
        ETRS = WGS84lalo_to_ETRSTM35FINxy(WGS)
        return {'type': COORD_TYPE_ETRSTM35FIN, 'N': ETRS['N'], 'E': ETRS['E']}
    elif (outType == COORD_TYPE_MGRS):
        MGRSstr = WGS84lalo_to_MGRS(WGS)
        return {'type': COORD_TYPE_MGRS, 'STR': MGRSstr}
    else:
        return None



###########################################################################
# Function:  KKJxy_to_WGS84lalo
###########################################################################
# Input:     dictionary with ['P'] is KKJ Northing
#                            ['I'] in KKJ Easting
#            zone (if given) of the KKJ point
# Output:    dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
###########################################################################

def KKJxy_to_WGS84lalo(KKJin, zone = None):  
  KKJz = KKJxy_to_KKJlalo(KKJin, zone)
  WGS = KKJlalo_to_WGS84lalo(KKJz)

  return WGS



###########################################################################
# Function:  WGS84lalo_to_KKJxy
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
# Output:    dictionary with ['P'] is KKJ Northing
#                            ['I'] in KKJ Eeasting
###########################################################################

def WGS84lalo_to_KKJxy(WGSin):
  KKJlalo = WGS84lalo_to_KKJlalo(WGSin);

  ZoneNumber = KKJ_Zone_Lo(KKJlalo['Lo'])
  KKJxy = KKJlalo_to_KKJxy(KKJlalo, ZoneNumber)

  return KKJxy



###########################################################################
# Function:  KKJlalo_to_WGS84lalo
###########################################################################

def KKJlalo_to_WGS84lalo(KKJ):
  return lalo_to_lalo(KKJ['La'], KKJ['Lo'], BW_TRANSFORM['KKJ_WGS84'], ELLIPSOID['KKJ'], ELLIPSOID['WGS84'])
 

###########################################################################
# Function:  WGS84lalo_to_KKJlalo
###########################################################################

def WGS84lalo_to_KKJlalo(WGS):
  return lalo_to_lalo(WGS['La'], WGS['Lo'], BW_TRANSFORM['WGS84_KKJ'], ELLIPSOID['WGS84'], ELLIPSOID['KKJ'])


###########################################################################
# Function:  KKJxy_to_KKJlalo
###########################################################################

def KKJxy_to_KKJlalo(KKJin, zone = None):  
  ZoneNumber = zone
  if ZoneNumber == None:
      ZoneNumber = KKJ_Zone_I(KKJin['I'])
  (lo0, E0) = KKJ_ZONE_INFO[ZoneNumber]
  
  return xy_to_lalo(KKJin['I'], KKJin['P'], lo0, E0, ELLIPSOID['KKJ'])


###########################################################################
# Function:  KKJlalo_to_KKJxy
###########################################################################

def KKJlalo_to_KKJxy(KKJin, ZoneNumber):
  (lo0, E0) = KKJ_ZONE_INFO[ZoneNumber]
  
  XY = lalo_to_xy(KKJin['La'], KKJin['Lo'], lo0, E0, ELLIPSOID['KKJ'])

  return {'P': XY['N'], 'I': XY['E']}    


###########################################################################
# Function:  KKJ_Zone_I
###########################################################################

def KKJ_Zone_I(KKJI):
  ZoneNumber = math.floor((KKJI/1000000.0))
  if ZoneNumber < 0 or ZoneNumber > 5:
      ZoneNumber = -1
      
  return ZoneNumber



###########################################################################
# Function:  KKJ_Zone_Lo
###########################################################################

def KKJ_Zone_Lo(KKJlo):
  # determine the zonenumber from KKJ easting
  # takes KKJ zone which has center meridian
  # longitude nearest (in math value) to
  # the given KKJ longitude
  ZoneNumber = 5
  while ZoneNumber >= 0:
    if math.fabs(KKJlo - KKJ_ZONE_INFO[ZoneNumber][0]) <= 1.5:
      break
    ZoneNumber = ZoneNumber - 1
            
  return ZoneNumber


###########################################################################
# Function:  KKJxy_ZoneShift
###########################################################################

def KKJxy_ZoneShift(KKJ, zone):
  kkjlalo = KKJxy_to_KKJlalo(KKJ)
  return KKJlalo_to_KKJxy(kkjlalo, zone)  


###########################################################################
# Function:  ETRSTM35FINxy_to_WGS84lalo
###########################################################################
# Input:     dictionary with ['N'] is ETRS-TM35FIN Northing
#                            ['E'] in ETRS-TM35FIN Easting
# Output:    dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
###########################################################################

def ETRSTM35FINxy_to_WGS84lalo(ETRSin):  
  lo0 = 27.0
  E0 = 500000.0
  
  return xy_to_lalo(ETRSin['E'], ETRSin['N'], lo0, E0, ELLIPSOID['WGS84'])


###########################################################################
# Function:  WGS84lalo_to_ETRSTM35FINxy
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
# Output:    dictionary with ['N'] is ETRS-TM35FIN Northing
#                            ['E'] in ETRS-TM35FIN Eeasting
###########################################################################

def WGS84lalo_to_ETRSTM35FINxy(WGSin):
  lo0 = 27.0
  E0 = 500000.0
  
  return lalo_to_xy(WGSin['La'], WGSin['Lo'], lo0, E0, ELLIPSOID['WGS84'])


###########################################################################
# Function:  KKJxy_to_ETRSTM35FINxy
###########################################################################
# Input:     dictionary with ['P'] is KKJ Northing
#                            ['I'] in KKJ Easting
# Output:    dictionary with ['N'] is ETRS-TM35FIN Northing
#                            ['E'] is ETRS-TM35FIN Easting
###########################################################################

def KKJxy_to_ETRSTM35FINxy(KKJin):
  return WGS84lalo_to_ETRSTM35FINxy(KKJxy_to_WGS84lalo(KKJin))  



###########################################################################
# Function:  ETRSTM35FINxy_to_KKJxy
###########################################################################
# Input:     dictionary with ['N'] is ETRS-TM35FIN Northing
#                            ['E'] is ETRS-TM35FIN Easting
# Output:    dictionary with ['P'] is KKJ Northing
#                            ['I'] in KKJ Easting
###########################################################################

def ETRSTM35FINxy_to_KKJxy(ETRSin):
  return WGS84lalo_to_KKJxy(ETRSTM35FINxy_to_WGS84lalo(ETRSin))  


###########################################################################
# Function:  WGS84lalo_to_GoogleMapsXY
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
#            google zoom factor (integer between 0 and 17, 0 for no zoom 
#                            and 17 for maximum zoom)
# Output:    dictionary with ['x'] is Google maps URL x parameter (tile number)
#                            ['y'] in Google maps URL y parameter (tile number)
###########################################################################

def WGS84lalo_to_GoogleMapsXY(WGSin, zoom):
  # Google maps maximum zoom factor (min = 0)
  MAXZOOM = 17.0

  worldwidth = math.pow(2.0, (MAXZOOM - zoom))
  x = WGSin['Lo'] + 180.0
  y = math.log(math.tan((math.pi / 4.0) + ((0.5 * math.pi * WGSin['La']) / 180.0))) / math.pi
  if y <-0.9999:
    y = -0.9999
  if y > 0.9999:
    y = 0.9999 
  y = (-90.0 * y + 90.0)
  
  out = {}
  out['x'] = (int) (math.floor((x / 360.0) * worldwidth))
  out['y'] = (int) (math.floor((y / 180.0) * worldwidth))

  return out



###########################################################################
# Function:  Str_to_CoordinateValue
###########################################################################
# Input:     string with a coordinate value in some WGS84-format
# 
# Output:    floating point value representing the given coordinate
#            Value returned is INVALID_COORDINATE if the given string cannot be
#            interpreted as WGS84 coordinate value. 
###########################################################################
INVALID_COORDINATE = -99999.99
def Str_to_CoordinateValue(WGSstr):
  
  # case 1: 61,27,4.96 (for 61 degrees, 27 minutes, 4.96 seconds)
  regexp1 = '^(?P<sig>-?)(?P<deg>\d+),(?P<min>\d+),(?P<sec>\d+(\.\d+)?)$'
  mo = re.match(regexp1, WGSstr)
  if (mo != None):
    value = string.atof( mo.group('deg') ) + string.atof( mo.group('min') ) / 60.0 + string.atof( mo.group('sec') ) / 3600.0
    if ( mo.group('sig') == '-' ):
      value = -value
    return value

  # case 2: 61,27.083 (for 61 degrees, 27.083 minutes)
  regexp2 = '^(?P<sig>-?)(?P<deg>\d+),(?P<min>\d+(\.\d+))?$'
  mo = re.match(regexp2, WGSstr)
  if (mo != None):
    value = string.atof( mo.group('deg') ) + string.atof( mo.group('min') ) / 60.0
    if ( mo.group('sig') == '-' ):
      value = -value
    return value

  # case 3: 61.451378 (for 61.451378 degrees)
  regexp3 = '^(?P<sig>-?)(?P<deg>\d+\.\d+)$'
  mo = re.match(regexp3, WGSstr)
  if (mo != None):
    value = string.atof( mo.group('deg') )
    if ( mo.group('sig') == '-' ):
        value = -value
    return value

  # case: other
  return INVALID_COORDINATE 



###########################################################################
# Function:  KKJxy_in_Finland
###########################################################################
# Input:     dictionary with ['P'] is KKJ Northing
#                            ['I'] in KKJ Eeasting
# Output:    truth value telling whether the given coordinate is 
#            _*_*_approximately_*_*_ in Finnish area. 
###########################################################################

FINLAND_AREA_ETRSTM35FIN_POLYGON = [ (242479, 6595855), (259792, 6602680), (434991, 6632824), \
    (523749, 6657805), (541775, 6672273), (580248, 6728861), (622590, 6775920), \
    (665040, 6831495), (706652, 6896976), (743454, 6978649), (721844, 7017760), \
    (663173, 7063864), (680055, 7099581), (677068, 7128412), (654291, 7188558), \
    (645272, 7232643), (642118, 7268281), (653915, 7274158), (641353, 7335926), \
    (601517, 7422752), (639760, 7506298), (604838, 7552257), (578703, 7567556), \
    (570304, 7591582), (581500, 7631426), (584914, 7652181), (600198, 7703337), \
    (589583, 7735212), (537873, 7781961), (477340, 7761288), (449677, 7724778), \
    (423642, 7637286), (414079, 7620132), (374155, 7644750), (348570, 7626281), \
    (325191, 7636098), (297949, 7691380), (273193, 7707338), (235223, 7676418), \
    (243467, 7641254), (290799, 7589169), (328792, 7554878), (337784, 7488078), \
    (342425, 7456602), (345515, 7407759), (341835, 7342122), (357809, 7236883), \
    (344275, 7178462), (307218, 7158541), (296364, 7128237), (269162, 7111042), \
    (244043, 7072179), (181370, 7050233), (149398, 7014137), (171995, 6848989), \
    (161054, 6788321), (70168, 6740277), (55310, 6692319), (95665, 6619487), \
    (118640, 6599765) ]

def PointInPolygon(point, polygon):
    (x, y) = point
    pollen = len(polygon)
    c = False
    i = 0
    j = pollen - 1
    while (i < pollen):
        ypj = polygon[j][1]
        ypi = polygon[i][1]
        xpj = polygon[j][0]
        xpi = polygon[i][0]
        if ( (ypi <= y and y < ypj) or (ypj <= y and y < ypi) ) and \
           (x < (xpj - xpi) * (y - ypi) / (ypj - ypi) + xpi):
            c = not c
                
        j = i
        i = i + 1
        
    return c

def KKJxy_in_Finland(KKJ):
  try:
    # Move the coordinates to ETRS-TM35FIN
    xy = KKJxy_to_ETRSTM35FINxy(KKJ)
    point = (xy['E'], xy['N'])
    if PointInPolygon(point, FINLAND_AREA_ETRSTM35FIN_POLYGON):
      return 1
  except:
    # If KKJ-conversion functions fail, assume it is not valid Finnish location
    return 0
     
  return 0


# inverse hyperbolic functions not found in Python math library 
# before Python version 2.6

def asinh(x):
  return math.log(x + math.sqrt(x * x + 1.0))

def atanh(x):
  return math.log((1.0 + x) / (1.0 - x)) / 2.0


###########################################################################
# Function:  xy_to_lalo
###########################################################################

def xy_to_lalo(x_E, y_N, lo0, E0, ellipsoid):  
  lo0 = math.radians(lo0)

  A1 = ellipsoid['A1']
  k0 = ellipsoid['k0']
  e = ellipsoid['e']
  h1 = ellipsoid['h1']
  h2 = ellipsoid['h2']
  h3 = ellipsoid['h3']
  h4 = ellipsoid['h4']
  
  E = y_N / (A1 * k0)
  nn = (x_E - E0) / (A1 * k0)
  
  E1p = h1 * math.sin(2.0 * E) * math.cosh(2.0 * nn)
  E2p = h2 * math.sin(4.0 * E) * math.cosh(4.0 * nn)
  E3p = h3 * math.sin(6.0 * E) * math.cosh(6.0 * nn)
  E4p = h4 * math.sin(8.0 * E) * math.cosh(8.0 * nn)
  nn1p = h1 * math.cos(2.0 * E) * math.sinh(2.0 * nn)
  nn2p = h2 * math.cos(4.0 * E) * math.sinh(4.0 * nn)
  nn3p = h3 * math.cos(6.0 * E) * math.sinh(6.0 * nn)
  nn4p = h4 * math.cos(8.0 * E) * math.sinh(8.0 * nn)
  Ep = E - E1p - E2p - E3p - E4p
  nnp = nn - nn1p - nn2p - nn3p - nn4p
  be = math.asin(math.sin(Ep) / math.cosh(nnp))
  
  Q = asinh(math.tan(be))
  Qp = Q + e * atanh(e * math.tanh(Q))
  Qp = Q + e * atanh(e * math.tanh(Qp))
  Qp = Q + e * atanh(e * math.tanh(Qp))
  Qp = Q + e * atanh(e * math.tanh(Qp))
  
  LALO = {}
  LALO['La'] = math.degrees(math.atan(math.sinh(Qp))) 
  LALO['Lo'] = math.degrees(lo0 + math.asin(math.tanh(nnp) / math.cos(be)))
  
  return LALO


###########################################################################
# Function:  xy_to_lalo
###########################################################################

def lalo_to_xy(la, lo, lo0, E0, ellipsoid):
  lo0 = math.radians(lo0)  
  la = math.radians(la)
  lo = math.radians(lo)  
  
  e = ellipsoid['e']
  k0 = ellipsoid['k0']
  h1p = ellipsoid['h1p']
  h2p = ellipsoid['h2p']
  h3p = ellipsoid['h3p']
  h4p = ellipsoid['h4p']
  A1 = ellipsoid['A1']  
    
  Q = asinh(math.tan(la)) - e * atanh(e * math.sin(la))
  be = math.atan(math.sinh(Q))
  nnp = atanh(math.cos(be) * math.sin(lo - lo0))
  Ep = math.asin(math.sin(be) * math.cosh(nnp))  
  E1 = h1p * math.sin(2.0 * Ep) * math.cosh(2.0 * nnp)
  E2 = h2p * math.sin(4.0 * Ep) * math.cosh(4.0 * nnp)
  E3 = h3p * math.sin(6.0 * Ep) * math.cosh(6.0 * nnp)
  E4 = h4p * math.sin(8.0 * Ep) * math.cosh(8.0 * nnp)
  nn1 = h1p * math.cos(2.0 * Ep) * math.sinh(2.0 * nnp)
  nn2 = h2p * math.cos(4.0 * Ep) * math.sinh(4.0 * nnp)
  nn3 = h3p * math.cos(6.0 * Ep) * math.sinh(6.0 * nnp)
  nn4 = h4p * math.cos(8.0 * Ep) * math.sinh(8.0 * nnp)
  E = Ep + E1 + E2 + E3 + E4
  nn = nnp + nn1 + nn2 + nn3 + nn4
  
  XY = {}
  XY['N'] = A1 * E * k0
  XY['E'] = A1 * nn * k0 + E0

  return XY


###########################################################################
# Function:  xy_to_lalo
###########################################################################

def lalo_to_lalo(la, lo, BW_transform, from_ellipsoid, to_ellipsoid):
  la = math.radians(la)
  lo = math.radians(lo)
   
  a_1 = from_ellipsoid['a']
  e_1 = from_ellipsoid['e']
  a_2 = to_ellipsoid['a']
  e_2 = to_ellipsoid['e']
  
  N = a_1 / math.sqrt(1.0 - math.pow(e_1 * math.sin(la), 2.0))
 
  X = N * math.cos(la) * math.cos(lo)
  Y = N * math.cos(la) * math.sin(lo)
  Z = N * (1.0 - math.pow(e_1, 2.0)) * math.sin(la)
  
  dx = BW_transform['dX']
  dy = BW_transform['dY']
  dz = BW_transform['dZ']
  ex = BW_transform['ex']
  ey = BW_transform['ey']
  ez = BW_transform['ez']
  m = BW_transform['m']
  
  X2 = (1.0 + m) * (X + ez * Y - ey * Z) + dx
  Y2 = (1.0 + m) * (Y - ez * X + ex * Z) + dy
  Z2 = (1.0 + m) * (ey * X - ex * Y + Z) + dz
  X2Y2 = math.sqrt(math.pow(X2, 2.0) + math.pow(Y2, 2.0))

  e2 = math.pow(e_2, 2.0)
  la0 = math.atan(Z2 / ((1.0 - e2) * X2Y2))
  la = la0

  dla = 1.0
  nn = 0
  while dla > 1.0E-12 and nn < 100:          
      N = a_2 / math.sqrt(1.0 - e2 * math.pow(math.sin(la), 2.0))
      if abs(la0) < (math.pi / 4.0):
          h = X2Y2 / math.cos(la) - N
      else:
          h = Z2 / math.sin(la) - N * (1.0 - e2)
      nla = math.atan(Z2 / (X2Y2 * (1.0 - (N * e2) / (N + h))))
      dla = abs(nla - la)
      la = nla
      nn = nn + 1
  
  lo = math.atan(Y2 / X2)
  
  LALO2 = {}
  LALO2['La'] = math.degrees(la)
  LALO2['Lo'] = math.degrees(lo)

  return LALO2





###########################################################################
# Function:  WGS84distance
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
#            dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
# Output:    distance between given points in meters
###########################################################################

def WGS84distance(WGSin1, WGSin2):
  Ca = ELLIPSOID['WGS84']['a']
  Cb = ELLIPSOID['WGS84']['b']
  Cf = ELLIPSOID['WGS84']['f']
  
  dLon = math.radians(WGSin2['Lo'] - WGSin1['Lo'])
    
  U1 = math.atan((1.0 - Cf) * math.tan(math.radians(WGSin1['La'])))
  U2 = math.atan((1.0 - Cf) * math.tan(math.radians(WGSin2['La'])))
  sinU1 = math.sin(U1) 
  cosU1 = math.cos(U1)
  sinU2 = math.sin(U2)
  cosU2 = math.cos(U2)
  
  lam = dLon
  isFirst = 1;
  lamPrev = 0.0
  limit = 100
  
  cosAlpha2 = 0.0
  sinSig = 0.0
  cosSig = 0.0
  cos2sigM = 0.0

  while ((isFirst or abs(lam - lamPrev) > 1e-12) and limit > 0):
    isFirst = 0;
    lamPrev = lam;
    sinLam = math.sin(lam)
    cosLam = math.cos(lam)
    
    sinSig = math.sqrt(math.pow(cosU2 * sinLam, 2.0) + math.pow(cosU1 * sinU2 - sinU1 * cosU2 * cosLam, 2.0))
    if sinSig == 0.0:
      return 0.0  # co-incident points
    cosSig = sinU1 * sinU2 + cosU1 * cosU2 * cosLam
    sig = math.atan2(sinSig, cosSig)
    sinAlpha = cosU1 * cosU2 * sinLam / sinSig
    cosAlpha2 = 1.0 - math.pow(sinAlpha, 2.0)

    if (cosAlpha2 == 0.0):
      cos2sigM = 0.0
    else:
      cos2sigM = cosSig - 2.0 * sinU1 * sinU2 / cosAlpha2

    C = Cf / 16.0 * cosAlpha2 * (4.0 + Cf * (4.0 - 3.0 * cosAlpha2))
    
    lam = dLon + (1.0 - C) * Cf * sinAlpha * (sig + C * sinSig * (cos2sigM + C * cosSig * (-1.0 + 2.0 * math.pow(cos2sigM, 2.0))))
    limit = limit - 1

  if limit == 0:
    return None 

  Cb2 = math.pow(Cb, 2.0)
  u2 = cosAlpha2 * (math.pow(Ca, 2.0) - Cb2) / Cb2
  A = 1.0 + u2 / 16384.0 * (4096.0 + u2 * (-768.0 + u2 * (320.0 - 175.0 * u2)))
  B = u2 / 1024.0 * (256.0 + u2 * (-128.0 + u2 * (74.0 - 47.0 * u2)))
  dsig = B * sinSig * (cos2sigM + 0.25 * B * (cosSig * (-1.0 + 2.0 * math.pow(cos2sigM, 2.0)) - \
         1.0 / 6.0 * B * cos2sigM * (-3.0 + 4.0 * math.pow(sinSig, 2.0)) * (-3.0 + 4.0 * math.pow(cos2sigM, 2.0))))
  
  return Cb * A * (sig - dsig)



###########################################################################
# Function:  WGS84bearing
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
#            dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
# Output:    tuple containing initial and final bearing (deg) from point 1 to 2 
###########################################################################

def WGS84bearing(WGSin1, WGSin2):
  Cf = ELLIPSOID['WGS84']['f']

  dLon = math.radians(WGSin2['Lo'] - WGSin1['Lo'])
    
  U1 = math.atan((1.0 - Cf) * math.tan(math.radians(WGSin1['La'])))
  U2 = math.atan((1.0 - Cf) * math.tan(math.radians(WGSin2['La'])))
  sinU1 = math.sin(U1) 
  cosU1 = math.cos(U1)
  sinU2 = math.sin(U2)
  cosU2 = math.cos(U2)
  
  lam = dLon
  isFirst = 1
  lamPrev = None
  limit = 100

  sinLam = 0.0
  cosLam = 0.0

  while ((isFirst or abs(lam - lamPrev) > 1e-12) and limit > 0):
    isFirst = 0
    lamPrev = lam
    sinLam = math.sin(lam)
    cosLam = math.cos(lam)
    
    sinSig = math.sqrt(math.pow(cosU2 * sinLam, 2.0) + math.pow(cosU1 * sinU2 - sinU1 * cosU2 * cosLam, 2.0))
    if sinSig == 0.0:
      return 0.0  # co-incident points
    cosSig = sinU1 * sinU2 + cosU1 * cosU2 * cosLam
    sig = math.atan2(sinSig, cosSig)
    sinAlpha = cosU1 * cosU2 * sinLam / sinSig
    cosAlpha2 = 1.0 - math.pow(sinAlpha, 2.0)

    if (cosAlpha2 == 0.0):
      cos2sigM = 0.0
    else:
      cos2sigM = cosSig - 2.0 * sinU1 * sinU2 / cosAlpha2

    C = Cf / 16.0 * cosAlpha2 * (4.0 + Cf * (4.0 - 3.0 * cosAlpha2))
    
    lam = dLon + (1.0 - C) * Cf * sinAlpha * (sig + C * sinSig * (cos2sigM + C * cosSig * (-1.0 + 2.0 * math.pow(cos2sigM, 2.0))))
    limit = limit - 1

  if limit == 0:
    return None 
  
  al1 = math.degrees(math.atan2(cosU2 * sinLam, cosU1 * sinU2 - sinU1 * cosU2 * cosLam))
  al2 = math.degrees(math.atan2(cosU1 * sinLam, -sinU1 * cosU2 + cosU1 * sinU2 * cosLam))

  return (al1, al2)

        
###########################################################################
# Function:  WGS84distance
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
#            bearing from given point
#            distance from given point
# Output:    dictionary with ['La'] and ['Lo'] as WGS84 coordinates of
#            the final point
###########################################################################

def WGS84travel(WGSin, bearing, distance):
  Ca = ELLIPSOID['WGS84']['a']
  Cb = ELLIPSOID['WGS84']['b']
  Cf = ELLIPSOID['WGS84']['f']

  s = distance
  alpha1 = math.radians(bearing)
  sinAlpha1 = math.sin(alpha1)
  cosAlpha1 = math.cos(alpha1)

  tanU1 = (1.0 - Cf) * math.tan(math.radians(WGSin['La']))
  cosU1 = 1.0 / math.sqrt(1.0 + tanU1 * tanU1)
  sinU1 = tanU1 * cosU1
  sigma1 = math.atan2(tanU1, cosAlpha1)
  sinAlpha = cosU1 * sinAlpha1
  cosSqAlpha = 1.0 - sinAlpha * sinAlpha
  uSq = cosSqAlpha * (Ca * Ca - Cb * Cb) / (Cb * Cb)
  A = 1.0 + uSq / 16384.0 * (4096.0 + uSq * (-768.0 + uSq * (320.0 - 175.0 * uSq)))
  B = uSq / 1024.0 * (256.0 + uSq * (-128.0 + uSq * (74.0 - 47.0 * uSq)))

  sigma = s / (Cb * A)
  sigmaPrev = 2.0 * math.pi
  while abs(sigma - sigmaPrev) > 1e-12:
    cos2SigmaM = math.cos(2.0 * sigma1 + sigma)
    sinSigma = math.sin(sigma)
    cosSigma = math.cos(sigma)
    deltaSigma = B * sinSigma * (cos2SigmaM + B / 4.0 * (cosSigma * (-1.0 + 2.0 * cos2SigmaM * cos2SigmaM) - B / 6.0 * cos2SigmaM * (-3.0 + 4.0 * sinSigma * sinSigma) * (-3.0 + 4.0 * cos2SigmaM * cos2SigmaM)))
    sigmaPrev = sigma
    sigma = s / (Cb * A) + deltaSigma
 
  sinSigma = math.sin(sigma)
  cosSigma = math.cos(sigma)
  cos2SigmaM = math.cos(2.0 * sigma1 + sigma)
  tmp1 = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1
  lat2 = math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1, (1.0 - Cf) * math.sqrt(sinAlpha * sinAlpha + tmp1 * tmp1))
  lam = math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1)
  C = Cf / 16.0 * cosSqAlpha * (4.0 + Cf * (4.0 - 3.0 * cosSqAlpha))
  L = lam - (1.0 - C) * Cf * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1.0 + 2.0 * cos2SigmaM * cos2SigmaM)))

  lat2 = math.degrees(lat2)
  lon2 = WGSin['Lo'] + math.degrees(L)
  lon2 = ((lon2 + 3 * 180.0) % 360.0) - 180.0

  return {'La': lat2, 'Lo': lon2}



###########################################################################
# Function:  WGS84lalo_to_MGRS
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
#            output precision in meters (1, 10, 100, 1000, ..., 100000)
#                            default is 1
# Output:    string containing MGRS coordinates 
###########################################################################

def WGS84lalo_to_MGRS(WGS, precision = 1):
  if WGS['La'] < -80.0: 
      return 'Antarctic not supported'
  if WGS['La'] >= 84.0:
      return 'North pole not supported'

  precisions = {1: '%05d', 10: '%04d', 100: '%03d', 1000: '%02d', 10000:'%01d', 100000: ''}
  if not precision in precisions.keys():
      return 'Unknown MGRS precision'

  xycoords = WGS84lalo_to_UTM_MGRS(WGS)

  mgrsx = int(math.floor(xycoords['E'] % 100000))
  mgrsy = int(math.floor(xycoords['N'] % 100000))
  mgrsx = mgrsx / precision;
  mgrsy = mgrsy / precision;

  formatstr = "%s%s%s"
  if precision < 100000:
      formatstr = formatstr + ' ' + precisions[precision]
      formatstr = formatstr + ' ' + precisions[precision]
      result = formatstr % (xycoords['zone'], xycoords['band'], xycoords['grid'], mgrsx, mgrsy)
  else:
      result = formatstr % (xycoords['zone'], xycoords['band'], xycoords['grid'])

  return result



###########################################################################
# Function:  MGRS_to_WGS84lalo
###########################################################################
# Input:     string containing MGRS coordinates
# Output:    lower left coordinate of the given MSRS grid position in
#            dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84) 
###########################################################################

def MGRS_to_WGS84lalo(MGRS):
    #eliminate white space
    MGRS = string.upper(string.strip(string.replace(MGRS, ' ', '')))

    # NNXXXN(0:10)
    mgrscs = string.join(MGRS_CHARS, '')
    regexp1 = '^(?P<zone>\d+)(?P<band>[' + mgrscs + '])(?P<gridcol>[' + mgrscs + '])(?P<gridrow>[' + mgrscs + '])(?P<coords>\d*)$'
    mo = re.match(regexp1, MGRS)
    if (mo == None):
        return {'La': None, 'Lo': None}

    try:
        zone = int(mo.group('zone')) 
        band = MGRS_CHARS.index(mo.group('band'))
        gridcol = MGRS_CHARS.index(mo.group('gridcol'))
        gridrow = MGRS_CHARS.index(mo.group('gridrow'))
    except:
        return {'La': None, 'Lo': None}

    coordstr = mo.group('coords')    
    if len(coordstr) not in [0,2,4,6,8,10]:
        return {'La': None, 'Lo': None}
    coordlen = len(coordstr) / 2
    strx = coordstr[0:coordlen]
    stry = coordstr[coordlen:]
    strx = strx + ((5-coordlen) * '0')
    stry = stry + ((5-coordlen) * '0')
    mgrsx = float(int(strx))
    mgrsy = float(int(stry))

    gridcol0 = ((zone % 3) - 1) * 8
    if gridcol < gridcol0:
        gridcol = gridcol + 24
    gridcol = (gridcol - gridcol0) % 24    
    utme = mgrsx + 100000.0 + gridcol * 100000.0
    
    lat = 8.0 * (band - 2) - 80.0
    lon0 = ((zone - 1) * 6.0 + 3.0) - 180.0  
    xyc = lalo_to_xy(lat, lon0, lon0, 500000.0, ELLIPSOID['WGS84'])
    utmn0 = xyc['N'];
    gridrow0 = calculateMGRSGridRow(zone, utmn0)

    if gridrow < gridrow0:
        gridrow = gridrow + 20
    gridrow = (gridrow - gridrow0) % 20
    utmn = 100000.0 * math.floor(utmn0 / 100000.0) + (gridrow * 100000.0) + mgrsy
        
    return xy_to_lalo(utme, utmn, lon0, 500000.0, ELLIPSOID['WGS84'])


###########################################################################
# Function:  WGS84lalo_to_UTM_MGRS
###########################################################################
# Input:     dictionary with ['La'] is latitude in degrees (WGS84)
#                            ['Lo'] is longitude in degrees (WGS84)
# Output:    dictionary with ['E'] is UTM easting
#                            ['N'] is UTM northing
#                            ['zone'] is UTM zone
#                            ['band'] is MGRS band
#                            ['grid'] is MGRS grid
###########################################################################

def WGS84lalo_to_UTM_MGRS(WGS):
    zone = int(math.floor( (WGS['Lo'] + 180.0) / 6.0 + 1 ))
    band = int(math.floor( (WGS['La'] + 80.0) / 8.0 ) + 2)
    if WGS['La'] >= 72.0 and WGS['La'] < 84.0:
        band = 21

    # in Norway 32V is extended 3.0 degrees to west
    if band == 19 and zone == 31:
        if WGS['Lo'] >= 3.0:
            zone = 32
    # in Svalbard remove 32X, 34X and 36X
    elif band == 21 and zone >= 31 and zone <= 37:
        if zone == 32:
            if WGS['Lo'] >= 9.0:
                zone = 33
            else:
                zone = 31
        elif zone == 34:
            if WGS['Lo'] >= 21.0:
                zone = 35
            else:
                zone = 33
        elif zone == 36:
            if WGS['Lo'] >= 33.0:
                zone = 37
            else:
                zone = 35

    lon0 = ((zone - 1) * 6.0 + 3.0) - 180.0
    xyc = lalo_to_xy(WGS['La'], WGS['Lo'], lon0, 500000.0, ELLIPSOID['WGS84'])

    if xyc['N'] < 0:
        xyc['N'] = xyc['N'] + 10000000.0

    gridcol = ((zone % 3) - 1) * 8
    gridcol = gridcol + int( math.floor( ((xyc['E'] - 100000.0) / 100000.0) ))
    gridcol = gridcol % 24

    gridrow = calculateMGRSGridRow(zone, xyc['N'])

    xyc['zone'] = zone;
    xyc['band'] = MGRS_CHARS[band]
    xyc['grid'] = MGRS_CHARS[gridcol] + MGRS_CHARS[gridrow]  

    return xyc

def calculateMGRSGridRow(zone, utmN):
    if zone % 2 > 0:
        gridrow = int( math.floor((utmN / 100000.0) % 20) )
    else:
        gridrow = int( math.floor((utmN / 100000.0 + 5.0) % 20) )

    if gridrow < 0:
        gridrow = gridrow + 20
    
    return gridrow