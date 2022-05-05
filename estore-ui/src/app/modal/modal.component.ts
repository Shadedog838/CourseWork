import { Component, Inject, OnInit } from '@angular/core';

import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css'],
})
export class ModalComponent implements OnInit {
  safeUrl: any;
  restricted: boolean = false;

  constructor(public dialogRef: MatDialogRef<ModalComponent>,
             @Inject(MAT_DIALOG_DATA) public data:any,
             private sanitizer : DomSanitizer
    ) {
      if (this.data.video != "restricted") {
        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.data.video);
      } else if (this.data.video == "restricted") {
        this.restricted = true;
      } else{
        this.restricted = false;
        this.safeUrl = "";
      }
    }


  ngOnInit() {}

  close() {
    this.dialogRef.close('Play Youtube Video Closed');
  }
}
