import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitiesPerUserChartComponent } from './activities-per-user-chart.component';

describe('ActivitiesPerUserChartComponent', () => {
  let component: ActivitiesPerUserChartComponent;
  let fixture: ComponentFixture<ActivitiesPerUserChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActivitiesPerUserChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivitiesPerUserChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
