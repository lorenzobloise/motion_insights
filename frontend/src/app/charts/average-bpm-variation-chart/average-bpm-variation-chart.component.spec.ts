import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AverageBpmVariationChartComponent } from './average-bpm-variation-chart.component';

describe('AverageBpmVariationChartComponent', () => {
  let component: AverageBpmVariationChartComponent;
  let fixture: ComponentFixture<AverageBpmVariationChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AverageBpmVariationChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AverageBpmVariationChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
