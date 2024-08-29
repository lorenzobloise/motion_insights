import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { ChartDataService } from '../../chart-data.service';
import { interval } from 'rxjs';

@Component({
  selector: 'app-activity-frequency-chart',
  templateUrl: './activity-frequency-chart.component.html',
  styleUrls: ['./activity-frequency-chart.component.css']
})
export class ActivityFrequencyChartComponent implements OnInit {

  pieChart: any;
  data:number[]=[0,0,0,0,0,0,0,0,0,0,0]
  newData:number[]=[]

  constructor(private chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.pieChart= new Chart('pieChartActivityFrequency',{
      type: 'pie',
      data:{
        labels: ['Activity-1', 'Activity-2','Activity-3',
        'Activity-4','Activity-5','Activity-6','Activity-7',
        'Activity-8','Activity-9','Activity-10','Activity-11'],
        datasets: [{
          data: this.data,
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(255, 206, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(153, 102, 255, 0.2)',
            'rgba(255, 159, 64, 0.2)',
            'rgba(255, 0, 0, 0.2)',
            'rgba(0, 255, 0, 0.2)',
            'rgba(0, 0, 255, 0.2)',
            'rgba(128, 128, 128, 0.2)',
            'rgba(0, 128, 128, 0.2)'
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)',
            'rgba(255, 0, 0, 1)',
            'rgba(0, 255, 0, 1)',
            'rgba(0, 0, 255, 1)',
            'rgba(128, 128, 128, 1)',
            'rgba(0, 128, 128, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels:{
              font:{
                size:11
              }
            },
            maxHeight: 100,
            position: 'left',
          },
          title: {
            display: false,
          }
        }
      },
    });

    interval(3000).subscribe(() => {
      this.newData=this.chartDataService.getPieDataActivityFrequence()
      this.data[0]=this.newData[0]
      this.data[1]=this.newData[1]
      this.data[2]=this.newData[2]
      this.data[3]=this.newData[3]
      this.data[4]=this.newData[4]
      this.data[5]=this.newData[5]
      this.data[6]=this.newData[6]
      this.data[7]=this.newData[7]
      this.data[8]=this.newData[8]
      this.data[9]=this.newData[9]
      this.data[10]=this.newData[10]
      this.data[11]=this.newData[11]
      this.pieChart.update();
    });

  }

}
