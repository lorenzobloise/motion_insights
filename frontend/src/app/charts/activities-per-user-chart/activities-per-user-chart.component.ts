import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { ChartDataService } from '../../chart-data.service';
import { interval } from 'rxjs';

@Component({
  selector: 'app-activities-per-user-chart',
  templateUrl: './activities-per-user-chart.component.html',
  styleUrls: ['./activities-per-user-chart.component.css']
})
export class ActivitiesPerUserChartComponent implements OnInit {

  data:number[]=[0,0,0,0,0]
  newData:number[]=[]
  barChart: any;

  constructor(private chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.barChart= new Chart('barchartActivity_User', {
      type: 'bar',
      data: {
        labels: ['User1', 'User2', 'User3', 'User4', 'User5',],
        datasets: [{
          label: '',
          data: this.data,
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(255, 206, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(153, 102, 255, 0.2)',
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
          ],
          borderWidth: 1
        }]
      },
      options: {
        plugins:{
          legend:{
            display:false
          }
        },
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
    interval(3000).subscribe(() => {
      this.newData=this.chartDataService.getChartBarNumberActivityUsers()
      this.data[0]=this.newData[0]
      this.data[1]=this.newData[1]
      this.data[2]=this.newData[2]
      this.data[3]=this.newData[3]
      this.data[4]=this.newData[4]
      this.barChart.update();
    });

  }

}
