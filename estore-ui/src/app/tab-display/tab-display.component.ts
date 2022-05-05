import { Component, OnInit } from '@angular/core';
import { TabComponent } from '../tab/tab.component';

@Component({
  selector: 'tab-display',
  templateUrl: './tab-display.component.html',
  styleUrls: ['./tab-display.component.css'],
})
export class TabDisplayComponent implements OnInit {
  tabs: TabComponent[] = [];

  constructor() {}

  ngOnInit(): void {}

  addTab(tab: TabComponent) {
    if (this.tabs.length === 0) {
      tab.active = true;
    }
    this.tabs.push(tab);
  }

  selectTab(tab: TabComponent) {
    this.tabs.forEach((tab) => {
      tab.active = false;
    });
    tab.active = true;
  }
}
