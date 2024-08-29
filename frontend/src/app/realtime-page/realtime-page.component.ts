import { Component, OnInit, HostListener } from '@angular/core';
import { Chronometer, StatusChonometer } from 'ngx-chronometer';
import { Chart } from 'chart.js/auto';
import { ChartDataService } from '../chart-data.service';
import { HttpClient } from '@angular/common/http';
import { interval } from 'rxjs';

@Component({
  selector: 'app-realtime-page',
  templateUrl: './realtime-page.component.html',
  styleUrls: ['./realtime-page.component.css']
})
export class RealtimePageComponent implements OnInit {

  chronometer: Chronometer = new Chronometer({
    status: StatusChonometer.stop
  })
  format = '00:00';
  title = 'motion_insights';
  batch_ecg: number[] = []
  statistics: string[] = ["","","","","","","","","","","",""]
  current_tuple: string[] = []
  ecgData: number[] = []
  abnormalPeaks: String = ""
  activityDescription: String = "";
  showG1 = true;
  bpm: number = 0;
  chestAcceleration: number = 0;
  batchPredictions: string[] = []
  activity: string = ""
  usersFrequenceActivity: number[] = []
  activityFrequence: number[] = []
  maxChestAcceleration: string[] = []
  maxPercAbnormalPeaks: string[] = []
  userDynamicAndStaticActivities: string[] = []
  stddev_avg_bpm: string = ''
  sessionHistoryIsUpdated: boolean = true
  dynamic_activities: string[] = ["4.0","5.0","9.0","10.0","11.0"]
  currentActivityIsDynamic: boolean = false;
  sessionMovementActivity: number[] = []
  user: string = ""
  public backendStarted: string = "false";
  timerStarted: boolean = false;
  execution: boolean = false;
  waitStop: boolean = false;
  waitRestart: boolean = false;
  firstActivity = true;
  firstSession = true;
  avgBpmActivity: number[] = [];
  min = 1;
  max = 50;
  randomInteger: number = 0
  numActivity: number = 0;
  pastActivity: string = ""
  numBatchActivity: number = 0;

  constructor(private http: HttpClient, public chartDataService: ChartDataService) { }

  ngOnInit(): void {

    this.runChronometer();
    this.fetchGeneralHistoryData();

    this.http.get('http://localhost:8081/isStreaming',{ responseType: 'text' }).subscribe((response: any) => {
      this.backendStarted=response
    });

    interval(3000).subscribe(() => {
      this.http.get('http://localhost:8081/isStreaming',{ responseType: 'text' }).subscribe((response: any) => {
        this.backendStarted=response
      });

      // Constantly update the charts
      this.chartDataService.setChartBarNumberActivityUsers(this.usersFrequenceActivity)
      this.chartDataService.setPieDataActivityFrequence(this.activityFrequence)
      this.chartDataService.setChartPie(this.sessionMovementActivity)

      if(this.backendStarted === "true"){
        if(this.firstActivity){
          this.drawECG();
          this.firstActivity = false;
        }
        this.fetchData(); // Call function to get data every 3 seconds
      }

      if(this.activity == "" && this.statistics[5] != "null" ){ // Session started
        this.activity = this.statistics[5]
        if(this.statistics[5]=="4.0" || this.statistics[5]=="5.0" || this.statistics[5]=="9.0" || this.statistics[5]=="10.0" || this.statistics[5]=="11.0"){
          this.currentActivityIsDynamic=true;
        }
        else{
          this.currentActivityIsDynamic=false;
        }
      }

      if(this.activity != "" && this.statistics[5] != "null" && this.activity != this.statistics[5]){ // Activity changed
        console.log("Cambio attività")
        this.numBatchActivity = 0;
        this.chartDataService.resetDataAccChest()
        this.chartDataService.resetDataBpm()
        this.numActivity+=1;
        this.pastActivity=this.activity
        this.fetchHistoryData()
        this.activity=this.statistics[5]
      }

      if(!this.sessionHistoryIsUpdated && this.backendStarted === "false"){ // Session ended
        if(this.numActivity == 0){
          this.numBatchActivity = 0;
          this.chartDataService.resetDataAccChest()
          this.chartDataService.resetDataBpm()
          this.numActivity += 1;
        }
        this.fetchHistoryData()
        this.sessionHistoryIsUpdated = true
      }

      if(this.statistics[5]=="4.0" || this.statistics[5]=="5.0" || this.statistics[5]=="9.0" || this.statistics[5]=="10.0" || this.statistics[5]=="11.0"){
        this.currentActivityIsDynamic=true;
      }
      else{
        this.currentActivityIsDynamic=false;
      }
    })

  }

  fetchGeneralHistoryData(){

    this.http.get('http://localhost:8081/num_activities_per_subject',{ responseType: 'text' }).subscribe((response: any) => {
      this.usersFrequenceActivity=response.split(",").map((str:string)=>{
        return parseInt(str)
      });
    });
    this.http.get('http://localhost:8081/num_performed_activities',{ responseType: 'text' }).subscribe((response: any) => {
      this.activityFrequence=response.split(",").map((str:string)=>{
        return parseInt(str)
      });
    });
    this.http.get('http://localhost:8081/max_acceleration_chest',{ responseType: 'text' }).subscribe((response: any) => {
      this.maxChestAcceleration=response.split(",").map((str:string)=>{
        return parseFloat(str).toFixed(2).toString()
      });
    });
    this.http.get('http://localhost:8081/max_perc_abnormal_peaks',{ responseType: 'text' }).subscribe((response: any) => {
      this.maxPercAbnormalPeaks=response.split(",").map((str:string)=>{
        return parseFloat(str).toFixed(2).toString()
      });
    });

  }

  fetchHistoryData(){

    this.http.get('http://localhost:8081/num_dynamic_and_static_activities',{ responseType: 'text' }).subscribe((response: any) => {
      this.sessionMovementActivity=response.split(',').map((str:string)=>{
        return parseInt(str)
      });
    });
    this.http.get('http://localhost:8081/avg_bpm_per_activity',{ responseType: 'text' }).subscribe((response: any) => {
      this.avgBpmActivity=response.split(',').map((str:string)=>{
        return parseFloat(str)
      });
    });
    this.http.get('http://localhost:8081/total_num_dynamic_and_static_activities/'+this.user,{ responseType: 'text' }).subscribe((response: any) => {
      this.userDynamicAndStaticActivities = response.split(',');
    });
    this.http.get('http://localhost:8081/stddev_avg_bpm',{ responseType: 'text' }).subscribe((response: any) => {
      this.stddev_avg_bpm = parseFloat(response).toFixed(2).toString()
    });
    setTimeout(() => {
      this.chartDataService.addDataAvgBpmSession(this.avgBpmActivity[this.avgBpmActivity.length-1],this.numActivity+"° Activity: "+this.pastActivity);
    }, 1000);

  }

  fetchData() {

    this.numBatchActivity+=1;
    this.http.get('http://localhost:8081/statistics',{ responseType: 'text' }).subscribe((response: any) => {
      this.statistics = response.split(';')
      this.abnormalPeaks= parseFloat(this.statistics[0]).toFixed(2).toString()
      this.activityDescription=this.statistics[8]
      this.batch_ecg = this.statistics[10].split(',').map(parseFloat);
      this.ecgData = this.ecgData.concat(this.batch_ecg.map(this.scale));
      this.current_tuple = this.statistics[10].split(',').map((str:string) => {
        const num = parseFloat(str);
        return num.toFixed(3).toString();
      })
      this.bpm=parseInt(this.statistics[1])
      if(this.bpm!=0){
        this.chartDataService.addDataBpm(this.bpm,this.numBatchActivity+"° mini-batch");
      }
      this.chestAcceleration=parseFloat(parseFloat(this.statistics[2]).toFixed(3));
      this.chartDataService.addDataAccChest(this.chestAcceleration,this.numBatchActivity+"° mini-batch");
      this.batchPredictions=this.statistics[7].split('$');
      this.user=this.statistics[11]
    });

  }

  myDiv:any

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.myDiv = document.getElementById('dynamicInfo');
    if (window.scrollY > 630) {
      this.myDiv.style.display = 'block';
    } else {
      this.myDiv.style.display = 'none';
    }
  }

  goToHomePage(){
    if(this.backendStarted==='true' || this.waitRestart){
     this.callDivAlert()
   }
   else{
     window.location.href="http://localhost:4200"
   }
  }

  divAlert:any
  closeBtn:any

  callDivAlert() {
    this.divAlert = document.getElementById("alert2");
    this.divAlert.classList.add("show");
    this.divAlert.classList.remove("hide");
    this.divAlert.classList.add("showAlert");
    this.closeBtn = document.querySelector('.close-btn');
    this.closeBtn.addEventListener('click', () => {
      this.divAlert.classList.remove("show");
      this.divAlert.classList.add("hide");
      return
    });
  }

  runChronometer(){
    interval(1000).subscribe(() => {
      if(this.backendStarted==="true" && !this.execution && !this.waitStop && this.firstSession){
        this.chronometer.start()
        this.execution=true;
        this.firstSession = false;
      }
      else if(this.backendStarted==="true" && !this.execution && !this.waitStop && !this.firstSession){
        this.chronometer.restart()
        this.execution=true;
        this.waitRestart = false;
      }
      if(this.backendStarted==="false" && this.waitStop){
        this.chronometer.pause()
        this.execution=false
        this.fetchGeneralHistoryData();
      }
    })
  }

  endSession() {
    this.execution = false
    this.waitStop=true
    this.sessionHistoryIsUpdated=false
    this.http.get('http://localhost:8081/stop',{ responseType: 'text' }).subscribe((response: any) => {
    });
  }

  reStartSession() {
    this.http.get('http://localhost:8081/start/'+this.user,{ responseType: 'text' }).subscribe((response: any) => {});
    this.waitRestart = true
    this.waitStop=false
    this.numActivity=0;
    this.activity=""
    this.statistics = ["","","","","","","","","","","",""]
    this.ecgData=[]
    this.firstActivity = true;
    this.chartDataService.resetDataAvgBpmSession()
  }

  scale(x: number): number {
    x = x*250+400
    x = Math.floor(x/5)
    return x
  }

  drawECG(): void {
    const canvas = <HTMLCanvasElement> document.getElementById("canvas1");
    const ctx = canvas.getContext("2d")!;
    var ww = window.innerWidth;
    var wh = window.innerHeight;
    ctx.fillStyle = "#dbbd7a";
    ctx.fill();
    var fps = 60;
    var m = 1;
    var n = 1;

    const drawWave = () => {
      setTimeout(() => {
        if(this.backendStarted==='false'){
          ctx.clearRect(0, 0, canvas.width, canvas.height);
          return;
        }
        requestAnimationFrame(drawWave);
        ctx.lineWidth = 2;
        ctx.strokeStyle = "red";
        n += 1;
        m += 1;
        if (m >= canvas.width) {
          m = 1;
        }
        if(n >= this.ecgData.length){
            ctx.beginPath();
            ctx.moveTo(m - 1, canvas.height/2);
            ctx.lineTo(m, canvas.height/2);
            ctx.stroke();
            n -= 1;
        }
        else{
            ctx.beginPath();
            ctx.moveTo(m - 1, this.ecgData[n - 1]);
            ctx.lineTo(m, this.ecgData[n]);
            ctx.stroke();
        }
        ctx.clearRect(m, 0, 50, canvas.height);
      }, 1000 / fps);
    }

    drawWave();
  }

  initializeChart() {
    var lineChart= new Chart('linechart2', {
      type: 'line',
      data: {
        labels: [1,2,3,4,5,6,7,8,9,10], // non vogliamo etichette sull'asse x
        datasets: [{
            data: [-10.5, 5, 12, 23, 30, 40, 35, 20, 8, -5],
            fill: false
        }]
      },
      options: {
        scales: {
            y: {
                min: -20,
                max: 50,
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
  }

}
