import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ChartDataService {

  private chartDataAccChest: number[] =[]
  private chartLabelsAccChest: string[]=[]
  private chartDataBpm: number[] =[]
  private chartLabelsBpm: string[]=[]
  private PieDataMovement: number[]=[]
  private chartDataAvgBpmSession: number[] =[]
  private chartLabelsAvgBpmSession: string[]=[]

  constructor() { }

  // Methods for adding data to the chest acceleration chart

  getChartDataAccChest(): number[] {
    return this.chartDataAccChest;
  }

  getChartLabelsAccChest(): string[] {
    return this.chartLabelsAccChest;
  }

  addDataAccChest(data: number, label: string): void {
    this.chartDataAccChest.push(data);
    console.log(this.chartDataAccChest)
    this.chartLabelsAccChest.push(label);
  }

  resetDataAccChest():void{
    var len=this.chartDataAccChest.length
    for(var i=0;i<len;i++){
      this.chartDataAccChest.pop()
    }
    for(var i=0;i<len;i++){
      this.chartLabelsAccChest.pop()
    }
  }

  // Methods for adding data to the BPM chart

  getChartDataBpm(): number[] {
    return this.chartDataBpm;
  }

  getChartLabelsBpm(): string[] {
    return this.chartLabelsBpm;
  }

  addDataBpm(data: number, label: string): void {
    this.chartDataBpm.push(data);
    console.log(this.chartDataBpm)
    this.chartLabelsBpm.push(label);
  }

  resetDataBpm():void{
    var len=this.chartDataBpm.length
    for(var i=0;i<len;i++){
      this.chartDataBpm.pop()
    }
    for(var i=0;i<len;i++){
      this.chartLabelsBpm.pop()
    }
  }

  // Method for adding data to the movement pie chart

  getPieDataMovementActivity():number[]{
    return this.PieDataMovement
  }

  setChartPie(data:number[]):void{
    this.PieDataMovement=data
  }

  // Methods for adding data to the average BPM per activity chart

  getChartDataAvgBpmSession(): number[] {
    return this.chartDataAvgBpmSession
  }

  getChartLabelAvgBpmSession(): string[] {
    return this.chartLabelsAvgBpmSession
  }

  addDataAvgBpmSession(data: number, label: string): void {
    this.chartDataAvgBpmSession.push(data);
    console.log(this.chartDataAvgBpmSession)
    this.chartLabelsAvgBpmSession.push(label);
  }

  resetDataAvgBpmSession():void{
    var len=this.chartDataAvgBpmSession.length
    for(var i=0;i<len;i++){
      this.chartDataAvgBpmSession.pop()
    }
    for(var i=0;i<len;i++){
      this.chartLabelsAvgBpmSession.pop()
    }
  }

  // Methods for adding data to the activity frequence pie chart

  private pieChartDataActivityFrequence: number[]=[]

  getPieDataActivityFrequence(): number[]{
    return this.pieChartDataActivityFrequence;
  }

  setPieDataActivityFrequence(data:number[]): void{
    this.pieChartDataActivityFrequence=data;
  }

  // Methods for adding data to the activity per user bar chart

  private barChartDataNumberOfActivityUsers: number[]=[]

  setChartBarNumberActivityUsers(data: number[]):void{
    this.barChartDataNumberOfActivityUsers=data
  }

  getChartBarNumberActivityUsers():number[]{
    return this.barChartDataNumberOfActivityUsers
  }

}
