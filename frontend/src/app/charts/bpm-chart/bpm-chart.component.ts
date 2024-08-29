import { Component, OnInit } from '@angular/core';
import { ChartDataService } from '../../chart-data.service';
import { Chart } from 'chart.js';
import { interval } from 'rxjs';

@Component({
  selector: 'app-bpm-chart',
  templateUrl: './bpm-chart.component.html',
  styleUrls: ['./bpm-chart.component.css']
})
export class BpmChartComponent implements OnInit {

  chartLine:any;
  refresh: any;

  constructor(private chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.chartLine=new Chart('linechartBpm', {
      type: 'line',
      data: {
        labels: this.chartDataService.getChartLabelsBpm(),
        datasets: [{
            label: 'bpm',
            data: this.chartDataService.getChartDataBpm(),
            fill: false
        }]
      },
      options: {
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
