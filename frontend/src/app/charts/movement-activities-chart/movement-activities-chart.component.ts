import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { interval } from 'rxjs';
import { ChartDataService } from '../../chart-data.service';

@Component({
  selector: 'app-movement-activities-chart',
  templateUrl: './movement-activities-chart.component.html',
  styleUrls: ['./movement-activities-chart.component.css']
})
export class MovementActivitiesChartComponent implements OnInit {

  newData:number[]=[]
  pieChart: any;
  dati:number[]=[0,0]

  constructor(private chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.pieChart= new Chart('pieChartDynamicActivties',{
      type: 'pie',
      data:{
        labels: ['Dynamic Activities', 'Static Activities'],
        datasets: [{
          data: this.dati,
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(54, 162, 235, 0.2)'
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'left',
          },
          title: {
            display: true,
          }
        },
        layout: {
          padding: 0
        }
      },
    });
    interval(3000).subscribe(() => {
      this.newData=this.chartDataService.getPieDataMovementActivity();
      this.dati[0]=this.newData[0]
      this.dati[1]=this.newData[1]
      this.pieChart.update();
    });

  }

}
