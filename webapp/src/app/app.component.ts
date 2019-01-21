import {Component, OnInit} from '@angular/core';
import {FlightServiceService} from "./flight-service.service";
import {Fare, Location} from "./app.models";
import {LazyLoadEvent, Message, SelectItem} from "primeng/api";
import {log} from "util";
import {forkJoin, Observable} from "rxjs";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'KLM Flight Finder';

  constructor(private flightService: FlightServiceService) {
  }

  msgs: Message[] = [];

  data: any;

  origin: Location;
  filteredOrigins: Location[];

  destination: Location;
  filteredDestination: Location[];

  currency: string;
  currencies: SelectItem[] = [{label: 'EUR', value: 'EUR'}, {label: 'USD', value: 'USD'}];

  fare: Fare;


  airports: Location[];

  ngOnInit(): void {
    this.flightService.find(0, 20).subscribe(locations => {
      this.filteredOrigins = locations;
      this.filteredDestination = locations;
    });
  }

  filterOrigins(event) {
    this.flightService.find(null, null, event.query).subscribe(locations => {
      this.filteredOrigins = locations;
    })
  }

  filterDestination(event) {
    this.flightService.find(null, null, event.query).subscribe(locations => {
      this.filteredDestination = locations;
    })
  }

  search(event) {
    if (!this.origin || !this.destination || this.origin.code === this.destination.code) {
      this.msgs.push({severity: 'error', summary: 'Invalid selections', detail: 'Invalid selections'});
      return;
    }
    this.flightService.fare(this.origin.code, this.destination.code, this.currency)
      .subscribe(fare => this.fare = fare);
  }


  handleChange(e) {
    let index = e.index;
    if (index === 1) {
      this.fillAirports(1, 25);
    }
    if (index === 2) {
      this.fillData();
    }
  }

  fillData() {
    this.flightService.loadMetrics().subscribe(data => {
      this.data = {
        labels: Object.keys(data),
        datasets: [{
          data: Object.values(data),
          backgroundColor: this.namesToColors(Object.keys(data))
        }]
      };
    });
  }

  namesToColors(names) {
    let colors: string[] = [];
    for (const name of names) {
      colors.push(this.nameToColor(name));
    }
    return colors;
  }

  nameToColor(str) {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    var colour = '#';
    for (var i = 0; i < 3; i++) {
      var value = (hash >> (i * 8)) & 0xFF;
      colour += ('00' + value.toString(16)).substr(-2);
    }
    return colour;
  }

  totalRecords: number;

  fillAirports(page?: number, size?: number, term?: string) {
    forkJoin(
      this.flightService.find(page, size, term),
      this.flightService.airportCount(term)
    ).subscribe(([airports, count]) => {
        this.airports = airports;
        this.totalRecords = count;
      });
  }


  loadAirports(event: LazyLoadEvent) {
    if (event.globalFilter) {
      this.fillAirports((event.first / 25) + 1, 25, event.globalFilter);
    } else {
      this.flightService.find((event.first / 25) + 1, 25).subscribe(value => {
        this.airports = value;
      });
    }
  }

}
