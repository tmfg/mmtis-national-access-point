'''
Kalkati to GTFS converter
Beware: it simplifies some things.

(c) 2011 Stefan Wehrmeyer http://stefanwehrmeyer.com
License: MIT License

http://developer.matka.fi/pages/en/kalkati.net-xml-database-dump.php

'''

from datetime import date, timedelta
from dateutil import parser as dparser
import os
import sys
import xml.sax
import tempfile
import shutil

from xml.sax.handler import ContentHandler
from coordinates import KKJxy_to_WGS84lalo

timezone = 'Europe/Helsinki'

'''
Kalkati Transport modes
1	air
2	train
21	long/mid distance train
22	local train
23	rapid transit
3	metro
4	tramway
5	bus, coach
6	ferry
7	waterborne
8	private vehicle
9	walk
10	other

GTFS Transport Modes
0 - Tram, Streetcar, Light rail.
1 - Subway, Metro.
2 - Rail.
3 - Bus.
4 - Ferry.
5 - Cable car.
6 - Gondola, Suspended cable car.
7 - Funicular.
'''

KALKATI_MODE_TO_GTFS_MODE = {
    '2': '2',
    '21': '2',
    '22': '0',
    '23': '2',
    '3': '1',
    '4': '0',
    '5': '3',
    '6': '4',
    '7': '4'
}


class KalkatiHandler(ContentHandler):
    agency_fields = (u'agency_id', u'agency_name', u'agency_url',
                     u'agency_timezone',)
    stops_fields = (u'stop_id', u'stop_name', u'stop_lat', u'stop_lon',)
    routes_fields = (u'route_id', u'agency_id', u'route_short_name',
                     u'route_long_name', u'route_type',)
    trips_fields = (u'route_id', u'service_id', u'trip_id',)
    stop_times_fields = (u'trip_id', u'arrival_time', u'departure_time',
                         u'stop_id', u'stop_sequence',)
    calendar_fields = (u'service_id', u'monday', u'tuesday', u'wednesday',
                       u'thursday', u'friday', u'saturday', u'sunday', u'start_date',
                       u'end_date',)

    route_count = 0
    service_count = 0
    routes = {}

    delivery = {}
    synonym = False
    stop_sequence = None
    kal_service_id = None
    trips = None
    route_agency_id = None
    route_short_name = None
    route_long_name = None
    service_validities = None
    service_mode = None
    transmodes = {}

    def write_values(self, name, values):
        self.files[name].write((u','.join(values) + u'\n').encode('utf-8'))

    def __init__(self, gtfs_files):
        self.files = gtfs_files

        for name in gtfs_files:
            self.write_values(name, getattr(self, '%s_fields' % name))

    # Converts Kalkati <Station> to GTFS stop
    def add_stop(self, attrs):
        if (not ('Y' in attrs or 'X' in attrs)):
            raise KeyError('<Station> is missing X or Y coordinates. StationId: ' + attrs['StationId'])

        # In Kalkati, Station coordinates ar in KKJ3 format. Convert them into WGS84.
        KKJNorthing = float(attrs['Y'])
        KKJEasting = float(attrs['X'])
        KKJLoc = {'P': KKJNorthing, 'I': KKJEasting}
        WGS84lalo = KKJxy_to_WGS84lalo(KKJin=KKJLoc, zone=3)

        self.write_values('stops', (attrs['StationId'],
                                    attrs.get('Name', 'Unnamed').replace(',', ' '),
                                    str(WGS84lalo['La']), str(WGS84lalo['Lo'])))

    def add_agency(self, attrs):
        self.write_values('agency', (attrs['CompanyId'],
                                     attrs['Name'].replace(',', ' '),
                                     'http://example.com', timezone))  # can't know

    def add_calendar(self, attrs):
        '''This is the inaccurate part of the whole operation!
        This assumes that the footnote vector has a regular shape
        i.e. every week the same service
        '''

        service_id = attrs.getValue('FootnoteId')
        # If there is no value for this attribute the start date of the vector is the Firstdate of the Delivery element.
        first = attrs.get('Firstdate', self.delivery.get('Firstday'))
        first_date = dparser.parse(first).date()
        vector = attrs.getValue('Vector')

        if not len(vector):
            null = ('0',) * 7
            empty_date = first.replace('-', '')
            self.write_values('calendar', (service_id,) + null +
                              (empty_date, empty_date))
            return

        end_date = first_date + timedelta(days=len(vector))
        weekday = first_date.weekday()
        weekdays = [0] * 7

        for i, day in enumerate(vector):
            weekdays[(weekday + i) % 7] += int(day)

        # only take services that appear at least half the maximum appearance
        # this is an oversimplification, sufficient for me for now
        avg = max(weekdays) / 2.0
        weekdays = map(lambda x: '1' if x > avg else '0', weekdays)
        fd = str(first_date).replace('-', '')
        ed = str(end_date).replace('-', '')

        self.write_values('calendar', (service_id,) + tuple(weekdays) +
                          (fd, ed))

    def add_stop_time(self, attrs):
        for trip in self.trips:
            station_idx = int(attrs['Ix'])
            first_stop = trip['Firststop']
            last_stop = trip['Laststop']

            if (first_stop and (station_idx < int(first_stop))) or (last_stop and (station_idx > int(last_stop))):
                continue

            self.stop_sequence.append(attrs['StationId'])

            departure_time = None
            arrival_time = None

            if 'Departure' in attrs:
                departure_time = ':'.join((attrs['Departure'][:2],
                                           attrs['Departure'][2:], '00'))

            # Arrival (optional) defines the arrival time (in local time) to the stop.
            # If arrival is not set, it is expected to be the same as departure time.
            if 'Arrival' in attrs:
                arrival_time = ':'.join((attrs['Arrival'][:2],
                                         attrs['Arrival'][2:], '00'))
            else:
                arrival_time = departure_time

            self.write_values('stop_times', (trip['id'], arrival_time,
                                             departure_time, attrs['StationId'], attrs['Ix']))

    def add_route(self, route_id):
        route_type = '3'  # fallback is bus

        if self.service_mode in self.transmodes:
            trans_mode = self.transmodes[self.service_mode]

            if trans_mode in KALKATI_MODE_TO_GTFS_MODE:
                route_type = KALKATI_MODE_TO_GTFS_MODE[trans_mode]

        self.write_values('routes', (route_id,
                                     self.route_agency_id,
                                     self.route_short_name.replace(',', '.'),
                                     self.route_long_name.replace(',', '.'),
                                     route_type))

    def add_trip(self, route_id):
        for service_id, trip in zip(self.service_validities, self.trips):
            self.write_values('trips', (route_id, service_id, trip['id']))

    def startElement(self, name, attrs):
        if not self.synonym and name == 'Delivery':
            self.delivery = {'Firstday': attrs.get('Firstday'),
                             'Lastday': attrs.get('Lastday'),
                             'CompanyId': attrs.getValue('CompanyId'),
                             'Version': attrs.getValue('Version')}
        elif not self.synonym and name == 'Company':
            self.add_agency(attrs)
        elif not self.synonym and name == 'Station':
            self.add_stop(attrs)
        elif not self.synonym and name == 'Trnsmode':
            if 'Modetype' in attrs:
                self.transmodes[attrs['TrnsmodeId']] = attrs['Modetype']
        elif name == 'Footnote':
            self.add_calendar(attrs)
        elif name == 'Service':
            self.kal_service_id = attrs['ServiceId']
            self.service_count += 1
            self.trips = []
            self.service_validities = []
            self.stop_sequence = []
        elif name == 'ServiceNbr':
            self.route_agency_id = attrs['CompanyId']
            self.route_short_name = attrs.get('Variant')
            self.route_long_name = attrs.get('Name', 'Unnamed')
        elif name == 'ServiceValidity':
            self.service_validities.append(attrs['FootnoteId'])
            self.trips.append({
                'id': 't_%s_%s' % (str(self.kal_service_id), str(len(self.trips))),
                'Firststop': attrs.get('Firststop'),
                'Laststop': attrs.get('Laststop')
            })
        elif name == 'ServiceTrnsmode':
            # Kalkati allows changing transport modes between stations in one route.
            # vs. GTFS allows only one transmode per route.
            # So, we'll have to assume that there is only one transmode defined in the Kalkati file and use it.
            self.service_mode = attrs['TrnsmodeId']
        elif name == 'Stop':
            self.add_stop_time(attrs)
        elif name == 'Synonym':
            self.synonym = True

    def endElement(self, name):
        if name == 'Synonym':
            self.synonym = False
        elif name == 'Service':
            route_seq = '-'.join(self.stop_sequence)

            if route_seq in self.routes:
                route_id = self.routes[route_seq]
            else:
                self.route_count += 1
                route_id = 'r_' + str(self.route_count)
                self.routes[route_seq] = route_id
                self.add_route(route_id)

            self.add_trip(route_id)
            self.kal_service_id = None
            self.trips = None
            self.stop_sequence = None
            self.route_agency_id = None
            self.route_short_name = None
            self.route_long_name = None
            self.service_validities = None
            self.service_mode = None


def init_gtfs_files():
    names = ['stops', 'agency', 'calendar', 'stop_times', 'trips', 'routes']
    files = {}
    MB = 1 << 20

    for name in names:
        files[name] = tempfile.SpooledTemporaryFile(max_size=500 * MB, mode='w+b')

    return files


def convert_in_memory(kalkati_file):
    gtfs_files = init_gtfs_files()
    handler = KalkatiHandler(gtfs_files)
    xml.sax.parse(kalkati_file, handler)

    for name, file in gtfs_files.iteritems():
        file.seek(0)

    return gtfs_files


def write_to_disk(files, directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

    for name, file in files.iteritems():
        file.seek(0)

        with open (os.path.join(directory, name + '.txt'), 'w') as target_file:
            shutil.copyfileobj(file, target_file)
            target_file.close()


def main(filename, directory):
    files = convert_in_memory(filename)
    write_to_disk(files, directory)


if __name__ == '__main__':
    try:
        filename = sys.argv[1]
        output = sys.argv[2]
    except IndexError:
        sys.stderr.write('Usage: %s kalkati_xml_file output_directory\n' % sys.argv[0])
        sys.exit(1)
    main(filename, output)
