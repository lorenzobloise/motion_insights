import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StartPageComponent } from './start-page/start-page.component';
import { RealtimePageComponent } from './realtime-page/realtime-page.component';
import { NgxChronometerModule } from 'ngx-chronometer';
import { BpmChartComponent } from './charts/bpm-chart/bpm-chart.component';
import { AccChestXChartComponent } from './charts/acc-chest-x-chart/acc-chest-x-chart.component';
import { AverageBpmVariationChartComponent } from './charts/average-bpm-variation-chart/average-bpm-variation-chart.component';
import { MovementActivitiesChartComponent } from './charts/movement-activities-chart/movement-activities-chart.component';
import { ActivityFrequencyChartComponent } from './charts/activity-frequency-chart/activity-frequency-chart.component';
import { ActivitiesPerUserChartComponent } from './charts/activities-per-user-chart/activities-per-user-chart.component';

@NgModule({
  declarations: [
    AppComponent,
    StartPageComponent,
    RealtimePageComponent,
    BpmChartComponent,
    AccChestXChartComponent,
    AverageBpmVariationChartComponent,
    MovementActivitiesChartComponent,
    ActivityFrequencyChartComponent,
    ActivitiesPerUserChartComponent,
  ],
  imports: [
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgxChronometerModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
})
export class AppModule { }
