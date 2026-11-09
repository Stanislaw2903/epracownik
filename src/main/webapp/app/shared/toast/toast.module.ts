import { ToastComponent } from './toast.component';
import { NgModule } from '@angular/core';
import { ToastModule } from 'primeng/toast';

@NgModule({
  imports: [ToastModule],
  exports: [ToastComponent],
  declarations: [ToastComponent],
  providers: [],
})
export class JhiToastModule {}
