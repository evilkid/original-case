export interface Location {
  code?: string;
  name?: string;
  coordinates?: Coordinates;
}

export interface Coordinates {
  latitude?: number;
  longitude?: number;
}

export interface Fare {
  amount?: number;
  currency?: string;
  origin?: Location;
  destination?: Location;
}
