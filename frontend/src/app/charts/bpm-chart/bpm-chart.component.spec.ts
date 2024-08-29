import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BpmChartComponent } from './bpm-chart.component';

describe('BpmChartComponent', () => {
  let component: BpmChartComponent;
  let fixture: ComponentFixture<BpmChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BpmChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BpmChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
