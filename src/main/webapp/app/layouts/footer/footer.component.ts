import { Component } from '@angular/core';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['footer.scss'],
})
export class FooterComponent {
  display = { ebok: false, privacy: false, contact: false };
  modal = {};

  showDialog(type: string): void {
    this.display[type] = true;
  }
}
