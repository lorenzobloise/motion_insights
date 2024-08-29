import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RealtimePageComponent } from './realtime-page.component';

describe('RealtimePageComponent', () => {
  let component: RealtimePageComponent;
  let fixture: ComponentFixture<RealtimePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RealtimePageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RealtimePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
