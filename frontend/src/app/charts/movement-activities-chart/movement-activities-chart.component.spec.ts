import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovementActivitiesChartComponent } from './movement-activities-chart.component';

describe('MovementActivitiesChartComponent', () => {
  let component: MovementActivitiesChartComponent;
  let fixture: ComponentFixture<MovementActivitiesChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MovementActivitiesChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MovementActivitiesChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
