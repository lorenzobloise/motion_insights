import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StartPageComponent } from './start-page/start-page.component';
import { RealtimePageComponent } from './realtime-page/realtime-page.component';

const routes: Routes = [
  {
    path: '',
    component: StartPageComponent,
    pathMatch: 'full'
  },
  {
    path: 'realtime',
    component: RealtimePageComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
