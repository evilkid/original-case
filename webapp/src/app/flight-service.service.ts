import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Fare, Location} from "./app.models";

@Injectable({
  providedIn: 'root'
})
export class FlightServiceService {

  public airportsUrl = "/travel/airports";
  public airportsCountUrl = "/travel/airports-count";
  public faresUrl = "/travel/fares";
  public metrics = "/travel/metrics";

  constructor(protected http: HttpClient) {
  }

  airportCount(term?: string): Observable<number> {
    let url = this.airportsCountUrl;

    if (term) {
      url += "?term=" + term;
    }

    return this.http.get<number>(url);
  }

  find(page?: number, size?: number, term?: string): Observable<Location[]> {
    let url = this.airportsUrl + "?";

    if (page && size) {
      url += "page=" + page + "&size=" + size;
    }
    if (term) {
      url += "&term=" + term;
    }

    return this.http.get<Location[]>(url);
  }

  fare(origin: string, destination: string, currency?: string): Observable<Fare> {
    let url = this.faresUrl + "/" + origin + "/" + destination;

    if (currency) {
      url += "?currency=" + currency;
    }
    return this.http.get<Fare>(url);
  }

  loadMetrics(): Observable<any> {
    return this.http.get(this.metrics);
  }
}
