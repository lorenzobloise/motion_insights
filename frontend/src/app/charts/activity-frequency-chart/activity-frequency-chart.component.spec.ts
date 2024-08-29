import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityFrequencyChartComponent } from './activity-frequency-chart.component';

describe('ActivityFrequencyChartComponent', () => {
  let component: ActivityFrequencyChartComponent;
  let fixture: ComponentFixture<ActivityFrequencyChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActivityFrequencyChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityFrequencyChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
