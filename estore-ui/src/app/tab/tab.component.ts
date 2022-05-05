import { Component, Input, OnInit } from '@angular/core';
import { TabDisplayComponent } from '../tab-display/tab-display.component';

@Component({
  selector: 'tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.css'],
})
export class TabComponent implements OnInit {
  @Input() title: string;
  active = false;

  constructor(tabDisplay: TabDisplayComponent) {
    tabDisplay.addTab(this);
  }

  ngOnInit(): void {}
}
