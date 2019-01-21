import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ButtonModule, ChartModule, DropdownModule, MessagesModule} from "primeng/primeng";
import {FormsModule} from "@angular/forms";
import {TabViewModule} from 'primeng/tabview';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {HttpClientModule} from "@angular/common/http";
import {TableModule} from "primeng/table";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    TabViewModule,
    AutoCompleteModule,
    DropdownModule,
    ButtonModule,
    MessagesModule,
    ChartModule,
    TableModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
