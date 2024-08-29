import { Component, OnInit } from '@angular/core';
import { ChartDataService } from '../../chart-data.service';
import { interval } from 'rxjs';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-average-bpm-variation-chart',
  templateUrl: './average-bpm-variation-chart.component.html',
  styleUrls: ['./average-bpm-variation-chart.component.css']
})
export class AverageBpmVariationChartComponent implements OnInit {

  chartLine:any

  constructor(private chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.chartLine=new Chart('linechartBPMVariation', {
      type: 'line',
      data: {
        labels: this.chartDataService.getChartLabelAvgBpmSession(),
        datasets: [{
            label: 'Bpm medi',
            data: this.chartDataService.getChartDataAvgBpmSession(),
            fill: false
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          y: {
            min: 40,
            max: 200,
              ticks: {
                  stepSize: 10
              }
          },
          x: {
            display: false
          },
        }
      }
    });
    interval(3000).subscribe(() => {
      this.chartLine.update();
    });

  }

}
