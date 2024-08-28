from __future__ import annotations

import csv
import math
from pathlib import Path
from typing import Optional

from defs import *

METRO_CONNECTIONS = [
    (MetroStationName.PLACE_DES_MARTYRS, MetroStationName.ALI_BOUMENDJEL),
    (MetroStationName.ALI_BOUMENDJEL, MetroStationName.TAFOURAH_GRANDE_POSTE),
    (MetroStationName.TAFOURAH_GRANDE_POSTE, MetroStationName.KHELIFA_BOUKHALFA),
    (MetroStationName.KHELIFA_BOUKHALFA, MetroStationName.PREMIER_MAI),
    (MetroStationName.PREMIER_MAI, MetroStationName.AISSAT_IDIR),
    (MetroStationName.AISSAT_IDIR, MetroStationName.HAMMA),
    (MetroStationName.HAMMA, MetroStationName.JARDIN_DESSAI),
    (MetroStationName.JARDIN_DESSAI, MetroStationName.LES_FUSILLES),
    (MetroStationName.LES_FUSILLES, MetroStationName.AMIROUCHE),
    (MetroStationName.AMIROUCHE, MetroStationName.MER_ET_SOLEIL),
    (MetroStationName.MER_ET_SOLEIL, MetroStationName.HAI_EL_BADR),
    (MetroStationName.HAI_EL_BADR, MetroStationName.BACHDJARAH_TENNIS),
    (MetroStationName.BACHDJARAH_TENNIS, MetroStationName.BACHDJARAH),
    (MetroStationName.BACHDJARAH, MetroStationName.EL_HARRACH_GARE),
    (MetroStationName.EL_HARRACH_GARE, MetroStationName.EL_HARRACH_CENTRE),
    (MetroStationName.HAI_EL_BADR, MetroStationName.STATION_DES_ATELIERS),
    (MetroStationName.STATION_DES_ATELIERS, MetroStationName.GUE_DE_CONSTANTINE),
    (MetroStationName.GUE_DE_CONSTANTINE, MetroStationName.AIN_NAADJA),
]

# Hack cause there is only one tramway branch
TRAMWAY_CONNECTIONS = [
    x for x in tuple(zip(list(TramwayStationName), list(TramwayStationName)[1:]))
]


def calculate_distance(start_station: Station, end_station: Station) -> float:
    # Haversine formula
    lat1 = math.radians(start_station.latitude)
    lon1 = math.radians(start_station.longitude)
    lat2 = math.radians(end_station.latitude)
    lon2 = math.radians(end_station.longitude)

    R = 6371  # Radius of the Earth in km
    dlat = lat2 - lat1
    dlon = lon2 - lon1

    a = (
        math.sin(dlat / 2) ** 2
        + math.cos(lat1) * math.cos(lat2) * math.sin(dlon / 2) ** 2
    )
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = R * c
    return round(distance, 3)


class Network:
    def __init__(self, name: str, speed: int):
        self.name = name
        self.speed = speed
        self.connections: list[Connection] = []

    def _calculate_travel_time(self, distance: float) -> int:
        return round(distance / self.speed * 60 * 60)

    def _get_travel_time_str(self, travel_time: int) -> str:
        minutes = travel_time // 60
        seconds = travel_time % 60
        return f"{minutes:02d}:{seconds:02d}"

    def add_connection(self, start: Station, end: Station):
        distance = calculate_distance(start, end)
        travel_time = self._calculate_travel_time(distance)
        travel_time_str = self._get_travel_time_str(travel_time)

        connection: Connection = Connection(
            line_name=self.name,
            start_station_name=start.name,
            start_station_lat=str(start.latitude),
            start_station_lon=str(start.longitude),
            end_station_name=end.name,
            end_station_lat=str(end.latitude),
            end_station_lon=str(end.longitude),
            travel_time=travel_time_str,
            distance=str(distance),
            branch=str(end.branch),
        )

        inverse_connection: Connection = Connection(
            line_name=self.name,
            start_station_name=end.name,
            start_station_lat=str(end.latitude),
            start_station_lon=str(end.longitude),
            end_station_name=start.name,
            end_station_lat=str(start.latitude),
            end_station_lon=str(start.longitude),
            travel_time=travel_time_str,
            distance=str(distance),
            branch=str(start.branch),
        )

        self.connections.append(connection)
        self.connections.append(inverse_connection)

    def combine_networks(self, other: Network, name: str = "network") -> Network:
        new_connections = self.connections + other.connections
        result = Network(name=name, speed=self.speed)
        result.connections = new_connections
        return result

    def export_to_csv(self, filename: Optional[str] = None):
        if filename is None:
            filename = f"{self.name}.csv"

        OUT_DIR = "output"

        Path(OUT_DIR).mkdir(parents=True, exist_ok=True)

        with open(f"{OUT_DIR}/{filename}", mode="w", newline="") as file:
            writer = csv.writer(file, delimiter=";", quoting=csv.QUOTE_ALL)
            for connection in self.connections:
                writer.writerow(
                    [
                        connection.line_name,
                        connection.branch,
                        connection.start_station_name,
                        connection.start_station_lat,
                        connection.start_station_lon,
                        connection.end_station_name,
                        connection.end_station_lat,
                        connection.end_station_lon,
                        connection.travel_time,
                        connection.distance,
                    ]
                )


def build_metro_network() -> Network:
    metro_network = Network(name="Métro d'Alger", speed=METRO_SPEED)

    for start_station, end_station in METRO_CONNECTIONS:
        start = METRO_STATION_DICT[start_station]
        end = METRO_STATION_DICT[end_station]
        metro_network.add_connection(start, end)

    return metro_network


def build_tramway_network() -> Network:
    tramway_network = Network(name="Tramway d'Alger", speed=TRAMWAY_SPEED)

    for start_station, end_station in TRAMWAY_CONNECTIONS:
        start = TRAMWAY_STATION_DICT[start_station]
        end = TRAMWAY_STATION_DICT[end_station]
        tramway_network.add_connection(start, end)

    return tramway_network


def main():
    metro_network = build_metro_network()
    metro_network.export_to_csv()

    tramway_network = build_tramway_network()
    tramway_network.export_to_csv()

    combined = metro_network.combine_networks(tramway_network)
    combined.export_to_csv()


if __name__ == "__main__":
    main()
