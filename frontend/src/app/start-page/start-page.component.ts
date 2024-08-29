import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { interval } from 'rxjs';

declare var ScrollReveal: any;

@Component({
  selector: 'app-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.css']
})
export class StartPageComponent implements OnInit {

  router: any;
  user: string = '1';
  backendStarted: string = ''
  started: boolean = false;
  myButton:any;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    ScrollReveal().reveal('#startBox', { delay: 700, origin: 'left' });
  }

  backendCall(){
    this.http.get('http://localhost:8081/start/'+this.user).subscribe(() => {
    });
    interval(500).subscribe(() => {
      this.started=true;
      this.http.get('http://localhost:8081/isStreaming',{ responseType: 'text' }).subscribe((response: any) => {
        this.backendStarted=response
      });
      if(this.backendStarted==='true'){
        window.location.href="http://localhost:4200/realtime"
      }
    });
  }

  onSelectionChange(value: string) {
    this.user=value
  }

}
