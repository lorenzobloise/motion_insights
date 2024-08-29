import { Component, OnInit } from '@angular/core';
import { ChartDataService } from '../../chart-data.service';
import { Chart } from 'chart.js';
import { interval } from 'rxjs';

@Component({
  selector: 'app-acc-chest-x-chart',
  templateUrl: './acc-chest-x-chart.component.html',
  styleUrls: ['./acc-chest-x-chart.component.css']
})
export class AccChestXChartComponent implements OnInit {

  chartLine:any;
  refresh: any;

  constructor(private chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.chartLine=new Chart('linechartAccChestX', {
      type: 'line',
      data: {
        labels: this.chartDataService.getChartLabelsAccChest(),
        datasets: [{
            label: 'm/s^2',
            borderColor: 'purple',
            data: this.chartDataService.getChartDataAccChest(),
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
            display: false
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
