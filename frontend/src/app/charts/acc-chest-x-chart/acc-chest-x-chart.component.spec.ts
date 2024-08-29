import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccChestXChartComponent } from './acc-chest-x-chart.component';

describe('AccChestXChartComponent', () => {
  let component: AccChestXChartComponent;
  let fixture: ComponentFixture<AccChestXChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccChestXChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccChestXChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
