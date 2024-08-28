from dataclasses import dataclass
from enum import Enum


@dataclass
class Station:
    name: str
    latitude: float
    longitude: float
    branch: int = 0


@dataclass
class Connection:
    line_name: str
    branch: str
    start_station_name: str
    start_station_lat: str
    start_station_lon: str
    end_station_name: str
    end_station_lat: str
    end_station_lon: str
    travel_time: str
    distance: str


class MetroStationName(Enum):
    PLACE_DES_MARTYRS = "Place des Martyrs"
    ALI_BOUMENDJEL = "Ali Boumendjel"
    TAFOURAH_GRANDE_POSTE = "Tafourah - Grande Poste"
    KHELIFA_BOUKHALFA = "Khelifa Boukhalfa"
    PREMIER_MAI = "1er Mai"
    AISSAT_IDIR = "Aïssat Idir"
    HAMMA = "Hamma"
    JARDIN_DESSAI = "Jardin d'Essai"
    LES_FUSILLES = "Les Fusillés"
    AMIROUCHE = "Amirouche"
    MER_ET_SOLEIL = "Mer et Soleil"
    HAI_EL_BADR = "Haï El Badr"
    BACHDJARAH_TENNIS = "Bachdjarah - Tennis"
    BACHDJARAH = "Bachdjarah"
    EL_HARRACH_GARE = "El Harrach Gare"
    EL_HARRACH_CENTRE = "El Harrach Centre"
    STATION_DES_ATELIERS = "Station des Ateliers"
    GUE_DE_CONSTANTINE = "Gué de Constantine"
    AIN_NAADJA = "Aïn Naâdja"


METRO_STATION_DICT = {
    MetroStationName.PLACE_DES_MARTYRS: Station(
        "Place des Martyrs", 36.785489, 3.0623351
    ),
    MetroStationName.ALI_BOUMENDJEL: Station("Ali Boumendjel", 36.7802085, 3.0586052),
    MetroStationName.TAFOURAH_GRANDE_POSTE: Station(
        "Tafourah - Grande Poste", 36.7720017, 3.0581852
    ),
    MetroStationName.KHELIFA_BOUKHALFA: Station(
        "Khelifa Boukhalfa", 36.7662321, 3.0536627
    ),
    MetroStationName.PREMIER_MAI: Station("1er Mai", 36.7601908, 3.0566104),
    MetroStationName.AISSAT_IDIR: Station("Aïssat Idir", 36.7568509, 3.0617531),
    MetroStationName.HAMMA: Station("Hamma", 36.752896, 3.0665223),
    MetroStationName.JARDIN_DESSAI: Station("Jardin d'Essai", 36.7466634, 3.0727322),
    MetroStationName.LES_FUSILLES: Station("Les Fusillés", 36.7421437, 3.0831356),
    MetroStationName.AMIROUCHE: Station("Amirouche", 36.737895, 3.0944401),
    MetroStationName.MER_ET_SOLEIL: Station("Mer et Soleil", 36.7335722, 3.1009505),
    MetroStationName.HAI_EL_BADR: Station("Haï El Badr", 36.7254312, 3.1022698),
    MetroStationName.BACHDJARAH_TENNIS: Station(
        "Bachdjarah - Tennis", 36.7180674, 3.1110684, 1
    ),
    MetroStationName.BACHDJARAH: Station("Bachdjarah", 36.7217003, 3.1180881, 1),
    MetroStationName.EL_HARRACH_GARE: Station(
        "El Harrach Gare", 36.7220937, 3.1317268, 1
    ),
    MetroStationName.EL_HARRACH_CENTRE: Station(
        "El Harrach Centre", 36.7220024, 3.1371632, 1
    ),
    MetroStationName.STATION_DES_ATELIERS: Station(
        "Station des Ateliers", 36.71552, 3.1008295, 2
    ),
    MetroStationName.GUE_DE_CONSTANTINE: Station(
        "Gué de Constantine", 36.6967503, 3.0952804, 2
    ),
    MetroStationName.AIN_NAADJA: Station("Aïn Naâdja", 36.6894156, 3.0789795, 2),
}

METRO_SPEED = 40


class TramwayStationName(Enum):
    RUISSEAU = "Ruisseau"
    LES_FUSILLES = "Les Fusillés"
    TRIPOLI_THAALIBIA = "Tripoli-Thaalibia"
    TRIPOLI_MOSQUEE = "Tripoli-Mosquée"
    TRIPOLI_HAMADECHE = "Tripoli-Hamadeche"
    TRIPOLI_MAQQARIA = "Tripoli-Maqqaria"
    CAROUBIER = "Caroubier"
    LA_GLACIERE = "La Glacière"
    PONT_EL_HARRACH = "Pont El Harrach"
    BELLEVUE = "Bellevue"
    BEKRI_BOUGERRA = "Bekri Bougerra"
    CINQ_MAISONS = "Cinq Maisons"
    FOIRE_D_ALGER = "Foire d'Alger"
    LES_PINS = "Les Pins"
    TAMARIS = "Tamaris"
    CITE_MOKHTAR_ZERHOUNI = "Cité Mokhtar Zerhouni"
    CITE_RABIA_TAHAR = "Cité Rabia Tahar"
    UNIVERSITE_DE_BAB_EZZOUAR = "Université de Bab Ezzouar (USTHB)"
    CITE_5_JUILLET = "Cité 5 juillet"
    BAB_EZZOUAR_LE_PONT = "Bab Ezzouar - Le Pont"
    CITE_UNIVERSITAIRE_CUB1 = "Cité universitaire - CUB1"
    CITE_8_MAI_1945 = "Cité 8 mai 1945"
    CITE_CLAIR_MATIN = "Cité Clair Matin"
    BORDJ_EL_KIFFAN_LYCEE = "Bordj El Kiffan - Lycée"
    BORDJ_EL_KIFFAN_CENTRE = "Bordj El Kiffan - Centre"
    BORDJ_EL_KIFFAN_POLYCLINIQUE = "Bordj El Kiffan - Polyclinique"
    MOUHOUS = "Mouhous"
    MIMOUNI_HAMOUD = "Mimouni Hamoud"
    BEN_MERABET = "Ben Merabet"
    BEN_REDOUANE = "Ben Redouane"
    BEN_MERRED = "Ben Merred"
    SIDI_DRIS = "Sidi Dris"
    BENZERGA = "Benzerga"
    CAFE_CHERGUI = "Café Chergui"
    FACULTE_BIOMEDICALE = "Faculté Biomédicale"
    DERGANA_CITE_DIPLOMATIQUE = "Dergana Cité Diplomatique"
    DERGANA_CENTRE = "Dergana Centre"


TRAMWAY_STATION_DICT = {
    TramwayStationName.RUISSEAU: Station("Ruisseau", 36.742878, 3.0834886),
    TramwayStationName.LES_FUSILLES: Station("Les Fusillés", 36.7459931, 3.086813),
    TramwayStationName.TRIPOLI_THAALIBIA: Station(
        "Tripoli-Thaalibia", 36.7452182, 3.0922946
    ),
    TramwayStationName.TRIPOLI_MOSQUEE: Station(
        "Tripoli-Mosquée", 36.7432626, 3.0974131
    ),
    TramwayStationName.TRIPOLI_HAMADECHE: Station(
        "Tripoli-Hamadeche", 36.7407195, 3.1040904
    ),
    TramwayStationName.TRIPOLI_MAQQARIA: Station(
        "Tripoli-Maqqaria", 36.7370321, 3.1136772
    ),
    TramwayStationName.CAROUBIER: Station("Caroubier", 36.7357136, 3.1181771),
    TramwayStationName.LA_GLACIERE: Station("La Glacière", 36.7328166, 3.1246314),
    TramwayStationName.PONT_EL_HARRACH: Station(
        "Pont El Harrach", 36.7302783, 3.1301597
    ),
    TramwayStationName.BELLEVUE: Station("Bellevue", 36.7294979, 3.1360516),
    TramwayStationName.BEKRI_BOUGERRA: Station("Bekri Bougerra", 36.7278111, 3.1434353),
    TramwayStationName.CINQ_MAISONS: Station("Cinq Maisons", 36.7258806, 3.1508033),
    TramwayStationName.FOIRE_D_ALGER: Station("Foire d'Alger", 36.7312694, 3.1613689),
    TramwayStationName.LES_PINS: Station("Les Pins", 36.7321994, 3.167),
    TramwayStationName.TAMARIS: Station("Tamaris", 36.7325544, 3.1732935),
    TramwayStationName.CITE_MOKHTAR_ZERHOUNI: Station(
        "Cité Mokhtar Zerhouni", 36.7294782, 3.1743236
    ),
    TramwayStationName.CITE_RABIA_TAHAR: Station(
        "Cité Rabia Tahar", 36.7244806, 3.1769703
    ),
    TramwayStationName.UNIVERSITE_DE_BAB_EZZOUAR: Station(
        "Université de Bab Ezzouar (USTHB)", 36.7208103, 3.1795751
    ),
    TramwayStationName.CITE_5_JUILLET: Station("Cité 5 juillet", 36.7215273, 3.1828188),
    TramwayStationName.BAB_EZZOUAR_LE_PONT: Station(
        "Bab Ezzouar - Le Pont", 36.7243461, 3.184784
    ),
    TramwayStationName.CITE_UNIVERSITAIRE_CUB1: Station(
        "Cité universitaire - CUB1", 36.7322506, 3.1840653
    ),
    TramwayStationName.CITE_8_MAI_1945: Station(
        "Cité 8 mai 1945", 36.7373281, 3.1849088
    ),
    TramwayStationName.CITE_CLAIR_MATIN: Station(
        "Cité Clair Matin", 36.7409744, 3.1860904
    ),
    TramwayStationName.BORDJ_EL_KIFFAN_LYCEE: Station(
        "Bordj El Kiffan - Lycée", 36.7453267, 3.1876677
    ),
    TramwayStationName.BORDJ_EL_KIFFAN_CENTRE: Station(
        "Bordj El Kiffan - Centre", 36.7470254, 3.1910023
    ),
    TramwayStationName.BORDJ_EL_KIFFAN_POLYCLINIQUE: Station(
        "Bordj El Kiffan - Polyclinique", 36.7481604, 3.1970759
    ),
    TramwayStationName.MOUHOUS: Station("Mouhous", 36.7493125, 3.2064799),
    TramwayStationName.MIMOUNI_HAMOUD: Station("Mimouni Hamoud", 36.7503393, 3.2119777),
    TramwayStationName.BEN_MERABET: Station("Ben Merabet", 36.7535674, 3.2200951),
    TramwayStationName.BEN_REDOUANE: Station("Ben Redouane", 36.7554099, 3.2242171),
    TramwayStationName.BEN_MERRED: Station("Ben Merred", 36.7589908, 3.2294045),
    TramwayStationName.SIDI_DRIS: Station("Sidi Dris", 36.7660323, 3.2384262),
    TramwayStationName.BENZERGA: Station("Benzerga", 36.7730406, 3.2451557),
    TramwayStationName.CAFE_CHERGUI: Station("Café Chergui", 36.7774533, 3.2501635),
    TramwayStationName.FACULTE_BIOMEDICALE: Station(
        "Faculté Biomédicale", 36.7777007, 3.2588623
    ),
    TramwayStationName.DERGANA_CITE_DIPLOMATIQUE: Station(
        "Dergana Cité Diplomatique", 36.7739566, 3.2593565
    ),
    TramwayStationName.DERGANA_CENTRE: Station("Dergana Centre", 36.7719729, 3.2603056),
}

TRAMWAY_SPEED = 30
